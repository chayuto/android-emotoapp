package eMotoLogic;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chayut on 23/07/15.
 */
public class eMotoAdsApprovalItem {

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

}
