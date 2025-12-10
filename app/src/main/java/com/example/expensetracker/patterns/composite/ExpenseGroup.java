package com.example.expensetracker.patterns.composite;

import java.util.ArrayList;
import java.util.List;

public class ExpenseGroup implements ExpenseComponent {

    private final String title;
    private final List<ExpenseComponent> children = new ArrayList<>();

    public ExpenseGroup(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public double getAmount() {
        // The operation for a Composite is to delegate to its children
        // and aggregate the results (in this case, sum them up).
        double total = 0.0;
        for (ExpenseComponent component : children) {
            total += component.getAmount();
        }
        return total;
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public void add(ExpenseComponent component) {
        children.add(component);
    }

    @Override
    public void remove(ExpenseComponent component) {
        children.remove(component);
    }

    @Override
    public List<ExpenseComponent> getChildren() {
        return children;
    }
}