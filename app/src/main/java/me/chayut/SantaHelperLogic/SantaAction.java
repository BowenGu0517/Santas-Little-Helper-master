package me.chayut.SantaHelperLogic;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chayut on 14/04/16.
 */
public class SantaAction  implements Parcelable{

    public static final int ACTION_NULL = 0;
    public static final int ACTION_SMS =1;
    public static final int ACTION_EMAIL =2;


    public static final int ACTION_WIFI = 3;

    private int taskType = 0 ;
    private String email = "";
    private String phoneNumber = "";
    private Boolean wifiState = true;
    private String message = "";
    private Boolean hasAttachment = false;


    public SantaAction() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.taskType);
        dest.writeString(this.email);
        dest.writeString(this.phoneNumber);
        dest.writeValue(this.wifiState);
        dest.writeString(this.message);
        dest.writeValue(this.hasAttachment);
    }

    protected SantaAction(Parcel in) {
        this.taskType = in.readInt();
        this.email = in.readString();
        this.phoneNumber = in.readString();
        this.wifiState = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.message = in.readString();
        this.hasAttachment = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<SantaAction> CREATOR = new Creator<SantaAction>() {
        @Override
        public SantaAction createFromParcel(Parcel source) {
            return new SantaAction(source);
        }

        @Override
        public SantaAction[] newArray(int size) {
            return new SantaAction[size];
        }
    };


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getWifiState() {
        return wifiState;
    }

    public void setWifiState(Boolean wifiState) {
        this.wifiState = wifiState;
    }

    public String getTaskTypeString(){

        //TODO set action string summary
        switch (getTaskType()){
            case SantaAction.ACTION_EMAIL:
                return "Email Action";
            case SantaAction.ACTION_SMS:
                return "SMS Action";
            case SantaAction.ACTION_WIFI:
                return "Wifi Action";
            default:
                return "Not set";
        }
    }
}

