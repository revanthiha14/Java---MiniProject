package com.cineflow.model;

/**
 * Three core phases of professional film making.
 */
public enum ProductionPhase {
    PRE_PRODUCTION("Pre-Production (Script, Casting, Location Scouting)"),
    PRODUCTION("In-Production / Shooting (Principal Photography)"),
    POST_PRODUCTION("Post-Production (Editing, Sound, VFX, Color Grading)"),
    RELEASED("Released (Theatrical / Streaming Distribution)");

    private final String description;

    ProductionPhase(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
