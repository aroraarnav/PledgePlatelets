package com.example.pledgeplatelets;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DonorActivity extends AppCompatActivity {

    private FloatingActionButton settingsButton;

    @Override
    public void onBackPressed () {
        // Do nothing.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Donor Requests");

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}