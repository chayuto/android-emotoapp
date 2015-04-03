package eMotoLogic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;


/**
 *
 * Created by chayut on 15/01/15.
 */
public class eMotoBTService implements eMotoBTServiceInterface {


    //debug
    private static final String TAG = "eMotoBTService";

    //Flags
    public final static byte PREAMBLE0 = (byte)0xEC;
    public final static byte PREAMBLE1 = (byte)0xDF;

    public final static int BT_STATE_DISCONNECTED = 0;
    public final static int BT_STATE_CONNECTED = 1;

    //Constants
    public final static int PAYLOAD_MTU = 1000;
    public final static int LEN_PKT_HEADER = 8;
    public final static String eMotoCellBTName = "HC-06";

    //Declarations
    private final BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int BTServiceState;
    private Context mContext;
    private eMotoBTPacketManager mPacketManager;
    private int iTransactionID;

    /** @deprecated */
    private byte[] mainIncomingBuffer = {0};


    public eMotoBTService(Context context) { //, Handler handler
        BTServiceState = BT_STATE_DISCONNECTED;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //mHandler = handler;
        mContext = context;
        mPacketManager = new eMotoBTPacketManager(this);

        iTransactionID = 0;//reset packet ID
    }


    //region send data section

    //supply new section ID for every new Transaction
    public int getNewTransactionID (){

        iTransactionID++;
        if(iTransactionID>=255){
            iTransactionID =0;
        }

        return iTransactionID;
    }

    public void addPacketToSendingQueue (eMotoBTPacket packet){
        mPacketManager.addPacketToPendingList(packet);

        sendPendingPackets();
    }

    public void sendTest(){

         sendBytes(mPacketManager.nextPacketSendBytes());
    }


    public void sendPendingPackets(){

        byte[] byteToSend = mPacketManager.nextPacketSendBytes();

        while (byteToSend != null) {
            sendBytes(byteToSend);
            byteToSend = mPacketManager.nextPacketSendBytes();
        }
    }

    /**
     * Send byte array over outputStream
     * @param bytes
     */
    public void sendBytes (byte[] bytes){

        if(bytes == null){
            Log.d(TAG,"sendBytes: null");
            return;
        }

        if(BTServiceState == BT_STATE_CONNECTED) {
            mConnectedThread.write(bytes);
        }
        else
        {
            Log.d(TAG,"BT is not in ready state");
            eMotoServiceBroadcaster.broadcastBTError("BT is not in ready state",mContext);
        }
    }

    //endregion

    //region Receive data section


    /**
     * @deprecated
     */
    private void processIncomingBytes (byte[] incomingBytes){
        String TAG = "BTProcessing";

        //append new data to buffer
        byte[] newMainBuffer = new byte[mainIncomingBuffer.length + incomingBytes.length];
        System.arraycopy(mainIncomingBuffer, 0, newMainBuffer, 0, mainIncomingBuffer.length);
        System.arraycopy(incomingBytes, 0, newMainBuffer, mainIncomingBuffer.length, incomingBytes.length);

        mainIncomingBuffer = newMainBuffer;

        Log.d(TAG,"MainBuffer:" + new String(mainIncomingBuffer, 0, mainIncomingBuffer.length));

        detectPacket();
    }

