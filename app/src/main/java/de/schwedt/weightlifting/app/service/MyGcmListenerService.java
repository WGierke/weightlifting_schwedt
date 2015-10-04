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

    //Example message: Update#New Article#Victory in Görlitz#Push the button ...#4
    private void sendNotification(String msg) {
        Log.d(WeightliftingApp.TAG, msg);
        String [] parts = msg.split("#");
        Log.d(WeightliftingApp.TAG, parts[0]);
        assert parts.length == 5;
        if(parts[0].equals("Update")) {
            Log.d(WeightliftingApp.TAG, parts[1]);
            Log.d(WeightliftingApp.TAG, parts[2]);
            Log.d(WeightliftingApp.TAG, parts[3]);
            Log.d(WeightliftingApp.TAG, parts[4]);
            Looper.prepare();
            UiHelper.showNotification(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), this);
        }
    }
}