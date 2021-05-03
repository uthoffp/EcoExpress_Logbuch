package com.uthoff.logbuch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class StartActivity extends AppCompatActivity {
    private ArrayList<Integer> userIds;
    private HashMap<Integer, String> employees;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionName += " " + getString(R.string.company_name);
            TextView txtVersion = findViewById(R.id.start_txt_version);
            txtVersion.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        this.employees = new HashMap<>();
        this.userIds = new ArrayList<>();
        this.layout = findViewById(R.id.start_layout_employees);

        getEmployeesFromDatabase();
    }

    public void onSelectUser(View view) {
        int id = view.getId();
        Button btn = findViewById(view.getId());

        if (userIds.contains(id)) {
            for (int i = 0; i < userIds.size(); i++) {
                if (userIds.get(i) == id) {
                    userIds.remove(i);  //remove user if selected
                    btn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                    break;
                }
            }
        } else {
            userIds.add(id);    //add user
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }
    }

    private void getEmployeesFromDatabase() {
        //get employee HashMap with id and name
        DatabaseController database = new DatabaseController(getApplicationContext());
        try {
            employees = database.getEmployees();
            database.close();
        } catch (SQLException | NullPointerException e) {
            Toast.makeText(this, "Internet Verbindung prüfen und aktualisieren.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        //add Layout Margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, 24);

        //Add Button for each employee with Button id = user id
        for (int id : employees.keySet()) {
            MaterialButton btn = new MaterialButton(this);
            btn.setId(id);
            btn.setLayoutParams(params);
            btn.setText(employees.get(id));
            btn.setOnClickListener(this::onSelectUser);
            layout.addView(btn);
        }
    }

    public void onContinue(View view) {
        //pass array of user ids to MainActivity
        if (userIds.isEmpty()) {
            Toast.makeText(this, "Es muss mindestens ein Mitarbeiter ausgewählt werden!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        int[] userarr = new int[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            userarr[i] = userIds.get(i);
        }
        intent.putExtra("userIds", userarr);
        startActivity(intent);
    }

    public void onClickRefresh(View view) {
        //remove all buttons and reload
        layout.removeAllViews();
        getEmployeesFromDatabase();
    }

    @Override
    public void onBackPressed() {
        //prevent onBackPressed
        //super.onBackPressed();
    }
}