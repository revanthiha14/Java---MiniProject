package com.cineflow.model;

/**
 * Enumeration representing the specialized disciplines of film post-production.
 * Encompasses Picture Editing, Sound Mixing, Visual Effects (CGI), and Final Rendering/Mastering.
 */
public enum PostProductionType {
    EDITING("Picture & Video Editing"),
    SOUND_MIXING("Sound Design & Audio Mixing"),
    VFX("Visual Effects & CGI Animation"),
    RENDERING("Render Farm & Master Export");

    private final String displayName;

    PostProductionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
