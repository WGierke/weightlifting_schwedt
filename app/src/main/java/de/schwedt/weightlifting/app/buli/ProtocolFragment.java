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

    private View fragment;

    private WebView webview;

    public ProtocolFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Protocol fragment");

        fragment = inflater.inflate(R.layout.buli_competition_protocol, container, false);

        webview = (WebView) fragment.findViewById(R.id.buli_competition_protocol);
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // Get protocol url from bundle
        try {
            Bundle bundle = this.getArguments();
            String url = bundle.getString("protocol-url");
            webview.loadUrl(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fragment;
    }
}
