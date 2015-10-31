package de.schwedt.weightlifting.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.Date;

import de.schwedt.weightlifting.app.buli.Competitions;
import de.schwedt.weightlifting.app.buli.Table;
import de.schwedt.weightlifting.app.buli.Team;
import de.schwedt.weightlifting.app.faq.FaqItem;
import de.schwedt.weightlifting.app.gallery.Galleries;
import de.schwedt.weightlifting.app.helper.DataHelper;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.MemoryCache;
import de.schwedt.weightlifting.app.news.Events;
import de.schwedt.weightlifting.app.news.News;

public class WeightliftingApp extends Application {

    public static final String TAG = "WeightliftingLog";
    public static final String TEAM_NAME = "Oder-Sund-Team";
    public static final int DISPLAY_DELAY = 500;
    public static Context mContext;
    public static boolean isUpdatingAll = false;
    public static boolean[] isUpdating = new boolean[6];
    public boolean isInForeground = true;
    public boolean isInitialized = false;
    public MemoryCache memoryCache;
    public ImageLoader imageLoader;

    public News news;
    public Events events;
    public Team team;
    public Competitions competitions;
    public Table table;
    public Galleries galleries;
    public Handler splashCallbackHandler;

    public final static int UPDATE_STATUS_SUCCESSFUL = 200;
    public final static int UPDATE_STATUS_FAILED = 201;
    public final static int UPDATE_STATUS_PENDING = 202;

    public void initialize(Handler callbackHandler) {
        splashCallbackHandler = callbackHandler;
        DataHelper.sendMessage(splashCallbackHandler, SplashActivity.KEY_MESSAGE, getString(R.string.loading_data));

        Log.i(TAG, "Initializing...");
        long dateStart = new Date().getTime();

        memoryCache = new MemoryCache();
        imageLoader = new ImageLoader(getApplicationContext());

        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.faq_off_signal_heading), getString(R.string.faq_off_signal_question), getString(R.string.faq_off_signal_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.faq_bad_attempt_jerking_heading), getString(R.string.faq_bad_attempt_jerking_question), getString(R.string.faq_bad_attempt_jerking_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.winner_single_competition_heading), getString(R.string.winner_single_competition_question), getString(R.string.winner_single_competition_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.winner_team_competition_heading), getString(R.string.winner_team_competition_question), getString(R.string.winner_team_competition_answer)));

        long dateDiff = (new Date().getTime() - dateStart);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        mContext = getApplicationContext();

        //new updatetAsk().execute(this);
        getNews();
        getEvents();
        getTeam();
        getCompetitions();
        getTable();
        getGalleries();

        updateSplashScreen();

        isInitialized = true;

        Log.i(TAG, "Initialized (" + String.valueOf(dateDiff) + "ms)");
    }

    private void updateSplashScreen() {
        switch(getUpdateStatus()) {
            case UPDATE_STATUS_PENDING:
                Runnable refreshRunnable = new Runnable() {
                    @Override
                    public void run() {
                        updateSplashScreen();
                    }
                };
                Log.d(TAG, "Update status: pending");
                Handler refreshHandler = new Handler();
                refreshHandler.postDelayed(refreshRunnable, 200);
                break;
            case UPDATE_STATUS_SUCCESSFUL:
                Log.d(TAG, "Update status: Success");
                DataHelper.sendMessage(splashCallbackHandler, SplashActivity.KEY_MESSAGE, SplashActivity.MESSAGE_INITIALIZED);
                break;
            case UPDATE_STATUS_FAILED:
                Log.d(TAG, "Update status: Failed");
                DataHelper.sendMessage(splashCallbackHandler, SplashActivity.KEY_STATUS, SplashActivity.STATUS_ERROR_NETWORK);
                break;
        }
    }

    public void updateData() {
        //Update everything and save it on storage
        Log.d(TAG, "updating everything");
        news.refreshItems();
        events.refreshItems();
        team.refreshItems();
        competitions.refreshItems();
        table.refreshItems();
        galleries.refreshItems();
    }

    public int getUpdateStatus() {
        Log.d(WeightliftingApp.TAG, news.isUpToDate + " " + events.isUpToDate + " " + team.isUpToDate + " " + competitions.isUpToDate + " " + table.isUpToDate + " " + galleries.isUpToDate);
        if (news.updateFailed || events.updateFailed || team.updateFailed || competitions.updateFailed || table.updateFailed || galleries.updateFailed) {
            isUpdatingAll = false;
            return UPDATE_STATUS_FAILED;
        }
        if (news.isUpToDate && events.isUpToDate && team.isUpToDate && competitions.isUpToDate && table.isUpToDate && galleries.isUpToDate) {
            isUpdatingAll = false;
            return UPDATE_STATUS_SUCCESSFUL;
        }
        else
            return UPDATE_STATUS_PENDING;
    }

    public void setFinishUpdateFlags(boolean value) {
        news.isUpToDate = value;
        events.isUpToDate = value;
        team.isUpToDate = value;
        competitions.isUpToDate = value;
        table.isUpToDate = value;
        galleries.isUpToDate = value;
    }

    public UpdateableWrapper getWrapperItems(UpdateableWrapper myInstance, Class<?> myClass) {
        try {
            if (myInstance == null) {
                myInstance = (UpdateableWrapper) myClass.newInstance();
                String fileName = myClass.getDeclaredField("fileName").get(myInstance).toString();
                File file = getApplicationContext().getFileStreamPath(fileName);
                if (file.exists()) {
                    String fileContent = DataHelper.readIntern(fileName, getApplicationContext());
                    if (!fileContent.equals("")) {
                        myInstance.parseFromString(fileContent);
                        myInstance.setLastUpdate(new File(getFilesDir() + "/" + fileName).lastModified());
                        Log.d(TAG, myClass.getName() + ": read from memory:" + fileContent.substring(0 ,20) + "...");
                    }
                }

                if (myInstance.needsUpdate() && !myInstance.isUpdating && !isUpdatingAll) {
                    myInstance.refreshItems();
                } else {
                    myInstance.isUpToDate = true;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Error in getWrapperItems");
            e.printStackTrace();
        }
        return myInstance;
    }

    public News getNews() {
        news = (News) getWrapperItems(news, News.class);
        return news;
    }

    public Events getEvents() {
        events = (Events) getWrapperItems(events, Events.class);
        return events;
    }

    public Team getTeam() {
        team = (Team) getWrapperItems(team, Team.class);
        return team;
    }

    public Competitions getCompetitions() {
        competitions = (Competitions) getWrapperItems(competitions, Competitions.class);
        return competitions;
    }

    public Table getTable() {
        table = (Table) getWrapperItems(table, Table.class);
        return table;
    }

    public Galleries getGalleries() {
        galleries = (Galleries) getWrapperItems(galleries, Galleries.class);
        return galleries;
    }

/*    private class updatetAsk extends AsyncTask<WeightliftingApp, Integer, Long> {

        protected Long doInBackground(WeightliftingApp... apps) {
            //load data either from storage or from the internet
            WeightliftingApp app = apps[0];
            Looper.prepare();
            app.getNews();
            app.getEvents();
            app.getTeam();
            app.getCompetitions();
            app.getTable();
            app.getGalleries();
            Toast.makeText(getApplicationContext().getApplicationContext(), "ready", Toast.LENGTH_SHORT);
            Log.d(TAG, "ready");
            return null;
        }
    }
    */
}
