package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    private ListView transactionListView;
    private DBHelper dbHelper;
    private List<Expense> transactions;
    private int userId = 1; // Replace with actual user ID if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        //Grab userID from intent
        userId = getIntent().getIntExtra("userID", -1);

        // Initialize views
        transactionListView = findViewById(R.id.transaction_list);
        FloatingActionButton fab = findViewById(R.id.fab_add_transaction);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Setup DB + Load List
        dbHelper = new DBHelper(this, "BudgetDB", null, 1);
        loadTransactions();

        // FAB to open AddTransactionActivity
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(TransactionsActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        // Setup bottom nav
        bottomNav.setSelectedItemId(R.id.nav_transactions);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_transactions) {
                return true;
            } else if (itemId == R.id.nav_overview) {
                startActivity(new Intent(this, OverviewActivity.class).putExtra("userID", userId));
                return true;
            } else if (itemId == R.id.nav_budget_sheet) {
                startActivity(new Intent(this, BudgetActivity.class).putExtra("userID", userId));
                return true;
            } else if (itemId == R.id.nav_detailed_breakdown) {
                startActivity(new Intent(this, DetailedBreakdownActivity.class).putExtra("userID", userId));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions(); // Refresh when returning from AddTransactionActivity
    }

    private void loadTransactions() {
        transactions = dbHelper.getExpensesByUserId(userId);
        TransactionAdapter adapter = new TransactionAdapter(this, transactions);
        transactionListView.setAdapter(adapter);
    }
}
