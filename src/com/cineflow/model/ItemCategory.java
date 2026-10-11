package com.cineflow.model;

/**
 * Enumeration representing categories of production inventory items.
 */
public enum ItemCategory {
    PROP("Prop"),
    COSTUME("Costume / Wardrobe"),
    EQUIPMENT("Filming & Sound Equipment");

    private final String displayName;

    ItemCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ItemCategory fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return PROP;
        }
        String clean = text.trim().toUpperCase();
        for (ItemCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(clean) || cat.displayName.toUpperCase().contains(clean)) {
                return cat;
            }
        }
        return PROP;
    }
}
