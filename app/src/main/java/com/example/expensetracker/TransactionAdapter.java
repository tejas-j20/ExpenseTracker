package com.example.expensetracker;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions;
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
        void onTransactionLongClick(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactions, OnTransactionClickListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(transaction);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onTransactionLongClick(transaction);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateList(List<Transaction> newList) {
        transactions = newList;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategory, tvAmount, tvDate, tvDescription;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        public void bind(Transaction transaction) {
            tvCategory.setText(transaction.getCategory());
            tvDate.setText(transaction.getDate());
            tvDescription.setText(transaction.getDescription());

            if (transaction.getType().equals("Income")) {
                tvAmount.setText("+ ₹" + String.format("%.2f", transaction.getAmount()));
                tvAmount.setTextColor(Color.parseColor("#2E7D32")); // Green
            } else {
                tvAmount.setText("- ₹" + String.format("%.2f", transaction.getAmount()));
                tvAmount.setTextColor(Color.parseColor("#C62828")); // Red
            }
        }
    }
}
