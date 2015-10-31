package de.schwedt.weightlifting.app.buli;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class TableFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;

    private ImageView cover;
    private ListView listViewTable;

    private Table buliTable;

    public TableFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Buli Table fragment");

        fragment = inflater.inflate(R.layout.buli_page, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        cover = (ImageView) fragment.findViewById(R.id.cover_buli);
        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover_competition));

        listViewTable = (ListView) fragment.findViewById(R.id.listView_Buli);

        getTable();

        return fragment;
    }

    private void getTable() {
        buliTable = app.getTable();
        if (buliTable.getItems().size() == 0) {
            // No table items yet
            Log.d(WeightliftingApp.TAG, "Waiting for buliTable...");

            // Check again in a few seconds
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getTable();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, Table.TIMER_RETRY);
        } else {
            // We have Table items to display
            try {
                ListView listViewTable = (ListView) fragment.findViewById(R.id.listView_Buli);
                TableListAdapter adapter = new TableListAdapter(Table.casteArray(buliTable.getItems()), getActivity());
                listViewTable.setAdapter(adapter);
                listViewTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Show the competitions the club already had
                        Fragment protocol = new FilterCompetitionsFragment();
                        Bundle bundle = new Bundle();
                        TableEntry entry = (TableEntry) app.getTable().getItem(position);
                        bundle.putString("club-name", entry.getClub());
                        protocol.setArguments(bundle);
                        ((MainActivity) getActivity()).addFragment(protocol, entry.getClub(), true);
                    }
                });

            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing Table failed");
                ex.toString();
            }

        }
    }
}
