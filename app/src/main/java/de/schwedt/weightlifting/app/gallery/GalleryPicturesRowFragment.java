package de.schwedt.weightlifting.app.gallery;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class GalleryPicturesRowFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;

    private ListView listViewPhotos;

    private Galleries galleries;

    public GalleryPicturesRowFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Gallery");

        fragment = inflater.inflate(R.layout.gallery_pictures, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        listViewPhotos = (ListView) fragment.findViewById(R.id.listView_gallery_pictures);

        getPhotos();

        return fragment;
    }

    private void getPhotos() {
        galleries = app.getGalleries();
        if (galleries.getItems().size() == 0) {
            // No news items yet
            app.setLoading(true);
            Log.d(WeightliftingApp.TAG, "Waiting for galleries...");

            // Check again in a few seconds
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getPhotos();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, Galleries.TIMER_RETRY);
        } else {
            // We have gallery photos to display
            app.setLoading(false);
            try {
                listViewPhotos = (ListView) fragment.findViewById(R.id.listView_gallery_pictures);

                ArrayList<GalleryPicturesRow> rows = new ArrayList<GalleryPicturesRow>();

                Bundle bundle = this.getArguments();
                int position = bundle.getInt("item");
                for (int i = 0; i < Galleries.casteArray(galleries.getItems()).get(position).getImageUrls().length; i += 2) {
                    GalleryPicturesRow row = new GalleryPicturesRow();

                    row.addGalleryPicture(Galleries.casteArray(galleries.getItems()).get(position).getImageUrl(i));
                    row.addGalleryPicture(Galleries.casteArray(galleries.getItems()).get(position).getImageUrl(i + 1));

                    rows.add(row);
                }

                GalleryRowAdapter adapter = new GalleryRowAdapter(rows, getActivity(), this);
                listViewPhotos.setAdapter(adapter);
            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing gallery pictures failed");
                ex.printStackTrace();
            }
        }
    }
}
