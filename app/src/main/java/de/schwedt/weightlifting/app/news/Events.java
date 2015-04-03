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

public class Events extends UpdateableWrapper{
/*
    // Refresh if older than 30 minutes
    public static final long TIMER_INVALIDATE = 1800000;

    // If news not yet ready, try again in 1 second
    public static final long TIMER_RETRY = 30 * 1000;
    public static ArrayList<EventItem> itemsToMark = new ArrayList<EventItem>();

    private long lastUpdate = 0;
    // holds all news items
    private ArrayList<EventItem> eventItems;

    public Events() {
        eventItems = new ArrayList<EventItem>();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<EventItem> getEventItems() {
        return eventItems;
    }

    public void setEventItems(ArrayList<EventItem> eventItems) {
        this.eventItems = eventItems;
    }

    public EventItem getNewsItem(int position) {
        return eventItems.get(position);
    }

    public EventItem getEventItem(int position) {
        return eventItems.get(position);
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
*/
    public void
    parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing events JSON...");
        try {
            ArrayList<UpdateableItem>  newItems = new ArrayList<UpdateableItem>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

            // parse events
            JSONArray events = jsonParser.getJsonArray("events");
            for (int i = 0; i < events.length(); i++) {
                try {
                    JSONObject event = events.getJSONObject(i);

                    EventItem item = new EventItem();
                    item.setTitle(event.getString("title"));
                    item.setDate(event.getString("date"));
                    item.setLocation(event.getString("location"));
                    newItems.add(item);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing event item #" + i);
                    //ex.printStackTrace();
                    Log.e(WeightliftingApp.TAG, ex.getMessage());
                }
            }
            setItems(newItems);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "Events parsed, " + newItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Event parsing failed");
            ex.printStackTrace();
        }
    }

    public static ArrayList<EventItem> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<EventItem> convertedItems = new ArrayList<EventItem>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((EventItem) array.get(i));
        }
        return convertedItems;
    }
}
