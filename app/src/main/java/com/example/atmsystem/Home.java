package com.example.atmsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {
    private TextView debitButton;
    private TextView creditButton;
    private TextView checkBalanceButton;
    private TextView transferMoneyButton;
    private TextView showInfoButton;
    private TextView changePINButton;
    private TextView forgotPINButton;
    private TextView lockButton;
    private TextView unlockButton;
    private TextView logoutButton;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        // Initialize buttons
        debitButton = findViewById(R.id.debit_button);
        creditButton = findViewById(R.id.credit_button);
        checkBalanceButton = findViewById(R.id.check_balance_button);
        transferMoneyButton = findViewById(R.id.transfer_money_button);
        showInfoButton = findViewById(R.id.show_info_button);
        changePINButton = findViewById(R.id.change_PIN_button);
        forgotPINButton = findViewById(R.id.forgot_PIN_button);
        lockButton = findViewById(R.id.lock_atm_button);
        unlockButton = findViewById(R.id.unlock_atm_button);
        logoutButton = findViewById(R.id.logout_button);

        // Retrieve email from the intent
        email = getIntent().getStringExtra("user_email");

        // Set onClickListeners for each button
        debitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(WithdrawAmount.class);
            }
        });

        creditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(DepositAmount.class);
            }
        });

        transferMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(TransferMoney.class);
            }
        });

        showInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(ShowInfo.class);
            }
        });

        checkBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(CheckBalance.class);
            }
        });

        changePINButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(ChangePIN.class);
            }
        });

        forgotPINButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(ForgotPIN.class);
            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(LockAccount.class);
            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateTo(UnlockAccount.class);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity and clear the back stack
                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(Home.this, targetActivity);
        intent.putExtra("user_email", email);
        startActivity(intent);
        finish();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Navigate to MainActivity and clear the back stack
        Intent intent = new Intent(Home.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
