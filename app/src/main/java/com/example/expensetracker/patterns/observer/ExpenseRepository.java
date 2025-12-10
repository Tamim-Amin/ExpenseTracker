package com.example.expensetracker.patterns.observer;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.patterns.singleton.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

// This acts as the "Subject" in the Observer Pattern
public class ExpenseRepository {
    private static ExpenseRepository instance;
    private final List<ExpenseObserver> observers = new ArrayList<>();
    private DatabaseReference databaseReference;

    private ExpenseRepository() {
        // Don't initialize databaseReference here - user may not be logged in yet
    }

    public static synchronized ExpenseRepository getInstance() {
        if (instance == null) {
            instance = new ExpenseRepository();
        }
        return instance;
    }

    // Initialize the database reference when we know user is logged in
    private DatabaseReference getDatabaseReference() {
        if (databaseReference == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseManager.getInstance().getDatabase().getReference("expenses").child(userId);
            }
        }
        return databaseReference;
    }

    // Reset the instance when user logs out (call this from logout)
    public static synchronized void resetInstance() {
        instance = null;
    }

    // --- Observer Management Methods ---
    public void addObserver(ExpenseObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ExpenseObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(List<MainActivity.Expense> expenses) {
        for (ExpenseObserver observer : observers) {
            observer.onExpensesUpdated(expenses);
        }
    }

    // --- Data Methods ---
    public void loadExpenses() {
        DatabaseReference dbRef = getDatabaseReference();
        if (dbRef == null) {
            // User not logged in, nothing to load
            return;
        }
        dbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<MainActivity.Expense> expenseList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    MainActivity.Expense expense = snapshot.getValue(MainActivity.Expense.class);
                    if (expense != null) {
                        expenseList.add(expense);
                    }
                }
                // Notify all observers (e.g., MainActivity) that data is ready
                notifyObservers(expenseList);
            }
        });
    }
}