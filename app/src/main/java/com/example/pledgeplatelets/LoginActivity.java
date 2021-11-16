package com.example.pledgeplatelets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button createButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private DatabaseReference reference;

    private ArrayList<String> emails;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisations
        emails = new ArrayList<String>();

        reference = FirebaseDatabase.getInstance().getReference();
        createButton = (Button) findViewById(R.id.createButton);
        loginButton = (Button) findViewById(R.id.loginAsAdminButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.editTextTextPassword);

        // Getting all emails
        reference.child("Healthcare").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    emails.add(dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://forms.gle/dTLYdQPFcb44fHhbA";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Final Email (inner method access)
                final String email = emailEditText.getText().toString().replace("@", "_").replace(".", "_");

                String password = passwordEditText.getText().toString();

                if (email.replaceAll("\\s+", "").equals("") || password.replaceAll("\\s+", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "One or more fields have been left empty. Please try again.", Toast.LENGTH_LONG).show();
                } else if (!emails.contains(email)) {
                    Toast.makeText(getApplicationContext(), "The email address hasn't been registered. Please register for an account before logging in.", Toast.LENGTH_LONG).show();
                } else {

                    reference.child("Healthcare").child(email).child("Password").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userPassword = snapshot.getValue().toString();

                            if (userPassword.equals(password)) {
                                // Success, take them to the Admin Screen, and store email in SharedPreferences

                                // Saving admin login session
                                SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                                editor.putString("email", email).apply();
                                editor.putBoolean("loggedInAdmin", true).apply();

                                // Send to Admin Page
                                Intent intent = new Intent (getApplicationContext(), AdminActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(getApplicationContext(), "The password was incorrect. Please try again!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login as Admin");
    }
}