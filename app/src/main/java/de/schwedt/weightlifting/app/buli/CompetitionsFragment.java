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
import android.widget.TextView;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class CompetitionsFragment extends ListViewFragment {

    protected Competitions competitions;

    @Override
    protected void getBuliElements() {
        competitions = app.getCompetitions(WeightliftingApp.UPDATE_IF_NECESSARY);
        if (competitions.getItems().size() == 0) {
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getBuliElements();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, Competitions.TIMER_RETRY);
        } else {
            try {
                CompetitionsListAdapter adapter = new CompetitionsListAdapter(Competitions.casteArray(competitions.getItems()), getActivity());
                listViewBuli.setAdapter(adapter);
                listViewBuli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Show the protocol which belongs to the competition
                        Fragment protocol = new ProtocolFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("protocol-url", Competitions.casteArray(competitions.getItems()).get(position).getProtocolUrl());
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

    @Override
    protected void setCoverImage() {
        ImageView  cover = (ImageView) fragment.findViewById(R.id.cover_buli);
        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover_competition));
    }

    @Override
    protected void setEmptyListItem() {
        TextView emptyText = (TextView) fragment.findViewById(R.id.emptyCompetitions);
        emptyText.setVisibility(View.VISIBLE);
        listViewBuli.setEmptyView(emptyText);
    }
}
