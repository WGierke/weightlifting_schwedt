package de.schwedt.weightlifting.app.archive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import de.schwedt.weightlifting.app.ArchiveFragment;
import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.buli.ListViewFragment;
import de.schwedt.weightlifting.app.buli.Table;
import de.schwedt.weightlifting.app.buli.TableEntry;
import de.schwedt.weightlifting.app.buli.TableListAdapter;
import de.schwedt.weightlifting.app.helper.Constants;
import de.schwedt.weightlifting.app.helper.DataHelper;

public class ArchivedTableFragment extends ListViewFragment {

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
            TableListAdapter adapter = new TableListAdapter(Table.casteArray(archivedTable.getItems()), getActivity());
            listViewBuli.setAdapter(adapter);
            listViewBuli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment protocol = new ArchivedFilterCompetitionsFragment();
                    TableEntry entry = (TableEntry) archivedTable.getItem(position);
                    bundle.putString(Constants.CLUB_NAME, entry.getClub());
                    protocol.setArguments(bundle);
                    ((MainActivity) getActivity()).addFragment(protocol, entry.getClub(), true);
                }
            });
        } catch (Exception ex) {
            Log.e(WeightliftingApp.TAG, "Showing table failed");
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
