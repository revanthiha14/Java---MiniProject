package com.cineflow.model;

/**
 * Enumeration representing the chronological stages and phases of a complete film production lifecycle.
 * Follows authentic industry sequence from pre-production narration to worldwide theatrical release.
 */
public enum ProductionStage {
    // Phase 1: Pre-Production
    SCRIPT_NARRATION("Pre-Production", "Script & Storyline Narration to Cast", 1),
    POSTER_PHOTOSHOOT("Pre-Production", "Look Test & Poster Photoshoot", 2),

    // Phase 2: Production (Principal Photography)
    PRINCIPAL_PHOTOGRAPHY("Production", "Scene Filming (Principal Photography)", 3),

    // Phase 3: Post-Production
    PICTURE_EDITING("Post-Production", "Picture & Video Editing (Assembly to Final Cut)", 4),
    AUDIO_AND_DUBBING("Post-Production", "Audio Dubbing (Voice-Over / ADR) & Sound Design", 5),
    VFX_AND_CGI("Post-Production", "VFX, CGI Animation & Color Grading (DI)", 6),

    // Phase 4: Marketing, Publicity & Theatrical Release (Strict Proper Order)
    POSTER_RELEASE("Promotion & Release", "Official First Look & Poster Release", 7),
    TEASER_RELEASE("Promotion & Release", "Official Teaser Release", 8),
    TRAILER_RELEASE("Promotion & Release", "Official Theatrical Trailer Release", 9),
    PRESS_MEET("Promotion & Release", "Press Meet & Media Conference (Optional)", 10),
    PROMOTIONAL_CAMPAIGN("Promotion & Release", "Promotional Tours, City Campaigns & Marketing", 11),
    THEATRICAL_RELEASE("Promotion & Release", "Worldwide Movie Premiere & Theatrical Release", 12);

    private final String phaseName;
    private final String displayName;
    private final int sequenceOrder;

    ProductionStage(String phaseName, String displayName, int sequenceOrder) {
        this.phaseName = phaseName;
        this.displayName = displayName;
        this.sequenceOrder = sequenceOrder;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }
}
