package de.schwedt.weightlifting.app;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import de.schwedt.weightlifting.app.buli.Competitions;
import de.schwedt.weightlifting.app.buli.Table;
import de.schwedt.weightlifting.app.buli.Team;
import de.schwedt.weightlifting.app.faq.FaqItem;
import de.schwedt.weightlifting.app.gallery.Galleries;
import de.schwedt.weightlifting.app.helper.DataHelper;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.MemoryCache;
import de.schwedt.weightlifting.app.helper.NetworkHelper;
import de.schwedt.weightlifting.app.helper.UiHelper;
import de.schwedt.weightlifting.app.news.Events;
import de.schwedt.weightlifting.app.news.News;

//import de.schwedt.weightlifting.app.buliTeam.Team;

public class WeightliftingApp extends Application {

    public static final String TAG = "Weightlifting";
    public static final String TEAM_NAME = "KG Schwedt-Stralsund";
    public static final int DISPLAY_DELAY = 500;
    public static final int CONNECTION_CHECK_CONNECTED = 30000;
    public static final int CONNECTION_CHECK_DISCONNECTED = 3000;

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
    private Team buliTeam;
    private Competitions buliCompetitions;
    private Table buliTable;
    private Galleries galleries;
    private ProgressDialog loadingProgressDialog;
    private MainActivity mainActivity;
    private SharedPreferences.Editor edit;
    private SharedPreferences userData;


