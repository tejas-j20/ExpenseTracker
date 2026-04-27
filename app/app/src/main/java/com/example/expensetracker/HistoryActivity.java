package com.example.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity
        implements TransactionAdapter.OnTransactionClickListener {

    private RecyclerView rvHistory;
    private Spinner spinnerMonth, spinnerType;
    private TextView tvHistoryIncome, tvHistoryExpense, tvHistoryEmpty;
    private ExpenseDbHelper dbHelper;
    private TransactionAdapter adapter;
    private List<Transaction> filteredList = new ArrayList<>();

    // Filter state
    private String selectedMonth = "All";
    private String selectedType  = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Views
        rvHistory       = findViewById(R.id.rvHistory);
        spinnerMonth    = findViewById(R.id.spinnerMonth);
        spinnerType     = findViewById(R.id.spinnerType);
        tvHistoryIncome = findViewById(R.id.tvHistoryIncome);
        tvHistoryExpense= findViewById(R.id.tvHistoryExpense);
        tvHistoryEmpty  = findViewById(R.id.tvHistoryEmpty);

        dbHelper = new ExpenseDbHelper(this);

        // RecyclerView
        adapter = new TransactionAdapter(filteredList, this);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);

        setupMonthSpinner();
        setupTypeSpinner();
    }

    // ── Month Spinner ────────────────────────────────────────────────────────
    private void setupMonthSpinner() {
        List<String> months = new ArrayList<>();
        months.add("All");

        // Build last 12 months dynamically
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 12; i++) {
            int year  = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // 1-based
            months.add(String.format("%d-%02d", year, month));
            cal.add(Calendar.MONTH, -1);
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedMonth = months.get(pos);
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    // ── Type Spinner ─────────────────────────────────────────────────────────
    private void setupTypeSpinner() {
        String[] types = {"All", "Income", "Expense"};

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedType = types[pos];
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    // ── Apply Both Filters ───────────────────────────────────────────────────
    private void applyFilters() {
        List<Transaction> base;

        // Step 1: filter by type
        if (selectedType.equals("All")) {
            base = dbHelper.getAllTransactions();
        } else {
            base = dbHelper.getTransactionsByType(selectedType);
        }

        // Step 2: filter by month (client-side on top of type result)
        filteredList.clear();
        for (Transaction t : base) {
            if (selectedMonth.equals("All") || t.getDate().startsWith(selectedMonth)) {
                filteredList.add(t);
            }
        }

        adapter.updateList(filteredList);

        // Update summary strip
        double income  = 0, expense = 0;
        for (Transaction t : filteredList) {
            if (t.getType().equals("Income"))  income  += t.getAmount();
            else                               expense += t.getAmount();
        }
        tvHistoryIncome .setText("▲ ₹" + String.format("%.2f", income));
        tvHistoryExpense.setText("▼ ₹" + String.format("%.2f", expense));

        // Empty state
        if (filteredList.isEmpty()) {
            tvHistoryEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvHistoryEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onTransactionClick(Transaction transaction) {
        // Optional: open detail/edit screen later
    }

    @Override
    public void onTransactionLongClick(Transaction transaction) {
        dbHelper.deleteTransaction(transaction.getId());
        applyFilters(); // Refresh after delete
    }
}
