package com.example.budgetapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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
}
