package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DetailedBreakdownActivity extends AppCompatActivity {


    /*
    Auth: Spondon
    Creates pane, collects info from others and handles nave bar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_breakdown); // make sure this layout exists

        // Get the intent extra
        String income = getIntent().getStringExtra("income");

        // Handle bottom nav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_transactions) {
                // TODO: When Transactions is ready, update this
                // Intent intent = new Intent(DetailedBreakdownActivity.this, TransactionsActivity.class);
                // intent.putExtra("income", income);
                // startActivity(intent);
            } else if (itemId == R.id.nav_overview) {
                Intent intent = new Intent(DetailedBreakdownActivity.this, OverviewActivity.class);
                intent.putExtra("income", income);
                startActivity(intent);
            } else if (itemId == R.id.nav_budget_sheet) {
                Intent intent = new Intent(DetailedBreakdownActivity.this, Expense.class);
                intent.putExtra("income", income);
                startActivity(intent);
            } else if (itemId == R.id.nav_detailed_breakdown) {
                Intent intent = new Intent(DetailedBreakdownActivity.this, DetailedBreakdownActivity.class);
                intent.putExtra("income", income);
                startActivity(intent);
            }
            return true;
        });
    }
}
