package de.schwedt.weightlifting.app.buli;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import de.schwedt.weightlifting.app.ImageFragment;
import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class TeamFragment extends ListViewFragment {

    private Team team;

    @Override
    protected void getBuliElements() {
        team = app.getTeam(WeightliftingApp.UPDATE_IF_NECESSARY);
        if (team.getItems().size() == 0) {
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getBuliElements();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, Team.TIMER_RETRY);
        } else {
            try {
                TeamListAdapter adapter = new TeamListAdapter(Team.casteArray(team.getItems()), getActivity());
                listViewBuli.setAdapter(adapter);
                listViewBuli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Fragment fr = new ImageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("imageURL", Team.casteArray(team.getItems()).get(position).getImageURL());
                        fr.setArguments(bundle);
                        ((MainActivity) getActivity()).addFragment(fr, getString(R.string.nav_buli), true);
                    }
                });

            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing team failed");
                ex.toString();
            }
        }
    }

    @Override
    protected void setCoverImage() {
        ImageView cover = (ImageView) fragment.findViewById(R.id.cover_buli);
        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover_team));
    }


    @Override
    protected void setEmptyListItem() {
        TextView emptyText = (TextView) fragment.findViewById(R.id.emptyTeam);
        emptyText.setVisibility(View.VISIBLE);
        listViewBuli.setEmptyView(emptyText);
    }
}
