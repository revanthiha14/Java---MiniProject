package com.cineflow.model;

import com.cineflow.service.BudgetService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Represents a complete movie production project under a production house.
 * Demonstrates:
 *  - Multiple interface implementation (Identifiable, Reportable, CostTrackable)
 *  - Collections Framework: List (scenes, call sheets), Set (cast, crew, assets), Queue (shooting priority queue)
 *  - Composition: Movie has a dedicated BudgetService, scenes, and personnel references
 *  - Constructor chaining with this()
 *  - Method overloading
 */
public class Movie implements Identifiable<String>, Reportable, CostTrackable, Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String studioId; // Associated production house ID
    private String title;
    private String genre;
    private String directorName;
    private ProductionPhase productionPhase;
    private int estimatedReleaseYear;
    private BudgetService budgetManager;
    private List<Scene> scenes;
    private Queue<Scene> shootingQueue;
    private List<CallSheet> callSheets;
    private Set<String> assignedActorIds;
    private Set<String> assignedCrewIds;
    private Set<String> bookedEquipmentIds;
    private Set<String> bookedLocationIds;
    private List<Review> reviews;

    /**
     * Default constructor with safe defaults.
     */
    public Movie() {
        this("MOV-000", "PH-101", "Untitled Project", "Drama", "Unassigned", ProductionPhase.PRE_PRODUCTION, 2027);
    }

    /**
     * Minimal constructor chaining to full constructor.
     */
    public Movie(String id, String title, String genre, String directorName) {
        this(id, "PH-101", title, genre, directorName, ProductionPhase.PRE_PRODUCTION, 2026);
    }

    public Movie(String id, String title, String genre, String directorName,
                 ProductionPhase productionPhase, int estimatedReleaseYear) {
        this(id, "PH-101", title, genre, directorName, productionPhase, estimatedReleaseYear);
    }

    /**
     * Full parameterized constructor initializing studioId, collections and budget manager.
     */
    public Movie(String id, String studioId, String title, String genre, String directorName,
                 ProductionPhase productionPhase, int estimatedReleaseYear) {
        this.id = id;
        this.studioId = studioId != null ? studioId : "PH-101";
        this.title = title;
        this.genre = genre;
        this.directorName = directorName;
        this.productionPhase = productionPhase;
        this.estimatedReleaseYear = estimatedReleaseYear;
        this.budgetManager = new BudgetService();
        this.scenes = new ArrayList<>();
        this.shootingQueue = new PriorityQueue<>();
        this.callSheets = new ArrayList<>();
        this.assignedActorIds = new HashSet<>();
        this.assignedCrewIds = new HashSet<>();
        this.bookedEquipmentIds = new HashSet<>();
        this.bookedLocationIds = new HashSet<>();
        this.reviews = new ArrayList<>();
    }

    // Method Overloading Demonstration: addScene variants
    public void addScene(Scene scene) {
        if (scene != null) {
            scene.setMovieId(this.id);
            this.scenes.add(scene);
            refreshShootingQueue();
        }
    }

    public void addScenes(Scene... sceneArray) {
        if (sceneArray != null) {
            for (Scene s : sceneArray) {
                addScene(s);
            }
        }
    }

    public void addScenes(List<Scene> sceneList) {
        if (sceneList != null) {
            for (Scene s : sceneList) {
                addScene(s);
            }
        }
    }

    public synchronized void refreshShootingQueue() {
        shootingQueue.clear();
        for (Scene s : scenes) {
            if (s.getStatus() == SceneStatus.DRAFT || s.getStatus() == SceneStatus.SCHEDULED) {
                shootingQueue.offer(s);
            }
        }
    }

    // Method Overloading Demonstration: assignActor variants
    public void assignActor(String actorId) {
        if (actorId != null && !actorId.trim().isEmpty()) {
            this.assignedActorIds.add(actorId.trim());
        }
    }

    public void assignActors(String... actorIds) {
        if (actorIds != null) {
            for (String aId : actorIds) {
                assignActor(aId);
            }
        }
    }

    public void assignCrew(String crewId) {
        if (crewId != null && !crewId.trim().isEmpty()) {
            this.assignedCrewIds.add(crewId.trim());
        }
    }

    public void assignCrew(String... crewIds) {
        if (crewIds != null) {
            for (String cId : crewIds) {
                assignCrew(cId);
            }
        }
    }

    public void bookEquipment(String equipmentId) {
        if (equipmentId != null && !equipmentId.trim().isEmpty()) {
            this.bookedEquipmentIds.add(equipmentId.trim());
        }
    }

    public void bookEquipment(String... equipmentIds) {
        if (equipmentIds != null) {
            for (String eqId : equipmentIds) {
                bookEquipment(eqId);
            }
        }
    }

    public void bookLocation(String locationId) {
        if (locationId != null && !locationId.trim().isEmpty()) {
            this.bookedLocationIds.add(locationId.trim());
        }
    }

    public void bookLocations(String... locationIds) {
        if (locationIds != null) {
            for (String locId : locationIds) {
                bookLocation(locId);
            }
        }
    }

    public void addCallSheet(CallSheet sheet) {
        if (sheet != null) {
            this.callSheets.add(sheet);
        }
    }

    public long getCompletedScenesCount() {
        return scenes.stream().filter(s -> s.getStatus() == SceneStatus.COMPLETED).count();
    }

    public double getProductionProgressPercentage() {
        if (scenes.isEmpty()) return 0.0;
        return ((double) getCompletedScenesCount() / scenes.size()) * 100.0;
    }

    @Override
    public double computeTotalCost() {
        return budgetManager.getTotalSpentBudget();
    }

    @Override
    public String getCostBreakdown() {
        return String.format("Allocated: $%,.2f | Expended: $%,.2f | Remaining: $%,.2f",
                budgetManager.getTotalAllocatedBudget(),
                budgetManager.getTotalSpentBudget(),
                budgetManager.getRemainingBudget());
    }

    @Override
    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append(String.format("                     MOVIE PROFILE: %s\n", title.toUpperCase()));
        sb.append("========================================================================\n");
        sb.append(String.format("Movie ID       : %s\n", id));
        sb.append(String.format("Genre          : %s | Estimated Release: %d\n", genre, estimatedReleaseYear));
        sb.append(String.format("Director       : %s\n", directorName));
        sb.append(String.format("Production Stage: %s\n", productionPhase.getDescription()));
        sb.append(String.format("Progress       : %d / %d Scenes Filmed (%.1f%%)\n",
                getCompletedScenesCount(), scenes.size(), getProductionProgressPercentage()));
        sb.append(String.format("Budget Status  : %s\n", getCostBreakdown()));
        sb.append(String.format("Audience Rating: %s\n", getRatingSummary()));
        sb.append(String.format("Roster Summary : %d Actors Assigned | %d Crew Members Assigned\n",
                assignedActorIds.size(), assignedCrewIds.size()));
        sb.append(String.format("Assets Booked  : %d Equipment Items | %d Locations\n",
                bookedEquipmentIds.size(), bookedLocationIds.size()));
        sb.append("========================================================================\n");
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudioId() {
        return studioId != null ? studioId : "PH-101";
    }

    public void setStudioId(String studioId) {
        this.studioId = studioId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public ProductionPhase getProductionPhase() {
        return productionPhase;
    }

    public void setProductionPhase(ProductionPhase productionPhase) {
        this.productionPhase = productionPhase;
    }

    public ProductionPhase getPhase() {
        return productionPhase;
    }

    public void setPhase(ProductionPhase phase) {
        this.productionPhase = phase;
    }

    public int getEstimatedReleaseYear() {
        return estimatedReleaseYear;
    }

    public void setEstimatedReleaseYear(int estimatedReleaseYear) {
        this.estimatedReleaseYear = estimatedReleaseYear;
    }

    public int getReleaseYear() {
        return estimatedReleaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.estimatedReleaseYear = releaseYear;
    }

    public BudgetService getBudgetManager() {
        return budgetManager;
    }

    public void setBudgetManager(BudgetService budgetManager) {
        this.budgetManager = budgetManager;
    }

    public BudgetService getBudgetService() {
        return budgetManager;
    }

    public void setBudgetService(BudgetService budgetService) {
        this.budgetManager = budgetService;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public Queue<Scene> getShootingQueue() {
        return shootingQueue;
    }

    public List<CallSheet> getCallSheets() {
        return callSheets;
    }

    public Set<String> getAssignedActorIds() {
        return Collections.unmodifiableSet(assignedActorIds);
    }

    public Set<String> getActorIds() {
        return Collections.unmodifiableSet(assignedActorIds);
    }

    public Set<String> getAssignedCrewIds() {
        return Collections.unmodifiableSet(assignedCrewIds);
    }

    public Set<String> getCrewIds() {
        return Collections.unmodifiableSet(assignedCrewIds);
    }

    public Set<String> getBookedEquipmentIds() {
        return Collections.unmodifiableSet(bookedEquipmentIds);
    }

    public Set<String> getEquipmentIds() {
        return Collections.unmodifiableSet(bookedEquipmentIds);
    }

    public Set<String> getBookedLocationIds() {
        return Collections.unmodifiableSet(bookedLocationIds);
    }

    public Set<String> getLocationIds() {
        return Collections.unmodifiableSet(bookedLocationIds);
    }

    // Method Overloading Demonstration: addReview variants
    public void addReview(Review review) {
        if (review != null) {
            if (review.getMovieId() == null || review.getMovieId().trim().isEmpty()) {
                review.setMovieId(this.id);
            }
            this.reviews.add(review);
        }
    }

    public void addReview(String reviewerName, int rating, String comment) {
        addReview(new Review(this.id, reviewerName, rating, comment));
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public int getReviewCount() {
        return reviews.size();
    }

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public String getRatingSummary() {
        if (reviews == null || reviews.isEmpty()) {
            return "No reviews yet";
        }
        double avg = getAverageRating();
        int roundedStars = (int) Math.round(avg);
        String stars = "★".repeat(Math.max(1, Math.min(5, roundedStars))) + "☆".repeat(Math.max(0, 5 - roundedStars));
        return String.format("%s %.1f / 5.0 (%d %s)",
                stars, avg, reviews.size(), reviews.size() == 1 ? "review" : "reviews");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, %d) - Stage: %s | Rating: %s",
                id, title, genre, estimatedReleaseYear, productionPhase.name(), getRatingSummary());
    }
}
