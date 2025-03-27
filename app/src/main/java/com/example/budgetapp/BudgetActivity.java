package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    public DBHelper dbHelper;
    private int userId;
    private BudgetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_sheet);

        dbHelper = new DBHelper(this, "BudgetDB", null, 1);
        userId = getIntent().getIntExtra("userID", -1);

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
        categories.add("Income"); // Add Income as a category

        // Initialize adapter
        adapter = new BudgetAdapter();
        summaryListView.setAdapter(adapter);

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
        Log.d("BudgetDebug", "Attempting to get budget for userID: " + userId);
        // Get budget and income from database
        final double initialBudget = dbHelper.getBudgetById(userId);
        final double totalIncome = dbHelper.getIncomeById(userId);
        Log.d("BudgetDebug", "Initial Budget: " + initialBudget + ", Income: " + totalIncome);

        // Calculate total expenses
        double totalExpenses = 0;
        for (Expense expense : allExpenses) {
            totalExpenses += expense.getAmount();
        }

        // Calculate remaining budget (initial budget + income - expenses)
        double remainingBudget = initialBudget + totalIncome + totalExpenses;

        // Set summary text
        budgetSummaryTextView.setText(String.format(
                "Initial Budget: $%.2f\n" +
                        "Total Income: $%.2f\n" +
                        "Total Expenses: $%.2f\n" +
                        "Remaining Budget: $%.2f",
                initialBudget, totalIncome, totalExpenses, remainingBudget
        ));

        // Notify adapter of data changes
        adapter.updateData(allExpenses, totalIncome);
    }

    // Custom adapter class
    private class BudgetAdapter extends BaseExpandableListAdapter {
        private Map<String, List<Expense>> expensesByCategory = new HashMap<>();
        private double totalIncome;

        public BudgetAdapter() {
            // Initialize the map with empty lists for each category
            for (String category : categories) {
                expensesByCategory.put(category, new ArrayList<>());
            }
        }

        public void updateData(List<Expense> newExpenses, double income) {
            this.totalIncome = income;

            // Clear existing data
            for (List<Expense> expenseList : expensesByCategory.values()) {
                expenseList.clear();
            }

            // Populate with new data
            for (Expense expense : newExpenses) {
                String category = expense.getCategory();
                if (expensesByCategory.containsKey(category)) {
                    expensesByCategory.get(category).add(expense);
                } else {
                    expensesByCategory.get("Other").add(expense);
                }
            }

            // Add income as a special "expense" (but with positive amount)
            if (totalIncome > 0) {
                expensesByCategory.get("Income").add(new Expense("Income", "Total Income", totalIncome));
            }

            notifyDataSetChanged();
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

            // Set text (format Income with +$)
            if (category.equals("Income")) {
                textView.setText(String.format("%s: +$%.2f", category, total));
            } else {
                textView.setText(String.format("%s: $%.2f", category, total));
                // Do NOT set color here → keeps default theme color
            }

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

            // Format amount differently for income vs expenses
            if (expense.getCategory().equals("Income")) {
                amountTextView.setText(String.format("+$%.2f", expense.getAmount()));
            } else {
                amountTextView.setText(String.format("-$%.2f", expense.getAmount()));
                amountTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_transactions) {
                startActivity(new Intent(this, TransactionsActivity.class)
                        .putExtra("userID", userId));
            } else if (itemId == R.id.nav_overview) {
                startActivity(new Intent(this, OverviewActivity.class)
                        .putExtra("userID", userId));
            } else if (itemId == R.id.nav_detailed_breakdown) {
                startActivity(new Intent(this, DetailedBreakdownActivity.class)
                        .putExtra("userID", userId));
            }
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}