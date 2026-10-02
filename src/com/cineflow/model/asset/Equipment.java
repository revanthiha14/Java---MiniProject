package com.cineflow.model.asset;

/**
 * Represents specialized filming equipment (cameras, lenses, sound rigs, lighting grids).
 * Demonstrates:
 *  - Inheritance from ProductionAsset
 *  - CostTrackable implementation & method overriding
 *  - Constructor chaining
 */
public class Equipment extends ProductionAsset {
    private static final long serialVersionUID = 1L;

    public enum EquipmentCategory {
        CAMERA_BODY,
        ANAMORPHIC_LENS,
        SOUND_RIG,
        LIGHTING_KIT,
        GIMBAL_STABILIZER,
        DRONE,
        VIRTUAL_LED_WALL
    }

    private String serialNumber;
    private EquipmentCategory category;
    private String condition;
    private boolean insuranceRequired;

    public Equipment() {
        this("EQ-000", "Generic Gear", 100.0, "SN-00000", EquipmentCategory.CAMERA_BODY, "GOOD", true);
    }

    public Equipment(String id, String name, double dailyRentalCost, EquipmentCategory category) {
        this(id, name, dailyRentalCost, "SN-" + System.currentTimeMillis() % 100000, category, "EXCELLENT", true);
    }

    public Equipment(String id, String name, double dailyRentalCost, String serialNumber,
                     EquipmentCategory category, String condition, boolean insuranceRequired) {
        super(id, name, dailyRentalCost);
        this.serialNumber = serialNumber;
        this.category = category;
        this.condition = condition;
        this.insuranceRequired = insuranceRequired;
    }

    @Override
    public String getAssetCategory() {
        return "Equipment: " + category.name();
    }

    @Override
    public double computeTotalCost() {
        double rental = dailyRentalCost * daysBooked;
        // Equipment insurance surcharge of 8% per rental day
        double insurance = insuranceRequired ? (rental * 0.08) : 0.0;
        return rental + insurance;
    }

    @Override
    public String getCostBreakdown() {
        double rental = dailyRentalCost * daysBooked;
        double insurance = insuranceRequired ? (rental * 0.08) : 0.0;
        return String.format("Rental: $%.2f + Insurance (8%%): $%.2f = Total: $%.2f", rental, insurance, rental + insurance);
    }

    @Override
    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== EQUIPMENT RECORD: %s (ID: %s) ===\n", name, id));
        sb.append(String.format("Serial Number : %s\n", serialNumber));
        sb.append(String.format("Category      : %s\n", category));
        sb.append(String.format("Condition     : %s\n", condition));
        sb.append(String.format("Daily Rate    : $%.2f | Insurance Required: %s\n", dailyRentalCost, insuranceRequired ? "YES" : "NO"));
        sb.append(String.format("Days Booked   : %d | Status: %s\n", daysBooked, available ? "AVAILABLE" : "RESERVED"));
        sb.append(String.format("Financials    : %s\n", getCostBreakdown()));
        return sb.toString();
    }

    // Getters and Setters
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public EquipmentCategory getCategory() {
        return category;
    }

    public void setCategory(EquipmentCategory category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isInsuranceRequired() {
        return insuranceRequired;
    }

    public void setInsuranceRequired(boolean insuranceRequired) {
        this.insuranceRequired = insuranceRequired;
    }
}
