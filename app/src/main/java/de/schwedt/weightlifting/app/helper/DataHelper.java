package de.schwedt.weightlifting.app.helper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class DataHelper {

    public static String inputStreamToString(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return new String(total);
    }

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String trimString(String string, int length) {
        if (string.length() > length) {
            String result = string.substring(0, length);
            if (result.contains(" ")) {
                result = result.substring(0, result.lastIndexOf(" "));
                result = result + " ...";
            }
            return result;
        } else {
            return string;
        }
    }

    public static String readIntern(String fileName, Context context) {
        try {
            FileInputStream in = context.getApplicationContext().openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            //Log.d(WeightliftingApp.TAG, "read from " + fileName + " " + sb.toString().substring(0, 20) + "...");
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void saveIntern(String content, String fileName, Context context) {
        try {
            FileOutputStream fos = context.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
            //Log.d(WeightliftingApp.TAG, "saved in " + fileName + " content: " + content.substring(0, 20) + "...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendMessage(Handler mHandler, String key, String value) {
        //Log.d(WeightliftingApp.TAG, "Sending message to handler: [" + key + "] " + value);
        Bundle data = new Bundle();
        data.putString(key, value);
        Message message = new Message();
        message.setData(data);
        mHandler.sendMessage(message);
    }

    public static void sendMessage(Handler mHandler, String key, int value) {
        //Log.d(WeightliftingApp.TAG, "Sending message to handler: [" + key + "] " + String.valueOf(value));
        Bundle data = new Bundle();
        data.putInt(key, value);
        Message message = new Message();
        message.setData(data);
        mHandler.sendMessage(message);
    }

    public static String getStringFromStream(InputStream inputStream) {
        if (inputStream != null) {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();
        } else {
            return "";
        }
    }
}
