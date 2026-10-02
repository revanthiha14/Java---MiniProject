package com.cineflow.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a discrete script scene to be filmed.
 * Demonstrates:
 *  - Comparable<Scene> for priority queue ordering (daylight, weather, urgency)
 *  - Multiple interface implementation (Identifiable, Schedulable, Reportable)
 *  - Collections Framework (Set for actors and equipment)
 *  - Arrays for scene production notes
 *  - Method overloading
 */
public class Scene implements Identifiable<String>, Comparable<Scene>, Schedulable, Reportable {
    private static final long serialVersionUID = 1L;

    private String id;
    private int sceneNumber;
    private String title;
    private String synopsis;
    private double scriptPages;
    private DaylightRequirement daylightRequirement;
    private SceneStatus status;
    private int priority; // 1 (Critical/Urgent) to 5 (Flexible/Standard)
    private LocalDate scheduledDate;
    private String shootTimeSlot;
    private String locationId;
    private Set<String> requiredActorIds;
    private Set<String> requiredEquipmentIds;
    private double estimatedShootHours;
    private String[] productionChecklist; // Demonstrates array usage
    private String movieId; // Associated movie project identifier

    public Scene() {
        this("SCN-000", "MOV-01", 0, "Untitled Scene", "No synopsis", 1.0,
                DaylightRequirement.INTERIOR_STUDIO, 3, 4.0);
    }

    public Scene(String id, int sceneNumber, String title, String synopsis, double scriptPages,
                 DaylightRequirement daylightRequirement, int priority, double estimatedShootHours) {
        this(id, "MOV-01", sceneNumber, title, synopsis, scriptPages, daylightRequirement, priority, estimatedShootHours);
    }

    public Scene(String id, String movieId, int sceneNumber, String title, String synopsis, double scriptPages,
                 DaylightRequirement daylightRequirement, int priority, double estimatedShootHours) {
        this.id = id;
        this.movieId = movieId != null ? movieId : "MOV-01";
        this.sceneNumber = sceneNumber;
        this.title = title;
        this.synopsis = synopsis;
        this.scriptPages = scriptPages;
        this.daylightRequirement = daylightRequirement;
        this.status = SceneStatus.DRAFT;
        this.priority = priority;
        this.estimatedShootHours = estimatedShootHours;
        this.requiredActorIds = new HashSet<>();
        this.requiredEquipmentIds = new HashSet<>();
        this.productionChecklist = new String[]{"Script breakdown approved", "Shot list finalized"};
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    // Method Overloading for Actor Assignment
    public void assignActor(String actorId) {
        if (actorId != null && !actorId.trim().isEmpty()) {
            this.requiredActorIds.add(actorId.trim());
        }
    }

    public void assignActors(String... actorIds) {
        if (actorIds != null) {
            for (String actorId : actorIds) {
                assignActor(actorId);
            }
        }
    }

    public void assignEquipment(String equipmentId) {
        if (equipmentId != null && !equipmentId.trim().isEmpty()) {
            this.requiredEquipmentIds.add(equipmentId.trim());
        }
    }

    public void addChecklistItem(String item) {
        if (item == null || item.trim().isEmpty()) return;
        String[] expanded = Arrays.copyOf(this.productionChecklist, this.productionChecklist.length + 1);
        expanded[this.productionChecklist.length] = item.trim();
        this.productionChecklist = expanded;
    }

    @Override
    public boolean hasScheduleConflict(LocalDate date, String timeSlot) {
        if (this.scheduledDate == null || date == null) return false;
        if (!this.scheduledDate.equals(date)) return false;
        if (this.shootTimeSlot == null || timeSlot == null) return false;
        return this.shootTimeSlot.equalsIgnoreCase(timeSlot);
    }

    /**
     * Orders scenes for PriorityQueue based on shooting urgency.
     * Criteria:
     * 1. Priority level (1 is most urgent, 5 is least)
     * 2. Weather/Sun dependency (Natural light before interior)
     * 3. Scene number
     */
    @Override
    public int compareTo(Scene other) {
        if (other == null) return -1;
        // Priority 1 comes before Priority 5
        int priorityComparison = Integer.compare(this.priority, other.priority);
        if (priorityComparison != 0) return priorityComparison;

        // Weather/Daylight dependent scenes have higher precedence
        boolean thisWeather = this.daylightRequirement.isWeatherDependent();
        boolean otherWeather = other.daylightRequirement.isWeatherDependent();
        if (thisWeather && !otherWeather) return -1;
        if (!thisWeather && otherWeather) return 1;

        // Tie-breaker: Scene script order
        return Integer.compare(this.sceneNumber, other.sceneNumber);
    }

    @Override
    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== SCENE #%02d: %s (ID: %s) ===\n", sceneNumber, title.toUpperCase(), id));
        sb.append(String.format("Status         : %s | Priority: Level %d\n", status.getLabel(), priority));
        sb.append(String.format("Lighting/Type  : %s\n", daylightRequirement.getDescription()));
        sb.append(String.format("Script Length  : %.1f Pages | Est. Shoot Time: %.1f Hours\n", scriptPages, estimatedShootHours));
        sb.append(String.format("Scheduled Date : %s | Slot: %s\n",
                scheduledDate != null ? scheduledDate.toString() : "TBD (Not Scheduled)",
                shootTimeSlot != null ? shootTimeSlot : "TBD"));
        sb.append(String.format("Location Asset : %s\n", locationId != null ? locationId : "Unassigned"));
        sb.append("Synopsis       : ").append(synopsis).append("\n");
        sb.append("Cast Required  : ").append(requiredActorIds.isEmpty() ? "None" : String.join(", ", requiredActorIds)).append("\n");
        sb.append("Equipment List : ").append(requiredEquipmentIds.isEmpty() ? "None" : String.join(", ", requiredEquipmentIds)).append("\n");
        sb.append("Prep Checklist : ").append(productionChecklist.length == 0 ? "None" : String.join("; ", productionChecklist)).append("\n");
        return sb.toString();
    }

    // Getters and Setters
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSceneNumber() {
        return sceneNumber;
    }

    public void setSceneNumber(int sceneNumber) {
        this.sceneNumber = sceneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getScriptPages() {
        return scriptPages;
    }

    public void setScriptPages(double scriptPages) {
        this.scriptPages = scriptPages;
    }

    public DaylightRequirement getDaylightRequirement() {
        return daylightRequirement;
    }

    public void setDaylightRequirement(DaylightRequirement daylightRequirement) {
        this.daylightRequirement = daylightRequirement;
    }

    public SceneStatus getStatus() {
        return status;
    }

    public void setStatus(SceneStatus status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getShootTimeSlot() {
        return shootTimeSlot;
    }

    public void setShootTimeSlot(String shootTimeSlot) {
        this.shootTimeSlot = shootTimeSlot;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Set<String> getRequiredActorIds() {
        return Collections.unmodifiableSet(requiredActorIds);
    }

    public Set<String> getRequiredEquipmentIds() {
        return Collections.unmodifiableSet(requiredEquipmentIds);
    }

    public double getEstimatedShootHours() {
        return estimatedShootHours;
    }

    public void setEstimatedShootHours(double estimatedShootHours) {
        this.estimatedShootHours = estimatedShootHours;
    }

    public String[] getProductionChecklist() {
        return Arrays.copyOf(productionChecklist, productionChecklist.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scene scene)) return false;
        return Objects.equals(id, scene.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Scene #%02d: \"%s\" [%s | Priority: %d | %s]",
                sceneNumber, title, status, priority, daylightRequirement);
    }
}
