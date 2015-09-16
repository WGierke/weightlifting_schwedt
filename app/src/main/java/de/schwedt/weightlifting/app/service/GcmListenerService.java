package de.schwedt.weightlifting.app.service;

import android.os.Bundle;
import android.util.Log;

/**
 * Receive GCM messages
 */

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    public GcmListenerService() {
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
    }
}