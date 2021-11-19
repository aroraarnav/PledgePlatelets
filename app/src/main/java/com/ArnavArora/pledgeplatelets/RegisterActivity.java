package com.ArnavArora.pledgeplatelets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ArnavArora.pledgeplatelets.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private DatabaseReference reference;
    private String selectedLocation;
    private String selectedBirthDay;

    // Items
    private Spinner locationSpinner;

    private EditText birthdayEditText;
    private EditText medicalHistoryEditText;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText localityEditText;

    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialisations
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);

        birthdayEditText = (EditText) findViewById(R.id.birthdayEditText);
        medicalHistoryEditText = (EditText) findViewById(R.id.medicalHistoryEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        localityEditText = (EditText) findViewById(R.id.localityEditText);

        registerButton = (Button) findViewById(R.id.registerButton);

        // Registration
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToOTP ();
            }
        });

        // Extracting available locations
        final ArrayList <String> locationList = new ArrayList <> ();
        final ArrayAdapter adapter = new ArrayAdapter <String> (this, R.layout.spinner_item, locationList);
        locationSpinner.setAdapter(adapter);
        locationSpinner.setOnItemSelectedListener(this);

        reference = FirebaseDatabase.getInstance().getReference().child("Donors");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    locationList.add (snapshot.getKey());
                }

                // Please select addition
                locationList.add (0, "SELECT YOUR LOCATION");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Register to Donate Platelets");

    }

    public void proceedToOTP () {
        // Data
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String age = birthdayEditText.getText().toString();
        String medicalHistory = medicalHistoryEditText.getText().toString();
        String locality = localityEditText.getText().toString();

        if (medicalHistory.replaceAll("\\s+", "").equals("")) {
            medicalHistory = "None";
        }

        if (name.equals("") || phone.equals("") || age.equals("") || selectedLocation.equals("SELECT YOUR LOCATION") || locality.equals("")) {
            Toast.makeText(this, "One or more fields have been left empty. Please fill all required fields.", Toast.LENGTH_LONG).show();
        } else if (phone.length() != 10) {
            Toast.makeText(this, "The phone number entered is invalid. Please try again.", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(age) < 18 || Integer.parseInt(age) > 65 ) {
            Toast.makeText(this, "You do not meet the age criteria to donate.", Toast.LENGTH_SHORT).show();
        } else {
                // All details have been entered successfully
                Intent intent = new Intent(getApplicationContext(), OtpActivity.class);

                intent.putExtra("name", name);
                intent.putExtra("phone", ("+91" + phone));
                intent.putExtra("age", age);
                intent.putExtra("location", selectedLocation);
                intent.putExtra("medicalHistory", medicalHistory);
                intent.putExtra("locality", locality);

                startActivity(intent);

            }
        }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedLocation = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectedBirthDay = dayOfMonth + "-" + (month + 1) + "-" + year;
        birthdayEditText.setText(selectedBirthDay);
    }
}