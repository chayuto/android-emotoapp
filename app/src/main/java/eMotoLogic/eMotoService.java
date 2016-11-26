package eMotoLogic;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by chayut on 6/02/15.
 */
public class eMotoService extends Service  {


    //Public RESPONSE
    public static final String RES_LOCATION_UPDATE = "RES_LOCATION_UPDATE";
    public static final String RES_LOCATION_ERROR = "RES_LOCATION_ERROR";
    public static final String RES_TOKEN_UPDATE = "RES_TOKEN_UPDATE";
    public static final String RES_TOKEN_UNAUTHORIZED = "RES_TOKEN_UNAUTHORIZED";
    public static final String RES_EXCEPTION_ENCOUNTERED = "RES_EXCEPTION_ENCOUNTERED";
    public static final String RES_BT_PAIRED_LIST= "RES_BT_PAIRED_LIST";
    public static final String RES_BT_DATA_RECEIVED = "RES_BT_DATA_RECEIVED";
    public static final String RES_BT_CONNECTED = "RES_BT_CONNECTED";
    public static final String RES_BT_DISCONNECTED = "RES_BT_DISCONNECTED";
    public static final String RES_BT_STATUS= "RES_BT_STATUS";
    public static final String RES_BT_ERROR= "RES_BT_ERROR";

    //Public CMD
    public final static String SERVICE_CMD = "ServiceCMD";

    public final static String CMD_SERVICE_START = "CMD_SERVICE_START";
    public final static String CMD_STARTAUTOREAUTHENTICATE = "CMD_STARTAUTOREAUTHENTICATE";
    public final static String CMD_GETTOKEN = "CMD_GETTOKEN";
    public final static String CMD_STARTLOCATIONSERVICE = "CMD_STARTLOCATIONSERVICE";
    public final static String CMD_STOPLOCATIONSERVICE = "CMD_STOPLOCATIONSERVICE";
    public final static String CMD_BT_GET_PAIRED_LIST= "CMD_BT_GET_PAIRED_LIST";
    public final static String CMD_BT_CONNECT_CELL = "CMD_BT_CONNECT_CELL";
    public final static String CMD_BT_GET_REPORT = "CMD_BT_GET_REPORT";
    public final static String CMD_BT_SEND_DATA = "CMD_BT_SEND_DATA";
    public final static String CMD_BT_SEND_TEST1 = "CMD_BT_SEND_TEST1";
    public final static String CMD_BT_SET_WIFI = "CMD_BT_SET_WIFI";
    public final static String CMD_BT_SET_CELL_AUTHEN = "CMD_BT_SET_CELL_AUTHEN";
    public final static String CMD_TEST_SCHEDULE = "CMD_TEST_SCHEDULE";
    //Debug
    private final static String TAG = "eMotoService";

    /** interface for clients that bind */
    private final IBinder mBinder = new LocalBinder();
    /** indicates whether onRebind should be used */
    boolean mAllowRebind = true;



    private eMotoLogic eLogic;
    private boolean logicInitalised = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        eLogic = new eMotoLogic(eMotoService.this);
        logicInitalised= true;


    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.d(TAG,"onStartCommand() id: " + startId + ": " + intent);

        /*
        if  (intent != null) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    classifyIntent(intent);
                }
            });
            t.start();
        }
        */

        return Service.START_STICKY;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind() id: "  + intent);
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService()*/
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }


    /** Called when The service is no longer used and is being destroyed*/
    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy()");
    }
    //endregion

    //region Testing

    public eMotoLogic getLogic()
    {
        return eLogic;
    }

    public String getHello()
    {
        return "Service Hello!";
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public eMotoService getService() {
            return eMotoService.this;
        }
    }









}
