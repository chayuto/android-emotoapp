package eMotoLogic;

import android.content.Context;

/**
 * Created by chayut on 22/03/15.
 */
public interface eMotoBTServiceInterface {

    public void sendBytes(byte[] bytes);
    public Context getBTServiceContext();
}