package com.example.expensetracker.patterns.strategy;

import com.example.expensetracker.MainActivity;

import java.util.List;

public class TotalExpenseStrategy implements ExpenseCalculationStrategy {
    @Override
    public double calculate(List<MainActivity.Expense> expenses) {
        double total = 0;
        for (MainActivity.Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }
}
