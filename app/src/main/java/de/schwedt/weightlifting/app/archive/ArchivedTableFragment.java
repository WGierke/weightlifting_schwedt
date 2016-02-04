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

public class ArchivedTableFragment extends ListViewFragment {

    private ArchivedRelay archivedRelay;
    private Table archivedTable;

    @Override
    protected void setEmptyListItem() {
        try {
            Bundle bundle = this.getArguments();
            int seasonPosition = bundle.getInt("seasonItem");
            int relayPosition = bundle.getInt("relayItem");
            archivedRelay = ArchiveFragment.archivedSeasonEntries.get(seasonPosition).getArchivedRelays().get(relayPosition);
            archivedTable = archivedRelay.getArchivedTable();
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
                    Bundle bundle = new Bundle();
                    TableEntry entry = (TableEntry) archivedTable.getItem(position);
                    bundle.putString("club-name", entry.getClub());
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
