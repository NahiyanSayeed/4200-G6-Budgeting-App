package com.example.budgetapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;


/*
* OverviewActivity Class
* Author: Jonathan Chiu
* Last Updated: 2025-03-24
* Purpose: An activity that acts as the overview for a budgeting application.
* Methods:  onCreate
*           generateExpense
* */
public class OverviewActivity extends AppCompatActivity {

    //Views
    private LinearLayout expenseList;
    private ProgressBar circlePBar;
    private TextView budgetText;
    private BottomNavigationView bottomNavigationView;

    //Variables
    private double budget;
    private double expense;


    /*
    * onCreate Method
    * Author: Jonathan Chiu
    * Last Updated: 2025-03-24
    * Purpose: When activity is opened, initalizes the views, fetches data from sql, and displays the information
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        //Init the views
        expenseList = findViewById(R.id.expenseContainer);
        circlePBar = findViewById(R.id.BudgetProgressBar);
        budgetText = findViewById(R.id.ProgressBarText);
        bottomNavigationView = findViewById(R.id.bottom_nav);

        //Expenses setup
        int userId = getIntent().getIntExtra("userID", -1);
        DBHelper dbHelper = new DBHelper(this, "BudgetDB", null, 2);
        budget = dbHelper.getBudgetById(userId); // <-- budget from DB

        List<Expense> expenses = dbHelper.getAllExpenses();
        double totalExpenses = 0;

        for (Expense e : expenses) {
            totalExpenses += e.getAmount();
        }

        // Handle empty budget
        Button setupBudgetBtn = findViewById(R.id.setupBudgetButton);
        Button addFundsBtn = findViewById(R.id.addFundsButton);

        if (budget <= 0) {
            setupBudgetBtn.setVisibility(View.VISIBLE);

                setupBudgetBtn.setText("First time? Set your budget here!");
                setupBudgetBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(OverviewActivity.this, BudgetSetupActivity.class);
                    intent.putExtra("userID", userId);
                    intent.putExtra("mode", "initial");
                    startActivity(intent);
                });

        } else {
            setupBudgetBtn.setVisibility(View.GONE);
        }

        //Set views
        bottomNavigationView.setSelectedItemId(R.id.nav_overview);

        //Get Expenses without any Negatives (Income)
        double onlyExpenses = 0;
        for (Expense e : expenses) {
            if (e.getAmount() >= 0) {
                onlyExpenses += e.getAmount();
            }
        }

        int progressPercentage = (budget != 0) ? (int) ((onlyExpenses / budget)*100.0) : 0;
        circlePBar.setProgress(progressPercentage);


        double budgetRemaining = budget - onlyExpenses;

        if (budgetRemaining < 0) {
            budgetText.setTextColor(Color.RED);
        }

        budgetText.setText("Budget Remaining: $" + String.format("%.2f", budgetRemaining));

        //Handles the expenses overview sheet. Percent of budget each item takes.
        for (Expense e : expenses) {
            if (e.getAmount() >= 0) {
                int percentage = (int) ((e.getAmount() / budget) * 100);
                generateExpense(e, percentage);
            }
        }

        //Handles Bottom Navigation Clicks

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_transactions) {
                    Intent intent = new Intent(OverviewActivity.this, TransactionsActivity.class);
                    intent.putExtra("userID", userId); // <-- pass it
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_overview) {
                    return true;
                } else if (id == R.id.nav_budget_sheet) {
                    Intent intent = new Intent(OverviewActivity.this, BudgetActivity.class);
                    intent.putExtra("userID", userId);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_detailed_breakdown) {
                    Intent intent = new Intent(OverviewActivity.this, DetailedBreakdownActivity.class);
                    intent.putExtra("userID", userId);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }


    /*
    * generateExpense Method
    * Author: Jonathan Chiu
    * Last Updated: 2025-03-24
    * Purpose: When given an Expense and Percentage, generates a view and percentage bar into the expense list
    * */
    private void generateExpense(Expense expense, int percentage) {
        // Create the TextView for the category
        TextView expenseText = new TextView(this);
        expenseText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        String expenseString = expense.getCategory() + ": " + percentage + "%";
        expenseText.setText(expenseString);
        expenseText.setTextColor(Color.parseColor("#4CAF50"));
        expenseText.setTextSize(16);

        // Create the ProgressBar
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Use MATCH_PARENT for full width
                LinearLayout.LayoutParams.WRAP_CONTENT  // Allow the ProgressBar to wrap its height
        ));
        progressBar.setProgress(percentage);
        progressBar.setMax(100);

        // Ensure the parent LinearLayout is configured to stack elements vertically
        if (expenseList.getOrientation() != LinearLayout.VERTICAL) {
            expenseList.setOrientation(LinearLayout.VERTICAL);
        }

        // Add some space between TextView and ProgressBar (optional)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);  // Adjust the margins for better spacing (top, bottom)
        expenseText.setLayoutParams(params);

        // Add the TextView and ProgressBar to the layout
        expenseList.addView(expenseText);
        expenseList.addView(progressBar);
    }


}