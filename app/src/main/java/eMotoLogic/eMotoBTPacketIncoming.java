package eMotoLogic;

import android.util.Log;

/**
 * Created by chayut on 26/03/15.
 */
public class eMotoBTPacketIncoming  {

    private final static String TAG = "eMotoBTPacketIncoming";
    private byte[] mPayloadBytes;
    private int  iPayloadLen;
    private byte transactionID ;
    private byte command ;
    private byte contentSize0 ;
    private byte contentSize1;
    private byte contentCRC ;
    private byte headerCRC ;


    public eMotoBTPacketIncoming (byte[] headerBytes, byte[] payloadBytes, int payloadLength){

        mPayloadBytes = payloadBytes;
        iPayloadLen = payloadLength;
        transactionID =    headerBytes[2];
        command =          headerBytes[3];
        contentSize0 =     headerBytes[4];
        contentSize1  =    headerBytes[5];
        contentCRC  =      headerBytes[6];
        headerCRC  =       headerBytes[7];

        //Analyse Header Command
        switch (command)
        {
            case eMotoBTPacket.SET_COMMAND: Log.d(TAG, "SET_COMMAND");
                break;
            case eMotoBTPacket.GET_COMMAND: Log.d(TAG, "GET_COMMAND");
                break;
            case eMotoBTPacket.ACK_COMMAND: Log.d(TAG, "ACK_COMMAND");
                break;
            case eMotoBTPacket.NACK_COMMAND: Log.d(TAG, "NACK_COMMAND");
                break;
            default:
                Log.d(TAG, "Unrecognized command");
                break;
        }

        //display received message
        //String receivedMessage = "messageBytes:" + eMotoUtility.bytesToHex(payloadBytes);
        //Log.d(TAG,receivedMessage );
    }

    public boolean isContentValid (){

        if(contentCRC == xCRCGen.crc_8_ccitt(mPayloadBytes,iPayloadLen) && mPayloadBytes.length == iPayloadLen ) {
            return true;
        }else {
            return false;
        }
    }

    public byte getCMD_Type(){
        return command;
    }
    public int getTransactionID() {
        return transactionID;
    }


}
