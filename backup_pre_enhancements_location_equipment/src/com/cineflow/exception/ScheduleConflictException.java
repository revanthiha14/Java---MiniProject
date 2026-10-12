package com.cineflow.exception;

/**
 * Custom checked exception thrown when a shooting schedule conflict is detected,
 * such as attempting to schedule two scenes at the same filming location on the
 * same date.
 * Demonstrates: Custom Exception Hierarchy and Business Rule Validation.
 */
public class ScheduleConflictException extends CineFlowException {
    private static final long serialVersionUID = 1L;

    private final String locationOrEntity;
    private final String conflictDate;
    private final String reason;

    public ScheduleConflictException(String message) {
        super(message);
        this.locationOrEntity = "General";
        this.conflictDate = "Unknown";
        this.reason = message;
    }

    public ScheduleConflictException(String locationOrEntity, String conflictDate, String reason) {
        super(String.format("Schedule conflict detected for '%s' on %s: %s",
                locationOrEntity, conflictDate, reason));
        this.locationOrEntity = locationOrEntity;
        this.conflictDate = conflictDate;
        this.reason = reason;
    }

    public String getLocationOrEntity() {
        return locationOrEntity;
    }

    public String getConflictDate() {
        return conflictDate;
    }

    public String getReason() {
        return reason;
    }
}
