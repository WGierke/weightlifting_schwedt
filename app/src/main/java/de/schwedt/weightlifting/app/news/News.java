package de.schwedt.weightlifting.app.news;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.schwedt.weightlifting.app.UpdateableItem;
import de.schwedt.weightlifting.app.UpdateableWrapper;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;

public class News extends UpdateableWrapper {

    public static ArrayList<NewsItem> itemsToMark = new ArrayList<NewsItem>();

    public static ArrayList<NewsItem> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<NewsItem> convertedItems = new ArrayList<NewsItem>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((NewsItem) array.get(i));
        }
        return convertedItems;
    }

    public void
    parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing news JSON...");
        try {
            ArrayList<UpdateableItem> newItems = new ArrayList<UpdateableItem>();

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
}
