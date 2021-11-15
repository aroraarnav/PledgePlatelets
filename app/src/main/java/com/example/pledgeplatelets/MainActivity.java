package com.example.pledgeplatelets;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // Buttons
    private Button adminButton;
    private Button donorButton;
    private Button policyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Welcome to Pledge Platelets");

        // Initialise Variables
        adminButton = (Button) findViewById(R.id.adminButton);
        donorButton = (Button) findViewById(R.id.donorButton);
        policyButton = (Button) findViewById(R.id.policyButton);

        policyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://www.websitepolicies.com/policies/view/8kjEq1Yj";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
        });

        donorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity (intent);
            }
        });

    }
}