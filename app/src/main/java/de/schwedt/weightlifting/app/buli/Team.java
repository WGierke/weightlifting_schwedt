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

public class Team extends UpdateableWrapper {

    public static ArrayList<TeamMember> itemsToMark = new ArrayList<TeamMember>();

    public static ArrayList<TeamMember> casteArray(ArrayList<UpdateableItem> array) {
        ArrayList<TeamMember> convertedItems = new ArrayList<TeamMember>();
        for (int i = 0; i < array.size(); i++) {
            convertedItems.add((TeamMember) array.get(i));
        }
        return convertedItems;
    }

    public static void markNewItems(ArrayList<TeamMember> oldItems, ArrayList<TeamMember> newItems) {
        int navigationPosition = MainActivity.FRAGMENT_BULI;
        int subPosition = 0;
        for (int i = 0; i < newItems.size(); i++) {
            boolean isNew = true;
            for (int j = 0; j < oldItems.size(); j++) {
                if (newItems.get(i).getName().equals(oldItems.get(j).getName()) && newItems.get(i).getSnatching().equals(oldItems.get(j).getSnatching()) && newItems.get(i).getJerking().equals(oldItems.get(j).getJerking()) && newItems.get(i).getMaxScore().equals(oldItems.get(j).getMaxScore()) && newItems.get(i).getYear().equals(oldItems.get(j).getYear()) && newItems.get(i).getImageURL().equals(oldItems.get(j).getImageURL())) {
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
        for (TeamMember item : itemsToMark) {
            content += item.getName() + ": " + item.getSnatching() + "/" + item.getJerking() + " (" + item.getMaxScore() + " RP)|";
        }
        return content;
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
                    TeamMember member = new TeamMember();
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
            Log.i(WeightliftingApp.TAG, "Team items parsed, " + newItems.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing buli team");
            ex.printStackTrace();
        }
    }
}
