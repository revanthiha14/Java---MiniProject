package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents a physical production inventory item (prop, costume, or filming equipment).
 * Demonstrates: Encapsulation, State Tracking, Invariant Preservation, and ValidationException.
 */
public class InventoryItem implements Identifiable<String> {
    private String id;
    private String name;
    private ItemCategory category;
    private int totalQuantity;
    private int availableQuantity;
    private int damagedQuantity;
    private int lostQuantity;
    private String condition;
    private String notes;

    public InventoryItem(String id, String name, ItemCategory category, int totalQuantity,
                         String condition, String notes) throws ValidationException {
        this(id, name, category, totalQuantity, totalQuantity, 0, 0, condition, notes);
    }

    public InventoryItem(String id, String name, ItemCategory category, int totalQuantity,
                         int availableQuantity, int damagedQuantity, int lostQuantity,
                         String condition, String notes) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Item ID", "Item ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Item Name", "Item name cannot be empty.");
        }
        if (totalQuantity <= 0) {
            throw new ValidationException("Total Quantity", "Total quantity must be greater than zero.");
        }
        if (availableQuantity < 0 || damagedQuantity < 0 || lostQuantity < 0) {
            throw new ValidationException("Quantities", "Inventory quantities cannot be negative.");
        }
        if (availableQuantity + damagedQuantity + lostQuantity > totalQuantity) {
            throw new ValidationException("Quantities",
                    "Sum of available (" + availableQuantity + "), damaged (" + damagedQuantity
                            + "), and lost (" + lostQuantity + ") cannot exceed total quantity (" + totalQuantity + ").");
        }

        this.id = id.trim();
        this.name = name.trim();
        this.category = (category != null) ? category : ItemCategory.PROP;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.damagedQuantity = damagedQuantity;
        this.lostQuantity = lostQuantity;
        this.condition = (condition != null && !condition.trim().isEmpty()) ? condition.trim() : "GOOD";
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Item ID", "Item ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Item Name", "Item name cannot be empty.");
        }
        this.name = name.trim();
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = (category != null) ? category : ItemCategory.PROP;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) throws ValidationException {
        if (totalQuantity <= 0) {
            throw new ValidationException("Total Quantity", "Total quantity must be positive.");
        }
        int activeInUse = getAssignedQuantity();
        if (totalQuantity < activeInUse + damagedQuantity + lostQuantity) {
            throw new ValidationException("Total Quantity",
                    "Cannot decrease total below currently checked-out and damaged/lost items ("
                            + (activeInUse + damagedQuantity + lostQuantity) + ").");
        }
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity - (activeInUse + damagedQuantity + lostQuantity);
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) throws ValidationException {
        if (availableQuantity < 0) {
            throw new ValidationException("Available Quantity", "Available quantity cannot be negative.");
        }
        this.availableQuantity = availableQuantity;
    }

    public int getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(int damagedQuantity) throws ValidationException {
        if (damagedQuantity < 0) {
            throw new ValidationException("Damaged Quantity", "Damaged quantity cannot be negative.");
        }
        this.damagedQuantity = damagedQuantity;
    }

    public int getLostQuantity() {
        return lostQuantity;
    }

    public void setLostQuantity(int lostQuantity) throws ValidationException {
        if (lostQuantity < 0) {
            throw new ValidationException("Lost Quantity", "Lost quantity cannot be negative.");
        }
        this.lostQuantity = lostQuantity;
    }

    public int getAssignedQuantity() {
        int assigned = totalQuantity - (availableQuantity + damagedQuantity + lostQuantity);
        return Math.max(0, assigned);
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = (condition != null && !condition.trim().isEmpty()) ? condition.trim() : "GOOD";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.trim() : "";
    }

    /**
     * Checks out a quantity of this item for shooting/production use.
     */
    public void checkout(int quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException("Checkout Quantity", "Quantity to checkout must be greater than zero.");
        }
        if (quantity > availableQuantity) {
            throw new ValidationException("Insufficient Stock",
                    "Cannot checkout " + quantity + " units of '" + name + "'. Only " + availableQuantity + " currently available.");
        }
        this.availableQuantity -= quantity;
    }

    /**
     * Processes return of checked-out units, categorizing good, damaged, and lost items.
     */
    public void processReturn(int goodQty, int damagedQty, int lostQty) throws ValidationException {
        if (goodQty < 0 || damagedQty < 0 || lostQty < 0) {
            throw new ValidationException("Return Quantities", "Return quantities cannot be negative.");
        }
        int totalReturning = goodQty + damagedQty + lostQty;
        int currentlyAssigned = getAssignedQuantity();
        if (totalReturning > currentlyAssigned) {
            throw new ValidationException("Return Exceeded",
                    "Returning " + totalReturning + " items exceeds currently checked-out quantity of " + currentlyAssigned + ".");
        }

        this.availableQuantity += goodQty;
        this.damagedQuantity += damagedQty;
        this.lostQuantity += lostQty;
    }

    public boolean isFullyAvailable() {
        return availableQuantity == totalQuantity;
    }

    public String getAvailabilityBadge() {
        if (availableQuantity == 0) {
            return "[OUT OF STOCK]";
        } else if (availableQuantity == totalQuantity) {
            return "[ALL AVAILABLE]";
        } else {
            return String.format("[%d/%d AVAIL]", availableQuantity, totalQuantity);
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %-26s | Cat: %-10s | Stock: %d (Avail: %d, In-Use: %d, Dmg: %d, Lost: %d) | Cond: %-8s %s",
                id, name, category.name(), totalQuantity, availableQuantity, getAssignedQuantity(),
                damagedQuantity, lostQuantity, condition, getAvailabilityBadge());
    }
}
