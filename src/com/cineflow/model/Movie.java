package com.cineflow.model;

import com.cineflow.exception.BudgetExceededException;
import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ScheduleConflictException;
import com.cineflow.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Movie project.
 * Demonstrates: Composition, Polymorphic Collections (List of Person),
 * Domain Business Rule Validation, and Exception Handling.
 */
public class Movie {
    private String id;
    private String title;
    private String genre;
    private String director;
    private int releaseYear;
    private double budget;
    private MovieStatus status;

    private List<Scene> scenes;
    private List<Person> castAndCrew; // Polymorphic collection storing both Actor and CrewMember
    private List<Expense> expenses;
    private List<Review> reviews;

    public Movie(String id, String title, String genre, String director, int releaseYear, double budget)
            throws ValidationException {
        this(id, title, genre, director, releaseYear, budget, MovieStatus.PLANNING);
    }

    public Movie(String id, String title, String genre, String director, int releaseYear, double budget, MovieStatus status)
            throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID cannot be empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Movie Title", "Movie title cannot be empty.");
        }
        if (genre == null || genre.trim().isEmpty()) {
            throw new ValidationException("Genre", "Movie genre cannot be empty.");
        }
        if (director == null || director.trim().isEmpty()) {
            throw new ValidationException("Director", "Director name cannot be empty.");
        }
        if (releaseYear < 1888 || releaseYear > 2100) {
            throw new ValidationException("Release Year", "Release year must be between 1888 and 2100.");
        }
        if (budget <= 0.0) {
            throw new ValidationException("Budget", "Allocated budget must be greater than zero.");
        }

        this.id = id.trim();
        this.title = title.trim();
        this.genre = genre.trim();
        this.director = director.trim();
        this.releaseYear = releaseYear;
        this.budget = budget;
        this.status = (status != null) ? status : MovieStatus.PLANNING;
        this.scenes = new ArrayList<>();
        this.castAndCrew = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Movie Title", "Movie title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) throws ValidationException {
        if (genre == null || genre.trim().isEmpty()) {
            throw new ValidationException("Genre", "Movie genre cannot be empty.");
        }
        this.genre = genre.trim();
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) throws ValidationException {
        if (director == null || director.trim().isEmpty()) {
            throw new ValidationException("Director", "Director name cannot be empty.");
        }
        this.director = director.trim();
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) throws ValidationException {
        if (releaseYear < 1888 || releaseYear > 2100) {
            throw new ValidationException("Release Year", "Release year must be between 1888 and 2100.");
        }
        this.releaseYear = releaseYear;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) throws ValidationException {
        if (budget <= 0.0) {
            throw new ValidationException("Budget", "Budget must be greater than zero.");
        }
        this.budget = budget;
    }

    public MovieStatus getStatus() {
        return status;
    }

    public void setStatus(MovieStatus status) {
        this.status = (status != null) ? status : MovieStatus.PLANNING;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public List<Person> getCastAndCrew() {
        return castAndCrew;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Adds a scene to the movie after validating against duplicate scene numbers
     * and potential location/schedule conflicts.
     */
    public void addScene(Scene scene) throws ValidationException, ScheduleConflictException {
        if (scene == null) {
            throw new ValidationException("Scene", "Cannot add null scene.");
        }
        for (Scene existing : scenes) {
            if (existing.getSceneNumber() == scene.getSceneNumber()) {
                throw new ValidationException("Scene Number",
                        "Scene #" + scene.getSceneNumber() + " already exists in movie '" + title + "'.");
            }
        }

        // Check for genuine schedule conflict if a shooting date is provided
        if (scene.getShootingDate() != null && !scene.getShootingDate().equalsIgnoreCase("TBD")) {
            for (Scene existing : scenes) {
                if (existing.getShootingDate() != null
                        && existing.getShootingDate().equalsIgnoreCase(scene.getShootingDate())
                        && existing.getLocation().equalsIgnoreCase(scene.getLocation())) {
                    throw new ScheduleConflictException(scene.getLocation(), scene.getShootingDate(),
                            "Scene #" + existing.getSceneNumber() + " ('" + existing.getTitle()
                                    + "') is already scheduled at '" + scene.getLocation() + "' on " + scene.getShootingDate() + ".");
                }
            }
        }

        scenes.add(scene);
    }

    /**
     * Schedules or reschedules an existing scene to a new shooting date.
     * Enforces conflict detection to prevent double-booking the same filming location.
     */
    public void scheduleScene(int sceneNumber, String shootingDate)
            throws ResourceNotFoundException, ValidationException, ScheduleConflictException {
        Scene target = findSceneByNumber(sceneNumber);
        if (target == null) {
            throw new ResourceNotFoundException("Scene", String.valueOf(sceneNumber));
        }
        if (shootingDate == null || shootingDate.trim().isEmpty()) {
            throw new ValidationException("Shooting Date", "Shooting date cannot be empty.");
        }

        String trimmedDate = shootingDate.trim();
        for (Scene existing : scenes) {
            if (existing.getSceneNumber() != sceneNumber
                    && existing.getShootingDate() != null
                    && existing.getShootingDate().equalsIgnoreCase(trimmedDate)
                    && existing.getLocation().equalsIgnoreCase(target.getLocation())) {
                throw new ScheduleConflictException(target.getLocation(), trimmedDate,
                        "Scene #" + existing.getSceneNumber() + " ('" + existing.getTitle()
                                + "') is already scheduled at '" + target.getLocation() + "' on " + trimmedDate + ".");
            }
        }

        target.setShootingDate(trimmedDate);
    }

    public Scene findSceneByNumber(int sceneNumber) {
        for (Scene sc : scenes) {
            if (sc.getSceneNumber() == sceneNumber) {
                return sc;
            }
        }
        return null;
    }

    public Scene getSceneOrThrow(int sceneNumber) throws ResourceNotFoundException {
        Scene sc = findSceneByNumber(sceneNumber);
        if (sc == null) {
            throw new ResourceNotFoundException("Scene", String.valueOf(sceneNumber));
        }
        return sc;
    }

    /**
     * Adds a cast or crew member after validating against duplicate IDs within the movie.
     */
    public void addPerson(Person person) throws ValidationException {
        if (person == null) {
            throw new ValidationException("Person", "Cannot add null person.");
        }
        for (Person p : castAndCrew) {
            if (p.getId().equalsIgnoreCase(person.getId())) {
                throw new ValidationException("Person ID",
                        "Person with ID '" + person.getId() + "' already exists in movie '" + title + "'.");
            }
        }
        castAndCrew.add(person);
    }

    /**
     * Adds a review to the movie.
     * Enforces business rules:
     * 1. Movie status must be RELEASED.
     * 2. Rating must be between 1 and 5.
     * Throws ValidationException when rules are violated.
     */
    public void addReview(Review review) throws ValidationException {
        if (review == null) {
            throw new ValidationException("Review", "Cannot add null review.");
        }
        if (this.status != MovieStatus.RELEASED) {
            throw new ValidationException("Movie Status",
                    "Reviews can only be added when the movie is RELEASED! Current status is " + this.status + ".");
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new ValidationException("Review Rating",
                    "Star rating must be between 1 and 5 (got: " + review.getRating() + ").");
        }
        reviews.add(review);
    }

    public double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Review r : reviews) {
            sum += r.getRating();
        }
        return sum / reviews.size();
    }

    /**
     * Adds an expense to the movie.
     * Throws:
     * - ValidationException if expense is null or ID is duplicate
     * - BudgetExceededException if the expense exceeds the movie's total allocated budget.
     */
    public void addExpense(Expense expense) throws ValidationException, BudgetExceededException {
        if (expense == null) {
            throw new ValidationException("Expense", "Cannot add null expense.");
        }
        for (Expense existing : expenses) {
            if (existing.getId().equalsIgnoreCase(expense.getId())) {
                throw new ValidationException("Expense ID",
                        "Expense with ID '" + expense.getId() + "' already exists in movie '" + title + "'.");
            }
        }

        double newTotal = getTotalExpenses() + expense.getAmount();
        if (newTotal > budget) {
            double remaining = getRemainingBudget();
            throw new BudgetExceededException(expense.getAmount(), remaining, budget);
        }
        expenses.add(expense);
    }

    public double getTotalExpenses() {
        double sum = 0.0;
        for (Expense e : expenses) {
            sum += e.getAmount();
        }
        return sum;
    }

    public double getRemainingBudget() {
        return budget - getTotalExpenses();
    }

    public double getBudgetUtilizationPercentage() {
        if (budget <= 0.0) return 0.0;
        return (getTotalExpenses() / budget) * 100.0;
    }

    @Override
    public String toString() {
        String ratingInfo = reviews.isEmpty()
                ? "No Ratings"
                : String.format("%.1f/5.0 Stars (%d review%s)", getAverageRating(), reviews.size(), reviews.size() == 1 ? "" : "s");
        return String.format("[%s] %s (%d) | %s | Status: %s | Budget: $%,.2f | Rating: %s",
                id, title, releaseYear, genre, status, budget, ratingInfo);
    }
}
