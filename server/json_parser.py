#!/usr/bin/python
# -*- coding: iso-8859-15 -*-
import urllib2
import re
import json
from bs4 import BeautifulSoup

iat_url = "https://www.iat.uni-leipzig.de/datenbanken/blgew1415/"

# Helper functions


def add_article_content(post, article):
    """Return content and first image of the post"""
    re_img_tag = re.compile(ur'(?<=<img)[^>]*')
    re_img_src = re.compile(ur'(?<=src="http)[^"]*(?=")')

    soup = BeautifulSoup(post)
    article["content"] = ''.join(soup.findAll(text=True)).replace("\n", "").replace(u"\u00a0", "").replace("[Zeige als Diashow]", "").replace("Thumbnails", "")
    img_tags = re.findall(re_img_tag, post)
    image = ''
    if len(img_tags) > 0:
        if len(re.findall(re_img_src, img_tags[0])) > 0:
            image = "http" + re.findall(re_img_src, img_tags[0])[0]
    article["image"] = image
    return article


def get_competitions_array(array):
    """Convert json in competitions array"""
    array = array.split("}, ")
    array[0] = array[0].split('"past_competitions": [')[1]
    array[-1] = array[-1].split("}")[0]
    return array


def add_gallery_images(gallery_entry):
    """Return image urls of the gallery"""
    total_links = []
    current_url = gallery_entry["url"]
    try:
        page = urllib2.urlopen(current_url).read()
    except Exception, e:
        print 'Error while downloading gallery site ', e
        return
    if(page.find('[Zeige als Diashow]') != -1):
        # Diashow, possibly multiple pages
        n = 1
        while(page.find("no images were found") == -1):
            page = page.replace('class="content"', '', 1).split('class="content"')[1].split('<!-- Pagination -->')[0]
            re_href = re.compile(ur'(?<=href=")[^"]*(?=")')
            image_links = re.findall(re_href, page)

            for i in range(len(image_links)):
                total_links.append(image_links[i])

            n += 1
            current_url = re.sub(ur'(?<=page\/)\d+', str(n), current_url)
            try:
                page = urllib2.urlopen(current_url).read()
            except Exception, e:
                print 'Error while downloading gallery site ', e
                return

        gallery_entry["images"] = total_links
        return gallery_entry
    else:
        # One page containing all pictures
        page = page.replace('class="content"', '', 1).split('class="content"')[1].split('<div id="comments">')[0]
        re_href = re.compile(ur"(?<=href=')[^']*(?=')")
        image_links = re.findall(re_href, page)

        for i in range(len(image_links)):
            total_links.append(image_links[i])

        gallery_entry["images"] = total_links
        return gallery_entry

# Main functions


def create_news_file():
    """Save posts in news.json"""
    global error_occured
    basic_url = 'http://gewichtheben.blauweiss65-schwedt.de/?page_id=171&paged='
    re_href = re.compile(ur'(?<=href=")[^"]*(?=")')
    re_date = re.compile(ur'(?<=class="date">).*(?=<\/span>)')

    news_dict = {}
    articles = []

    try:
        # Check if a new article appeared.
        page = urllib2.urlopen(basic_url + str(1)).read()
        first_post = page.split('id="pagenavi"')[0].split('<div class="post"')[1]
        first_url = re.findall(re_href, first_post)[0]
        with open('production/news.json') as news_json:
            old_json = json.load(news_json)

        # Cancel the process if the first article is already saved.
        if first_url == old_json[0]["articles"][0]["url"]:
            print "No new articles"
            return

        n = 1
        page = urllib2.urlopen(basic_url + str(n)).read()
        while page.find('Willkommen auf der 404 Fehlerseite') == -1:
            print "News page " + str(n)
            posts = page.split('id="pagenavi"')[0].split('<div class="post"')[1:]

            for i in range(len(posts)):
                post = posts[i]
                article = {}

                article["url"] = re.findall(re_href, post)[0]
                article["date"] = re.findall(re_date, post)[0]
                article["heading"] = post[post.find('"bookmark">')+len('"bookmark">'):post.find('</a>')].replace('&#8211;', '-')

                if post.find("Mehr&#8230;") != -1:
                    post = urllib2.urlopen(article["url"]).read()
                    post = post.replace('"content">', '', 2)

                post = post[post.find('"content">')+len('"content">'):post.find('class="under"')]
                article = add_article_content(post, article)
                articles.append(article)

            n += 1
            page = urllib2.urlopen(basic_url + str(n)).read()
    except Exception, e:
        print e
        print articles
        if e.code == 404 and articles[len(articles)-1]["heading"] == "Qualifikation DM C-Jugend":
            pass
        else:
            error_occured = True
            print 'Error while downloading news ', e
            return
    if not error_occured:
        news_dict["articles"] = articles
        json_news = json.dumps(news_dict)
        json_news = "[" + json_news.replace("\u00df", "ß").replace("\u00e4", "ä").replace("\u00fc", "ü") + "]"
        f = open("production/news.json", "w")
        f.write(json_news)
        f.close()


