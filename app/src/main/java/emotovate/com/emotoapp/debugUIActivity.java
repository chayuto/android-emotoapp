package emotovate.com.emotoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import eMotoLogic.eMotoAdsSchedule;
import eMotoLogic.eMotoCell;
import eMotoLogic.eMotoLoginResponse;
import eMotoLogic.eMotoService;
import eMotoLogic.eMotoUtility;

public class debugUIActivity extends AppCompatActivity {


    public static final String TAG = "debugUIActivity";


    Button btnGetSchedule,btnMimicScreen;
    /**
     * eMotoService
     */
    private eMotoLoginResponse mLoginResponse = new eMotoLoginResponse();
    private ServiceResponseReceiver mServiceResponseReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_ui);


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

    @Override
    public void onPause() {
        Log.d(TAG,"onPause()");
        this.unregisterBackgroundService();
        super.onPause();
    }

    //region Background Service

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
                eMotoService.BROADCAST_ACTION);

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



    private class ServiceResponseReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private ServiceResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "BroadCastReceived: " + intent.getStringExtra(eMotoService.BROADCAST_STATUS));

            switch(intent.getStringExtra(eMotoService.BROADCAST_STATUS)){
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


    //endregion


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
