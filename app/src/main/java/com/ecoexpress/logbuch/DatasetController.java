package com.ecoexpress.logbuch;

import android.content.Context;
import android.content.ContextWrapper;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DatasetController extends ContextWrapper {
    private int[] userId;
    private DatabaseController database;
    private ArrayList<Location> locations;


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

    public void writeNewDataset(Location location, Date startTime, double latitude, double longitude) throws SQLException {
        Date endTime = new Date();
        int duration = duration(startTime, endTime);
        int distance = distance(latitude, longitude, location.getLatitude(), location.getLongitude());

        int datasetId = database.insertDataset(userId, (int) location.getId(), startTime, endTime, duration,
                latitude, longitude, distance);

        for (int i = 0; i < userId.length; i++) {
            database.insertUserInDataset(userId[i], datasetId);
        }
    }

    private int distance(double aLat, double aLong, double bLat, double bLong) {
        return 0;
    }

    private int duration(Date start, Date end) {
        return (int) ((end.getTime() / 1000) - (start.getTime() / 1000));
    }

    public Location nearestLocation(double latitude, double longitude) {
        return new Location(0, null, 0, 0);
    }


}
