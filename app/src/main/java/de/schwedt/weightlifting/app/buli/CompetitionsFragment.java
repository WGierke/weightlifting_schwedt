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

public class CompetitionsFragment extends Fragment {

    protected WeightliftingApp app;
    protected View fragment;

    protected ImageView cover;
    protected ListView listViewCompetitions;

    protected Competitions buliCompetitions;

    public CompetitionsFragment() {
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

    protected void getBuliCompetitions() {
        buliCompetitions = app.getCompetitions();
        if (buliCompetitions.getItems().size() == 0) {
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
            refreshHandler.postDelayed(refreshRunnable, Competitions.TIMER_RETRY);
        } else {
            // We have competitions to display
            app.setLoading(false);
            try {
                CompetitionsListAdapter adapter = new CompetitionsListAdapter(Competitions.casteArray(buliCompetitions.getItems()), getActivity());
                listViewCompetitions.setAdapter(adapter);
                listViewCompetitions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Show the protocol which belongs to the competition
                        Fragment protocol = new ProtocolFragment();
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
