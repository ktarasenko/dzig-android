package com.dzig.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.dzig.api.ParseHelpers;
import org.json.JSONObject;

import java.util.Date;

public class Coordinate implements Parcelable{

    private  final String id;
    private  final User creator;
    private  final Date date;
    private  final double lat;
    private  final double lon;
    private  final double accuracy;


    public Coordinate(String id, User creator, Date date, double lat, double lon, double accuracy) {
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

    public User getCreator() {
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

    public double getAccuracy() {
        return accuracy;
    }
    
    @Override
    public int describeContents() {
        return 2001;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
         out.writeString(id);
        out.writeParcelable(creator, 0);
        out.writeLong(date.getTime());
        out.writeDouble(lat);
        out.writeDouble(lon);
        out.writeDouble(accuracy);
    }

    public static final ComplexCreator<Coordinate> CREATOR
            = new ComplexCreator<Coordinate>() {
        @Override
		public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(
                    in.readString(),
                    (User)in.readParcelable(getClass().getClassLoader()),
                    new Date(in.readLong()),
                    in.readDouble(),
                    in.readDouble(),
                    in.readDouble());

        }

        @Override
		public Coordinate createFromJSON(JSONObject in) {
            return new Coordinate(
                    in.optString("id"),
                    User.CREATOR.createFromJSON(in.optJSONObject("creator")),
                    ParseHelpers.parseDate(in.optString("date")),
                    in.optDouble("lat"),
                    in.optDouble("lon"),
                    in.optDouble("accuracy"));
        }


        @Override
		public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };
}
