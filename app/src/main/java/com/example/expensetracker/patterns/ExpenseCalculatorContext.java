package com.example.expensetracker.patterns;

import com.example.expensetracker.MainActivity;

import java.util.List;

public class ExpenseCalculatorContext {
    private ExpenseCalculationStrategy strategy;

    public void setStrategy(ExpenseCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public double executeStrategy(List<MainActivity.Expense> expenses) {
        return strategy.calculate(expenses);
    }
}
