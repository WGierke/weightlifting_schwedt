package de.schwedt.weightlifting.app.buli;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class FilterCompetitionsFragment extends CompetitionsFragment {

    protected void getBuliCompetitions() {
        buliCompetitions = app.getCompetitions();
        if (buliCompetitions.getItems().size() == 0) {
            app.setLoading(true);
            Log.d(WeightliftingApp.TAG, "Waiting for Competitions...");

            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getBuliCompetitions();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, Competitions.TIMER_RETRY);
        } else {
            app.setLoading(false);
            try {
                Bundle bundle = this.getArguments();
                String name = bundle.getString("filter-name");
                CompetitionsListAdapter adapter = new CompetitionsListAdapter(filter(Competitions.casteArray(buliCompetitions.getItems()), name), getActivity());
                listViewCompetitions.setAdapter(adapter);
                listViewCompetitions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Fragment protocol = new ProtocolFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("item", position);
                        protocol.setArguments(bundle);
                        ((MainActivity) getActivity()).addFragment(protocol, getString(R.string.nav_buli), true);
                    }
                });
            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing competitions failed");
                ex.toString();
            }
        }
    }

    private ArrayList<PastCompetition> filter(ArrayList<PastCompetition> items, String name) {
        ArrayList<PastCompetition> result = new ArrayList<PastCompetition>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getHome().equals(name) || items.get(i).getGuest().equals(name)) {
                result.add(items.get(i));
            }
        }
        return result;
    }
}

