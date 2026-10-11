package com.cineflow.model;

/**
 * Enumeration representing categories of promotional and marketing film assets.
 */
public enum AssetType {
    POSTER("First Look / Theatrical Poster"),
    TEASER("Official Teaser Video"),
    TRAILER("Theatrical Trailer (2K/4K)"),
    BANNER("Web / Billboard Banner"),
    SOCIAL_MEDIA("Social Media Motion Poster / Reel");

    private final String displayName;

    AssetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AssetType fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return POSTER;
        }
        String clean = text.trim().toUpperCase();
        for (AssetType t : values()) {
            if (t.name().equalsIgnoreCase(clean) || t.displayName.toUpperCase().contains(clean)) {
                return t;
            }
        }
        return POSTER;
    }
}
