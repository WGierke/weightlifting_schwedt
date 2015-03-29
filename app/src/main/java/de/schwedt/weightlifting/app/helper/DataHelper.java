package de.schwedt.weightlifting.app.helper;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class DataHelper {

    public static final String PREF_FILE_NAME = "hpi_preferences";
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public static byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    public static String decodeUTF8(byte[] data) {
        return new String(data, UTF8_CHARSET);
    }

    public static int dipToPx(int value, Activity activity) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    public static int pxToDip(int value, Activity activity) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) ((value - 0.5f) / scale);
    }

    public static byte[] inputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();
        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }
        return baos.toByteArray();
    }

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

    public static String getDeviceName() {
        return android.os.Build.MODEL;
    }

    public static String getDeviceOs() {
        return "Android " + android.os.Build.VERSION.RELEASE;
    }

    public static String getAppVersion(Application application) {
        try {
            return application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "n.a.";
        }
    }

    public static int getAppVersionCode(Application application) {
        try {
            return application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static void setPreference(String prev_name, String prev_value, Application application) {
        SharedPreferences preferences = application.getSharedPreferences(PREF_FILE_NAME, application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(prev_name, prev_value);
        editor.commit();
    }

    public static void setPreference(String prev_name, Boolean prev_value, Application application) {
        SharedPreferences preferences = application.getSharedPreferences(PREF_FILE_NAME, application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(prev_name, prev_value);
        editor.commit();
    }

    public static void deletePreference(String prev_name, Application application) {
        SharedPreferences preferences = application.getSharedPreferences(PREF_FILE_NAME, application.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(prev_name);
        editor.commit();
    }

    public static Boolean checkPreference(String prev_name, Application application) {
        SharedPreferences preferences = application.getSharedPreferences(PREF_FILE_NAME, application.MODE_PRIVATE);
        return preferences.contains(prev_name);
    }

    public static String getPreference(String prev_name, Application application) {
        SharedPreferences preferences = application.getSharedPreferences(PREF_FILE_NAME, application.MODE_PRIVATE);
        String storedPreference = preferences.getString(prev_name, null);
        return storedPreference;
    }

    public static Boolean getPreference(String prev_name, Boolean prev_default, Application application) {
        SharedPreferences preferences = application.getSharedPreferences(PREF_FILE_NAME, application.MODE_PRIVATE);
        Boolean storedPreference = preferences.getBoolean(prev_name, prev_default);
        return storedPreference;
    }

    public static String getSetting(String prev_name, Application application) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(application);
        String storedPreference = sharedPref.getString(prev_name, null);
        return storedPreference;
    }

    public static Boolean getSetting(String prev_name, Boolean prev_default, Application application) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(application);
        Boolean storedPreference = sharedPref.getBoolean(prev_name, prev_default);
        return storedPreference;
    }
}
