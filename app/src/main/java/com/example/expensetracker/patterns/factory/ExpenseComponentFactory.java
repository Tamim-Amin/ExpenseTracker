package com.example.expensetracker.patterns.factory;

import com.example.expensetracker.MainActivity.Expense;
import com.example.expensetracker.patterns.composite.ExpenseComponent;

//The Factory Interface: Declares the factory method for creating ExpenseComponents.
public interface ExpenseComponentFactory {
    ExpenseComponent createExpenseComponent(Expense expense);
}
