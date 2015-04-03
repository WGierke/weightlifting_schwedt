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

public class Events extends UpdateableWrapper {

    public static ArrayList<EventItem> itemsToMark = new ArrayList<EventItem>();

    public static ArrayList<EventItem> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<EventItem> convertedItems = new ArrayList<EventItem>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((EventItem) array.get(i));
        }
        return convertedItems;
    }

    public void
    parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing events JSON...");
        try {
            ArrayList<UpdateableItem> newItems = new ArrayList<UpdateableItem>();

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
}
