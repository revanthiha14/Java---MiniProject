package com.cineflow.model;

/**
 * Represents an individual cast / actor call time entry on a daily production call sheet.
 * Captures industry-standard call sheet checkpoints: Pickup, Hair/Makeup, On-Set call, and special notes.
 */
public class CastCallEntry {
    private String actorId;
    private String actorName;
    private String characterName;
    private String pickupTime;
    private String makeupCall;
    private String onSetCall;
    private String instructions;

    public CastCallEntry(String actorId, String actorName, String characterName,
                         String pickupTime, String makeupCall, String onSetCall, String instructions) {
        this.actorId = (actorId != null) ? actorId.trim() : "ACT-00";
        this.actorName = (actorName != null && !actorName.trim().isEmpty()) ? actorName.trim() : "TBD Actor";
        this.characterName = (characterName != null && !characterName.trim().isEmpty()) ? characterName.trim() : "Supporting";
        this.pickupTime = (pickupTime != null && !pickupTime.trim().isEmpty()) ? pickupTime.trim() : "On Own";
        this.makeupCall = (makeupCall != null && !makeupCall.trim().isEmpty()) ? makeupCall.trim() : "TBD";
        this.onSetCall = (onSetCall != null && !onSetCall.trim().isEmpty()) ? onSetCall.trim() : "08:00 AM";
        this.instructions = (instructions != null) ? instructions.trim() : "";
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = (actorId != null) ? actorId.trim() : "ACT-00";
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = (actorName != null && !actorName.trim().isEmpty()) ? actorName.trim() : "TBD Actor";
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = (characterName != null && !characterName.trim().isEmpty()) ? characterName.trim() : "Supporting";
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = (pickupTime != null && !pickupTime.trim().isEmpty()) ? pickupTime.trim() : "On Own";
    }

    public String getMakeupCall() {
        return makeupCall;
    }

    public void setMakeupCall(String makeupCall) {
        this.makeupCall = (makeupCall != null && !makeupCall.trim().isEmpty()) ? makeupCall.trim() : "TBD";
    }

    public String getOnSetCall() {
        return onSetCall;
    }

    public void setOnSetCall(String onSetCall) {
        this.onSetCall = (onSetCall != null && !onSetCall.trim().isEmpty()) ? onSetCall.trim() : "08:00 AM";
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = (instructions != null) ? instructions.trim() : "";
    }

    /**
     * Serializes this entry into a safe format for flat-file storage:
     * actorId#actorName#characterName#pickupTime#makeupCall#onSetCall#instructions
     */
    public String toSerializedString() {
        String safeActor = actorName.replace("#", " ").replace("~", " ");
        String safeChar = characterName.replace("#", " ").replace("~", " ");
        String safePick = pickupTime.replace("#", " ").replace("~", " ");
        String safeMu = makeupCall.replace("#", " ").replace("~", " ");
        String safeSet = onSetCall.replace("#", " ").replace("~", " ");
        String safeInst = instructions.replace("#", " ").replace("~", " ");
        return actorId + "#" + safeActor + "#" + safeChar + "#" + safePick + "#" + safeMu + "#" + safeSet + "#" + safeInst;
    }

    public static CastCallEntry fromSerializedString(String data) {
        if (data == null || data.trim().isEmpty()) return null;
        String[] parts = data.split("#", -1);
        if (parts.length >= 7) {
            return new CastCallEntry(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
        } else if (parts.length >= 3) {
            return new CastCallEntry(parts[0], parts[1], parts[2], "On Own", "TBD", "08:00 AM", "");
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-18s | %-16s | Pickup: %-8s | H/MU: %-8s | On-Set: %-8s | %s",
                actorId, actorName, characterName, pickupTime, makeupCall, onSetCall, instructions);
    }
}
