package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView; // Make sure this is imported
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText etDescription, etAmount, etDate;
    private Spinner spinnerCategory;
    private Button btnSelectDate, btnSave;
    private TextView tvHeader; // Add this

    private Calendar selectedDate;

    private String expenseId;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure this layout file has the ID 'tv_header_title'
        setContentView(R.layout.activity_edit_expense);

        initViews();
        setupCategorySpinner();
        setupClickListeners();

        selectedDate = Calendar.getInstance();

        // --- Load data from Intent ---
        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "Error: No expense data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        expenseId = intent.getStringExtra("expenseId");
        String description = intent.getStringExtra("description");
        double amount = intent.getDoubleExtra("amount", 0);
        String category = intent.getStringExtra("category");
        String date = intent.getStringExtra("date");

        // --- Set the text for the Edit screen ---
        tvHeader.setText("Edit Expense");
        btnSave.setText("💾 Update Expense");
        // --- End of new text ---

        // Set the fields with the data
        etDescription.setText(description);
        etAmount.setText(String.valueOf(amount));
        etDate.setText(date);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedDate.setTime(dateFormat.parse(date));
        } catch (Exception e) {
            // If parsing fails, just keep the current date
        }

        // Set the category spinner
        if (category != null) {
            int spinnerPosition = categoryAdapter.getPosition(category);
            spinnerCategory.setSelection(spinnerPosition);
        }
    }

    private void initViews() {
        etDescription = findViewById(R.id.et_description);
        etAmount = findViewById(R.id.et_amount);
        etDate = findViewById(R.id.et_date);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSave = findViewById(R.id.btn_save);

        // --- Find the new header ID ---
        tvHeader = findViewById(R.id.tv_header_title);
    }

    private void setupClickListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateField();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateField() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void saveExpense() {
        // Validation (same as before)
        etDescription.setError(null);
        etAmount.setError(null);
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String date = etDate.getText().toString().trim();

        if (description.isEmpty()) {
            etDescription.setError("Please enter a description");
            etDescription.requestFocus();
            return;
        }
        if (amountStr.isEmpty()) {
            etAmount.setError("Please enter an amount");
            etAmount.requestFocus();
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                etAmount.setError("Amount must be greater than zero");
                etAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("Please enter a valid amount");
            etAmount.requestFocus();
            return;
        }
        // ... (add any other validation you have) ...

        // Send data back to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("expenseId", expenseId);
        resultIntent.putExtra("description", description);
        resultIntent.putExtra("amount", amount);
        resultIntent.putExtra("category", category);
        resultIntent.putExtra("date", date);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setupCategorySpinner() {
        String[] categories = {"Food", "Transport", "Entertainment", "Shopping", "Bills", "Healthcare", "Education", "Others"};
        categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}