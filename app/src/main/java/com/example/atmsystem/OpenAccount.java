package com.example.atmsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class OpenAccount extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView textView;
    private Button submitDetails, nextButton;
    private EditText full_name_text, initial_amount_text, security_text, phone_text, adhaar_text, pan_text, atm_text, school_text, pet_text;
    private String full_name, email, phone, adhaar, pan, atm, school, pet;
    private double initial_amount;
    private int security;
    String account_number;
    private boolean isPINCorrect, isPANCorrect, isAdhaarCorrect, isPhoneCorrect;
    public boolean checkPIN(String PIN){
        if (isValidPIN(PIN) && validatePINComplexity(PIN))
            return true;
        return false;
    }
    public boolean isValidPIN(String PIN){
        if (PIN.length() == 4)
            return true;
        return false;
    }

    public boolean validatePINComplexity(String PINKey){
        if (PINKey.charAt(0) == PINKey.charAt(1) &&  PINKey.charAt(2) == PINKey.charAt(3))
            return false;
        return !PINKey.equals("1234") && !PINKey.equals("9876");
    }

    public boolean isValidMobile(String str){
        return str.length() == 10 && (str.startsWith("9") || str.startsWith("8") || str.startsWith("7") || str.startsWith("6"));
    }

    public boolean isValidPAN(String pan) {
        return pan != null && pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    }


    public boolean isValidAdhaar(String str){
        if (str.length() == 12)
            return true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_account_details);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        submitDetails = findViewById(R.id.submit_details_button);
        textView = findViewById(R.id.text_account_created);
        nextButton = findViewById(R.id.loginToHome_button);
        full_name_text = findViewById(R.id.enter_fullName);
        initial_amount_text = findViewById(R.id.enter_initialAmount);
        security_text = findViewById(R.id.enter_securityPIN);
        phone_text = findViewById(R.id.enter_phoneNumber);
        adhaar_text = findViewById(R.id.enter_adhaarNumber);
        pan_text = findViewById(R.id.enter_panNumber);
        atm_text = findViewById(R.id.enter_atmNumber);
        school_text = findViewById(R.id.enter_schoolName);
        pet_text = findViewById(R.id.enter_petName);

        email = getIntent().getStringExtra("user_email");

        nextButton.setVisibility(View.GONE);
        Button register_back = findViewById(R.id.back_to_register);
        register_back.setVisibility(View.GONE);

        submitDetails.setOnClickListener(v -> {
            try {
                // Get values from input fields
                full_name = full_name_text.getText().toString();
                initial_amount = Double.parseDouble(initial_amount_text.getText().toString().trim());
                security = Integer.parseInt(security_text.getText().toString().trim());
                phone = phone_text.getText().toString();
                adhaar = adhaar_text.getText().toString();
                pan = pan_text.getText().toString();
                atm = atm_text.getText().toString();
                school = school_text.getText().toString();
                pet = pet_text.getText().toString();

                // Validate input fields
                isPINCorrect = checkPIN(String.valueOf(security));
                isPANCorrect = isValidPAN(pan);
                isAdhaarCorrect = isValidAdhaar(adhaar);
                isPhoneCorrect = isValidMobile(phone);

                if (isPINCorrect && isPhoneCorrect && isAdhaarCorrect && isPANCorrect && initial_amount >= 2000){
                    generateAccountNumber();
                }
                else if (initial_amount < 2000){
                    textView.setText("Minimum balance should be 2000");
                    initial_amount_text.setText("");
                }
                else if (!isPINCorrect && !validatePINComplexity(String.valueOf(security))){
                    textView.setText("PIN is simple and easy to guess.");
                    security_text.setText("");
                }
                else if (!isPINCorrect && !isValidPIN(String.valueOf(security))){
                    textView.setText("PIN must be of 4 digits.");
                    security_text.setText("");
                }
                else if (!isPhoneCorrect){
                    textView.setText("Invalid Phone Number");
                    phone_text.setText("");
                }
                else if (!isPANCorrect){
                    textView.setText("Invalid PAN Number");
                    pan_text.setText("");
                }
                else if (!isAdhaarCorrect){
                    textView.setText("Invalid Adhaar Number");
                    adhaar_text.setText("");
                }

            } catch (NumberFormatException e) {
                textView.setText("Invalid input format. Please check the fields.");
                Log.e("OpenAccount", "Error parsing input: " + e.getMessage(), e);
            } catch (Exception e) {
                textView.setText("An unexpected error occurred.");
                Log.e("OpenAccount", "Error: " + e.getMessage(), e);
            }
        });

        register_back.setOnClickListener(v -> {
            Intent intent = new Intent(OpenAccount.this, Registration.class);
            startActivity(intent);
            finish();
        });

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(OpenAccount.this, LoginPage.class);
            startActivity(intent);
            finish();
        });
    }

    private void generateAccountNumber() {
        account_number = "100000000"; // Default account number
        // Get the last account number from Firestore
        db.collection("accounts")
                .orderBy("accountNumber", Query.Direction.DESCENDING) // Sort by account_number in descending order
                .limit(1) // Get only the latest account document
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the latest account document and parse the account number
                        DocumentSnapshot lastAccount = queryDocumentSnapshots.getDocuments().get(0);
                        String lastAccountNumber = lastAccount.getString("accountNumber");

                        if (lastAccountNumber != null) {
                            try {
                                int lastNumber = Integer.parseInt(lastAccountNumber);
                                account_number = String.valueOf(lastNumber + 1); // Increment the last account number
                            } catch (NumberFormatException e) {
                                Log.e("mytag", "Error converting account number: " + e.getMessage(), e);
                            }
                        }
                    }
                    // Submit the new account details
                    submitAccountDetails(account_number);
                })
                .addOnFailureListener(e -> {
                    Log.e("mytag", "Error fetching last account number: " + e.getMessage());
                    textView.setText("Failed to fetch account details.");
                });
    }

    private void submitAccountDetails(String account_number) {
        // Create a new Account object
        Account newAccount = new Account(full_name, email, account_number, initial_amount, security, phone, adhaar, pan, atm, school, pet, "Active");

        // Save the new account in Firestore
        db.collection("accounts")
                .document(account_number) // Use the account number as document ID
                .set(newAccount) // Set the newAccount object to Firestore
                .addOnSuccessListener(aVoid -> {
                    textView.setText("Details are Submitted.");
                    submitDetails.setVisibility(View.GONE);
                    nextButton.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    textView.setText("Failed to submit details: " + e.getMessage());
                    findViewById(R.id.back_to_register).setVisibility(View.VISIBLE);
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
