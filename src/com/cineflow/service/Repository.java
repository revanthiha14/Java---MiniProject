package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.Identifiable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Generic repository interface defining standard CRUD and query contracts for domain entities.
 * Demonstrates: Generic Interfaces, Bounded Type Parameters (<T extends Identifiable<ID>>),
 * and Type Safety.
 *
 * @param <ID> the unique identifier type
 * @param <T>  the entity type implementing Identifiable<ID>
 */
public interface Repository<ID, T extends Identifiable<ID>> {
    /**
     * Adds an entity to the repository.
     *
     * @param entity the entity to store
     * @throws ValidationException if the entity or its ID is null or duplicate
     */
    void add(T entity) throws ValidationException;

    /**
     * Finds an entity by its identifier.
     *
     * @param id the entity ID
     * @return the entity, or null if not found
     */
    T findById(ID id);

    /**
     * Finds an entity by its identifier or throws a ResourceNotFoundException.
     *
     * @param id the entity ID
     * @return the found entity
     * @throws ResourceNotFoundException if the entity is not found
     */
    T getOrThrow(ID id) throws ResourceNotFoundException;

    /**
     * Retrieves all entities in the repository.
     *
     * @return a list of all stored entities
     */
    List<T> getAll();

    /**
     * Removes an entity by its identifier.
     *
     * @param id the entity ID
     * @return true if removed, false otherwise
     */
    boolean remove(ID id);

    /**
     * Checks if the repository contains an entity with the specified identifier.
     *
     * @param id the entity ID
     * @return true if present, false otherwise
     */
    boolean contains(ID id);

    /**
     * Returns the number of entities in the repository.
     *
     * @return total entity count
     */
    int size();

    /**
     * Checks if the repository is empty.
     *
     * @return true if empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Clears all entities from the repository.
     */
    void clear();

    /**
     * Filters stored entities matching the specified predicate.
     *
     * @param predicate criteria for filtering
     * @return list of matching entities
     */
    List<T> filter(Predicate<? super T> predicate);
}
