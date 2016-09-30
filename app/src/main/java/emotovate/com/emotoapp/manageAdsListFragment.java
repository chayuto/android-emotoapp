package emotovate.com.emotoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import eMotoLogic.eMotoAdsApprovalItem;
import eMotoLogic.eMotoAdsArrayAdapter;
import eMotoLogic.eMotoAdsApproval;

/**
 * to handle interaction events.
 * Use the {@link manageAdsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class manageAdsListFragment extends Fragment {

    private static final String ARG_PARAM1 = "eMotoCell";
    //debug
    private static String TAG = "manageAdsListFragment";
    ListView listview;
    eMotoAdsArrayAdapter myAdapter;
    boolean viewApprovedAds = false;

    private ArrayList<eMotoAdsApprovalItem> adsArray = new ArrayList<eMotoAdsApprovalItem>();
    private eMotoAdsApproval myAdsApproval = new eMotoAdsApproval();
    private OnAdsListSelectListener mListener;
    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            onListItemClick((ListView) parent, v, position, id);

            eMotoAdsApprovalItem ads =  adsArray.get(position);
            Toast.makeText(getActivity(), String.format("Item Clicked %s", ads.description()),
                    Toast.LENGTH_SHORT).show();

            mListener.onAdsListSelect(ads);
        }
    };

    public manageAdsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *

     * @return A new instance of fragment manageAdsApproveListFragment.
     */
    public static manageAdsListFragment newInstance() {
        Log.d(TAG,"newInstance()");
        manageAdsListFragment fragment = new manageAdsListFragment();
        //Bundle args = new Bundle();
        //args.putParcelable(ARG_PARAM1,myCell);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

        setHasOptionsMenu(true);
        if (viewApprovedAds){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Pending Unapprove");
        }
        else {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Pending Approve");
        }


        //if (getArguments() != null) {
        //    mCell = getArguments().getParcelable(ARG_PARAM1);
        //
        //}

        //setup ads array
        myAdapter = new eMotoAdsArrayAdapter(getActivity(),R.layout.adsview_item_row,adsArray);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_ads_list_frag, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            /*
            case R.id.activity_menu_item:
                // Not implemented here
                return false; */

            case R.id.switchApprovalViewItem:

                // Do Fragment menu item stuff here
                Log.d(TAG,"R.id.viewUnapprovedAdsItem pressed");
                viewApprovedAds = !viewApprovedAds;
                new getAdsCollectionTask().execute();
                if (viewApprovedAds){
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Pending Unapprove");
                }
                else {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Pending Approve");
                }

                return true;

            default:
                break;
        }

        return false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView()");

        //view recreated every time when backed from backstack
        View view  =  inflater.inflate(R.layout.fragment_manage_ads_approve_list, container, false);
        listview = (ListView) view.findViewById(R.id.adsListView);
        listview.setOnItemClickListener(mOnClickListener);
        fillListView(); //fill list view from content in adsArray

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated( view,savedInstanceState);
        Log.d(TAG, "onViewCreated()");

        //start retrieving ads
        new getAdsCollectionTask().execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            mListener = (OnAdsListSelectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //region Task

    //region Listview
    private void fillListView(){

        listview.setAdapter(myAdapter);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) { }

    private String getLoginToken (){
        return ((manageAdsActivity) getActivity()).getLoginToken();
    }

    //endregion

    public void approveAds(eMotoAdsApprovalItem  Ads){
        Log.d(TAG, "approveAds()"+ Ads.id());
        new ApproveTask().execute(Ads.id(),this.getLoginToken());
    }

    public void unapproveAds(eMotoAdsApprovalItem  Ads){
        Log.d(TAG, "unapproveAds()" + Ads.id());
        new UnapproveTask().execute(Ads.id(),this.getLoginToken());
    }

    public interface OnAdsListSelectListener {
        void onAdsListSelect(eMotoAdsApprovalItem Ads);
    }

    //endregion

    //region logic

    private class getAdsCollectionTask extends AsyncTask<Object, Void, String> {

        ProgressDialog progress = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute()
        {
            //show progress dialogure
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();
        }
        @Override
        protected String doInBackground(Object... prams) {

            String token = ((manageAdsActivity) getActivity()).getLoginToken();
            Log.d(TAG,"getAdsCollectionTask():"+token);

            //get get approved list from server

            if (viewApprovedAds) {
                myAdsApproval.adsHashMap= eMotoAdsApproval.getAdsApproved(token);
            }
            else {
                myAdsApproval.adsHashMap= eMotoAdsApproval.getAdsUnapproved(token);
            }
            return "test";

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("AyncThread", "onPostExecute");
            //completion handler
            adsArray.clear();
            adsArray.addAll(myAdsApproval.adsHashMap.values());


            int index =listview.getFirstVisiblePosition();
            View v = listview.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - listview.getPaddingTop());

            fillListView();

            listview.setSelectionFromTop(index, top);
            progress.dismiss();
        }
    }

    private class ApproveTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected String doInBackground(String... prams) {
            try {
                myAdsApproval.approveAdsWithID(prams[0],prams[1]);
                return "put the background thread function here";
            } catch (Exception ex) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("AyncThread", "onPostExecute");
            new getAdsCollectionTask().execute();
            //completion handler
        }
    }

    private class UnapproveTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected String doInBackground(String... prams) {
            try {
                myAdsApproval.unapproveAdsWithID(prams[0], prams[1]);
                return "put the background thread function here";
            } catch (Exception ex) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("AyncThread", "onPostExecute");
            new getAdsCollectionTask().execute();
            //completion handler
        }
    }

}
