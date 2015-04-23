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
        mServiceInterface = serviceInterface;
        mBTServiceInterface = BTServiceInterface;
        mPacketManager = new eMotoBTPacketManager(BTServiceInterface,this);
    }

    private void initializeSession(){
        //TODO:get device ID

        //TODO:get cell info from the server
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
        int len = 800;
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
