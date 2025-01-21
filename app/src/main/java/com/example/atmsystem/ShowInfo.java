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
import com.google.firebase.firestore.QuerySnapshot;

public class ShowInfo extends AppCompatActivity {
    private int security_pin;
    private Button show_info;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info_layout);

        db = FirebaseFirestore.getInstance();  // Initialize Firebase Firestore

        show_info = findViewById(R.id.show_info_page_button);
        TextView full_name = findViewById(R.id.full_name_show_page);
        TextView full_name_result = findViewById(R.id.full_name_result_show_page);
        TextView email_text = findViewById(R.id.email_show_page);
        TextView email_result = findViewById(R.id.email_result_show_page);
        TextView account = findViewById(R.id.account_number_show_page);
        TextView account_result = findViewById(R.id.account_number_result_show_page);
        TextView adhaar = findViewById(R.id.adhaar_number_show_page);
        TextView adhaar_result = findViewById(R.id.adhaar_number_result_show_page);
        TextView atm = findViewById(R.id.ATM_Number_show_page);
        TextView atm_result = findViewById(R.id.ATM_Number_result_show_page);
        TextView pan = findViewById(R.id.PAN_show_page);
        TextView pan_result = findViewById(R.id.PAN_result_show_page);
        TextView phone = findViewById(R.id.phone_number_show_page);
        TextView phone_result = findViewById(R.id.phone_number_result_show_page);
        TextView account_status = findViewById(R.id.account_status_show_page);
        TextView account_status_result = findViewById(R.id.account_status_result_show_page);
        TextView balance = findViewById(R.id.balance_show_page);
        TextView balance_result = findViewById(R.id.balance_result_show_page);
        TextView security = findViewById(R.id.security_show_info_page);
        EditText security_result = findViewById(R.id.enter_security_show_info_page);

        // Initially hide all info fields
        hideInfoFields(full_name, full_name_result, account, account_result, phone, phone_result, email_text, email_result, adhaar, adhaar_result, atm, atm_result, account_status, account_status_result, balance, balance_result, pan, pan_result);

        String email = getIntent().getStringExtra("user_email");

        show_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    security_pin = Integer.parseInt(security_result.getText().toString().trim());
                } catch (NumberFormatException e) {
                    security_result.setError("Please enter a valid security pin.");
                    return; // Exit the method early
                }

                // Fetch account details from Firestore
                db.collection("accounts")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot != null && !documentSnapshot.isEmpty()) {
                                    DocumentSnapshot document = documentSnapshot.getDocuments().get(0);

                                    // Fetch the security PIN stored in Firestore
                                    int security_stored = document.getLong("securityPIN").intValue();  // Assuming 'securityPin' is a field in the document

                                    if (security_stored == security_pin) {
                                        // Set user data to the UI
                                        show_info.setVisibility(View.GONE);
                                        security_result.setVisibility(View.GONE);
                                        security.setVisibility(View.GONE);
                                        full_name_result.setText(document.getString("fullName"));
                                        account_result.setText(document.getString("accountNumber"));
                                        phone_result.setText(document.getString("phoneNumber"));
                                        email_result.setText(email);
                                        adhaar_result.setText(document.getString("adhaarNumber"));
                                        atm_result.setText(document.getString("atmNumber"));
                                        account_status_result.setText(document.getString("accountStatus"));
                                        balance_result.setText(String.valueOf(document.getDouble("initialAmount")));
                                        pan_result.setText(document.getString("panNumber"));

                                        // Show the details UI
                                        showInfoFields(full_name, full_name_result, account, account_result, phone, phone_result, email_text, email_result, adhaar, adhaar_result, atm, atm_result, account_status, account_status_result, balance, balance_result, pan, pan_result);

                                    } else {
                                        // If PIN doesn't match
                                        Toast.makeText(ShowInfo.this, "Wrong Security PIN.", Toast.LENGTH_SHORT).show();
                                        security_result.setText("");
                                    }
                                } else {
                                    // No account found
                                    Toast.makeText(ShowInfo.this, "Account not found.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Firestore query failed
                                Toast.makeText(ShowInfo.this, "Error retrieving account details.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        Button return_home = findViewById(R.id.return_home_button);
        return_home.setOnClickListener(v -> {
            // Start the home activity
            Intent intent = new Intent(ShowInfo.this, Home.class);
            intent.putExtra("user_email", email);
            startActivity(intent);
            finish();
        });
    }

    // Helper method to hide the info fields initially
    private void hideInfoFields(TextView full_name, TextView full_name_result, TextView account, TextView account_result,
                                TextView phone, TextView phone_result, TextView email_text, TextView email_result,
                                TextView adhaar, TextView adhaar_result, TextView atm, TextView atm_result,
                                TextView account_status, TextView account_status_result, TextView balance, TextView balance_result,
                                TextView pan, TextView pan_result) {
        full_name.setVisibility(View.GONE);
        full_name_result.setVisibility(View.GONE);
        account.setVisibility(View.GONE);
        account_result.setVisibility(View.GONE);
        phone.setVisibility(View.GONE);
        phone_result.setVisibility(View.GONE);
        email_text.setVisibility(View.GONE);
        email_result.setVisibility(View.GONE);
        adhaar.setVisibility(View.GONE);
        adhaar_result.setVisibility(View.GONE);
        atm.setVisibility(View.GONE);
        atm_result.setVisibility(View.GONE);
        account_status.setVisibility(View.GONE);
        account_status_result.setVisibility(View.GONE);
        balance.setVisibility(View.GONE);
        balance_result.setVisibility(View.GONE);
        pan.setVisibility(View.GONE);
        pan_result.setVisibility(View.GONE);
    }

    // Helper method to show the info fields
    private void showInfoFields(TextView full_name, TextView full_name_result, TextView account, TextView account_result,
                                TextView phone, TextView phone_result, TextView email_text, TextView email_result,
                                TextView adhaar, TextView adhaar_result, TextView atm, TextView atm_result,
                                TextView account_status, TextView account_status_result, TextView balance, TextView balance_result,
                                TextView pan, TextView pan_result) {
        full_name.setVisibility(View.VISIBLE);
        full_name_result.setVisibility(View.VISIBLE);
        account.setVisibility(View.VISIBLE);
        account_result.setVisibility(View.VISIBLE);
        phone.setVisibility(View.VISIBLE);
        phone_result.setVisibility(View.VISIBLE);
        email_text.setVisibility(View.VISIBLE);
        email_result.setVisibility(View.VISIBLE);
        adhaar.setVisibility(View.VISIBLE);
        adhaar_result.setVisibility(View.VISIBLE);
        atm.setVisibility(View.VISIBLE);
        atm_result.setVisibility(View.VISIBLE);
        account_status.setVisibility(View.VISIBLE);
        account_status_result.setVisibility(View.VISIBLE);
        balance.setVisibility(View.VISIBLE);
        balance_result.setVisibility(View.VISIBLE);
        pan.setVisibility(View.VISIBLE);
        pan_result.setVisibility(View.VISIBLE);
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
