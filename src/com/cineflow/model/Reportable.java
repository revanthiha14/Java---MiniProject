package com.cineflow.model;

/**
 * Interface contract for entities capable of generating formatted text reports.
 * Used across cast, crew, scenes, and call sheets.
 */
public interface Reportable {
    /**
     * Generates a detailed, human-readable terminal report of the entity state.
     * @return Formatted multi-line text report
     */
    String generateDetailedReport();
}
