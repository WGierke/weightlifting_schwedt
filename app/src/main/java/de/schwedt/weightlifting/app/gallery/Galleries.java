package de.schwedt.weightlifting.app.gallery;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.schwedt.weightlifting.app.UpdateableItem;
import de.schwedt.weightlifting.app.UpdateableWrapper;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;

public class Galleries extends UpdateableWrapper {

    public static ArrayList<GalleryItem> itemsToMark = new ArrayList<GalleryItem>();

    public static ArrayList<GalleryItem> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<GalleryItem> convertedItems = new ArrayList<GalleryItem>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((GalleryItem) array.get(i));
        }
        return convertedItems;
    }

    public void parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing gallery JSON...");
        try {
            ArrayList<UpdateableItem> newItems = new ArrayList<UpdateableItem>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

            // parse news feed
            JSONArray galleries = jsonParser.getJsonArray("galleries");
            for (int i = 0; i < galleries.length(); i++) {
                try {
                    JSONObject gallery = galleries.getJSONObject(i);

                    GalleryItem item = new GalleryItem();
                    item.setTitle(gallery.getString("title"));
                    item.setUrl(gallery.getString("url"));

                    JSONArray gallery_images = gallery.getJSONArray(("images"));
                    List<String> image_urls = new ArrayList<String>();
                    for (int j = 0; j < gallery_images.length(); j++) {
                        image_urls.add(gallery_images.getString(j));
                    }
                    item.setImageUrls(image_urls.toArray(new String[image_urls.size()]));
                    imageLoader.preloadImage(item.getImageUrls()[0]);
                    newItems.add(item);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing gallery #" + i);
                    ex.printStackTrace();
                }
            }

            setItems(newItems);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "Galleries parsed, " + newItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing galleries");
            ex.printStackTrace();
        }
    }
}
