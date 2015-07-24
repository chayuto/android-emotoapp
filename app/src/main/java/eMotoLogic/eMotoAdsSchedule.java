package eMotoLogic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by chayut on 13/07/15.
 */
public class eMotoAdsSchedule {

    private static final String TAG = "eMotoAdsSchedule";


    //region network connection
    static public List<eMotoAdsScheduleEntry> getScheduleAds (String token,eMotoCell eMotoCell) {

        Log.d(TAG, "getScheduleAds()");

        List<eMotoAdsScheduleEntry> mySchedule = new ArrayList<>();

        BufferedReader rd  = null;



        try {
            URL u;
            if (eMotoCell.isFixed())
            {
                u = new URL(String.format("https://emotovate.com/api/ads/getbydevice/%s?deviceId=%s",token, eMotoCell.deviceID)); //

            }
            else {
                u = new URL(String.format("https://emotovate.com/api/ads/all/%s?deviceId=%s&lat=%s&lng=%s",token, eMotoCell.deviceID, eMotoCell.deviceLatitude, eMotoCell.deviceLongitude)); //
            }

            HttpsURLConnection c = (HttpsURLConnection) u.openConnection();
            Log.v(TAG, "GET:" + u);
            c.setRequestMethod("GET");

            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type","application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            c.connect();
            int status = c.getResponseCode();

            Log.v(TAG, String.format("http-response:%3d", status));
            switch (status) {

                case 200:
                case 201:
                    rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));


                    String json = rd.readLine();

                    //Log.d(TAG, "Response: " + json);

                    JSONArray jArray  = new JSONArray(json);
                    for(int n = 0; n < jArray.length(); n++) {


                        HashMap<String,eMotoAds> hashMap =  new HashMap<String,eMotoAds>();

                        JSONObject myJSONObject = jArray.getJSONObject(n);

                        String entryFrom = myJSONObject.getString("From");
                        String entryTo = myJSONObject.getString("To");


                        Log.d(TAG, "From: " + entryFrom );
                        Log.d(TAG, "TO: " + entryTo);

                        JSONArray adsJSONArray =  myJSONObject.getJSONArray("Ads");

                        List<eMotoAds> adsList = new ArrayList<>();

                        for(int m = 0; m < adsJSONArray.length(); m++) {


                            JSONObject adsJSONObject = adsJSONArray.getJSONObject(m);
                            eMotoAds myAds = new eMotoAds(adsJSONObject);
                            adsList.add(myAds);

                            Log.d(TAG, "Ads ID" + adsJSONObject.getString("Id") );

                        }
                        eMotoAdsScheduleEntry myEntry = new eMotoAdsScheduleEntry(entryFrom,entryTo,adsList);

                        mySchedule.add(myEntry);
                    }



                    break;
                case 400:
                case 401:
                case 404:
                case 500:
                    rd  = new BufferedReader(new InputStreamReader(c.getErrorStream()));

                    Log.d(TAG,"ERROR " +rd.readLine());
                    break;
                default:

                    break;

            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        catch (JSONException ex){
            ex.printStackTrace();
        }

        //TODO: remove this later!
        return mySchedule;
    }


}
