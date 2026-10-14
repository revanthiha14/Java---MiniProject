package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.Identifiable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Generic in-memory repository implementation using a hash-mapped data store.
 * Demonstrates: Java Generics with Bounded Type Parameters (<T extends Identifiable<ID>>),
 * Generic Class Implementation, Type-Safe Collections, and Generic Static Utility Methods (<E>).
 *
 * @param <ID> the identifier type (e.g. String, Long)
 * @param <T>  the entity type implementing Identifiable<ID>
 */
public class GenericRepository<ID, T extends Identifiable<ID>> implements Repository<ID, T> {
    private final Map<ID, T> storage;
    private final String entityName;

    public GenericRepository(String entityName) {
        this.entityName = (entityName != null && !entityName.trim().isEmpty()) ? entityName.trim() : "Entity";
        this.storage = new LinkedHashMap<>();
    }

    @Override
    public void add(T entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException(entityName, "Cannot add null " + entityName + ".");
        }
        ID id = entity.getId();
        if (id == null) {
            throw new ValidationException(entityName, entityName + " ID cannot be null.");
        }
        if (contains(id)) {
            throw new ValidationException(entityName, entityName + " with ID '" + id + "' already exists.");
        }
        storage.put(id, entity);
    }

    @Override
    public T findById(ID id) {
        if (id == null) return null;
        return storage.get(id);
    }

    @Override
    public T getOrThrow(ID id) throws ResourceNotFoundException {
        T item = findById(id);
        if (item == null) {
            throw new ResourceNotFoundException(entityName, String.valueOf(id));
        }
        return item;
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean remove(ID id) {
        if (id == null) return false;
        return storage.remove(id) != null;
    }

    @Override
    public boolean contains(ID id) {
        if (id == null) return false;
        return storage.containsKey(id);
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public List<T> filter(Predicate<? super T> predicate) {
        List<T> result = new ArrayList<>();
        if (predicate == null) return getAll();
        for (T entity : storage.values()) {
            if (predicate.test(entity)) {
                result.add(entity);
            }
        }
        return result;
    }

    // =========================================================================
    // GENERIC STATIC UTILITY METHODS
    // =========================================================================

    /**
     * Generic static utility method: filters any list with a type-safe predicate.
     * Demonstrates: Generic Method with type parameter <E>.
     *
     * @param <E>       the element type
     * @param list      the source list
     * @param predicate the condition to evaluate
     * @return a new list containing only matching elements
     */
    public static <E> List<E> filterList(List<E> list, Predicate<? super E> predicate) {
        List<E> matches = new ArrayList<>();
        if (list == null || predicate == null) return matches;
        for (E item : list) {
            if (predicate.test(item)) {
                matches.add(item);
            }
        }
        return matches;
    }

    /**
     * Generic static utility method: finds the first element matching a predicate.
     * Demonstrates: Generic Method with return type <E>.
     *
     * @param <E>       the element type
     * @param list      the source list
     * @param predicate the condition to evaluate
     * @return the first matching element, or null if none found
     */
    public static <E> E findFirst(List<E> list, Predicate<? super E> predicate) {
        if (list == null || predicate == null) return null;
        for (E item : list) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }
}