    public void initialize(Activity activity) {
        Log.i(TAG, "Initializing...");
        long dateStart = new Date().getTime();


        mainActivity = (MainActivity) activity;
        memoryCache = new MemoryCache();
        imageLoader = new ImageLoader(activity);
        //imageLoader.clearCache();

        NetworkHelper networkHelper = new NetworkHelper(this);
        checkConnection(true);

        getData();

        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.faq_off_signal_heading), getString(R.string.faq_off_signal_question), getString(R.string.faq_off_signal_answer)));
        FaqFragment.faqEntries.add(new FaqItem(getString(R.string.faq_bad_attempt_jerking_heading), getString(R.string.faq_bad_attempt_jerking_question), getString(R.string.faq_bad_attempt_jerking_answer)));

        loadSettings();

        isInitialized = true;
        long dateDiff = (new Date().getTime() - dateStart);
        Log.i(TAG, "Initialized (" + String.valueOf(dateDiff) + "ms)");
    }

    public void loadSettings() {
        // Restore user data
    }

    public void getData() {
        getNews();
        getEvents();
        getBuliTeam();
        getBuliCompetitions();
        getBuliTable();
        getGalleries();
    }

    public void checkConnection(boolean checkPermanent) {
        Log.d(TAG, "checking connection");
        //if the device went offline
        if (isOnline && !NetworkHelper.isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.disconnected, Toast.LENGTH_LONG).show();
        }
        //if device went online
        if (!isOnline && NetworkHelper.isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.connected, Toast.LENGTH_LONG).show();
            isOnline = NetworkHelper.isOnline();
            getData();
        }
        isOnline = NetworkHelper.isOnline();
        if (checkPermanent) {
            Runnable refreshRunnable = new Runnable() {
                @Override
                public void run() {
                    checkConnection(true);
                }
            };
            Handler refreshHandler = new Handler();
            refreshHandler.postDelayed(refreshRunnable, ((isOnline) ? CONNECTION_CHECK_CONNECTED : CONNECTION_CHECK_DISCONNECTED));
        }
    }


    public News getNews() {
        if (news == null) {
            news = new News();
            news.parseFromString(readInternal("news.json"), imageLoader);
        }

        if (news.needsUpdate() && !isUpdatingNews && isOnline) {
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
                        checkConnection(false);
                        isUpdatingNews = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "news.json", getApplicationContext());

                    News newNews = new News();
                    newNews.parseFromString(result, imageLoader);
                    markNewItems((ArrayList) news.getItems(), (ArrayList) newNews.getItems(), (ArrayList) News.itemsToMark, MainActivity.FRAGMENT_NEWS, 0);
                    news = newNews;
                    Log.i(TAG, "News updated");
                } catch (Exception ex) {
                    Log.e(TAG, "News update failed");
                    ex.printStackTrace();
                }
                isUpdatingNews = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_NEWS, callBackHandler);
    }

    public Events getEvents() {
        if (events == null) {
            events = new Events();
        }

        if (events.needsUpdate() && !isUpdatingEvents && isOnline) {
            updateEvents();
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
                        checkConnection(false);
                        isUpdatingEvents = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "events.json", getApplicationContext());

                    Events newEvents = new Events();
                    newEvents.parseFromString(result, imageLoader);
                    markNewItems((ArrayList) events.getItems(), (ArrayList) newEvents.getItems(), (ArrayList) Events.itemsToMark, MainActivity.FRAGMENT_NEWS, 1);
                    events = newEvents;
                    Log.i(TAG, "Events updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Events update failed");
                    ex.printStackTrace();
                }
                isUpdatingEvents = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_EVENTS, callBackHandler);
    }

    public Team getBuliTeam() {
        if (buliTeam == null) {
            buliTeam = new Team();
        }

        if (buliTeam.needsUpdate() && !isUpdatingTeam && isOnline) {
            updateBuliTeam();
        }

        return buliTeam;
    }

    public void updateBuliTeam() {
        Log.i(TAG, "Updating buliTeam...");
        isUpdatingTeam = true;
        setLoading(true);
        Handler callBackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    if (result == null) {
                        checkConnection(false);
                        isUpdatingTeam = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "team.json", getApplicationContext());

                    Team newTeam = new Team();
                    newTeam.parseFromString(result, imageLoader);
                    markNewItems((ArrayList) buliTeam.getItems(), (ArrayList) newTeam.getItems(), (ArrayList) Team.itemsToMark, MainActivity.FRAGMENT_BULI, 0);
                    buliTeam = newTeam;
                    Log.i(TAG, "Team updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Team update failed");
                    ex.printStackTrace();
                }
                isUpdatingTeam = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_BULI_TEAM, callBackHandler);
    }

    public Competitions getBuliCompetitions() {
        if (buliCompetitions == null) {
            buliCompetitions = new Competitions();
        }

        if (buliCompetitions.needsUpdate() && !isUpdatingCompetitions && isOnline) {
            updateBuliCompetitions();
        }

        return buliCompetitions;
    }

    public void updateBuliCompetitions() {
        Log.i(TAG, "Updating buliTeam...");
        isUpdatingCompetitions = true;
        setLoading(true);
        Handler callBackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    if (result == null) {
                        checkConnection(false);
                        isUpdatingCompetitions = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "competitions.json", getApplicationContext());

                    Competitions newCompetitions = new Competitions();
                    newCompetitions.parseFromString(result, imageLoader);
                    markNewItems((ArrayList) buliCompetitions.getItems(), (ArrayList) newCompetitions.getItems(), (ArrayList) Competitions.itemsToMark, MainActivity.FRAGMENT_BULI, 1);
                    buliCompetitions = newCompetitions;
                    Log.i(TAG, "Competitions updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Competitions update failed");
                    ex.printStackTrace();
                }
                isUpdatingCompetitions = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_BULI_COMPETITIONS, callBackHandler);
    }

    public Table getBuliTable() {
        if (buliTable == null) {
            buliTable = new Table();
        }

        if (buliTable.needsUpdate() && !isUpdatingTable && isOnline) {
            updateBuliTable();
        }

        return buliTable;
    }

    public void updateBuliTable() {
        Log.i(TAG, "Updating buliTable...");
        isUpdatingTable = true;
        setLoading(true);
        Handler callBackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    if (result == null) {
                        checkConnection(false);
                        isUpdatingTable = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "table.json", getApplicationContext());


                    Table newTable = new Table();
                    newTable.parseFromString(result, imageLoader);
                    markNewItems((ArrayList) buliTable.getItems(), (ArrayList) newTable.getItems(), (ArrayList) Table.itemsToMark, MainActivity.FRAGMENT_BULI, 2);
                    buliTable = newTable;
                    Log.i(TAG, "Table updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Table update failed");
                    ex.printStackTrace();
                }
                isUpdatingTable = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_BULI_TABLE, callBackHandler);
    }


    public Galleries getGalleries() {
        if (galleries == null) {
            galleries = new Galleries();
        }

        if (galleries.needsUpdate() && !isUpdatingGalleries && isOnline) {
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
                        checkConnection(false);
                        isUpdatingGalleries = false;
                        setLoading(false);
                        return;
                    }
                    DataHelper.saveIntern(result, "galleries.json", getApplicationContext());

                    Galleries newGalleries = new Galleries();
                    newGalleries.parseFromString(result, imageLoader);
                    markNewItems((ArrayList) galleries.getItems(), (ArrayList) newGalleries.getItems(), (ArrayList) Galleries.itemsToMark, MainActivity.FRAGMENT_GALLERY, 0);
                    galleries = newGalleries;
                    Log.i(TAG, "Gallery updated");
                } catch (Exception ex) {
                    Log.e(TAG, "Gallery update failed");
                    ex.printStackTrace();
                }
                isUpdatingGalleries = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_GALLERY, callBackHandler);
    }

    private void markNewItems(ArrayList<UpdateableItem> oldItems, ArrayList<UpdateableItem> newItems, ArrayList<UpdateableItem> itemsToMark, int navigationPosition, int subPosition) {
        for (int i = 0; i < newItems.size(); i++) {
            if (!oldItems.contains(newItems.get(i))) {
                itemsToMark.add(newItems.get(i));
            }
        }
        UiHelper.refreshCounterNav(navigationPosition, subPosition, itemsToMark.size());
    }


    public void setLoading(boolean value) {
        try {
            mainActivity.setProgressBarIndeterminateVisibility(value);
        } catch (Exception ex) {
            // Not supported. Wayne.
        }
    }
}
