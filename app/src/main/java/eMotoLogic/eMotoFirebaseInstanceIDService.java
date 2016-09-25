package eMotoLogic;

/**
 * Created by chayut on 25/09/2016.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


//Class extending FirebaseInstanceIdService
public class eMotoFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "eMotoFirebaseIDService";

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        LocalBroadcastManager.getInstance(this).registerReceiver(tokenRequestReceiver,
                new IntentFilter("FCM_tokenRequestReceiver")); */

    }

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToMainService(refreshedToken);

    }

    private void sendRegistrationToMainService(String token) {

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        final Intent intent = new Intent("FCM_tokenReceiver");
        intent.putExtra("FCM_token",token);
        broadcastManager.sendBroadcast(intent);
    }



}