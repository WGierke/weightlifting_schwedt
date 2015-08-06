package de.schwedt.weightlifting.app.gallery;

import android.content.Context;
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
import de.schwedt.weightlifting.app.helper.JsonParser;
import de.schwedt.weightlifting.app.helper.UiHelper;

public class Galleries extends UpdateableWrapper {

    public static final String fileName = "galleries.json";

    public static ArrayList<GalleryItem> itemsToMark = new ArrayList<GalleryItem>();

    public static ArrayList<GalleryItem> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<GalleryItem> convertedItems = new ArrayList<GalleryItem>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((GalleryItem) array.get(i));
        }
        return convertedItems;
    }

    public static String getNotificationMessage() {
        String content = "";
        for (GalleryItem item : itemsToMark) {
            content += item.getTitle() + "|";
        }
        return content;
    }

    public static void markNewItems(ArrayList<UpdateableItem> oldItems, ArrayList<UpdateableItem> newItems) {
        ArrayList<GalleryItem> oldGalleryItems = casteArray(oldItems);
        ArrayList<GalleryItem> newGalleryItems = casteArray(newItems);
        int navigationPosition = MainActivity.FRAGMENT_GALLERY;
        int subPosition = 0;
        for (int i = 0; i < newGalleryItems.size(); i++) {
            boolean isNew = true;
            for (int j = 0; j < oldGalleryItems.size(); j++) {
                if (newGalleryItems.get(i).getTitle().equals(oldGalleryItems.get(j).getTitle()) && Arrays.equals(newGalleryItems.get(i).getImageUrls(), oldGalleryItems.get(j).getImageUrls()) && newGalleryItems.get(i).getUrl().equals(oldGalleryItems.get(j).getUrl())) {
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                itemsToMark.add(newGalleryItems.get(i));
            }
        }
        UiHelper.refreshCounterNav(navigationPosition, subPosition, itemsToMark.size());
    }

    public void update() {
        super.update("https://raw.githubusercontent.com/WGierke/weightlifting_schwedt/updates/production/galleries.json", fileName, "Galleries");
    }

    protected void updateWrapper(String result) {
        Galleries newItems = new Galleries();
        newItems.parseFromString(result);
        if (items.size() > 0) {
            keepOldReferences(items, newItems.getItems());
            markNewItems(items, newItems.getItems());
        }
        items = newItems.getItems();
    }

    public void parseFromString(String jsonString) {
        Log.d(WeightliftingApp.TAG, "Parsing gallery JSON...");
        try {
            ArrayList<UpdateableItem> newItems = new ArrayList<UpdateableItem>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

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
                    //imageLoader.preloadImage(item.getImageUrls()[0]);
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

    private void keepOldReferences(ArrayList<UpdateableItem> oldItems, ArrayList<UpdateableItem> newItems) {
        ArrayList<GalleryItem> oldGalleryItems = casteArray(oldItems);
        ArrayList<GalleryItem> newGalleryItems = casteArray(newItems);
        for (int i = 0; i < newGalleryItems.size(); i++) {
            for (int j = 0; j < oldGalleryItems.size(); j++) {
                if ((newGalleryItems.get(i)).equals(oldGalleryItems.get(j))) {
                    newGalleryItems.set(i, oldGalleryItems.get(j));
                }
            }
        }
    }
}
