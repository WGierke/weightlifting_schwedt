package de.schwedt.weightlifting.app.gallery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.R;

public class GalleryOverviewAdapter extends BaseAdapter {

    private ArrayList<GalleryItem> items;
    private Activity activity;
    private LayoutInflater inflater;

    public GalleryOverviewAdapter(ArrayList<GalleryItem> items, Activity activity) {
        this.items = items;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<GalleryItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<GalleryItem> items) {
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
            view = inflater.inflate(R.layout.gallery_item, null);
        }

        TextView title = (TextView) view.findViewById(R.id.gallery_name);
        title.setText(items.get(position).getTitle());

        return view;
    }
}
