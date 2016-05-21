package me.chayut.SantaHelperLogic;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Created by chayut on 24/03/16.
 */
@Deprecated
public class EndPoint implements Parcelable {

    public static final int TYPE_EMAIL = 0;
    public static final int TYPE_PHONE = 1;
    public static final Creator<EndPoint> CREATOR = new Creator<EndPoint>() {
        @Override
        public EndPoint createFromParcel(Parcel source) {
            return new EndPoint(source);
        }

        @Override
        public EndPoint[] newArray(int size) {
            return new EndPoint[size];
        }
    };
    private  String value = "";
    private  String Name = "";
    private int type =0;

    /**
     * default constructors
     */
    public EndPoint(){
    }
    public EndPoint(int inType,String inName ,String inValue){
        value = inValue;
        Name = inName;
        type = inType;
    }

    protected EndPoint(Parcel in) {
        this.value = in.readString();
        this.Name = in.readString();
        this.type = in.readInt();
    }

    public int getType() {
        return type;
    }

    /**
     * Methods
     */

    public String getValue() {
        return value;
    }

    public String getName() {
        return Name;
    }

    /**
     * parcelable
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeString(this.Name);
        dest.writeInt(this.type);
    }
}
