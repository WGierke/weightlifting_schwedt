package de.schwedt.weightlifting.app.buli;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class ProtocolFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;
    private PastCompetition competition;

    private WebView webview;

    public ProtocolFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Protocol fragment");

        fragment = inflater.inflate(R.layout.buli_competition_protocol, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        webview = (WebView) fragment.findViewById(R.id.buli_competition_protocol);
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // Get protocol information from bundle
        try {
            Bundle bundle = this.getArguments();
            int position = bundle.getInt("item");
            competition = (PastCompetition) app.getCompetitions().getItem(position);
            webview.loadUrl(competition.getProtocolUrl());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fragment;
    }
}
