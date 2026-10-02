package com.cineflow.service;

import com.cineflow.exception.BudgetExceededException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.Department;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service managing department-level financial allocations, expenses, and variance audits.
 * Demonstrates:
 *  - Map Collections: TreeMap (sorted by Department enum) and HashMap
 *  - List Collection: ArrayList for expense records
 *  - Method Overloading (allocateBudget and logExpense variants)
 *  - Custom Exceptions (BudgetExceededException, ValidationException)
 *  - Functional Programming: Stream API (filter, mapToDouble, sum)
 *  - IO Streams: Formatted file export using PrintWriter and BufferedWriter
 */
public class BudgetService implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Inner record/class for logging individual expenditure transactions.
     */
    public static class ExpenseRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String transactionId;
        private final Department department;
        private final double amount;
        private final String description;
        private final String approvedBy;
        private final LocalDateTime timestamp;

        public ExpenseRecord(String transactionId, Department department, double amount,
                             String description, String approvedBy) {
            this.transactionId = transactionId;
            this.department = department;
            this.amount = amount;
            this.description = description;
            this.approvedBy = approvedBy;
            this.timestamp = LocalDateTime.now();
        }

        public String getTransactionId() { return transactionId; }
        public Department getDepartment() { return department; }
        public double getAmount() { return amount; }
        public String getDescription() { return description; }
        public String getApprovedBy() { return approvedBy; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("[%s] %s: $%,.2f (%s) - Appr: %s",
                    transactionId, department.name(), amount, description, approvedBy);
        }
    }

    private final Map<Department, Double> allocatedBudgets; // Demonstrates TreeMap (sorted keys)
    private final Map<Department, Double> spentBudgets;     // Demonstrates HashMap
    private final List<ExpenseRecord> expenseHistory;       // Demonstrates ArrayList

    public BudgetService() {
        this.allocatedBudgets = new TreeMap<>();
        this.spentBudgets = new HashMap<>();
        this.expenseHistory = new ArrayList<>();
        // Initialize all departments with zero allocation
        for (Department dept : Department.values()) {
            allocatedBudgets.put(dept, 0.0);
            spentBudgets.put(dept, 0.0);
        }
    }

    public synchronized void clear() {
        for (Department dept : Department.values()) {
            allocatedBudgets.put(dept, 0.0);
            spentBudgets.put(dept, 0.0);
        }
        expenseHistory.clear();
    }

    // Method Overloading Demonstration 1: allocateBudget
    public void allocateBudget(Department dept, double amount) throws ValidationException {
        allocateBudget(dept, amount, "Standard Production Allocation");
    }

    public void allocateBudget(Department dept, double amount, String justification) throws ValidationException {
        if (amount < 0) {
            throw new ValidationException("amount", "Allocated budget cannot be negative (" + amount + ")");
        }
        if (dept == null) {
            throw new ValidationException("department", "Department cannot be null");
        }
        allocatedBudgets.put(dept, amount);
    }

    // Method Overloading Demonstration 2: logExpense
    public ExpenseRecord logExpense(Department dept, double amount, String description)
            throws BudgetExceededException, ValidationException {
        return logExpense(dept, amount, description, "Line Producer");
    }

    public ExpenseRecord logExpense(Department dept, double amount, String description, String approvedBy)
            throws BudgetExceededException, ValidationException {
        if (amount <= 0) {
            throw new ValidationException("amount", "Expense amount must be strictly positive (" + amount + ")");
        }
        if (dept == null) {
            throw new ValidationException("department", "Department cannot be null");
        }

        double allocated = allocatedBudgets.getOrDefault(dept, 0.0);
        double currentSpent = spentBudgets.getOrDefault(dept, 0.0);
        double remaining = allocated - currentSpent;

        if (currentSpent + amount > allocated) {
            throw new BudgetExceededException(dept, amount, remaining);
        }

        spentBudgets.put(dept, currentSpent + amount);
        String txId = "TX-" + (expenseHistory.size() + 1001);
        ExpenseRecord record = new ExpenseRecord(txId, dept, amount, description, approvedBy);
        expenseHistory.add(record);
        return record;
    }

    /**
     * Calculates the total overall movie production budget using Streams and Lambdas.
     */
    public double getTotalAllocatedBudget() {
        return allocatedBudgets.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Calculates total spent funds to date across all departments using Streams and Lambdas.
     */
    public double getTotalSpentBudget() {
        return spentBudgets.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public double getRemainingBudget() {
        return getTotalAllocatedBudget() - getTotalSpentBudget();
    }

    public double getAllocatedForDepartment(Department dept) {
        return allocatedBudgets.getOrDefault(dept, 0.0);
    }

    public double getSpentForDepartment(Department dept) {
        return spentBudgets.getOrDefault(dept, 0.0);
    }

    public double getRemainingForDepartment(Department dept) {
        return getAllocatedForDepartment(dept) - getSpentForDepartment(dept);
    }

    public List<ExpenseRecord> getExpenseHistory() {
        return Collections.unmodifiableList(expenseHistory);
    }

    public double getTotalAllocated() {
        return getTotalAllocatedBudget();
    }

    public double getTotalSpent() {
        return getTotalSpentBudget();
    }

    public double getRemainingContingency() {
        return getRemainingBudget();
    }

    public double getBudgetUtilizationPercentage() {
        double alloc = getTotalAllocatedBudget();
        return alloc > 0 ? (getTotalSpentBudget() / alloc) * 100.0 : 0.0;
    }

    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("TOTAL ALLOCATED : $%,.2f\n", getTotalAllocatedBudget()));
        sb.append(String.format("TOTAL EXPENDED  : $%,.2f\n", getTotalSpentBudget()));
        sb.append(String.format("OVERALL VARIANCE: $%,.2f (%s)\n", getRemainingBudget(),
                getRemainingBudget() >= 0 ? "UNDER BUDGET" : "OVER BUDGET"));
        sb.append("-----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-30s | %-15s | %-15s | %-15s | %-8s\n",
                "DEPARTMENT", "ALLOCATED", "SPENT", "REMAINING", "UTIL %"));
        sb.append("-----------------------------------------------------------------------------------------\n");
        for (Map.Entry<Department, Double> entry : allocatedBudgets.entrySet()) {
            Department dept = entry.getKey();
            double allocated = entry.getValue();
            double spent = spentBudgets.getOrDefault(dept, 0.0);
            double rem = allocated - spent;
            double util = allocated > 0 ? (spent / allocated) * 100.0 : 0.0;
            sb.append(String.format("%-30s | $%,13.2f | $%,13.2f | $%,13.2f | %6.1f%%\n",
                    dept.getDisplayName(), allocated, spent, rem, util));
        }
        return sb.toString();
    }

    /**
     * Exports the detailed financial ledger to a persistent text file using IO Streams.
     */
    public void exportBudgetReportToFile(String filePath) throws IOException {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pw.println("=========================================================================================");
            pw.println("                      CINEFLOW: COMPREHENSIVE PRODUCTION BUDGET REPORT                   ");
            pw.println("=========================================================================================");
            pw.printf("Report Generated: %s\n", LocalDateTime.now().format(dtf));
            pw.printf("TOTAL ALLOCATED : $%,.2f\n", getTotalAllocatedBudget());
            pw.printf("TOTAL EXPENDED  : $%,.2f\n", getTotalSpentBudget());
            pw.printf("OVERALL VARIANCE: $%,.2f (%s)\n", getRemainingBudget(),
                    getRemainingBudget() >= 0 ? "UNDER BUDGET" : "OVER BUDGET");
            pw.println("-----------------------------------------------------------------------------------------");
            pw.printf("%-30s | %-15s | %-15s | %-15s | %-8s\n",
                    "DEPARTMENT", "ALLOCATED", "SPENT", "REMAINING", "UTIL %");
            pw.println("-----------------------------------------------------------------------------------------");

            for (Map.Entry<Department, Double> entry : allocatedBudgets.entrySet()) {
                Department dept = entry.getKey();
                double allocated = entry.getValue();
                double spent = spentBudgets.getOrDefault(dept, 0.0);
                double rem = allocated - spent;
                double util = allocated > 0 ? (spent / allocated) * 100.0 : 0.0;
                pw.printf("%-30s | $%,13.2f | $%,13.2f | $%,13.2f | %6.1f%%\n",
                        dept.getDisplayName(), allocated, spent, rem, util);
            }

            pw.println("=========================================================================================");
            pw.println("RECENT TRANSACTION AUDIT LOG:");
            if (expenseHistory.isEmpty()) {
                pw.println("  (No transactions recorded yet)");
            } else {
                for (ExpenseRecord er : expenseHistory) {
                    pw.printf("  • [%s] %s | $%,10.2f | %s | Appr: %s\n",
                            er.getTransactionId(), er.getDepartment().name(), er.getAmount(),
                            er.getDescription(), er.getApprovedBy());
                }
            }
            pw.println("=========================================================================================");
            pw.flush();
        }
    }
}
