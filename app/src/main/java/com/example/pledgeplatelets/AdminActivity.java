package com.example.pledgeplatelets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ListView listView;
    private Spinner adminSpinner;
    private DatabaseReference reference;

    // ArrayLists
    private ArrayList<String> donorNames;
    private ArrayList<String> donorLocality;
    private ArrayList<String> donorHistory;
    private ArrayList<String> donorPhones;
    private ArrayList<String> donorKeys;
    private ArrayList<String> donorAges;

    // Strings
    String selectedLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Welcome");

        adminSpinner = (Spinner) findViewById(R.id.adminSpinner);
        listView = (ListView) findViewById(R.id.donorListView);



        donorNames = new ArrayList<String> ();
        donorHistory = new ArrayList<String> ();
        donorLocality = new ArrayList<String> ();
        donorPhones = new ArrayList<String> ();
        donorKeys = new ArrayList<String> ();
        donorAges = new ArrayList<String> ();

        // Extracting Locations
        // Extracting available locations
        final ArrayList<String> locationList = new ArrayList <> ();
        final ArrayAdapter adapter = new ArrayAdapter <String> (this, R.layout.spinner_item, locationList);
        adminSpinner.setAdapter(adapter);
        adminSpinner.setOnItemSelectedListener(this);

        reference = FirebaseDatabase.getInstance().getReference().child("Donors");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                locationList.clear();

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
    }

    private void locationSelected (String location) {

        if (!location.equals("SELECT YOUR LOCATION")) {

            clearLists();

            reference = FirebaseDatabase.getInstance().getReference().child("Donors").child(location);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    clearLists();

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        String name = dataSnapshot.child("Name").getValue().toString();
                        String phone = dataSnapshot.child("Phone").getValue().toString();
                        String history = dataSnapshot.child("Medical History").getValue().toString();
                        String locality = dataSnapshot.child("Locality").getValue().toString();
                        String key = dataSnapshot.child("Key").getValue().toString();
                        String age = dataSnapshot.child("Age").getValue().toString();

                        donorNames.add(name);
                        donorLocality.add(locality);
                        donorHistory.add(history);
                        donorPhones.add(phone);
                        donorKeys.add(key);
                        donorAges.add(age);

                    }

                    // Adapter

                    String [] namesArr = new String[donorNames.size()];
                    namesArr = donorNames.toArray(namesArr);

                    String [] birthdaysArr = new String[donorAges.size()];
                    birthdaysArr = donorAges.toArray(birthdaysArr);

                    String [] historiesArr = new String[donorHistory.size()];
                    historiesArr = donorHistory.toArray(historiesArr);

                    String [] phonesArr = new String[donorPhones.size()];
                    phonesArr = donorPhones.toArray(phonesArr);

                    String [] localitiesArr = new String[donorLocality.size()];
                    localitiesArr = donorLocality.toArray(localitiesArr);

                    String [] keysArr = new String[donorKeys.size()];
                    keysArr = donorKeys.toArray(keysArr);

                    DonorAdapter adapter = new DonorAdapter(getApplicationContext(), namesArr, phonesArr, localitiesArr, historiesArr, keysArr, birthdaysArr);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            String name = donorNames.get(position);
                            String phone = donorPhones.get(position);

                            // Show alert to send requests
                            new AlertDialog.Builder(AdminActivity.this)
                                    .setTitle("Make a phone call to " + name + "?")
                                    .setMessage("You will be connected to " + name + " via a phone call immediately.")

                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            makeCall (phone);
                                        }
                                    })

                                    // Null Listener
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_menu_call)
                                    .show();

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }

    public void clearLists () {
        donorNames.clear();
        donorAges.clear();
        donorKeys.clear();
        donorPhones.clear();
        donorLocality.clear();
        donorHistory.clear();
    }

    public void makeCall (String phone) {

        // Making Phone Call
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    123);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + phone)));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {



                } else {
                    Toast.makeText(this, "The permission to call was denied. Please go to Settings > Pledge Platelets to allow calls.", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed () {
        // Do nothing OnBackPressed
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedLocation = parent.getItemAtPosition(position).toString();
        locationSelected(selectedLocation);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    class DonorAdapter extends ArrayAdapter<String> {

        Context context;
        String names [];
        String localities [];
        String histories [];
        String phones [];
        String keys [];
        String birthdays [];

        DonorAdapter (Context c, String name [], String phone [], String locality [], String history [], String key [], String birthday [] ) {
            super(c, R.layout.row, R.id.nameTextView, name);

            this.context = c;
            this.names = name;
            this.phones = phone;
            this.histories = history;
            this.localities = locality;
            this.keys = key;
            this.birthdays = birthday;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);

            TextView name = row.findViewById(R.id.nameTextView);
            TextView history = row.findViewById(R.id.medicalHistoryTextView);
            TextView locality = row.findViewById(R.id.localityTextView);
            TextView birthday = row.findViewById(R.id.birthdayTextView);

            // Setting Views
            name.setText(names[position]);
            locality.setText("Locality: " + localities[position]);
            history.setText("Medical History: " + histories[position]);
            birthday.setText("Age: " + birthdays[position] + " years old");

            return row;
        }
    }
}