def create_events_file():
    """Save events in events.json"""
    global error_occured
    print "Parsing events ..."
    try:
        months = urllib2.urlopen("http://gewichtheben.blauweiss65-schwedt.de/?page_id=31").read().replace('<div class="fixed"></div>', '', 3).split('<div class="fixed"></div>')[0].split('<strong>')[1:]
    except Exception, e:
        print 'Error while downloading events ', e
        error_occured = True
        return

    events_dict = {}
    final_events = []

    for i in range(len(months)):
        month = months[i].split('<')[0]
        month_events = months[i].split('<p>')[1:]
        month_events = filter(None, month_events)
        for j in range(len(month_events)):
            event_entry = {}
            event = month_events[j].replace('&#8221;', '"').replace('&#8220;', '"').replace('&#8211;', '-')
            event_entry["date"] = event.split(".")[0] + ". " + month
            event_entry["location"] = event.split('(')[1].split(')')[0] if event.find(")") != -1 else ''
            title = re.sub('\d+\.', '', event, 1).replace('&nbsp;', '').replace('\n', '')
            title = re.sub('\s*(?=.)', '', title, 1).split("</p>")[0]
            event_entry["title"] = title.replace('(' + event_entry["location"] + ')', '')

            final_events.append(event_entry)

    events_dict["events"] = final_events
    json_events = json.dumps(events_dict)
    events_dict = "[" + json_events + "]"
    f = open("production/events.json", "w")
    f.write(events_dict)
    f.close()

def create_competitions_file():
    """Save past competitions in past_competitions.json"""
    global iat_url
    global error_occured
    print "Parsing competitions ..."
    try:
        competitions = urllib2.urlopen(iat_url + "start.php?pid=%27123%27&resultate=1&bl=1&staffel=Gruppe+B").read().split("</TABLE>")[0]
    except Exception, e:
        print 'Error while downloading competitions ', e
        error_occured = True
        return
    re_competition_entry = re.compile(ur'(?<=class=font4>).*(?=<\/TD>)')
    re_href = re.compile(ur'(?<=href=)[^>]*(?=>)')
    competition_entries = re.findall(re_competition_entry, competitions)

    competitions_dict = {}
    final_competitions = []

    for i in range(0, len(competition_entries), 7):
        entry = {}
        entry["location"] = competition_entries[i+1]
        entry["date"] = competition_entries[i+2]
        entry["home"] = competition_entries[i+3]
        entry["guest"] = competition_entries[i+4]
        entry["score"] = competition_entries[i+5]
        entry["url"] = iat_url + re.findall(re_href,
                                            competition_entries[i+6])[0]

        final_competitions.append(entry)

    # Handle swapping of competitions due to IAT database
    with open("production/past_competitions.json", "r") as f:
        old_competitions = f.read()
    old_competitions = get_competitions_array(old_competitions)

    competitions_dict["past_competitions"] = final_competitions
    json_competitions = json.dumps(competitions_dict, encoding='latin1')
    json_competitions = "[" + json_competitions + "]"

    new_competitions = get_competitions_array(json_competitions)
    if sorted(new_competitions) != sorted(old_competitions):
        f = open("production/past_competitions.json", "w")
        f.write(json_competitions)
        f.close()
        print "Competitions: Change detected"

