package com.example.budgetapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


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
        expenseList = findViewById(R.id.ExpenseTracker);
        circlePBar = findViewById(R.id.BudgetProgressBar);
        budgetText = findViewById(R.id.ProgressBarText);

        //Budget
        //TODO: Grab budget and expenses from sql first then getIntent
        budget = getIntent().getDoubleExtra("budget", 0);
        expense = getIntent().getDoubleExtra("expense", 0);

        //TODO: Grab overview from sql

        //Set views
        int progressPercentage = (budget != 0) ? (int) (expense / budget) : 0;
        circlePBar.setProgress(progressPercentage);

        double budgetRemaining = budget - expense;
        if (budgetRemaining < 0) {
            budgetText.setTextColor(Color.RED);
        }
        budgetText.setText("Budget Remaining: $" + budgetRemaining);
    }


    /*
    * generateExpense Method
    * Author: Jonathan Chiu
    * Last Updated: 2025-03-24
    * Purpose: When given an Expense and Percentage, generates a view and percentage bar into the expense list
    * */
    private void generateExpense(Expense expense, int percentage) {
        //Create the TextView
        TextView expenseText = new TextView(this);
        expenseText.setLayoutParams(new LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1
        ));
        String expenseString = expense.getCategory() + ": " + percentage + "%";
        expenseText.setText(expenseString);
        expenseText.setTextColor(Color.parseColor("#4CAF50"));
        expenseText.setTextSize(16);

        //Create the ProgressBar
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                300,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        progressBar.setProgress(percentage);
        progressBar.setMax(100);

        expenseList.addView(expenseText);
        expenseList.addView(progressBar);
    }
}