package eMotoLogic;/*
package me.chayut.eMotoLogic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class eMotoAssetLibrary {

    //debug
    private final static String TAG = "eMotoAssetLibrary";

    private Map<String,Bitmap> adsHashMap =  new HashMap<String,Bitmap>();


    public Bitmap getThumbnail (eMotoAds ads){
        return this.getThumbnail(ads.id(),ads.getAdsThumbnailURLstr());
    }

    public Bitmap getThumbnail (String AdsID, String strAdsThumbnailURL){

        Bitmap adsThumbnail = adsHashMap.get(AdsID);

        if (adsThumbnail==null)
        {
            try {
                URL imageURL = new URL(strAdsThumbnailURL);
                adsThumbnail = BitmapFactory.decodeStream(imageURL.openStream());
                adsHashMap.put(AdsID,adsThumbnail);
            }
            catch (MalformedURLException ex)
            {
                ex.printStackTrace();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return adsThumbnail;
    }



    public void clearAssetLibrary(){
        adsHashMap.clear();
    }



}
*/