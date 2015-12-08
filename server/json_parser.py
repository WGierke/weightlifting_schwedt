#!/usr/bin/python
# -*- coding: iso-8859-15 -*-
import urllib2
import re
import json
import codecs
from bs4 import BeautifulSoup


class BuliParser:

    def __init__(self, season, league, relay):
        self.iat_season_base = "https://www.iat.uni-leipzig.de/datenbanken/blgew{0}/".format(season)
        self.iat_competitions_url = "{0}start.php?pid=%27123%27&resultate=1&bl={1}&staffel={2}".format(self.iat_season_base, league, relay)
        self.iat_table_url = "{0}start.php?pid=%27123%27&tabelle=1&bl={1}&staffel={2}".format(self.iat_season_base, league, relay)
        self.error_occured = False

        # Helper functions

    def get_additional_entries(self, old_array, new_array):
        new_entries = []
        for new_entry in new_array:
            if new_entry not in old_array:
                new_entries.append(new_entry)
        return new_entries

    def save_push_message(self, headline, message_array, notification_id):
        msg = headline + "#" + "|".join(message_array) + u"#Dr\u00fccke in der App auf den \u21BB Knopf, um Updates herunterzuladen.#" + str(notification_id) + "\n"
        push_file = codecs.open('server/push_messages.txt', 'a', 'utf-8')
        push_file.write(msg)
        push_file.close()

    # Main functions


    def create_competitions_file(self):
        """Save past competitions in past_competitions.json"""
        print "Parsing competitions ..."
        try:
            competitions = urllib2.urlopen(self.iat_competitions_url).read().split("</TABLE>")[0]
        except Exception, e:
            print 'Error while downloading competitions ', e
            self.error_occured = True
            return

        re_competition_entry = re.compile(ur'(?<=class=font4>).*(?=[\r\n]?<\/TD>)')
        re_href = re.compile(ur'(?<=href=)[^>]*(?=>)')
        competition_entries = re.findall(re_competition_entry, competitions)
        competition_entries = [w.replace('\r', '') for w in competition_entries]

        competitions_dict = {}
        final_competitions = []

        for i in range(0, len(competition_entries), 7):
            entry = {}
            entry["location"] = competition_entries[i+1]
            entry["date"] = competition_entries[i+2]
            entry["home"] = competition_entries[i+3]
            entry["guest"] = competition_entries[i+4]
            entry["score"] = competition_entries[i+5]
            entry["url"] = self.iat_season_base + re.findall(re_href, competition_entries[i+6])[0]

            final_competitions.append(entry)

        if len(final_competitions) == 0:
            return

        # Handle swapping of competitions due to IAT database
        with open("production/past_competitions.json", "r") as f:
            old_competitions = f.read()

        competitions_dict["past_competitions"] = final_competitions
        json_competitions = json.dumps(competitions_dict, encoding='latin1', sort_keys=True, indent=4, separators=(',', ': '))
        competitions_dict_json = "[" + json_competitions + "]"

        if sorted(competitions_dict_json.decode('utf-8')) != sorted(old_competitions.decode('utf-8')):
            print "Competitions: Change detected"
            f = open("production/past_competitions.json", "w")
            f.write(competitions_dict_json.decode('utf-8'))
            f.close()

            push_messages = []
            old_competitions_dict = json.loads(old_competitions, encoding='utf-8')[0]["past_competitions"]
            new_competitions_dict = json.loads(competitions_dict_json, encoding='utf-8')[0]["past_competitions"]

            for competition in self.get_additional_entries(old_competitions_dict, new_competitions_dict):
                push_messages.append(competition["home"] + " vs. " + competition["guest"] + " - " + competition["score"])
            self.save_push_message("Neue Wettkampfergebnisse", push_messages, 2)

    def create_table_file(self):
        """Save table entries in table.json"""
        print "Parsing table ..."
        try:
            table = urllib2.urlopen(self.iat_table_url).read().split("</TABLE>")[0]
        except Exception, e:
            print 'Error while downloading table ', e
            self.error_occured = True
            return

        re_table_entry = re.compile(ur'(?<=class=font4>).*(?=[\r\n]?<\/TD>)')
        table_entries = re.findall(re_table_entry, table)
        table_entries = [w.replace('\r', '') for w in table_entries]

        table_dict = {}
        final_entries = []

        for i in range(0, len(table_entries), 4):
            entry = {}
            entry["place"] = str(i/4+1)
            entry["club"] = table_entries[i]
            entry["score"] = table_entries[i+1]
            entry["max_score"] = table_entries[i+2]
            entry["cardinal_points"] = table_entries[i+3]
            final_entries.append(entry)

        if len(final_entries) == 0:
            return

        with open("production/table.json", "r") as f:
            old_table = f.read()

        table_dict["table"] = final_entries
        json_table = json.dumps(table_dict, encoding='latin1', sort_keys=True, indent=4, separators=(',', ': '))
        table_dict_json = "[" + json_table + "]"

        if sorted(old_table.decode('utf-8')) != sorted(table_dict_json.decode('utf-8')):
            f = open("production/table.json", "w")
            f.write(table_dict_json.decode('utf-8'))
            f.close()

            push_messages = []
            old_table_dict = json.loads(old_table, encoding='utf-8')[0]["table"]
            new_table_dict = json.loads(table_dict_json, encoding='utf-8')[0]["table"]

            for table_entry in self.get_additional_entries(old_table_dict, new_table_dict):
                push_messages.append(table_entry["place"] + ". " + table_entry["club"])
            self.save_push_message("Neue Tabellenergebnisse", push_messages, 3)

    def create_buli_files(self):
        for func in [self.create_competitions_file, self.create_table_file]:
            if not self.error_occured:
                func()


