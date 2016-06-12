package eMotoLogic;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chayut on 7/02/15.
 */
public class eMotoServiceBroadcaster {

    static public void broadcastIntentWithState(String status,Context context) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(eMotoLogic.BROADCAST_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(eMotoLogic.BROADCAST_STATUS, status);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Gets an instance of the support library local broadcastmanager
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

    }


    static public void broadcastNewToken(String token,Context context) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(eMotoLogic.BROADCAST_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(eMotoLogic.BROADCAST_STATUS, eMotoService.RES_TOKEN_UPDATE);
        localIntent.putExtra(eMotoService.RES_TOKEN_UPDATE,token);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Gets an instance of the support library local broadcastmanager
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }

    static public void broadcastNewLocation(Location location,Context context) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(eMotoLogic.BROADCAST_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(eMotoLogic.BROADCAST_STATUS, eMotoService.RES_LOCATION_UPDATE);
        localIntent.putExtra(eMotoService.RES_LOCATION_UPDATE,location);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Gets an instance of the support library local broadcastmanager
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }

    static public void broadcastBTPairedList(ArrayList<String> cellList,Context context) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(eMotoLogic.BROADCAST_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(eMotoLogic.BROADCAST_STATUS, eMotoService.RES_BT_PAIRED_LIST);
        localIntent.putStringArrayListExtra(eMotoService.RES_BT_PAIRED_LIST, cellList);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Gets an instance of the support library local broadcastmanager
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }

    static public void broadcastBTStatus(String string,Context context) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(eMotoLogic.BROADCAST_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(eMotoLogic.BROADCAST_STATUS, eMotoService.RES_BT_STATUS);
        localIntent.putExtra(eMotoService.RES_BT_STATUS,string);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Gets an instance of the support library local broadcastmanager
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }

    static public void broadcastBTError(String string,Context context) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(eMotoLogic.BROADCAST_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(eMotoLogic.BROADCAST_STATUS, eMotoService.RES_BT_ERROR);
        localIntent.putExtra(eMotoService.RES_BT_ERROR,string);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Gets an instance of the support library local broadcastmanager
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }




}
