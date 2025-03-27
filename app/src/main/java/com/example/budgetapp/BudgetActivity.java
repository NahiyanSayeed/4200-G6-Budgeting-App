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

        dbHelper = new DBHelper(this, "BudgetDB", null, 2);
        userId = getIntent().getIntExtra("userID", -1);

        // Initialize views
        summaryListView = findViewById(R.id.summaryListView);
        budgetSummaryTextView = findViewById(R.id.budgetSummaryTextView);

        // Initialize categories
        categories = new ArrayList<>();
        categories.add("Income");
        categories.add("Housing");
        categories.add("Bills");
        categories.add("Food");
        categories.add("Transport");
        categories.add("Entertainment");
        categories.add("Other");

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
        final double initialBudget = dbHelper.getBudgetById(userId);

        // Separate income and expenses
        double totalIncome = 0;
        double totalExpenses = 0;
        List<Expense> incomeList = new ArrayList<>();
        List<Expense> expenseList = new ArrayList<>();

        for (Expense expense : allExpenses) {
            if (expense.getAmount() < 0) {
                totalIncome += Math.abs(expense.getAmount());
                incomeList.add(expense);
            } else {
                totalExpenses += expense.getAmount();
                expenseList.add(expense);
            }
        }


        double remainingBudget = initialBudget + totalIncome - totalExpenses;

        budgetSummaryTextView.setText(String.format(
                "Initial Budget: $%.2f\n" +
                        "Total Income: $%.2f\n" +
                        "Total Expenses: $%.2f\n" +
                        "Remaining Budget: $%.2f",
                initialBudget, Math.abs(totalIncome), totalExpenses, remainingBudget
        ));

        adapter.updateData(incomeList, expenseList, totalIncome);
    }

    private class BudgetAdapter extends BaseExpandableListAdapter {
        private Map<String, List<Expense>> expensesByCategory = new HashMap<>();
        private List<Expense> incomeItems = new ArrayList<>();
        private double totalIncome;

        public BudgetAdapter() {
            for (String category : categories) {
                expensesByCategory.put(category, new ArrayList<>());
            }
        }

        public void updateData(List<Expense> incomeList, List<Expense> expenseList, double income) {
            this.totalIncome = income;
            this.incomeItems = incomeList;

            // Clear existing data
            for (List<Expense> list : expensesByCategory.values()) {
                list.clear();
            }

            // Populate expenses by category
            for (Expense expense : expenseList) {
                String category = expense.getCategory();
                if (expensesByCategory.containsKey(category)) {
                    expensesByCategory.get(category).add(expense);
                } else {
                    expensesByCategory.get("Other").add(expense);
                }
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
            if (category.equals("Income")) {
                return incomeItems.size();
            }
            return expensesByCategory.get(category).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return categories.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String category = categories.get(groupPosition);
            if (category.equals("Income")) {
                return incomeItems.get(childPosition);
            }
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

            double total = 0;
            if (category.equals("Income")) {
                total = totalIncome;
            } else {
                for (Expense expense : expensesByCategory.get(category)) {
                    total += expense.getAmount();  // Expenses are negative
                }
            }

            if (category.equals("Income")) {
                textView.setText(String.format("%s: +$%.2f", category, total));
            } else {
                textView.setText(String.format("%s: -$%.2f", category, Math.abs(total)));
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

            if (categories.get(groupPosition).equals("Income")) {
                amountTextView.setText(String.format("+$%.2f", Math.abs(expense.getAmount())));
                amountTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                amountTextView.setText(String.format("-$%.2f", Math.abs(expense.getAmount())));
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