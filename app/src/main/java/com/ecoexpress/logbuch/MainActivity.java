package com.ecoexpress.logbuch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private Button previousBtn;
    private DatasetController datasetController;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLocationPermission();

        int[] userIds = getIntent().getIntArrayExtra("userIds");
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.datasetController = new DatasetController(getApplicationContext(), userIds);
        this.calendar = Calendar.getInstance();
    }

    public void onClick(View view) {
        //Check for Location Permission
        if (!hasLocationPermission()) {
            Toast.makeText(this,
                    "Unable to select location without required Permission. Please restart the application.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // set Button Color + Location
        Button btn = findViewById(view.getId());
        btn.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        String location = btn.getText().toString();
        if (previousBtn != null) {
            previousBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
        }
        previousBtn = btn;

        openDialog(location);
    }

    private void openDialog(String strLocation) {
        // create Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_location, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // GPS
        Location gpsLocation = getCurrentLocation();
        if (gpsLocation != null) {
            double latitude = gpsLocation.getLatitude();
            double longitude = gpsLocation.getLongitude();
        }

        // Time
        Date date = Calendar.getInstance().getTime();
        DateFormat timeFormat = new SimpleDateFormat("hh:mm");
        String strDate = timeFormat.format(date);

        // set Widgets
        TextView txtLocation = dialogView.findViewById(R.id.dialog_txt_location);
        txtLocation.setText(strLocation);
        TextView txtDate = dialogView.findViewById(R.id.dialog_txt_time);
        txtDate.setText(strDate + " Uhr");
        Button btnContinue = dialogView.findViewById(R.id.dialog_btn_continue);
        btnContinue.setOnClickListener(v -> {
            new AlertDialog.Builder(this)   //application context crashes
                    .setTitle("Wieder Unterwegs?")
                    .setPositiveButton("Weiter", (dialog, which) -> {
                        
                        alertDialog.dismiss();
                    })
                    .setNegativeButton("Abbrechen", null)
                    .show();
        });
    }

    //requests permission for the location of the current device
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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