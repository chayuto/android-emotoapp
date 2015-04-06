package emotovate.com.emotoapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import eMotoLogic.eMotoLoginResponse;
import eMotoLogic.eMotoService;


public class baseActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    //Debug
    public String TAG = "baseActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * eMotoService
     */
    private eMotoLoginResponse mLoginResponse = new eMotoLoginResponse();
    private ServiceResponseReceiver mServiceResponseReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.onOverrideTest();
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


    //region Child class Interface

    public void setupNavFragment (int position){
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        //Log.d(TAG,"Title : " + mTitle);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),position);
    }


    public String getLoginToken()
    {
        return mLoginResponse.getToken();
    }

    public void onOverrideTest(){

    }

    public void onTokenUpdate(String Token){

    }

    public void onNarvigationFirstItemSelected(){

    }
    public void onNarvigationSecondItemSelected(){

    }
    //endregion

    //region Nav Drawer
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d(TAG, String.format("NavDraw %d", position));
        // depending on the position in your drawer list change this
        switch (position) {
            case 0: {
                this.onNarvigationFirstItemSelected();
                break;
            }
            case 1: {
                this.onNarvigationSecondItemSelected();
                break;
            }
            case 2: {

                break;
            }
            default:
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.manage_ads, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    //endregion

    //region Background Service

    private void requestToken(){
        Log.d("Activity", "requestToken()");
        // use this to start and trigger a service
        Intent i= new Intent(this, eMotoService.class);
        i.putExtra("ServiceCMD", eMotoService.CMD_GETTOKEN);
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
                case eMotoService.RES_LOCATION_UPDATE:
                    Location location = intent.getParcelableExtra(eMotoService.RES_LOCATION_UPDATE);
                    Toast.makeText(getApplicationContext(), String.format("Location %f %f %f", location.getLatitude(), location.getLongitude(), location.getAccuracy()),
                            Toast.LENGTH_SHORT).show();
                    break;
                case eMotoService.RES_LOCATION_ERROR:
                case eMotoService.RES_TOKEN_UNAUTHORIZED:
                case eMotoService.RES_EXCEPTION_ENCOUNTERED:
                    break;
                case eMotoService.RES_BT_DATA_RECEIVED:
                    break;
                case eMotoService.RES_BT_ERROR:
                    String errorMsg = intent.getStringExtra(eMotoService.RES_BT_ERROR);
                    Toast.makeText(getApplicationContext(),errorMsg,
                            Toast.LENGTH_SHORT).show();
                    break;
                case eMotoService.RES_BT_STATUS:
                    String statusMsg = intent.getStringExtra(eMotoService.RES_BT_STATUS);
                    Toast.makeText(getApplicationContext(),statusMsg,
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }


    //endregion



}
