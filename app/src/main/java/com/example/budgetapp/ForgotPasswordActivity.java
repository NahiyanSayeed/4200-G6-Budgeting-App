package com.example.budgetapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText usernameInput, newPasswordInput, confirmPasswordInput;
    Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        usernameInput = findViewById(R.id.usernameInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        DBHelper dbHelper = new DBHelper(this, "BudgetDB", null, 2);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String newPass = newPasswordInput.getText().toString();
                String confirmPass = confirmPasswordInput.getText().toString();

                if (username.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass.equals(confirmPass)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Account> accounts = dbHelper.getAllAccount();
                boolean updated = false;

                for (Account acc : accounts) {
                    if (acc.getUsername().equals(username)) {
                        acc.setPassword(newPass);
                        dbHelper.deleteAccountData(username);
                        dbHelper.addAccount(username, newPass);
                        updated = true;
                        break;
                    }
                }

                if (updated) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password updated!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
