package de.schwedt.weightlifting.app.news;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.UpdateableItem;
import de.schwedt.weightlifting.app.UpdateableWrapper;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.JsonParser;
import de.schwedt.weightlifting.app.helper.UiHelper;

public class News extends UpdateableWrapper {

    public static final String fileName = "news.json";

    public static ArrayList<NewsItem> itemsToMark = new ArrayList<NewsItem>();

    public static ArrayList<NewsItem> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<NewsItem> convertedItems = new ArrayList<NewsItem>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((NewsItem) array.get(i));
        }
        return convertedItems;
    }

    public static String getNotificationMessage() {
        String content = "";
        for (NewsItem item : itemsToMark) {
            content += item.getHeading() + "|";
        }
        return content;
    }

    public static void markNewItems(ArrayList<UpdateableItem> oldItems, ArrayList<UpdateableItem> newItems) {
        ArrayList<NewsItem> oldNewsItems = casteArray(oldItems);
        ArrayList<NewsItem> newNewsItems = casteArray(newItems);
        int navigationPosition = MainActivity.FRAGMENT_NEWS;
        int subPosition = 0;
        for (int i = 0; i < newNewsItems.size(); i++) {
            boolean isNew = true;
            for (int j = 0; j < oldNewsItems.size(); j++) {
                if (newNewsItems.get(i).getContent().equals(oldNewsItems.get(j).getContent()) && newNewsItems.get(i).getDate().equals(oldNewsItems.get(j).getDate()) && newNewsItems.get(i).getHeading().equals(oldNewsItems.get(j).getHeading()) && newNewsItems.get(i).getImageURL().equals(oldNewsItems.get(j).getImageURL()) && newNewsItems.get(i).getURL().equals(oldNewsItems.get(j).getURL())) {
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                itemsToMark.add(newNewsItems.get(i));
            }
        }
        UiHelper.refreshCounterNav(navigationPosition, subPosition, itemsToMark.size());
    }

    public void update() {
        super.update("https://raw.githubusercontent.com/WGierke/weightlifting_schwedt/updates/production/news.json", fileName, "News");
    }

    protected void updateWrapper(String result) {
        News newItems = new News();
        newItems.parseFromString(result);
        if (items.size() > 0) {
            keepOldReferences(items, newItems.getItems());
            markNewItems(items, newItems.getItems());
        }
        items = newItems.getItems();
    }

    public void parseFromString(String jsonString) {
        Log.d(WeightliftingApp.TAG, "Parsing news JSON...");
        Log.d(WeightliftingApp.TAG, "json: " + jsonString);
        try {
            ArrayList<UpdateableItem> newItems = new ArrayList<UpdateableItem>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

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
                    newItems.add(item);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing feed item #" + i);
                    //ex.printStackTrace();
                    Log.e(WeightliftingApp.TAG, ex.getMessage());
                }
            }

            setItems(newItems);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "News parsed, " + newItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "News parsing failed");
            ex.printStackTrace();
        }
    }

    private void keepOldReferences(ArrayList<UpdateableItem> oldItems, ArrayList<UpdateableItem> newItems) {
        ArrayList<NewsItem> oldNewItems = casteArray(oldItems);
        ArrayList<NewsItem> newNewsItems = casteArray(newItems);
        for (int i = 0; i < newNewsItems.size(); i++) {
            for (int j = 0; j < oldNewItems.size(); j++) {
                if ((newNewsItems.get(i)).equals(oldNewItems.get(j))) {
                    newNewsItems.set(i, oldNewItems.get(j));
                }
            }
        }
    }
}
