package de.schwedt.weightlifting.app.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.schwedt.weightlifting.app.WeightliftingApp;

public class NetworkHelper {

    public static final String URL_NEWS = "http://pastebin.com/raw.php?i=8us7U1sW";
    public static final String URL_EVENTS = "http://pastebin.com/raw.php?i=Ahh3fmGV";
    public static final String URL_BULI_TEAM = "http://pastebin.com/raw.php?i=FTWHySQn";
    public static final String URL_BULI_COMPETITIONS = "http://pastebin.com/raw.php?i=RGjFCd41";
    public static final String URL_BULI_TABLE = "http://pastebin.com/raw.php?i=C5bYpcAt";
    public static final String URL_GALLERY = "http://pastebin.com/raw.php?i=kyKhHxr8";
    /*
        public static final String URL_NEWS = "https://raw.githubusercontent.com/WGierke/weightlifting_schwedt/master/production/news.json";
        public static final String URL_EVENTS = "https://raw.githubusercontent.com/WGierke/weightlifting_schwedt/master/production/events.json";
        public static final String URL_BULI_TEAM = "https://raw.githubusercontent.com/WGierke/weightlifting_schwedt/master/production/team.json";
        public static final String URL_BULI_COMPETITIONS = "https://raw.githubusercontent.com/WGierke/weightlifting_schwedt/master/production/past_competitions.json";
        public static final String URL_BULI_TABLE = "https://raw.githubusercontent.com/WGierke/weightlifting_schwedt/master/production/table.json";
        public static final String URL_GALLERY = "https://raw.githubusercontent.com/WGierke/weightlifting_schwedt/master/production/galleries.json";
    */
    public static boolean isOnline = true;

    private static Context context;

    public NetworkHelper(Context context) {
        this.context = context;
    }

    public static void getWebRequest(final String url, Handler handler) {
        final Handler mHandler = handler;
        (new Thread() {
            @Override
            public void run() {
                try {
                    Log.d(WeightliftingApp.TAG, "Requesting " + url);
                    String result = getRequest(url);
                    if (result.indexOf("!DOCTYPE") != -1)
                        result = null;
                    Bundle data = new Bundle();
                    data.putString("result", result);
                    Message message = new Message();
                    message.setData(data);
                    mHandler.sendMessage(message);
                } catch (Exception ex) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    private static String getRequest(String myurl) throws IOException {
        InputStream is = null;
        String result = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            result = DataHelper.inputStreamToString(is);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return result;
    }

    public static boolean isOnline() {
        WeightliftingApp app = (WeightliftingApp) context.getApplicationContext();
        ConnectivityManager cm =
                (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isConnected = netInfo != null && netInfo.isConnectedOrConnecting();
        if (!isConnected) {
            isOnline = false;
            return false;
        }
        new ConnectionChecker().execute("http://www.google.com");
        return isOnline;
    }
}

class ConnectionChecker extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        try {
            return getHTTPStatus(urls[0]);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        NetworkHelper.isOnline = (result.equals("200"));
    }

    private String getHTTPStatus(String myurl) throws IOException {
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(2000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getResponseCode() + "";
        } catch (Exception e) {
            Log.e("network", "Error while requesting google", e);
        }
        return "500";
    }
}
