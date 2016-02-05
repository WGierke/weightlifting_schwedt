package de.schwedt.weightlifting.app.archive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.ArchiveFragment;
import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.helper.Constants;
import de.schwedt.weightlifting.app.helper.DataHelper;

public class ArchivedSeasonFragment extends Fragment {

    public static ArrayList<String> archivedRelayEntries = new ArrayList<>();
    private int seasonPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_faq, container, false);

        // Get archived season information from bundle
        try {
            Bundle bundle = this.getArguments();
            seasonPosition = bundle.getInt(Constants.SEASON_ITEM_POSITION);
            String archivedSeason = ArchiveFragment.archivedSeasonEntries.get(seasonPosition);
            archivedRelayEntries = DataHelper.getRelays(archivedSeason);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ArchivedRelayListAdapter adapter = new ArchivedRelayListAdapter(archivedRelayEntries, getActivity());

        ListView archivedSeasonList = (ListView) fragment.findViewById(R.id.listView_faqs);
        archivedSeasonList.setAdapter(adapter);
        archivedSeasonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show an archived relay fragment and put the selected index as argument
                Fragment archivedRelayFragment = new ArchivedRelayFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.SEASON_ITEM_POSITION, seasonPosition);
                bundle.putInt(Constants.RELAY_ITEM_POSITION, position);
                archivedRelayFragment.setArguments(bundle);
                ((MainActivity) getActivity()).addFragment(archivedRelayFragment, archivedRelayEntries.get(position), true);
            }
        });

        return fragment;
    }
}