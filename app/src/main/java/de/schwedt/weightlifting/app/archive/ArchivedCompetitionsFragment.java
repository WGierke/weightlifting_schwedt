package de.schwedt.weightlifting.app.archive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import de.schwedt.weightlifting.app.ArchiveFragment;
import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.buli.Competitions;
import de.schwedt.weightlifting.app.buli.CompetitionsListAdapter;
import de.schwedt.weightlifting.app.buli.ListViewFragment;
import de.schwedt.weightlifting.app.buli.PastCompetition;
import de.schwedt.weightlifting.app.buli.ProtocolFragment;

public class ArchivedCompetitionsFragment extends ListViewFragment {

    private ArchivedRelay archivedRelay;
    private Competitions archivedCompetitions;

    @Override
    protected void setEmptyListItem() {
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
            CompetitionsListAdapter adapter = new CompetitionsListAdapter(Competitions.casteArray(archivedCompetitions.getItems()), getActivity());
            listViewBuli.setAdapter(adapter);
            listViewBuli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Show the protocol which belongs to the competition
                    Fragment protocol = new ProtocolFragment();
                    Bundle bundle = new Bundle();
                    PastCompetition competition = Competitions.casteArray(archivedCompetitions.getItems()).get(position);
                    bundle.putString("protocol-url", competition.getProtocolUrl());
                    bundle.putString("competition-parties", competition.getHome() + " vs. " + competition.getGuest() + ": " + competition.getScore());
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
    protected void getBuliElements() {

    }

    @Override
    protected void setCoverImage() {

    }
}
