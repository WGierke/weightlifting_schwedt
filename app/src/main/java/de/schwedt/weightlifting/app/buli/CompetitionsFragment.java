package de.schwedt.weightlifting.app.buli;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.Constants;

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
                setCompetitionsListAdaptherWithProtocolFragment(Competitions.casteArray(competitions.getItems()), getActivity());
            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing competitions failed");
                ex.toString();
            }

        }
    }

    public void setCompetitionsListAdaptherWithProtocolFragment(final ArrayList<PastCompetition> competitionItems, Activity activity) {
        CompetitionsListAdapter adapter = new CompetitionsListAdapter(competitionItems, activity);
        listViewBuli.setAdapter(adapter);
        listViewBuli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show the protocol which belongs to the competition
                Fragment protocol = new ProtocolFragment();
                Bundle bundle = new Bundle();
                PastCompetition competition = competitionItems.get(position);
                bundle.putString(Constants.PROTOCOL_URL, competition.getProtocolUrl());
                bundle.putString(Constants.COMPETITION_PARTIES, competition.getHome() + " vs. " + competition.getGuest() + ": " + competition.getScore());
                protocol.setArguments(bundle);
                ((MainActivity) getActivity()).addFragment(protocol, getString(R.string.nav_buli), true);
            }
        });
    }

    @Override
    protected void setCoverImage() {
        ImageView cover = (ImageView) fragment.findViewById(R.id.cover_buli);
        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover_competition));
    }

    @Override
    protected void setEmptyListItem() {
        TextView emptyText = (TextView) fragment.findViewById(R.id.emptyCompetitions);
        emptyText.setVisibility(View.VISIBLE);
        listViewBuli.setEmptyView(emptyText);
    }
}
