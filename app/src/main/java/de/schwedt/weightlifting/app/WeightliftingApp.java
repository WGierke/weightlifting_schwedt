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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.Date;

import de.schwedt.weightlifting.app.buli.Competitions;
import de.schwedt.weightlifting.app.buli.PastCompetition;
import de.schwedt.weightlifting.app.buli.Table;
import de.schwedt.weightlifting.app.buli.TableEntry;
import de.schwedt.weightlifting.app.buli.Team;
import de.schwedt.weightlifting.app.buli.TeamMember;
import de.schwedt.weightlifting.app.faq.FaqItem;
import de.schwedt.weightlifting.app.gallery.Galleries;
import de.schwedt.weightlifting.app.gallery.GalleryItem;
import de.schwedt.weightlifting.app.helper.DataHelper;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.MemoryCache;
import de.schwedt.weightlifting.app.helper.NetworkHelper;
import de.schwedt.weightlifting.app.news.EventItem;
import de.schwedt.weightlifting.app.news.Events;
import de.schwedt.weightlifting.app.news.News;
import de.schwedt.weightlifting.app.news.NewsItem;

public class WeightliftingApp extends Application {

    public static final String TAG = "Weightlifting";
    public static final String TEAM_NAME = "KG Schwedt-Stralsund";
    public static final int DISPLAY_DELAY = 500;
    private static final int REFRESH_INTERVAL = 1000 * 60 * 60 * 24; //once a day
    public static boolean[] finishedUpdating = {false, false, false, false, false, false};
    public static boolean isUpdatingAll = false;
    public boolean isInitialized = false;
    public boolean isUpdatingNews = false;
    public boolean isUpdatingEvents = false;
    public boolean isUpdatingTeam = false;
    public boolean isUpdatingCompetitions = false;
    public boolean isUpdatingTable = false;
    public boolean isUpdatingGalleries = false;
    public boolean isOnline = true;
    public boolean isInForeground = true;
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

        NetworkHelper networkHelper = new NetworkHelper(this);
        checkConnection();

