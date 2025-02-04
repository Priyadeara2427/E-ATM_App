package com.example.atmsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginPage extends AppCompatActivity {
    private Button loginButton;
    private EditText emailText, passwordText;
    private String email, password;
    private TextView login_response;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView register;
    private TextView forgot_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        loginButton = findViewById(R.id.login_button);
        emailText = findViewById(R.id.enter_email_login_page);
        passwordText = findViewById(R.id.enter_password_login_page);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        login_response = findViewById(R.id.login_page_response);
        register = findViewById(R.id.not_registered_id);
        forgot_password = findViewById(R.id.forgot_password);

        loginButton.setOnClickListener(v -> {
            email = emailText.getText().toString().trim();
            password = passwordText.getText().toString().trim();

            if (email.isEmpty()) {
                login_response.setText("Enter email.");
            } else if (password.isEmpty()) {
                login_response.setText("Enter password.");
            } else {
                // Disable the button to prevent multiple clicks
                loginButton.setEnabled(false);
                authenticateUser(email, password);
            }
        });

        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, Registration.class);
            startActivity(intent);
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(LoginPage.this, ForgotPassword.class);
                 startActivity(intent);
            }
        });
    }

    private void authenticateUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                checkAccountDetails(user.getEmail());
                            } else {
                                login_response.setText("Please verify your email before logging in.");
                                emailText.setText("");
                                passwordText.setText("");
                                loginButton.setEnabled(true); // Re-enable the button
                            }
                        }
                    } else {
                        login_response.setText("Login failed. Check email and password.");

                        emailText.setText("");
                        passwordText.setText("");
                        loginButton.setEnabled(true); // Re-enable the button
                    }
                });
    }

    private void checkAccountDetails(String email) {
        db.collection("accounts")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            if (document.contains("fullName") && document.contains("accountNumber")) {
                                proceedToLogin();
                            } else {
                                Toast.makeText(this, "Please complete your account details.", Toast.LENGTH_SHORT).show();
                                loginButton.setEnabled(true); // Re-enable the button
                                promptOpenAccount(email);
                            }
                        } else {
                            Toast.makeText(this, "No account details found. Please sign up or complete your account.", Toast.LENGTH_SHORT).show();
                            loginButton.setEnabled(true); // Re-enable the button
                            promptOpenAccount(email);
                        }
                    } else {
                        Log.e("Login", "Error checking account details", task.getException());
                        login_response.setText("Error checking account details.");
                        loginButton.setEnabled(true); // Re-enable the button
                    }
                });
    }

    private void proceedToLogin() {
        Intent intent = new Intent(LoginPage.this, Home.class);
        intent.putExtra("user_email", email);
        startActivity(intent);
        finish();
    }

    private void promptOpenAccount(String email) {
        Intent intent = new Intent(LoginPage.this, OpenAccount.class);
        intent.putExtra("user_email", email);
        startActivity(intent);
        finish();
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
