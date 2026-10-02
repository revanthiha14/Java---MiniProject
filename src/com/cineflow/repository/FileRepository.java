package com.cineflow.repository;

import com.cineflow.model.Identifiable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Generic file-based repository implementation using Java Object IO Streams.
 * Demonstrates:
 *  - Generic class with bounded type parameters: <T extends Identifiable<ID>, ID>
 *  - Map Collection Framework (LinkedHashMap for maintaining insertion order)
 *  - IO Streams (FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream)
 *  - Exception handling with try-with-resources
 */
public class FileRepository<T extends Identifiable<ID>, ID> implements Repository<T, ID> {
    private final Map<ID, T> storageMap;

    public FileRepository() {
        this.storageMap = new LinkedHashMap<>();
    }

    @Override
    public synchronized void save(T entity) {
        if (entity != null && entity.getId() != null) {
            storageMap.put(entity.getId(), entity);
        }
    }

    @Override
    public synchronized void saveAll(Collection<T> entities) {
        if (entities != null) {
            for (T item : entities) {
                save(item);
            }
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storageMap.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storageMap.values());
    }

    @Override
    public synchronized boolean deleteById(ID id) {
        if (id == null) return false;
        return storageMap.remove(id) != null;
    }

    @Override
    public boolean existsById(ID id) {
        if (id == null) return false;
        return storageMap.containsKey(id);
    }

    @Override
    public int count() {
        return storageMap.size();
    }

    @Override
    public synchronized void persistToStorage(String filePath) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(this.storageMap);
            oos.flush();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized void loadFromStorage(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                this.storageMap.clear();
                this.storageMap.putAll((Map<ID, T>) obj);
            }
        }
    }
}
