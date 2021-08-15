package com.example.weatherkok.weather.models;

public class xy {
    private static final String TAG = xy.class.getSimpleName();

    String x;

    String y;

    String lon;

    String lat;

    public xy(String x, String y, String lon, String lat) {
        this.x = x;
        this.y = y;
        this.lon = lon;
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public xy() {
    }

    public xy(String x, String y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
}
