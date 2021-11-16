package com.example.pledgeplatelets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ListView listView;
    private Spinner adminSpinner;
    private DatabaseReference reference;

    private ArrayList<String> donorNames;
    private ArrayList<String> donorLocality;
    private ArrayList<String> donorHistory;
    private ArrayList<String> donorPhones;
    private ArrayList<String> donorKeys;
    private ArrayList<String> donorBirthdays;

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
        donorBirthdays = new ArrayList<String> ();

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

            donorNames.clear();
            donorBirthdays.clear();
            donorKeys.clear();
            donorPhones.clear();
            donorLocality.clear();
            donorHistory.clear();

            reference = FirebaseDatabase.getInstance().getReference().child("Donors").child(location);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                        String name = dataSnapshot.child("Name").getValue().toString();
                        String phone = dataSnapshot.child("Phone").getValue().toString();
                        String history = dataSnapshot.child("Medical History").getValue().toString();
                        String locality = dataSnapshot.child("Locality").getValue().toString();
                        String key = dataSnapshot.child("Key").getValue().toString();
                        String birthday = dataSnapshot.child("Birthday").getValue().toString();

                        donorNames.add(name);
                        donorLocality.add(locality);
                        donorHistory.add(history);
                        donorPhones.add(phone);
                        donorKeys.add(key);
                        donorBirthdays.add(birthday);

                    }

                    // Adapter

                    String [] namesArr = new String[donorNames.size()];
                    namesArr = donorNames.toArray(namesArr);

                    String [] birthdaysArr = new String[donorBirthdays.size()];
                    birthdaysArr = donorBirthdays.toArray(birthdaysArr);

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
                            // Activity Shift
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }

    @Override
    public void onBackPressed () {
        // Do nothing OnBackPressed
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedLocation = parent.getItemAtPosition(position).toString();
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
            birthday.setText("Birthday: " + birthdays[position]);

            return row;
        }
    }
}