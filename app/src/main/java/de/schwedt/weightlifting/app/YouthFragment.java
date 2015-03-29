package de.schwedt.weightlifting.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dominik on 02.05.14.
 */
public class YouthFragment extends Fragment {
    private WeightliftingApp app;
    private View fragment;


    public YouthFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Youth fragment");

        fragment = inflater.inflate(R.layout.fragment_youth, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        return fragment;
    }
}


