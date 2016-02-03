package de.schwedt.weightlifting.app.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public abstract class NewsListFragment extends Fragment {

    protected WeightliftingApp app;
    protected ListView listViewNews;
    protected View fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.news_list, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();
        listViewNews = (ListView) fragment.findViewById(R.id.listView_News);
        setEmptyListItem();
        getNewsItems();
        postItemsFetching();
        return fragment;
    }

    protected abstract void setEmptyListItem();

    protected abstract void postItemsFetching();

    protected abstract void getNewsItems();
}
