package de.schwedt.weightlifting.app.news;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;

public class News {

    // Refresh if older than 30 minutes
    public static final long TIMER_INVALIDATE = 1800000;
    // If news not yet ready, try again in 1 second
    public static final long TIMER_RETRY = 30 * 1000;
    public static ArrayList<NewsItem> itemsToMark = new ArrayList<NewsItem>();
    private long lastUpdate = 0;
    // holds all news items
    private ArrayList<NewsItem> newsItems;

    public News() {
        newsItems = new ArrayList<NewsItem>();
    }

    public static void addItemsToMark(News oldNews, News newNews) {
        ArrayList<NewsItem> oldItems = oldNews.getNewsItems();
        ArrayList<NewsItem> newItems = newNews.getNewsItems();
        for (int i = 0; i < newItems.size(); i++) {
            if (!oldItems.contains(newItems.get(i))) {
                News.itemsToMark.add(newItems.get(i));
            }
        }
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<NewsItem> getNewsItems() {
        return newsItems;
    }

    public void setNewsItems(ArrayList<NewsItem> newsItems) {
        this.newsItems = newsItems;
    }

    public NewsItem getNewsItem(int position) {
        return newsItems.get(position);
    }

    public boolean needsUpdate() {
        // Update only if last refresh is older than 30 minutes
        long now = new Date().getTime();

        if ((lastUpdate < now - TIMER_INVALIDATE)) {
            return true;
        } else {
            return false;
        }
    }

    public void
    parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing news JSON...");
        try {
            newsItems = new ArrayList<NewsItem>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

            // parse news feed
            JSONArray articles = jsonParser.getJsonArray("articles");
            for (int i = 0; i < articles.length(); i++) {
                try {
                    JSONObject article = articles.getJSONObject(i);

                    NewsItem item = new NewsItem();
                    item.setHeading(article.getString("heading"));
                    item.setContent(article.getString("content"));
                    item.setDate(article.getString("date"));
                    item.setURL(article.getString("url"));
                    item.setImageURL(article.getString("image"));

                    imageLoader.preloadImage(item.getImageURL());
                    newsItems.add(item);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing feed item #" + i);
                    //ex.printStackTrace();
                    Log.e(WeightliftingApp.TAG, ex.getMessage());
                }
            }

            setNewsItems(newsItems);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "News parsed, " + newsItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "News parsing failed");
            ex.printStackTrace();
        }
    }
}
