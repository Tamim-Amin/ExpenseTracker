package com.example.expensetracker.patterns.observer;

import com.example.expensetracker.MainActivity;
import java.util.List;

public interface ExpenseObserver {
    void onExpensesUpdated(List<MainActivity.Expense> expenses);
}