package de.schwedt.weightlifting.app.archive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.ArchiveFragment;
import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.buli.Competitions;
import de.schwedt.weightlifting.app.buli.CompetitionsListAdapter;
import de.schwedt.weightlifting.app.buli.ListViewFragment;
import de.schwedt.weightlifting.app.buli.PastCompetition;
import de.schwedt.weightlifting.app.buli.ProtocolFragment;
import de.schwedt.weightlifting.app.helper.Constants;
import de.schwedt.weightlifting.app.helper.DataHelper;

public class ArchivedFilterCompetitionsFragment extends ListViewFragment {

    private ArrayList<PastCompetition> filteredCompetitions;
    private Competitions archivedCompetitions;
    private Bundle bundle;

    protected void getBuliElements() {
        try {
            bundle = this.getArguments();
            archivedCompetitions = ArchivedCompetitionsFragment.archivedCompetitions;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            String clubName = bundle.getString(Constants.CLUB_NAME);
            filteredCompetitions = filter(Competitions.casteArray(archivedCompetitions.getItems()), clubName);
            CompetitionsListAdapter adapter = new CompetitionsListAdapter(filteredCompetitions, getActivity());
            listViewBuli.setAdapter(adapter);
            listViewBuli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment protocol = new ProtocolFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.PROTOCOL_URL, filteredCompetitions.get(position).getProtocolUrl());
                    protocol.setArguments(bundle);
                    ((MainActivity) getActivity()).addFragment(protocol, getString(R.string.nav_buli), true);
                }
            });
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Showing archived filter competitions failed");
            ex.toString();
        }

    }

    @Override
    protected void setCoverImage() {

    }

    @Override
    protected void setEmptyListItem() {

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

