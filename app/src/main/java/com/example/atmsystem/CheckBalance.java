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

public class CheckBalance extends AppCompatActivity {
    private EditText security_pin;
    private int security;
    private int security_stored;
    private Button check_balance_buton;
    private TextView response;
    private FirebaseFirestore db;
    private String email;
    private Double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_balance_layout);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        Button return_home = findViewById(R.id.return_home_button);
        security_pin = findViewById(R.id.enter_security_check_balance);
        check_balance_buton = findViewById(R.id.check_balance_page_button);
        response = findViewById(R.id.check_balance_page_response);

        email = getIntent().getStringExtra("user_email");

        check_balance_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Parse the inputs
                try {
                    security = Integer.parseInt(security_pin.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(CheckBalance.this, "Please enter valid values.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch account details from Firestore
                db.collection("accounts")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                if (document != null) {
                                    int security_stored = document.getLong("securityPIN").intValue();

                                    if (security_stored == security) {
                                        Double current_balance = document.getDouble("initialAmount");
                                        response.setText("Current Balance = " +String.valueOf(current_balance));
                                        check_balance_buton.setVisibility(View.GONE);
                                    } else {
                                        response.setText("Wrong Security PIN.");
                                        security_pin.setText("");
                                    }
                                }
                            } else {
                                // If Firestore query fails
                                Toast.makeText(CheckBalance.this, "Error retrieving account details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                return_home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Start the home activity
                        Intent intent = new Intent(CheckBalance.this, Home.class);
                        intent.putExtra("user_email", email);
                        startActivity(intent);
                    }
                });
            }
        });
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
