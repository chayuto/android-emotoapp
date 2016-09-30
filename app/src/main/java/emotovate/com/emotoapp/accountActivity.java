package emotovate.com.emotoapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import eMotoLogic.eMotoLogic;
import eMotoLogic.eMotoService;

public class accountActivity extends screenBaseActivity{

    //Debug
    public static String TAG = "accountActivity";


    accountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        FragmentManager fragmentManager = getSupportFragmentManager();
        accountFragment =  accountFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.container, accountFragment)
                .commit();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");

        //set fragment
        super.setupNavFragment(2);

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent()");
    }

    @Override
    public void onOverrideTest() {
        //super.onOverrideTest();
        Log.d(TAG, "onOverrideTest successful");
    }

    @Override
    public void onNavigationFirstItemSelected() {
        super.onNavigationFirstItemSelected();
        //start first item in nav drawer
        Intent intent = new Intent(accountActivity.this, manageAdsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onNavigationSecondItemSelected(){
        super.onNavigationSecondItemSelected();
        //start second item in nav drawer
        Intent intent = new Intent(accountActivity.this,manageDeviceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    /*
    if (!mNavigationDrawerFragment.isDrawerOpen()) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.manage_ads, menu);
        restoreActionBar();
        return true;
    }
    */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
