package de.schwedt.weightlifting.app.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class NewsEventFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;
    private EventItem event;

    private TextView title;
    private TextView content;
    private TextView date_location;

    public NewsEventFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Event fragment");

        fragment = inflater.inflate(R.layout.news_article, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        title = (TextView) fragment.findViewById(R.id.article_title);
        content = (TextView) fragment.findViewById(R.id.article_content);
        date_location = (TextView) fragment.findViewById(R.id.article_date);

        // Get event information from bundle
        try {
            Bundle bundle = this.getArguments();
            int position = bundle.getInt("item");
            event = (EventItem) app.getEvents().getItem(position);
            showEvent();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fragment;
    }

    private void showEvent() {
        title.setText(event.getTitle());
        date_location.setText(event.getDate());
        if (event.getLocation().length() > 0) {
            date_location.setText(event.getDate() + " in " + event.getLocation());
        }
    }
}
