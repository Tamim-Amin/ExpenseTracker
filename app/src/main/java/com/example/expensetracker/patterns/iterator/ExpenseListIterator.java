package com.example.expensetracker.patterns.iterator;

import com.example.expensetracker.MainActivity.ExpenseItem;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * A concrete implementation of ExpenseIterator for a List of ExpenseItems.
 * This class encapsulates the logic for traversing the list.
 */
public class ExpenseListIterator implements ExpenseIterator {

    private final List<ExpenseItem> items;
    private int position = 0;

    /**
     * Constructor that takes the collection to be iterated over.
     * @param items The list of ExpenseItem objects.
     */
    public ExpenseListIterator(List<ExpenseItem> items) {
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return position < items.size();
    }

    @Override
    public ExpenseItem next() {
        if (!hasNext()) {
            // Throwing an exception is standard practice for iterators.
            throw new NoSuchElementException("No more elements in the expense list.");
        }
        return items.get(position++);
    }
}
