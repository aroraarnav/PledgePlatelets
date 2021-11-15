package com.example.pledgeplatelets;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
    }
}