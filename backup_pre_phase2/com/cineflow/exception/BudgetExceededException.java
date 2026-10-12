package com.cineflow.exception;

/**
 * Custom checked exception thrown when an expense would exceed the
 * allocated budget of a movie.
 * Demonstrates: Custom Exception Handling and Inheritance in Exception Hierarchy.
 */
public class BudgetExceededException extends CineFlowException {
    private static final long serialVersionUID = 1L;

    private final double attemptedAmount;
    private final double remainingBudget;
    private final double totalBudget;

    public BudgetExceededException(String message) {
        super(message);
        this.attemptedAmount = 0.0;
        this.remainingBudget = 0.0;
        this.totalBudget = 0.0;
    }

    public BudgetExceededException(double attemptedAmount, double remainingBudget, double totalBudget) {
        super(String.format("Expense of $%,.2f exceeds remaining budget of $%,.2f! (Allocated Budget: $%,.2f)",
                attemptedAmount, remainingBudget, totalBudget));
        this.attemptedAmount = attemptedAmount;
        this.remainingBudget = remainingBudget;
        this.totalBudget = totalBudget;
    }

    public double getAttemptedAmount() {
        return attemptedAmount;
    }

    public double getRemainingBudget() {
        return remainingBudget;
    }

    public double getTotalBudget() {
        return totalBudget;
    }
}
