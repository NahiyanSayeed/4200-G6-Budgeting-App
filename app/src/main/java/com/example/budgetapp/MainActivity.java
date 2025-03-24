package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    EditText editIncome;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSubmit = findViewById(R.id.btn_submit);
        editIncome = findViewById(R.id.editText_income);



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String incomeText = editIncome.getText().toString();
                if (incomeText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your income", Toast.LENGTH_SHORT).show();
                    return;
                }

                double income;
                try {
                    income = Double.parseDouble(incomeText);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid income", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (income <= 0) {
                    Toast.makeText(MainActivity.this, "Income must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, OverviewActivity.class);

                //Intent intent = new Intent(MainActivity.this, Test.class);

                intent.putExtra("income", income);
                startActivity(intent);
            }
        });

        // Handles nav bar
//        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
//        bottomNav.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.nav_transactions) {
//                //
//            } else if (itemId == R.id.nav_overview) {
//                //
//            } else if (itemId == R.id.nav_budget_sheet) {
//                //
//            } else if (itemId == R.id.nav_detailed_breakdown) {
//                //
//            }
//            return true;
//        });


    }
}