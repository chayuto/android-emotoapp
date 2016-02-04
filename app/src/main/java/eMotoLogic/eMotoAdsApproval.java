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
 * Created by chayut on 23/07/15.
 */
public class eMotoAdsApproval {


    private static final String TAG = "eMotoAdsApproval";

    public HashMap<String,eMotoAdsApprovalItem> adsHashMap =  new HashMap<String,eMotoAdsApprovalItem>();

    //region network connection
    static public HashMap<String,eMotoAdsApprovalItem> getAdsUnapproved (String token) {

        Log.d(TAG, "getAdsCollection()");
        HashMap<String,eMotoAdsApprovalItem> hashMap =  new HashMap<String,eMotoAdsApprovalItem>();
        BufferedReader rd  = null;



        try {
            URL u;

            u = new URL(String.format("https://emotovate.com/api/ads/approvalstatus/%s/?status=0",token));

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

                    hashMap.clear();//clear all old entry in hashmap

                    String json = rd.readLine();

                    Log.d(TAG, "Response: " + json);

                    JSONArray jArray  = new JSONArray(json);
                    for(int n = 0; n < jArray.length(); n++) {
                        eMotoAdsApprovalItem myAds = new  eMotoAdsApprovalItem(jArray.getJSONObject(n));
                        hashMap.put(myAds.id(), myAds);
                    }

                    for(HashMap.Entry<String, eMotoAdsApprovalItem> entry: hashMap.entrySet())  {

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

    static public HashMap<String,eMotoAdsApprovalItem> getAdsApproved (String token) {

        Log.d(TAG, "getAdsCollection()");
        HashMap<String,eMotoAdsApprovalItem> hashMap =  new HashMap<String,eMotoAdsApprovalItem>();
        BufferedReader rd  = null;



        try {
            URL u;

            u = new URL(String.format("https://emotovate.com/api/ads/approvalstatus/%s/?status=1",token));

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

                    hashMap.clear();//clear all old entry in hashmap

                    String json = rd.readLine();

                    Log.d(TAG, "Response: " + json);

                    JSONArray jArray  = new JSONArray(json);
                    for(int n = 0; n < jArray.length(); n++) {
                        eMotoAdsApprovalItem myAds = new  eMotoAdsApprovalItem(jArray.getJSONObject(n));
                        hashMap.put(myAds.id(), myAds);
                    }

                    for(HashMap.Entry<String, eMotoAdsApprovalItem> entry: hashMap.entrySet())  {

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
    //endregion

    //region ads management
    public eMotoAdsApprovalItem getAdsWithId (String id)
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

    public boolean approveAdsWithID(String adsID,String token){

        BufferedReader rd  = null;

        if(this.getAdsWithId(adsID) != null)
        {
            eMotoAdsApprovalItem ads = adsHashMap.get(adsID);
            try {
                //TODO: fix asset ID
                String s = String.format("https://emotovate.com/api/ads/approve/%s?scheduleAssetId=%s&userIP=%s",token,adsID,"192.168.1.1");
                Log.d(TAG,s);
                URL u = new URL(s);HttpsURLConnection c = (HttpsURLConnection) u.openConnection();

                c.setRequestMethod("POST");

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

                        String json = rd.readLine();
                        Log.d(TAG,json);
                        break;
                    case 401:
                        Log.d(TAG,"Server unauthorized");
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
            eMotoAdsApprovalItem ads = adsHashMap.get(adsID);
            try {
                //TODO: fix asset ID
                String s = String.format("https://emotovate.com/api/ads/unapprove/%s?scheduleAssetId=%s&userIP=%s",token,adsID,"192.168.1.1");
                Log.d(TAG,s);
                URL u = new URL(s);
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

                Log.d(TAG, String.format("http-response:%3d", status));
                switch (status) {

                    case 200:
                    case 201:
                        rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));

                        String json = rd.readLine();
                        Log.d(TAG,"Ads" + json);
                        break;
                    case 401:
                        Log.d(TAG,"Server unauthorized");
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
