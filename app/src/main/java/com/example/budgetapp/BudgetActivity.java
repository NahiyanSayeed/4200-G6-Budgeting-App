package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {

    private ExpandableListView summaryListView;
    private TextView budgetSummaryTextView;
    private List<Expense> allExpenses;
    private List<String> categories;
    private DBHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_sheet);

        dbHelper = new DBHelper(this, "BudgetDB", null, 1);
        userId = getIntent().getIntExtra("userId", -1);

        // Initialize views
        summaryListView = findViewById(R.id.summaryListView);
        budgetSummaryTextView = findViewById(R.id.budgetSummaryTextView);

        // Initialize categories
        categories = new ArrayList<>();
        categories.add("Housing");
        categories.add("Bills");
        categories.add("Food");
        categories.add("Transport");
        categories.add("Entertainment");
        categories.add("Other");

        // Load expenses from database
        loadExpensesFromDatabase();

        // Setup navigation
        setupNavigation();
    }

    private void loadExpensesFromDatabase() {
        allExpenses = dbHelper.getAllExpenses();
        updateSummary();
    }

    private void updateSummary() {
        // Get initial budget and income from database
        final double initialBudget = dbHelper.getBudgetById(userId);
        //double totalIncome = dbHelper.getIncomeById(userId);

        // Calculate total expenses
        double totalExpenses = 0;
        for (Expense expense : allExpenses) {
            totalExpenses += expense.getAmount();
        }

        double remainingBudget = initialBudget - totalExpenses;

        // Set summary text
        budgetSummaryTextView.setText(String.format(
                "Initial Budget: $%.2f\n" +
                        "Total Expenses: $%.2f\n" +
                        "Remaining Budget: $%.2f",
                initialBudget, totalExpenses, remainingBudget
        ));

        // Create adapter for ExpandableListView
        BaseExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            // Map to store expenses by category
            private Map<String, List<Expense>> expensesByCategory = new HashMap<>();

            {
                // Initialize the map with empty lists for each category
                for (String category : categories) {
                    expensesByCategory.put(category, new ArrayList<>());
                }

                // Populate the map with expenses
                for (Expense expense : allExpenses) {
                    String category = expense.getCategory();
                    if (expensesByCategory.containsKey(category)) {
                        expensesByCategory.get(category).add(expense);
                    } else {
                        // If category not in our list, add to "Other"
                        expensesByCategory.get("Other").add(expense);
                    }
                }
            }

            @Override
            public int getGroupCount() {
                return categories.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                String category = categories.get(groupPosition);
                return expensesByCategory.get(category).size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return categories.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                String category = categories.get(groupPosition);
                return expensesByCategory.get(category).get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(
                            android.R.layout.simple_expandable_list_item_1, parent, false);
                }
                TextView textView = convertView.findViewById(android.R.id.text1);
                String category = categories.get(groupPosition);

                // Calculate total for this category
                double total = 0;
                for (Expense expense : expensesByCategory.get(category)) {
                    total += expense.getAmount();
                }

                textView.setText(String.format("%s (Total: $%.2f)", category, total));
                return convertView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(
                            R.layout.expense_item_display, parent, false);
                }

                TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
                TextView amountTextView = convertView.findViewById(R.id.amountTextView);

                Expense expense = (Expense) getChild(groupPosition, childPosition);
                descriptionTextView.setText(expense.getDescription());

                // Format amount with - for expenses (since all are expenses in this implementation)
                amountTextView.setText(String.format("-$%.2f", expense.getAmount()));
                amountTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                return convertView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return false;
            }
        };

        summaryListView.setAdapter(adapter);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_transactions) {
                startActivity(new Intent(this, TransactionsActivity.class)
                        .putExtra("userId", userId));
            } else if (itemId == R.id.nav_overview) {
                startActivity(new Intent(this, OverviewActivity.class)
                        .putExtra("userId", userId));
            } else if (itemId == R.id.nav_detailed_breakdown) {
                startActivity(new Intent(this, DetailedBreakdownActivity.class)
                        .putExtra("userId", userId));
            }
            return true;
        });
    }
}