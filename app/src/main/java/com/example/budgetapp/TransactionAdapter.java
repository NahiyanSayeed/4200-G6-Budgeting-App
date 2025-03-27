package com.example.budgetapp;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.util.List;

public class TransactionAdapter extends BaseAdapter {

    private Context context;
    private List<Expense> transactions;
    private DBHelper dbHelper;

    public TransactionAdapter(Context context, List<Expense> transactions) {
        this.context = context;
        this.transactions = transactions;
        this.dbHelper = new DBHelper(context, "BudgetDB", null, 1);
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txtCategory, txtDescription, txtAmount;
        ImageButton btnDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Expense transaction = transactions.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
            holder = new ViewHolder();
            holder.txtCategory = convertView.findViewById(R.id.txt_category);
            holder.txtDescription = convertView.findViewById(R.id.txt_description);
            holder.txtAmount = convertView.findViewById(R.id.txt_amount);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtCategory.setText(transaction.getCategory());
        holder.txtDescription.setText(transaction.getDescription());
        holder.txtCategory.setText(transaction.getCategory());
        holder.txtDescription.setText(transaction.getDescription());

        double amount = transaction.getAmount();
        holder.txtAmount.setText(String.format("$%.2f", Math.abs(amount)));

        if (amount < 0) {
            holder.txtAmount.setTextColor(Color.parseColor("#4CAF50")); // Green for income
        } else {
            holder.txtAmount.setTextColor(Color.parseColor("#F44336")); // Red for expense
        }


        holder.btnDelete.setOnClickListener(v -> {
            dbHelper.deleteExpenseByDetails(transaction.getCategory(), transaction.getDescription(), transaction.getAmount());
            transactions.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Transaction deleted", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}

