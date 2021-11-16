package com.example.pledgeplatelets;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Welcome");
    }

    @Override
    public void onBackPressed () {
        // Do nothing OnBackPressed
    }
}