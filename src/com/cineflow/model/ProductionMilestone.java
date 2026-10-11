package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents a key milestone in the master film production and release schedule.
 * Demonstrates: Encapsulation, Validation, Formatting, and Pipeline Dependency Management.
 */
public class ProductionMilestone {
    private String id;
    private ProductionStage stage;
    private String title;
    private String targetDate; // YYYY-MM-DD or TBD
    private String status; // PLANNED, IN_PROGRESS, COMPLETED, DELAYED
    private String responsibleLead; // e.g. Director, Chief Editor, PR Head
    private boolean optional;
    private String notes;

    public ProductionMilestone(String id, ProductionStage stage, String targetDate) throws ValidationException {
        this(id, stage, stage.getDisplayName(), targetDate, "PLANNED", "TBD", false, "");
    }

    public ProductionMilestone(String id, ProductionStage stage, String title, String targetDate,
                               String status, String responsibleLead, boolean optional, String notes) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Milestone ID", "Milestone ID cannot be empty.");
        }
        if (stage == null) {
            throw new ValidationException("Production Stage", "Production stage cannot be null.");
        }
        this.id = id.trim().toUpperCase();
        this.stage = stage;
        this.title = (title != null && !title.trim().isEmpty()) ? title.trim() : stage.getDisplayName();
        this.targetDate = (targetDate != null && !targetDate.trim().isEmpty()) ? targetDate.trim() : "TBD";
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "PLANNED";
        this.responsibleLead = (responsibleLead != null && !responsibleLead.trim().isEmpty()) ? responsibleLead.trim() : "TBD";
        this.optional = optional;
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Milestone ID", "Milestone ID cannot be empty.");
        }
        this.id = id.trim().toUpperCase();
    }

    public ProductionStage getStage() {
        return stage;
    }

    public void setStage(ProductionStage stage) throws ValidationException {
        if (stage == null) {
            throw new ValidationException("Production Stage", "Production stage cannot be null.");
        }
        this.stage = stage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = (title != null && !title.trim().isEmpty()) ? title.trim() : stage.getDisplayName();
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = (targetDate != null && !targetDate.trim().isEmpty()) ? targetDate.trim() : "TBD";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "PLANNED";
    }

    public String getResponsibleLead() {
        return responsibleLead;
    }

    public void setResponsibleLead(String responsibleLead) {
        this.responsibleLead = (responsibleLead != null && !responsibleLead.trim().isEmpty()) ? responsibleLead.trim() : "TBD";
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    public boolean isInProgress() {
        return "IN_PROGRESS".equalsIgnoreCase(status);
    }

    public String getStatusBadge() {
        return switch (status.toUpperCase()) {
            case "COMPLETED" -> "[COMPLETED]  ";
            case "IN_PROGRESS" -> "[IN PROGRESS]";
            case "DELAYED" -> "[DELAYED]    ";
            default -> "[PLANNED]    ";
        };
    }

    @Override
    public String toString() {
        String optTag = optional ? " (Optional)" : "";
        String dateStr = (targetDate == null || targetDate.equalsIgnoreCase("TBD")) ? "TBD" : targetDate;
        return String.format("[%s] %-14s | %-45s | Date: %-10s | Lead: %-22s %s",
                id, getStatusBadge().trim(), title + optTag, dateStr, responsibleLead,
                notes.isEmpty() ? "" : "(" + notes + ")");
    }
}