class SchwedtBuliParser(BuliParser):

    def __init__(self, season, league, relay):
        BuliParser.__init__(self, season, league, relay)

    # Helper methods

    def add_article_content(self, post, article):
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

    def add_gallery_images(self, gallery_entry):
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

    # Main methods

    def create_news_file(self):
        """Save posts in news.json"""
        basic_url = 'http://gewichtheben.blauweiss65-schwedt.de/?page_id=6858&paged='
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
                    article = self.add_article_content(post, article)
                    articles.append(article)

                n += 1
                page = urllib2.urlopen(basic_url + str(n)).read()
        except Exception, e:
            if e.code == 404 and articles[len(articles)-1]["heading"] == "Qualifikation DM C-Jugend":
                pass
            else:
                self.error_occured = True
                print 'Error while downloading news ', e
                return

        with open("production/news.json", "r") as f:
            old_news = f.read()

        news_dict["articles"] = articles
        json_news = json.dumps(news_dict, sort_keys=True, indent=4, separators=(',', ': '))
        news_dict_json = "[" + json_news + "]"

        if sorted(old_news.decode('utf-8')) != sorted(news_dict_json.decode('utf-8')):
            f = open("production/news.json", "w")
            f.write(news_dict_json.decode('utf-8'))
            f.close()

            push_messages = []
            old_news_dict = json.loads(old_news, encoding='utf-8')[0]["articles"]
            new_news_dict = json.loads(news_dict_json, encoding='utf-8')[0]["articles"]

            for article in self.get_additional_entries(old_news_dict, new_news_dict):
                push_messages.append(article["heading"])
            self.save_push_message("Neue Artikel", push_messages, 0)

    def create_events_file(self):
        """Save events in events.json"""
        print "Parsing events ..."
        try:
            months_data = urllib2.urlopen("http://gewichtheben.blauweiss65-schwedt.de/?page_id=31").read().replace('<div class="content">', '').split('<div class="content">')[0].split('<strong>')[1:]
            months_data[-1] = months_data[-1].split('<div class="fixed">')[0]
        except Exception, e:
            print 'Error while downloading events ', e
            self.error_occured = True
            return

        events_dict = {}
        final_events = []

        for i in range(len(months_data)):
            month = months_data[i].split('<')[0]
            month_events = months_data[i].split('<p>')[1:]
            month_events = filter(None, month_events)
            month_events = filter(lambda x: x != "&nbsp;</p>\n", month_events)
            for j in range(len(month_events)):
                event_entry = {}
                event = month_events[j].replace('&#8221;', '"').replace('&#8220;', '"').replace('&#8211;', '-').replace('\xa0', '').replace('\xc2', '')
                event_entry["date"] = event.split(".")[0] + ". " + month
                event_entry["location"] = event.split('(')[1].split(')')[0] if event.find(")") != -1 else ''
                event_entry["title"] = re.sub('\d+\.\s+', '', event.split("</p>")[0], 1).split(' (')[0]
                final_events.append(event_entry)

        with open("production/events.json", "r") as f:
            old_events = f.read()

        events_dict["events"] = final_events
        json_events = json.dumps(events_dict, sort_keys=True, indent=4, separators=(',', ': '))
        events_dict_json = "[" + json_events + "]"

        if sorted(old_events.decode('utf-8')) != sorted(events_dict_json.decode('utf-8')):
            f = open("production/events.json", "w")
            f.write(events_dict_json.decode('utf-8'))
            f.close()

            push_messages = []
            old_events_dict = json.loads(old_events, encoding='utf-8')[0]["events"]
            new_events_dict = json.loads(events_dict_json, encoding='utf-8')[0]["events"]

            for event in self.get_additional_entries(old_events_dict, new_events_dict):
                if event["location"]:
                    push_messages.append(event["title"] + " in " + event["location"])
                else:
                    push_messages.append(event["title"] + " am " + event["date"])
            self.save_push_message("Neue Veranstaltungen", push_messages, 1)

    def create_galleries_file(self):
        """Save gallery images in galleries.json"""
        try:
            print "Parsing galleries ..."
            page = urllib2.urlopen("http://www.gewichtheben-schwedt.de").read().split('class="page_item page-item-28 page_item_has_children">')[1].split("javascript:void(0);")[0]
        except Exception, e:
            print 'Error while downloading galleries ', e
            self.error_occured = True
            return

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

            gallery_entry = self.add_gallery_images(gallery_entry)

            final_entries.append(gallery_entry)

        with open("production/galleries.json", "r") as f:
            old_galleries = f.read()

        gallery_dict["galleries"] = final_entries
        json_galleries = json.dumps(gallery_dict, sort_keys=True, indent=4, separators=(',', ': '))
        galleries_dict_json = "[" + json_galleries + "]"

        if sorted(old_galleries.decode('utf-8')) != sorted(galleries_dict_json.decode('utf-8')):
            f = open("production/galleries.json", "w")
            f.write(galleries_dict_json.decode('utf-8'))
            f.close()

            push_messages = []
            old_galleries_dict = json.loads(old_galleries, encoding='utf-8')[0]["galleries"]
            new_galleries_dict = json.loads(galleries_dict_json, encoding='utf-8')[0]["galleries"]

            for gallery_entry in self.get_additional_entries(old_galleries_dict, new_galleries_dict):
                push_messages.append(gallery_entry["title"])
            self.save_push_message("Neue Gallerie", push_messages, 4)

    def create_club_files(self):
        for func in [self.create_news_file, self.create_events_file, self.create_galleries_file]:
            if not self.error_occured:
                func()

if __name__ == '__main__':
    SchwedtBuliParser = SchwedtBuliParser("1516", "1", "Gruppe+B")
    SchwedtBuliParser.create_buli_files()
    SchwedtBuliParser.create_club_files()
