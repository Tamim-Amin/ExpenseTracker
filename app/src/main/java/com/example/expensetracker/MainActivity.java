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
// __FACTORY PATTERN START__: Import the factory classes.
import com.example.expensetracker.patterns.ConcreteExpenseFactory;
import com.example.expensetracker.patterns.ExpenseComponentFactory;
// __FACTORY PATTERN END__
import com.example.expensetracker.patterns.DailyExpenseStrategy;
import com.example.expensetracker.patterns.ExpenseAnalysisFacade;
import com.example.expensetracker.patterns.ExpenseCalculatorContext;
import com.example.expensetracker.patterns.ExpenseComponent;
import com.example.expensetracker.patterns.ExpenseGroup;
import com.example.expensetracker.patterns.ExpenseObserver;
import com.example.expensetracker.patterns.ExpenseRepository;
import com.example.expensetracker.patterns.SingleExpense;
import com.example.expensetracker.patterns.TotalExpenseStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.expensetracker.patterns.iterator.ExpenseIterator;
import com.example.expensetracker.patterns.iterator.ExpenseListIterator;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ExpenseObserver {

    // Request codes for starting activities
    private static final int REQUEST_CODE_ADD_EXPENSE = 1001;
    private static final int REQUEST_CODE_EDIT_EXPENSE = 1002; // For editing

    private TextView tvWelcome, tvTotal;
    private LinearLayout expenseListContainer;
    private ExpenseAnalysisFacade analysisFacade;

    private FirebaseAuth mAuth;
    private DatabaseReference expenseDbRef;

    private final List<ExpenseItem> expenseList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Register this activity as an observer
        ExpenseRepository.getInstance().addObserver(this);
        // Trigger the load
        ExpenseRepository.getInstance().loadExpenses();
        // Initialize the Facade
        analysisFacade = new ExpenseAnalysisFacade();


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        String userId = currentUser.getUid();
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

                // We can use the Facade to calculate the total for the UI
                List<Expense> tempExpenseListForStrategy = new ArrayList<>();

                for (DataSnapshot snapshot : Objects.requireNonNull(task.getResult()).getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);
                    String expenseId = snapshot.getKey();
                    if (expense != null && expenseId != null) {
                        addExpenseToUI(expense, expenseId);
                        tempExpenseListForStrategy.add(expense);
                    }
                }

                // --- FACADE IMPLEMENTATION ---

                // 1. Use Facade to get the Total for the UI (Replaces manual loop sum)
                double currentTotal = analysisFacade.calculateTotal(tempExpenseListForStrategy);
                tvTotal.setText(String.format("$%.2f", currentTotal));

                List<Expense> expensesOnly = getExpenseList();

                // 2. Use Facade to run complex Composite logic
                analysisFacade.generateCompositeAnalysisLog(expensesOnly);

                // 3. Use Facade to run specific Strategy calculations (Logging examples)
                double foodTotal = analysisFacade.calculateCategoryTotal(expensesOnly, "Food");
                Log.d("FacadeDemo", "Food Total via Facade: " + foodTotal);

                // --- ITERATOR PATTERN DEMONSTRATION ---
                demonstrateIteratorPattern();
                // -----------------------------

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
        Button btnEdit = view.findViewById(R.id.btn_edit);

        // Use getters for safety and good practice
        tvDescription.setText(expense.getDescription());
        tvCategory.setText(expense.getCategory());
        tvDate.setText(expense.getDate());
        tvAmount.setText(String.format("$%.2f", expense.getAmount()));
        setCategoryBackground(tvCategory, expense.getCategory());

        ExpenseItem expenseItem = new ExpenseItem(expense, view, expenseId);
        expenseList.add(expenseItem);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            intent.putExtra("isEditMode", true);
            intent.putExtra("expenseId", expenseId);
            intent.putExtra("description", expense.getDescription());
            intent.putExtra("amount", expense.getAmount());
            intent.putExtra("category", expense.getCategory());
            intent.putExtra("date", expense.getDate());
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
        expenseDbRef.child(expenseId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    loadUserExpensesFromFirebase(); // Reload to update UI and totals
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete expense", Toast.LENGTH_SHORT).show());
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

    private void setCategoryBackground(TextView tvCategory, String category) {
        if (category == null) category = "other";
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
            String title = item.getTitle().toString();
            if ("About".equals(title)) {
                new AlertDialog.Builder(this)
                        .setTitle("About")
                        .setMessage("Simple Expense Tracker\nVersion 1.0\nBuilt with ❤")
                        .setPositiveButton("OK", null).show();
                return true;
            } else if ("Delete All Expenses".equals(title)) {
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
            } else if ("Logout".equals(title)) {
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
        private String description;
        private String id;
        private String category; // This is currently private
        private double amount;
        private long timestamp;
        private String date;

        // REQUIRED: empty constructor for Firebase DataSnapshot.getValue(Expense.class)
        public Expense() {}

        public Expense(String description, double amount, String category, String date) {
            this.description = description;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }

        // --- GETTER METHODS ---
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public String getCategory() { return category; }
        public String getDate() { return date; }
    }

    /**
     * Helper class to hold a reference to an Expense and its corresponding View and ID.
     */
    public static class ExpenseItem {
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
        if (category == null) category = "other";
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
        Log.d("CompositeDemo", "--- Demonstrating Composite Pattern with Factory ---");
        if (expenseList.isEmpty()) {
            Log.d("CompositeDemo", "No expenses to group.");
            return;
        }

        // __FACTORY PATTERN START__: Create an instance of the Concrete Factory.
        ExpenseComponentFactory expenseFactory = new ConcreteExpenseFactory();

        ExpenseComponent allExpensesGroup = new ExpenseGroup("Total Expenses");
        Map<String, ExpenseGroup> categoryGroups = new HashMap<>();

        for (ExpenseItem item : expenseList) {
            String category = item.expense.getCategory();
            ExpenseGroup categoryGroup = categoryGroups.get(category);

            if (categoryGroup == null) {
                // We still create Groups directly as they are containers
                categoryGroup = new ExpenseGroup(category);
                categoryGroups.put(category, categoryGroup);
                allExpensesGroup.add(categoryGroup);
            }

            ExpenseComponent singleExpenseLeaf = expenseFactory.createExpenseComponent(item.expense);

            categoryGroup.add(singleExpenseLeaf);
        }

        Log.d("CompositeDemo", "--- Calculating Totals ---");
        Log.d("CompositeDemo", "GRAND TOTAL (" + allExpensesGroup.getTitle() + "): $" + String.format("%.2f", allExpensesGroup.getAmount()));
        Log.d("CompositeDemo", "------------------------------------------");
    }

    private void applyStrategyExample() {
        Log.d("StrategyPattern", "--- Demonstrating Strategy Pattern ---");
        if(expenseList.isEmpty()) {
            Log.d("StrategyPattern", "No expenses to calculate.");
            return;
        }

        ExpenseCalculatorContext context = new ExpenseCalculatorContext();
        List<Expense> expensesOnly = getExpenseList();

        context.setStrategy(new TotalExpenseStrategy());
        double totalAll = context.executeStrategy(expensesOnly);
        Log.d("StrategyPattern", "Total (All Expenses): $" + totalAll);

        context.setStrategy(new CategoryExpenseStrategy("Food"));
        double totalFood = context.executeStrategy(expensesOnly);
        Log.d("StrategyPattern", "Total (Food): $" + totalFood);

        context.setStrategy(new DailyExpenseStrategy("12/08/2025")); // Example date
        double totalToday = context.executeStrategy(expensesOnly);
        Log.d("StrategyPattern", "Total for 12/08/2025: $" + totalToday);
        Log.d("StrategyPattern", "------------------------------------");
    }

    private List<Expense> getExpenseList() {
        List<Expense> expenses = new ArrayList<>();
        for (ExpenseItem item : expenseList) {
            expenses.add(item.expense);
        }
        return expenses;
    }
    @Override
    public void onExpensesUpdated(List<Expense> expenses) {
        // Clear old views
        expenseList.clear();
        expenseListContainer.removeAllViews();
        double currentTotal = 0.0;

        // Rebuild the UI with the new list
        for (Expense expense : expenses) {
            // You might need to refactor addExpenseToUI slightly to handle IDs if they aren't in the Expense object
            // Or fetch IDs alongside data in the Repository
            addExpenseToUI(expense, "temp_id"); // Ideally, add ID to your Expense model
            currentTotal += expense.getAmount();
        }

        tvTotal.setText(String.format("$%.2f", currentTotal));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 4. Clean up
        ExpenseRepository.getInstance().removeObserver(this);
    }
    public ExpenseIterator createIterator() {
        // Pass the list to be iterated to the new constructor
        return new ExpenseListIterator(expenseList);
    }
    private void demonstrateIteratorPattern() {
        Log.d("IteratorPattern", "--- Demonstrating External Iterator Pattern ---");
        // Get the iterator using the updated factory method
        ExpenseIterator iterator = createIterator();

        // Use the iterator to traverse the list
        while (iterator.hasNext()) {
            ExpenseItem item = iterator.next();
            // Perform an action on each item
            Log.d("IteratorPattern", "Iterating over: " + item.expense.getDescription());
        }
        Log.d("IteratorPattern", "-----------------------------------------");
    }
}