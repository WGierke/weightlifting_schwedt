package de.schwedt.weightlifting.app.archive;

import android.os.Bundle;
import android.util.Log;

import de.schwedt.weightlifting.app.ArchiveFragment;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.buli.Competitions;
import de.schwedt.weightlifting.app.buli.CompetitionsFragment;
import de.schwedt.weightlifting.app.helper.Constants;
import de.schwedt.weightlifting.app.helper.DataHelper;

public class ArchivedCompetitionsFragment extends CompetitionsFragment {

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
            setCompetitionsListAdaptherWithProtocolFragment(Competitions.casteArray(archivedCompetitions.getItems()), getActivity());

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
