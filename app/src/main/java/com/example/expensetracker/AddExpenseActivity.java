// Path: app/src/main/java/com/example/expensetracker/AddExpenseActivity.java
package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.patterns.memento.ExpenseMemento; // Import the Memento class

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    // UI Components
    private EditText etDescription, etAmount;
    private Spinner spinnerCategory;
    private TextView tvSelectedDate, tvTitle;
    private Button btnSave, btnCancel, btnUndo;

    // State variables
    private boolean isEditMode = false;
    private String expenseId = null;
    private final Calendar calendar = Calendar.getInstance();

    // --- Memento Pattern: The Caretaker ---
    // This variable holds the saved state (the memento) for the undo feature.
    private ExpenseMemento savedStateMemento;

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
        btnUndo = findViewById(R.id.btn_undo);
        tvTitle = findViewById(R.id.tv_title);

        setupCategorySpinner();

        // Check if we are in "Edit Mode"
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        if (isEditMode) {
            // --- EDIT MODE ---
            tvTitle.setText("Edit Expense");
            btnUndo.setVisibility(View.VISIBLE); // Show the "Undo Changes" button

            expenseId = intent.getStringExtra("expenseId");

            // Create an object with the initial state passed from MainActivity
            MainActivity.Expense initialState = new MainActivity.Expense(
                    intent.getStringExtra("description"),
                    intent.getDoubleExtra("amount", 0),
                    intent.getStringExtra("category"),
                    intent.getStringExtra("date")
            );

            // --- Memento: Originator's Action ---
            // SAVE the initial state to a memento for the undo feature.
            saveStateToMemento(initialState);

            // Pre-fill the form with the initial state
            restoreFormFromState(initialState);

        } else {
            // --- ADD MODE ---
            tvTitle.setText("Add New Expense");
            updateDateLabel(); // Set a default date for a new expense
        }

        // --- Setup Listeners ---
        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());
        btnSave.setOnClickListener(v -> saveExpense());
        btnCancel.setOnClickListener(v -> finish()); // Go back without saving

        // --- Memento: Caretaker's Action ---
        // Use the memento to RESTORE the state if the user clicks Undo.
        btnUndo.setOnClickListener(v -> {
            if (savedStateMemento != null) {
                // The Originator's action: RESTORE its state from the memento
                restoreFormFromState(savedStateMemento.getSavedState());
                Toast.makeText(this, "Changes have been undone", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // This method is the "createMemento" part of the pattern.
    private void saveStateToMemento(MainActivity.Expense expense) {
        // The Caretaker (this activity) saves the memento.
        this.savedStateMemento = new ExpenseMemento(expense);
    }

    // This method is the "setMemento" part of the pattern.
    private void restoreFormFromState(MainActivity.Expense expenseState) {
        if (expenseState == null) return;

        // Use public getter methods instead of direct private field access
        etDescription.setText(expenseState.getDescription());
        etAmount.setText(String.valueOf(expenseState.getAmount()));
        tvSelectedDate.setText(expenseState.getDate());

        // Set the spinner to the correct category
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(expenseState.getCategory());
            spinnerCategory.setSelection(position);
        }
    }

    // --- Other methods (setupCategorySpinner, saveExpense, etc.) remain the same ---
    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        Intent resultIntent = new Intent();
        resultIntent.putExtra("description", description);
        resultIntent.putExtra("amount", amount);
        resultIntent.putExtra("category", category);
        resultIntent.putExtra("date", date);

        if (isEditMode) {
            resultIntent.putExtra("expenseId", expenseId);
        }

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
