package com.uthoff.jnw;

import androidx.appcompat.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private Button btnAway;
    private DatasetController datasetController;
    private long awayTime;
    private com.uthoff.jnw.Location awayLoc;
    private Button btnPrev;
    private boolean first;
    private boolean logout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLocationPermission();
        int[] userIds = getIntent().getIntArrayExtra("userIds");
        this.first = true;
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.datasetController = new DatasetController(getApplicationContext(), userIds);
        this.btnAway = findViewById(R.id.unterwegs);
        this.btnAway.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        this.awayTime = new Date().getTime();
        this.awayLoc = new com.uthoff.jnw.Location(1, "Unterwegs", 0, 0);
    }

    public void onClick(View view) {
        //Check for Location Permission
        if (!hasLocationPermission()) {
            Toast.makeText(this,
                    "Unable to select location without required Permission. Please restart the application.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Insert away Time to db
        boolean dbResult;
        if (first) {
            dbResult = datasetController.writeNewDataset(awayLoc, new Date(awayTime), 0, 0, "Arbeitsbeginn");
            first = false;
        } else {
            dbResult = datasetController.writeNewDataset(awayLoc, new Date(awayTime), 0, 0, null);
        }

        if (!dbResult) return;

        // set Button Color + Location
        Button btn = findViewById(view.getId());
        btn.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        btnAway.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
        String location = btn.getText().toString();
        btnPrev = btn;
        openDialog(location);
    }

    private void openDialog(String strLocation) {
        // GPS
        Location gpsLocation = getCurrentLocation();
        double latitude = 0;
        double longitude = 0;
        if (gpsLocation != null) {
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();
        }

        // Get nearest Location wenn Waschsalon ausgewaehlt
        com.uthoff.jnw.Location location = new com.uthoff.jnw.Location();
        if (strLocation.equals("Waschsalon")) {
            location = datasetController.nearestLocation(latitude, longitude);
            strLocation = location.getName();
        } else {
            location.setId(1);
            location.setName(strLocation);
            location.setLatitude(0);
            location.setLongitude(0);
        }

        // create Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_location, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        // Time
        Date date = Calendar.getInstance().getTime();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String strDate = timeFormat.format(date);

        // set Widgets
        TextView txtLocation = dialogView.findViewById(R.id.dialog_txt_location);
        txtLocation.setText(strLocation);
        TextView txtDate = dialogView.findViewById(R.id.dialog_txt_time);
        txtDate.setText(strDate + " Uhr");
        Spinner spinner = dialogView.findViewById(R.id.spinner);
        spinner.setPrompt("Tätigkeit auswählen");
        Button btnContinue = dialogView.findViewById(R.id.dialog_btn_continue);

        // continue btn click
        double finalLatitude = latitude;
        double finalLongitude = longitude;
        com.uthoff.jnw.Location finalLocation = location;
        btnContinue.setOnClickListener(v -> {
            String activity = spinner.getSelectedItem().toString();
            if (!activity.equals("nicht ausgewählt")) {
                new AlertDialog.Builder(this)   //application context crashes
                        .setTitle("Wieder Unterwegs?")
                        .setPositiveButton("Weiter", (dialog, which) -> {
                            boolean dbResult = datasetController.writeNewDataset(finalLocation, date, finalLatitude, finalLongitude, activity);
                            if (dbResult) {
                                awayTime = new Date().getTime();
                                btnPrev.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                                btnAway.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                                alertDialog.dismiss();
                            } else {

                            }
                        })
                        .setNegativeButton("Abbrechen", null)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), "Bitte eine Tätigkeit auswählen.", Toast.LENGTH_LONG).show();
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logoutmenue, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_logout) {
            new AlertDialog.Builder(this)   //application context crashes
                    .setTitle("Feierabend machen?")
                    .setPositiveButton("Ja", (dialog, which) -> {
                        if (!first) {
                            boolean dbResult = datasetController.writeNewDataset(new com.uthoff.jnw.Location(1, "Unterwegs", 0, 0), new Date(awayTime), 0, 0, "Feierabend");
                            if (!dbResult) return;
                        }
                        logout = true;
                        onBackPressed();
                    })
                    .setNegativeButton("Abbrechen", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (logout) {
            super.onBackPressed();
        }
    }
}