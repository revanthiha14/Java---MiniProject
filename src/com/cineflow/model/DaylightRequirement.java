package com.cineflow.model;

/**
 * Lighting and time-of-day filming condition requirements for scenes.
 */
public enum DaylightRequirement {
    DAY_EXTERIOR("Day Exterior (Sunlight Dependent)"),
    NIGHT_EXTERIOR("Night Exterior (Requires Night Setup)"),
    GOLDEN_HOUR("Golden Hour / Twilight (Strict 45-min Window)"),
    INTERIOR_STUDIO("Interior Studio (Controlled Stage Lighting)"),
    INTERIOR_PRACTICAL("Interior Location (Mixed Lighting)");

    private final String description;

    DaylightRequirement(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isWeatherDependent() {
        return this == DAY_EXTERIOR || this == GOLDEN_HOUR;
    }

    @Override
    public String toString() {
        return description;
    }
}
