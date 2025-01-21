package com.example.atmsystem;

public class Accounts {
    private String full_name;
    private String email;
    private String password;
    private String account_number;
    private double balance;
    private int security_pin;
    private String phone_number;
    private String adhaar_number;
    private String pan_number;
    private String card_number;
    private String school_name;
    private String pet_name;
    private String status;

    public Accounts(String full_name, String email, String password) {
        this.full_name = full_name;
        this.email = email;
        this.password = password;
    }

    public Accounts(String full_name, String email, String account_number, double balance, int security_pin, String phone_number, String adhaar_number, String pan_number, String card_number, String school_name, String pet_name, String status) {
        this.full_name = full_name;
        this.email = email;
        this.account_number = account_number;
        this.balance = balance;
        this.security_pin = security_pin;
        this.phone_number = phone_number;
        this.adhaar_number = adhaar_number;
        this.pan_number = pan_number;
        this.card_number = card_number;
        this.school_name = school_name;
        this.pet_name = pet_name;
        this.status = status;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getAccount_number() {
        return account_number;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getSecurity_pin() {
        return security_pin;
    }

    public void setSecurity_pin(int security_pin) {
        this.security_pin = security_pin;
    }

    public String getPhone_number() {
        return phone_number;
    }
    public String getPan_number() {
        return pan_number;
    }

    public String getAdhaar_number() {
        return adhaar_number;
    }

    public String getCard_number() {
        return card_number;
    }

    public String getSchool_name() {
        return school_name;
    }

    public String getPet_name() {
        return pet_name;
    }

    public String getStatus() {
        return status;
    }
}
