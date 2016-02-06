package de.schwedt.weightlifting.app.buli;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.schwedt.weightlifting.app.MainActivity;
import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.Constants;

public class TableFragment extends ListViewFragment {

    private Table table;

    @Override
    protected void getBuliElements() {
        table = app.getTable(WeightliftingApp.UPDATE_IF_NECESSARY);
        if (table.getItems().size() == 0) {
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getBuliElements();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, Table.TIMER_RETRY);
        } else {
            try {
                setTableListAdapterWithFilterCompetitionsFragment(Table.casteArray(table.getItems()), getActivity());
            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing table failed");
                ex.toString();
            }

        }
    }

    public void setTableListAdapterWithFilterCompetitionsFragment(final ArrayList<TableEntry> tableItems, Activity activity) {
        TableListAdapter adapter = new TableListAdapter(tableItems, activity);
        listViewBuli.setAdapter(adapter);
        listViewBuli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment protocol = new FilterCompetitionsFragment();
                Bundle bundle = new Bundle();
                TableEntry entry = (TableEntry) table.getItem(position);
                bundle.putString(Constants.CLUB_NAME, entry.getClub());
                protocol.setArguments(bundle);
                ((MainActivity) getActivity()).addFragment(protocol, entry.getClub(), true);
            }
        });
    }

    @Override
    protected void setCoverImage() {
        ImageView cover = (ImageView) fragment.findViewById(R.id.cover_buli);
        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover_competition));
    }

    @Override
    protected void setEmptyListItem() {
        TextView emptyText = (TextView) fragment.findViewById(R.id.emptyTables);
        emptyText.setVisibility(View.VISIBLE);
        listViewBuli.setEmptyView(emptyText);
    }
}
