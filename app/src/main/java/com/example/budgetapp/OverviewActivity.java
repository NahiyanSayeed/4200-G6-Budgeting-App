package com.example.budgetapp;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


/*
* OverviewActivity Class
* Author: Jonathan Chiu
* Last Updated: 2025-03-24
* Purpose: An activity that acts as the overview for a budgeting application.
* Methods:
* */
public class OverviewActivity extends AppCompatActivity {

    private LinearLayout expenseList;
    private ProgressBar circlePBar;
    private TextView budgetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        //Init the views
        expenseList = findViewById(R.id.ExpenseTracker);
        circlePBar = findViewById(R.id.BudgetProgressBar);
        budgetText = findViewById(R.id.ProgressBarText);


    }
}