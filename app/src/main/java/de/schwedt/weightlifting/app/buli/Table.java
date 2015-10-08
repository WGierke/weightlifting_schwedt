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
import de.schwedt.weightlifting.app.helper.JsonParser;
import de.schwedt.weightlifting.app.helper.UiHelper;

public class Table extends UpdateableWrapper {

    public static final String fileName = "table.json";

    public static ArrayList<TableEntry> itemsToMark = new ArrayList<TableEntry>();

    private final String UPDATE_URL = "https://raw.githubusercontent.com/WGierke/weightlifting_schwedt/updates/production/table.json";

    public static ArrayList<TableEntry> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<TableEntry> convertedItems = new ArrayList<TableEntry>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((TableEntry) array.get(i));
        }
        return convertedItems;
    }

    public static String getNotificationMessage() {
        String content = "";
        for (TableEntry item : itemsToMark) {
            content += item.getPlace() + ". " + item.getClub() + "|";
        }
        return content;
    }

    public static void markNewItems(ArrayList<UpdateableItem> oldItems, ArrayList<UpdateableItem> newItems) {
        ArrayList<TableEntry> oldTableItems = casteArray(oldItems);
        ArrayList<TableEntry> newTableItems = casteArray(newItems);
        int navigationPosition = MainActivity.FRAGMENT_BULI;
        int subPosition = 2;
        for (int i = 0; i < newTableItems.size(); i++) {
            boolean isNew = true;
            for (int j = 0; j < oldTableItems.size(); j++) {
                if (newTableItems.get(i).getClub().equals(oldTableItems.get(j).getClub()) && newTableItems.get(i).getScore().equals(oldTableItems.get(j).getScore()) && newTableItems.get(i).getCardinalPoints().equals(oldTableItems.get(j).getCardinalPoints()) && newTableItems.get(i).getMaxScore().equals(oldTableItems.get(j).getMaxScore()) && newTableItems.get(i).getPlace().equals(oldTableItems.get(j).getPlace())) {
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                itemsToMark.add(newTableItems.get(i));
            }
        }
        UiHelper.refreshCounterNav(navigationPosition, subPosition, itemsToMark.size());
    }

    public void update() {
        super.update(UPDATE_URL, fileName, "Table");
    }

    protected void updateWrapper(String result) {
        Table newItems = new Table();
        newItems.parseFromString(result);
        if (items.size() > 0) {
            keepOldReferences(items, newItems.getItems());
            markNewItems(items, newItems.getItems());
        }
        items = newItems.getItems();
    }

    public void parseFromString(String jsonString) {
        Log.d(WeightliftingApp.TAG, "Parsing buli table JSON...");
        try {
            ArrayList<UpdateableItem> newBuliTableItems = new ArrayList<UpdateableItem>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

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

    private void keepOldReferences(ArrayList<UpdateableItem> oldItems, ArrayList<UpdateableItem> newItems) {
        ArrayList<TableEntry> oldTableItems = casteArray(oldItems);
        ArrayList<TableEntry> newTableItems = casteArray(newItems);
        for (int i = 0; i < newTableItems.size(); i++) {
            for (int j = 0; j < oldTableItems.size(); j++) {
                if ((newTableItems.get(i)).equals(oldTableItems.get(j))) {
                    newTableItems.set(i, oldTableItems.get(j));
                }
            }
        }
    }
}
