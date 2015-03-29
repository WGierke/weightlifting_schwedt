package de.schwedt.weightlifting.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    private WeightliftingApp app;
    private View fragment;

    public HomeFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Home fragment");

        fragment = inflater.inflate(R.layout.home, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        return fragment;
    }
}
