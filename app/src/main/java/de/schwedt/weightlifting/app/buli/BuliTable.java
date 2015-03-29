package de.schwedt.weightlifting.app.buli;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;

public class BuliTable {

    // Refresh if older than 30 minutes
    public static final long TIMER_INVALIDATE = 1800000;

    // If table not yet ready, try again in 30 seconds
    public static final long TIMER_RETRY = 30000;

    private long lastUpdate = 0;

    // holds all past competitions
    private ArrayList<BuliTableEntry> buliTableEntries;


    public BuliTable() {
        buliTableEntries = new ArrayList<BuliTableEntry>();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<BuliTableEntry> getBuliTableEntries() {
        return buliTableEntries;
    }

    public void setBuliTableEntries(ArrayList<BuliTableEntry> buliCompetitions) {
        this.buliTableEntries = buliCompetitions;
    }

    public BuliTableEntry getTeamMember(int position) {
        return buliTableEntries.get(position);
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
        Log.d(WeightliftingApp.TAG, "Parsing buli table JSON...");
        try {
            buliTableEntries = new ArrayList<BuliTableEntry>();

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

                    this.buliTableEntries.add(tableEntry);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing table entry #" + i);
                    ex.printStackTrace();
                }
            }
            setBuliTableEntries(buliTableEntries);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "BuliTable items parsed, " + buliTableEntries.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing buli table");
            ex.printStackTrace();
        }
    }
}
