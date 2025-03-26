package com.example.budgetapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DetailedBreakdownActivity extends AppCompatActivity {


    /*
    Auth: Spondon
    Creates pane, collects info from others and handles nave bar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_breakdown); // make sure this layout exists

        LinearLayout breakdownContainer = findViewById(R.id.breakdownContainer);
        DBHelper dbHelper = new DBHelper(this, "BudgetDB", null, 1);
        List<Expense> allExpenses = dbHelper.getAllExpenses();
        Map<String, List<Expense>> grouped = new LinkedHashMap<>();

        double totalSpent = 0;
        // Group expenses by category
        for (Expense expense : allExpenses) {
            totalSpent += expense.getAmount();
            if (!grouped.containsKey(expense.getCategory())) {
                grouped.put(expense.getCategory(), new ArrayList<>());
            }
            grouped.get(expense.getCategory()).add(expense);
        }
        for (Map.Entry<String, List<Expense>> entry : grouped.entrySet()) {
            String category = entry.getKey();
            List<Expense> expenses = entry.getValue();

            // Category header
            TextView categoryHeader = new TextView(this);
            categoryHeader.setText(category);
            categoryHeader.setTextSize(20f);
            categoryHeader.setPadding(0, 24, 0, 8);
            categoryHeader.setTextColor(getResources().getColor(android.R.color.black));
            categoryHeader.setTypeface(null, Typeface.BOLD);
            breakdownContainer.addView(categoryHeader);

            // Expenses under category
            for (Expense e : expenses) {
                View itemView = getLayoutInflater().inflate(R.layout.expense_item, null);

                TextView descriptionText = itemView.findViewById(R.id.descriptionTextView);
                TextView amountText = itemView.findViewById(R.id.amountTextView);
                ImageView deleteIcon = itemView.findViewById(R.id.deleteIcon);

                // Hide delete icon (for read-only breakdown)
                deleteIcon.setVisibility(View.GONE);

                descriptionText.setText(e.getDescription());
                amountText.setText(String.format("$%.2f", e.getAmount()));

                breakdownContainer.addView(itemView);
            }
        }
        // Add total at the bottom
        TextView totalText = new TextView(this);
        totalText.setText(String.format("\nTotal Spent: $%.2f", totalSpent));
        totalText.setTextSize(18f);
        totalText.setTypeface(null, Typeface.BOLD);
        totalText.setPadding(0, 32, 0, 32);
        totalText.setTextColor(getResources().getColor(android.R.color.black));
        breakdownContainer.addView(totalText);

        // Handle bottom nav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_overview) {
                startActivity(new Intent(this, OverviewActivity.class));
            } else if (itemId == R.id.nav_budget_sheet) {
                startActivity(new Intent(this, BudgetActivity.class));
            }
            return true;
        });

    }
}
