package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents a financial expense incurred during film production.
 * Demonstrates: Encapsulation, Data Modeling, and Domain Validation.
 */
public class Expense {
    private String id;
    private String category;
    private double amount;
    private String description;

    public Expense(String id, String category, double amount, String description) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Expense ID", "Expense ID cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Category", "Expense category cannot be empty.");
        }
        if (amount <= 0.0) {
            throw new ValidationException("Expense Amount", "Expense amount must be greater than zero.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Description", "Expense description cannot be empty.");
        }
        this.id = id.trim();
        this.category = category.trim();
        this.amount = amount;
        this.description = description.trim();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Expense ID", "Expense ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) throws ValidationException {
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Category", "Expense category cannot be empty.");
        }
        this.category = category.trim();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) throws ValidationException {
        if (amount <= 0.0) {
            throw new ValidationException("Expense Amount", "Expense amount must be greater than zero.");
        }
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Description", "Expense description cannot be empty.");
        }
        this.description = description.trim();
    }

    @Override
    public String toString() {
        return String.format("[%s] %-18s : $%,12.2f (%s)", id, category, amount, description);
    }
}
