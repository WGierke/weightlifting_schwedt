package de.schwedt.weightlifting.app.gallery;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.UpdateableItem;
import de.schwedt.weightlifting.app.UpdateableWrapper;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;
import de.schwedt.weightlifting.app.helper.UiHelper;

public class Galleries extends UpdateableWrapper {

    public static ArrayList<GalleryItem> itemsToMark = new ArrayList<GalleryItem>();

    public static ArrayList<GalleryItem> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<GalleryItem> convertedItems = new ArrayList<GalleryItem>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((GalleryItem) array.get(i));
        }
        return convertedItems;
    }

    public static void markNewItems(ArrayList<GalleryItem> oldItems, ArrayList<GalleryItem> newItems) {
        int navigationPosition = MainActivity.FRAGMENT_GALLERY;
        int subPosition = 0;
        for (int i = 0; i < newItems.size(); i++) {
            boolean isNew = true;
            for (int j = 0; j < oldItems.size(); j++) {
                if (newItems.get(i).getTitle().equals(oldItems.get(j).getTitle()) && Arrays.equals(newItems.get(i).getImageUrls(), oldItems.get(j).getImageUrls()) && newItems.get(i).getUrl().equals(oldItems.get(j).getUrl())) {
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                itemsToMark.add(newItems.get(i));
            }
        }
        UiHelper.refreshCounterNav(navigationPosition, subPosition, itemsToMark.size());
    }

    public static String getNotificationMessage() {
        String content = "";
        for (GalleryItem item : itemsToMark) {
            content += item.getTitle() + "|";
        }
        return content;
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
