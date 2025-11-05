package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.patterns.ExpenseComponent;
import com.example.expensetracker.patterns.ExpenseGroup;
import com.example.expensetracker.patterns.SingleExpense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_EXPENSE = 1001;

    private TextView tvWelcome, tvTotal;
    private LinearLayout expenseListContainer;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference expenseDbRef;
    private String userId;

    private double totalExpenses = 0.0;
    private final List<ExpenseItem> expenseList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        userId = currentUser.getUid();
        expenseDbRef = FirebaseDatabase.getInstance().getReference("expenses").child(userId);

        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tv_welcome);
        tvTotal = findViewById(R.id.tv_total);
        expenseListContainer = findViewById(R.id.expense_list_container);

        String userName = getUserDisplayName(currentUser);
        tvWelcome.setText("Welcome, " + userName);

        findViewById(R.id.btn_add_expense).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_EXPENSE);
        });

        TextView tvSettings = findViewById(R.id.tv_settings);
        tvSettings.setOnClickListener(this::showSettingsMenu);

        loadUserExpensesFromFirebase();
    }

    public static String getUserDisplayName(FirebaseUser user) {
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            return user.getDisplayName();
        } else if (user.getEmail() != null) {
            return user.getEmail().split("@")[0];
        } else {
            return "User";
        }
    }

    private void loadUserExpensesFromFirebase() {
        expenseDbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                expenseList.clear();
                expenseListContainer.removeAllViews();
                totalExpenses = 0.0;

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);
                    String expenseId = snapshot.getKey();
                    if (expense != null && expenseId != null) {
                        addExpenseToUI(expense, expenseId);
                        totalExpenses += expense.amount;
                    }
                }

                updateTotalExpenses();

                // Call the Composite Pattern demo method
                demonstrateCompositePattern();

            } else {
                Toast.makeText(this, "Failed to load expenses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_EXPENSE && resultCode == RESULT_OK && data != null) {
            String description = data.getStringExtra("description");
            double amount = data.getDoubleExtra("amount", 0);
            String category = data.getStringExtra("category");
            String date = data.getStringExtra("date");

            Expense expense = new Expense(description, amount, category, date);

            String expenseId = expenseDbRef.push().getKey();
            if (expenseId != null) {
                expenseDbRef.child(expenseId).setValue(expense)
                        .addOnSuccessListener(aVoid -> {
                            addExpenseToUI(expense, expenseId);
                            totalExpenses += amount;
                            updateTotalExpenses();
                            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void addExpenseToUI(Expense expense, String expenseId) {
        View view = getLayoutInflater().inflate(R.layout.item_expense, expenseListContainer, false);

        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvCategory = view.findViewById(R.id.tv_category);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvAmount = view.findViewById(R.id.tv_amount);
        Button btnDelete = view.findViewById(R.id.btn_delete);

        tvDescription.setText(expense.description);
        tvCategory.setText(expense.category);
        tvDate.setText(expense.date);
        tvAmount.setText(String.format("$%.2f", expense.amount));
        setCategoryBackground(tvCategory, expense.category);

        ExpenseItem expenseItem = new ExpenseItem(expense, view, expenseId);
        expenseList.add(expenseItem);

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteExpense(expenseItem);
                        expenseDbRef.child(expenseId).removeValue();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        expenseListContainer.addView(view);
    }

    private void deleteExpense(ExpenseItem item) {
        expenseList.remove(item);
        expenseListContainer.removeView(item.view);
        totalExpenses -= item.expense.amount;
        updateTotalExpenses();
        Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateTotalExpenses() {
        tvTotal.setText(String.format("$%.2f", totalExpenses));
    }

    private void setCategoryBackground(TextView tvCategory, String category) {
        int color;
        switch (category.toLowerCase()) {
            case "food": color = 0xFF10B981; break;
            case "transport": color = 0xFF3B82F6; break;
            case "entertainment": color = 0xFFEF4444; break;
            case "shopping": color = 0xFFF59E0B; break;
            case "bills": color = 0xFF8B5CF6; break;
            case "healthcare": color = 0xFFEC4899; break;
            case "education": color = 0xFF06B6D4; break;
            default: color = 0xFF6B7280; break;
        }
        tvCategory.setBackgroundColor(color);
    }

    private void showSettingsMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("About");
        popup.getMenu().add("Delete All Expenses");
        popup.getMenu().add("Logout");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "About":
                    new AlertDialog.Builder(this)
                            .setTitle("About")
                            .setMessage("Simple Expense Tracker\nVersion 1.0\nBuilt with ❤")
                            .setPositiveButton("OK", null).show();
                    return true;

                case "Delete All Expenses":
                    if (expenseList.isEmpty()) {
                        Toast.makeText(this, "No expenses to delete", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("Delete All?")
                            .setMessage("Delete all expenses? This can't be undone.")
                            .setPositiveButton("Delete All", (d, w) -> {
                                expenseList.clear();
                                expenseListContainer.removeAllViews();
                                totalExpenses = 0.0;
                                updateTotalExpenses();
                                expenseDbRef.removeValue();
                                Toast.makeText(this, "All expenses deleted", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null).show();
                    return true;

                case "Logout":
                    new AlertDialog.Builder(this)
                            .setTitle("Logout")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Logout", (dialog, which) -> {
                                FirebaseAuth.getInstance().signOut();
                                navigateToLogin();
                            })
                            .setNegativeButton("Cancel", null).show();
                    return true;
            }
            return false;
        });

        popup.show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Expense model
    public static class Expense {
        public String description;
        public double amount;
        public String category;
        public String date;

        public Expense() {} // required for Firebase

        public Expense(String description, double amount, String category, String date) {
            this.description = description;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }
    }

    // Wrapper class for tracking views
    private static class ExpenseItem {
        public Expense expense;
        public View view;
        public String id;

        public ExpenseItem(Expense expense, View view, String id) {
            this.expense = expense;
            this.view = view;
            this.id = id;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            navigateToLogin();
        }
    }
    public static int getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "food": return 0xFF10B981;
            case "transport": return 0xFF3B82F6;
            case "entertainment": return 0xFFEF4444;
            case "shopping": return 0xFFF59E0B;
            case "bills": return 0xFF8B5CF6;
            case "healthcare": return 0xFFEC4899;
            case "education": return 0xFF06B6D4;
            default: return 0xFF6B7280;
        }
    }

    //COMPOSITE PATTERN

    private void demonstrateCompositePattern() {
        Log.d("CompositeDemo", "--- Demonstrating Composite Pattern ---");

        if (expenseList.isEmpty()) {
            Log.d("CompositeDemo", "No expenses to group.");
            return;
        }

        // Create the root Composite node
        ExpenseComponent allExpensesGroup = new ExpenseGroup("Total Expenses");

        // Use a Map to hold category Composites
        Map<String, ExpenseGroup> categoryGroups = new HashMap<>();

        // Iterate over the flat list and build the tree
        for (ExpenseItem item : expenseList) {
            String category = item.expense.category;

            // Get or create the composite group for this category
            ExpenseGroup categoryGroup = categoryGroups.get(category);
            if (categoryGroup == null) {
                categoryGroup = new ExpenseGroup(category);
                categoryGroups.put(category, categoryGroup);

                // Add the new category group to the root
                allExpensesGroup.add(categoryGroup);
            }

            // Create the Leaf node for the single expense
            ExpenseComponent singleExpenseLeaf = new SingleExpense(item.expense);

            //  Add the Leaf to its category Composite
            categoryGroup.add(singleExpenseLeaf);
        }

        // Now, we can treat groups and leaves uniformly.
        // We can call getAmount() on ANY component.

        Log.d("CompositeDemo", "--- Calculating Totals ---");

        // Calculate total for a specific category (a Composite)
        ExpenseGroup foodGroup = categoryGroups.get("Food");
        if (foodGroup != null) {
            // Note: We call foodGroup.getAmount()
            Log.d("CompositeDemo", "Total for " + foodGroup.getTitle() + ": $" + foodGroup.getAmount());
        }

        // Calculate total for ALL expenses (the root Composite)
        // Note: We call allExpensesGroup.getAmount()
        // This is the power of the pattern: the *same method* works
        // on a single group or the entire tree.
        Log.d("CompositeDemo", "GRAND TOTAL (" + allExpensesGroup.getTitle() + "): $" + allExpensesGroup.getAmount());
        Log.d("CompositeDemo", "------------------------------------------");

        // We can also build a function to recursively print the tree
        Log.d("CompositeTree", "--- Expense Tree Structure ---");
        printExpenseTree(allExpensesGroup, 0);
    }
    private void printExpenseTree(ExpenseComponent component, int indentLevel) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indent.append("  ");
        }

        if (component.isComposite()) {
            // It's a Composite (ExpenseGroup)
            Log.d("CompositeTree", indent.toString() + "GROUP: " + component.getTitle() + " (Subtotal: $" + component.getAmount() + ")");
            for (ExpenseComponent child : component.getChildren()) {
                printExpenseTree(child, indentLevel + 1);
            }
        } else {
            // It's a Leaf (SingleExpense)
            Log.d("CompositeTree", indent.toString() + "- LEAF: " + component.getTitle() + " ($" + component.getAmount() + ")");
        }
    }



}