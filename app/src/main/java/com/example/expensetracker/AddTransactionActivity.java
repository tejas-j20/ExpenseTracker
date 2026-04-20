package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    private EditText etAmount, etDescription;
    private RadioGroup rgType;
    private RadioButton rbIncome, rbExpense;
    private Spinner spinnerCategory;
    private TextView tvDate;
    private Button btnSave;
    private MaterialToolbar toolbar;

    private ExpenseDbHelper dbHelper;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Initialize views
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        rgType = findViewById(R.id.rgType);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvDate = findViewById(R.id.tvDate);
        btnSave = findViewById(R.id.btnSave);
        toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Transaction");
        toolbar.setNavigationOnClickListener(v -> finish());

        // Setup database
        dbHelper = new ExpenseDbHelper(this);

        // Setup category spinner
        String[] categories = {"Food", "Transport", "Shopping", "Entertainment", "Utilities", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set default date to today
        updateDateDisplay();

        // Date picker
        tvDate.setOnClickListener(v -> showDatePicker());

        // Save button
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateDisplay();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void saveTransaction() {
        // Get values
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        // Validate amount
        if (amountStr.isEmpty()) {
            etAmount.setError("Enter an amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Enter a valid number");
            return;
        }

        if (amount <= 0) {
            etAmount.setError("Amount must be greater than 0");
            return;
        }

        // Get transaction type
        String type = rbIncome.isChecked() ? "Income" : "Expense";

        // Get date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String date = sdf.format(selectedDate.getTime());

        // Create transaction
        Transaction transaction = new Transaction(amount, category, type, date, description);

        // Save to database
        long result = dbHelper.addTransaction(transaction);

        if (result > 0) {
            Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error saving transaction", Toast.LENGTH_SHORT).show();
        }
    }
}
