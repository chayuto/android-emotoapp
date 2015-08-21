package eMotoLogic;

import android.util.Log;

/**
 * Created by chayut on 23/04/15.
 */

public class eMotoBTSession implements eMotoBTSessionInterface {

    private static String TAG = "eMotoBTSession";

    //Setup
    private eMotoBTServiceInterface mBTServiceInterface;
    private eMotoServiceInterface  mServiceInterface;
    private eMotoBTPacketManager mPacketManager;
    private eMotoCell mCell;
    private int iTransactionID;


    public eMotoBTSession(eMotoBTServiceInterface BTServiceInterface, eMotoServiceInterface serviceInterface) {
        Log.d(TAG,"eMotoBTSession()");
        mServiceInterface = serviceInterface;
        mBTServiceInterface = BTServiceInterface;
        mPacketManager = new eMotoBTPacketManager(BTServiceInterface,this);

        initializeSession();
    }

    private void initializeSession(){
        Log.d(TAG,"initializeSession()");
        //get device ID
        mPacketManager.addPacketToPendingList(eMotoBTPacket.GetDeviceIdPacket(getNewTransactionID()));
        mPacketManager.sendPendingPackets();
    }

    public void setupSessionWithDeviceID(String deviceID){
        Log.d(TAG,"setupSessionWithDeviceID()");
        //TODO:make network call to get device info
        mCell = eMotoCell.getDeviceFromServer(mServiceInterface.getLoginToken(),deviceID);
        if(mCell.isFixed()){
            Log.d(TAG,"FixedCell");
        }
        else{
            Log.d(TAG,"MobileCell");
        }
        //HACK: set dummy position
        mCell.setDeviceAssetId("Test2");
        mCell.deviceLatitude = "-33.7238297";
        mCell.deviceLongitude =  "151.1220244";
        mCell.putDeviceOnServer(mServiceInterface.getLoginToken());
    }


    public int getNewTransactionID (){

        iTransactionID++;
        if(iTransactionID>=255){
            iTransactionID =0;
        }
        return iTransactionID;
    }

    public void testInteraction(){
        //HACK: mock up image data
        int len = 5;
        byte[] testData = new byte[len];
        for (int i =0;i<len;i++){
            testData[i] = (byte) (i%254);
        }
        eMotoBTPacket testPacket = eMotoBTPacket.SetImageDataPacket(getNewTransactionID(),testData,100);
        Log.d(TAG, testPacket.getPacketKeyStr());
        mPacketManager.addPacketToPendingList(testPacket);
        mPacketManager.sendPendingPackets();
    }

    public void reveiceIncomingPacket(eMotoBTPacketIncoming mPacket){
        mPacketManager.ackReceived(mPacket);
    }



}
