package de.schwedt.weightlifting.app.buli;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;

public class BuliCompetitions {

    // Refresh if older than 30 minutes
    public static final long TIMER_INVALIDATE = 1800000;

    // If news not yet ready, try again in 30 seconds
    public static final long TIMER_RETRY = 30000;

    public static ArrayList<BuliPastCompetition> itemsToMark = new ArrayList<BuliPastCompetition>();

    private long lastUpdate = 0;

    // holds all past competitions
    private ArrayList<BuliPastCompetition> buliPastCompetitions;


    public BuliCompetitions() {
        buliPastCompetitions = new ArrayList<BuliPastCompetition>();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<BuliPastCompetition> getBuliPastCompetitions() {
        return buliPastCompetitions;
    }

    public void setBuliCompetitions(ArrayList<BuliPastCompetition> buliCompetitions) {
        this.buliPastCompetitions = buliCompetitions;
    }

    public BuliPastCompetition getCompetition(int position) {
        return buliPastCompetitions.get(position);
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
        Log.d(WeightliftingApp.TAG, "Parsing competitions JSON...");
        try {
            buliPastCompetitions = new ArrayList<BuliPastCompetition>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

            // parse past competitions
            JSONArray competitions = jsonParser.getJsonArray("past_competitions");
            Log.d(WeightliftingApp.TAG, competitions.length() + " competitions found");
            for (int i = 0; i < competitions.length(); i++) {
                try {
                    JSONObject jsonCompoetition = competitions.getJSONObject(i);
                    BuliPastCompetition competition = new BuliPastCompetition();
                    competition.setLocation(jsonCompoetition.getString("location"));
                    competition.setDate(jsonCompoetition.getString("date"));
                    competition.setHome(jsonCompoetition.getString("home"));
                    competition.setGuest(jsonCompoetition.getString("guest"));
                    competition.setScore(jsonCompoetition.getString("score"));
                    competition.setUrl(jsonCompoetition.getString("url"));

                    this.buliPastCompetitions.add(competition);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing competition #" + i);
                    ex.printStackTrace();
                }
            }
            setBuliCompetitions(buliPastCompetitions);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "BuliCompetition items parsed, " + buliPastCompetitions.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing competitions");
            ex.printStackTrace();
        }
    }
}
