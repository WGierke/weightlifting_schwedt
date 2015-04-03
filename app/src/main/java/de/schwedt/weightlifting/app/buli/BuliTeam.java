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

public class BuliTeam extends UpdateableWrapper {

    public static ArrayList<BuliTeamMember> itemsToMark = new ArrayList<BuliTeamMember>();

    public static ArrayList<BuliTeamMember> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<BuliTeamMember> convertedItems = new ArrayList<BuliTeamMember>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((BuliTeamMember) array.get(i));
        }
        return convertedItems;
    }

    public void parseFromString(String jsonString, ImageLoader imageLoader) {
        Log.d(WeightliftingApp.TAG, "Parsing buliTeam JSON...");
        try {
            ArrayList<UpdateableItem> newItems = new ArrayList<UpdateableItem>();

            JsonParser jsonParser = new JsonParser();
            jsonParser.getJsonFromString(jsonString);

            // parse team
            JSONArray team = jsonParser.getJsonArray("team");
            Log.d(WeightliftingApp.TAG, team.length() + " team members found");
            for (int i = 0; i < team.length(); i++) {
                try {
                    JSONObject jsonMember = team.getJSONObject(i);
                    BuliTeamMember member = new BuliTeamMember();
                    member.setName(jsonMember.getString("name"));
                    member.setYear(jsonMember.getString("year"));
                    member.setSnatching(jsonMember.getString("snatching"));
                    member.setJerking(jsonMember.getString("jerking"));
                    member.setMaxScore(jsonMember.getString("max_score"));
                    member.setImageURL(jsonMember.getString("image"));

                    imageLoader.preloadImage(member.getImageURL());
                    newItems.add(member);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing buli team member #" + i);
                    ex.printStackTrace();
                }
            }
            setItems(newItems);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "BuliTeam items parsed, " + newItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing buli team");
            ex.printStackTrace();
        }
    }
}
