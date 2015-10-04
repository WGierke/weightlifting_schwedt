package de.schwedt.weightlifting.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
//import com.parse.ParseCrashReporting;

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
    public static final String TEAM_NAME = "KG Schwedt-Stralsund";
    public static final int DISPLAY_DELAY = 500;
    public static Context mContext;
    public static boolean isUpdatingAll = false;
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
    public MainActivity mainActivity;


    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        //ParseCrashReporting.enable(this);
        DataHelper.loadConfig();
        Parse.initialize(this, DataHelper.CONFIG_APP_ID, DataHelper.CONFIG_CLIENT_KEY);
    }

    public void initialize(Activity activity) {
        Log.i(TAG, "Initializing...");
        long dateStart = new Date().getTime();

        mainActivity = (MainActivity) activity;
        memoryCache = new MemoryCache();
        imageLoader = new ImageLoader(activity);

        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.faq_off_signal_heading), getString(R.string.faq_off_signal_question), getString(R.string.faq_off_signal_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.faq_bad_attempt_jerking_heading), getString(R.string.faq_bad_attempt_jerking_question), getString(R.string.faq_bad_attempt_jerking_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.winner_single_competition_heading), getString(R.string.winner_single_competition_question), getString(R.string.winner_single_competition_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.winner_team_competition_heading), getString(R.string.winner_team_competition_question), getString(R.string.winner_team_competition_answer)));

        loadSettings();

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

        getData();

        isInitialized = true;

        Log.i(TAG, "Initialized (" + String.valueOf(dateDiff) + "ms)");
    }

    public void loadSettings() {
        // Restore user data
    }

    public void getData() {
        //load data either from storage or from the internet
        getNews();
        getEvents();
        getTeam();
        getCompetitions();
        getTable();
        getGalleries();
    }

    public void updateData(boolean showNotification) {
        //Update everything and save it on storage
        Log.d(TAG, "updating everything");
        news.update();
        events.update();
        team.update();
        competitions.update();
        table.update();
        galleries.update();
        /*if (showNotification) {
            if (News.itemsToMark.size() > 0) {
                UiHelper.showNotification(getResources().getQuantityString(R.plurals.new_news, News.itemsToMark.size(), News.itemsToMark.size()), News.getNotificationMessage(), 0, this);
            }
            Log.d(TAG, Events.itemsToMark.size() + "");
            if (Events.itemsToMark.size() > 0) {
                UiHelper.showNotification(getResources().getQuantityString(R.plurals.new_events, Events.itemsToMark.size(), Events.itemsToMark.size()), Events.getNotificationMessage(), 1, this);
            }
            if (Team.itemsToMark.size() > 0) {
                UiHelper.showNotification(getResources().getQuantityString(R.plurals.new_member, Team.itemsToMark.size(), Team.itemsToMark.size()), Team.getNotificationMessage(), 2, this);
            }
            if (Competitions.itemsToMark.size() > 0) {
                UiHelper.showNotification(getResources().getQuantityString(R.plurals.new_competitions, Competitions.itemsToMark.size(), Competitions.itemsToMark.size()), Competitions.getNotificationMessage(), 3, this);
            }
            if (Table.itemsToMark.size() > 0) {
                UiHelper.showNotification(getResources().getQuantityString(R.plurals.new_table, Table.itemsToMark.size(), Table.itemsToMark.size()), Table.getNotificationMessage(), 2, this);
            }
            if (Galleries.itemsToMark.size() > 0) {
                UiHelper.showNotification(getResources().getQuantityString(R.plurals.new_gallery, Galleries.itemsToMark.size(), Galleries.itemsToMark.size()), Galleries.getNotificationMessage(), 2, this);
            }
        }*/
    }

    public News getNews() {
        if (news == null) {
            news = new News();
            File file = getApplicationContext().getFileStreamPath(News.fileName);
            if (file.exists()) {
                String newsFileContent = DataHelper.readIntern(News.fileName, getApplicationContext());
                if (!newsFileContent.equals("")) {
                    news.parseFromString(newsFileContent);
                    news.setLastUpdate(new File(getFilesDir() + "/" + News.fileName).lastModified());
                    Log.d(TAG, "News: read from memory:" + newsFileContent);
                }
            }
        }

        if (news.needsUpdate() && !news.isUpdating && !isUpdatingAll) {
            setLoading(true);
            news.update();
            setLoading(false);
            if (news.updateFailed) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Neuigkeiten"), Toast.LENGTH_LONG).show();
                }
                news.updateFailed = false;
            }
        }
        return news;
    }

    public Events getEvents() {
        if (events == null) {
            events = new Events();
            File file = getApplicationContext().getFileStreamPath(Events.fileName);
            if (file.exists()) {
                String eventsFileContent = DataHelper.readIntern(Events.fileName, getApplicationContext());
                if (eventsFileContent != "") {
                    events.parseFromString(eventsFileContent);
                    events.setLastUpdate(new File(getFilesDir() + "/" + Events.fileName).lastModified());
                    Log.d(TAG, "Events: read from memory:" + eventsFileContent);
                }
            }
        }

        if (events.needsUpdate() && !events.isUpdating && !isUpdatingAll) {
            setLoading(true);
            events.update();
            setLoading(false);
            if (events.updateFailed) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Veranstaltungs"), Toast.LENGTH_LONG).show();
                }
                events.updateFailed = false;
            }
        }
        return events;
    }

    public Team getTeam() {
        if (team == null) {
            team = new Team();
            File file = getApplicationContext().getFileStreamPath(Team.fileName);
            if (file.exists()) {
                String teamFileContent = DataHelper.readIntern(Team.fileName, getApplicationContext());
                if (teamFileContent != "") {
                    team.parseFromString(teamFileContent);
                    team.setLastUpdate(new File(getFilesDir() + "/" + Team.fileName).lastModified());
                    Log.d(TAG, "Team: read from memory:" + teamFileContent);
                }
            }
        }

        if (team.needsUpdate() && !team.isUpdating && !isUpdatingAll) {
            setLoading(true);
            team.update();
            setLoading(false);
            if (team.updateFailed) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Team"), Toast.LENGTH_LONG).show();
                }
                team.updateFailed = false;
            }
        }
        return team;
    }

    public Competitions getCompetitions() {
        if (competitions == null) {
            competitions = new Competitions();
            File file = getApplicationContext().getFileStreamPath(Competitions.fileName);
            if (file.exists()) {
                String competitionsFileContent = DataHelper.readIntern(Competitions.fileName, getApplicationContext());
                if (competitionsFileContent != "") {
                    competitions.parseFromString(competitionsFileContent);
                    competitions.setLastUpdate(new File(getFilesDir() + "/" + Competitions.fileName).lastModified());
                    Log.d(TAG, "Competitions: read from memory:" + competitionsFileContent);
                }
            }
        }

        if (competitions.needsUpdate() && !competitions.isUpdating && !isUpdatingAll) {
            setLoading(true);
            competitions.update();
            setLoading(false);
            if (competitions.updateFailed) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Wettkampf"), Toast.LENGTH_LONG).show();
                }
                competitions.updateFailed = false;
            }
        }
        return competitions;
    }

    public Table getTable() {
        if (table == null) {
            table = new Table();
            File file = getApplicationContext().getFileStreamPath(Table.fileName);
            if (file.exists()) {
                String tableFileContent = DataHelper.readIntern(Table.fileName, getApplicationContext());
                if (tableFileContent != "") {
                    table.parseFromString(tableFileContent);
                    table.setLastUpdate(new File(getFilesDir() + "/" + Table.fileName).lastModified());
                    Log.d(TAG, "Table: read from memory:" + tableFileContent);
                }
            }
        }

        if (table.needsUpdate() && !table.isUpdating && !isUpdatingAll) {
            setLoading(true);
            table.update();
            setLoading(false);
            if (table.updateFailed) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Tabellen"), Toast.LENGTH_LONG).show();
                }
                table.updateFailed = false;
            }
        }
        return table;
    }

    public Galleries getGalleries() {
        if (galleries == null) {
            galleries = new Galleries();
            File file = getApplicationContext().getFileStreamPath(Galleries.fileName);
            if (file.exists()) {
                String galleriesFileContent = DataHelper.readIntern(Galleries.fileName, getApplicationContext());
                if (galleriesFileContent != "") {
                    galleries.parseFromString(galleriesFileContent);
                    galleries.setLastUpdate(new File(getFilesDir() + "/" + Galleries.fileName).lastModified());
                    Log.d(TAG, "Galleries: read from memory:" + galleriesFileContent);
                }
            }
        }

        if (galleries.needsUpdate() && !galleries.isUpdating && !isUpdatingAll) {
            galleries.update();
            if (galleries.updateFailed) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Galerien"), Toast.LENGTH_LONG).show();
                }
                galleries.updateFailed = false;
            }
        }
        return galleries;
    }

    public void setLoading(boolean value) {
        try {
            mainActivity.setProgressBarIndeterminateVisibility(value);
        } catch (Exception ex) {
            // Not supported. Wayne.
        }
    }
}
