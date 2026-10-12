package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents a dedicated filming location (soundstage, outdoor lot, indoor set, etc.).
 * Demonstrates: Encapsulation, Domain Validation, and Resource Allocation Modeling.
 */
public class ProductionLocation {
    private String id;
    private String name;
    private String address;
    private String locationType; // STUDIO, INDOOR, OUTDOOR, SET, OTHER
    private String contactPerson;
    private String contactDetails;
    private int capacity;
    private String availabilityStatus; // AVAILABLE, MAINTENANCE, UNAVAILABLE
    private double rentalCostPerDay;
    private String notes;

    public ProductionLocation(String id, String name, String address, String locationType,
                              double rentalCostPerDay) throws ValidationException {
        this(id, name, address, locationType, "TBD Contact", "TBD Phone/Email", 50,
                "AVAILABLE", rentalCostPerDay, "");
    }

    public ProductionLocation(String id, String name, String address, String locationType,
                              String contactPerson, String contactDetails, int capacity,
                              String availabilityStatus, double rentalCostPerDay, String notes)
            throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Location ID", "Location ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Location Name", "Location name cannot be empty.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException("Location Address", "Location address cannot be empty.");
        }
        if (capacity < 0) {
            throw new ValidationException("Capacity", "Capacity cannot be negative.");
        }
        if (rentalCostPerDay < 0.0) {
            throw new ValidationException("Rental Cost", "Rental cost cannot be negative.");
        }

        this.id = id.trim().toUpperCase();
        this.name = name.trim();
        this.address = address.trim();
        this.locationType = normalizeType(locationType);
        this.contactPerson = (contactPerson != null && !contactPerson.trim().isEmpty()) ? contactPerson.trim() : "TBD Contact";
        this.contactDetails = (contactDetails != null && !contactDetails.trim().isEmpty()) ? contactDetails.trim() : "TBD Phone/Email";
        this.capacity = capacity;
        this.availabilityStatus = normalizeStatus(availabilityStatus);
        this.rentalCostPerDay = rentalCostPerDay;
        this.notes = (notes != null) ? notes.trim() : "";
    }

    private String normalizeType(String type) {
        if (type == null || type.trim().isEmpty()) return "OTHER";
        String t = type.trim().toUpperCase();
        return switch (t) {
            case "STUDIO", "INDOOR", "OUTDOOR", "SET" -> t;
            default -> "OTHER";
        };
    }

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) return "AVAILABLE";
        String s = status.trim().toUpperCase();
        return switch (s) {
            case "MAINTENANCE", "UNAVAILABLE" -> s;
            default -> "AVAILABLE";
        };
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Location ID", "Location ID cannot be empty.");
        }
        this.id = id.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Location Name", "Location name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) throws ValidationException {
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException("Location Address", "Location address cannot be empty.");
        }
        this.address = address.trim();
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = normalizeType(locationType);
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = (contactPerson != null && !contactPerson.trim().isEmpty()) ? contactPerson.trim() : "TBD Contact";
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = (contactDetails != null && !contactDetails.trim().isEmpty()) ? contactDetails.trim() : "TBD Phone/Email";
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) throws ValidationException {
        if (capacity < 0) {
            throw new ValidationException("Capacity", "Capacity cannot be negative.");
        }
        this.capacity = capacity;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = normalizeStatus(availabilityStatus);
    }

    public double getRentalCostPerDay() {
        return rentalCostPerDay;
    }

    public void setRentalCostPerDay(double rentalCostPerDay) throws ValidationException {
        if (rentalCostPerDay < 0.0) {
            throw new ValidationException("Rental Cost", "Rental cost cannot be negative.");
        }
        this.rentalCostPerDay = rentalCostPerDay;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(availabilityStatus);
    }

    public String getStatusBadge() {
        return switch (availabilityStatus.toUpperCase()) {
            case "AVAILABLE" -> "[AVAILABLE]  ";
            case "MAINTENANCE" -> "[MAINTENANCE]";
            default -> "[UNAVAILABLE]";
        };
    }

    @Override
    public String toString() {
        return String.format("[%s] %-28s | Type: %-8s | Cap: %-4d | Rate/Day: $%,8.2f | Status: %s | Contact: %s (%s)",
                id, name, locationType, capacity, rentalCostPerDay, getStatusBadge().trim(), contactPerson, contactDetails);
    }
}
