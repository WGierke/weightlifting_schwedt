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

import java.util.Date;

import de.schwedt.weightlifting.app.buli.BuliCompetitions;
import de.schwedt.weightlifting.app.buli.BuliTable;
import de.schwedt.weightlifting.app.buli.BuliTeam;
import de.schwedt.weightlifting.app.faq.FaqItem;
import de.schwedt.weightlifting.app.gallery.Galleries;
import de.schwedt.weightlifting.app.helper.ImageLoader;
import de.schwedt.weightlifting.app.helper.MemoryCache;
import de.schwedt.weightlifting.app.helper.NetworkHelper;
import de.schwedt.weightlifting.app.news.Events;
import de.schwedt.weightlifting.app.news.News;

//import de.schwedt.weightlifting.app.buliTeam.BuliTeam;

public class WeightliftingApp extends Application {

    public static final String TAG = "HPI";
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
    private BuliTeam buliTeam;
    private BuliCompetitions buliCompetitions;
    private BuliTable buliTable;
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
                    news = new News();
                    news.parseFromString(result, imageLoader);
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
                    events = new Events();
                    events.parseFromString(result, imageLoader);
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

    public BuliTeam getBuliTeam() {
        if (buliTeam == null) {
            buliTeam = new BuliTeam();
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
                    buliTeam = new BuliTeam();
                    buliTeam.parseFromString(result, imageLoader);
                    Log.i(TAG, "BuliTeam updated");
                } catch (Exception ex) {
                    Log.e(TAG, "BuliTeam update failed");
                    ex.printStackTrace();
                }
                isUpdatingTeam = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_BULI_TEAM, callBackHandler);
    }

    public BuliCompetitions getBuliCompetitions() {
        if (buliCompetitions == null) {
            buliCompetitions = new BuliCompetitions();
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
                    buliCompetitions = new BuliCompetitions();
                    buliCompetitions.parseFromString(result, imageLoader);
                    Log.i(TAG, "BuliCompetitions updated");
                } catch (Exception ex) {
                    Log.e(TAG, "BuliCompetitions update failed");
                    ex.printStackTrace();
                }
                isUpdatingCompetitions = false;
                setLoading(false);
            }
        };
        NetworkHelper.getWebRequest(NetworkHelper.URL_BULI_COMPETITIONS, callBackHandler);
    }

    public BuliTable getBuliTable() {
        if (buliTable == null) {
            buliTable = new BuliTable();
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
                    buliTable = new BuliTable();
                    buliTable.parseFromString(result, imageLoader);
                    Log.i(TAG, "BuliTable updated");
                } catch (Exception ex) {
                    Log.e(TAG, "BuliTable update failed");
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
                    galleries = new Galleries();
                    galleries.parseFromString(result, imageLoader);
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


    public void setLoading(boolean value) {
        try {
            mainActivity.setProgressBarIndeterminateVisibility(value);
        } catch (Exception ex) {
            // Not supported. Wayne.
        }
    }

}
