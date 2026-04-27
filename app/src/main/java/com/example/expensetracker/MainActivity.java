package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TransactionAdapter.OnTransactionClickListener {
    private RecyclerView rvTransactions;
    private TextView tvTotalBalance, tvIncome, tvExpense, tvEmptyState;
    private FloatingActionButton fabAdd;
    private ExpenseDbHelper dbHelper;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        rvTransactions = findViewById(R.id.rvTransactions);
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvEmptyState = findViewById(R.id.emptyState);
        fabAdd = findViewById(R.id.fabAdd);

        Button btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

        Button btnInsights = findViewById(R.id.btnInsights);
        btnInsights.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, InsightsActivity.class))
        );

        Button btnStreak = findViewById(R.id.btnStreak);
        btnStreak.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, StreakActivity.class))
        );


        // Setup database
        dbHelper = new ExpenseDbHelper(this);

        // Setup RecyclerView
        adapter = new TransactionAdapter(transactionList, this);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        // Load transactions
        loadTransactions();



        // Setup FAB click listener
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

    }

    private void loadTransactions() {
        transactionList.clear();
        transactionList.addAll(dbHelper.getAllTransactions());
        adapter.updateList(transactionList);

        // Update summary
        updateSummary();

        // Show/hide empty state
        if (transactionList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
        }
    }

    private void updateSummary() {
        double totalIncome = dbHelper.getTotalIncome();
        double totalExpense = dbHelper.getTotalExpense();
        double balance = totalIncome - totalExpense;

        tvTotalBalance.setText("₹ " + String.format("%.2f", balance));
        tvIncome.setText("+ ₹ " + String.format("%.2f", totalIncome));
        tvExpense.setText("- ₹ " + String.format("%.2f", totalExpense));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadTransactions(); // Refresh after adding new transaction
        }
    }

    @Override
    public void onTransactionClick(Transaction transaction) {
        // Handle click (optional - could open edit screen)
    }

    @Override
    public void onTransactionLongClick(Transaction transaction) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?\n\n"
                        + "📁 " + transaction.getCategory() + "\n"
                        + "💰 ₹" + String.format("%.2f", transaction.getAmount()))
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteTransaction(transaction.getId());
                    loadTransactions();
                    showDeleteSuccessMessage();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    private void showDeleteSuccessMessage() {
        android.widget.Toast.makeText(
                this,
                "Transaction deleted successfully",
                android.widget.Toast.LENGTH_SHORT
        ).show();
    }


}
