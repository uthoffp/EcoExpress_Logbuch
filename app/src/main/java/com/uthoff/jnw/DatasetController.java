package com.uthoff.jnw;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class DatasetController extends ContextWrapper {
    private final int[] userId;
    private final DatabaseController database;
    private ArrayList<Location> locations;
    private int failCounter;


    public DatasetController(Context context, int[] userId) {
        super(context);
        this.database = new DatabaseController(context);
        this.userId = userId;
        try {
            this.locations = database.getLocations();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean writeNewDataset(Location location, Date startTime, double latitude, double longitude, String activity) {
        Date endTime = new Date();
        int duration = duration(startTime, endTime);
        int distance = distance(latitude, longitude, location.getLatitude(), location.getLongitude());
        location.setId(getLocationId(location.getName()));  //replace placeholder id

        try {
            int datasetId = database.insertDataset((int) location.getId(), startTime, endTime, duration,
                    latitude, longitude, distance, activity);

            for (int value : userId) {
                database.insertUserInDataset(value, datasetId);
            }
            failCounter = 0;
        } catch (SQLException e) {
            e.printStackTrace();
            if (failCounter < 10) {
                failCounter++;
                database.initConnection();  //init connection again and retry
                return writeNewDataset(location, startTime, latitude, longitude, activity);
            } else {
                Toast.makeText(getApplicationContext(), "Internet Verbindung prüfen und erneut versuchen.", Toast.LENGTH_LONG).show();
                failCounter = 0;
            }
            return false;
        }
        return true;
    }

    private int distance(double aLat, double aLong, double bLat, double bLong) {
        if (aLat == 0 || aLong == 0 || bLat == 0 || bLong == 0) {
            return 0;
        }

        double theta = aLong - bLong;
        double dist = Math.sin(deg2rad(aLat)) * Math.sin(deg2rad(bLat)) + Math.cos(deg2rad(aLat)) * Math.cos(deg2rad(bLat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000;
        return (int) dist;
    }

    private int duration(Date start, Date end) {
        return (int) (((end.getTime() / 1000) - (start.getTime() / 1000)) / 60);
    }

    public Location nearestLocation(double gpsLat, double gpsLong) {
        int minDistance = Integer.MAX_VALUE;
        int tmpDistance = 0;
        Location result = new Location();

        for (Location location : locations) {
            tmpDistance = distance(gpsLat, gpsLong, location.getLatitude(), location.getLongitude());
            if (tmpDistance < minDistance && tmpDistance != 0) {
                minDistance = tmpDistance;
                result = location;
            }
        }
        return result;
    }

    private long getLocationId(String strLocation) {
        for (Location location : locations) {
            if (location.getName().equals(strLocation)) {
                return location.getId();
            }
        }
        return 0;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
