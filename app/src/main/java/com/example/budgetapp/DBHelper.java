package com.example.budgetapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/*
* Author: Jonathan Chiu
* Last Updated: 2025-03-25
* Purpose: A database manager for the budget activity. Responsible for interactions from activity to database
* Methods:  onCreate(SQLiteDatabase db)
*           onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
*           addExpense(int userID, String category, String description, double amount)
*           addAccount(String username, String password)
*           addBudget(int userID, double budget)
*           addIncome(int userID, double income)
*           displayExpenseData()
*           displayAccountData()
*           deleteExpenseData(int id)
*           deleteExpensesByCategory(String category)
*           deleteExpenseByDetails(String category, String description, double amount)
*           getAllAccount()
*           deleteAccountData(String username)
*           deleteAccountData(int id)
*           deleteBudgetData(int id)
*           deleteIncomeData(int id)
*           getAllExpenses()
*           getExpenseById(int id)
*           getAllAccount()
*           getBudgetById(int userID)
*           getIncomeById(int userID)
* */
public class DBHelper extends SQLiteOpenHelper {
    //Constructor
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    //On database creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE expenses(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, category TEXT NOT NULL,description TEXT, amount DOUBLE, FOREIGN KEY(user_id) REFERENCES account(_id) ON DELETE CASCADE)";
        String query2 = "CREATE TABLE account(_id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, password VARCHAR)";
        String query3 = "CREATE TABLE budget(_id INTEGER PRIMARY KEY, amount DOUBLE, FOREIGN KEY(_id) REFERENCES account(_id) ON DELETE CASCADE)";
        String query4 = "CREATE TABLE income(_id INTEGER PRIMARY KEY, amount DOUBLE, FOREIGN KEY(_id) REFERENCES account(_id) ON DELETE CASCADE)";
        db.execSQL(query);
        db.execSQL(query2);
        db.execSQL(query3);
        db.execSQL(query4);
    }

    //Refreshes database to newest version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS expenses";
        String query2 = "DROP TABLE IF EXISTS account";
        String query3 = "DROP TABLE IF EXISTS budget";
        String query4 = "DROP TABLE IF EXISTS income";
        db.execSQL(query);
        db.execSQL(query2);
        db.execSQL(query3);
        db.execSQL(query4);
        onCreate(db);
    }

    public void addExpense(int userID, String category, String description, double amount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", userID); //Must match an existing user ID
        contentValues.put("category", category);
        contentValues.put("description", description);
        contentValues.put("amount", amount);
        db.insert("expenses", null, contentValues);
        db.close();
    }

    public long addAccount(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        return db.insert("account", null, contentValues);
    }

    public long addBudget(int userID, double budget) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", userID); //Must match an existing user ID
        contentValues.put("amount", budget);
        return db.insert("budget", null, contentValues);
    }

    public long addIncome(int userID, double income) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put("_id", userID); //Must match an existing user ID
        contentValues.put("amount", income);
        return db.insert("income", null, contentValues);

    }

    public Cursor displayExpenseData(){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM expenses", null);
    }

    public Cursor displayAccountData(){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM account", null);
    }

    public void deleteExpenseData(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("expenses", "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteExpensesByCategory(String category) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("expenses", "category=?", new String[]{category});
        db.close();
    }

    public void deleteAccountData(String username) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM account WHERE username=?", new String[]{username});
        if (cursor.getCount() > 0) {
            db.delete("account", "username=?", new String[]{username});
        }
        cursor.close();
        db.close();
    }

    public void deleteAccountData(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM account WHERE _id=?", new String[]{Integer.toString(id)});
        if (cursor.getCount() > 0) {
            db.delete("account", "_id=?", new String[]{Integer.toString(id)});
        }
        cursor.close();
        db.close();
    }

    public void deleteBudgetData(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM budget WHERE _id=?", new String[]{Integer.toString(id)});
        if (cursor.getCount() > 0) {
            db.delete("budget", "_id=?", new String[]{Integer.toString(id)});
        }
        cursor.close();
        db.close();
    }

    public void deleteIncomeData(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM income WHERE _id=?", new String[]{Integer.toString(id)});
        if (cursor.getCount() > 0) {
            db.delete("income", "_id=?", new String[]{Integer.toString(id)});
        }
        cursor.close();
        db.close();
    }

    public void deleteExpenseByDetails(String category, String description, double amount) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("expenses", "category=? AND description=? AND amount=?",
                new String[]{category, description, String.valueOf(amount)});
        db.close();
    }


    public List<Expense> getAllExpenses() {
        List<Expense> output = new ArrayList<Expense>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT category, description, amount FROM expenses", null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                String description = cursor.getString(1);
                double amount = cursor.getDouble(2);

                output.add(new Expense(category, description, amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return output;
    }


    public Expense getExpenseById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM expenses WHERE _id=?", new String[]{String.valueOf(id)});

        Expense expense = null;
        if (cursor.moveToFirst()) {
            String category = cursor.getString(2);  // Index 1 -> category
            String description = cursor.getString(3);  // Index 2 -> description
            double amount = cursor.getDouble(4);  // Index 3 -> amount

            expense = new Expense(category, description, amount);
        }
        cursor.close();
        db.close();
        return expense;
    }

    public List<Expense> getExpensesByUserId(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM expenses WHERE user_id=?", new String[]{String.valueOf(userId)});
        List<Expense> output = new ArrayList<Expense>();
        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(2);
                String description = cursor.getString(3);
                double amount = cursor.getDouble(4);
                output.add(new Expense(category,description,amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
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
        db.close();
        return output;
    }

    public double getBudgetById(int userID) {
        double output = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT amount FROM budget WHERE _id=?", new String[]{String.valueOf(userID)});
        if (cursor.moveToFirst()) {
            output = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return output;
    }

    public double getIncomeById(int userID) {
        double output = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT amount FROM income WHERE _id=?", new String[]{Integer.toString(userID)});
        if (cursor.moveToFirst()) {
            output = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return output;
    }
}
