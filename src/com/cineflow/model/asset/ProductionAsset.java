package com.cineflow.model.asset;

import com.cineflow.model.CostTrackable;
import com.cineflow.model.Identifiable;
import com.cineflow.model.Reportable;
import java.util.Objects;

/**
 * Abstract class representing physical film assets (Equipment, Shooting Locations).
 * Demonstrates:
 *  - Abstract class with abstract and concrete methods
 *  - Inheritance hierarchy
 *  - Multiple interface implementation (Identifiable, CostTrackable, Reportable)
 *  - Encapsulation
 */
public abstract class ProductionAsset implements Identifiable<String>, CostTrackable, Reportable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected double dailyRentalCost;
    protected int daysBooked;
    protected boolean available;

    public ProductionAsset() {
        this("AST-000", "Generic Asset", 0.0);
    }

    public ProductionAsset(String id, String name, double dailyRentalCost) {
        this.id = id;
        this.name = name;
        this.dailyRentalCost = dailyRentalCost;
        this.daysBooked = 0;
        this.available = true;
    }

    /**
     * Abstract method defining the category or classification of this asset.
     */
    public abstract String getAssetCategory();

    public void bookDays(int days) {
        if (days > 0) {
            this.daysBooked += days;
        }
    }

    @Override
    public double computeTotalCost() {
        return dailyRentalCost * daysBooked;
    }

    public double calculateTotalCost() {
        return computeTotalCost();
    }

    public double getDailyCost() {
        return dailyRentalCost;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDailyRentalCost() {
        return dailyRentalCost;
    }

    public void setDailyRentalCost(double dailyRentalCost) {
        this.dailyRentalCost = dailyRentalCost;
    }

    public int getDaysBooked() {
        return daysBooked;
    }

    public void setDaysBooked(int daysBooked) {
        this.daysBooked = daysBooked;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductionAsset that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - $%.2f/day (Status: %s)",
                id, name, getAssetCategory(), dailyRentalCost, available ? "AVAILABLE" : "BOOKED");
    }
}
