package com.dzig.model;

import java.util.Date;

public class CoordinatesTest {

    private  final String id;
    private  final String creator;
    private  final Date date;
    private  final double lat;
    private  final double lon;
    private  final int accuracy;


    public CoordinatesTest(String id, String creator, Date date, double lat, double lon, int accuracy) {
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
}
