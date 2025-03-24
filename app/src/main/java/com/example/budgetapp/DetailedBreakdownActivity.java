package com.example.budgetapp;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DetailedBreakdownActivity extends AppCompatActivity {



    BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_transactions) {
//                Intent intent = new Intent(BudgetActivity.this, DetailedBreakdownActivity.class);
//                intent.putExtra("income", income);
//                startActivity(intent);
            //todo When transactions is made make a swap here
        } else if (itemId == R.id.nav_overview) {
            Intent intent = new Intent(BudgetActivity.this, OverviewActivity.class);
            intent.putExtra("income", income);
            startActivity(intent);
        } else if (itemId == R.id.nav_budget_sheet) {
            Intent intent = new Intent(BudgetActivity.this, Expense.class);
            intent.putExtra("income", income);
            startActivity(intent);
        } else if (itemId == R.id.nav_detailed_breakdown) {
            Intent intent = new Intent(BudgetActivity.this, DetailedBreakdownActivity.class);
            intent.putExtra("income", income);
            startActivity(intent);
        }
        return true;
    });
}
