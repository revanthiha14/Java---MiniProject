package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Models a marketing asset (Poster, Teaser, Trailer, Banner, Social Media) for a movie.
 * Enforces approval status transitions (Pending, Approved, Rejected) and revision logging.
 */
public class PromotionalAsset {
    private String assetId;
    private String movieId;
    private AssetType assetType;
    private String title;
    private String filePathOrUrl;
    private String language;
    private String platform;
    private ApprovalStatus approvalStatus;
    private int revisionNumber;
    private String approvalDetails;
    private String lastUpdatedDate;

    public PromotionalAsset(String assetId, String movieId, AssetType assetType, String title,
                            String filePathOrUrl, String language, String platform) throws ValidationException {
        this(assetId, movieId, assetType, title, filePathOrUrl, language, platform,
                ApprovalStatus.PENDING, 1, "Awaiting creative director review", "2026-10-12");
    }

    public PromotionalAsset(String assetId, String movieId, AssetType assetType, String title,
                            String filePathOrUrl, String language, String platform,
                            ApprovalStatus approvalStatus, int revisionNumber,
                            String approvalDetails, String lastUpdatedDate) throws ValidationException {
        if (assetId == null || assetId.trim().isEmpty()) {
            throw new ValidationException("Asset ID", "Asset ID cannot be empty.");
        }
        if (movieId == null || movieId.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID cannot be empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Asset Title", "Asset title cannot be empty.");
        }
        if (filePathOrUrl == null || filePathOrUrl.trim().isEmpty()) {
            throw new ValidationException("File Path / URL", "File path or URL cannot be empty.");
        }
        if (revisionNumber <= 0) {
            throw new ValidationException("Revision Number", "Revision number must be positive.");
        }

        this.assetId = assetId.trim();
        this.movieId = movieId.trim();
        this.assetType = (assetType != null) ? assetType : AssetType.POSTER;
        this.title = title.trim();
        this.filePathOrUrl = filePathOrUrl.trim();
        this.language = (language != null && !language.trim().isEmpty()) ? language.trim() : "Original";
        this.platform = (platform != null && !platform.trim().isEmpty()) ? platform.trim() : "All Platforms";
        this.approvalStatus = (approvalStatus != null) ? approvalStatus : ApprovalStatus.PENDING;
        this.revisionNumber = revisionNumber;
        this.approvalDetails = (approvalDetails != null) ? approvalDetails.trim() : "";
        this.lastUpdatedDate = (lastUpdatedDate != null && !lastUpdatedDate.trim().isEmpty()) ? lastUpdatedDate.trim() : "2026-10-12";
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) throws ValidationException {
        if (assetId == null || assetId.trim().isEmpty()) {
            throw new ValidationException("Asset ID", "Asset ID cannot be empty.");
        }
        this.assetId = assetId.trim();
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) throws ValidationException {
        if (movieId == null || movieId.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID cannot be empty.");
        }
        this.movieId = movieId.trim();
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = (assetType != null) ? assetType : AssetType.POSTER;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Asset Title", "Asset title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getFilePathOrUrl() {
        return filePathOrUrl;
    }

    public void setFilePathOrUrl(String filePathOrUrl) throws ValidationException {
        if (filePathOrUrl == null || filePathOrUrl.trim().isEmpty()) {
            throw new ValidationException("File Path / URL", "File path or URL cannot be empty.");
        }
        this.filePathOrUrl = filePathOrUrl.trim();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = (language != null && !language.trim().isEmpty()) ? language.trim() : "Original";
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = (platform != null && !platform.trim().isEmpty()) ? platform.trim() : "All Platforms";
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = (approvalStatus != null) ? approvalStatus : ApprovalStatus.PENDING;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) throws ValidationException {
        if (revisionNumber <= 0) {
            throw new ValidationException("Revision Number", "Revision number must be positive.");
        }
        this.revisionNumber = revisionNumber;
    }

    public String getApprovalDetails() {
        return approvalDetails;
    }

    public void setApprovalDetails(String approvalDetails) {
        this.approvalDetails = (approvalDetails != null) ? approvalDetails.trim() : "";
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = (lastUpdatedDate != null && !lastUpdatedDate.trim().isEmpty()) ? lastUpdatedDate.trim() : "2026-10-12";
    }

    /**
     * Approves this asset for public marketing distribution.
     */
    public void approve(String approvedBy, String notes) {
        this.approvalStatus = ApprovalStatus.APPROVED;
        String sign = (approvedBy != null && !approvedBy.trim().isEmpty()) ? approvedBy.trim() : "Marketing Lead";
        String n = (notes != null && !notes.trim().isEmpty()) ? " - " + notes.trim() : "";
        this.approvalDetails = "APPROVED by " + sign + n;
    }

    /**
     * Rejects this asset and requests revisions.
     */
    public void reject(String rejectedBy, String reason) {
        this.approvalStatus = ApprovalStatus.REJECTED;
        String sign = (rejectedBy != null && !rejectedBy.trim().isEmpty()) ? rejectedBy.trim() : "Creative Director";
        String r = (reason != null && !reason.trim().isEmpty()) ? reason.trim() : "Modifications needed";
        this.approvalDetails = "REJECTED by " + sign + " (Reason: " + r + ")";
    }

    /**
     * Submits a new revision with an updated file path and resets status to PENDING.
     */
    public void submitRevision(String newFilePathOrUrl, String changeNotes) throws ValidationException {
        if (newFilePathOrUrl != null && !newFilePathOrUrl.trim().isEmpty()) {
            this.filePathOrUrl = newFilePathOrUrl.trim();
        }
        this.revisionNumber++;
        this.approvalStatus = ApprovalStatus.PENDING;
        String notes = (changeNotes != null && !changeNotes.trim().isEmpty()) ? changeNotes.trim() : "Revision v" + revisionNumber + " uploaded";
        this.approvalDetails = "Revision v" + revisionNumber + ": " + notes;
    }

    public boolean isPending() {
        return approvalStatus == ApprovalStatus.PENDING;
    }

    public boolean isApproved() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }

    public boolean isRejected() {
        return approvalStatus == ApprovalStatus.REJECTED;
    }

    public String getStatusBadge() {
        return switch (approvalStatus) {
            case APPROVED -> "[ APPROVED ]";
            case REJECTED -> "[ REJECTED ]";
            default -> "[ PENDING  ]";
        };
    }

    @Override
    public String toString() {
        return String.format("[%s] %-24s | Type: %-12s | Lang: %-10s | Plat: %-16s | Rev: v%-2d %s",
                assetId, title, assetType.name(), language, platform, revisionNumber, getStatusBadge());
    }
}
