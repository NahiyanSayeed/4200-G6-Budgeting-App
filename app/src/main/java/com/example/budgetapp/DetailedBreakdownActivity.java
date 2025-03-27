// DetailedBreakdownActivity.java
package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailedBreakdownActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_breakdown);

        GridLayout grid = findViewById(R.id.gridContainer);
        DBHelper dbHelper = new DBHelper(this, "BudgetDB", null, 1);
        List<Expense> allExpenses = dbHelper.getAllExpenses();

        // Define fixed category order
        String[] orderedCategories = {"Housing", "Bills", "Food", "Transport", "Entertainment"};

        // Map to store expenses by category
        Map<String, List<Expense>> categoryMap = new HashMap<>();
        for (String cat : orderedCategories) {
            categoryMap.put(cat, new ArrayList<>());
        }

        // Group existing expenses into categories
        for (Expense e : allExpenses) {
            if (categoryMap.containsKey(e.getCategory())) {
                categoryMap.get(e.getCategory()).add(e);
            }
        }

        // Loop through fixed order and create cards
        for (String category : orderedCategories) {
            List<Expense> expenses = categoryMap.get(category);
            double total = 0;
            for (Expense e : expenses) {
                total += e.getAmount();
            }

            View card = getLayoutInflater().inflate(R.layout.card_category, null);
            TextView title = card.findViewById(R.id.categoryTitle);
            LinearLayout container = card.findViewById(R.id.expensesContainer);

            title.setText(category + "\n" + "(Total: $" + String.format("%.2f", total) + ")");

            for (Expense exp : expenses) {
                TextView item = new TextView(this);
                item.setText("• $" + exp.getAmount());
                item.setTextSize(14f);
                container.addView(item);
            }

            card.setOnClickListener(v -> {
                container.setVisibility(container.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();

            if (category.equals("Entertainment")) {
                // Span full width if it's the last card
                params.columnSpec = GridLayout.spec(0, 2); // span 2 columns
                params.width = GridLayout.LayoutParams.MATCH_PARENT;
            } else {
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.width = 0;
            }

            card.setLayoutParams(params);

            grid.addView(card);
        }

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_transactions) {
                    startActivity(new Intent(DetailedBreakdownActivity.this, TransactionsActivity.class));
                    return true;
                } else if (id == R.id.nav_overview) {
                    startActivity(new Intent(DetailedBreakdownActivity.this, OverviewActivity.class));
                    return true;
                } else if (id == R.id.nav_budget_sheet) {
                    startActivity(new Intent(DetailedBreakdownActivity.this, BudgetActivity.class));
                    return true;
                } else if (id == R.id.nav_detailed_breakdown) {
                    return true;
                }
                return false;
            }
        });
    }
}

