package com.example.budgetapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class BudgetActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText descriptionInput;
    private EditText amountInput;
    private Button submitButton;
    private ExpandableListView summaryListView;
    private TextView totalTextView;
    private List<Expense> expenseList;
    private List<String> categories;
    private double income;

    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_sheet);

        // Get income from MainActivity
        income = getIntent().getDoubleExtra("income", 0.0);

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner);
        descriptionInput = findViewById(R.id.descriptionInput);
        amountInput = findViewById(R.id.amountInput);
        submitButton = findViewById(R.id.submitButton);
        summaryListView = findViewById(R.id.summaryListView);
        totalTextView = findViewById(R.id.totalTextView);

        // Initialize data
        expenseList = new ArrayList<>();
        categories = new ArrayList<>();
        categories.add("Housing");
        categories.add("Bills");
        categories.add("Food");
        categories.add("Transport");
        categories.add("Entertainment");

        // Set up the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Handle submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = categorySpinner.getSelectedItem().toString();
                String description = descriptionInput.getText().toString();
                String amountText = amountInput.getText().toString();

                // Validate inputs
                if (description.isEmpty()) {
                    Toast.makeText(BudgetActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amountText.isEmpty()) {
                    Toast.makeText(BudgetActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountText);
                } catch (NumberFormatException e) {
                    Toast.makeText(BudgetActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount <= 0) {
                    Toast.makeText(BudgetActivity.this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the expense to the list
                expenseList.add(new Expense(category, description, amount));
                updateSummary();
                descriptionInput.setText(""); // Clear the description field
                amountInput.setText(""); // Clear the amount field
            }
        });

        // Set up the ExpandableListView adapter
        updateSummary();

        // Handles nav bar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_transactions) {
                //todo add stuff to transfer screens
            } else if (itemId == R.id.nav_overview) {
                //todo add stuff to transfer screens
            } else if (itemId == R.id.nav_budget_sheet) {
                //todo add stuff to transfer screens
            } else if (itemId == R.id.nav_detailed_breakdown) {
                //todo add stuff to transfer screens
            }
            return true;
        });
    }

    private void updateSummary() {
        // Calculate total expenses
        double totalExpenses = 0;
        for (Expense expense : expenseList) {
            totalExpenses += expense.getAmount();
        }

        // Update total TextView
        double remainingBudget = income - totalExpenses;
        totalTextView.setText(String.format("Total Expenses: $%.2f\nRemaining Budget: $%.2f", totalExpenses, remainingBudget));

        // Set up the ExpandableListView adapter
        BaseExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {  //must have to satisfy adapter BaseExpandableListAdapter
                return categories.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {  //must have to satisfy adapter BaseExpandableListAdapter
                String category = categories.get(groupPosition);
                int count = 0;
                for (Expense expense : expenseList) {
                    if (expense.getCategory().equals(category)) {
                        count++;
                    }
                }
                return count;
            }

            @Override
            public Object getGroup(int groupPosition) {  //must have to satisfy adapter BaseExpandableListAdapter
                return categories.get(groupPosition);
            }

            @Override   //this is to get the children, so we know the position, which category it is under
            public Object getChild(int groupPosition, int childPosition) {
                String category = categories.get(groupPosition);
                List<Expense> categoryExpenses = new ArrayList<>();
                for (Expense expense : expenseList) {
                    if (expense.getCategory().equals(category)) {
                        categoryExpenses.add(expense);
                    }
                }
                return categoryExpenses.get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {  //must have to satisfy adapter BaseExpandableListAdapter
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {  //must have to satisfy adapter BaseExpandableListAdapter
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {  //must have to satisfy adapter BaseExpandableListAdapter
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
                }
                TextView textView = convertView.findViewById(android.R.id.text1);
                String category = categories.get(groupPosition);
                double total = 0;
                for (Expense expense : expenseList) {
                    if (expense.getCategory().equals(category)) {
                        total += expense.getAmount();
                    }
                }
                textView.setText(String.format("%s (Total: $%.2f)", category, total));
                return convertView;
            }

            @Override   //getchildview is basically getting the elements from each of the categories.
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.expense_item, parent, false);
                }

                //we are populating each child with these values, a description, value and the trash icon for delete
                TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
                TextView amountTextView = convertView.findViewById(R.id.amountTextView);
                ImageView deleteIcon = convertView.findViewById(R.id.deleteIcon);

                //using the EXPENSE class to get child contents
                Expense expense = (Expense) getChild(groupPosition, childPosition);
                descriptionTextView.setText(expense.getDescription());
                amountTextView.setText(String.format("$%.2f", expense.getAmount()));
                //we are displaying the values

                //the trash icon is now clickable
                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmationDialog(expense);
                    }
                });

                //when user longclicks the element itself no buttons
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showEditExpenseDialog(expense);
                        return true;
                    }
                });

                return convertView;
            }

            @Override   //must have to satisfy adapter BaseExpandableListAdapter
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        };

        summaryListView.setAdapter(adapter);
    }

    private Expense selectedExpense;    //uses the Expense Class to retrieve expense VALUES
    private void showEditExpenseDialog(final Expense expense) { //basically spinner is the expandable dropdown

        // Hide the ExpandableListView we want to hide this incase user tries to select multiple items and positions
        summaryListView.setVisibility(View.GONE);

        // Populate the EditText fields we are jsut setting both the edit texts above with the contents of the selected eleement
        categorySpinner.setSelection(categories.indexOf(expense.getCategory()));
        descriptionInput.setText(expense.getDescription());
        amountInput.setText(String.valueOf(expense.getAmount()));

        // Change the submit button text
        submitButton.setText("Update");
        isEditMode = true;

        selectedExpense = expense;
        // when you click the submit button we will check if it meets the criteria before updating
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = categorySpinner.getSelectedItem().toString();
                String description = descriptionInput.getText().toString();
                String amountText = amountInput.getText().toString();

                if (description.isEmpty()) {    //checking for empty string
                    Toast.makeText(BudgetActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amountText.isEmpty()) { //checking for empty string
                    Toast.makeText(BudgetActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {   //if the amounts do not meet the criteria that it should be a double value
                    amount = Double.parseDouble(amountText);
                } catch (NumberFormatException e) {
                    Toast.makeText(BudgetActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount <= 0) {  //the amount cannot be 0
                    Toast.makeText(BudgetActivity.this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isEditMode && selectedExpense != null) {    //flags if user long holds an element/item it is flagged as editmode and an expense has been selected
                    // Handle updating an existing expense
                    selectedExpense.setCategory(category);
                    selectedExpense.setDescription(description);
                    selectedExpense.setAmount(amount);
                } else {
                    // Handle adding a new expense
                    expenseList.add(new Expense(category, description, amount));
                }

                //update summary just replaces the element wiht the new values in both edittexts description and amount
                updateSummary();

                //reset form is jsut setting the edittext to empty
                resetForm();
            }
        });
    }

    private void resetForm() {  //this is for whenever user presses submit, the edittext will be emptied
        // Clear input fields
        descriptionInput.setText("");
        amountInput.setText("");

        // Reset the submit button text and behavior
        submitButton.setText("Submit");
        isEditMode = false;
        selectedExpense = null; // Reset the selected expense

        // Show the ExpandableListView
        summaryListView.setVisibility(View.VISIBLE);
    }



    private void showDeleteConfirmationDialog(final Expense expense) {  //handling delete
        new AlertDialog.Builder(this)   //basic alert dialog making sure user wants to delete the element
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the expense from the list
                        expenseList.remove(expense);    //we jsut remove element from the arraylist

                        updateSummary();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}