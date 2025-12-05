package com.example.expensetracker.patterns;

import java.util.Collections;
import java.util.List;

public interface ExpenseComponent {
    
    String getTitle();
    double getAmount();
    boolean isComposite();
    default void add(ExpenseComponent component) {
        throw new UnsupportedOperationException("Cannot add to this component");
    }
    default void remove(ExpenseComponent component) {
        throw new UnsupportedOperationException("Cannot remove from this component");
    }
    default List<ExpenseComponent> getChildren() {
        return Collections.emptyList();
    }
}