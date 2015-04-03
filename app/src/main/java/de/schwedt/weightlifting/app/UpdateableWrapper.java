package de.schwedt.weightlifting.app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;
import de.schwedt.weightlifting.app.news.EventItem;

public abstract class UpdateableWrapper {


    // Refresh if older than 30 minutes
    public static final long TIMER_INVALIDATE = 1800000;

    // If not yet ready, try again in 30 second
    public static final long TIMER_RETRY = 30 * 1000;
    public static ArrayList<UpdateableItem> itemsToMark = new ArrayList<UpdateableItem>();

    private long lastUpdate = 0;
    // holds all news items
    protected ArrayList<UpdateableItem> items;

    public UpdateableWrapper() {
        items = new ArrayList<UpdateableItem>();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<UpdateableItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<UpdateableItem> items) {
        this.items = items;
    }

    public UpdateableItem getItem(int position) {
        return items.get(position);
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
        Log.d(WeightliftingApp.TAG, "Parsing updateable items...");
    }
}
