package com.example.atmsystem;

public class Account {
    private String fullName;
    private String email;
    private String accountNumber;
    private Double initialAmount;
    private int securityPIN;
    private String phoneNumber;
    private String adhaarNumber;
    private String panNumber;
    private String atmNumber;
    private String schoolName;
    private String petName;
    private String accountStatus;

    // Default constructor for Firestore
    public Account() {
    }

    // Constructor to initialize the account details
    public Account(String fullName, String email, String accountNumber, Double initialAmount,
                   int securityPIN, String phoneNumber, String adhaarNumber, String panNumber,
                   String atmNumber, String schoolName, String petName, String accountStatus) {
        this.fullName = fullName;
        this.email = email;
        this.accountNumber = accountNumber;
        this.initialAmount = initialAmount;
        this.securityPIN = securityPIN;
        this.phoneNumber = phoneNumber;
        this.adhaarNumber = adhaarNumber;
        this.panNumber = panNumber;
        this.atmNumber = atmNumber;
        this.schoolName = schoolName;
        this.petName = petName;
        this.accountStatus = accountStatus;
    }

    // Getters and Setters for each field
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public int getSecurityPIN() {
        return securityPIN;
    }

    public void setSecurityPIN(int securityPIN) {
        this.securityPIN = securityPIN;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAdhaarNumber() {
        return adhaarNumber;
    }

    public void setAdhaarNumber(String adhaarNumber) {
        this.adhaarNumber = adhaarNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAtmNumber() {
        return atmNumber;
    }

    public void setAtmNumber(String atmNumber) {
        this.atmNumber = atmNumber;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
