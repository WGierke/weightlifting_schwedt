package de.schwedt.weightlifting.app;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.Date;

import de.schwedt.weightlifting.app.buli.Competitions;
import de.schwedt.weightlifting.app.buli.Table;
import de.schwedt.weightlifting.app.buli.TableEntry;
import de.schwedt.weightlifting.app.buli.Team;
import de.schwedt.weightlifting.app.faq.FaqItem;
import de.schwedt.weightlifting.app.gallery.Galleries;
import de.schwedt.weightlifting.app.gallery.GalleryItem;
import de.schwedt.weightlifting.app.helper.DataHelper;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.MemoryCache;
import de.schwedt.weightlifting.app.helper.NetworkHelper;
import de.schwedt.weightlifting.app.news.Events;
import de.schwedt.weightlifting.app.news.News;

public class WeightliftingApp extends Application {

    public static final String TAG = "WeightliftingLog";
    public static final String TEAM_NAME = "KG Schwedt-Stralsund";
    public static final int DISPLAY_DELAY = 500;
    private static final int REFRESH_INTERVAL = 1000 * 60 * 60 * 24; //once a day
    public static boolean[] finishedUpdating = {false, false, false, false, false, false};
    public static boolean[] failedUpdates = {false, false, false, false, false, false};
    public static boolean isUpdatingAll = false;
    public boolean isInForeground = true;
    public boolean isInitialized = false;
    public MemoryCache memoryCache;
    public ImageLoader imageLoader;

    private News news;
    private Events events;
    private Team team;
    private Competitions competitions;
    private Table table;
    private Galleries galleries;
    private MainActivity mainActivity;


    public void initialize(Activity activity) {
        Log.i(TAG, "Initializing...");
        long dateStart = new Date().getTime();


        mainActivity = (MainActivity) activity;
        memoryCache = new MemoryCache();
        imageLoader = new ImageLoader(activity);

        refreshData();

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

    public void updateData() {
        //Update everything and save it on storage
        Log.d(TAG, "updating everything");
        news.update();
        events.update();
        team.update();
        competitions.update();
        table.update();
        galleries.update();
    }

    private void refreshData() {
        //update everything periodically
        getData();
        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                refreshData();
            }
        };
        Handler refreshHandler = new Handler();
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    public News getNews() {
        if (news == null) {
            news = new News();
            File file = new File(News.fileName);
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
            if (failedUpdates[0]) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Neuigkeiten"), Toast.LENGTH_LONG).show();
                }
                failedUpdates[0] = false;
            }
            if (News.itemsToMark.size() > 0) {
                showNotification(getResources().getQuantityString(R.plurals.new_news, News.itemsToMark.size(), News.itemsToMark.size()), News.getNotificationMessage(), 0);
            }
        }
        return news;
    }

    public Events getEvents() {
        if (events == null) {
            events = new Events();
            File file = new File(Events.fileName);
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
            if (failedUpdates[1]) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Veranstaltungs"), Toast.LENGTH_LONG).show();
                }
                failedUpdates[1] = false;
            }
            if (Events.itemsToMark.size() > 0) {
                showNotification(getResources().getQuantityString(R.plurals.new_events, Events.itemsToMark.size(), Events.itemsToMark.size()), Events.getNotificationMessage(), 1);
            }
        }
        return events;
    }

    public Team getTeam() {
        if (team == null) {
            team = new Team();
            File file = new File(Team.fileName);
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
            if (failedUpdates[2]) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Team"), Toast.LENGTH_LONG).show();
                }
                failedUpdates[2] = false;
            }
            if (Team.itemsToMark.size() > 0) {
                showNotification(getResources().getQuantityString(R.plurals.new_member, Team.itemsToMark.size(), Team.itemsToMark.size()), Team.getNotificationMessage(), 2);
            }
        }
        return team;
    }

    public Competitions getCompetitions() {
        if (competitions == null) {
            competitions = new Competitions();
            File file = new File(Competitions.fileName);
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
            if (failedUpdates[3]) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Wettkampf"), Toast.LENGTH_LONG).show();
                }
                failedUpdates[3] = false;
            }
            if (Competitions.itemsToMark.size() > 0) {
                showNotification(getResources().getQuantityString(R.plurals.new_competitions, Competitions.itemsToMark.size(), Competitions.itemsToMark.size()), Competitions.getNotificationMessage(), 3);
            }
        }
        return competitions;
    }

    public Table getTable() {
        if (table == null) {
            table = new Table();
            File file = new File(Table.fileName);
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
            if (failedUpdates[4]) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Tabellen"), Toast.LENGTH_LONG).show();
                }
                failedUpdates[4] = false;
            }
            if (Table.itemsToMark.size() > 0) {
                showNotification(getResources().getQuantityString(R.plurals.new_table, Table.itemsToMark.size(), Table.itemsToMark.size()), Table.getNotificationMessage(), 2);
            }
        }
        return table;
    }

    public Galleries getGalleries() {
        if (galleries == null) {
            galleries = new Galleries();
            String galleriesFileContent = DataHelper.readIntern("galleries.json", getApplicationContext());
            if (galleriesFileContent != "") {
                galleries.parseFromString(galleriesFileContent);
                galleries.setLastUpdate(new File(getFilesDir() + "/galleries.json").lastModified());
                Log.d(TAG, "Galleries: read from memory:" + galleriesFileContent);
            }
        }

        if (galleries.needsUpdate() && !galleries.isUpdating && !isUpdatingAll) {
            galleries.update();
            if (failedUpdates[5]) {
                if (isInForeground) {
                    Toast.makeText(getApplicationContext(), getString(R.string.update_failed, "Galerien"), Toast.LENGTH_LONG).show();
                }
                failedUpdates[5] = false;
            }
            if (Galleries.itemsToMark.size() > 0) {
                showNotification(getResources().getQuantityString(R.plurals.new_gallery, Galleries.itemsToMark.size(), Galleries.itemsToMark.size()), Galleries.getNotificationMessage(), 2);
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

    private void showNotification(String title, String message, int notificationId) {

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder normal = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationCompat.InboxStyle big = new NotificationCompat.InboxStyle(normal);

        //big.setSummaryText("this is the summary text");
        String[] parts = message.split("\\|");
        for (int i = 0; i < parts.length; i++) {
            big.addLine(parts[i]);
        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(notificationId, big.build());
    }
}
