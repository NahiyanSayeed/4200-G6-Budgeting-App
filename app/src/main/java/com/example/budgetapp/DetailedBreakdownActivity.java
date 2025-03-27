package com.example.budgetapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

        int userId = getIntent().getIntExtra("userID", -1);
        GridLayout grid = findViewById(R.id.gridContainer);
        TextView summary = findViewById(R.id.budgetSummary);

        DBHelper dbHelper = new DBHelper(this, "BudgetDB", null, 1);
        List<Expense> allExpenses = dbHelper.getAllExpenses();

        final double totalSpent = calculateTotalSpent(allExpenses);
        final double budget = dbHelper.getBudgetById(userId);
        final double remaining = budget + totalSpent; //Addition because spent is already negative.

        summary.setText("Budget: $" + budget +
                " | Spent: $" + String.format("%.2f", totalSpent) +
                " | Left: $" + String.format("%.2f", remaining));

        // Define fixed category order
        final String[] orderedCategories = {"Housing", "Bills", "Food", "Transport", "Entertainment"};

        // Map to store expenses by category
        final Map<String, List<Expense>> categoryMap = new HashMap<>();
        for (String cat : orderedCategories) {
            categoryMap.put(cat, new ArrayList<>());
        }
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

            double percentage = (budget > 0) ? (total / budget) * 100 : 0;

            // Remaining allowance
            double remainingForCategory = budget * (percentage / 100) - total;
            TextView allowanceText = new TextView(this);
            allowanceText.setTextSize(14f);

            if (remainingForCategory < 0) {
                allowanceText.setText("Over budget by $" + String.format("%.2f", Math.abs(remainingForCategory)));
                allowanceText.setTextColor(Color.RED);
            } else {
                allowanceText.setText("Remaining: $" + String.format("%.2f", remainingForCategory));
                allowanceText.setTextColor(Color.DKGRAY);
            }

            container.addView(allowanceText);

            // Title + % of budget
            title.setText(category + "\n(Total: $" + String.format("%.2f", total) + ")" +
                    "\n" + String.format("%.1f", percentage) + "% of budget");

            if (percentage > 30) {
                title.setTextColor(Color.RED);
            }

            // Progress bar
            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            bar.setMax(100);
            bar.setProgress((int) percentage);

            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            barParams.setMargins(0, 8, 0, 16);
            bar.setLayoutParams(barParams);

            if (percentage > 80) {
                bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_red));
            } else if (percentage > 50) {
                bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_yellow));
            } else {
                bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_green));
            }

            container.addView(bar);

            // Expenses list
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
                params.columnSpec = GridLayout.spec(0, 2);
                params.width = GridLayout.LayoutParams.MATCH_PARENT;
            } else {
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.width = 0;
            }

            card.setLayoutParams(params);
            grid.addView(card);
        }

        // Bottom Nav Bar
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_transactions) {
                    Intent intent = new Intent(DetailedBreakdownActivity.this, TransactionsActivity.class);
                    intent.putExtra("userID", userId);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_overview) {
                    Intent intent = new Intent(DetailedBreakdownActivity.this, OverviewActivity.class);
                    intent.putExtra("userID", userId);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_budget_sheet) {
                    Intent intent = new Intent(DetailedBreakdownActivity.this, BudgetActivity.class);
                    intent.putExtra("userID", userId);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_detailed_breakdown) {
                    return true;
                }
                return false;
            }
        });

        // Share Summary Button
        Button shareBtn = findViewById(R.id.shareSummary);
        shareBtn.setOnClickListener(v -> {
            StringBuilder summaryText = new StringBuilder();
            summaryText.append("📊 Budget Summary\n");
            summaryText.append("Total Budget: $" + budget + "\n");
            summaryText.append("Total Spent: $" + String.format("%.2f", totalSpent) + "\n\n");

            for (String category : orderedCategories) {
                List<Expense> expenses = categoryMap.get(category);
                double catTotal = 0;
                for (Expense e : expenses) catTotal += e.getAmount();

                double percent = (budget > 0) ? (catTotal / budget) * 100 : 0;
                summaryText.append("• " + category + ": $" + String.format("%.2f", catTotal) +
                        " (" + String.format("%.1f", percent) + "% of budget)\n");
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Budget Breakdown");
            shareIntent.putExtra(Intent.EXTRA_TEXT, summaryText.toString());
            startActivity(Intent.createChooser(shareIntent, "Share Budget Breakdown"));
        });
    }

    private double calculateTotalSpent(List<Expense> expenses) {
        double total = 0;
        for (Expense e : expenses) {
            total += e.getAmount();
        }
        return total;
    }
}
