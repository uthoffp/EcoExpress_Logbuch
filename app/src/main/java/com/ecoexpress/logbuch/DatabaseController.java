package com.ecoexpress.logbuch;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseController extends ContextWrapper {
    private Connection connection;

    public DatabaseController(Context context) {
        super(context);
        initConnection();
    }

    private void initConnection() {
        String url = "jdbc:jtds:sqlserver://"
                + getString(R.string.ip) + ":"
                + getString(R.string.port) + "/"
                + getString(R.string.database);

        try {
            Class.forName(getString(R.string.classes));
            this.connection = DriverManager.getConnection(url, getString(R.string.username), getString(R.string.password));
        } catch (ClassNotFoundException | SQLException e) {
            Toast.makeText(getApplicationContext(), "Database connection failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public int insertDataset() {
        return 0;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
