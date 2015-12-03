package de.schwedt.weightlifting.app.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class ArticleImageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.news_article_image, container, false);
        WeightliftingApp app = (WeightliftingApp) getActivity().getApplicationContext();

        Bundle bundle = this.getArguments();
        String imageURL = bundle.getString("imageURL");

        ImageView imageView = (ImageView) fragment.findViewById(R.id.image);
        app.imageLoader.displayImage(imageURL, imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        return fragment;
    }
}
