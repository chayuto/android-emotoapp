package eMotoLogic;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by chayut on 3/03/15.
 */

public class eMotoBTPacketManager {

    //debug
    private final static String TAG = "eMotoBTPacketManager";

    //================================================================================
    // Properties
    //================================================================================
    public final static long SEND_TIME_OUT = 500; //ms

    //Setup
    private eMotoBTServiceInterface mBTServiceInterface;
    private eMotoBTSessionInterface mBTSessionInterface;


    //store all pending packets
    private ArrayList<eMotoBTPacket> packetList;
    private int lastSendIndex;

    //================================================================================
    // Constructors
    //================================================================================

    public eMotoBTPacketManager(eMotoBTServiceInterface mServiceInterface,eMotoBTSessionInterface mSessionInterface){
        packetList = new ArrayList<>();
        lastSendIndex = 0;
        mBTServiceInterface = mServiceInterface;
        mBTSessionInterface = mSessionInterface;
    }


    //================================================================================
    // process outgoing Section
    //================================================================================

    public boolean addPacketToPendingList(eMotoBTPacket mPacket){
        //add packet to queue
        packetList.add(mPacket);
        return true;
    }

    public void sendPendingPackets(){
        byte[] byteToSend = nextPacketSendBytes();

        while (byteToSend != null) {
            mBTServiceInterface.sendBytes(byteToSend);
            byteToSend = nextPacketSendBytes();
        }
    }

    public byte[] nextPacketSendBytes(){

        //TODO: keep track of Nacked and pending packet, and change sending policy
        if(packetList.size() ==0)
        {
            lastSendIndex = 0;
            return null;
        }

        for(int i = lastSendIndex;i<packetList.size();i++) {

            eMotoBTPacket mPacket = packetList.get(i);

            //Log.d(TAG,String.format("Process Pkt: %d state %d",mPacket.getTransactionID(),mPacket.getPacketStat()));

            switch (mPacket.getPacketStat()) {

                case eMotoBTPacket.PKT_SECOND_ATTEMPT:
                case eMotoBTPacket.PKT_FIRST_ATTEMPT:
                case eMotoBTPacket.PKT_NO_ATTEMPT:

                    //if packet never been ACK in time limit
                    if(System.currentTimeMillis() - mPacket.getLastSendTimeStamp() > SEND_TIME_OUT ) {
                        Log.d(TAG,String.format("Send Pkt: %d state %d @ %d",mPacket.getTransactionID(),mPacket.getPacketStat(),System.currentTimeMillis()));

                        mPacket.setPacketStat(mPacket.getPacketStat() + 1); //increase number of attempt
                        mPacket.setLastSendTimeStamp(System.currentTimeMillis());
                        lastSendIndex = i;

                        return mPacket.getPacketBytes();
                    }
                    break;
                case eMotoBTPacket.PKT_THIRD_ATTEMPT:
                    if(System.currentTimeMillis() - mPacket.getLastSendTimeStamp() > SEND_TIME_OUT ) {

                        //TODO: never receive ACK within time limit. abort all transmit?
                        Log.d(TAG,String.format("Drop Pkt: %d state %d @ %d",mPacket.getTransactionID(),mPacket.getPacketStat(),System.currentTimeMillis()));
                        packetList.remove(i);
                    }
                default:
            }
        }
        lastSendIndex = 0;
        return null;

    }

    /**
     * Test
     */
    class processNext implements Runnable {
        @Override
        public void run() {

        }
    }

    //================================================================================
    // Process Incoming Section
    //================================================================================

    public void ackReceived (eMotoBTPacketIncoming mPacket){

        switch (mPacket.getCMD_Type()) {
            case eMotoBTPacket.ACK_COMMAND:
                //search for match packet to ack
                for (int i = 0; i < packetList.size(); i++) {
                    eMotoBTPacket sourcePacket = packetList.get(i);
                    if(sourcePacket.getTransactionID() == mPacket.getTransactionID() )
                    {
                        Log.d(TAG,String.format("ACK for txn:%d "  , (int) mPacket.getTransactionID() ));
                        sourcePacket.setPacketStat(eMotoBTPacket.PKT_ACKED);
                        packetList.remove(i);
                        Log.d(TAG,String.format("DID:%02x ",(int)mPacket.getDataId()));

                        switch(mPacket.getDataId()){

                            case eMotoBTPacket.DID_DEVICE_ID:
                                Log.d(TAG,"DID_DEVICE_ID");

                                int len = eMotoBTPacket.LEN_DID_ACK_DEV_ID;

                                if (mPacket.getmPayloadBytes().length == len){
                                    byte[] devIDbytes = new byte[len];
                                    System.arraycopy(mPacket.getmPayloadBytes(), 1, devIDbytes, 0, len);
                                    String devID = String.format("%02x%02x%02x%02x",devIDbytes[0],devIDbytes[1],devIDbytes[2],devIDbytes[3]);
                                    Log.d(TAG,String.format("devID:%s ",devID));
                                    mBTSessionInterface.setupSessionWithDeviceID(devID);
                                }
                                else
                                {
                                    //length error
                                    Log.d(TAG,"Payload length mismatch DID");
                                }


                            case eMotoBTPacket.DID_HW_VERSION:

                            case eMotoBTPacket.DID_FW_VERSION:
                            case eMotoBTPacket.DID_IMG_INFO:

                            case eMotoBTPacket.DID_PROTOCOL:

                            default:

                                break;

                        }

                    }
                }
                break;
            case eMotoBTPacket.NACK_COMMAND:

                //search for match packet to nack
                for (int i = 0; i < packetList.size(); i++) {
                    eMotoBTPacket sourcePacket = packetList.get(i);
                    if(sourcePacket.getTransactionID() == mPacket.getTransactionID() )
                    {
                        Log.d(TAG,String.format("NACK for txn:%d "  , (int) mPacket.getTransactionID() ));
                        sourcePacket.setPacketStat(eMotoBTPacket.PKT_NACKED);
                        //TODO:REPORT Error
                    }
                }
            default:
                break;
        }

    }

    //================================================================================
    // Utility Section
    //================================================================================

    public int countPendingPacket (){
        return packetList.size();
    }

    public void clearPendingPacket (){
        packetList.clear();
    }





}
