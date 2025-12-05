package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    // Will be true if we are editing an existing expense
    private boolean isEditMode = false;
    // Will hold the ID of the expense being edited
    private String expenseId = null;

    private EditText etDescription, etAmount;
    private Spinner spinnerCategory;
    private TextView tvSelectedDate;
    private Button btnSave, btnCancel;

    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize UI components
        etDescription = findViewById(R.id.et_description);
        etAmount = findViewById(R.id.et_amount);
        spinnerCategory = findViewById(R.id.spinner_category);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        TextView tvTitle = findViewById(R.id.tv_title); // Title of the screen

        setupCategorySpinner();

        // --- Check for Edit Mode ---
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        if (isEditMode) {
            // If we are in edit mode, change the title and pre-fill the form
            tvTitle.setText("Edit Expense"); // Change the title
            expenseId = intent.getStringExtra("expenseId");

            // Retrieve and set the existing data
            String description = intent.getStringExtra("description");
            double amount = intent.getDoubleExtra("amount", 0);
            String category = intent.getStringExtra("category");
            String date = intent.getStringExtra("date");

            etDescription.setText(description);
            etAmount.setText(String.valueOf(amount));
            tvSelectedDate.setText(date);

            // Set the spinner to the correct category
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
            int position = adapter.getPosition(category);
            spinnerCategory.setSelection(position);

        } else {
            // If we are in add mode, set a default date
            tvTitle.setText("Add New Expense");
            updateDateLabel();
        }

        // --- Setup Listeners ---
        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());
        btnSave.setOnClickListener(v -> saveExpense());
        btnCancel.setOnClickListener(v -> finish()); // Go back without saving
    }

    private void setupCategorySpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerCategory.setAdapter(adapter);
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateLabel() {
        String format = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        tvSelectedDate.setText(sdf.format(calendar.getTime()));
    }

    private void saveExpense() {
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String date = tvSelectedDate.getText().toString();

        // Validation
        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Amount is required");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount");
            return;
        }

        // Prepare the result intent to send back to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("description", description);
        resultIntent.putExtra("amount", amount);
        resultIntent.putExtra("category", category);
        resultIntent.putExtra("date", date);

        // If we were editing, we must also pass back the original expense ID
        if (isEditMode) {
            resultIntent.putExtra("expenseId", expenseId);
        }

        setResult(RESULT_OK, resultIntent);
        finish(); // Close this activity and return to MainActivity
    }
}
