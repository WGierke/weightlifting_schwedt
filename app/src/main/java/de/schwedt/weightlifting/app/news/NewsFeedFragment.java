package de.schwedt.weightlifting.app.news;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class NewsFeedFragment extends Fragment {

    public News news;
    private WeightliftingApp app;
    private View fragment;
    private ListView listViewNews;

    public NewsFeedFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing News Feed fragment");

        fragment = inflater.inflate(R.layout.news_feed, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        listViewNews = (ListView) fragment.findViewById(R.id.listView_News);

        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                getNews();
            }
        };
        Handler refreshHandler = new Handler();
        refreshHandler.postDelayed(refreshRunnable, WeightliftingApp.DISPLAY_DELAY);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getNews();
    }

    private void getNews() {
        news = app.getNews();
        if (news.getItems().size() == 0) {
            // No news items yet
            app.setLoading(true);
            Log.d(WeightliftingApp.TAG, "Waiting for news...");

            // Check again in a few seconds
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getNews();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, News.TIMER_RETRY);
        } else {
            // We have news items to display
            app.setLoading(false);
            try {
                NewsFeedListAdapter adapter = new NewsFeedListAdapter(News.casteArray(news.getItems()), getActivity());
                listViewNews.setAdapter(adapter);
                listViewNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Show an article fragment and put the selected index as argument
                        Fragment article = new NewsArticleFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("item", position);
                        article.setArguments(bundle);
                        ((MainActivity) getActivity()).addFragment(article, getString(R.string.nav_news), true);
                    }
                });
            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing news feed failed");
                ex.toString();
            }
        }
    }

}
