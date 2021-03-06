package de.schwedt.weightlifting.app.archive;

import android.os.Bundle;
import android.util.Log;

import de.schwedt.weightlifting.app.ArchiveFragment;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.buli.Table;
import de.schwedt.weightlifting.app.buli.TableFragment;
import de.schwedt.weightlifting.app.helper.Constants;
import de.schwedt.weightlifting.app.helper.DataHelper;

public class ArchivedTableFragment extends TableFragment {

    private Table archivedTable;
    private Bundle bundle;

    @Override
    protected void setEmptyListItem() {
        try {
            bundle = this.getArguments();
            int seasonPosition = bundle.getInt(Constants.SEASON_ITEM_POSITION);
            int relayPosition = bundle.getInt(Constants.RELAY_ITEM_POSITION);
            String archivedSeason = ArchiveFragment.archivedSeasonEntries.get(seasonPosition);
            String archivedRelay = ArchivedSeasonFragment.archivedRelayEntries.get(relayPosition);
            archivedTable = DataHelper.getTableFromSeasonRelay(archivedSeason, archivedRelay, getActivity());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            setTableListAdapterWithFilterCompetitionsFragment(Table.casteArray(archivedTable.getItems()), getActivity(), ArchivedFilterCompetitionsFragment.class);
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Showing archived table failed");
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
