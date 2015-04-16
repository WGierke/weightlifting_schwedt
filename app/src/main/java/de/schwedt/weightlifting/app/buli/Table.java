package de.schwedt.weightlifting.app.buli;

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

public class Table extends UpdateableWrapper {

    public static ArrayList<TableEntry> itemsToMark = new ArrayList<TableEntry>();

    public static ArrayList<TableEntry> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<TableEntry> convertedItems = new ArrayList<TableEntry>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((TableEntry) array.get(i));
        }
        return convertedItems;
    }

    public static void markNewItems(ArrayList<TableEntry> oldItems, ArrayList<TableEntry> newItems) {
        int navigationPosition = MainActivity.FRAGMENT_BULI;
        int subPosition = 2;
        for (int i = 0; i < newItems.size(); i++) {
            boolean isNew = true;
            for (int j = 0; j < oldItems.size(); j++) {
                if (newItems.get(i).getClub().equals(oldItems.get(j).getClub()) && newItems.get(i).getScore().equals(oldItems.get(j).getScore()) && newItems.get(i).getCardinalPoints().equals(oldItems.get(j).getCardinalPoints()) && newItems.get(i).getMaxScore().equals(oldItems.get(j).getMaxScore()) && newItems.get(i).getPlace().equals(oldItems.get(j).getPlace())) {
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
        for (TableEntry item : itemsToMark) {
            content += item.getPlace() + ". " + item.getClub() + "|";
        }
        return content;
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
                    TableEntry tableEntry = new TableEntry();
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
            Log.i(WeightliftingApp.TAG, "Table items parsed, " + newBuliTableItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing buli table");
            ex.printStackTrace();
        }
    }
}
