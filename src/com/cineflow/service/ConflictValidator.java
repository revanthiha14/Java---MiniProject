package com.cineflow.service;

import com.cineflow.model.Scene;
import java.time.LocalDate;

/**
 * Functional interface for evaluating custom scheduling validation rules via lambdas.
 */
@FunctionalInterface
public interface ConflictValidator {
    /**
     * Checks if a scene schedule candidate satisfies a custom operational constraint.
     * @param scene Target scene
     * @param targetDate Proposed shoot date
     * @param timeSlot Proposed time slot
     * @return true if valid and free of conflict, false otherwise
     */
    boolean isValid(Scene scene, LocalDate targetDate, String timeSlot);
}
