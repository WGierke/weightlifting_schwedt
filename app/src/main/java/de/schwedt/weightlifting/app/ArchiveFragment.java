package de.schwedt.weightlifting.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.archive.ArchivedSeasonFragment;
import de.schwedt.weightlifting.app.archive.ArchivedSeasonListAdapter;
import de.schwedt.weightlifting.app.helper.Constants;

public class ArchiveFragment extends Fragment {

    public static ArrayList<String> archivedSeasonEntries = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_faq, container, false);

        ArchivedSeasonListAdapter adapter = new ArchivedSeasonListAdapter(archivedSeasonEntries, getActivity());

        ListView archivedSeasonList = (ListView) fragment.findViewById(R.id.listView_faqs);
        archivedSeasonList.setAdapter(adapter);
        archivedSeasonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show an archived season fragment and put the selected index as argument
                Fragment seasonFragment = new ArchivedSeasonFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.SEASON_ITEM_POSITION, position);
                seasonFragment.setArguments(bundle);
                ((MainActivity) getActivity()).addFragment(seasonFragment, archivedSeasonEntries.get(position), true);
            }
        });

        return fragment;
    }
}