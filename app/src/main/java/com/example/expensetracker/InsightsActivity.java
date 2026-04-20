package com.example.expensetracker;

import android.graphics.Color;
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

public class InsightsActivity extends AppCompatActivity {

    private TextView tvNetSavings, tvSummaryIncome, tvSummaryExpense, tvSaveRate;
    private RecyclerView rvInsights;
    private Spinner spinnerInsightMonth;
    private ExpenseDbHelper dbHelper;
    private InsightAdapter insightAdapter;
    private List<InsightCard> insightCards = new ArrayList<>();
    private String selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarInsights);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Views
        tvNetSavings    = findViewById(R.id.tvNetSavings);
        tvSummaryIncome = findViewById(R.id.tvSummaryIncome);
        tvSummaryExpense= findViewById(R.id.tvSummaryExpense);
        tvSaveRate      = findViewById(R.id.tvSaveRate);
        rvInsights      = findViewById(R.id.rvInsights);
        spinnerInsightMonth = findViewById(R.id.spinnerInsightMonth);

        dbHelper = new ExpenseDbHelper(this);

        // Default to current month
        Calendar cal = Calendar.getInstance();
        selectedMonth = String.format("%d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);

        // RecyclerView
        insightAdapter = new InsightAdapter(insightCards);
        rvInsights.setLayoutManager(new LinearLayoutManager(this));
        rvInsights.setAdapter(insightAdapter);

        setupMonthSpinner();
    }

    // ── Month Spinner ─────────────────────────────────────────────────────────
    private void setupMonthSpinner() {
        List<String> months = new ArrayList<>();
        months.add("All Time");

        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 12; i++) {
            months.add(String.format("%d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1));
            cal.add(Calendar.MONTH, -1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInsightMonth.setAdapter(adapter);

        // Default to index 1 = current month
        spinnerInsightMonth.setSelection(1);

        spinnerInsightMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedMonth = months.get(pos);
                loadInsights();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    // ── Core Insight Engine ───────────────────────────────────────────────────
    private void loadInsights() {
        insightCards.clear();

        // Pull transactions for selected period
        List<Transaction> txList;
        if (selectedMonth.equals("All Time")) {
            txList = dbHelper.getAllTransactions();
        } else {
            txList = dbHelper.getTransactionsByMonth(selectedMonth);
        }

        // ── Aggregate ──────────────────────────────────────────────────────
        double totalIncome  = 0, totalExpense = 0;
        java.util.Map<String, Double> categoryMap = new java.util.LinkedHashMap<>();

        for (Transaction t : txList) {
            if (t.getType().equals("Income")) {
                totalIncome += t.getAmount();
            } else {
                totalExpense += t.getAmount();
                categoryMap.merge(t.getCategory(), t.getAmount(), Double::sum);
            }
        }

        double savings  = totalIncome - totalExpense;
        double saveRate = totalIncome > 0 ? (savings / totalIncome) * 100 : 0;

        // ── Hero Card ──────────────────────────────────────────────────────
        tvNetSavings.setText((savings >= 0 ? "+ ₹" : "- ₹") +
                String.format("%.2f", Math.abs(savings)));
        tvNetSavings.setTextColor(savings >= 0
                ? Color.parseColor("#A5D6A7")
                : Color.parseColor("#EF9A9A"));
        tvSummaryIncome .setText("₹" + String.format("%.2f", totalIncome));
        tvSummaryExpense.setText("₹" + String.format("%.2f", totalExpense));
        tvSaveRate      .setText(String.format("%.1f%%", saveRate));

        // ── Insight Card 1: Transaction Count ─────────────────────────────
        insightCards.add(new InsightCard(
                "Transactions",
                String.valueOf(txList.size()),
                selectedMonth.equals("All Time") ? "All time" : "This period",
                Color.parseColor("#6200EE"),
                "📊"
        ));

        // ── Insight Card 2: Top Spending Category ─────────────────────────
        String topCat   = "N/A";
        double topAmt   = 0;
        for (java.util.Map.Entry<String, Double> e : categoryMap.entrySet()) {
            if (e.getValue() > topAmt) { topAmt = e.getValue(); topCat = e.getKey(); }
        }
        insightCards.add(new InsightCard(
                "Top Expense Category",
                topCat,
                topAmt > 0 ? "₹" + String.format("%.2f", topAmt) + " spent" : "No expenses yet",
                Color.parseColor("#C62828"),
                "🔥"
        ));

        // ── Insight Card 3: Saving Rate ────────────────────────────────────
        String savingMsg;
        int savingColor;
        if (saveRate >= 30)      { savingMsg = "Excellent saving habit! 🎉"; savingColor = Color.parseColor("#2E7D32"); }
        else if (saveRate >= 10) { savingMsg = "Good — keep it up!";          savingColor = Color.parseColor("#F57F17"); }
        else if (saveRate > 0)   { savingMsg = "Try to save more this month"; savingColor = Color.parseColor("#E65100"); }
        else                     { savingMsg = "Expenses exceed income ⚠️";   savingColor = Color.parseColor("#B71C1C"); }

        insightCards.add(new InsightCard(
                "Saving Rate",
                String.format("%.1f%%", saveRate),
                savingMsg,
                savingColor,
                "💰"
        ));

        // ── Insight Card 4: Biggest Single Expense ────────────────────────
        Transaction biggestExpense = null;
        for (Transaction t : txList) {
            if (t.getType().equals("Expense")) {
                if (biggestExpense == null || t.getAmount() > biggestExpense.getAmount()) {
                    biggestExpense = t;
                }
            }
        }
        insightCards.add(new InsightCard(
                "Biggest Expense",
                biggestExpense != null ? "₹" + String.format("%.2f", biggestExpense.getAmount()) : "N/A",
                biggestExpense != null ? biggestExpense.getCategory() + " — " + biggestExpense.getDate() : "No expenses yet",
                Color.parseColor("#AD1457"),
                "💸"
        ));

        // ── Insight Card 5: Category Breakdown ────────────────────────────
        if (!categoryMap.isEmpty()) {
            StringBuilder breakdown = new StringBuilder();
            for (java.util.Map.Entry<String, Double> e : categoryMap.entrySet()) {
                double pct = totalExpense > 0 ? (e.getValue() / totalExpense) * 100 : 0;
                breakdown.append(e.getKey())
                        .append(": ")
                        .append(String.format("%.1f%%", pct))
                        .append("\n");
            }
            insightCards.add(new InsightCard(
                    "Expense Breakdown",
                    categoryMap.size() + " categories",
                    breakdown.toString().trim(),
                    Color.parseColor("#1565C0"),
                    "📂"
            ));
        }

        // ── Insight Card 6: Budget Alert ──────────────────────────────────
        if (!selectedMonth.equals("All Time") && totalExpense > 0 && totalIncome > 0) {
            double ratio = totalExpense / totalIncome;
            String alertMsg;
            int alertColor;
            if (ratio > 1.0)      { alertMsg = "You've overspent your income!";       alertColor = Color.parseColor("#B71C1C"); }
            else if (ratio > 0.8) { alertMsg = "Warning: 80%+ of income spent";       alertColor = Color.parseColor("#E65100"); }
            else if (ratio > 0.5) { alertMsg = "Moderate spending — stay cautious";   alertColor = Color.parseColor("#F57F17"); }
            else                  { alertMsg = "Great! Spending is well under control";alertColor = Color.parseColor("#2E7D32"); }

            insightCards.add(new InsightCard(
                    "Budget Health",
                    String.format("%.0f%%", ratio * 100) + " of income spent",
                    alertMsg,
                    alertColor,
                    "🩺"
            ));
        }

        insightAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
