package com.cineflow.model;

/**
 * Progression status of an individual film scene.
 */
public enum SceneStatus {
    DRAFT("Draft / Unscheduled"),
    SCHEDULED("Scheduled for Shooting"),
    IN_PROGRESS("Currently Filming"),
    COMPLETED("Wrapped / Filmed"),
    APPROVED("Approved by Director"),
    RESIGN_REQUIRED("Requires Reshoot");

    private final String label;

    SceneStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
