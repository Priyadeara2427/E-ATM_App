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

public class WithdrawAmount extends AppCompatActivity {
    private EditText withdraw_amount;
    private Double amount;
    private EditText security_pin;
    private int security;
    private TextView response;
    private String email;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_amount_layout);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        Button return_home = findViewById(R.id.return_home_button);
        withdraw_amount = findViewById(R.id.enter_debit_amount);
        security_pin = findViewById(R.id.enter_security_debit);
        response = findViewById(R.id.debit_page_response);
        Button deposit_money_button = findViewById(R.id.debit_page_button);

        email = getIntent().getStringExtra("user_email");

        deposit_money_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Parse the inputs
                try {
                    security = Integer.parseInt(security_pin.getText().toString().trim());
                    amount = Double.parseDouble(withdraw_amount.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(WithdrawAmount.this, "Please enter valid values.", Toast.LENGTH_SHORT).show();
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
                                    String status = document.getString("accountStatus");

                                    if ("Locked".equals(status)) {
                                        response.setText("This account is locked. Transaction can't be done.");
                                    } else if (security_stored == security) {
                                        Double current_balance = document.getDouble("initialAmount");
                                        if (current_balance != null) {
                                            Double new_balance = current_balance - amount;
                                            if (new_balance >= 2000.0) {
                                                db.collection("accounts")
                                                        .document(document.getId())
                                                        .update("initialAmount", new_balance)
                                                        .addOnCompleteListener(updateTask -> {
                                                            if (updateTask.isSuccessful()) {
                                                                deposit_money_button.setVisibility(View.GONE);
                                                                response.setText("Rs. " + amount + " is debitted.");
                                                            } else {
                                                                response.setText("Failed to update balance: " + updateTask.getException().getMessage());
                                                            }
                                                        });
                                            }
                                            else if (new_balance < 2000.0)
                                            {
                                                response.setText("Insufficient balance to withdraw i.e. Rs. "+ current_balance);
                                                withdraw_amount.setText("");
                                                security_pin.setText("");
                                            }
                                        }
                                    } else {
                                        response.setText("Wrong Security PIN.");
                                        security_pin.setText("");
                                    }
                                }
                            } else {
                                // If Firestore query fails
                                Toast.makeText(WithdrawAmount.this, "Error retrieving account details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                return_home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Start the home activity
                        Intent intent = new Intent(WithdrawAmount.this, Home.class);
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
