package emotovate.com.emotoapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import eMotoLogic.eMotoAds;
import eMotoLogic.eMotoAdsArrayAdapter;
import eMotoLogic.eMotoCellArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link manageDeviceMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link manageDeviceMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class manageDeviceMainFragment extends Fragment implements View.OnClickListener {

    //debug
    private static String TAG = "manageDeviceMainFragment";

    private ArrayList<String> pairedArrayList = new ArrayList<String>();
    private OnFragmentInteractionListener mListener;

    private ListView pairedListView;
    ArrayAdapter myAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment manageDeviceMainFragment.
     */
    public static manageDeviceMainFragment newInstance() {
        manageDeviceMainFragment fragment = new manageDeviceMainFragment();
        Bundle args = new Bundle();
       // fragment.setArguments(args);
        return fragment;
    }

    public manageDeviceMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/

        //setup ads array
        myAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,pairedArrayList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_device_main, container, false);
        Button btnConnect = (Button) view.findViewById(R.id.btnConnect);
        Button btnTest1 = (Button) view.findViewById(R.id.btnTest1);
        Button btnTest2 = (Button) view.findViewById(R.id.btnTest2);
        pairedListView = (ListView) view.findViewById(R.id.pairedlistView);
        pairedListView.setOnItemClickListener(mOnClickListener);
        fillListView();
        
        btnConnect.setOnClickListener(this);
        btnTest1.setOnClickListener(this);
        btnTest2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick()");
        //do what you want to do when button is clicked
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.btnConnect:
                    mListener.onClickBtnConnect();
                    break;
                case R.id.btnTest1:
                    mListener.onClickBtnTest1();
                    break;
                case R.id.btnTest2:
                    mListener.onClickBtnTest2();
                    break;
            }
        }
    }

    public void deviceListUpdate(ArrayList<String> list){
        pairedArrayList = list;
        if(pairedListView != null){
            fillListView();
        }


    }



    /**
     * This interface must be implemented by activities
     */
    public interface OnFragmentInteractionListener {
        public void onClickBtnConnect();
        public void onClickBtnTest1();
        public void onClickBtnTest2();
        public void requestConnect(String eMotoCell);
    }

    //region Listview
    private void fillListView(){
        pairedListView.setAdapter(myAdapter);
    }


    protected void onListItemClick(ListView l, View v, int position, long id) { }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            onListItemClick((ListView) parent, v, position, id);
            Log.d(TAG," onListItemClick()" + pairedArrayList.get(position));
            mListener.requestConnect(pairedArrayList.get(position));
        }
    };

    //endregion

}
