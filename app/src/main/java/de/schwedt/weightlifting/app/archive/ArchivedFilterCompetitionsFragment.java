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

public class ArchivedFilterCompetitionsFragment extends ListViewFragment {

    private ArrayList<PastCompetition> filteredCompetitions;
    private ArchivedRelay archivedRelay;
    private Competitions archivedCompetitions;

    protected void getBuliElements() {
        try {
            Bundle bundle = this.getArguments();
            int seasonPosition = bundle.getInt("seasonItem");
            int relayPosition = bundle.getInt("relayItem");
            archivedRelay = ArchiveFragment.archivedSeasonEntries.get(seasonPosition).getArchivedRelays().get(relayPosition);
            archivedCompetitions = archivedRelay.getArchivedCompetitions();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            Bundle bundle = this.getArguments();
            String clubName = bundle.getString("club-name");
            filteredCompetitions = filter(Competitions.casteArray(archivedCompetitions.getItems()), clubName);
            CompetitionsListAdapter adapter = new CompetitionsListAdapter(filteredCompetitions, getActivity());
            listViewBuli.setAdapter(adapter);
            listViewBuli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment protocol = new ProtocolFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("protocol-url", filteredCompetitions.get(position).getProtocolUrl());
                    protocol.setArguments(bundle);
                    ((MainActivity) getActivity()).addFragment(protocol, getString(R.string.nav_buli), true);
                }
            });
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Showing competitions failed");
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

