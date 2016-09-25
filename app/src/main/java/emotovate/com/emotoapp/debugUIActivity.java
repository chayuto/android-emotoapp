package emotovate.com.emotoapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import eMotoLogic.eMotoAdsSchedule;
import eMotoLogic.eMotoCell;
import eMotoLogic.eMotoLogic;
import eMotoLogic.eMotoLoginResponse;
import eMotoLogic.eMotoService;
import eMotoLogic.eMotoUtility;

public class debugUIActivity extends AppCompatActivity {


    public static final String TAG = "debugUIActivity";

    eMotoService mService;
    eMotoLogic mLogic;
    boolean mBound = false;


    Button btnGetSchedule,btnMimicScreen;
    /**
     * eMotoService
     */
    private eMotoLoginResponse mLoginResponse = new eMotoLoginResponse();
    private ServiceResponseReceiver mServiceResponseReceiver;
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            eMotoService.LocalBinder binder = (eMotoService.LocalBinder) service;
            mService = binder.getService();
            mLogic = mService.getLogic();
            mBound = true;


            Log.d(TAG,mService.getHello());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_ui);

        //check service
        //startIntent
        Intent i= new Intent(this, eMotoService.class);
        i.putExtra(eMotoService.SERVICE_CMD, eMotoService.CMD_SERVICE_START);
        this.startService(i);


        btnGetSchedule = (Button) findViewById(R.id.btnGetSch);
        btnGetSchedule.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //do something
                        new GetScheduleTask().execute((Void) null);
                    }
                }
        );


        btnMimicScreen = (Button) findViewById(R.id.btnMimicScreen);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume()");

        //register for background service
        this.registerBackgroundService();
        //request initial token
        this.requestToken();

    }

    //region Background Service

    @Override
    public void onPause() {
        Log.d(TAG,"onPause()");
        this.unregisterBackgroundService();
        super.onPause();
    }

    private void requestToken(){
        Log.d(TAG, "requestToken()");
        // use this to start and trigger a service
        Intent i= new Intent(this, eMotoService.class);
        i.putExtra(eMotoService.SERVICE_CMD, eMotoService.CMD_GETTOKEN);
        this.startService(i);
    }

    private void registerBackgroundService(){

        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(
                eMotoLogic.BROADCAST_ACTION);

        // Sets the filter's category to DEFAULT
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Instantiates a new DownloadStateReceiver
        mServiceResponseReceiver = new ServiceResponseReceiver();

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mServiceResponseReceiver,
                statusIntentFilter);
    }

    private void unregisterBackgroundService(){
        // If the DownloadStateReceiver still exists, unregister it and set it to null
        if (mServiceResponseReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceResponseReceiver);
            mServiceResponseReceiver = null;
        }
        // Unregisters the FragmentDisplayer instance
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mServiceResponseReceiver);

    }


    //endregion

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, eMotoService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }


    //region Child class Interface

    private class ServiceResponseReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private ServiceResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "BroadCastReceived: " + intent.getStringExtra(eMotoLogic.BROADCAST_STATUS));

            switch(intent.getStringExtra(eMotoLogic.BROADCAST_STATUS)){
                case eMotoService.RES_TOKEN_UPDATE:
                    String token = intent.getStringExtra(eMotoService.RES_TOKEN_UPDATE);
                    if (token != null){
                        mLoginResponse.setToken(token);
                        Log.d(TAG,"New Token: " + mLoginResponse.getToken());
                        onTokenUpdate(token);
                    }

                    break;

            }
        }

        public void onTokenUpdate(String Token){

        }

    }

    public class GetScheduleTask extends AsyncTask<Void, Void,String> {

        @Override
        protected String doInBackground(Void... params) {

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

            return "done";
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d(TAG,result.toString());
            Log.d("AsyncThread", "onPostExecute");
        }
    }

}

