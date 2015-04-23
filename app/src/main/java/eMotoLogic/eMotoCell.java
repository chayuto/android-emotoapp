package eMotoLogic;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by chayut on 6/01/15.
 */
public class eMotoCell implements Parcelable {

    private static final String TAG = "eMotoCell";

    public String deviceID;
    public String deviceName;
    public String eMotocellSerialNo;
    private String deviceFixed;

    public String deviceLatitude;
    public String deviceLongitude;
    public String deviceAssetId;


    public eMotoCell(JSONObject cell)
    {
        setCellProperty(cell);
    }
    public eMotoCell(){

    }

    //constructor
    private void setCellProperty(JSONObject cell)
    {
        try {
            deviceID= cell.getString("DeviceId");
            deviceName = cell.getString("DeviceName");
            eMotocellSerialNo = cell.getString("eMotocellSerialNo");
            deviceFixed = cell.getString("Fixed");
            deviceAssetId = ""; //set empty string

        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    //getter
    public boolean isFixed (){
        return Boolean.parseBoolean(deviceFixed);
    }



    //region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {

        out.writeString(deviceID);
        out.writeString(deviceName);
        out.writeString(eMotocellSerialNo);
        out.writeString(deviceLatitude);
        out.writeString(deviceLongitude);
        out.writeString(deviceFixed);
        out.writeString(deviceAssetId);
    }

    public static final Creator<eMotoCell> CREATOR
            = new Creator<eMotoCell>() {
        public eMotoCell createFromParcel(Parcel in) {
            return new eMotoCell(in);
        }

        public eMotoCell[] newArray(int size) {
            return new eMotoCell[size];
        }
    };

    private eMotoCell(Parcel in) {
        deviceID = in.readString();
        deviceName = in.readString();
        eMotocellSerialNo = in.readString();
        deviceLatitude = in.readString();
        deviceLongitude = in.readString();
        deviceFixed = in.readString();
        deviceAssetId = in.readString();
    }
    //endregion

    //region network connection
    public void putDeviceOnServer (String token) {
        Log.d(TAG, "putDeviceOnServer");

        BufferedReader rd ;

        try {
            URL u = new URL(String.format("https://emotovate.com/api/devicetracking/add/%s",token));
            String jsonString = String.format("{\"DeviceId\":\"%s\",\"LightSensor\":\"%s\",\"Longitude\":\"%s\",\"Latitude\":\"%s\",\"Temperature\":\"%s\",\"AssetId\":\"%s\"}",deviceID,"true",deviceLongitude,deviceLatitude,"24.3",deviceAssetId);
            //String jsonString = String.format("{DeviceId:\"%s\",LightSensor:\"%s\",Longitude:\"%s\",Latitude:\"%s\",Temperature:\"%s\"}","00000000","true","-33.8238395","151.1996951","24.3");
            Log.d(TAG, String.format("JSON:%s", jsonString));
            HttpsURLConnection c = (HttpsURLConnection) u.openConnection();


            c.setRequestMethod("POST");
            c.setUseCaches(false);
            c.setDoInput(true);
            c.setDoOutput(true);
            c.setRequestProperty("Content-length", String.format("%d",jsonString.getBytes("UTF-8").length));
            c.setRequestProperty("Content-Type","application/json");
            //c.setRequestProperty("Accept", "application/json");
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);

            c.connect();

            OutputStream os = c.getOutputStream();
            os.write(jsonString.getBytes("UTF-8"));
            os.flush();
            os.close();

            int status = c.getResponseCode();

            Log.d(TAG, String.format("http-response:%3d", status));
            switch (status) {

                case 200:
                case 201:
                    rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    Log.d(TAG,rd.readLine());
                    break;
                case 400:
                case 500:
                    rd  = new BufferedReader(new InputStreamReader(c.getErrorStream()));
                    Log.d(TAG,rd.readLine());
                    break;
                case 401:
                    rd  = new BufferedReader(new InputStreamReader(c.getInputStream()));
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
}