def create_table_file():
    """Save table entries in table.json"""
    global iat_url
    global error_occured
    print "Parsing table ..."
    try:
        table = urllib2.urlopen(iat_url + "start.php?pid=%27123%27&tabelle=1&bl=1&staffel=Gruppe+B").read().split("</TABLE>")[0]
    except Exception, e:
        print 'Error while downloading table ', e
        error_occured = True
        return
    re_table_entry = re.compile(ur'(?<=class=font4>).*(?=<\/TD>)')
    table_entries = re.findall(re_table_entry, table)

    table_dict = {}
    final_entries = []

    for i in range(len(table_entries)/4):
        entry = {}
        entry["place"] = str(i+1)
        entry["club"] = table_entries[i*4]
        entry["score"] = table_entries[i*4+1]
        entry["max_score"] = table_entries[i*4+2]
        entry["cardinal_points"] = table_entries[i*4+3]
        final_entries.append(entry)

    table_dict["table"] = final_entries
    json_table = json.dumps(table_dict, encoding='latin1')
    json_table = "[" + json_table + "]"
    f = open("production/table.json", "w")
    f.write(json_table)
    f.close()


def create_galleries_file():
    """Save gallery images in galleries.json"""
    global error_occured
    try:
        print "Parsing galleries ..."
        page = urllib2.urlopen("http://www.gewichtheben-schwedt.de").read().split('class="page_item page-item-28 page_item_has_children">')[1].split("javascript:void(0);")[0]
        re_gallery_link = re.compile(ur'(?<=href=").*(?=<\/a>)')

        gallery_links = re.findall(re_gallery_link, page)[1:]

        gallery_dict = {}
        final_entries = []

        # Check if a new gallery appeared.
        first_gallery_link = "http://gewichtheben.blauweiss65-schwedt.de/index.php/nggallery/page/1?" + gallery_links[0].split('">')[0].split("?")[1]
        with open('production/galleries.json') as galleries_json:
            old_json = json.load(galleries_json)

        # Cancel the process if the first gallery is already saved.
        if first_gallery_link == old_json[0]["galleries"][0]["url"]:
            print "No new galleries"
            return

        for i in range(len(gallery_links)):
            gallery_entry = {}
            gallery_entry["url"] = gallery_links[i].split('">')[0]
            gallery_entry["title"] = gallery_links[i].split('">')[1].replace('&#8211;', '-')
            print gallery_entry["title"]
            first_page_url = "http://gewichtheben.blauweiss65-schwedt.de/index.php/nggallery/page/1?" + gallery_entry["url"].split("?")[1]
            gallery_entry["url"] = first_page_url

            gallery_entry = add_gallery_images(gallery_entry)

            final_entries.append(gallery_entry)

        gallery_dict["galleries"] = final_entries
        json_galleries = json.dumps(gallery_dict)
        json_galleries = "[" + json_galleries + "]"
        f = open("production/galleries.json", "w")
        f.write(json_galleries)
        f.close()
    except Exception, e:
        print 'Error while downloading galleries ', e
        error_occured = True
        return

if __name__ == '__main__':
    error_occured = False
    create_news_file()
    creating_functions = [create_events_file,
                          create_competitions_file,
                          create_table_file,
                          create_galleries_file]
    for func in creating_functions:
        if not error_occured:
            func()
