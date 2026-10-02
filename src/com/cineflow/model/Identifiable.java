package com.cineflow.model;

import java.io.Serializable;

/**
 * Generic interface representing an entity that possesses a unique identifier.
 * Demonstrates the use of Generics in interface definitions.
 *
 * @param <ID> the type of identifier (e.g., String, Long, Integer)
 */
public interface Identifiable<ID> extends Serializable {
    /**
     * Retrieves the unique identifier of the entity.
     * @return the unique ID
     */
    ID getId();
}
