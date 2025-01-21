package com.example.atmsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TransferMoney extends AppCompatActivity {
    private EditText transfer_amount, account_no, security_pin;
    private String account, email;
    private Double amount;
    private int security;
    private TextView response;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_amount_layout);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        Button return_home = findViewById(R.id.return_home_button);
        transfer_amount = findViewById(R.id.enter_transfer_amount);
        security_pin = findViewById(R.id.enter_security_check_balance);
        response = findViewById(R.id.transfer_page_response);
        Button transfer_money_button = findViewById(R.id.check_balance_page_button);
        account_no = findViewById(R.id.enter_recievers_account);

        email = getIntent().getStringExtra("user_email");

        transfer_money_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                security = Integer.parseInt(security_pin.getText().toString().trim());
                amount = Double.parseDouble(transfer_amount.getText().toString().trim());
                account = account_no.getText().toString();

                // Retrieve sender's data from Firestore
                // Retrieve sender's data from Firestore using email
                db.collection("accounts")
                        .whereEqualTo("email", email) // Use whereEqualTo to get sender by email
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                int securityStored = documentSnapshot.getLong("securityPIN").intValue();
                                String statusSender = documentSnapshot.getString("accountStatus");

                                if ("Locked".equals(statusSender)) {
                                    response.setText("Your account is locked. Transaction can't be done.");
                                    return;
                                }

                                if (securityStored != security) {
                                    response.setText("Wrong Security PIN.");
                                    security_pin.setText("");
                                    return;
                                }

                                Double balanceSender = documentSnapshot.getDouble("initialAmount");
                                if (balanceSender == null || balanceSender < amount || balanceSender - amount < 2000.0) {
                                    response.setText("Insufficient balance to transfer.");
                                    transfer_amount.setText("");
                                    return;
                                }

                                // Retrieve receiver's data from Firestore
                                DocumentReference receiverRef = db.collection("accounts").document(account);
                                receiverRef.get().addOnSuccessListener(receiverDoc -> {
                                    if (receiverDoc.exists()) {
                                        String statusReceiver = receiverDoc.getString("accountStatus");

                                        if ("Locked".equals(statusReceiver)) {
                                            response.setText("Receiver's account is locked. Transaction can't be done.");
                                            transfer_amount.setText(" ");
                                            security_pin.setText(" ");
                                            account_no.setText(" ");
                                            return;
                                        }

                                        Double balanceReceiver = receiverDoc.getDouble("initialAmount");
                                        if (balanceReceiver == null) {
                                            response.setText("Receiver's account balance not found.");
                                            return;
                                        }

                                        // Perform the transaction
                                        Double newSenderBalance = balanceSender - amount;
                                        Double newReceiverBalance = balanceReceiver + amount;

                                        // Update sender's account balance
                                        db.collection("accounts")
                                                .whereEqualTo("email", email) // Get sender by email
                                                .get()
                                                .addOnSuccessListener(query -> {
                                                    if (!query.isEmpty()) {
                                                        DocumentSnapshot senderDoc = query.getDocuments().get(0);
                                                        senderDoc.getReference().update("initialAmount", newSenderBalance)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Now update the receiver's account balance
                                                                    receiverRef.update("initialAmount", newReceiverBalance)
                                                                            .addOnSuccessListener(aVoid1 -> {
                                                                                transfer_money_button.setVisibility(View.GONE);
                                                                                response.setText("Rs. " + amount + " is transferred successfully.");
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                response.setText("Transaction Failed for Receiver.");
                                                                                Log.e("TransferMoney", "Receiver update failed: " + e.getMessage());
                                                                            });
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    response.setText("Transaction Failed for Sender.");
                                                                    Log.e("TransferMoney", "Sender update failed: " + e.getMessage());
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    response.setText("Error fetching sender's data.");
                                                    Log.e("TransferMoney", "Error fetching sender's data: " + e.getMessage());
                                                });

                                    } else {
                                        response.setText("Receiver's account does not exist.");
                                        transfer_amount.setText(" ");
                                        security_pin.setText(" ");
                                        account_no.setText(" ");
                                    }
                                }).addOnFailureListener(e -> response.setText("Receiver's data could not be retrieved."));
                            } else {
                                response.setText("Your account does not exist.");
                                transfer_amount.setText(" ");
                                security_pin.setText(" ");
                                account_no.setText(" ");
                            }
                        })
                        .addOnFailureListener(e -> response.setText("Error fetching account data."));

            }
        });

        return_home.setOnClickListener(v -> {
            Intent intent = new Intent(TransferMoney.this, Home.class);
            intent.putExtra("user_email", email);
            startActivity(intent);
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
