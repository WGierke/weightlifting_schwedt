package de.schwedt.weightlifting.app.helper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.service.GCMPreferences;

public class NetworkHelper {

    private static final String BASE_SERVER_URL = "http://weightliftingschwedt.appspot.com/";

    public static void getWebRequest(final String url, Handler handler) {
        final Handler mHandler = handler;
        (new Thread() {
            @Override
            public void run() {
                Bundle data = new Bundle();
                try {
                    //Log.d(WeightliftingApp.TAG, "Requesting " + url);
                    String result = getRequest(url);
                    if (result.contains("!DOCTYPE"))
                        result = null;
                    data.putString("result", result);
                } catch (Exception ex) {
                    //Log.d(WeightliftingApp.TAG, "Error while fetching " + url + ":" + ex.getMessage());
                    data.putString("result", "");
                }
                Message message = new Message();
                message.setData(data);
                mHandler.sendMessage(message);
            }
        }).start();
    }

    public static String getRequest(String myurl) throws Exception {
        InputStream is;
        String result;

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        is = conn.getInputStream();
        result = DataHelper.inputStreamToString(is);

        if (is != null) {
            is.close();
        }

        return result;
    }

    public static void sendToken(String token, Handler handler) {
        try {
            String data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
            String url = BASE_SERVER_URL + "add_token";
            sendAuthenticatedHttpPostRequest(url, data, handler);
        } catch (UnsupportedEncodingException ignored) {
            ignored.printStackTrace();
        }
    }


    public static void sendAuthenticatedHttpPostRequest(final String url, final String data, final Handler handler) {
        (new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle resultBundle = new Bundle();
                try {
                    byte[] dataBytes = data.getBytes();
                    int dataLength = dataBytes.length;
                    URL httpUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setRequestProperty("Content-Length", Integer.toString(dataLength));
                    conn.setRequestProperty("X-Secret-Key", Keys.SECRET_KEY);
                    conn.setUseCaches(false);

                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();
                    int responseCode = conn.getResponseCode();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder responseBuffer = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        responseBuffer.append(inputLine);
                    }
                    in.close();
                    String response = responseBuffer.toString();
                    if (responseCode == 200 && response.contains("Success")) {
                        resultBundle.putString(GCMPreferences.RESULT_KEY, GCMPreferences.RESULT_SUCCESS);
                    } else {
                        throw new Exception("Request failed with Code " + responseCode + " and content '" + response + "'");
                    }
                } catch (Exception e) {
                    Log.d(WeightliftingApp.TAG, "posting authenticated data failed: " + e.getMessage());
                    resultBundle.putString(GCMPreferences.RESULT_KEY, GCMPreferences.RESULT_FAILURE);
                }
                message.setData(resultBundle);
                handler.sendMessage(message);
            }
        }).start();
    }
}
