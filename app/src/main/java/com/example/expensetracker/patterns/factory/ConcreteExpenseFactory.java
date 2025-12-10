package com.example.expensetracker.patterns.factory;

import com.example.expensetracker.MainActivity.Expense;
import com.example.expensetracker.patterns.composite.ExpenseComponent;
import com.example.expensetracker.patterns.composite.SingleExpense;

//Implements the factory method to create a SingleExpense (leaf) object.
public class ConcreteExpenseFactory implements ExpenseComponentFactory {
    @Override
    public ExpenseComponent createExpenseComponent(Expense expense) {
        // This factory creates the simple, single expense components.
        return new SingleExpense(expense);
    }
}
