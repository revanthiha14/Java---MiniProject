package com.cineflow.exception;

import java.time.LocalDate;

/**
 * Thrown when attempting to book a talent, location, or scene at an overlapping
 * date and time slot that is already reserved.
 */
public class ScheduleConflictException extends CineFlowException {
    private static final long serialVersionUID = 1L;

    private final String entityName;
    private final LocalDate conflictDate;
    private final String timeSlot;

    public ScheduleConflictException(String entityName, LocalDate conflictDate, String timeSlot, String reason) {
        super(String.format("Schedule conflict detected for '%s' on %s (%s): %s",
                entityName, conflictDate, timeSlot, reason));
        this.entityName = entityName;
        this.conflictDate = conflictDate;
        this.timeSlot = timeSlot;
    }

    public String getEntityName() {
        return entityName;
    }

    public LocalDate getConflictDate() {
        return conflictDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }
}
