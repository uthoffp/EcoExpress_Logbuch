package com.ecoexpress.logbuch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {
    private ArrayList<Integer> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        users = new ArrayList<>();
    }

    public void onSelectUser(View view) {
        Button btn = findViewById(view.getId());
        String location = btn.getText().toString();
        int id = 0;

        switch (location) {
            case "Marcel":
                id = 4;
                break;
            case "Jana":
                id = 10;
                break;
            case "Bogicevic":
                id = 11;
                break;
            case "Hofmann":
                id = 12;
                break;
            case "Boufaied":
                id = 13;
                break;
        }

        if (users.contains(id)) {
            for (int i = 0; i < users.size(); i++) {
                if(users.get(i) == id) {
                    users.remove(i);
                    btn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                    break;
                }
            }
        } else {
            users.add(id);
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }
    }

    public void onContinue(View view) {
        if(users.isEmpty()) {
            Toast.makeText(this, "Es muss mindestens ein Mitarbeiter ausgewählt werden!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        int[] userarr = new int[users.size()];
        for (int i = 0; i < users.size(); i++) {
            userarr[i] = users.get(i);
        }
        intent.putExtra("userIds", userarr);
        startActivity(intent);
    }
}