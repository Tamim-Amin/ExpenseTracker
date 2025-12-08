package com.example.expensetracker.patterns;

import com.example.expensetracker.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * ADAPTER PATTERN:
 * The "Adapter" class. This class wraps the "Adaptee" (ExternalExpense)
 * and exposes a target interface that the client code expects.
 */
public class ExternalExpenseAdapter extends MainActivity.Expense {
    private final ExternalExpense externalExpense;

    public ExternalExpenseAdapter(ExternalExpense externalExpense) {
        // The adapter holds a reference to the adaptee
        this.externalExpense = externalExpense;
    }

    @Override
    public String getDescription() {
        return externalExpense.getDescription();
    }

    @Override
    public double getAmount() {
        // Transformation logic: Convert string "$25.50" to double 25.50
        String cost = externalExpense.getCost().replace("$", "").trim();
        try {
            return Double.parseDouble(cost);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public String getCategory() {
        return externalExpense.getCategory();
    }

    @Override
    public String getDate() {
        // Transformation logic: Convert Date object to String
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(externalExpense.getTransactionDate());
    }

    // You might need to override other methods from Expense if they are not
    // handled by a default constructor or if they need to be adapted.
    // For example, if your client code relies on Id, you might generate
    // one or return a default value.
//    @Override
    public String getId() {
        return "external-" + externalExpense.getDescription() + externalExpense.getCost();
    }
}
