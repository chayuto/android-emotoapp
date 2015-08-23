package eMotoLogic;

import android.util.Log;

import java.nio.charset.Charset;

/**
 * Created by chayut on 27/02/15.
 */
public class eMotoBTPacket {
    //debug
    private final static String TAG = "eMotoBTPacket";

    //flag
    public final static int PKT_NO_ATTEMPT =0; //new packet
    public final static int PKT_FIRST_ATTEMPT =1;
    public final static int PKT_SECOND_ATTEMPT =2;
    public final static int PKT_THIRD_ATTEMPT =3;

    public final static int PKT_ACKED = 5;

    public final static int PKT_CORRUPT =-1; //for received packet with failed CRC
    public final static int PKT_NACKED = -2;

    public final static byte GET_COMMAND = (byte)0xA5;
    public final static byte SET_COMMAND = (byte)0x4B;
    public final static byte ACK_COMMAND= (byte)0x6B;
    public final static byte NACK_COMMAND = (byte)0x8E;

    public final static byte DID_DEVICE_ID = (byte)0x00;
    public final static byte DID_HW_VERSION = (byte)0x01;
    public final static byte DID_FW_VERSION = (byte)0x02;
    public final static byte DID_PROTOCOL = (byte)0x03;
    public final static byte DID_C_TIME = (byte)0x10;
    public final static byte DID_IMG_INFO = (byte)0x20;
    public final static byte DID_IMG_DATA = (byte)0x21;
    public final static byte DID_IMG_ONLIST = (byte)0x22;

    public final static byte DID_WIFI_SETUP = (byte)0x30;

    public final static int LEN_DID_BYTE = 1;
    public final static int LEN_DID_ACK_DEV_ID = 4;
    public final static int LEN_DID_GET_DEV_ID = 1;
    public final static int LEN_DID_HW_VER = 2;
    public final static int LEN_DID_FW_VER = 2;
    public final static int LEN_DID_C_TIME = 6;
    public final static int LEN_DID_SET_IMG_INFO = 15;

    public final static int LEN_DID_SET_WIFI_SETUP = 62;

    public final static int LEN_WIFI_SSID_BYTE = 20;
    public final static int LEN_WIFI_SEC_BYTE = 1;
    public final static int LEN_WIFI_KEY_BYTE = 40;


    //Constants
    public final static int PAYLOAD_MTU = 1000;
    public final static int HEADER_LENGTH = 8;

    //variable
    //packet info
    private byte[] mPayload;
    private byte mCMD_Type;
    private int mTransactionID;

    //stat
    private int packetStat;
    private long lastSendTimeStamp;

    //constructor
    public eMotoBTPacket(byte cmdType, int transactionID, byte[] payload)
    {
        //payload is not yet send.
        packetStat = PKT_NO_ATTEMPT;
        lastSendTimeStamp = -1;

        //set payload info
        mCMD_Type = cmdType;
        mTransactionID = transactionID;
        mPayload = payload;
    }


    /*
     *static constructor
     */
    public static eMotoBTPacket SetImageDataPacket(int transactionID, byte[] imageData, int imageID)
    {
        eMotoBTPacket mPacket;

        byte image_ID = (byte) imageID;

        byte[] payloadBytes = new byte[imageData.length+2];
        System.arraycopy(imageData,0,payloadBytes,2,imageData.length);
        payloadBytes[0] = DID_IMG_DATA;
        payloadBytes[1] = image_ID;

        mPacket = new eMotoBTPacket(SET_COMMAND,transactionID,payloadBytes);

        return mPacket;
    }


    public static eMotoBTPacket GetHardwareInfoPacket (int transactionID){

        eMotoBTPacket mPacket;

        byte[] payloadBytes = new byte[1];

        payloadBytes[0] = DID_HW_VERSION;

        mPacket = new eMotoBTPacket(GET_COMMAND,transactionID,payloadBytes);
        return mPacket;
    }

    public static eMotoBTPacket GetDeviceIdPacket (int transactionID){

        eMotoBTPacket mPacket;

        byte[] payloadBytes = new byte[1];

        payloadBytes[0] = DID_DEVICE_ID;

        mPacket = new eMotoBTPacket(GET_COMMAND,transactionID,payloadBytes);
        return mPacket;
    }


    public static eMotoBTPacket imageInfoSetPacket (int transactionID,int ID,int size,String startTim,String endingTime)
    {
        eMotoBTPacket mPacket;

        byte[] payloadBytes = new byte[LEN_DID_SET_IMG_INFO+1];
        payloadBytes[0] = DID_IMG_INFO;
        payloadBytes[1] = (byte) ID;
        payloadBytes[2] = (byte) size;
        payloadBytes[3] = (byte) (size>>8);
        System.arraycopy(getTimeBytes(1, 1, 1, 1, 1, 1),0,payloadBytes,4,LEN_DATE_BYTE);
        System.arraycopy(getTimeBytes(1, 2, 1, 1, 1, 1),0,payloadBytes,10,LEN_DATE_BYTE);

        mPacket = new eMotoBTPacket(SET_COMMAND,transactionID,payloadBytes);

        return mPacket;
    }




