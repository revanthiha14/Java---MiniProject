package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.asset.Equipment;
import com.cineflow.model.asset.Equipment.EquipmentCategory;
import com.cineflow.model.asset.Location;
import com.cineflow.model.asset.Location.LocationType;
import com.cineflow.model.asset.ProductionAsset;
import com.cineflow.repository.Repository;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service managing equipment inventory and filming location bookings.
 * Demonstrates:
 *  - Generic Repository integration
 *  - Polymorphism on ProductionAsset hierarchy
 *  - Method Overloading
 *  - Functional Programming: Predicates, Streams, Lambdas
 *  - Custom Exceptions
 */
public class AssetService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Repository<ProductionAsset, String> assetRepository;

    public AssetService(Repository<ProductionAsset, String> assetRepository) {
        this.assetRepository = assetRepository;
    }

    public void registerAsset(ProductionAsset asset) throws ValidationException {
        if (asset == null) {
            throw new ValidationException("asset", "Asset cannot be null");
        }
        if (asset.getId() == null || asset.getId().trim().isEmpty()) {
            throw new ValidationException("id", "Asset ID cannot be empty");
        }
        if (asset.getName() == null || asset.getName().trim().isEmpty()) {
            throw new ValidationException("name", "Asset name cannot be empty");
        }
        if (asset.getDailyRentalCost() < 0) {
            throw new ValidationException("dailyRentalCost", "Rental cost cannot be negative");
        }
        assetRepository.save(asset);
    }

    // Method Overloading Demonstration: registerEquipment variants
    public Equipment registerEquipment(String id, String name, double dailyCost, EquipmentCategory category)
            throws ValidationException {
        Equipment eq = new Equipment(id, name, dailyCost, category);
        registerAsset(eq);
        return eq;
    }

    public Equipment registerEquipment(String id, String name, double dailyCost, String serialNumber,
                                      EquipmentCategory category, String condition, boolean insuranceRequired)
            throws ValidationException {
        Equipment eq = new Equipment(id, name, dailyCost, serialNumber, category, condition, insuranceRequired);
        registerAsset(eq);
        return eq;
    }

    // Method Overloading Demonstration: registerLocation
    public Location registerLocation(String id, String name, double dailyCost, String address, String city,
                                     LocationType type, boolean permits, int capacity, double permitFee)
            throws ValidationException {
        Location loc = new Location(id, name, dailyCost, address, city, type, permits, capacity, permitFee);
        registerAsset(loc);
        return loc;
    }

    public ProductionAsset getAssetById(String id) throws ResourceNotFoundException {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", id));
    }

    public List<ProductionAsset> getAllAssets() {
        return assetRepository.findAll();
    }

    public List<Equipment> getAllEquipment() {
        return assetRepository.findAll().stream()
                .filter(a -> a instanceof Equipment)
                .map(a -> (Equipment) a)
                .sorted(Comparator.comparing(Equipment::getName))
                .collect(Collectors.toList());
    }

    public List<Location> getAllLocations() {
        return assetRepository.findAll().stream()
                .filter(a -> a instanceof Location)
                .map(a -> (Location) a)
                .sorted(Comparator.comparing(Location::getName))
                .collect(Collectors.toList());
    }

    public List<ProductionAsset> findAssets(Predicate<ProductionAsset> filter) {
        return assetRepository.findAll().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * Polymorphic total asset rental cost calculation across both Equipment and Locations.
     */
    public double computeTotalAssetExpenditure() {
        return assetRepository.findAll().stream()
                .mapToDouble(ProductionAsset::computeTotalCost) // Polymorphic dispatch
                .sum();
    }

    public boolean removeAsset(String id) {
        return assetRepository.deleteById(id);
    }
}
