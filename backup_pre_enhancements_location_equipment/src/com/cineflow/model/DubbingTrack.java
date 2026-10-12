package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents a localized voice dubbing track for a character in a specific language.
 * Tracks voice artist assignment, recording progress, retake counters, and quality approvals.
 * Demonstrates: Encapsulation, Validation, Domain Modeling, and State Transitions.
 */
public class DubbingTrack {
    private String trackId;
    private String characterName;
    private String voiceArtist;
    private String language;
    private String status; // PLANNED, IN_PROGRESS, RECORDED, APPROVED, RETAKES_NEEDED
    private int retakeCount;
    private String retakeReason;
    private String studioLocation;

    public DubbingTrack(String trackId, String characterName, String voiceArtist, String language)
            throws ValidationException {
        this(trackId, characterName, voiceArtist, language, "PLANNED", 0, "", "Main Studio");
    }

    public DubbingTrack(String trackId, String characterName, String voiceArtist, String language,
                        String status, int retakeCount, String retakeReason, String studioLocation)
            throws ValidationException {
        if (trackId == null || trackId.trim().isEmpty()) {
            throw new ValidationException("Track ID", "Dubbing Track ID cannot be empty.");
        }
        if (characterName == null || characterName.trim().isEmpty()) {
            throw new ValidationException("Character Name", "Character name cannot be empty.");
        }
        if (voiceArtist == null || voiceArtist.trim().isEmpty()) {
            throw new ValidationException("Voice Artist", "Voice artist name cannot be empty.");
        }
        if (language == null || language.trim().isEmpty()) {
            throw new ValidationException("Language", "Dubbing language cannot be empty.");
        }

        this.trackId = trackId.trim().toUpperCase();
        this.characterName = characterName.trim();
        this.voiceArtist = voiceArtist.trim();
        this.language = language.trim();
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "PLANNED";
        this.retakeCount = Math.max(0, retakeCount);
        this.retakeReason = (retakeReason != null) ? retakeReason.trim() : "";
        this.studioLocation = (studioLocation != null && !studioLocation.trim().isEmpty())
                ? studioLocation.trim() : "Main Studio";
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) throws ValidationException {
        if (trackId == null || trackId.trim().isEmpty()) {
            throw new ValidationException("Track ID", "Dubbing Track ID cannot be empty.");
        }
        this.trackId = trackId.trim().toUpperCase();
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) throws ValidationException {
        if (characterName == null || characterName.trim().isEmpty()) {
            throw new ValidationException("Character Name", "Character name cannot be empty.");
        }
        this.characterName = characterName.trim();
    }

    public String getVoiceArtist() {
        return voiceArtist;
    }

    public void setVoiceArtist(String voiceArtist) throws ValidationException {
        if (voiceArtist == null || voiceArtist.trim().isEmpty()) {
            throw new ValidationException("Voice Artist", "Voice artist cannot be empty.");
        }
        this.voiceArtist = voiceArtist.trim();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) throws ValidationException {
        if (language == null || language.trim().isEmpty()) {
            throw new ValidationException("Language", "Language cannot be empty.");
        }
        this.language = language.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "PLANNED";
    }

    public int getRetakeCount() {
        return retakeCount;
    }

    public void setRetakeCount(int retakeCount) {
        this.retakeCount = Math.max(0, retakeCount);
    }

    public String getRetakeReason() {
        return retakeReason;
    }

    public void setRetakeReason(String retakeReason) {
        this.retakeReason = (retakeReason != null) ? retakeReason.trim() : "";
    }

    public String getStudioLocation() {
        return studioLocation;
    }

    public void setStudioLocation(String studioLocation) {
        this.studioLocation = (studioLocation != null && !studioLocation.trim().isEmpty())
                ? studioLocation.trim() : "Main Studio";
    }

    /**
     * Registers a retake for this track, incrementing the counter and updating the required reason.
     */
    public void logRetake(String reason) {
        this.retakeCount++;
        this.status = "RETAKES_NEEDED";
        if (reason != null && !reason.trim().isEmpty()) {
            if (this.retakeReason.isEmpty()) {
                this.retakeReason = reason.trim();
            } else {
                this.retakeReason = this.retakeReason + " | Retake #" + retakeCount + ": " + reason.trim();
            }
        }
    }

    /**
     * Approves this dubbing track after sound supervisor QC inspection.
     */
    public void approveTrack() {
        this.status = "APPROVED";
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(status);
    }

    public String getStatusBadge() {
        return switch (status.toUpperCase()) {
            case "APPROVED" -> "[APPROVED]      ";
            case "RECORDED" -> "[RECORDED]      ";
            case "IN_PROGRESS" -> "[IN PROGRESS]   ";
            case "RETAKES_NEEDED" -> "[RETAKES NEEDED]";
            default -> "[PLANNED]       ";
        };
    }

    @Override
    public String toString() {
        String retakeInfo = (retakeCount > 0)
                ? String.format(" (Retakes: %d | Reason: %s)", retakeCount, retakeReason)
                : "";
        return String.format("[%s] %-16s | %-12s | Char: %-18s | Artist: %-18s | Studio: %-18s%s",
                trackId, getStatusBadge().trim(), language, characterName, voiceArtist, studioLocation, retakeInfo);
    }
}
