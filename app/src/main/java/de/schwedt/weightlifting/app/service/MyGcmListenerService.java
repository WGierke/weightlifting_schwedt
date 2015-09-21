package de.schwedt.weightlifting.app.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import de.schwedt.weightlifting.app.WeightliftingApp;

/**
 * Receive GCM messages
 */

public class MyGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private WeightliftingApp app;

    public MyGcmListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotification("Received GCM Message: " + data.toString());
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        Log.d("Logger", msg);
        Looper.prepare();
        app = (WeightliftingApp) getApplicationContext();
        app.updateData(true);
        /*Intent launchIntent = getPackageManager().getLaunchIntentForPackage("de.schwedt.weightlifting.MainActivity");
        startActivity(launchIntent);*/
    }
}