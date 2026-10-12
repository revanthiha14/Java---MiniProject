package com.cineflow.model;

/**
 * Enumeration representing review and approval states for promotional marketing assets.
 */
public enum ApprovalStatus {
    PENDING("Pending Review"),
    APPROVED("Approved for Release"),
    REJECTED("Revision Required");

    private final String displayName;

    ApprovalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ApprovalStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return PENDING;
        }
        String clean = text.trim().toUpperCase();
        for (ApprovalStatus s : values()) {
            if (s.name().equalsIgnoreCase(clean) || s.displayName.toUpperCase().contains(clean)) {
                return s;
            }
        }
        return PENDING;
    }
}
