package emotovate.com.emotoapp;

import android.app.Fragment;
import android.content.Intent;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import eMotoLogic.eMotoAds;
import eMotoLogic.eMotoCell;


public class manageAdsActivity extends screenBaseActivity
        implements manageAdsMainFragment.OnEmotoCellSelectedListener,
        manageAdsListFragment.OnAdsListSelectListener,
        manageAdsDetailsFragment.OnAdsApproveSelectListener{

    //Debug
    private static String TAG = "manageAdsActivity";

    private static String FragAdsListTag = "FragAdsList";
    private static String FragAdsDetailsTag = "FragAdsDetails";
    private static String FragStackTag = "FragStack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_ads);

        //setup simple fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, manageAdsMainFragment.newInstance(1)).addToBackStack(FragStackTag)
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
    public void onBackPressed()
    {
        Log.d(TAG,"onBackPressed()");
        final Fragment fragment = getFragmentManager().findFragmentByTag(FragAdsDetailsTag);
        if(fragment!=null)
        {
            if (fragment.isVisible()) {
                Log.d(TAG,"FragAdsDetails Visible");
                getFragmentManager().popBackStack(FragAdsDetailsTag,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return;
            }
            else {
                Log.d(TAG,"FragAdsDetails Not Visible");
            }
        }
        final Fragment fragment2 = getFragmentManager().findFragmentByTag(FragAdsListTag);
        if(fragment2!=null)
        {
            if (fragment2.isVisible()) {
                Log.d(TAG,"FragAdsList Visible");
                getFragmentManager().popBackStack(FragAdsListTag,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return;
            }
            else {
                Log.d(TAG,"FragAdsList Not Visible");
            }
        }

        super.onBackPressed(); //exit app
    }

    @Override
    public String getLoginToken()
    {
        return super.getLoginToken();
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

    //region OptionMenu
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


    //region Logic

    public void onEmotoCellSelected(eMotoCell cell){
        Log.d(TAG,"onEmotoCellSelected()");

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, manageAdsListFragment.newInstance(cell), FragAdsListTag ).addToBackStack(FragAdsListTag);

        // Commit the transaction
        transaction.commit();
    }

    public void onAdsListSelect(eMotoAds Ads){
        Log.d(TAG,"Ads Selected: " + Ads.description());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, manageAdsDetailsFragment.newInstance(Ads), FragAdsDetailsTag ).addToBackStack( FragAdsDetailsTag);

        // Commit the transaction
        transaction.commit();
    }

    public void onAdsApproveSelect(eMotoAds Ads){

        //popbackstack
        getFragmentManager().popBackStack(FragAdsDetailsTag,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //Pass Ads to fragment to make Network calls and update info
        manageAdsListFragment listFrag = (manageAdsListFragment) getFragmentManager().findFragmentByTag(FragAdsListTag);
        listFrag.approveAds(Ads);

    }
    public void onAdsUnapproveSelect(eMotoAds Ads){
        //popbackstack
        getFragmentManager().popBackStack(FragAdsDetailsTag,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //Pass Ads to fragment to make Network calls and update info
        manageAdsListFragment listFrag = (manageAdsListFragment) getFragmentManager().findFragmentByTag(FragAdsListTag);
        listFrag.unapproveAds(Ads);

    }

    //endregion

}
