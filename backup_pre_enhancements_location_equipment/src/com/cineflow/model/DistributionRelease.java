package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents a Theatrical & Digital (OTT) Distribution and Release deal for a movie.
 * Captures: Release territory/region, theatrical distributor, theatrical release date,
 * cinema exhibition status, screen count, OTT streaming platform (optional), and digital release date.
 */
public class DistributionRelease {
    private String releaseId;
    private String region;
    private String distributor;
    private String theatricalReleaseDate;
    private String theatricalStatus; // SCHEDULED, ADVANCE_BOOKING_OPEN, RUNNING_IN_THEATERS, CLOSED_RUN, POSTPONED
    private int screenCount;
    private String ottPlatform; // Optional: e.g. Netflix, Prime Video, Disney+ Hotstar, TBD
    private String digitalReleaseDate; // Optional / TBD
    private String notes;

    public DistributionRelease(String releaseId, String region, String distributor,
                               String theatricalReleaseDate, String theatricalStatus,
                               int screenCount, String ottPlatform, String digitalReleaseDate,
                               String notes) throws ValidationException {
        if (releaseId == null || releaseId.trim().isEmpty()) {
            throw new ValidationException("Release ID", "Release ID cannot be blank.");
        }
        this.releaseId = releaseId.trim().toUpperCase();
        this.region = (region != null && !region.trim().isEmpty()) ? region.trim() : "Worldwide";
        this.distributor = (distributor != null && !distributor.trim().isEmpty()) ? distributor.trim() : "Studio Direct";
        this.theatricalReleaseDate = (theatricalReleaseDate != null && !theatricalReleaseDate.trim().isEmpty())
                ? theatricalReleaseDate.trim() : "TBD";
        this.theatricalStatus = (theatricalStatus != null && !theatricalStatus.trim().isEmpty())
                ? theatricalStatus.trim().toUpperCase() : "SCHEDULED";
        this.screenCount = Math.max(screenCount, 0);
        this.ottPlatform = (ottPlatform != null && !ottPlatform.trim().isEmpty()) ? ottPlatform.trim() : "TBD / Pending Deal";
        this.digitalReleaseDate = (digitalReleaseDate != null && !digitalReleaseDate.trim().isEmpty())
                ? digitalReleaseDate.trim() : "TBD";
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public DistributionRelease(String releaseId, String region, String distributor,
                               String theatricalReleaseDate) throws ValidationException {
        this(releaseId, region, distributor, theatricalReleaseDate, "SCHEDULED", 0, "TBD / Pending Deal", "TBD", "");
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) throws ValidationException {
        if (releaseId == null || releaseId.trim().isEmpty()) {
            throw new ValidationException("Release ID", "Release ID cannot be blank.");
        }
        this.releaseId = releaseId.trim().toUpperCase();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = (region != null && !region.trim().isEmpty()) ? region.trim() : "Worldwide";
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = (distributor != null && !distributor.trim().isEmpty()) ? distributor.trim() : "Studio Direct";
    }

    public String getTheatricalReleaseDate() {
        return theatricalReleaseDate;
    }

    public void setTheatricalReleaseDate(String theatricalReleaseDate) {
        this.theatricalReleaseDate = (theatricalReleaseDate != null && !theatricalReleaseDate.trim().isEmpty())
                ? theatricalReleaseDate.trim() : "TBD";
    }

    public String getTheatricalStatus() {
        return theatricalStatus;
    }

    public void setTheatricalStatus(String theatricalStatus) {
        if (theatricalStatus != null && !theatricalStatus.trim().isEmpty()) {
            this.theatricalStatus = theatricalStatus.trim().toUpperCase();
        }
    }

    public int getScreenCount() {
        return screenCount;
    }

    public void setScreenCount(int screenCount) {
        this.screenCount = Math.max(screenCount, 0);
    }

    public String getOttPlatform() {
        return ottPlatform;
    }

    public void setOttPlatform(String ottPlatform) {
        this.ottPlatform = (ottPlatform != null && !ottPlatform.trim().isEmpty())
                ? ottPlatform.trim() : "TBD / Pending Deal";
    }

    public String getDigitalReleaseDate() {
        return digitalReleaseDate;
    }

    public void setDigitalReleaseDate(String digitalReleaseDate) {
        this.digitalReleaseDate = (digitalReleaseDate != null && !digitalReleaseDate.trim().isEmpty())
                ? digitalReleaseDate.trim() : "TBD";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public boolean hasOttPlatform() {
        return ottPlatform != null
                && !ottPlatform.equalsIgnoreCase("TBD / Pending Deal")
                && !ottPlatform.equalsIgnoreCase("None")
                && !ottPlatform.equalsIgnoreCase("TBD");
    }

    public boolean hasDigitalReleaseDate() {
        return digitalReleaseDate != null
                && !digitalReleaseDate.equalsIgnoreCase("TBD")
                && !digitalReleaseDate.trim().isEmpty();
    }

    @Override
    public String toString() {
        String screensStr = (screenCount > 0) ? String.format("%d Screens", screenCount) : "Screens TBD";
        String ottStr = hasOttPlatform() ? ottPlatform : "No OTT Attached";
        return String.format("[%s] Region: %-22s | Dist: %-20s | Theatrical: %-10s (%-14s) | %-12s | OTT: %s (Digi: %s)",
                releaseId, region, distributor, theatricalReleaseDate, theatricalStatus, screensStr, ottStr, digitalReleaseDate);
    }
}
