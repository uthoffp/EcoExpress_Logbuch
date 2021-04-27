package com.ecoexpress.logbuch;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public void writeNewDataset(Location location, Date startTime, double latitude, double longitude) {
        Date endTime = new Date();
        int duration = duration(startTime, endTime);
        int distance = distance(latitude, longitude, location.getLatitude(), location.getLongitude());
        location.setId(getLocationId(location.getName()));  //replace placeholder id

        try {
            int datasetId = database.insertDataset((int) location.getId(), startTime, endTime, duration,
                    latitude, longitude, distance);

            for (int value : userId) {
                database.insertUserInDataset(value, datasetId);
            }
            failCounter = 0;
        } catch (SQLException e) {
            e.printStackTrace();
            if(failCounter < 10) {
                failCounter++;
                database.initConnection();  //init connection again and retry
                writeNewDataset(location, startTime, latitude, longitude);
            } else {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private int distance(double aLat, double aLong, double bLat, double bLong) {
        return 0;
    }

    private int duration(Date start, Date end) {
        return (int) (((end.getTime() / 1000) - (start.getTime() / 1000)) / 60);
    }

    public Location nearestLocation(double latitude, double longitude) {
        return new Location(0, null, 0, 0);
    }

    private long getLocationId(String strLocation) {
        for(Location location : locations) {
            if (location.getName().equals(strLocation)) {
                return location.getId();
            }
        }
        return 0;
    }
}
