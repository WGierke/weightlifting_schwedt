package de.schwedt.weightlifting.app.news;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;

public class NewsEventsListAdapter extends BaseAdapter {

    private ArrayList<EventItem> items;
    private Activity activity;
    private LayoutInflater inflater;
    private int type;

    public NewsEventsListAdapter(ArrayList<EventItem> items, Activity activity) {
        this.items = items;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<EventItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<EventItem> items) {
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
            view = inflater.inflate(R.layout.news_events_item, null);
        }

        TextView title = (TextView) view.findViewById(R.id.event_title);
        title.setText(items.get(position).getTitle());

        TextView date = (TextView) view.findViewById(R.id.event_date);
        date.setText(items.get(position).getDate());

        TextView location = (TextView) view.findViewById(R.id.event_location);
        location.setText(items.get(position).getLocation());

        if (Events.itemsToMark.contains(items.get(position))) {
            Events.itemsToMark.remove(items.get(position));
            MainActivity.navDrawerItems.get(MainActivity.FRAGMENT_NEWS).decreaseCount(1);
            Resources res = activity.getResources();
            ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), res.getColor(R.color.counter_text_bg), 0xffccc);
            colorFade.setDuration(3000);
            colorFade.start();
        }

        return view;
    }
}
