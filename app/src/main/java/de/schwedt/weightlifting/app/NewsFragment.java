package de.schwedt.weightlifting.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.schwedt.weightlifting.app.news.NewsEventsFragment;
import de.schwedt.weightlifting.app.news.NewsFeedFragment;

public class NewsFragment extends Fragment {

    private static final int FRAGMENT_FEED = 0;
    private static final int FRAGMENT_EVENTS = 1;
    NewsCollectionPagerAdapter mNewsCollectionPagerAdapter;
    ViewPager mViewPager;
    private WeightliftingApp app;
    private View fragment;
    private long lastUpdaDate = 0;

    public NewsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing News fragment");

        fragment = inflater.inflate(R.layout.news, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        mNewsCollectionPagerAdapter = new NewsCollectionPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager) fragment.findViewById(R.id.news_pager);
        mViewPager.setAdapter(mNewsCollectionPagerAdapter);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class NewsCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public NewsCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            switch (position) {
                case FRAGMENT_FEED:
                    fragment = new NewsFeedFragment();
                    break;
                case FRAGMENT_EVENTS:
                    fragment = new NewsEventsFragment();
                    break;
                default:
                    fragment = null;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title;
            switch (position) {
                case FRAGMENT_FEED:
                    title = getString(R.string.news_feed);
                    break;
                case FRAGMENT_EVENTS:
                    title = getString(R.string.news_events);
                    break;
                default:
                    title = getString(R.string.nav_news);
            }
            return title;
        }
    }

}