        refreshData();

        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.faq_off_signal_heading), getString(R.string.faq_off_signal_question), getString(R.string.faq_off_signal_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.faq_bad_attempt_jerking_heading), getString(R.string.faq_bad_attempt_jerking_question), getString(R.string.faq_bad_attempt_jerking_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.winner_single_competition_heading), getString(R.string.winner_single_competition_question), getString(R.string.winner_single_competition_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.winner_team_competition_heading), getString(R.string.winner_team_competition_question), getString(R.string.winner_team_competition_answer)));

        loadSettings();

        isInitialized = true;
        long dateDiff = (new Date().getTime() - dateStart);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

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
        Log.d("network", "updating everything");
        updateNews();
        updateEvents();
        updateTeam();
        updateCompetitions();
        updateTable();
        updateGallery();
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

    public void checkConnection() {
        Log.d(TAG, "checking connection");
        isOnline = NetworkHelper.isOnline();
    }


    public News getNews() {
        if (news == null) {
            news = new News();
            String newsFileContent = DataHelper.readIntern("news.json", getApplicationContext());
            if (newsFileContent != "") {
                news.parseFromString(newsFileContent, imageLoader);
                news.setLastUpdate(new File(getFilesDir() + "/news.json").lastModified());
            }
        }

        if (news.needsUpdate() && !isUpdatingNews && !isUpdatingAll && isOnline) {
            updateNews();
        }

        if (!isOnline) {
            Log.d(TAG, "No connection");
        }
        return news;
    }

    public void updateNews() {
        Log.i(TAG, "Updating news...");
        isUpdatingNews = true;
        setLoading(true);
        Handler callBackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    if (result == null) {
                        checkConnection();
                        isUpdatingNews = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "news.json", getApplicationContext());

                    News newNews = new News();
                    newNews.parseFromString(result, imageLoader);

                    //Keep references to old items
                    for (int i = 0; i < newNews.getItems().size(); i++) {
                        for (int j = 0; j < news.getItems().size(); j++) {
                            if (((NewsItem) newNews.getItems().get(i)).equals((NewsItem) news.getItems().get(j))) {
                                newNews.getItems().set(i, news.getItems().get(j));
                            }
                        }
                    }

                    News.markNewItems(News.casteArray(news.getItems()), News.casteArray(newNews.getItems()));
                    if (News.itemsToMark.size() > 0) {
                        showNotification(getResources().getQuantityString(R.plurals.new_news, News.itemsToMark.size(), News.itemsToMark.size()), News.getNotificationMessage(), 0);
                    }
                    news = newNews;
                    Log.i(TAG, "News updated");
                } catch (Exception ex) {
                    Log.e(TAG, "News update failed");
                    ex.printStackTrace();
                }
                finishedUpdating[0] = true;
                isUpdatingNews = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_NEWS, callBackHandler);
    }

    public Events getEvents() {
        if (events == null) {
            events = new Events();
            String eventsFileContent = DataHelper.readIntern("events.json", getApplicationContext());
            if (eventsFileContent != "") {
                events.parseFromString(eventsFileContent, imageLoader);
                events.setLastUpdate(new File(getFilesDir() + "/events.json").lastModified());
            }
        }

        if (events.needsUpdate() && !isUpdatingEvents && !isUpdatingAll && isOnline) {
            updateEvents();
            //Toast.makeText(getApplicationContext(), "Updating events", Toast.LENGTH_LONG).show();
        }
        if (!isOnline) {
            Log.d(TAG, "No connection");
        }
        return events;
    }

    public void updateEvents() {
        Log.i(TAG, "Updating events...");
        isUpdatingEvents = true;
        setLoading(true);
        Handler callBackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    if (result == null) {
                        checkConnection();
                        isUpdatingEvents = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "events.json", getApplicationContext());

                    Events newEvents = new Events();
                    newEvents.parseFromString(result, imageLoader);

                    //Keep references to old items
                    for (int i = 0; i < newEvents.getItems().size(); i++) {
                        for (int j = 0; j < events.getItems().size(); j++) {
                            if (((EventItem) newEvents.getItems().get(i)).equals((EventItem) events.getItems().get(j))) {
                                newEvents.getItems().set(i, events.getItems().get(j));
                            }
                        }
                    }

                    Events.markNewItems(Events.casteArray(events.getItems()), Events.casteArray(newEvents.getItems()));
                    if (Events.itemsToMark.size() > 0) {
                        showNotification(getResources().getQuantityString(R.plurals.new_events, Events.itemsToMark.size(), Events.itemsToMark.size()), Events.getNotificationMessage(), 1);
                    }

                    events = newEvents;
                    Log.i(TAG, "Events updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Events update failed");
                    ex.printStackTrace();
                }
                finishedUpdating[1] = true;
                isUpdatingEvents = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_EVENTS, callBackHandler);
    }

    public Team getTeam() {
        if (team == null) {
            team = new Team();
            String teamFileContent = DataHelper.readIntern("team.json", getApplicationContext());
            if (teamFileContent != "") {
                team.parseFromString(teamFileContent, imageLoader);
                team.setLastUpdate(new File(getFilesDir() + "/team.json").lastModified());
            }
        }

        if (team.needsUpdate() && !isUpdatingTeam && !isUpdatingAll && isOnline) {
            updateTeam();
        }

        return team;
    }

    public void updateTeam() {
        Log.i(TAG, "Updating team...");
        isUpdatingTeam = true;
        setLoading(true);
        Handler callBackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    if (result == null) {
                        checkConnection();
                        isUpdatingTeam = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "team.json", getApplicationContext());

                    Team newTeam = new Team();
                    newTeam.parseFromString(result, imageLoader);

                    //Keep references to old items
                    for (int i = 0; i < newTeam.getItems().size(); i++) {
                        for (int j = 0; j < team.getItems().size(); j++) {
                            if (((TeamMember) newTeam.getItems().get(i)).equals((TeamMember) team.getItems().get(j))) {
                                newTeam.getItems().set(i, team.getItems().get(j));
                            }
                        }
                    }

                    Team.markNewItems(Team.casteArray(team.getItems()), Team.casteArray(newTeam.getItems()));
                    if (Team.itemsToMark.size() > 0) {
                        showNotification(getResources().getQuantityString(R.plurals.new_member, Team.itemsToMark.size(), Team.itemsToMark.size()), Team.getNotificationMessage(), 2);
                    }
                    team = newTeam;
                    Log.i(TAG, "Team updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Team update failed");
                    ex.printStackTrace();
                }
                finishedUpdating[2] = true;
                isUpdatingTeam = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_BULI_TEAM, callBackHandler);
    }

    public Competitions getCompetitions() {
        if (competitions == null) {
            competitions = new Competitions();
            String competitionsFileContent = DataHelper.readIntern("competitions.json", getApplicationContext());
            if (competitionsFileContent != "") {
                competitions.parseFromString(competitionsFileContent, imageLoader);
                competitions.setLastUpdate(new File(getFilesDir() + "/competitions.json").lastModified());
            }
        }

        if (competitions.needsUpdate() && !isUpdatingCompetitions && !isUpdatingAll && isOnline) {
            updateCompetitions();
        }

        return competitions;
    }

    public void updateCompetitions() {
        Log.i(TAG, "Updating team...");
        isUpdatingCompetitions = true;
        setLoading(true);
        Handler callBackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    if (result == null) {
                        checkConnection();
                        isUpdatingCompetitions = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "competitions.json", getApplicationContext());

                    Competitions newCompetitions = new Competitions();
                    newCompetitions.parseFromString(result, imageLoader);

                    //Keep references to old items
                    for (int i = 0; i < newCompetitions.getItems().size(); i++) {
                        for (int j = 0; j < competitions.getItems().size(); j++) {
                            if (((PastCompetition) newCompetitions.getItems().get(i)).equals((PastCompetition) competitions.getItems().get(j))) {
                                newCompetitions.getItems().set(i, competitions.getItems().get(j));
                            }
                        }
                    }

                    Competitions.markNewItems(Competitions.casteArray(competitions.getItems()), Competitions.casteArray(newCompetitions.getItems()));
                    if (Competitions.itemsToMark.size() > 0) {
                        showNotification(getResources().getQuantityString(R.plurals.new_competitions, Competitions.itemsToMark.size(), Competitions.itemsToMark.size()), Competitions.getNotificationMessage(), 3);
                    }
                    competitions = newCompetitions;
                    Log.i(TAG, "Competitions updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Competitions update failed");
                    ex.printStackTrace();
                }
                finishedUpdating[3] = true;
                isUpdatingCompetitions = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_BULI_COMPETITIONS, callBackHandler);
    }

    public Table getTable() {
        if (table == null) {
            table = new Table();
            String tableFileContent = DataHelper.readIntern("table.json", getApplicationContext());
            if (tableFileContent != "") {
                table.parseFromString(tableFileContent, imageLoader);
                table.setLastUpdate(new File(getFilesDir() + "/table.json").lastModified());
            }
        }

        if (table.needsUpdate() && !isUpdatingTable && !isUpdatingAll && isOnline) {
            updateTable();
        }

        return table;
    }

    public void updateTable() {
        Log.i(TAG, "Updating table...");
        isUpdatingTable = true;
        setLoading(true);
        Handler callBackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    if (result == null) {
                        checkConnection();
                        isUpdatingTable = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "table.json", getApplicationContext());

                    Table newTable = new Table();
                    newTable.parseFromString(result, imageLoader);

                    //Keep references to old items
                    for (int i = 0; i < newTable.getItems().size(); i++) {
                        for (int j = 0; j < table.getItems().size(); j++) {
                            if (((TableEntry) newTable.getItems().get(i)).equals((TableEntry) table.getItems().get(j))) {
                                newTable.getItems().set(i, table.getItems().get(j));
                            }
                        }
                    }

                    Table.markNewItems(Table.casteArray(table.getItems()), Table.casteArray(newTable.getItems()));
                    if (Table.itemsToMark.size() > 0) {
                        showNotification(getResources().getQuantityString(R.plurals.new_table, Table.itemsToMark.size(), Table.itemsToMark.size()), Table.getNotificationMessage(), 2);
                    }
                    table = newTable;
                    Log.i(TAG, "Table updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Table update failed");
                    ex.printStackTrace();
                }
                finishedUpdating[4] = true;
                isUpdatingTable = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_BULI_TABLE, callBackHandler);
    }

    public Galleries getGalleries() {
        if (galleries == null) {
            galleries = new Galleries();
            String galleriesFileContent = DataHelper.readIntern("galleries.json", getApplicationContext());
            if (galleriesFileContent != "") {
                galleries.parseFromString(galleriesFileContent, imageLoader);
                galleries.setLastUpdate(new File(getFilesDir() + "/galleries.json").lastModified());
            }
        }

        if (galleries.needsUpdate() && !isUpdatingGalleries && !isUpdatingAll && isOnline) {
            updateGallery();
        }

        return galleries;
    }

    public void updateGallery() {
        Log.i(TAG, "Updating Gallery...");
        isUpdatingGalleries = true;
        setLoading(true);
        Handler callBackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    if (result == null) {
                        checkConnection();
                        isUpdatingGalleries = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "galleries.json", getApplicationContext());

                    Galleries newGalleries = new Galleries();
                    newGalleries.parseFromString(result, imageLoader);

                    //Keep references to old items
                    for (int i = 0; i < newGalleries.getItems().size(); i++) {
                        for (int j = 0; j < galleries.getItems().size(); j++) {
                            if (((GalleryItem) newGalleries.getItems().get(i)).equals((GalleryItem) galleries.getItems().get(j))) {
                                newGalleries.getItems().set(i, galleries.getItems().get(j));
                            }
                        }
                    }

                    Galleries.markNewItems(Galleries.casteArray(galleries.getItems()), Galleries.casteArray(newGalleries.getItems()));
                    if (Galleries.itemsToMark.size() > 0) {
                        showNotification(getResources().getQuantityString(R.plurals.new_gallery, Galleries.itemsToMark.size(), Galleries.itemsToMark.size()), Galleries.getNotificationMessage(), 2);
                    }
                    galleries = newGalleries;
                    Log.i(TAG, "Gallery updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Gallery update failed");
                    ex.printStackTrace();
                }
                finishedUpdating[5] = true;
                isUpdatingGalleries = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_GALLERY, callBackHandler);
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
