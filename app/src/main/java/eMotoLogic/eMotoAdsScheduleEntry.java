package eMotoLogic;

import java.util.List;

/**
 * Created by chayut on 16/07/15.
 */
public class eMotoAdsScheduleEntry {

    private String entryFrom;
    private String entryTo;
    private List<eMotoAds> adsList;


    public eMotoAdsScheduleEntry(String entryFrom, String entryTo,List<eMotoAds> adsList) {
        this.adsList = adsList;
        this.entryFrom = entryFrom;
        this.entryTo = entryTo;
    }

    public String getEntryFrom() {
        return entryFrom;
    }

    public String getEntryTo() {
        return entryTo;
    }

    public List<eMotoAds> getAdsList() {
        return adsList;
    }


}
