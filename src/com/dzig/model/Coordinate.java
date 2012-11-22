package com.dzig.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Coordinate implements Parcelable{

    private  final String id;
    private  final String creator;
    private  final Date date;
    private  final double lat;
    private  final double lon;
    private  final int accuracy;


    public Coordinate(String id, String creator, Date date, double lat, double lon, int accuracy) {
        this.id = id;
        this.creator = creator;
        this.date = date;
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
    }

    public String getId() {
        return id;
    }

    public String getCreator() {
        return creator;
    }

    public Date getDate() {
        return date;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getAccuracy() {
        return accuracy;
    }

    @Override
    public int describeContents() {
        return 2001;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
         out.writeString(id);
        out.writeString(creator);
        out.writeLong(date.getTime());
        out.writeDouble(lat);
        out.writeDouble(lon);
        out.writeInt(accuracy);
    }

    public static final Parcelable.Creator<Coordinate> CREATOR
            = new Parcelable.Creator<Coordinate>() {
        public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(
                    in.readString(),
                    in.readString(),
                    new Date(in.readLong()),
                    in.readDouble(),
                    in.readDouble(),
                    in.readInt());
        }

        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };
}
