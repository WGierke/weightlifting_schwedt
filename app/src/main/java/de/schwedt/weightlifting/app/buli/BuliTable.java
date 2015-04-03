package de.schwedt.weightlifting.app.buli;

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

public class BuliTable extends UpdateableWrapper {

    public static ArrayList<BuliTableEntry> itemsToMark = new ArrayList<BuliTableEntry>();

    public static ArrayList<BuliTableEntry> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<BuliTableEntry> convertedItems = new ArrayList<BuliTableEntry>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((BuliTableEntry) array.get(i));
        }
        return convertedItems;
    }

    public void parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing buli table JSON...");
        try {
            ArrayList<UpdateableItem> newBuliTableItems = new ArrayList<UpdateableItem>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

            // parse past competitions
            JSONArray table = jsonParser.getJsonArray("table");
            Log.d(WeightliftingApp.TAG, table.length() + " table entries found");
            for (int i = 0; i < table.length(); i++) {
                try {
                    JSONObject jsonTableEntry = table.getJSONObject(i);
                    BuliTableEntry tableEntry = new BuliTableEntry();
                    tableEntry.setPlace(jsonTableEntry.getString("place"));
                    tableEntry.setClub(jsonTableEntry.getString("club"));
                    tableEntry.setScore(jsonTableEntry.getString("score"));
                    tableEntry.setMaxScore(jsonTableEntry.getString("max_score"));
                    tableEntry.setCardinalPoints(jsonTableEntry.getString("cardinal_points"));

                    newBuliTableItems.add(tableEntry);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing table entry #" + i);
                    ex.printStackTrace();
                }
            }
            setItems(newBuliTableItems);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "BuliTable items parsed, " + newBuliTableItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing buli table");
            ex.printStackTrace();
        }
    }
}
