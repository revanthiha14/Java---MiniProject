package com.cineflow.model;

/**
 * Represents a financial expense incurred during film production.
 * Demonstrates: Encapsulation and Data Modeling.
 */
public class Expense {
    private String id;
    private String category;
    private double amount;
    private String description;

    public Expense(String id, String category, double amount, String description) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-18s : $%,12.2f (%s)", id, category, amount, description);
    }
}
