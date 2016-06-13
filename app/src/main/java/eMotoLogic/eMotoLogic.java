package eMotoLogic;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by chayut on 7/06/16.
 */

public class eMotoLogic implements eMotoLogicInterface {

    // Defines Intent action
    public static final String BROADCAST_ACTION = "com.emotovate.android.eMotoApp.BROADCAST";
    public static final String BROADCAST_STATUS = "com.emotovate.android.eMotoApp.STATUS";
    //EXTRA
    public static final String EXTRA_EMOTOLOGINRESPONSE = "EXTRA_EMOTOLOGINRESPONSE";
    public static final String EXTRA_EMOTOCELL_NAME = "EXTRA_EMOTOCELL_NAME";
    public static final String EXTRA_WIFI_SSID = "EXTRA_WIFI_SSID";
    public static final String EXTRA_WIFI_SEC = "EXTRA_WIFI_SEC";
    public static final String EXTRA_WIFI_KEY = "EXTRA_WIFI_KEY";
    private static final String TAG = "eMotoLogic";
    Context mContext;

    private ScheduledThreadPoolExecutor stpe;
    private int swapSeconds = 15;
    private int pullSeconds = 3600;
    private int reportSeconds = 3600;

    //LocalVariable
    private eMotoLoginResponse mLoginResponse ;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String locationProvider;
    private Handler handler;
    private boolean locationServiceIsRunning = false;

    //region Bluetooth Service
    private eMotoBTService mBTService ;




    public eMotoLogic(Context context) {

        Log.d(TAG,"eMotoLogic initiated");

        mLoginResponse = new eMotoLoginResponse();

        // Handler will get associated with the current thread,
        // which is the main thread.
        handler = new Handler();

        mContext = context;

        mBTService = new eMotoBTService(mContext,this);

    }

    public void startAutoReauthenticate (eMotoLoginResponse mLoginResponse) {

        try {

            this.mLoginResponse = mLoginResponse;

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

    private void cmdGettoken()
    {
        if (mLoginResponse == null)
        {
            Log.d(TAG, "Null Object Reference!");
            //eMotoServiceBroadcaster.broadcastIntentWithState(RES_EXCEPTION_ENCOUNTERED, this);
            return;
        }

        if(mLoginResponse.isSuccess()) {
            Log.d(TAG, "Login Credential: " + mLoginResponse.getToken());
            //eMotoServiceBroadcaster.broadcastNewToken(mLoginResponse.getToken(), this);
        }
        else
        {
            Log.d(TAG, "token is not valid");
            //eMotoServiceBroadcaster.broadcastIntentWithState(RES_TOKEN_UNAUTHORIZED, this);
        }
    }
    //region Authentication Service

    private void stopAutoReauthenticate ()
    {
        stpe.shutdownNow();

    }

    public String getLoginToken(){
        return mLoginResponse.getToken();
    }

    private void testGetSchedule ()
    {
        String token = mLoginResponse.getToken();
        eMotoCell mCell = eMotoCell.getDeviceFromServer(token, "00000000");

        if(mCell != null)
        {
            eMotoAdsSchedule.getScheduleAds(token, mCell);
        }
        else
        {
            Log.d(TAG,"device is empty");
        }

    }

    /**
     * Pass user credential to the eMotocell
     */
    private void cmdBtSetCellAuthen(){
        //if the login session is active and cell is ready
        if(mBTService.sessionIsReady() && mLoginResponse.isSuccess() ) {

            mBTService.getSession().setDeviceAuthen(mLoginResponse.getCredential());
        }
    }

    private void startLocationService()
    {
        Log.d(TAG, "startLocationService()");
        //if(!locationServiceIsRunning) {
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d(TAG, location.toString());
                eMotoServiceBroadcaster.broadcastNewLocation(location, mContext);
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
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

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



    //region Location Service

    /**
     * Pass wifi SSID and key information to the eMotoCell
     *
     * @param intent
     */
    private void cmdBtSetWifi(Intent intent){
        if(mBTService.sessionIsReady()) {
            String ssid = intent.getStringExtra(EXTRA_WIFI_SSID);
            int secType = intent.getIntExtra(EXTRA_WIFI_SEC,0);
            String key = intent.getStringExtra(EXTRA_WIFI_KEY);
            mBTService.getSession().setDeviceWifi(ssid, secType, key);
        }
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

                eMotoServiceBroadcaster.broadcastNewToken(mLoginResponse.getToken(), mContext);
            }

            System.out.println("run:" + mLoginResponse.getToken());
        }
    }

    //endregion

    public class GetAppConfigTask extends AsyncTask<Void, Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {

            return eMotoUtility.getAppConfigFromServer();
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            Log.d(TAG,result.toString());
            Log.d("AsyncThread", "onPostExecute");
        }
    }


}
