package com.example.pledgeplatelets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonorActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private String donorKey;
    private String location;
    private DatabaseReference reference;

    private Button optOutButton;
    private Button websiteButton;

    @Override
    public void onBackPressed () {
        // Do nothing.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        optOutButton = (Button) findViewById(R.id.optOutButton);

        donorKey = getSharedPreferences("login", MODE_PRIVATE).getString("key", "");
        location = getSharedPreferences("login", MODE_PRIVATE).getString("location", "");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Name").getValue().toString();
                welcomeTextView.setText("Welcome, \n" +  name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        reference = FirebaseDatabase.getInstance().getReference().child("Donors").child(location).child(donorKey);
        reference.addValueEventListener(listener);

        optOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show alert
                new AlertDialog.Builder(DonorActivity.this)
                        .setTitle("Are you sure you want to opt out?")
                        .setMessage("By staying registered, you can help a patient in need.")

                        .setPositiveButton("Opt Out", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                reference.removeEventListener(listener);

                                FirebaseDatabase.getInstance().getReference().child("Donors").child(location).child(donorKey)
                                .removeValue();


                                SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                                editor.putString("key", null).apply();
                                editor.putString("location", null).apply();
                                editor.putBoolean("loggedInDonor", false).apply();

                                Intent intent = new Intent (DonorActivity.this, MainActivity.class);
                                startActivity(intent);


                            }
                        })

                        // Null Listener
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Thank you for Registering!");

    }
}