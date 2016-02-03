package de.schwedt.weightlifting.app.news;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class NewsFeedFragment extends NewsListFragment {

    public News news;
    private NewsFeedListAdapter adapter;
    private boolean is_loading = false;
    private int visibleItems = 5;

    @Override
    protected void setEmptyListItem() {
        TextView emptyText = (TextView) fragment.findViewById(R.id.emptyArticles);
        emptyText.setVisibility(View.VISIBLE);
        listViewNews.setEmptyView(emptyText);
    }

    @Override
    protected void postItemsFetching() {
    }

    @Override
    protected void getNewsItems() {
        news = app.getNews(WeightliftingApp.UPDATE_IF_NECESSARY);
        if (news.getItems().size() == 0) {
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getNewsItems();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, News.TIMER_RETRY);
        } else {
            // We have news items to display
            try {
                adapter = new NewsFeedListAdapter(news.getFirstElements(visibleItems), getActivity());
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
                listViewNews.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                            if (!is_loading) {
                                is_loading = true;
                                addItems();
                            }
                        }
                    }
                });
            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing news feed failed");
                ex.toString();
            }
        }
    }

    private void addItems() {
        visibleItems += 5;
        adapter.setItems(news.getFirstElements(visibleItems));
        adapter.notifyDataSetChanged();
        is_loading = false;
    }
}
