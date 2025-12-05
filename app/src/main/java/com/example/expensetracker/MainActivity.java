package com.example.expensetracker;

import android.app.AlertDialog;
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
import androidx.appcompat.app.AppCompatActivity;

// imports from your project
import com.example.expensetracker.patterns.CategoryExpenseStrategy;
import com.example.expensetracker.patterns.DailyExpenseStrategy;
import com.example.expensetracker.patterns.ExpenseCalculatorContext;
import com.example.expensetracker.patterns.ExpenseComponent;
import com.example.expensetracker.patterns.ExpenseGroup;
import com.example.expensetracker.patterns.SingleExpense;
import com.example.expensetracker.patterns.TotalExpenseStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Request codes for starting activities
    private static final int REQUEST_CODE_ADD_EXPENSE = 1001;
    private static final int REQUEST_CODE_EDIT_EXPENSE = 1002; // For editing

    private TextView tvWelcome, tvTotal;
    private LinearLayout expenseListContainer;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference expenseDbRef;
    private String userId;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return; // Exit if the activity was canceled or returned no data
        }

        // Common data for both adding and editing
        String description = data.getStringExtra("description");
        double amount = data.getDoubleExtra("amount", 0);
        String category = data.getStringExtra("category");
        String date = data.getStringExtra("date");

        Expense expenseData = new Expense(description, amount, category, date);

        if (requestCode == REQUEST_CODE_ADD_EXPENSE) {
            // Logic for adding a NEW expense
            String expenseId = expenseDbRef.push().getKey();
            if (expenseId != null) {
                expenseDbRef.child(expenseId).setValue(expenseData)
                        .addOnSuccessListener(aVoid -> {
                            loadUserExpensesFromFirebase(); // Reload all data for consistency
                            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show());
            }
        } else if (requestCode == REQUEST_CODE_EDIT_EXPENSE) {
            // Logic for UPDATING an existing expense
            String expenseIdToUpdate = data.getStringExtra("expenseId");
            if (expenseIdToUpdate != null) {
                expenseDbRef.child(expenseIdToUpdate).setValue(expenseData)
                        .addOnSuccessListener(aVoid -> {
                            loadUserExpensesFromFirebase(); // Reload all data to show changes
                            Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show());
            }
        }
    }


    private void loadUserExpensesFromFirebase() {
        expenseDbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                expenseList.clear();
                expenseListContainer.removeAllViews();
                double currentTotal = 0.0;

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);
                    String expenseId = snapshot.getKey();
                    if (expense != null && expenseId != null) {
                        addExpenseToUI(expense, expenseId);
                        currentTotal += expense.amount;
                    }
                }
                tvTotal.setText(String.format("$%.2f", currentTotal));
                demonstrateCompositePattern();
            } else {
                Toast.makeText(this, "Failed to load expenses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addExpenseToUI(Expense expense, String expenseId) {
        View view = getLayoutInflater().inflate(R.layout.item_expense, expenseListContainer, false);

        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvCategory = view.findViewById(R.id.tv_category);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvAmount = view.findViewById(R.id.tv_amount);
        Button btnDelete = view.findViewById(R.id.btn_delete);
        Button btnEdit = view.findViewById(R.id.btn_edit); // Find the edit button

        tvDescription.setText(expense.description);
        tvCategory.setText(expense.category);
        tvDate.setText(expense.date);
        tvAmount.setText(String.format("$%.2f", expense.amount));
        setCategoryBackground(tvCategory, expense.category);

        ExpenseItem expenseItem = new ExpenseItem(expense, view, expenseId);
        expenseList.add(expenseItem);

        // --- SET ONCLICK LISTENER FOR THE EDIT BUTTON ---
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            // Pass data to pre-fill the fields in AddExpenseActivity
            intent.putExtra("isEditMode", true);
            intent.putExtra("expenseId", expenseId);
            intent.putExtra("description", expense.description);
            intent.putExtra("amount", expense.amount);
            intent.putExtra("category", expense.category);
            intent.putExtra("date", expense.date);
            startActivityForResult(intent, REQUEST_CODE_EDIT_EXPENSE);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteExpense(expenseId))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        expenseListContainer.addView(view);
    }

    private void deleteExpense(String expenseId) {
        if (expenseId == null) return;
        // Just remove from Firebase. The UI will refresh after.
        expenseDbRef.child(expenseId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    loadUserExpensesFromFirebase(); // Reload to update UI and totals
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete expense", Toast.LENGTH_SHORT).show());
    }

    // --- Utility and other methods from your original file (no changes needed below) ---

    public static String getUserDisplayName(FirebaseUser user) {
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            return user.getDisplayName();
        } else if (user.getEmail() != null) {
            return user.getEmail().split("@")[0];
        } else {
            return "User";
        }
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
                                expenseDbRef.removeValue().addOnSuccessListener(aVoid -> {
                                    loadUserExpensesFromFirebase(); // Reload to show empty state
                                    Toast.makeText(this, "All expenses deleted", Toast.LENGTH_SHORT).show();
                                });
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

    public static class Expense {
        public String description;
        public double amount;
        public String category;
        public String date;
        public Expense() {}
        public Expense(String description, double amount, String category, String date) {
            this.description = description;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }
    }

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

    private void demonstrateCompositePattern() {
        Log.d("CompositeDemo", "--- Demonstrating Composite Pattern ---");
        if (expenseList.isEmpty()) {
            Log.d("CompositeDemo", "No expenses to group.");
            return;
        }
        ExpenseComponent allExpensesGroup = new ExpenseGroup("Total Expenses");
        Map<String, ExpenseGroup> categoryGroups = new HashMap<>();
        for (ExpenseItem item : expenseList) {
            String category = item.expense.category;
            ExpenseGroup categoryGroup = categoryGroups.get(category);
            if (categoryGroup == null) {
                categoryGroup = new ExpenseGroup(category);
                categoryGroups.put(category, categoryGroup);
                allExpensesGroup.add(categoryGroup);
            }
            ExpenseComponent singleExpenseLeaf = new SingleExpense(item.expense);
            categoryGroup.add(singleExpenseLeaf);
        }
        Log.d("CompositeDemo", "--- Calculating Totals ---");
        ExpenseGroup foodGroup = categoryGroups.get("Food");
        if (foodGroup != null) {
            Log.d("CompositeDemo", "Total for " + foodGroup.getTitle() + ": $" + foodGroup.getAmount());
        }
        Log.d("CompositeDemo", "GRAND TOTAL (" + allExpensesGroup.getTitle() + "): $" + allExpensesGroup.getAmount());
        Log.d("CompositeDemo", "------------------------------------------");
    }

    // Example usage of Strategy Pattern
    private void applyStrategyExample() {
        ExpenseCalculatorContext context = new ExpenseCalculatorContext();

        // 1. Total of ALL expenses
        context.setStrategy(new TotalExpenseStrategy());
        double totalAll = context.executeStrategy(getExpenseList());
        Log.d("StrategyPattern", "Total All Expenses: $" + totalAll);

        // 2. Total of only FOOD category
        context.setStrategy(new CategoryExpenseStrategy("Food"));
        double totalFood = context.executeStrategy(getExpenseList());
        Log.d("StrategyPattern", "Total Food Expenses: $" + totalFood);

        // 3. Total of today
        context.setStrategy(new DailyExpenseStrategy("2025-11-06")); // Example
        double totalToday = context.executeStrategy(getExpenseList());
        Log.d("StrategyPattern", "Total Today Expenses: $" + totalToday);
    }

    // Convert ExpenseItem list to Expense list
    private List<Expense> getExpenseList() {
        List<Expense> expenses = new ArrayList<>();
        for (ExpenseItem item : expenseList) {
            expenses.add(item.expense);
        }
        return expenses;
    }

}
