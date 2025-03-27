package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {

    Spinner typeSpinner, categorySpinner;
    EditText amountInput, descriptionInput;
    Button saveButton;
    DBHelper dbHelper;
    int userId = 1;
    int expenseNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        typeSpinner = findViewById(R.id.spinner_type);
        categorySpinner = findViewById(R.id.spinner_category);
        amountInput = findViewById(R.id.edit_amount);
        descriptionInput = findViewById(R.id.edit_description);
        saveButton = findViewById(R.id.button_save);

        dbHelper = new DBHelper(this, "BudgetDB", null, 2);

        //Grab userID from intent
        userId = getIntent().getIntExtra("userID", -1);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_types, android.R.layout.simple_spinner_item);
        typeSpinner.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_categories, android.R.layout.simple_spinner_item);
        categorySpinner.setAdapter(categoryAdapter);

        saveButton.setOnClickListener(v -> {
            String type = typeSpinner.getSelectedItem().toString();
            String category = categorySpinner.getSelectedItem().toString();
            String desc = descriptionInput.getText().toString();
            String amountText = amountInput.getText().toString();

            if (amountText.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountText);
            if (type.equals("Income")) amount *= -1;

            dbHelper.addExpense(userId, category, desc, amount);
            Toast.makeText(this, "Transaction Saved", Toast.LENGTH_SHORT).show();
            finish(); // Go back to TransactionsActivity
        });
    }
}
