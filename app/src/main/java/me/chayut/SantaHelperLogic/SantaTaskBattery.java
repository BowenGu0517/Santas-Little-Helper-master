package me.chayut.SantaHelperLogic;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chayut on 8/04/16.
 */
public class SantaTaskBattery extends SantaTask implements Parcelable {

    public static final Creator<SantaTaskBattery> CREATOR = new Creator<SantaTaskBattery>() {
        @Override
        public SantaTaskBattery createFromParcel(Parcel source) {
            return new SantaTaskBattery(source);
        }

        @Override
        public SantaTaskBattery[] newArray(int size) {
            return new SantaTaskBattery[size];
        }
    };
    private static final String TAG = "SantaTaskBattery";
    private int mBattPercentage = 0;



    public SantaTaskBattery(int thresholdPercentage,SantaAction action){
        mAction = action;
        mBattPercentage = thresholdPercentage;
        uuid = SantaUtilities.getNewUUID();
    }

    protected SantaTaskBattery(Parcel in) {
        this.mBattPercentage = in.readInt();
        this.uuid = in.readString();
        this.mAction = in.readParcelable(SantaAction.class.getClassLoader());
    }


    public int getmBattPercentage() {
        return mBattPercentage;
    }

    public void setmBattPercentage(int mBattPercentage) {
        this.mBattPercentage = mBattPercentage;
    }


    @Override
    public JSONObject toJSONObject (){

        JSONObject mObject = new JSONObject();

        try{
            mObject.put(SantaLogic.JTAG_SANTA_TASK_TYPE,SantaLogic.JTAG_SANTA_TASK_BATT);
            mObject.put(SantaLogic.JTAG_SANTA_BATT_LEVEL,mBattPercentage);
            mObject.put(SantaLogic.JTAG_UUID,uuid);
            Gson gson = new Gson();
            mObject.put(SantaLogic.JTAG_SANTA_ACTION,new JSONObject( gson.toJson(mAction)));

            //test
            //SantaAction test = gson.fromJson(gson.toJson(mAction),SantaAction.class);

            Log.d(TAG,gson.toJson(mAction) );
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return  mObject;
    }

    public boolean isConditionMet(int battLevel){

        if(battLevel < mBattPercentage){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mBattPercentage);
        dest.writeString(this.uuid);
        dest.writeParcelable(this.mAction, flags);
    }
}
