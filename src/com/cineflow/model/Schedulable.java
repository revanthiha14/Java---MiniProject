package com.cineflow.model;

import java.time.LocalDate;

/**
 * Interface contract for production items tied to a calendar shooting schedule.
 */
public interface Schedulable {
    /**
     * Gets the currently scheduled shoot date.
     * @return LocalDate of the shoot
     */
    LocalDate getScheduledDate();

    /**
     * Checks if this item has a scheduling conflict with another requested date and time slot.
     * @param date Target date
     * @param timeSlot Time slot description (e.g., "08:00 - 14:00")
     * @return true if a conflict exists, false otherwise
     */
    boolean hasScheduleConflict(LocalDate date, String timeSlot);
}
