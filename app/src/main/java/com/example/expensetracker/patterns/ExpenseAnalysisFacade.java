package com.example.expensetracker.patterns;

import android.util.Log;
import com.example.expensetracker.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FACADE PATTERN:
 * Provides a simplified interface to the complex logic of
 * Expense Strategies and Composite hierarchies.
 * The Client (MainActivity) only needs to call one method to get results,
 * without knowing how the strategies or trees are built.
 */
public class ExpenseAnalysisFacade {

    private final ExpenseCalculatorContext strategyContext;

    public ExpenseAnalysisFacade() {
        this.strategyContext = new ExpenseCalculatorContext();
    }

    // --- STRATEGY SUBSYSTEM WRAPPERS ---

    public double calculateTotal(List<MainActivity.Expense> expenses) {
        strategyContext.setStrategy(new TotalExpenseStrategy());
        return strategyContext.executeStrategy(expenses);
    }

    public double calculateCategoryTotal(List<MainActivity.Expense> expenses, String category) {
        strategyContext.setStrategy(new CategoryExpenseStrategy(category));
        return strategyContext.executeStrategy(expenses);
    }

    public double calculateDailyTotal(List<MainActivity.Expense> expenses, String date) {
        strategyContext.setStrategy(new DailyExpenseStrategy(date));
        return strategyContext.executeStrategy(expenses);
    }

    // --- COMPOSITE SUBSYSTEM WRAPPER ---

    /**
     * Hides the complexity of building the Composite Tree.
     * It constructs the tree and returns the calculated grand total.
     */
    public void generateCompositeAnalysisLog(List<MainActivity.Expense> expenses) {
        // --- ADAPTER PATTERN DEMO ---
        // 1. Create the incompatible object from the external source
        ExternalExpense externalExpense = new ExternalExpense(
                "External Library Subscription",
                "$15.99",
                "Software",
                new Date()
        );

        // 2. Create the adapter and wrap the external object
        MainActivity.Expense adaptedExpense = new ExternalExpenseAdapter(externalExpense);

        // 3. Add the adapted object to the list. The client code can now
        //    treat it just like any other standard Expense object.
        List<MainActivity.Expense> allExpenses = new ArrayList<>(expenses);
        allExpenses.add(adaptedExpense);
        // --- END ADAPTER DEMO ---

        if (allExpenses.isEmpty()) return;

        Log.d("FacadePattern", "--- Generating Composite Analysis ---");

        ExpenseComponent allExpensesGroup = new ExpenseGroup("Total Expenses");
        Map<String, ExpenseGroup> categoryGroups = new HashMap<>();

        // Loop through the combined list of expenses
        for (MainActivity.Expense expense : allExpenses) {
            String category = expense.getCategory();

            ExpenseGroup categoryGroup = categoryGroups.get(category);
            if (categoryGroup == null) {
                categoryGroup = new ExpenseGroup(category);
                categoryGroups.put(category, categoryGroup);
                allExpensesGroup.add(categoryGroup);
            }

            ExpenseComponent singleExpenseLeaf = new SingleExpense(expense);
            categoryGroup.add(singleExpenseLeaf);
        }

        double total = allExpensesGroup.getAmount();
        Log.d("FacadePattern", "Composite Calculated Total: $" + total);
        Log.d("AdapterPattern", "Successfully processed adapted expense: " + adaptedExpense.getDescription());
    }
}
