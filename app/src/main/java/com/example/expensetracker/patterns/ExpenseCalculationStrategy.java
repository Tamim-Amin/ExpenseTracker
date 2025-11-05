package com.example.expensetracker.patterns;

import com.example.expensetracker.MainActivity;
import java.util.List;

public interface ExpenseCalculationStrategy {
    double calculate(List<MainActivity.Expense> expenses);
}


