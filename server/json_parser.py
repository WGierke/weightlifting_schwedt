#!/usr/bin/python
# -*- coding: iso-8859-15 -*-
import urllib2
import hashlib
import re
import json
from bs4 import BeautifulSoup

iat_url = "https://www.iat.uni-leipzig.de/datenbanken/blgew1415/"

def add_article_content(post, article):
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

def create_news_file():
    global error_occured
    basic_url = 'http://gewichtheben.blauweiss65-schwedt.de/?page_id=171&paged='
    re_href = re.compile(ur'(?<=href=")[^"]*(?=")')
    re_date = re.compile(ur'(?<=class="date">).*(?=<\/span>)')

    news_dict = {}
    articles = []

    try:
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
                article["heading"] = post[post.find('"bookmark">')+len('"bookmark">'):post.find('</a>')]

                if post.find("Mehr&#8230;") != -1:
                    post = urllib2.urlopen(article["url"]).read()
                    post = post.replace('"content">', '', 2)

                post = post[post.find('"content">')+len('"content">'):post.find('class="under"')]
                article = add_article_content(post, article)
                articles.append(article)

            n += 1
            page = urllib2.urlopen(basic_url + str(n)).read()
    except urllib2.URLError, e:
        if e.code == 404 and articles[len(articles)-1]["heading"] == "Qualifikation DM C-Jugend":
            pass
        else:
            error_occured = True
            print 'Error while downloading news ', e
    if not error_occured:
        news_dict["articles"] = articles
        json_news = json.dumps(news_dict)
        json_news = "[" + json_news.replace("\u00df","ß").replace("\u00e4", "ä").replace("\u00fc", "ü") + "]"
        f = open("production/news.json", "w")
        f.write(json_news)
        f.close()


def create_events_file():
    global error_occured
    print "Parsing events ..."
    try:
        months = urllib2.urlopen("http://gewichtheben.blauweiss65-schwedt.de/?page_id=31").read().replace('<div class="fixed"></div>', '', 3).split('<div class="fixed"></div>')[0].split('<strong>')[1:]
    except rllib2.URLError, e:
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

def get_competitions_array(array):
    array = array.split("}, ")
    array[0] = array[0].split('"past_competitions": [')[1]
    array[-1] = array[-1].split("}")[0]
    return array


def create_competitions_file():
    global iat_url
    global error_occured
    print "Parsing competitions ..."
    try:
        competitions = urllib2.urlopen(iat_url + "start.php?pid=%27123%27&resultate=1&bl=1&staffel=Gruppe+B").read().split("</TABLE>")[0]
    except rllib2.URLError, e:
        print 'Error while downloading competitions ', e
        error_occured = True
        return
    re_competition_entry = re.compile(ur'(?<=class=font4>).*(?=<\/TD>)')
    re_href = re.compile(ur'(?<=href=)[^>]*(?=>)')
    competition_entries = re.findall(re_competition_entry, competitions)

    competitions_dict = {}
    final_competitions = []

    for i in range(len(competition_entries)/7):
        entry = {}
        entry["location"] = competition_entries[i*7+1]
        entry["date"] = competition_entries[i*7+2]
        entry["home"] = competition_entries[i*7+3]
        entry["guest"] = competition_entries[i*7+4]
        entry["score"] = competition_entries[i*7+5]
        entry["url"] = iat_url + re.findall(re_href, competition_entries[i*7+6])[0]

        final_competitions.append(entry)

    #handle swapping of competitions due to IAT database
    f = open("production/past_competitions.json", "r")
    old_competitions = f.read()
    f.close()
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
    global iat_url
    global error_occured
    print "Parsing table ..."
    try:
        table = urllib2.urlopen(iat_url + "start.php?pid=%27123%27&tabelle=1&bl=1&staffel=Gruppe+B").read().split("</TABLE>")[0]
    except rllib2.URLError, e:
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

def add_gallery_images(gallery_entry):
    total_links = []
    current_url = gallery_entry["url"]
    try:
        page = urllib2.urlopen(current_url).read()
    except rllib2.URLError, e:
        print 'Error while downloading gallery site ', e
        return
    if(page.find('[Zeige als Diashow]') != -1):                                                                    #diashow, possibly multiple pages
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
            except rllib2.URLError, e:
                print 'Error while downloading gallery site ', e
                return

        gallery_entry["images"] = total_links
        return gallery_entry
    else:                                                                                                           #one page containing all pictures
        page = page.replace('class="content"', '', 1).split('class="content"')[1].split('<div id="comments">')[0]
        re_href = re.compile(ur"(?<=href=')[^']*(?=')")
        image_links = re.findall(re_href, page)

        for i in range(len(image_links)):
            total_links.append(image_links[i])

        gallery_entry["images"] = total_links
        return gallery_entry



def create_galleries_file():
    global error_occured
    try:
        print "Parsing galleries ..."
        page = urllib2.urlopen("http://www.gewichtheben-schwedt.de").read().split('class="page_item page-item-28 page_item_has_children">')[1].split("javascript:void(0);")[0]
        re_gallery_link = re.compile(ur'(?<=href=").*(?=<\/a>)')

        gallery_links = re.findall(re_gallery_link, page)[1:]

        gallery_dict = {}
        final_entries = []

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
    except rllib2.URLError, e:
        print 'Error while downloading galleries ', e
        error_occured = True
        return

error_occured = False
create_news_file()
if not error_occured:
    create_events_file()
if not error_occured:
    create_competitions_file()
if not error_occured:
    create_galleries_file()
if not error_occured:
    create_table_file()
