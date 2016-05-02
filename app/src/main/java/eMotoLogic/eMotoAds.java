package eMotoLogic;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chayut on 7/01/15.
 */
public class eMotoAds implements Parcelable {
    public static final Creator<eMotoAds> CREATOR
            = new Creator<eMotoAds>() {
        public eMotoAds createFromParcel(Parcel in) {
            return new eMotoAds(in);
        }

        public eMotoAds[] newArray(int size) {
            return new eMotoAds[size];
        }
    };
    private String AdsId;
    private String AdsDescription;
    private String AdsScheduleAssetId;
    private String AdsApprove;
    private String AdsUrl;
    private String AdsExtension;
    private String AdsWidth;
    private String AdsHeight;
    private String AdsSize;

    public eMotoAds(JSONObject ads)
    {
        setAdsProperties(ads);
    }

    private eMotoAds(Parcel in) {
        AdsId= in.readString();
        AdsDescription= in.readString();
        AdsApprove = in.readString();
        AdsScheduleAssetId= in.readString();
        AdsHeight= in.readString();
        AdsWidth = in.readString();
        AdsSize = in.readString();
        AdsUrl = in.readString();
        AdsExtension = in.readString();
    }

    private void setAdsProperties(JSONObject ads){
        try {
            AdsId = ads.getString("Id");
            AdsDescription= ads.getString("Description");
            AdsApprove = ads.getString("Approved");
            AdsScheduleAssetId =ads.getString("ScheduleAssetId");
            AdsUrl =ads.getString("Url");
            AdsExtension = ads.getString("Extension");
            AdsHeight = ads.getString("Height");
            AdsWidth= ads.getString("Width");
            AdsSize = ads.getString("Size");
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public String description() {
        return AdsDescription;
    }

    public String id() { return AdsId;}

    public String scheduleAssetId() {

       return AdsScheduleAssetId;
    }

    public String isApprovedStr() {

        return AdsApprove;
    }

    public Boolean isApproved(){
        return Boolean.parseBoolean(isApprovedStr());
    }

    public String getAdsImageURLstr(){
        return AdsUrl;
    }

    public String getAdsThumbnailURLstr(){
        String s = getAdsImageURLstr();
        String ThumbnailURL = s.substring(0,s.length()-4) + "_t.jpg";

        return ThumbnailURL;
    }

    public String getAdsExtension(){
        return AdsExtension;
    }

    public int getAdsWidth(){
        return Integer.parseInt(AdsWidth);
    }

    public int getAdsHeight(){
        return Integer.parseInt(AdsHeight);
    }

    public int getAdsSize(){
        return Integer.parseInt(AdsSize);
    }

    //region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(AdsId);
        out.writeString(AdsDescription);
        out.writeString(AdsApprove);
        out.writeString(AdsScheduleAssetId);
        out.writeString(AdsHeight);
        out.writeString(AdsWidth);
        out.writeString(AdsSize);
        out.writeString(AdsUrl);
        out.writeString(AdsExtension);
    }

    //endregion

}
