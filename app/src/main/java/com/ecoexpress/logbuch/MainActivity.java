package com.ecoexpress.logbuch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private int pinCode;
    private Calendar calendar;
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
        this.dateFormat = new SimpleDateFormat("dd,MM,yyyy" );
        this.timeFormat = new SimpleDateFormat("HH:mm");
    }

    public void onClick(View view) {
        //set Button Color + Location
        Button btn = findViewById(view.getId());
        btn.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        String location = btn.getText().toString();
        if(previousBtn != null) {
            previousBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
        }
        previousBtn = btn;

        // Date + Time
        calendar = Calendar.getInstance();
        time = timeFormat.format(calendar.getTime());
        date = dateFormat.format(calendar.getTime());

        // Database insert
        Dataset dataset = new Dataset(date, time, location, "[GPS Placeholder]");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Logbuch");
        databaseReference.child(Integer.toString(pinCode)).child(dataset.getDate()).child(dataset.getTime()).setValue(dataset);

        // Log Output
        String output = location + ", " + time + ", [GPS Placeholder]";
        Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT).show();
    }
}