    public static eMotoBTPacket getDeviceIDGetPacket (int transactionID){
        eMotoBTPacket mPacket;

        byte[] payloadBytes = new byte[LEN_DID_GET_DEV_ID];

        payloadBytes[0] = DID_DEVICE_ID;

        mPacket = new eMotoBTPacket(GET_COMMAND,transactionID,payloadBytes);

        return mPacket;
    }

    public static eMotoBTPacket setDeviceWifiPacket(int transactionID, String SSID, int sectype, String key)
    {
        eMotoBTPacket mPacket;

        byte[] payloadBytes = new byte[LEN_DID_SET_WIFI_SETUP];

        byte[] SSIDBytes = SSID.getBytes(Charset.forName("UTF-8"));
        byte[] keyBytes = key.getBytes(Charset.forName("UTF-8"));

        payloadBytes[0] = DID_WIFI_SETUP;
        System.arraycopy(SSIDBytes,0,payloadBytes,LEN_DID_BYTE,SSIDBytes.length);
        payloadBytes[LEN_WIFI_SSID_BYTE+LEN_DID_BYTE] = (byte) sectype;
        System.arraycopy(keyBytes,0,payloadBytes,LEN_DID_BYTE+LEN_WIFI_SSID_BYTE+LEN_WIFI_SEC_BYTE,keyBytes.length);

        mPacket = new eMotoBTPacket(SET_COMMAND,transactionID,payloadBytes);

        return mPacket;
    }

    /*
     * Functional modules
     */
    public String getPacketKeyStr(){

        return String.format("KEY%d",mTransactionID);

    }

    //Actual Packet only computed when transmitted
    public byte[] getPacketBytes(){

        byte[] outBytes = new byte[HEADER_LENGTH + mPayload.length];
        byte[] headerBytes = getPacketHeader();

        System.arraycopy(headerBytes,0,outBytes,0,HEADER_LENGTH);
        System.arraycopy(mPayload,0,outBytes,8,mPayload.length); //copy payload into out buffer


        Log.d(TAG,String.format("transactionID:%d",mTransactionID));
        Log.d(TAG,String.format("mPayload.length:%d",mPayload.length));
        Log.d(TAG,"Header:\t\t" + eMotoUtility.bytesToHex(headerBytes));
       // Log.d(TAG,"Payload:\t" + eMotoUtility.bytesToHex(mPayload));

        return outBytes;
    }

    public byte[] getPacketHeader(){

        byte[] headerBytes = new byte[HEADER_LENGTH];

        headerBytes[0] = eMotoBTService.PREAMBLE0;
        headerBytes[1] = eMotoBTService.PREAMBLE1;
        headerBytes[2] = (byte) mTransactionID;
        headerBytes[3] = mCMD_Type;
        headerBytes[4] = (byte) mPayload.length;
        headerBytes[5] = (byte) (mPayload.length>>8);
        headerBytes[6] = xCRCGen.crc_8_ccitt(mPayload,mPayload.length);
        headerBytes[7] = xCRCGen.crc_8_ccitt(headerBytes, HEADER_LENGTH - 1); //exclude the header bytes

        return headerBytes;
    }


    /*
     * Static functions
     */
    private static void analyzeACKCommand (byte[] DataMap){

        switch (DataMap[0])
        {
            case DID_DEVICE_ID:
            case DID_HW_VERSION:
            case DID_FW_VERSION:
            case DID_C_TIME:
            case DID_PROTOCOL:
            case DID_IMG_INFO:
            case DID_IMG_DATA:
            case DID_IMG_ONLIST:
            default:
                break;
        }
    }
    public static final int LEN_DATE_BYTE = 6;
    private static byte[] getTimeBytes(int year, int month, int date, int hour, int minute, int second){
        byte[] timeBytes = new byte[6];
        timeBytes[0] = (byte) second;
        timeBytes[1] = (byte) minute;
        timeBytes[2] = (byte) hour;
        timeBytes[3] = (byte) date;
        timeBytes[4] = (byte) month;
        timeBytes[5] = (byte) year;

        return timeBytes;
    }

    //region getter setter
    public byte[] getPayload() {
        return mPayload;
    }

    public byte getCMD_Type() {
        return mCMD_Type;
    }

    public int getTransactionID() {
        return mTransactionID;
    }

    public int getPacketStat() {
        return packetStat;
    }

    public void setPacketStat(int packetStat) {
        this.packetStat = packetStat;
    }

    public long getLastSendTimeStamp() {
        return lastSendTimeStamp;
    }

    public void setLastSendTimeStamp(long lastSendTimeStamp) {
        this.lastSendTimeStamp = lastSendTimeStamp;
    }
    //endregion

}
