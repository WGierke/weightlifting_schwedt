package de.schwedt.weightlifting.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.faq.FaqAnswerFragment;
import de.schwedt.weightlifting.app.faq.FaqItem;
import de.schwedt.weightlifting.app.faq.FaqListAdapter;
import de.schwedt.weightlifting.app.helper.Constants;

public class FaqFragment extends Fragment {

    public static ArrayList<FaqItem> faqEntries = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.d(WeightliftingApp.TAG, "Showing Faq fragment");

        View fragment = inflater.inflate(R.layout.list_view_no_cover, container, false);

        FaqListAdapter adapter = new FaqListAdapter(faqEntries, getActivity());

        ListView faqMenu = (ListView) fragment.findViewById(R.id.list_view);
        faqMenu.setAdapter(adapter);
        faqMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show an article fragment and put the selected index as argument
                Fragment faqAnswer = new FaqAnswerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.ITEM, position);
                faqAnswer.setArguments(bundle);
                ((MainActivity) getActivity()).addFragment(faqAnswer, getString(R.string.nav_faq), true);
            }
        });

        return fragment;
    }
}