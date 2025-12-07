package com.example.expensetracker.patterns;

import com.example.expensetracker.MainActivity.Expense;

//The Factory Interface: Declares the factory method for creating ExpenseComponents.
public interface ExpenseComponentFactory {
    ExpenseComponent createExpenseComponent(Expense expense);
}
