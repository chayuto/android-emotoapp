package eMotoLogic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chayut on 6/01/15.
 */
public class eMotoLoginResponse implements Parcelable {

    private boolean success;
    private String token;
    private String idle;
    private String username;
    private String credential; //temporary, unsafe

    public eMotoLoginResponse(){
        success = false;
        this.token = null;
        this.idle = null;
        this.username = null;
        this.credential = null;
    }


    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdle() {
        return idle;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    //Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeByte((byte) (success ? 1 : 0));
        out.writeString(token);
        out.writeString(idle);
        out.writeString(username);
        out.writeString(credential);
    }

    public static final Creator<eMotoLoginResponse> CREATOR
            = new Creator<eMotoLoginResponse>() {
        public eMotoLoginResponse createFromParcel(Parcel in) {
            return new eMotoLoginResponse(in);
        }

        public eMotoLoginResponse[] newArray(int size) {
            return new eMotoLoginResponse[size];
        }
    };

    private eMotoLoginResponse(Parcel in) {

        success = in.readByte() != 0;
        token = in.readString();
        idle = in.readString();
        username = in.readString();
        credential = in.readString();

    }

}
