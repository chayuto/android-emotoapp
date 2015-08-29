package eMotoLogic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by chayut on 6/02/15.
 */
public class eMotoService extends Service implements eMotoServiceInterface {

    //Debug
    private final static String TAG = "eMotoService";

    // Defines Intent action
    public static final String BROADCAST_ACTION = "com.emotovate.android.eMotoApp.BROADCAST";
    public static final String BROADCAST_STATUS = "com.emotovate.android.eMotoApp.STATUS";

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

    //EXTRA
    public static final String EXTRA_EMOTOLOGINRESPONSE = "EXTRA_EMOTOLOGINRESPONSE";
    public static final String EXTRA_EMOTOCELL_NAME = "EXTRA_EMOTOCELL_NAME";
    public static final String EXTRA_WIFI_SSID = "EXTRA_WIFI_SSID";
    public static final String EXTRA_WIFI_SEC = "EXTRA_WIFI_SEC";
    public static final String EXTRA_WIFI_KEY = "EXTRA_WIFI_KEY";


    //Public CMD
    public final static String SERVICE_CMD = "ServiceCMD";
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


    //LocalVariable
    private eMotoLoginResponse mLoginResponse ;
    private Handler handler;


    private boolean serviceIsInitialized = false;


    /** interface for clients that bind */
    private final IBinder mBinder = new LocalBinder();
    /** indicates whether onRebind should be used */
    boolean mAllowRebind = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        // Handler will get associated with the current thread,
        // which is the main thread.
        handler = new Handler();

        mLoginResponse = new eMotoLoginResponse();

        serviceIsInitialized = true;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand() id: " + startId + ": " + intent);

        Thread t = new Thread(new Runnable() {
            public void run() {
                classifyIntent(intent);
            }
        });
        t.start();

