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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class NewsFeedListAdapter extends BaseAdapter {

    private ArrayList<NewsItem> items;
    private Activity activity;
    private LayoutInflater inflater;

    public NewsFeedListAdapter(ArrayList<NewsItem> items, Activity activity) {
        this.items = items;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<NewsItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<NewsItem> items) {
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
            view = inflater.inflate(R.layout.news_feed_item, null);
        }

        TextView title = (TextView) view.findViewById(R.id.event_title);
        title.setText(items.get(position).getHeading());

        TextView preview = (TextView) view.findViewById(R.id.event_preview);
        preview.setText(items.get(position).getPreview());

        TextView date = (TextView) view.findViewById(R.id.event_date);
        date.setText(items.get(position).getDate());

        if (News.itemsToMark.contains(items.get(position))) {
            News.itemsToMark.remove(items.get(position));
            MainActivity.navDrawerItems.get(MainActivity.FRAGMENT_NEWS).setCount(News.itemsToMark.size());
            Resources res = activity.getResources();
            ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), res.getColor(R.color.counter_text_bg), 0xffccc);
            colorFade.setDuration(5000);
            colorFade.start();
        }

        ImageView icon = (ImageView) view.findViewById(R.id.news_icon);
        if (items.get(position).getImage() != null) {
            icon.setImageDrawable(items.get(position).getImage());
        } else {
            if (items.get(position).getImageURL() != null) {
                ((WeightliftingApp) activity.getApplicationContext()).imageLoader.displayImage(items.get(position).getImageURL(), icon);
            } else {
                // Show default cover image
            }
        }

        return view;
    }
}
