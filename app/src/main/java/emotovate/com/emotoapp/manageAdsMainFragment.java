package emotovate.com.emotoapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import eMotoLogic.eMotoCell;
import eMotoLogic.eMotoCellArrayAdapter;

/**
 *@deprecated
 */
public class manageAdsMainFragment extends Fragment  {

    //debug
    private static String TAG = "manageAdsMainFragment";



    //ads array for ListView
    private ArrayList<eMotoCell> cellArray = new ArrayList<eMotoCell>();
    ListView listview;
    eMotoCellArrayAdapter myAdapter;

    OnEmotoCellSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnEmotoCellSelectedListener {
        public void onEmotoCellSelected(eMotoCell cell);
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";



    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static manageAdsMainFragment newInstance(int sectionNumber) {
        Log.d(TAG,"newInstance");
        manageAdsMainFragment fragment = new manageAdsMainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public manageAdsMainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         myAdapter = new eMotoCellArrayAdapter(this.getActivity(),R.layout.emotocell_item_row,cellArray);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_manage_ads, container, false);
        listview = (ListView) rootView.findViewById(R.id.cellListView);
        listview .setOnItemClickListener(mOnClickListener);
        fillInListView ();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button testButton = (Button) view.findViewById(R.id.btnStartManageAdsTest);
        Log.d(TAG,"onViewCreated()");
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Log.d(TAG,"onClick");
               new getDeviceTaskTask().execute(((manageAdsActivity) getActivity()).getLoginToken());
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((manageAdsActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnEmotoCellSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEmotoCellSelectedListener");
        }
    }

    //region ListView

    private void fillInListView (){

        listview.setAdapter(myAdapter);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) { }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            onListItemClick((ListView) parent, v, position, id);

            eMotoCell myMotoCell = cellArray.get(position);

            Toast.makeText(getActivity(), String.format("Item Clicked: ID %s", myMotoCell.deviceID),
                    Toast.LENGTH_SHORT).show();

            //HACK: set dummy position
            myMotoCell.deviceLatitude = "-33.7238297";
            myMotoCell.deviceLongitude =  "151.1220244";
            mCallback.onEmotoCellSelected(myMotoCell);
        }
    };

    private class getDeviceTaskTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected String doInBackground(String... prams) {
            try {

                HashMap<String,eMotoCell> cellHashMap =  eMotoCell.getDeviceListFromServer(prams[0]);

                //fill into List Array
                cellArray.clear();
                cellArray.addAll(cellHashMap.values());

                //Sorting result by dev ID
                Collections.sort(cellArray, new Comparator<eMotoCell>() {
                    @Override
                    public int compare(eMotoCell cell1, eMotoCell cell2) {
                        return cell1.deviceID.compareTo(cell2.deviceID);
                    }
                });

                return "put the background thread function here";
            } catch (Exception ex) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute");
            //completion handler
            fillInListView();
        }
    }

    //endregion



}