package com.cineflow.model;

/**
 * Standard film crew and studio departments.
 */
public enum Department {
    DIRECTING("Directing & Creative"),
    CAMERA("Camera & Cinematography"),
    LIGHTING("Grip & Electrical / Lighting"),
    SOUND("Sound Recording & Mixing"),
    ART_AND_PROPS("Art Department & Props"),
    COSTUME_AND_MAKEUP("Wardrobe & Makeup"),
    VFX_AND_POST("Visual Effects & Post-Production"),
    STUNTS("Stunts & Special Actions"),
    LOGISTICS_AND_CATERING("Production Logistics & Catering");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
