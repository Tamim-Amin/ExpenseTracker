package com.example.expensetracker.patterns.strategy;

import com.example.expensetracker.MainActivity;

import java.util.List;

public class DailyExpenseStrategy implements ExpenseCalculationStrategy {
    private String date; // Format: YYYY-MM-DD

    public DailyExpenseStrategy(String date) {
        this.date = date;
    }

    @Override
    public double calculate(List<MainActivity.Expense> expenses) {
        double total = 0;
        for (MainActivity.Expense expense : expenses) {
            if (expense.getDate().equals(date)) {
                total += expense.getAmount();
            }
        }
        return total;
    }
}
