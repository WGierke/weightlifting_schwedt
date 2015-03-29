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

public class BuliCompetitionsFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;

    private ImageView cover;
    private ListView listViewCompetitions;

    private BuliCompetitions buliCompetitions;

    public BuliCompetitionsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Buli Competitions fragment");

        fragment = inflater.inflate(R.layout.buli_page, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        cover = (ImageView) fragment.findViewById(R.id.cover_buli);
        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover_competition));

        listViewCompetitions = (ListView) fragment.findViewById(R.id.listView_Buli);

        getBuliCompetitions();

        return fragment;
    }

    private void getBuliCompetitions() {
        buliCompetitions = app.getBuliCompetitions();
        if (buliCompetitions.getBuliPastCompetitions().size() == 0) {
            // No news items yet
            app.setLoading(true);
            Log.d(WeightliftingApp.TAG, "Waiting for buliCompetitions...");

            // Check again in a few seconds
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getBuliCompetitions();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, BuliCompetitions.TIMER_RETRY);
        } else {
            // We have competitions to display
            app.setLoading(false);
            try {
                BuliCompetitionsListAdapter adapter = new BuliCompetitionsListAdapter(buliCompetitions.getBuliPastCompetitions(), getActivity());
                listViewCompetitions.setAdapter(adapter);
                listViewCompetitions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Show an article fragment and put the selected index as argument
                        Fragment protocol = new BuliProtocolFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("item", position);
                        protocol.setArguments(bundle);
                        ((MainActivity) getActivity()).addFragment(protocol, getString(R.string.nav_buli), true);
                    }
                });

            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing competitions failed");
                ex.toString();
            }

        }
    }
}
