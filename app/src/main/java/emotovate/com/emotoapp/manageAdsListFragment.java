package emotovate.com.emotoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import eMotoLogic.eMotoAds;
import eMotoLogic.eMotoAdsArrayAdapter;
import eMotoLogic.eMotoAdsCollection;
import eMotoLogic.eMotoCell;

/**
 *
 *
 * to handle interaction events.
 * Use the {@link manageAdsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class manageAdsListFragment extends Fragment {

    //debug
    private static String TAG = "manageAdsListFragment";

    private static final String ARG_PARAM1 = "eMotoCell";

    private eMotoCell mCell;
    private ArrayList<eMotoAds> adsArray = new ArrayList<eMotoAds>();
    private eMotoAdsCollection myAdsCollection = new eMotoAdsCollection();
    ListView listview;
    eMotoAdsArrayAdapter myAdapter;

    private OnAdsListSelectListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *

     * @return A new instance of fragment manageAdsApproveListFragment.
     */
    public static manageAdsListFragment newInstance(eMotoCell myCell) {
        manageAdsListFragment fragment = new manageAdsListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1,myCell);
        fragment.setArguments(args);
        return fragment;
    }

    public manageAdsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCell = getArguments().getParcelable(ARG_PARAM1);

        }
        //setup ads array
        myAdapter = new eMotoAdsArrayAdapter(getActivity(),R.layout.adsview_item_row,adsArray);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

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


    public interface OnAdsListSelectListener {
        public void onAdsListSelect(eMotoAds Ads);
    }

    //region Task

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


            //while( ((manageAdsActivity) getActivity()).getLoginToken() == null){} // loop while waiting for token

            String token = ((manageAdsActivity) getActivity()).getLoginToken();
            Log.d(TAG,"getAdsCollectionTask()"+token);
            mCell.putDeviceOnServer(token);

            myAdsCollection.adsHashMap= eMotoAdsCollection.getAdsCollection(token,  mCell);

            return "test";

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("AyncThread", "onPostExecute");
            //completion handler
            adsArray.clear();
            adsArray.addAll(myAdsCollection.adsHashMap.values());


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
                myAdsCollection.approveAdsWithID(prams[0],prams[1]);
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
                myAdsCollection.unapproveAdsWithID(prams[0],prams[1]);
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

    //endregion

    //region Listview
    private void fillListView(){

        listview.setAdapter(myAdapter);
    }


    protected void onListItemClick(ListView l, View v, int position, long id) { }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            onListItemClick((ListView) parent, v, position, id);

            eMotoAds ads =  adsArray.get(position);
            Toast.makeText(getActivity(), String.format("Item Clicked %s", ads.description()),
                    Toast.LENGTH_SHORT).show();

            mListener.onAdsListSelect(ads);
        }
    };

    //endregion

    //region logic

    private String getLoginToken (){
        return ((manageAdsActivity) getActivity()).getLoginToken();
    }
    public void approveAds(eMotoAds Ads){
        new ApproveTask().execute(Ads.id(),this.getLoginToken());
    }

    public void unapproveAds(eMotoAds Ads){
        new UnapproveTask().execute(Ads.id(),this.getLoginToken());
    }

}
