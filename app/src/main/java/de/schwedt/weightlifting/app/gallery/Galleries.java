package de.schwedt.weightlifting.app.gallery;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;

public class Galleries {

    // Refresh if older than 30 minutes
    public static final long TIMER_INVALIDATE = 1800000;

    // If news not yet ready, try again in 3 seconds
    public static final long TIMER_RETRY = 3000;

    public static ArrayList<GalleryItem> itemsToMark = new ArrayList<GalleryItem>();

    private long lastUpdate = 0;

    // holds all news items
    private ArrayList<GalleryItem> galleries;

    public Galleries() {
        galleries = new ArrayList<GalleryItem>();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<GalleryItem> getGalleries() {
        return galleries;
    }

    public void setGalleries(ArrayList<GalleryItem> galleries) {
        this.galleries = galleries;
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

    public void parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing gallery JSON...");
        try {
            galleries = new ArrayList<GalleryItem>();

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

                    this.galleries.add(item);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing gallery #" + i);
                    ex.printStackTrace();
                }
            }

            setGalleries(this.galleries);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "Galleries parsed, " + this.galleries.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing galleries");
            ex.printStackTrace();
        }
    }

}
