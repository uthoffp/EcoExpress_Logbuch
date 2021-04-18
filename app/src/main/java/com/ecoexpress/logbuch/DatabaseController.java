package com.ecoexpress.logbuch;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.StrictMode;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DatabaseController extends ContextWrapper {
    private Connection connection;
    private DateFormat dateFormat;
    private DateFormat timeFormat;

    public DatabaseController(Context context) {
        super(context);
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.timeFormat = new SimpleDateFormat("HH:mm");
        initConnection();
    }

    private void initConnection() {
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

    public int insertDataset(int[] employees, int locationId, Date date, Time time, int distance, Time duration) throws SQLException {
        Statement statement = connection.createStatement();
        int id = statement.executeUpdate("");
        statement.close();
        return id;
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

    public void testInsert() {
        try {
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate("INSERT INTO employee (name) VALUES ('TestUser')");
            statement.close();
            if (affectedRows > 0) {
                Toast.makeText(getApplicationContext(), "Insert successful", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}
