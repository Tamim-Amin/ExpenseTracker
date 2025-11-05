package com.example.expensetracker.patterns;

import com.example.expensetracker.MainActivity;

import java.util.List;

public class CategoryExpenseStrategy implements ExpenseCalculationStrategy {
    private String category;

    public CategoryExpenseStrategy(String category) {
        this.category = category;
    }

    @Override
    public double calculate(List<MainActivity.Expense> expenses) {
        double total = 0;
        for (MainActivity.Expense expense : expenses) {
            if (expense.category.equalsIgnoreCase(category)) {
                total += expense.amount;
            }
        }
        return total;
    }
}
