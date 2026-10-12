package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents a Scene within a movie script/production.
 * Demonstrates: Encapsulation, Data Modeling, and Domain Validation.
 */
public class Scene {
    private int sceneNumber;
    private String title;
    private String location;
    private String status; // e.g. "PLANNED", "IN_PROGRESS", "COMPLETED"
    private String shootingDate; // e.g. "2026-11-15" or "TBD"
    private String slugline; // e.g. "INT. SOUNDSTAGE A - DAY" or "EXT. DOCK WAREHOUSE - NIGHT"
    private double scriptPages; // Screenplay page length (1 page ≈ 1 minute screen time)
    private String scriptContent; // Screenplay action description and dialogue lines

    public Scene(int sceneNumber, String title, String location, String status) throws ValidationException {
        this(sceneNumber, title, location, status, "TBD");
    }

    public Scene(int sceneNumber, String title, String location, String status, String shootingDate) throws ValidationException {
        this(sceneNumber, title, location, status, shootingDate, null, 1.0, null);
    }

    public Scene(int sceneNumber, String title, String location, String status, String shootingDate,
                 String slugline, double scriptPages, String scriptContent) throws ValidationException {
        if (sceneNumber <= 0) {
            throw new ValidationException("Scene Number", "Scene number must be a positive integer.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Scene Title", "Scene title cannot be empty.");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new ValidationException("Scene Location", "Scene location cannot be empty.");
        }
        this.sceneNumber = sceneNumber;
        this.title = title.trim();
        this.location = location.trim();
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "PLANNED";
        this.shootingDate = (shootingDate != null && !shootingDate.trim().isEmpty()) ? shootingDate.trim() : "TBD";
        this.slugline = (slugline != null && !slugline.trim().isEmpty()) ? slugline.trim().toUpperCase() : generateDefaultSlugline();
        this.scriptPages = (scriptPages > 0) ? scriptPages : 1.0;
        this.scriptContent = (scriptContent != null && !scriptContent.trim().isEmpty()) ? scriptContent.trim() : "";
    }

    private String generateDefaultSlugline() {
        return "EXT. " + location.toUpperCase() + " - DAY";
    }

    public int getSceneNumber() {
        return sceneNumber;
    }

    public void setSceneNumber(int sceneNumber) throws ValidationException {
        if (sceneNumber <= 0) {
            throw new ValidationException("Scene Number", "Scene number must be a positive integer.");
        }
        this.sceneNumber = sceneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Scene Title", "Scene title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) throws ValidationException {
        if (location == null || location.trim().isEmpty()) {
            throw new ValidationException("Scene Location", "Scene location cannot be empty.");
        }
        this.location = location.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "PLANNED";
    }

    public String getShootingDate() {
        return shootingDate;
    }

    public void setShootingDate(String shootingDate) {
        this.shootingDate = (shootingDate != null && !shootingDate.trim().isEmpty()) ? shootingDate.trim() : "TBD";
    }

    public String getSlugline() {
        return slugline;
    }

    public void setSlugline(String slugline) {
        this.slugline = (slugline != null && !slugline.trim().isEmpty())
                ? slugline.trim().toUpperCase()
                : generateDefaultSlugline();
    }

    public double getScriptPages() {
        return scriptPages;
    }

    public void setScriptPages(double scriptPages) throws ValidationException {
        if (scriptPages <= 0) {
            throw new ValidationException("Script Pages", "Script page count must be greater than zero.");
        }
        this.scriptPages = scriptPages;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = (scriptContent != null) ? scriptContent.trim() : "";
    }

    public boolean hasScript() {
        return scriptContent != null && !scriptContent.trim().isEmpty();
    }

    /**
     * Updates the screenplay script components for this scene.
     */
    public void updateScript(String newSlugline, double newPages, String newContent) throws ValidationException {
        if (newSlugline != null && !newSlugline.trim().isEmpty()) {
            setSlugline(newSlugline);
        }
        if (newPages > 0) {
            setScriptPages(newPages);
        }
        if (newContent != null) {
            setScriptContent(newContent);
        }
    }

    /**
     * Formats this scene's screenplay script following standard screenplay aesthetics.
     */
    public String getFormattedScript() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(String.format("SCENE #%-3d  %-35s  [Pages: %.1f | Est: ~%.0f min]\n",
                sceneNumber, title.toUpperCase(), scriptPages, scriptPages));
        sb.append(String.format("SLUGLINE: %s\n", slugline));
        sb.append(String.format("LOCATION: %-25s | STATUS: %-10s | SHOOT: %s\n",
                location, status, shootingDate));
        sb.append("--------------------------------------------------------------------------------\n");
        if (hasScript()) {
            sb.append(scriptContent).append("\n");
        } else {
            sb.append("   [No screenplay dialogue or action description recorded for this scene yet]\n");
            sb.append("   [Use 'Add/Modify Scene Script' to enter screenplay dialogue.]\n");
        }
        sb.append("================================================================================");
        return sb.toString();
    }

    @Override
    public String toString() {
        String dateStr = (shootingDate == null || shootingDate.equalsIgnoreCase("TBD")) ? "TBD" : shootingDate;
        String scriptBadge = hasScript() ? String.format("[Script: %.1f pgs]", scriptPages) : "[No Script]";
        return String.format("Scene #%d: %-22s | Loc: %-18s | Status: %-11s | Date: %-10s | %s",
                sceneNumber, title, location, status, dateStr, scriptBadge);
    }
}
