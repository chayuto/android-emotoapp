package emotovate.com.emotoapp;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import android.Manifest;

import java.util.ArrayList;
import java.util.List;

import eMotoLogic.ssidArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link manageDeviceSetupWifiFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link manageDeviceSetupWifiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class manageDeviceSetupWifiFragment extends Fragment implements View.OnClickListener {

    static public int WIRELESS_NO_SEC = 0;
    static public int WIRELESS_WEP = 1;
    static public int WIRELESS_WPA = 2;
    static public int WIRELESS_WPA2 = 3;
    private static String TAG = "SetupWifiFrag";
    Button btnGetWifiInfo;
    ListView listViewSSIDS;
    int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1000;
    WifiManager wifi;
    WifiScanReceiver wifiReciever;
    ssidArrayAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private ArrayList<ScanResult> ssidArray = new ArrayList<>();
    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            Log.d("Ok click","test");

            onListItemClick((ListView) parent, v, position, id);

            ScanResult network = ssidArray.get(position);

            popupDialogue(network);

        }
    };

    public manageDeviceSetupWifiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment manageDeviceSetupWifiFragment.
     */
    public static manageDeviceSetupWifiFragment newInstance() {
        manageDeviceSetupWifiFragment fragment = new manageDeviceSetupWifiFragment();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    static int getSecurity(ScanResult result) {

        int secType = -1 ;
        if (result.capabilities.contains("WEP")) {
            Log.d (TAG,"Security info: SECURITY_WEP");
            secType = WIRELESS_WEP;

        }
        else if (result.capabilities.contains("WPA2")) {
            Log.d (TAG,"Security info: SECURITY_WPA2");
            secType = WIRELESS_WPA2;

        } else if (result.capabilities.contains("WPA")) {
            Log.d (TAG,"Security info: SECURITY_WPA");
            secType = WIRELESS_WPA;
        }
        else{
            Log.d (TAG,"Security info: SECURITY_NONE");
            secType = WIRELESS_NO_SEC;
        }

        return secType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_device_setup_wifi, container, false);


        btnGetWifiInfo = (Button) view.findViewById(R.id.btnGetWifi);
        btnGetWifiInfo.setOnClickListener(this);

        listViewSSIDS = (ListView) view.findViewById(R.id.listViewWifiSSIDs);
        listViewSSIDS.setOnItemClickListener(mOnClickListener);

        adapter = new ssidArrayAdapter(getActivity(),R.layout.wifi_ssid_cell,ssidArray);

        wifiReciever = new WifiScanReceiver();

        wifi= (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);


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
    public void onResume() {
        Log.e(TAG, "onResume of fragment");
        getActivity().registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "OnPause of fragment");
        getActivity().unregisterReceiver(wifiReciever);
        super.onPause();
    }

    private void getWifiInfo (){

        WifiManager wifiManager = (WifiManager) getActivity() .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();


        if (!wifiManager.isWifiEnabled()){
            //wifi is not enabled

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

            // Setting Dialog Title
            alertDialog.setTitle("Wifi is disabled.");

            // Setting Dialog Message
            alertDialog.setMessage("Do you Turn on Wifi?");

            // Setting Icon to Dialog
            // alertDialog.setIcon(R.drawable.ic_launcher);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // Activity transfer to wifi settings
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("no",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event

                            dialog.cancel();
                        }
                    });

            // Showing Alert Message
            alertDialog.show();

            return;
        }

        Log.d(TAG, wifiInfo.toString());
        if (wifiInfo.getSSID() != null) {
            Log.d(TAG, "SSID:" + wifiInfo.getSSID());
        }
        else
        {
            Log.d(TAG, "not associate with any wifi");
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        }else{

            //do something, permission was previously granted; or legacy device
            wifi.startScan();
            Toast.makeText(getActivity().getApplicationContext(), "Scan for nearby SSID", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Scan for nearby SSID");
        }





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission

            //scan for sequcity

        }
    }

    //region Listview
    private void fillListView(){
        listViewSSIDS.setAdapter(adapter);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) { }

    public void popupDialogue (final ScanResult network)
    {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        //TODO: Check if the network has security

        if (true){
            //inflate layout to prompt for user key input

            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.wifi_key_dialogue, null));


            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Setup eMotoCell with this wifi?")
                    .setTitle("Network: " + network.SSID);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    Dialog f = (Dialog) dialog;
                    EditText wifiKeyEditText = (EditText) f.findViewById(R.id.editTextssidKey);

                    onFinishWifiSetup(network,wifiKeyEditText.getText().toString());
                }
            })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        else {
            //IF the network has no security

            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.wifi_key_dialogue_no_security, null));


            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Setup eMotoCell with this wifi?")
                    .setTitle("Network: " + network.SSID);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    onFinishWifiSetup(network,""); //pass empty string for no security key
                }
            })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();


        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }

        getWifiInfo();
    }

    //endregion

    public void onFinishWifiSetup(ScanResult network,String key)
    {
        mListener.onFragmentWifiSetup(network.SSID,getSecurity(network),key);
        getFragmentManager().popBackStack();
    }

    /**
     * provide interface to manageDeviceActivity
     */
    public interface OnFragmentInteractionListener {
        void onFragmentWifiSetup(String SSID,int SecType, String key);
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> networkList = wifi.getScanResults();
            ssidArray.clear();

            if (networkList != null) {

                Log.d(TAG, "listing wifi ssids...");
                for (ScanResult network : networkList) {
                    String Capabilities = network.capabilities;
                    Log.d(TAG, network.SSID + " capabilities : " + Capabilities);
                    getSecurity(network);
                    ssidArray.add(network);
                }
            }
            Toast.makeText(getActivity().getApplicationContext(), "Wifi Scan completed", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Wifi Scan completed");

            fillListView();
        }
    }



}
