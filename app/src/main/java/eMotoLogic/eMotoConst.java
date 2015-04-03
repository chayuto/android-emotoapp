package eMotoLogic;

/**
 *
 * Created by chayut on 26/03/15.
 */
public final class eMotoConst {
    eMotoConst(){
    }

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

    public final static int LEN_DID_ACK_DEV_ID = 4;
    public final static int LEN_DID_GET_DEV_ID = 1;
    public final static int LEN_DID_HW_VER = 2;
    public final static int LEN_DID_FW_VER = 2;
    public final static int LEN_DID_C_TIME = 6;
    public final static int LEN_DID_SET_IMG_INFO = 15;
}
