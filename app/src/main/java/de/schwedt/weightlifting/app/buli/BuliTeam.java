package de.schwedt.weightlifting.app.buli;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.JsonParser;

public class BuliTeam {

    // Refresh if older than 30 minutes
    public static final long TIMER_INVALIDATE = 1800000;

    // If team not yet ready, try again in 30 seconds
    public static final long TIMER_RETRY = 30000;

    private long lastUpdate = 0;

    // holds all team members
    private ArrayList<BuliTeamMember> buliTeamMembers;


    public BuliTeam() {
        buliTeamMembers = new ArrayList<BuliTeamMember>();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<BuliTeamMember> getBuliTeamMembers() {
        return buliTeamMembers;
    }

    public void setBuliTeamMembers(ArrayList<BuliTeamMember> buliTeamMembers) {
        this.buliTeamMembers = buliTeamMembers;
    }

    public BuliTeamMember getTeamMember(int position) {
        return buliTeamMembers.get(position);
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
        Log.d(WeightliftingApp.TAG, "Parsing buliTeam JSON...");
        try {
            buliTeamMembers = new ArrayList<BuliTeamMember>();

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
                    this.buliTeamMembers.add(member);
                } catch (Exception ex) {
                    Log.e(WeightliftingApp.TAG, "Error while parsing buli team member #" + i);
                    ex.printStackTrace();
                }
            }
            setBuliTeamMembers(buliTeamMembers);
            setLastUpdate((new Date()).getTime());
            Log.i(WeightliftingApp.TAG, "BuliTeam items parsed, " + buliTeamMembers.size() + " items found");
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Error while parsing buli team");
            ex.printStackTrace();
        }
    }
}
