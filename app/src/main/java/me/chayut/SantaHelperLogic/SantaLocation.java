package me.chayut.SantaHelperLogic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chayut on 1/05/16.
 */
public class SantaLocation implements Parcelable {

    private String name;
    private double latitude;
    private double longitude;

    public SantaLocation() {
    }

    public SantaLocation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected SantaLocation(Parcel in) {
        this.name = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<SantaLocation> CREATOR = new Parcelable.Creator<SantaLocation>() {
        @Override
        public SantaLocation createFromParcel(Parcel source) {
            return new SantaLocation(source);
        }

        @Override
        public SantaLocation[] newArray(int size) {
            return new SantaLocation[size];
        }
    };
}
