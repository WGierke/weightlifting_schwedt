package de.schwedt.weightlifting.app.buli;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.Constants;

public class FilterCompetitionsFragment extends CompetitionsFragment {

    protected void getBuliElements() {
        competitions = app.getCompetitions(WeightliftingApp.UPDATE_IF_NECESSARY);
        if (competitions.getItems().size() == 0) {
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getBuliElements();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, Competitions.TIMER_RETRY);
        } else {
            try {
                Bundle bundle = this.getArguments();
                String clubName = bundle.getString(Constants.CLUB_NAME);
                ArrayList<PastCompetition> filteredCompetitions = filter(Competitions.casteArray(competitions.getItems()), clubName);
                setCompetitionsListAdaptherWithProtocolFragment(filteredCompetitions, getActivity());
            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing competitions failed");
                ex.toString();
            }
        }
    }

    private ArrayList<PastCompetition> filter(ArrayList<PastCompetition> items, String name) {
        ArrayList<PastCompetition> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getHome().equals(name) || items.get(i).getGuest().equals(name)) {
                result.add(items.get(i));
            }
        }
        return result;
    }
}

