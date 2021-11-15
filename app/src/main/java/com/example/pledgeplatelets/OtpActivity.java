package com.example.pledgeplatelets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    // User Details
    private String name;
    private String phone;
    private String location;
    private String medicalHistory;
    private String birthday;

    // Items
    private EditText otpEditText;
    private Button otpButton;

    public String verificationCodeBySystem;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("OTP Verification");

        // Initialisations
        otpButton = (Button) findViewById(R.id.otpButton);
        otpEditText = (EditText) findViewById(R.id.otpEditText);

        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        location = getIntent().getStringExtra("location");
        medicalHistory = getIntent().getStringExtra("medicalHistory");
        birthday = getIntent().getStringExtra("birthday");

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
                            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                        } else {
                            // Failed.
                            Toast.makeText(getApplicationContext(), "The OTP entered is incorrect, please try again.", Toast.LENGTH_LONG).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }
}