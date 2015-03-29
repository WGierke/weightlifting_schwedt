package de.schwedt.weightlifting.app.gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class GalleryRowAdapter extends BaseAdapter {

    private ArrayList<GalleryPicturesRow> items;
    private Activity activity;
    private LayoutInflater inflater;
    private GalleryPicturesRowFragment fragment;
    private int index;

    public GalleryRowAdapter(ArrayList<GalleryPicturesRow> items, Activity activity, GalleryPicturesRowFragment fragment) {
        this.items = items;
        this.activity = activity;
        this.fragment = fragment;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<GalleryPicturesRow> getItems() {
        return items;
    }

    public void setItems(ArrayList<GalleryPicturesRow> items) {
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
        if (items.get(0) != null) {
            return items.size();
        } else {
            return 0;
        }
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
            view = inflater.inflate(R.layout.gallery_picture, null);
        }

        ImageView image1 = (ImageView) view.findViewById(R.id.gallery_image1);
        ImageView image2 = (ImageView) view.findViewById(R.id.gallery_image2);
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Image1");
                toggleImage(v);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Image2");
                toggleImage(v);
            }
        });


        if (items.get(position) != null) {
            ((WeightliftingApp) activity.getApplicationContext()).imageLoader.displayImage(items.get(position).getItem(0), image1);
        } else {
            image1.setImageResource(R.drawable.ic_launcher);
        }

        if (items.get(position).getItem(1) != null) {
            ((WeightliftingApp) activity.getApplicationContext()).imageLoader.displayImage(items.get(position).getItem(1), image2);
        } else {
            image2.setImageResource(R.drawable.ic_launcher);
        }

        return view;
    }

    private void toggleImage(View v) {
        final ImageView expandedImageView = (ImageView) fragment.getView().findViewById(R.id.expanded_image);

        expandedImageView.setImageBitmap(((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap());
        if (expandedImageView.getVisibility() == View.VISIBLE) {
            expandedImageView.setVisibility(View.INVISIBLE);
        } else {
            expandedImageView.setVisibility(View.VISIBLE);
        }
    }
}
