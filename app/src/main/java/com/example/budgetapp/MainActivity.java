package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;


public class MainActivity extends AppCompatActivity {

//    EditText editIncome;
//    Button btnSubmit;

    EditText editUsername, editPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        btnSubmit = findViewById(R.id.btn_submit);
//        editIncome = findViewById(R.id.editText_income);

        //replaced with new buttons/fields
        editUsername = findViewById(R.id.editText_username);
        editPassword = findViewById(R.id.editText_password);
        btnLogin = findViewById(R.id.btn_login);

        DBHelper dbHelper = new DBHelper(this, "BudgetDB", null, 2);

        TextView registerText = findViewById(R.id.registerText);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        TextView forgotText = findViewById(R.id.forgotText);
        forgotText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                //stops empty fields
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check credentials
                List<Account> accounts = dbHelper.getAllAccount();
                boolean matchFound = false;
                int userId = -1;

                for (Account acc : accounts) {
                    if (acc.getUsername().equals(username) && acc.getPassword().equals(password)) {
                        matchFound = true;
                        userId = acc.getId();
                        break;
                    }
                }

                if (matchFound) {
                    Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, OverviewActivity.class);
                    intent.putExtra("userID", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });



//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String incomeText = editIncome.getText().toString();
//                if (incomeText.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Please enter your income", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                double income;
//                try {
//                    income = Double.parseDouble(incomeText);
//                } catch (NumberFormatException e) {
//                    Toast.makeText(MainActivity.this, "Invalid income", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (income <= 0) {
//                    Toast.makeText(MainActivity.this, "Income must be greater than 0", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Intent intent = new Intent(MainActivity.this, OverviewActivity.class);
//
//                //Intent intent = new Intent(MainActivity.this, Test.class);
//
//                intent.putExtra("income", income);
//                startActivity(intent);
//            }
//        });

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