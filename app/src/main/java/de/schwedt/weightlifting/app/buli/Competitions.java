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

public class Competitions extends UpdateableWrapper {

    public static ArrayList<PastCompetition> itemsToMark = new ArrayList<PastCompetition>();

    public static ArrayList<PastCompetition> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<PastCompetition> convertedItems = new ArrayList<PastCompetition>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((PastCompetition) array.get(i));
        }
        return convertedItems;
    }

    public static void markNewItems(ArrayList<PastCompetition> oldItems, ArrayList<PastCompetition> newItems) {
        int navigationPosition = MainActivity.FRAGMENT_BULI;
        int subPosition = 1;
        for (int i = 0; i < newItems.size(); i++) {
            boolean isNew = true;
            for (int j = 0; j < oldItems.size(); j++) {
                if (newItems.get(i).getLocation().equals(oldItems.get(j).getLocation()) && newItems.get(i).getDate().equals(oldItems.get(j).getDate()) && newItems.get(i).getGuest().equals(oldItems.get(j).getGuest()) && newItems.get(i).getHome().equals(oldItems.get(j).getHome()) && newItems.get(i).getProtocolUrl().equals(oldItems.get(j).getProtocolUrl()) && newItems.get(i).getScore().equals(oldItems.get(j).getScore())) {
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
        for (PastCompetition item : itemsToMark) {
            content += item.getHome() + " vs. " + item.getGuest() + "|";
        }
        return content;
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
