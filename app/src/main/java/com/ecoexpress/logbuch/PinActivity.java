package com.ecoexpress.logbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PinActivity extends AppCompatActivity {
    private final static int pincode = 1234;
    private EditText etPinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        etPinCode =findViewById(R.id.et_pin);
    }

    public void onLogin(View view) {
        int input = Integer.parseInt(etPinCode.getText().toString());

        if (input == pincode) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}