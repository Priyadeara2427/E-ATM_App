package com.example.atmsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class Registration extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button submitButton, loginButton, sendOTP, verifyOtpButton;
    private TextView textView;
    private EditText full_name_text, email_text, password_text, OTP;
    private String email;
    private String generatedOTP; // Store OTP as a class-level variable
    private int isValidOTP = 0;

    public int generateOTP() {
        Random ran = new Random();
        return 1000 + ran.nextInt(9000); // Generate 4-digit OTP
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        db = FirebaseFirestore.getInstance();

        submitButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.button_login);
        sendOTP = findViewById(R.id.sendOTP);
        verifyOtpButton = findViewById(R.id.verify_otp_button);
        textView = findViewById(R.id.textView5);
        full_name_text = findViewById(R.id.enter_full_name_registration);
        email_text = findViewById(R.id.enter_email_register);
        password_text = findViewById(R.id.enter_password_register);
        OTP = findViewById(R.id.edit_OTP);

        loginButton.setVisibility(View.GONE);
        verifyOtpButton.setVisibility(View.GONE);

        SendEmail send = new SendEmail();

        // OTP verification logic
        verifyOtpButton.setOnClickListener(v -> {
            String input = OTP.getText().toString();
            if (input.isEmpty()) {
                textView.setText("Please enter OTP!");
            } else if (input.equals(generatedOTP)) {
                isValidOTP = 1;
                textView.setText("OTP Verified");
                OTP.setText(""); // Clear OTP field after verification
            } else {
                textView.setText("Wrong OTP. Try again.");
                OTP.setText("");
            }
        });

        // Registration logic
        sendOTP.setOnClickListener(v -> {
            email = email_text.getText().toString();
            if (email.isEmpty()) {
                textView.setText("Please enter an email.");
            } else if (!email.contains("@")) {
                textView.setText("Invalid email format.");
            } else {
                String password = password_text.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                generatedOTP = String.valueOf(generateOTP()); // Generate OTP
                                send.sendEmail(email, generatedOTP); // Send OTP to email
                                textView.setText("OTP sent to your email successfully.");
                                sendOTP.setVisibility(View.GONE);
                                verifyOtpButton.setVisibility(View.VISIBLE);
                            } else {
                                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                textView.setText("Registration failed: " + e.getMessage());
                            }
                        });
            }
        });

        // Submit button logic
        submitButton.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            textView.setText("");
            if (user != null && isValidOTP == 1) {
                user.sendEmailVerification()
                        .addOnCompleteListener(verificationTask -> {
                            if (verificationTask.isSuccessful()) {
                                textView.setText("Registration successful! Please verify your email.");
                                submitButton.setVisibility(View.GONE);
                                loginButton.setVisibility(View.VISIBLE);
                            } else {
                                textView.setText("Failed to send verification email.");
                            }
                        });
            } else {
                textView.setText("OTP is not verified.");
            }
        });

        // Login button logic
        loginButton.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (user.isEmailVerified()) {
                            Intent intent = new Intent(Registration.this, OpenAccount.class);
                            startActivity(intent);
                            finish();
                        } else {
                            textView.setText("Please verify your email before proceeding.");
                        }
                    } else {
                        textView.setText("Error verifying email. Try again.");
                    }
                });
            } else {
                textView.setText("No user is logged in.");
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Navigate to Home activity and clear any intermediate stack
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
