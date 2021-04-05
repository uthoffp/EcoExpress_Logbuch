package com.ecoexpress.logbuch;

public class Dataset {
    private String date;
    private String time;
    private String location;
    private int distance;
    private String gps;

    public Dataset(String date, String time, String location, String gps) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.gps = gps;
        this.distance = 0;
    }

    public Dataset() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }
}
