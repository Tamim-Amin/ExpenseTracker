package com.example.expensetracker.patterns.memento;

import com.example.expensetracker.MainActivity;

/**
 * The Memento: Holds the state of an Expense object.
 * It stores a copy of the state so that the original object's state can be restored.
 */
public class ExpenseMemento {
    // The state is stored as an Expense object.
    // It's 'final' to ensure it's immutable once the memento is created.
    private final MainActivity.Expense savedState;

    /**
     * The Memento's constructor. It takes the state to save.
     * @param stateToSave The Expense state to save.
     */
    public ExpenseMemento(MainActivity.Expense stateToSave) {
        // Create a *new* Expense object to ensure the state is a true copy
        // and not just a reference to a mutable object in the activity.
        this.savedState = new MainActivity.Expense(
                stateToSave.getDescription(),
                stateToSave.getAmount(),
                stateToSave.getCategory(),
                stateToSave.getDate()
        );
    }

    /**
     * The method for the Caretaker (AddExpenseActivity) to retrieve the saved state.
     * @return The saved Expense state.
     */
    public MainActivity.Expense getSavedState() {
        return savedState;
    }
}
