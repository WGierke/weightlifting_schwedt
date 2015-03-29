package de.schwedt.weightlifting.app.buli;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class BuliTableFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;

    private ImageView cover;
    private ListView listViewTable;

    private BuliTable buliTable;

    public BuliTableFragment() {
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

        getBuliTable();

        return fragment;
    }

    private void getBuliTable() {
        buliTable = app.getBuliTable();
        if (buliTable.getBuliTableEntries().size() == 0) {
            // No table items yet
            app.setLoading(true);
            Log.d(WeightliftingApp.TAG, "Waiting for buliTable...");

            // Check again in a few seconds
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getBuliTable();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, BuliTable.TIMER_RETRY);
        } else {
            // We have buliTable items to display
            app.setLoading(false);
            try {
                ListView listViewNews = (ListView) fragment.findViewById(R.id.listView_Buli);
                BuliTableListAdapter adapter = new BuliTableListAdapter(buliTable.getBuliTableEntries(), getActivity());
                listViewNews.setAdapter(adapter);

            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing buliTeam failed");
                ex.toString();
            }

        }
    }
}
