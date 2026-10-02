package com.cineflow.service;

import com.cineflow.model.Scene;

/**
 * Functional interface used for dynamically computing variable scene filming budgets.
 * Demonstrates:
 *  - Custom Functional Interface with @FunctionalInterface
 *  - Lambda expression target
 */
@FunctionalInterface
public interface CostEstimator {
    /**
     * Estimates the cost of shooting a scene given the number of scheduled shoot days.
     * @param scene Target scene
     * @param shootDays Estimated duration in days
     * @return Estimated cost in dollars
     */
    double estimate(Scene scene, int shootDays);
}
