package eMotoLogic;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chayut on 23/07/15.
 */
public class eMotoAdsApprovalItem implements Parcelable {

    private static final String TAG = "eMotoAdsApprovalItem";

    private String AdsId;
    private String AdsDescription;
    private String AdsUrl;
    private String Approved;
    private String AdsApprovalDate;
    private String AdsApprovedById;

    public eMotoAdsApprovalItem(JSONObject ads)
    {
        setAdsProperties(ads);
    }

    private void setAdsProperties(JSONObject ads){
        try {
            AdsId = ads.getString("Id");
            AdsDescription= ads.getString("Description");
            AdsUrl =ads.getString("Url");
            Approved = ads.getString ("Approved");
            AdsApprovalDate = ads.getString("ApprovalDate");
            AdsApprovedById = ads.getString("ApprovedById");
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public String description() {
        return AdsDescription;
    }

    public String id() { return AdsId;}

    public String getAdsImageURLstr(){
        return AdsUrl;
    }

    public String getAdsThumbnailURLstr(){
        String s = getAdsImageURLstr();
        String ThumbnailURL = s.substring(0,s.length()-4) + "_t.jpg";

        return ThumbnailURL;
    }


    public String isApprovedStr() {

        Log.d(TAG,"TEMP: " + Approved);
        return Approved;
    }

    public Boolean isApproved(){

        return Boolean.parseBoolean(isApprovedStr());
    }



    //region Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {

        out.writeString(AdsId);
        out.writeString(AdsDescription);
        out.writeString(AdsUrl);
        out.writeString(Approved);
        out.writeString(AdsApprovalDate);
        out.writeString(AdsApprovedById);
    }

    public static final Creator<eMotoAdsApprovalItem> CREATOR
            = new Creator<eMotoAdsApprovalItem>() {
        public eMotoAdsApprovalItem createFromParcel(Parcel in) {
            return new eMotoAdsApprovalItem(in);
        }

        public eMotoAdsApprovalItem[] newArray(int size) {
            return new eMotoAdsApprovalItem[size];
        }
    };

    private eMotoAdsApprovalItem(Parcel in) {
        AdsId= in.readString();
        AdsDescription= in.readString();
        AdsUrl = in.readString();
        Approved = in.readString();
        AdsApprovalDate = in.readString();
        AdsApprovedById = in.readString();
    }

    //endregion

}
