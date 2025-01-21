package com.example.atmsystem;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DbHandler extends SQLiteOpenHelper {
    //private static final String DATABASE_NAME = "ATMSystem.db";
    //private static final int DATABASE_VERSION = 2;
    public DbHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the accounts table
        String createAccountsTable = "CREATE TABLE accounts (account_number TEXT PRIMARY KEY, full_name TEXT, email TEXT, balance DOUBLE, security_pin INTEGER, phone_number TEXT, adhaar_number TEXT, pan_number TEXT, card_number TEXT, school_name TEXT, pet_name TEXT, status TEXT DEFAULT \"ACTIVE\")";

        // SQL statement to create the user table
        String createUserTable = "CREATE TABLE user (full_name TEXT,email TEXT PRIMARY KEY, password TEXT)";

        // Execute both SQL statements
        db.execSQL(createUserTable);
        db.execSQL(createAccountsTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the accounts table if it exists
        db.execSQL("DROP TABLE IF EXISTS accounts");

        // Drop the user table if it exists
        db.execSQL("DROP TABLE IF EXISTS user");

        // Recreate the tables
        onCreate(db);
    }

    public void deleteTableContent(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName);
        db.close();
    }


    public boolean addAccount(Accounts account){
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("account_number", account.getAccount_number());
            values.put("full_name", account.getFull_name());
            values.put("email", account.getEmail());
            values.put("balance", account.getBalance());
            values.put("security_pin", account.getSecurity_pin());
            values.put("phone_number", account.getPhone_number());
            values.put("adhaar_number", account.getAdhaar_number());
            values.put("pan_number", account.getPan_number());
            values.put("card_number", account.getCard_number());
            values.put("school_name",account.getSchool_name());
            values.put("pet_name", account.getPet_name());
            values.put("status", account.getStatus());

            // Insert the row into the user table
            long rowId = db.insert("accounts", null, values);

            if (rowId != -1) {
                Log.d("mytag", "User added successfully with row ID: " + rowId);
                return true;
            } else {
                Log.e("mytag", "Failed to add account.");
                return false;
            }
        } catch (Exception e) {
            Log.e("mytag", "Error while adding account: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public boolean addUser(Accounts account) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("full_name", account.getFull_name());
            values.put("email", account.getEmail());
            values.put("password", account.getPassword());

            // Insert the row into the user table
            long rowId = db.insert("user", null, values);

            if (rowId != -1) {
                Log.d("mytag", "User added successfully with row ID: " + rowId);
                return true;
            } else {
                Log.e("mytag", "Failed to add user.");
                return false;
            }
        } catch (Exception e) {
            Log.e("mytag", "Error while adding user: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }



    public void updateAccountStatus(String email, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus); // Specify the column to update and the new value

        // Update the row in the database
        int rowsAffected = db.update(
                "accounts",                   // Table name
                values,                       // ContentValues containing the new data
                "email = ?",         // WHERE clause to specify which row to update
                new String[]{String.valueOf(email)}   // Arguments for the WHERE clause
        );

        Log.d("mytag", "Rows affected: " + rowsAffected);
        db.close();
    }
    public void updateBalance(String email, Double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", newBalance); // Specify the column to update and the new value

        // Update the row in the database
        int rowsAffected = db.update(
                "accounts",                   // Table name
                values,                       // ContentValues containing the new data
                "email = ?",         // WHERE clause to specify which row to update
                new String[]{String.valueOf(email)}   // Arguments for the WHERE clause
        );

        Log.d("mytag", "Rows affected: " + rowsAffected);
        db.close();
    }

    public void updateSecurityPIN(String email, int newSecurityPIN) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("security_pin", newSecurityPIN); // Specify the column to update and the new value

        // Update the row in the database
        int rowsAffected = db.update(
                "accounts",                   // Table name
                values,                       // ContentValues containing the new data
                "email = ?",         // WHERE clause to specify which row to update
                new String[]{String.valueOf(email)}   // Arguments for the WHERE clause
        );

        Log.d("mytag", "Rows affected: " + rowsAffected);
        db.close();
    }


    public String getAccountNumber(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "accounts",
                new String[]{"account_number"},
                "email = ?",
                new String[]{email},
                null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String accountNumber = cursor.getString(cursor.getColumnIndex("account_number"));
            cursor.close();
            return accountNumber;
        } else {
            cursor.close();
            return null; // No user found
        }
    }
    // Method to get full name by email
    public String getFullName(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String full_name = null;
        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT full_name FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                full_name = cursor.getString(0); // Get the full name from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching full_name: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return full_name;
    }

    // Method to get password by email
    public String getPassword(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String password = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT password FROM user WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                password = cursor.getString(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching password: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return password;
    }

    public double getBalance(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Double balance = 0.0;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT balance FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                balance = cursor.getDouble(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching balance: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return balance;
    }
    public int getSecurityPIN(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int security = -1;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT security_pin FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                security = cursor.getInt(0); // Get the security PIN from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching security PIN: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return security;
    }

    public String getPhoneNumber(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String phone = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT phone_number FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                phone = cursor.getString(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching phone number: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return phone;
    }
    public String getAdhaarNumber(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String adhaar = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT adhaar_number FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                adhaar = cursor.getString(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching adhaar number: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return adhaar;
    }
    public String getPANNumber(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String PAN = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT pan_number FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                PAN = cursor.getString(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching PAN number: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return PAN;
    }
    public String getCardNumber(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String card = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT card_number FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                card = cursor.getString(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching card number: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return card;
    }

    public String getSchoolName(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String school_name = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT school_name FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                school_name = cursor.getString(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching school name: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return school_name;
    }
    public String getPetName(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String pet_name = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT pet_name FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                pet_name = cursor.getString(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching pet name: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return pet_name;
    }
    public String getStatus(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String status = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT status FROM accounts WHERE email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                status = cursor.getString(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching status: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return status;
    }
    public String getEmail(String account_number) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String email = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the password for the given email
            String query = "SELECT email FROM accounts WHERE account_number = ?";
            cursor = db.rawQuery(query, new String[]{account_number});

            if (cursor.moveToFirst()) {
                email = cursor.getString(0); // Get the password from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching email: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return email;
    }

    public String getLastAccountNumber() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String lastAccountNumber = null;

        try {
            db = this.getReadableDatabase();
            // Query to get the last account number
            String query = "SELECT account_number FROM accounts ORDER BY ROWID DESC LIMIT 1";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                lastAccountNumber = cursor.getString(0); // Get the account number from the first column
            }
        } catch (Exception e) {
            Log.e("mytag", "Error fetching last account number: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return lastAccountNumber;
    }


}
