package com.example.budgetapp;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.List;

public class TransactionAdapter extends BaseAdapter {

    private Context context;
    private List<Expense> transactions;

    public TransactionAdapter(Context context, List<Expense> transactions) {
        this.context = context;
        this.transactions = transactions;
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtCategory.setText(transaction.getCategory());
        holder.txtDescription.setText(transaction.getDescription());
        holder.txtAmount.setText(String.format("$%.2f", transaction.getAmount()));
        return convertView;
    }
}
