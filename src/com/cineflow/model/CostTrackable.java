package com.cineflow.model;

/**
 * Interface for entities that incur or track financial expenditures in the film budget.
 */
public interface CostTrackable {
    /**
     * Calculates the total cost incurred by this asset or operation.
     * @return Total financial cost in standard currency units
     */
    double computeTotalCost();

    /**
     * Retrieves a breakdown summary of how the cost was calculated.
     * @return Formatted breakdown string
     */
    String getCostBreakdown();
}
