package emotovate.com.emotoapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import eMotoLogic.eMotoLogic;
import eMotoLogic.eMotoLoginResponse;
import eMotoLogic.eMotoService;


public class screenBaseActivity extends AppCompatActivity
        implements screenNavigationDrawerFragment.NavigationDrawerCallbacks {


    //Debug
    public static final String TAG = "screenBaseActivity";
    eMotoService mService;
    eMotoLogic mLogic;
    boolean mBound = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private screenNavigationDrawerFragment mScreenNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

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

        this.onOverrideTest();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //startIntent
        Intent i= new Intent(this, eMotoService.class);
        i.putExtra(eMotoService.SERVICE_CMD, eMotoService.CMD_SERVICE_START);
        this.startService(i);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume()");

        //register for background service
        this.registerBackgroundService();




    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause()");
        this.unregisterBackgroundService();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, eMotoService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }


    //region Child class Interface

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

    public void setupNavFragment (int position){
        Log.d(TAG, "setupNavFragment()");
        mScreenNavigationDrawerFragment = (screenNavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        //Log.d(TAG,"Title : " + mTitle);
        // Set up the drawer.
        mScreenNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), position);
    }

    public String getLoginToken()
    {
        while(!mBound){
            //wait till bound to service
        }
        return mLogic.getLoginToken();
    }

    public void onOverrideTest(){

    }

    public void onTokenUpdate(String Token){

    }

    public void onBTPairedList(ArrayList<String> list)
    {

    }

    public void onBTConnected(){

    }

    public void onBTDisconnected(){

    }

    public void onNavigationFirstItemSelected(){

    }

    public void onNavigationSecondItemSelected(){

    }

    public void onNavigationForthItemSelected(){
        Intent intent = new Intent(screenBaseActivity.this, advertiserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    //endregion

    public void onNavigationFifthItemSelected(){
        Intent intent = new Intent(screenBaseActivity.this, debugUIActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    //region Nav Drawer
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d(TAG, String.format("NavDraw %d", position));
        // depending on the position in your drawer list change this
        switch (position) {
            case 0: {
                this.onNavigationFirstItemSelected();
                break;
            }
            case 1: {
                this.onNavigationSecondItemSelected();
                break;
            }
            case 2: {

                break;
            }
            case 3:{
                this.onNavigationForthItemSelected();
                break;
            }

            case 4: {
                this.onNavigationFifthItemSelected();
                break;
            }
            default:
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.screen_nav_draw_section1);
                break;
            case 2:
                mTitle = getString(R.string.screen_nav_draw_section2);
                break;
            case 3:
                mTitle = getString(R.string.screen_nav_draw_section3);
                break;
            case 4:
                mTitle = getString(R.string.screen_nav_draw_section4);
                break;
            case 5:
                mTitle = getString(R.string.screen_nav_draw_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    //endregion

    //region Background Service

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mScreenNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.manage_ads, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
                case eMotoService.RES_LOCATION_UPDATE:
                    Location location = intent.getParcelableExtra(eMotoService.RES_LOCATION_UPDATE);
                    Toast.makeText(getApplicationContext(), String.format("Location %f %f %f", location.getLatitude(), location.getLongitude(), location.getAccuracy()),
                            Toast.LENGTH_SHORT).show();
                    break;
                case eMotoService.RES_LOCATION_ERROR:
                case eMotoService.RES_TOKEN_UNAUTHORIZED:
                case eMotoService.RES_EXCEPTION_ENCOUNTERED:
                    break;
                case eMotoService.RES_BT_CONNECTED:
                    onBTConnected();
                    break;
                case eMotoService.RES_BT_DISCONNECTED:
                    onBTDisconnected();
                    break;
                case eMotoService.RES_BT_STATUS:
                    String statusMsg = intent.getStringExtra(eMotoService.RES_BT_STATUS);
                    Log.d(TAG,statusMsg);
                    Toast.makeText(getApplicationContext(),statusMsg,
                            Toast.LENGTH_SHORT).show();
                    break;
                case eMotoService.RES_BT_ERROR:

                    String errorMsg = intent.getStringExtra(eMotoService.RES_BT_ERROR);
                    Log.d(TAG,errorMsg);
                    Toast.makeText(getApplicationContext(),errorMsg,
                            Toast.LENGTH_SHORT).show();
                    break;
                case eMotoService.RES_BT_PAIRED_LIST:
                    ArrayList<String> devList = intent.getStringArrayListExtra(eMotoService.RES_BT_PAIRED_LIST);
                    onBTPairedList(devList);
                    break;
                case eMotoService.RES_BT_DATA_RECEIVED:
                    break;


                default:
                    break;
            }
        }
    }

}
