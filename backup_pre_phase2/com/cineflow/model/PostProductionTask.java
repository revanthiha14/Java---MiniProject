package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents a specialized post-production task within editing, sound mixing, VFX, or rendering.
 * Tracks assigned leads, technical workflow parameters, completion percentages, and QC sign-offs.
 * Demonstrates: Encapsulation, Validation, State Management, and Progress Metrics.
 */
public class PostProductionTask {
    private String taskId;
    private PostProductionType type;
    private String taskName;
    private String leadSpecialist;
    private String status; // PLANNED, IN_PROGRESS, QUALITY_CHECK, COMPLETED, REVISION_REQUESTED
    private int progressPercentage; // 0 - 100
    private String technicalSpecs;
    private String notes;

    public PostProductionTask(String taskId, PostProductionType type, String taskName, String leadSpecialist)
            throws ValidationException {
        this(taskId, type, taskName, leadSpecialist, "PLANNED", 0, "Standard Production Spec", "");
    }

    public PostProductionTask(String taskId, PostProductionType type, String taskName, String leadSpecialist,
                              String status, int progressPercentage, String technicalSpecs, String notes)
            throws ValidationException {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new ValidationException("Task ID", "Post-production task ID cannot be empty.");
        }
        if (type == null) {
            throw new ValidationException("Post-Production Type", "Post-production discipline cannot be null.");
        }
        if (taskName == null || taskName.trim().isEmpty()) {
            throw new ValidationException("Task Name", "Post-production task name cannot be empty.");
        }
        if (leadSpecialist == null || leadSpecialist.trim().isEmpty()) {
            throw new ValidationException("Lead Specialist", "Lead specialist or facility vendor cannot be empty.");
        }

        this.taskId = taskId.trim().toUpperCase();
        this.type = type;
        this.taskName = taskName.trim();
        this.leadSpecialist = leadSpecialist.trim();
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "PLANNED";
        this.progressPercentage = Math.clamp(progressPercentage, 0, 100);
        this.technicalSpecs = (technicalSpecs != null && !technicalSpecs.trim().isEmpty())
                ? technicalSpecs.trim() : "Standard Spec";
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) throws ValidationException {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new ValidationException("Task ID", "Task ID cannot be empty.");
        }
        this.taskId = taskId.trim().toUpperCase();
    }

    public PostProductionType getType() {
        return type;
    }

    public void setType(PostProductionType type) throws ValidationException {
        if (type == null) {
            throw new ValidationException("Post-Production Type", "Post-production discipline cannot be null.");
        }
        this.type = type;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) throws ValidationException {
        if (taskName == null || taskName.trim().isEmpty()) {
            throw new ValidationException("Task Name", "Task name cannot be empty.");
        }
        this.taskName = taskName.trim();
    }

    public String getLeadSpecialist() {
        return leadSpecialist;
    }

    public void setLeadSpecialist(String leadSpecialist) throws ValidationException {
        if (leadSpecialist == null || leadSpecialist.trim().isEmpty()) {
            throw new ValidationException("Lead Specialist", "Lead specialist cannot be empty.");
        }
        this.leadSpecialist = leadSpecialist.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "PLANNED";
        if ("COMPLETED".equalsIgnoreCase(this.status)) {
            this.progressPercentage = 100;
        }
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = Math.clamp(progressPercentage, 0, 100);
        if (this.progressPercentage == 100 && !"QUALITY_CHECK".equalsIgnoreCase(this.status)) {
            this.status = "COMPLETED";
        }
    }

    public String getTechnicalSpecs() {
        return technicalSpecs;
    }

    public void setTechnicalSpecs(String technicalSpecs) {
        this.technicalSpecs = (technicalSpecs != null && !technicalSpecs.trim().isEmpty())
                ? technicalSpecs.trim() : "Standard Spec";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public void updateProgress(int percentage, String newStatus, String newNotes) {
        setProgressPercentage(percentage);
        if (newStatus != null && !newStatus.trim().isEmpty()) {
            this.status = newStatus.trim().toUpperCase();
        }
        if (newNotes != null && !newNotes.trim().isEmpty()) {
            this.notes = newNotes.trim();
        }
    }

    public void markCompleted() {
        this.progressPercentage = 100;
        this.status = "COMPLETED";
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status) || progressPercentage >= 100;
    }

    public String getStatusBadge() {
        return switch (status.toUpperCase()) {
            case "COMPLETED" -> "[COMPLETED]     ";
            case "QUALITY_CHECK" -> "[QC IN REVIEW]  ";
            case "IN_PROGRESS" -> "[IN PROGRESS]   ";
            case "REVISION_REQUESTED" -> "[REVISION REQ]  ";
            default -> "[PLANNED]       ";
        };
    }

    public String getProgressBar() {
        int filled = progressPercentage / 10;
        return "[" + "=".repeat(filled) + " ".repeat(10 - filled) + "] " + progressPercentage + "%";
    }

    @Override
    public String toString() {
        return String.format("[%s] %-14s | %-13s | %-32s | %-16s | Lead: %-18s | Specs: %s",
                taskId, getStatusBadge().trim(), type.name(), taskName, getProgressBar(), leadSpecialist, technicalSpecs);
    }
}
