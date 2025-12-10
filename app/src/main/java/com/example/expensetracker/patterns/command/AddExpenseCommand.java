//concrete command
package com.example.expensetracker.patterns.command;

import com.example.expensetracker.MainActivity;

/**
 * COMMAND PATTERN:
 * A concrete command for adding an expense.
 */
public class AddExpenseCommand implements Command {
    private final ExpenseReceiver receiver;
    private final MainActivity.Expense expense;

    public AddExpenseCommand(ExpenseReceiver receiver, MainActivity.Expense expense) {
        this.receiver = receiver;
        this.expense = expense;
    }

    @Override
    public void execute() {
        receiver.addExpense(expense);
    }
}
