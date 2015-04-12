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


public class manageAdsActivity extends baseActivity {

    //TODO: change to extends baseAvtivity

    //Debug
    private static String TAG = "manageAdsActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ads);

        //setup simple fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, manageAdsMainFragment.newInstance(1))
                .commit();
    }


    @Override
    public void onNewIntent (Intent intent){
        super.onNewIntent(intent);
        Log.d(TAG,"onNewIntent()");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume()");

        //set fragment
        super.setupNavFragment(0); //position 0

    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause()");

        super.onPause();
    }


    @Override
    public void onNavigationSecondItemSelected(){
        super.onNavigationSecondItemSelected();
        //start second item in nav drawer
        Intent intent = new Intent(manageAdsActivity.this,manageDeviceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    //region Nav Drawer
    /*
    @Override
    public void onNavigationDrawerItemSelected(int position) {


        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

        Log.d(TAG,String.format("NavDraw %d",position));
        // depending on the position in your drawer list change this
        switch (position) {


            case 0: {
               /* Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);

                break;
            }
            case 1: {

                //start second item in nav drawer
                Intent intent = new Intent(manageAdsActivity.this,manageDeviceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            }
            case 3: {
            /*
                // if this position is to add fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container,
                                PlaceholderFragment.newInstance(position + 1)).commit();

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
   */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* if (!mNavigationDrawerFragment.isDrawerOpen()) {
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

    //endregion


    public String getLoginToken()
    {
        return super.getLoginToken();
    }

}
