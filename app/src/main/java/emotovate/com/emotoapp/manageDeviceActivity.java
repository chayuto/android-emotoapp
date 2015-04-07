package emotovate.com.emotoapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class manageDeviceActivity extends baseActivity
        implements manageDeviceMainFragment.OnFragmentInteractionListener {

    //Debug
    public static String TAG = "manageDeviceActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate()");
        //super.TAG = TAG;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);




        //setup simple fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, manageDeviceMainFragment.newInstance("test1", "test2"))
                .commit();



    }


    @Override
    public void onResume(){
        Log.d(TAG,"onResume()");

        //set fragment
        super.setupNavFragment(1);

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause()");
        super.onPause();
    }

    @Override
    public void onNewIntent (Intent intent){
        super.onNewIntent(intent);
        Log.d(TAG,"onNewIntent()");
    }

    @Override
    public void onOverrideTest(){
        //super.onOverrideTest();
        Log.d(TAG,"onOverrideTest successful");
    }


    @Override
    public void onNavigationFirstItemSelected() {
        super.onNavigationFirstItemSelected();
        //start first item in nav drawer
        Intent intent = new Intent(manageDeviceActivity.this, manageAdsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

    public void onFragmentInteraction(Uri uri){

    }


}
