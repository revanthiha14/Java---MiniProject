package com.cineflow.repository;

import com.cineflow.model.Identifiable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining standard CRUD operations.
 * Demonstrates:
 *  - Generics with bounded type parameters (T extends Identifiable<ID>)
 *  - Abstraction over data persistence
 *
 * @param <T>  The entity type
 * @param <ID> The entity's primary key identifier type
 */
public interface Repository<T extends Identifiable<ID>, ID> {
    void save(T entity);
    void saveAll(Collection<T> entities);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
    boolean existsById(ID id);
    int count();
    void persistToStorage(String filePath) throws IOException;
    void loadFromStorage(String filePath) throws IOException, ClassNotFoundException;
}
