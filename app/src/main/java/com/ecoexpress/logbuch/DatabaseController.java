package com.ecoexpress.logbuch;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.StrictMode;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseController extends ContextWrapper {
    private Connection connection;

    public DatabaseController(Context context) {
        super(context);
        initConnection();
    }

    public void initConnection() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String url = "jdbc:jtds:sqlserver://"
                + getString(R.string.ip) + ":"
                + getString(R.string.port) + "/"
                + getString(R.string.database);

        try {
            //Class.forName(getString(R.string.classes));
            this.connection = DriverManager.getConnection(url, getString(R.string.username), getString(R.string.password));
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "Database connection failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public int insertDataset(int[] employees, int locationId, java.util.Date startTime, java.util.Date endTime,
                             int duration, double latitude, double longitude, int distance) throws SQLException {
        String insert = "INSERT INTO dataset (date, start_time, end_time, duration, latitude, longitude, distance, location_id)  " +
                "VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
        statement.setDate(1, new java.sql.Date(startTime.getTime()));
        statement.setTime(2, new Time(startTime.getTime()));
        statement.setTime(3, new Time(endTime.getTime()));
        statement.setInt(4, duration);
        statement.setDouble(5, latitude);
        statement.setDouble(6, longitude);
        statement.setInt(7, distance);
        statement.setInt(8, locationId);

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) return 0;

        ResultSet generatedKeys = statement.getGeneratedKeys();
        long id = 0;
        if(generatedKeys.next()){
            id = generatedKeys.getLong(1);
        }
        return (int) id;
    }

    public void insertUserInDataset(int userId, int locationId) throws SQLException {
        String insert = "INSERT INTO employee_in_dataset VALUES (?,?);";
        PreparedStatement statement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, userId);
        statement.setInt(2, locationId);
        statement.executeUpdate();
    }

    public HashMap<Integer, String> getEmployees() throws SQLException {
        HashMap<Integer, String> employees = new HashMap<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM employee");

        while (result.next()) {
            employees.put(result.getInt("id"), result.getString("name"));
        }
        result.close();
        statement.close();
        return employees;
    }

    public ArrayList<Location> getLocations() throws SQLException {
        ArrayList<Location> locations = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM location");

        while (result.next()) {
            locations.add(new Location(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getDouble("latitude"),
                    result.getDouble("longitude")));
        }
        result.close();
        statement.close();
        return locations;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testInsert() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO employee (name) VALUES(?)",
                Statement.RETURN_GENERATED_KEYS);
        String name = "testUserName123";
        statement.setString(1, name);
        // ...

        int affectedRows = statement.executeUpdate();

        if (affectedRows == 0) {
            return;
        }

        ResultSet generatedKeys = statement.getGeneratedKeys();
        long id = 0;
        if(generatedKeys.next()){
            id = generatedKeys.getLong(1);
        }
    }

}
