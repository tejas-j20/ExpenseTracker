package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
            startActivityForResult(intent, 1);
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
        // Handle long click - delete transaction
        dbHelper.deleteTransaction(transaction.getId());
        loadTransactions();
    }
}
