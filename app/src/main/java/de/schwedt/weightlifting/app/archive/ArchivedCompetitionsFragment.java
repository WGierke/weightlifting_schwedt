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
import de.schwedt.weightlifting.app.helper.Constants;
import de.schwedt.weightlifting.app.helper.DataHelper;

public class ArchivedCompetitionsFragment extends ListViewFragment {

    public static Competitions archivedCompetitions;

    @Override
    protected void getBuliElements() {
        try {
            Bundle bundle = this.getArguments();
            int seasonPosition = bundle.getInt(Constants.SEASON_ITEM_POSITION);
            int relayPosition = bundle.getInt(Constants.RELAY_ITEM_POSITION);
            String archivedSeason = ArchiveFragment.archivedSeasonEntries.get(seasonPosition);
            String archivedRelay = ArchivedSeasonFragment.archivedRelayEntries.get(relayPosition);
            archivedCompetitions = DataHelper.getCompetitionFromSeasonRelay(archivedSeason, archivedRelay, getActivity());
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
                    PastCompetition competition = Competitions.casteArray(archivedCompetitions.getItems()).get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.PROTOCOL_URL, competition.getProtocolUrl());
                    bundle.putString(Constants.COMPETITION_PARTIES, competition.getHome() + " vs. " + competition.getGuest() + ": " + competition.getScore());
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
    protected void setEmptyListItem() {
    }

    @Override
    protected void setCoverImage() {

    }
}
