package de.schwedt.weightlifting.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import de.schwedt.weightlifting.app.gallery.Galleries;
import de.schwedt.weightlifting.app.gallery.GalleryOverviewAdapter;
import de.schwedt.weightlifting.app.helper.UniversalImageLoader.Constants;
import de.schwedt.weightlifting.app.helper.UniversalImageLoader.ImageGridFragment;
import de.schwedt.weightlifting.app.helper.UniversalImageLoader.SimpleImageActivity;
import de.schwedt.weightlifting.app.news.News;

public class GalleriesFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;
    private Galleries galleries;

    public GalleriesFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Gallery fragment");

        fragment = inflater.inflate(R.layout.fragment_galleries, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                getGalleries();
            }
        };
        Handler refreshHandler = new Handler();
        refreshHandler.postDelayed(refreshRunnable, WeightliftingApp.DISPLAY_DELAY);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getGalleries();
    }

    private void getGalleries() {
        galleries = app.getGalleries();
        if (galleries.getItems().size() == 0) {
            // No news items yet
            app.setLoading(true);
            Log.d(WeightliftingApp.TAG, "Waiting for galleries...");

            // Check again in a few seconds
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getGalleries();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, News.TIMER_RETRY);
        } else {
            // We have news items to display
            app.setLoading(false);
            try {
                ListView listViewGalleries = (ListView) fragment.findViewById(R.id.listView_galleries);
                GalleryOverviewAdapter adapter = new GalleryOverviewAdapter(Galleries.casteArray(galleries.getItems()), getActivity());
                listViewGalleries.setAdapter(adapter);
                listViewGalleries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(app, SimpleImageActivity.class);
                        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
                        intent.putExtra(Constants.Extra.GALLERY_POSITION, position);
                        startActivity(intent);
                    }


                });
            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing gallery failed");
                ex.printStackTrace();
            }
        }
    }
}
