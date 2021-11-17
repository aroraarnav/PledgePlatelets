package com.example.pledgeplatelets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    // User Details
    private String name;
    private String phone;
    private String location;
    private String medicalHistory;
    private String age;
    private String locality;

    // Items
    private EditText otpEditText;
    private Button otpButton;

    public String verificationCodeBySystem;
    public String userKey;

    FirebaseAuth mAuth;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        reference = FirebaseDatabase.getInstance().getReference();

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("OTP Verification");

        // Initialisations
        otpButton = (Button) findViewById(R.id.otpButton);
        otpEditText = (EditText) findViewById(R.id.otpEditText);

        name = getIntent().getStringExtra("name");
        locality = getIntent().getStringExtra("locality");
        phone = getIntent().getStringExtra("phone");
        location = getIntent().getStringExtra("location");
        medicalHistory = getIntent().getStringExtra("medicalHistory");
        age = getIntent().getStringExtra("age");

        mAuth = FirebaseAuth.getInstance();
        sendVerificationCode (phone);

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(otpEditText.getText().toString());
            }
        });

    }

    private void sendVerificationCode (String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        // Code has been sent
                        verificationCodeBySystem = s;

                    }
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        verifyCode(code);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    private void verifyCode (String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInWithPhoneAuthCredential (credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Success, sign in.
                            successfulRegistration ();
                        } else {
                            // Failed.
                            Toast.makeText(getApplicationContext(), "The OTP entered is incorrect, please try again.", Toast.LENGTH_LONG).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

    public void successfulRegistration () {
        // Entering user details on Firebase
        userKey = reference.child("Donors").child(location).push().getKey();

        reference.child("Donors").child(location).child(userKey).child("Name").setValue(name);
        reference.child("Donors").child(location).child(userKey).child("Locality").setValue(locality);
        reference.child("Donors").child(location).child(userKey).child("Key").setValue(userKey);
        reference.child("Donors").child(location).child(userKey).child("Age").setValue(age);
        reference.child("Donors").child(location).child(userKey).child("Phone").setValue(phone);
        reference.child("Donors").child(location).child(userKey).child("Medical History").setValue(medicalHistory);

        // Saving donor login session
        SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
        editor.putString("key", userKey).apply();
        editor.putString("location", location).apply();
        editor.putBoolean("loggedInDonor", true).apply();

        // Donor screen
        Intent intent = new Intent(this, DonorActivity.class);
        startActivity(intent);

    }
}