package de.schwedt.weightlifting.app.buli;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.UiHelper;

public class TeamListAdapter extends BaseAdapter {

    private ArrayList<TeamMember> items;
    private Activity activity;
    private LayoutInflater inflater;

    public TeamListAdapter(ArrayList<TeamMember> items, Activity activity) {
        this.items = items;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<TeamMember> getItems() {
        return items;
    }

    public void setItems(ArrayList<TeamMember> items) {
        this.items = items;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.buli_team_item, null);
        }

        Resources res = activity.getResources();

        TextView name = (TextView) view.findViewById(R.id.buli_member_name);
        name.setText(items.get(position).getName());

        TextView year = (TextView) view.findViewById(R.id.buli_member_year);
        year.setText(res.getString(R.string.buli_year) + ": " + items.get(position).getYear());

        TextView snatching = (TextView) view.findViewById(R.id.buli_member_snatching);
        snatching.setText(res.getString(R.string.buli_snatching) + ": " + items.get(position).getSnatching() + " kg");

        TextView jerking = (TextView) view.findViewById(R.id.buli_member_jerking);
        jerking.setText(res.getString(R.string.buli_jerking) + ": " + items.get(position).getJerking() + " kg");

        TextView maxPoints = (TextView) view.findViewById(R.id.buli_member_max_score);
        maxPoints.setText(res.getString(R.string.buli_relative_points) + ": " + items.get(position).getMaxScore());

        if (Team.itemsToMark.contains(items.get(position))) {
            UiHelper.colorFade(view, res);
            Team.itemsToMark.remove(items.get(position));
            UiHelper.refreshCounterNav(MainActivity.FRAGMENT_BULI, 0, Team.itemsToMark.size());
        }

        ImageView icon = (ImageView) view.findViewById(R.id.buli_member_image);

        if (items.get(position).getImageURL() != null) {
            ((WeightliftingApp) activity.getApplicationContext()).imageLoader.displayImage(items.get(position).getImageURL(), icon);
        } else {
            // Show default cover image
        }

        return view;
    }
}
