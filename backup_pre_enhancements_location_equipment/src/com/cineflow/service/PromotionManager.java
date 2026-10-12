package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.ApprovalStatus;
import com.cineflow.model.AssetType;
import com.cineflow.model.PromotionalAsset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service managing promotional and marketing assets (Posters, Teasers, Trailers, Banners, Social Media).
 * Coordinates asset registration, search, multi-version revisions, and the Approve/Reject workflow.
 */
public class PromotionManager {
    private final Map<String, PromotionalAsset> assets;

    public PromotionManager() {
        this.assets = new LinkedHashMap<>();
    }

    public void addAsset(PromotionalAsset asset) throws ValidationException {
        if (asset == null) {
            throw new ValidationException("Promotional Asset", "Cannot add null promotional asset.");
        }
        String key = asset.getAssetId().trim().toUpperCase();
        if (assets.containsKey(key)) {
            throw new ValidationException("Asset ID", "Asset with ID '" + asset.getAssetId() + "' already exists.");
        }
        assets.put(key, asset);
    }

    public PromotionalAsset findAssetById(String assetId) {
        if (assetId == null) return null;
        return assets.get(assetId.trim().toUpperCase());
    }

    public PromotionalAsset getAssetOrThrow(String assetId) throws ResourceNotFoundException {
        PromotionalAsset asset = findAssetById(assetId);
        if (asset == null) {
            throw new ResourceNotFoundException("Promotional Asset", (assetId != null) ? assetId.trim() : "null");
        }
        return asset;
    }

    public List<PromotionalAsset> getAllAssets() {
        return new ArrayList<>(assets.values());
    }

    public List<PromotionalAsset> getAssetsForMovie(String movieId) {
        List<PromotionalAsset> list = new ArrayList<>();
        if (movieId == null) return list;
        for (PromotionalAsset a : assets.values()) {
            if (a.getMovieId().equalsIgnoreCase(movieId.trim())) {
                list.add(a);
            }
        }
        return list;
    }

    public List<PromotionalAsset> searchAssets(String query, String movieId, AssetType type, ApprovalStatus status) {
        List<PromotionalAsset> results = new ArrayList<>();
        String q = (query != null) ? query.trim().toLowerCase() : "";
        String mId = (movieId != null) ? movieId.trim().toLowerCase() : "";

        for (PromotionalAsset a : assets.values()) {
            boolean matchesQuery = q.isEmpty()
                    || a.getAssetId().toLowerCase().contains(q)
                    || a.getTitle().toLowerCase().contains(q)
                    || a.getPlatform().toLowerCase().contains(q)
                    || a.getLanguage().toLowerCase().contains(q);

            boolean matchesMovie = mId.isEmpty()
                    || a.getMovieId().toLowerCase().contains(mId);

            boolean matchesType = (type == null) || a.getAssetType() == type;

            boolean matchesStatus = (status == null) || a.getApprovalStatus() == status;

            if (matchesQuery && matchesMovie && matchesType && matchesStatus) {
                results.add(a);
            }
        }
        return results;
    }

    public void approveAsset(String assetId, String approvedBy, String notes) throws ResourceNotFoundException {
        PromotionalAsset asset = getAssetOrThrow(assetId);
        asset.approve(approvedBy, notes);
    }

    public void rejectAsset(String assetId, String rejectedBy, String reason) throws ResourceNotFoundException {
        PromotionalAsset asset = getAssetOrThrow(assetId);
        asset.reject(rejectedBy, reason);
    }

    public void submitRevision(String assetId, String newFilePath, String notes)
            throws ResourceNotFoundException, ValidationException {
        PromotionalAsset asset = getAssetOrThrow(assetId);
        asset.submitRevision(newFilePath, notes);
    }

    public int getPendingApprovalsCount() {
        int count = 0;
        for (PromotionalAsset a : assets.values()) {
            if (a.isPending()) count++;
        }
        return count;
    }

    public int getApprovedAssetsCount() {
        int count = 0;
        for (PromotionalAsset a : assets.values()) {
            if (a.isApproved()) count++;
        }
        return count;
    }

    public int getRejectedAssetsCount() {
        int count = 0;
        for (PromotionalAsset a : assets.values()) {
            if (a.isRejected()) count++;
        }
        return count;
    }

    public void clear() {
        assets.clear();
    }
}
