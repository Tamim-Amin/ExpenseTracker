//Adaptee - incompatible external object
//Tamim Amin
package com.example.expensetracker.patterns.adapter;

import java.util.Date;

/**
 * ADAPTER PATTERN:
 * The "Adaptee" class. This represents an incompatible object from an
 * external source that our system needs to integrate with.
 * Note the differences from MainActivity.Expense:
 * - Amount is a String with a currency symbol.
 * - Date is a java.util.Date object.
 * - There is no expense ID.
 */
public class ExternalExpense {
    private final String description;
    private final String cost; // e.g., "$25.50"
    private final String category;
    private final Date transactionDate;

    public ExternalExpense(String description, String cost, String category, Date transactionDate) {
        this.description = description;
        this.cost = cost;
        this.category = category;
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public String getCost() {
        return cost;
    }

    public String getCategory() {
        return category;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }
}