    /**
     * @deprecated
     */
    private void detectPacket(){

        for (int i =0 ; i<= (mainIncomingBuffer.length - LEN_PKT_HEADER); i++) {

            if (mainIncomingBuffer[i] == PREAMBLE0) {
                if (mainIncomingBuffer[i + 1] == PREAMBLE1) {
                    Log.d(TAG, "Detect incoming PreAmble");

                    byte[] headerBytes = new byte[LEN_PKT_HEADER];
                    System.arraycopy(mainIncomingBuffer, i, headerBytes, 0, LEN_PKT_HEADER);

                    //Extract Header info (Some to done after check packet length
                    byte transactionID =    headerBytes[2];
                    byte command =          headerBytes[3];
                    byte contentSize0 =     headerBytes[4];
                    byte contentSize1  =    headerBytes[5];
                    byte contentCRC  =      headerBytes[6];
                    byte headerCRC  =       headerBytes[7];
                    byte[] contentSizeArray = {contentSize0,contentSize1};
                    int iContentSize = (int)ByteBuffer.wrap(contentSizeArray).order(ByteOrder.LITTLE_ENDIAN).getShort();

                    Log.d(TAG, String.format("Content Size:%d",iContentSize));

                    if (headerCRC != xCRCGen.crc_8_ccitt(headerBytes, LEN_PKT_HEADER - 1)){
                        Log.d(TAG,"Header Corrupt");




                      break; //break for loop, ignore the bytes
                    }

                    int iMessageLength = LEN_PKT_HEADER+iContentSize;

                    //check if received the full packet
                    if(i+iMessageLength <= mainIncomingBuffer.length) {


                        int iNewRemainingMainBufferLength = mainIncomingBuffer.length - iMessageLength - i;
                        byte[] newRemainingMainBuffer = new byte[iNewRemainingMainBufferLength];
                        byte[] contentBytes = new byte[iContentSize];

                        i=0;

                        //Extract message from main Buffer
                        System.arraycopy(mainIncomingBuffer, iMessageLength + i, newRemainingMainBuffer, 0, iNewRemainingMainBufferLength);
                        System.arraycopy(mainIncomingBuffer, i+ LEN_PKT_HEADER,  contentBytes, 0, iContentSize);
                        mainIncomingBuffer = newRemainingMainBuffer; //update buffer after dectect Preamble
                        i = 0; //reset counter

                        //TODO: process response
                        eMotoBTPacketIncoming incomingPacket = new eMotoBTPacketIncoming(headerBytes, contentBytes,iContentSize);
                        if(incomingPacket.isContentValid()){
                            Log.d(TAG,"Packet is valid");
                            mPacketManager.ackReceived(incomingPacket);
                        }
                        else{
                            Log.d(TAG,"Packet is invalid");
                        }
                    }


                }
            }
        }
    }

    /**
     * Analyse packet header with header CRC checking and obtain the length of the payload
     * @param headerBytes
     * @return the size of the content in the expecting packet, return -1, if header is invalid
     */
    private int analyseHeader (byte[] headerBytes){
        //Extract Header info (Some to done after check packet length

        if(headerBytes.length != LEN_PKT_HEADER){
            return -1;
        }

        byte transactionID =    headerBytes[2];
        byte command =          headerBytes[3];
        byte contentSize0 =     headerBytes[4];
        byte contentSize1  =    headerBytes[5];
        byte contentCRC  =      headerBytes[6];
        byte headerCRC  =       headerBytes[7];
        byte[] contentSizeArray = {contentSize0,contentSize1};
        int iContentSize = (int)ByteBuffer.wrap(contentSizeArray).order(ByteOrder.LITTLE_ENDIAN).getShort();

        Log.d(TAG, String.format("Content Size:%d",iContentSize));

        if (headerCRC != xCRCGen.crc_8_ccitt(headerBytes, LEN_PKT_HEADER - 1)){
            Log.d(TAG,"Header Corrupt");
            Log.d(TAG,eMotoUtility.bytesToHex(headerBytes));

            return -1;
        }

        return iContentSize;
    }


    //endregion

    public void startBTService(){

        if(BTServiceState == BT_STATE_CONNECTED )
        {
            return;
        }

        BluetoothDevice mDevice = getBTDevice();
        if (mDevice!= null) {
            mConnectThread = new ConnectThread(mDevice);
            mConnectThread.start();
        }
        else
        {
            Log.d(TAG, "Device is not Paired");
            eMotoServiceBroadcaster.broadcastBTError("Device is not Paired",mContext);
        }
    }

