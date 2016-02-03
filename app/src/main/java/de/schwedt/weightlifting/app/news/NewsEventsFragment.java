package de.schwedt.weightlifting.app.news;

import android.os.Handler;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.schwedt.weightlifting.app.NewsListFragment;
import de.schwedt.weightlifting.app.WeightliftingApp;

public class NewsEventsFragment extends NewsListFragment {

    private Events events;

    @Override
    protected void postItemsFetching() {
        int nextEvent = getNextEvent();
        if (nextEvent > 0) nextEvent -= 1; //previous event fades now
        listViewNews.setSelection(nextEvent);
    }

    @Override
    protected void getNewsItems() {
        events = app.getEvents(WeightliftingApp.UPDATE_IF_NECESSARY);
        if (events.getItems().size() == 0) {
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    getNewsItems();
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, News.TIMER_RETRY);
        } else {
            try {
                NewsEventsListAdapter adapter = new NewsEventsListAdapter(events.getItems(), getActivity());
                listViewNews.setAdapter(adapter);
            } catch (Exception ex) {
                Log.e(WeightliftingApp.TAG, "Showing events failed");
                ex.toString();
            }
        }
    }

    private int getNextEvent() {
        Calendar now = Calendar.getInstance();
        Integer current_day = now.get(Calendar.DATE);
        SimpleDateFormat df = new SimpleDateFormat("MMMM", Locale.GERMAN);
        String current_month = df.format(now.getTime());
        Boolean monthAlreadyFound = false;

        ArrayList<EventItem> items = Events.casteArray(events.getItems());
        EventItem event;
        for (int i = 0; i < items.size(); i++) {
            event = items.get(i);
            if (!monthAlreadyFound) {
                if (event.getDate().contains(current_month)) {
                    monthAlreadyFound = true;
                    if (Integer.valueOf(event.getDate().split("\\.")[0]) >= current_day) {
                        return i;
                    }
                }
            } else {
                if (event.getDate().contains(current_month)) {
                    if (Integer.valueOf(event.getDate().split("\\.")[0]) >= current_day) {
                        return i;
                    }
                } else
                    return i;
            }
        }
        return 0;
    }
}
