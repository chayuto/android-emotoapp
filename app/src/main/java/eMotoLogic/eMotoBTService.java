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
import java.util.ArrayList;
import java.util.Set;


/**
 *
 * Created by chayut on 15/01/15.
 */
public class eMotoBTService implements eMotoBTServiceInterface {


    //Flags
    public final static byte PREAMBLE0 = (byte)0xEC;
    public final static byte PREAMBLE1 = (byte)0xDF;
    public final static int BT_STATE_DISCONNECTED = 0;
    public final static int BT_STATE_CONNECTED = 1;
    //Constants
    public final static int PAYLOAD_MTU = 1000;
    public final static int LEN_PKT_HEADER = 8;
    public final static String eMotoCellBTName = "HC-06";
    //debug
    private static final String TAG = "eMotoBTService";
    //Declarations
    private final BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int BTServiceState;

    private Context mContext;
    private eMotoLogicInterface mServiceInterface;
    private eMotoBTSession mBTSession;


    public eMotoBTService(Context context, eMotoLogicInterface myServiceInterface) { //, Handler handler
        BTServiceState = BT_STATE_DISCONNECTED;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //mHandler = handler;
        mServiceInterface = myServiceInterface;
        mContext = context;
    }

    //region BT management

    public void startBTService(String cellName){

        if(BTServiceState == BT_STATE_CONNECTED )
        {
            //TODO:Choose to disconnect and reconnect
            return;
        }
        if(ifHardwareSupportBT()){
            BluetoothDevice mDevice =  getPairedCell(cellName);

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
    }

    /**
     *
     * @return true if the device support bluetooth
     */
    private boolean ifHardwareSupportBT(){

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

    /**
     * get List of paired eMotoCell to the phone
     *
     * @return ArrayList of device Name of eMotoCell
     */
    public ArrayList<String> getPairedCellList (){
        Log.d(TAG,"getPairedCellList()");
        ArrayList<String> deviceList = new ArrayList<String>();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            eMotoServiceBroadcaster.broadcastBTError("Device does not support Bluetooth",mContext);
            return null;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d(TAG,"Paired:" + device.getName() + " : " + device.getAddress());
                String devName = device.getName();
                if (devName.toLowerCase().contains(eMotoCellBTName.toLowerCase())) {
                   deviceList.add(devName);
                }
            }
        }
        return deviceList;
    }

    private BluetoothDevice getPairedCell (String cellName){
        Log.d(TAG,"getPairedCell()");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d(TAG,"Paired:" + device.getName() + " : " + device.getAddress());
                if (device.getName().equalsIgnoreCase(cellName)) {
                    return device; //return if match
                }
            }
        }
        return null;
    }

    public int getServiceState (){
        return BTServiceState;
    }

    public String getServiceReport (){

        String report = String.format("State:%d",getServiceState());

        return report;
    }

    /**
     * Setup initial setup after connected to an eMotoCell
     */
    private void onDeviceConnect(){
        Log.d(TAG,"onDeviceConnect()");
        eMotoServiceBroadcaster.broadcastIntentWithState(eMotoService.RES_BT_CONNECTED,mContext);
        BTServiceState = BT_STATE_CONNECTED;
        mBTSession = new eMotoBTSession(this,mServiceInterface);
    }

    /**
     * Clear Session after disconnected
     */
    private void onDeviceDisconnect(){
        Log.d(TAG,"onDeviceDisconnect()");
        eMotoServiceBroadcaster.broadcastIntentWithState(eMotoService.RES_BT_DISCONNECTED,mContext);
        BTServiceState = BT_STATE_DISCONNECTED;

    }

    public Context getBTServiceContext(){
        return mContext;
    }

    //endregion

    //region send data section

    /**
     * provide interface for the eMotoService to send information to the session
     * @return
     */
    public eMotoBTSession getSession(){
        return mBTSession;
    }

    /**
     * check if the session is ready for end-to-end communication
     * @return boolean
     */
    public boolean sessionIsReady(){

        boolean returnVal = false;
        if(mBTSession!=null){
            returnVal = true;
        }
        return returnVal;
    }


    /**
     * Send byte array over outputStream
     * @param bytes bytes array to send
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
     * Analyse packet header with header CRC checking and obtain the length of the payload
     * @param headerBytes bytes array
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
            onDeviceConnect();

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
                                    mBTSession.reveiceIncomingPacket(mPacket);
                                }
                                else{
                                    Log.d(TAG,"Packet is invalid");
                                }

                            }
                        }
                    }

                } catch (IOException e) {
                    onDeviceDisconnect();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {

            }
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
