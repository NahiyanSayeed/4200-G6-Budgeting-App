package com.example.budgetapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    //Constructor
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //On database creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE expenses(category VARCHAR PRIMARY KEY, description TEXT, amount DOUBLE)";
        String query2 = "CREATE TABLE account(_id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, password VARCHAR)";
        db.execSQL(query);
        db.execSQL(query2);
    }

    //Refreshes database to newest version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS expenses";
        String query2 = "DROP TABLE IF EXISTS account";
        db.execSQL(query);
        db.execSQL(query2);
        onCreate(db);
    }

    public long addExpense(String category, String description, double amount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", category);
        contentValues.put("description", description);
        contentValues.put("amount", amount);
        return db.insert("expenses", null, contentValues);
    }

    public long addAccount(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        return db.insert("account", null, contentValues);
    }

    public Cursor displayExpenseData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM expenses", null);
        return cursor;
    }

    public Cursor displayAccountData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM accounts", null);
        return cursor;
    }

    public void deleteExpenseData(String title) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM expenses WHERE category=?", new String[]{title});
        if (cursor.getCount() > 0) {
            db.delete("expenses", "category=?", new String[]{title});
        }
        cursor.close();
    }

    public void deleteAccountData(String username) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM account WHERE username=?", new String[]{username});
        if (cursor.getCount() > 0) {
            db.delete("account", "username=?", new String[]{username});
        }
        cursor.close();
    }

    public void deleteAccountData(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM account WHERE _id=?", new String[]{Integer.toString(id)});
        if (cursor.getCount() > 0) {
            db.delete("account", "_id=?", new String[]{Integer.toString(id)});
        }
        cursor.close();
    }

    public List<Expense> getAllExpenses() {
        List<Expense> output = new ArrayList<Expense>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM expenses", null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                String description = cursor.getString(1);
                double amount = cursor.getDouble(2);

                output.add(new Expense(category, description, amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return output;
    }

    public List<Account> getAllAccount() {
        List<Account> output = new ArrayList<Account>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM account", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String username = cursor.getString(1);
                String password = cursor.getString(2);

                output.add(new Account(id, username, password));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return output;
    }
}
