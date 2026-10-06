package com.cineflow.model;

/**
 * Represents a Scene within a movie script/production.
 * Demonstrates: Encapsulation and Data Modeling.
 */
public class Scene {
    private int sceneNumber;
    private String title;
    private String location;
    private String status; // e.g. "PLANNED", "IN_PROGRESS", "COMPLETED"

    public Scene(int sceneNumber, String title, String location, String status) {
        this.sceneNumber = sceneNumber;
        this.title = title;
        this.location = location;
        this.status = status;
    }

    public int getSceneNumber() {
        return sceneNumber;
    }

    public void setSceneNumber(int sceneNumber) {
        this.sceneNumber = sceneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Scene #%d: %-25s | Location: %-20s | Status: %s",
                sceneNumber, title, location, status);
    }
}
