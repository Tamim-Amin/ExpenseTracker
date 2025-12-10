package com.example.expensetracker.patterns.composite;

import com.example.expensetracker.MainActivity;

public class SingleExpense implements ExpenseComponent {

    // Holds the original data object from MainActivity
    private final MainActivity.Expense expense;

    public SingleExpense(MainActivity.Expense expense) {
        this.expense = expense;
    }

    @Override
    public String getTitle() {
        // Return the description for the leaf
        return this.expense.getDescription();
    }

    @Override
    public double getAmount() {
        // The operation for a Leaf is to just return its own value
        return this.expense.getAmount();
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    // For access to other data if needed
    public MainActivity.Expense getExpense() {
        return expense;
    }

    // Note: add(), remove(), and getChildren() will use the default
    // interface methods, which throw UnsupportedOperationException.
}