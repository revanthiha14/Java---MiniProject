package com.cineflow.exception;

import com.cineflow.model.Department;

/**
 * Thrown when an expenditure or asset allocation breaches the approved department budget ceiling.
 */
public class BudgetExceededException extends CineFlowException {
    private static final long serialVersionUID = 1L;

    private final Department department;
    private final double attemptedAmount;
    private final double remainingBudget;

    public BudgetExceededException(Department department, double attemptedAmount, double remainingBudget) {
        super(String.format("CRITICAL OVERRUN: Department '%s' attempted to spend $%.2f, but only $%.2f remains in allocation!",
                department.getDisplayName(), attemptedAmount, remainingBudget));
        this.department = department;
        this.attemptedAmount = attemptedAmount;
        this.remainingBudget = remainingBudget;
    }

    public Department getDepartment() {
        return department;
    }

    public double getAttemptedAmount() {
        return attemptedAmount;
    }

    public double getRemainingBudget() {
        return remainingBudget;
    }

    public double getAvailableBudget() {
        return remainingBudget;
    }

    public double getOverdraftAmount() {
        return Math.max(0.0, attemptedAmount - remainingBudget);
    }
}
