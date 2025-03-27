package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BudgetSetupActivity extends AppCompatActivity {

    EditText budgetInput;
    Button saveBudgetButton;
    DBHelper dbHelper;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_setup);

        budgetInput = findViewById(R.id.budgetInput);
        saveBudgetButton = findViewById(R.id.saveBudgetButton);
        dbHelper = new DBHelper(this, "BudgetDB", null, 2);

        userId = getIntent().getIntExtra("userID", -1);

        saveBudgetButton.setOnClickListener(v -> {
            String budgetStr = budgetInput.getText().toString();
            if (budgetStr.isEmpty()) {
                Toast.makeText(this, "Please enter a budget", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double budget = Double.parseDouble(budgetStr);
                dbHelper.deleteBudgetData(userId);
                dbHelper.addBudget(userId, budget);
                Toast.makeText(this, "Budget saved!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(BudgetSetupActivity.this, OverviewActivity.class);
                intent.putExtra("userID", userId);
                startActivity(intent);
                finish();
            }catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid budget format. Please enter valid number", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
