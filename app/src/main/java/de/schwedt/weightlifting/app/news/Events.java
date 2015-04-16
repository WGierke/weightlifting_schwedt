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
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;
import de.schwedt.weightlifting.app.helper.UiHelper;

public class Events extends UpdateableWrapper {

    public static ArrayList<EventItem> itemsToMark = new ArrayList<EventItem>();

    public static ArrayList<EventItem> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<EventItem> convertedItems = new ArrayList<EventItem>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((EventItem) array.get(i));
        }
        return convertedItems;
    }

    public static void markNewItems(ArrayList<EventItem> oldItems, ArrayList<EventItem> newItems) {
        int navigationPosition = MainActivity.FRAGMENT_NEWS;
        int subPosition = 1;
        for (int i = 0; i < newItems.size(); i++) {
            boolean isNew = true;
            for (int j = 0; j < oldItems.size(); j++) {
                if (newItems.get(i).getLocation().equals(oldItems.get(j).getLocation()) && newItems.get(i).getDate().equals(oldItems.get(j).getDate()) && newItems.get(i).getTitle().equals(oldItems.get(j).getTitle()) && newItems.get(i).getPreview().equals(oldItems.get(j).getPreview())) {
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
        for (EventItem item : itemsToMark) {
            if (item.getLocation().length() > 0)
                content += item.getTitle() + " in " + item.getLocation() + "|";
            else
                content += item.getTitle() + "|";
        }
        return content;
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
