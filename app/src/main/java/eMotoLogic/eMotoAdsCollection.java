package eMotoLogic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by chayut on 6/01/15.
 */
public class eMotoAdsCollection {

    private static final String TAG = "eMotoAdsCollection";

    public HashMap<String,eMotoAds> adsHashMap =  new HashMap<String,eMotoAds>();


    //region ads management
    public eMotoAds getAdsWithId (String id)
    {
        return  adsHashMap.get(id);
    }

    public boolean removeAdsWithId (String id){

        if(adsHashMap.remove(id) == null)
        {
            return false;
        }
        else{
            return true;
        }
    }
    //endregion


    //region network connection
    static public HashMap<String,eMotoAds> getAdsCollection (String token,eMotoCell eMotoCell) {

        Log.d(TAG,"getAdsCollection()");
        HashMap<String,eMotoAds> hashMap =  new HashMap<String,eMotoAds>();
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

            c.setRequestMethod("GET");

            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type","application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            c.connect();
            int status = c.getResponseCode();

            Log.d(TAG, String.format("http-response:%3d", status));
            switch (status) {

                case 200:
                case 201:
                    rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));

                    hashMap.clear();//clear all old entry in hashmap

                    String json = rd.readLine();
                    JSONArray jArray  = new JSONArray(json);
                    for(int n = 0; n < jArray.length(); n++) {
                        eMotoAds myAds = new eMotoAds(jArray.getJSONObject(n));
                        hashMap.put(myAds.id(), myAds);
                    }

                    for(HashMap.Entry<String, eMotoAds> entry: hashMap.entrySet())  {

                        Log.d(TAG, "Ads: " + entry.getValue().id());
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
        return hashMap;
    }

    public boolean approveAdsWithID(String adsID,String token){

        BufferedReader rd  = null;

        if(this.getAdsWithId(adsID) != null)
        {
            eMotoAds ads = adsHashMap.get(adsID);
            try {
                URL u = new URL(String.format("https://emotovate.com/api/ads/approve/%s?scheduleAssetId=%s&userIP=%s",token,ads.scheduleAssetId(),"192.168.1.1"));
                HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

                c.setRequestMethod("POST");

                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("Content-Type","application/json");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(5000);
                c.setReadTimeout(5000);
                c.connect();
                int status = c.getResponseCode();

                Log.d("Application:", String.format("http-response:%3d", status));
                switch (status) {

                    case 200:
                    case 201:
                        rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));

                        String json = rd.readLine();
                        Log.d("Application:",json);
                        break;
                    case 401:
                        Log.d("Application:","Server unauthorized");
                        break;
                    default:


                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();

            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public boolean unapproveAdsWithID(String adsID,String token){

        BufferedReader rd  = null;

        if(this.getAdsWithId(adsID) != null)
        {
            eMotoAds ads = adsHashMap.get(adsID);
            try {
                URL u = new URL(String.format("https://emotovate.com/api/ads/unapprove/%s?scheduleAssetId=%s&userIP=%s",token,ads.scheduleAssetId(),"192.168.1.1"));
                HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

                c.setRequestMethod("POST");

                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("Content-Type","application/json");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(5000);
                c.setReadTimeout(5000);
                c.connect();
                int status = c.getResponseCode();

                Log.d("Application:", String.format("http-response:%3d", status));
                switch (status) {

                    case 200:
                    case 201:
                        rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));

                        String json = rd.readLine();
                        Log.d("Application:","Ads" + json);
                        break;
                    case 401:
                        Log.d("Application:","Server unauthorized");
                        break;
                    default:


                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();

            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
    //endregion



}
