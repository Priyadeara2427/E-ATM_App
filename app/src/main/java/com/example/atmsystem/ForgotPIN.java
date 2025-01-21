package com.example.atmsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class ForgotPIN extends AppCompatActivity {
    private Button forgotPINButton, HomeButton;
    private TextView OTP_response, resultText;
    private String email, school_name, pet_name, generatedOTP;
    private EditText OTP, new_security_text, school_name_text, pet_name_text;
    private int  new_security_pin, isValidOTP = 0;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pin_layout);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get user email from intent
        email = getIntent().getStringExtra("user_email");

        // Initialize Views
        forgotPINButton = findViewById(R.id.forgot_pin_page_button);
        HomeButton = findViewById(R.id.return_home_button);
        OTP_response = findViewById(R.id.otp_response);
        OTP = findViewById(R.id.enter_otp);
        new_security_text = findViewById(R.id.enter_new_security);
        school_name_text = findViewById(R.id.enter_school_name_forgot_pin_page);
        pet_name_text = findViewById(R.id.enter_pet_name_forgot_pin);
        resultText = findViewById(R.id.forgot_pin_page_response);

        Button verify_otp = findViewById(R.id.verify_otp_button_forgot_pin_page);
        Button sendOTP = findViewById(R.id.send_otp_button_forgot);

        // Hide verify OTP button initially
        verify_otp.setVisibility(View.GONE);

        // Send OTP Button
        sendOTP.setOnClickListener(v -> {
            db.collection("accounts")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            int securityStored = document.getLong("securityPIN").intValue();
                            String status = document.getString("accountStatus");

                            if ("Locked".equals(status)) {
                                resultText.setText("This account is locked. Security PIN can't be changed.");
                                return;
                            }

                            generatedOTP = String.valueOf(generateOTP());
                            SendEmail send = new SendEmail();
                            send.sendEmail(email, generatedOTP);
                            OTP_response.setText("OTP is sent to your email successfully.");
                            sendOTP.setVisibility(View.GONE);
                            verify_otp.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(this, "No matching account found.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Verify OTP Button
        verify_otp.setOnClickListener(v -> {
            String input = OTP.getText().toString().trim();
            if (input.isEmpty()) {
                OTP_response.setText("Please enter the OTP.");
            } else if (input.equals(generatedOTP)) {
                isValidOTP = 1;
                OTP_response.setText("OTP Verified.");
            } else {
                OTP_response.setText("Incorrect OTP.");
                OTP.setText("");
            }
        });

        // Change PIN Button
        forgotPINButton.setOnClickListener(v -> {
            if (isValidOTP != 1) {
                OTP_response.setText("OTP is not verified.");
                return;
            }

            String newSecurityInput = new_security_text.getText().toString().trim();
            school_name = school_name_text.getText().toString().trim();
            pet_name = pet_name_text.getText().toString().trim();

            if (newSecurityInput.isEmpty() || school_name.isEmpty() || pet_name.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                new_security_pin = Integer.parseInt(newSecurityInput);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid new security PIN format.", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("accounts")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            int securityStored = document.getLong("securityPIN").intValue();
                            String status = document.getString("accountStatus");
                            String schoolNameStored = document.getString("schoolName");
                            String petNameStored = document.getString("petName");

                            if ("Locked".equals(status)) {
                                resultText.setText("This account is locked. Transaction can't be done.");
                            } else if (schoolNameStored.equals(school_name) &&
                                    petNameStored.equals(pet_name)) {
                                db.collection("accounts")
                                        .document(document.getId())
                                        .update("securityPIN", new_security_pin)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                resultText.setText("Security PIN changed successfully.");
                                                forgotPINButton.setVisibility(View.GONE);
                                            } else {
                                                resultText.setText("Failed to change PIN: " + updateTask.getException().getMessage());
                                            }
                                        });
                            } else {
                                resultText.setText("Security questions failed to verify.");
                            }
                        } else {
                            Toast.makeText(this, "No matching account found.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Home Button
        HomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPIN.this, Home.class);
            intent.putExtra("user_email", email);
            startActivity(intent);
        });
    }

    // Generate OTP
    private int generateOTP() {
        Random ran = new Random();
        return 1000 + ran.nextInt(9000); // Generate 4-digit OTP
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Navigate to Home activity and clear any intermediate stack
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
