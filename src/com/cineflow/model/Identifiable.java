package com.cineflow.model;

/**
 * Generic interface representing any domain entity with a unique identifier.
 * Demonstrates: Java Generics, Generic Interface, and Type Parameterization (<ID>).
 *
 * @param <ID> the type of identifier (e.g. String, Long, Integer)
 */
public interface Identifiable<ID> {
    /**
     * Returns the unique identifier of the entity.
     *
     * @return the unique entity ID
     */
    ID getId();
}
