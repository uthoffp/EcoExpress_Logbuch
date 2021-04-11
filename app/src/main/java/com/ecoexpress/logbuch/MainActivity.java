package com.ecoexpress.logbuch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private int pinCode;
    private Calendar calendar;
    private LocationManager locationManager;
    private DateFormat dateFormat;
    private DateFormat timeFormat;
    private Button previousBtn;
    private String time;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.pinCode = getIntent().getIntExtra("pinCode", 0);
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.timeFormat = new SimpleDateFormat("HH:mm");
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        requestLocationPermission();
    }

    public void onClick(View view) {
        //Check for Location Permission
        if (!hasLocationPermission()) {
            Toast.makeText(this,
                    "Unable to select location without required Permission. Please restart the application.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //set Button Color + Location
        Button btn = findViewById(view.getId());
        btn.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        String location = btn.getText().toString();
        if (previousBtn != null) {
            previousBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
        }
        previousBtn = btn;

        // Date + Time
        calendar = Calendar.getInstance();
        time = timeFormat.format(calendar.getTime());
        date = dateFormat.format(calendar.getTime());

        // GPS
        String strGpsLocation = "GPS not Found";
        Location gpsLocation = getCurrentLocation();
        if(gpsLocation != null) {
            String latitude = Double.toString(gpsLocation.getLatitude());
            String longitude = Double.toString(gpsLocation.getLongitude());
            strGpsLocation = latitude + ", " + longitude;
        }

        // Log Output
        String output = location + ", " + time + ", [GPS Placeholder]";
        Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT).show();
    }

    //Requests Permission for the Location of the current Device
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
    }

    private boolean hasLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private Location getCurrentLocation() {
        Location gps_loc = null;
        Location network_loc = null;
        Location final_loc = null;

        try {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
        } else if (network_loc != null) {
            final_loc = network_loc;
        }
        return final_loc;
    }
}