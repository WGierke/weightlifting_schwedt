package de.schwedt.weightlifting.app.service;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import de.schwedt.weightlifting.app.WeightliftingApp;
import de.schwedt.weightlifting.app.helper.UiHelper;

/**
 * Receive GCM messages
 */

public class MyGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    public MyGcmListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotification(data.getString("update"));
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

    private void sendNotification(String msg) {
        Log.d(WeightliftingApp.TAG, msg);
        Looper.prepare();
        UiHelper.showNotification("Update verfügbar", msg, 4, this);
    }
}