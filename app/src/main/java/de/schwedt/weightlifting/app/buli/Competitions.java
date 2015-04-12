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

public class Competitions extends UpdateableWrapper {

    public static ArrayList<PastCompetition> itemsToMark = new ArrayList<PastCompetition>();

    public static ArrayList<PastCompetition> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<PastCompetition> convertedItems = new ArrayList<PastCompetition>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((PastCompetition) array.get(i));
        }
        return convertedItems;
    }

    public void parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing competitions JSON...");
        try {
            ArrayList<UpdateableItem> newItems = new ArrayList<UpdateableItem>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

            // parse past competitions
            JSONArray competitions = jsonParser.getJsonArray("past_competitions");
            Log.d(WeightliftingApp.TAG, competitions.length() + " competitions found");
            for (int i = 0; i < competitions.length(); i++) {
                try {
                    JSONObject jsonCompoetition = competitions.getJSONObject(i);
                    PastCompetition competition = new PastCompetition();
                    competition.setLocation(jsonCompoetition.getString("location"));
                    competition.setDate(jsonCompoetition.getString("date"));
                    competition.setHome(jsonCompoetition.getString("home"));
                    competition.setGuest(jsonCompoetition.getString("guest"));
                    competition.setScore(jsonCompoetition.getString("score"));
                    competition.setUrl(jsonCompoetition.getString("url"));

                    newItems.add(competition);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing competition #" + i);
                    ex.printStackTrace();
                }
            }
            setItems(newItems);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "BuliCompetition items parsed, " + newItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing competitions");
            ex.printStackTrace();
        }
    }
}
