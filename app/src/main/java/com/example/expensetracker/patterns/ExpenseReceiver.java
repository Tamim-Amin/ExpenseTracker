package com.example.expensetracker.patterns;

import com.example.expensetracker.MainActivity;
import java.util.List;

/**
 * COMMAND PATTERN:
 * The "Receiver" class. This contains the business logic to execute.
 * It knows how to perform the necessary actions (e.g., add or remove from a list).
 */
public class ExpenseReceiver {
    private final List<MainActivity.Expense> expenses;

    public ExpenseReceiver(List<MainActivity.Expense> expenses) {
        this.expenses = expenses;
    }

    public void addExpense(MainActivity.Expense expense) {
        expenses.add(expense);
        // In a real app, you'd also save to a database, update UI, etc.
    }

    public void removeExpense(MainActivity.Expense expense) {
        expenses.remove(expense);
    }
}
