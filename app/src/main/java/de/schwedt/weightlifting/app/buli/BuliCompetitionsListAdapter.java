package de.schwedt.weightlifting.app.buli;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.UiHelper;

public class BuliCompetitionsListAdapter extends BaseAdapter {

    private ArrayList<BuliPastCompetition> items;
    private Activity activity;
    private LayoutInflater inflater;

    public BuliCompetitionsListAdapter(ArrayList<BuliPastCompetition> items, Activity activity) {
        this.items = items;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<BuliPastCompetition> getItems() {
        return items;
    }

    public void setItems(ArrayList<BuliPastCompetition> items) {
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
            view = inflater.inflate(R.layout.buli_competition_item, null);
        }

        TextView home = (TextView) view.findViewById(R.id.buli_competition_home);
        home.setText(items.get(position).getHome());
        if (home.getText().equals(WeightliftingApp.TEAM_NAME))
            home.setText(Html.fromHtml("<u>" + home.getText() + "</u>"));

        TextView guest = (TextView) view.findViewById(R.id.buli_competition_guest);
        guest.setText(items.get(position).getGuest());
        if (guest.getText().equals(WeightliftingApp.TEAM_NAME))
            guest.setText(Html.fromHtml("<u>" + guest.getText() + "</u>"));

        TextView score = (TextView) view.findViewById(R.id.buli_competition_score);
        score.setText(items.get(position).getScore());

        TextView date = (TextView) view.findViewById(R.id.buli_competition_date);
        date.setText(items.get(position).getDate());

        TextView location = (TextView) view.findViewById(R.id.buli_competition_location);
        location.setText(items.get(position).getLocation());

        if (BuliCompetitions.itemsToMark.contains(items.get(position))) {
            UiHelper.colorFade(view, activity.getResources());
            BuliCompetitions.itemsToMark.remove(items.get(position));
            UiHelper.refreshCounterNav(MainActivity.FRAGMENT_BULI, 1, BuliCompetitions.itemsToMark.size());
        }

        return view;
    }
}
