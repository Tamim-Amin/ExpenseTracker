package com.example.expensetracker.patterns.iterator;

import com.example.expensetracker.MainActivity;

// We need to import the ExpenseItem class, which is a nested class in MainActivity.
// For this to work, ExpenseItem must be declared as public static.
import com.example.expensetracker.MainActivity.ExpenseItem;

/**
 * Public interface for iterating over a collection of ExpenseItems.
 * This decouples the client from the concrete implementation of the collection.
 */
public interface ExpenseIterator {
    /**
     * Checks if there are more elements in the collection to iterate over.
     * @return true if there is a next element, false otherwise.
     */
    boolean hasNext();

    /**
     * Retrieves the next ExpenseItem from the collection.
     * @return The next ExpenseItem, or null if there are no more elements.
     */
    ExpenseItem next();
}
