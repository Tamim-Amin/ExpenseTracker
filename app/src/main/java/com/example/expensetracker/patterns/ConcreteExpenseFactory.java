package com.example.expensetracker.patterns;

import com.example.expensetracker.MainActivity.Expense;

//Implements the factory method to create a SingleExpense (leaf) object.
public class ConcreteExpenseFactory implements ExpenseComponentFactory {
    @Override
    public ExpenseComponent createExpenseComponent(Expense expense) {
        // This factory creates the simple, single expense components.
        return new SingleExpense(expense);
    }
}
