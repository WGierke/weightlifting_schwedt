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

public class TeamFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;

    private ImageView cover;
    private ListView listViewTeam;

    private Team buliTeam;

    public TeamFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Buli Team fragment");

        fragment = inflater.inflate(R.layout.buli_page, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        cover = (ImageView) fragment.findViewById(R.id.cover_buli);
        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover_team));

        listViewTeam = (ListView) fragment.findViewById(R.id.listView_Buli);

        /*Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                getTeam();
            }
        };
        Handler refreshHandler = new Handler();
        refreshHandler.postDelayed(refreshRunnable, WeightliftingApp.DISPLAY_DELAY);
        */
        getTeam();

        return fragment;
    }


    private void getTeam() {
        buliTeam = app.getBuliTeam();
        if (buliTeam.getItems().size() == 0) {
            // No news items yet
            app.setLoading(true);
            Log.d(WeightliftingApp.TAG, "Waiting for buli team...");

            // Check again in a few seconds
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getTeam();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, Team.TIMER_RETRY);
        } else {
            // We have buliTeam items to display
            app.setLoading(false);
            try {
                //ListView listViewNews = (ListView) fragment.findViewById(R.id.listView_Buli);
                TeamListAdapter adapter = new TeamListAdapter(Team.casteArray(buliTeam.getItems()), getActivity());
                listViewTeam.setAdapter(adapter);

            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing buliTeam failed");
                ex.toString();
            }

        }
    }
}
