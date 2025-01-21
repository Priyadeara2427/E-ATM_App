package com.example.atmsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    private Button register;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register = findViewById(R.id.button3);
        login = findViewById(R.id.button4);
        FirebaseApp.initializeApp(this);


        /*DbHandler handler = new DbHandler(this, "user", null, 1);
        handler.deleteTableContent("accounts");
        handler.deleteTableContent("user");
        handler.close();*/

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Registration.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        // Close the app when back is pressed
        finishAffinity();
    }
}