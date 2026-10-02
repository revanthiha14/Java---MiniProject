package com.cineflow.service;

import com.cineflow.model.Movie;
import com.cineflow.model.ProductionHouse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service managing multiple Production Houses across the filmmaking ecosystem.
 * Demonstrates:
 *  - Central studio registry using LinkedHashMap
 *  - Dynamic switching of active studio and active project
 *  - Multi-studio catalog queries
 */
public class StudioService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, ProductionHouse> studios;
    private String activeStudioId;

    public StudioService() {
        this.studios = new LinkedHashMap<>();
        this.activeStudioId = null;
    }

    public synchronized void registerStudio(ProductionHouse studio) {
        if (studio != null && studio.getId() != null) {
            studios.put(studio.getId(), studio);
            if (activeStudioId == null) {
                activeStudioId = studio.getId();
            }
        }
    }

    public Optional<ProductionHouse> getStudio(String studioId) {
        if (studioId == null) return Optional.empty();
        return Optional.ofNullable(studios.get(studioId));
    }

    public List<ProductionHouse> getAllStudios() {
        return new ArrayList<>(studios.values());
    }

    public ProductionHouse getActiveStudio() {
        if (activeStudioId != null && studios.containsKey(activeStudioId)) {
            return studios.get(activeStudioId);
        }
        if (!studios.isEmpty()) {
            ProductionHouse first = studios.values().iterator().next();
            activeStudioId = first.getId();
            return first;
        }
        return null;
    }

    public boolean setActiveStudio(String studioId) {
        if (studioId != null && studios.containsKey(studioId)) {
            this.activeStudioId = studioId;
            return true;
        }
        return false;
    }

    public boolean setActiveStudio(ProductionHouse studio) {
        if (studio != null && studios.containsKey(studio.getId())) {
            this.activeStudioId = studio.getId();
            return true;
        }
        return false;
    }

    public String getActiveStudioId() {
        return activeStudioId;
    }

    public Movie getActiveMovie() {
        ProductionHouse studio = getActiveStudio();
        return studio != null ? studio.getActiveMovie() : null;
    }

    public int getStudioCount() {
        return studios.size();
    }

    public int getTotalMovieCount() {
        return studios.values().stream().mapToInt(ProductionHouse::getMovieCount).sum();
    }

    public void clear() {
        studios.clear();
        activeStudioId = null;
    }
}
