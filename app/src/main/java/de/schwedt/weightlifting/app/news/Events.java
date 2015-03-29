package de.schwedt.weightlifting.app.news;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;

public class Events {

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

    public void
    parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing events JSON...");
        try {
            eventItems = new ArrayList<EventItem>();

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
                    eventItems.add(item);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing event item #" + i);
                    //ex.printStackTrace();
                    Log.e(WeightliftingApp.TAG, ex.getMessage());
                }
            }
            setEventItems(eventItems);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "Events parsed, " + eventItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Event parsing failed");
            ex.printStackTrace();
        }
    }

    public static void addItemsToMark(Events oldEvents, Events newEvents) {
        ArrayList<EventItem> oldItems = oldEvents.getEventItems();
        ArrayList<EventItem> newItems = newEvents.getEventItems();
        for (int i = 0; i < newItems.size(); i++) {
            if (!oldItems.contains(newItems.get(i))) {
                Events.itemsToMark.add(newItems.get(i));
            }
        }
        MainActivity.counter[MainActivity.FRAGMENT_NEWS] += Events.itemsToMark.size();
    }
}
