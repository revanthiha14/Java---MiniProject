package com.cineflow.model.asset;

/**
 * Represents a physical filming location (sound stages, outdoor ranches, urban streets).
 * Demonstrates:
 *  - Inheritance from ProductionAsset
 *  - CostTrackable custom calculation with fixed permit fees
 *  - Method overriding
 */
public class Location extends ProductionAsset {
    private static final long serialVersionUID = 1L;

    public enum LocationType {
        INDOOR_STUDIO,
        OUTDOOR_NATURAL,
        URBAN_STREET,
        HISTORICAL_MONUMENT,
        WATER_TANK_STAGE
    }

    private String address;
    private String city;
    private LocationType locationType;
    private boolean permitsGranted;
    private int maxCrewCapacity;
    private double municipalPermitFee;

    public Location() {
        this("LOC-000", "Generic Studio", 2000.0, "100 Studio Way", "Burbank", LocationType.INDOOR_STUDIO, true, 80, 500.0);
    }

    public Location(String id, String name, double dailyRentalCost, String address, String city,
                    LocationType locationType, boolean permitsGranted, int maxCrewCapacity, double municipalPermitFee) {
        super(id, name, dailyRentalCost);
        this.address = address;
        this.city = city;
        this.locationType = locationType;
        this.permitsGranted = permitsGranted;
        this.maxCrewCapacity = maxCrewCapacity;
        this.municipalPermitFee = municipalPermitFee;
    }

    @Override
    public String getAssetCategory() {
        return "Location: " + locationType.name();
    }

    @Override
    public double computeTotalCost() {
        if (daysBooked == 0) return 0.0;
        // Rental cost across booked days plus one-time municipal shooting permit fee
        return (dailyRentalCost * daysBooked) + municipalPermitFee;
    }

    @Override
    public String getCostBreakdown() {
        double rental = dailyRentalCost * daysBooked;
        return String.format("Rental (%d days @ $%.2f): $%.2f + Municipal Permit Fee: $%.2f = Total: $%.2f",
                daysBooked, dailyRentalCost, rental, municipalPermitFee, computeTotalCost());
    }

    @Override
    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== LOCATION RECORD: %s (ID: %s) ===\n", name, id));
        sb.append(String.format("Type           : %s\n", locationType));
        sb.append(String.format("Address        : %s, %s\n", address, city));
        sb.append(String.format("Max Capacity   : %d personnel\n", maxCrewCapacity));
        sb.append(String.format("Permit Status  : %s (Permit Fee: $%.2f)\n", permitsGranted ? "OFFICIALLY GRANTED" : "PENDING/UNAPPROVED", municipalPermitFee));
        sb.append(String.format("Daily Rate     : $%.2f | Days Booked: %d | Status: %s\n", dailyRentalCost, daysBooked, available ? "AVAILABLE" : "RESERVED"));
        sb.append(String.format("Financials     : %s\n", getCostBreakdown()));
        return sb.toString();
    }

    // Getters and Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public boolean isPermitsGranted() {
        return permitsGranted;
    }

    public void setPermitsGranted(boolean permitsGranted) {
        this.permitsGranted = permitsGranted;
    }

    public int getMaxCrewCapacity() {
        return maxCrewCapacity;
    }

    public void setMaxCrewCapacity(int maxCrewCapacity) {
        this.maxCrewCapacity = maxCrewCapacity;
    }

    public double getMunicipalPermitFee() {
        return municipalPermitFee;
    }

    public void setMunicipalPermitFee(double municipalPermitFee) {
        this.municipalPermitFee = municipalPermitFee;
    }
}