        //this.classifyIntent(intent);
        return Service.START_NOT_STICKY;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
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

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        eMotoService getService() {
            return eMotoService.this;
        }
    }


    /**
     * classify intent from activity and decide what action to take
     *
     * @param intent
     */
    private void classifyIntent(Intent intent){
        String ServiceCMD = intent.getStringExtra(SERVICE_CMD);
        Log.d(TAG, ServiceCMD);

        switch (ServiceCMD){
            case CMD_STARTAUTOREAUTHENTICATE:
                mLoginResponse = intent.getExtras().getParcelable(EXTRA_EMOTOLOGINRESPONSE);
                if(mLoginResponse != null) {
                    Log.d(TAG, "Login Credential: " + mLoginResponse.getToken());
                    this.startAutoReauthenticate(mLoginResponse);
                    //eMotoServiceBroadcaster.broadcastNewToken(mLoginResponse.token, this);
                }
                else
                {
                    Log.d(TAG, "Null Object Reference!");
                    eMotoServiceBroadcaster.broadcastIntentWithState(RES_EXCEPTION_ENCOUNTERED, eMotoService.this);
                }
                break;
            case CMD_GETTOKEN:

                if (mLoginResponse == null)
                {
                    Log.d(TAG, "Null Object Reference!");
                    eMotoServiceBroadcaster.broadcastIntentWithState(RES_EXCEPTION_ENCOUNTERED, this);
                    break;
                }

                if(mLoginResponse.isSuccess()) {
                    Log.d(TAG, "Login Credential: " + mLoginResponse.getToken());
                    eMotoServiceBroadcaster.broadcastNewToken(mLoginResponse.getToken(), this);
                }
                else
                {
                    Log.d(TAG, "token is not valid");
                    eMotoServiceBroadcaster.broadcastIntentWithState(RES_TOKEN_UNAUTHORIZED, this);
                }
                break;
            case CMD_STARTLOCATIONSERVICE:
                startLocationService();

                break;
            case CMD_STOPLOCATIONSERVICE:
                stopLocationService();
                break;

            case CMD_BT_GET_PAIRED_LIST:
                //response with List of paired emotocell
                eMotoServiceBroadcaster.broadcastBTPairedList(mBTService.getPairedCellList(),eMotoService.this);
                break;
            case CMD_BT_CONNECT_CELL:
                if(mBTService.getServiceState() == eMotoBTService.BT_STATE_DISCONNECTED){
                    mBTService.startBTService(intent.getStringExtra(EXTRA_EMOTOCELL_NAME));
                }
                break;

            case CMD_BT_GET_REPORT:
                eMotoServiceBroadcaster.broadcastBTStatus(mBTService.getServiceReport(),eMotoService.this);
                break;

            case CMD_BT_SEND_TEST1:
                if(mBTService.sessionIsReady()) {
                    mBTService.getSession().testInteraction();
                }
                break;

            case CMD_BT_SET_WIFI:
                this.setCmdBtSetWifi(intent);
                break;

            case CMD_BT_SET_CELL_AUTHEN:
                this.setCmdBtSetCellAuthen();
                break;

            case CMD_TEST_SCHEDULE:
                this.testGetSchedule();
                break;
            default:
                Log.d(TAG, "Service Command Unrecognized");
                break;
        }
    }

    //region command actions

    private void setCmdBtSetCellAuthen(){
        //if the login session is active and cell is ready
        if(mBTService.sessionIsReady() && mLoginResponse.isSuccess() ) {

            mBTService.getSession().setDeviceAuthen(mLoginResponse.getCredential());
        }
    }

    private void setCmdBtSetWifi(Intent intent){
        if(mBTService.sessionIsReady()) {
            String ssid = intent.getStringExtra(EXTRA_WIFI_SSID);
            int secType = intent.getIntExtra(EXTRA_WIFI_SEC,0);
            String key = intent.getStringExtra(EXTRA_WIFI_KEY);
            mBTService.getSession().setDeviceWifi(ssid, secType, key);
        }
    }


    //endregion


    //region Testing

    private void testGetSchedule ()
    {
        String token = mLoginResponse.getToken();
        eMotoCell mCell = eMotoCell.getDeviceFromServer(token, "00000000");

        eMotoAdsSchedule.getScheduleAds (token, mCell);

    }
    //endregion

    //region Authentication Service


    private ScheduledThreadPoolExecutor stpe;

    private void startAutoReauthenticate (eMotoLoginResponse mLoginResponse) {

        try {

            int corePoolSize = 1;
            //creates ScheduledThreadPoolExecutor object with number of thread 2
            stpe = new ScheduledThreadPoolExecutor(corePoolSize);

            //starts runnable thread once
            RunnableThread runThread = new RunnableThread();
            runThread.setLoginResponse(mLoginResponse);

            int delay = Integer.parseInt(mLoginResponse.getIdle());

            //starts callable thread that will start after delay minutes
            ScheduledFuture sf = stpe.scheduleAtFixedRate(runThread,delay,delay,
                    TimeUnit.MINUTES);

            int activeCnt = stpe.getActiveCount();
            System.out.println("activeCnt:" + activeCnt);
        }

        catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private void stopAutoReauthenticate ()
    {
        stpe.shutdownNow();

    }

    /**
     * Runable thread to be call periodically to authenticate with server
     *
     */
    class RunnableThread implements Runnable {

        private eMotoLoginResponse mLoginResponse;

        public void setLoginResponse(eMotoLoginResponse mResponse){
            mLoginResponse = mResponse;
        }
        @Override
        public void run() {

            mLoginResponse = eMotoUtility.performLoginWithLoginResponse(mLoginResponse);

            if(mLoginResponse.isSuccess()) {

                eMotoServiceBroadcaster.broadcastNewToken(mLoginResponse.getToken(), eMotoService.this);
            }

            System.out.println("run:" + mLoginResponse.getToken());
        }
    }

    public String getLoginToken(){
        return mLoginResponse.getToken();
    }

    //endregion

    //region Bluetooth Service
    private eMotoBTService mBTService = new eMotoBTService(eMotoService.this,this);


    //endregion

    //region Location Service



    private LocationManager locationManager;
    private LocationListener locationListener;
    private String locationProvider;
    private boolean locationServiceIsRunning = false;

    private void startLocationService()
    {
        Log.d(TAG, "startLocationService()");
        //if(!locationServiceIsRunning) {
            // Define a listener that responds to location updates
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    Log.d(TAG, location.toString());
                    eMotoServiceBroadcaster.broadcastNewLocation(location, eMotoService.this);
                    //Toast.toastOnUI(String.format("Location: %f, %f : %f", location.getLatitude(), location.getLongitude(), location.getAccuracy()));
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d("Location", "onStatusChanged : " + provider);
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            // Get the location manager
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            locationProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(locationProvider);

            Log.d(TAG,"Location Provider:"+ locationProvider);
            //Log.d(TAG,"Last know location:" + location.toString());
            // Register the listener with the Location Manager to receive location updates

            final Runnable r = new Runnable() {
                public void run() {
                    locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);//.NETWORK_PROVIDER,.GPS_PROVIDER
                }
            };
            handler.post(r);

          locationServiceIsRunning = true;
        //}
    }

    private void stopLocationService(){
        locationManager.removeUpdates(locationListener);
        locationServiceIsRunning =false;
    }



    //endregion

}