    public boolean initiateBTHardware (){

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.d(TAG, "Device does not support Bluetooth");
            eMotoServiceBroadcaster.broadcastBTError("Device does not support Bluetooth",mContext);
            return false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is not enabled");
            eMotoServiceBroadcaster.broadcastBTError( "Bluetooth is not enabled",mContext);
            return false;
        }
        return true;
    }

    public static BluetoothDevice getBTDevice (){

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices

        BluetoothDevice mDevice = null;
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d(TAG,"Paired: " + device.getName() + " : " + device.getAddress());
                if (device.getName().equalsIgnoreCase(eMotoCellBTName)) {
                    mDevice = device; //if the device is the BT dongle, TODO: selection
                }
            }
        }
        return mDevice;
    }

    public int getServiceState (){
        return BTServiceState;
    }

    public String getServiceReport (){
        return String.format("State:%d TxnID:%d PendingPkt:%d",getServiceState(),iTransactionID,mPacketManager.countPendingPacket());
    }

    //region Threads section

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {

                Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                tmp = (BluetoothSocket) m.invoke(device, 1);


            }
            /*catch (IOException e) {
                Log.d("BT","Err:" +e.getMessage());
                eMotoServiceBroadcaster.broadcastBTError("Err:" +e.getMessage(),mContext);
            }*/
            catch (NoSuchMethodException e)
            {
                Log.d("BT","Err:" +e.getMessage());
                eMotoServiceBroadcaster.broadcastBTError("Err:" +e.getMessage(),mContext);
            }
            catch (IllegalAccessException e){}
            catch (InvocationTargetException e){}
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            //mBluetoothAdapter.cancelDiscovery();
            Log.d("BT","ConnectThread Run");
            eMotoServiceBroadcaster.broadcastBTStatus("Start Connecting...", mContext);
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Log.d("BT","EX:" +connectException.getMessage());
                eMotoServiceBroadcaster.broadcastBTError("Err:" +connectException.getMessage(),mContext);

                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.d("BT","EX:" +closeException.getMessage());
                }
                return;
            }

            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.run();
            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { e.printStackTrace();}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            Log.d(TAG,"ConnectedThread Run");
            eMotoServiceBroadcaster.broadcastBTStatus("Connected to EmotoCell", mContext);
            BTServiceState = BT_STATE_CONNECTED;

            // Keep listening to the InputStream until an exception occurs
            while (BTServiceState == BT_STATE_CONNECTED) {
                try {
                    //int availableBytes = mmInStream.available();


                    byte firstByte = (byte) mmInStream.read();
                    //Log.d(TAG,String.format("Read %x",firstByte));

                    if(firstByte ==PREAMBLE0){
                        byte secondByte = (byte) mmInStream.read();
                        if(secondByte ==PREAMBLE1){
                            //Log.d(TAG,String.format("Read %x",secondByte));

                            Log.d(TAG, "Detect incoming PreAmble");

                            byte[] headerBytes = new byte[LEN_PKT_HEADER];
                            headerBytes[0] = PREAMBLE0;
                            headerBytes[1] = PREAMBLE1;

                            int cnt = 0;
                            while(cnt!= 6) {
                                //Log.d(TAG,"Header incomplete Read");
                                cnt += mmInStream.read(headerBytes, 2+cnt, 6-cnt); //read remaining part of the preamble
                            }

                            int contentSize = analyseHeader(headerBytes);
                            if(contentSize>=0){

                                byte[] contentBytes = new byte[contentSize];

                                int count = 0;
                                while(count != contentSize)
                                {
                                    //Log.d(TAG,"Content incomplete Read");
                                    count += mmInStream.read(contentBytes,count,contentSize-count);
                                }

                                eMotoBTPacketIncoming mPacket = new eMotoBTPacketIncoming(headerBytes,contentBytes,contentSize);
                                if(mPacket.isContentValid()){
                                    Log.d(TAG,"Packet is valid");
                                    mPacketManager.ackReceived(mPacket);
                                }
                                else{
                                    Log.d(TAG,"Packet is invalid");
                                }

                            }


                        }
                    }


                        /*
                    byte[] byteArray = new byte[availableBytes];
                    // Read from the InputStream
                    mmInStream.read(byteArray,0,availableBytes);

                    //mmOutStream.write(PREAMBLE);
                    processIncomingBytes(byteArray);
                    */

                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    //endregion



}
