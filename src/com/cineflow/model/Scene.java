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

    public Scene(int sceneNumber, String title, String location, String status) throws ValidationException {
        this(sceneNumber, title, location, status, "TBD");
    }

    public Scene(int sceneNumber, String title, String location, String status, String shootingDate) throws ValidationException {
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

    @Override
    public String toString() {
        String dateStr = (shootingDate == null || shootingDate.equalsIgnoreCase("TBD")) ? "TBD" : shootingDate;
        return String.format("Scene #%d: %-24s | Location: %-20s | Status: %-12s | Shoot Date: %s",
                sceneNumber, title, location, status, dateStr);
    }
}
