package de.schwedt.weightlifting.app.news;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.DataHelper;

public class NewsArticleFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;
    private NewsItem article;

    private TextView heading;
    private TextView content;
    private TextView date;
    private TextView url;
    private ImageView cover;
    private ScrollView scrollView;

    public NewsArticleFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Article fragment");

        fragment = inflater.inflate(R.layout.news_article, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        heading = (TextView) fragment.findViewById(R.id.article_title);
        content = (TextView) fragment.findViewById(R.id.article_content);
        date = (TextView) fragment.findViewById(R.id.article_date);
        url = (TextView) fragment.findViewById(R.id.article_url);
        cover = (ImageView) fragment.findViewById(R.id.article_cover);

        scrollView = (ScrollView) fragment.findViewById(R.id.article_scrollView);

        // Get article information from bundle
        try {
            Bundle bundle = this.getArguments();
            int position = bundle.getInt("item");
            article = app.getNews().getNewsItem(position);
            showArticle();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError();
        }

        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataHelper.pxToDip(cover.getLayoutParams().height, getActivity()) > 250) {
                    animateCoverHeight(150);
                } else {
                    animateCoverHeight(300);
                }
            }
        });

        setCoverHeight(0);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //scrollView.scrollTo(0, DataHelper.dipToPx(100, getActivity()));
                animateCoverHeight(150);
            }
        }, 1);

        return fragment;
    }

    private void setCoverHeightDip(int height) {
        int pixels = DataHelper.dipToPx(height, getActivity());
        ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
        layoutParams.height = pixels;
        cover.setLayoutParams(layoutParams);
    }

    private void setCoverHeight(int height) {
        ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
        layoutParams.height = height;
        cover.setLayoutParams(layoutParams);
    }

    private void animateCoverHeight(int height) {
        int newHeight = DataHelper.dipToPx(height, getActivity());
        int currentHeight = cover.getLayoutParams().height;

        int steps;
        int increment;

        // increase or decrease height?
        if (currentHeight > newHeight) {
            steps = currentHeight - newHeight;
            increment = -1;
        } else {
            steps = newHeight - currentHeight;
            increment = 1;
        }

        final int count = steps;
        final int change = increment;
        final int current = currentHeight;

        // set height in thread
        final Handler handler = new Handler();
        (new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    final int new_height = current + (i * change);
                    handler.post(new Runnable() {
                        public void run() {
                            setCoverHeight(new_height);
                        }
                    });
                    try {
                        sleep(1);
                    } catch (Exception ex) {
                        break;
                    }
                }
            }
        }).start();
    }


    private void showArticle() {
        heading.setText(article.getHeading());
        content.setText(article.getContent());
        date.setText(article.getDate());
        url.setText(Html.fromHtml("<a href=\"" + article.getURL() + "\">" + getString(R.string.news_article_url) + "</a>"));
        url.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        if (article.getImage() != null) {
            cover.setImageDrawable(article.getImage());
        } else {
            if (article.getImageURL() != null) {
                app.imageLoader.displayImage(article.getImageURL(), cover);
            } else {
                cover.setImageDrawable(getResources().getDrawable(R.drawable.cover_home));
            }
        }
    }

    private void showError() {
        heading.setText(getString(R.string.news_error_heading));
        content.setText(getString(R.string.news_error_content));
        date.setVisibility(View.GONE);

        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover_error));
    }
}
