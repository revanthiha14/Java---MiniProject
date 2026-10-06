package com.cineflow.model;

import com.cineflow.exception.BudgetExceededException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Movie project.
 * Demonstrates: Composition, Polymorphic Collections (List of Person), and Exception Handling.
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

    public Movie(String id, String title, String genre, String director, int releaseYear, double budget) {
        this(id, title, genre, director, releaseYear, budget, MovieStatus.PLANNING);
    }

    public Movie(String id, String title, String genre, String director, int releaseYear, double budget, MovieStatus status) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.director = director;
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

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public MovieStatus getStatus() {
        return status;
    }

    public void setStatus(MovieStatus status) {
        this.status = status;
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

    public void addScene(Scene scene) {
        scenes.add(scene);
    }

    public void addPerson(Person person) {
        castAndCrew.add(person);
    }

    /**
     * Adds a review to the movie.
     * Enforces business rules:
     * 1. Movie status must be RELEASED.
     * 2. Rating must be between 1 and 5.
     * Returns true if added successfully, false otherwise.
     */
    public boolean addReview(Review review) {
        if (this.status != MovieStatus.RELEASED) {
            return false;
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            return false;
        }
        reviews.add(review);
        return true;
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
     * Throws BudgetExceededException if the expense exceeds the movie's total budget.
     */
    public void addExpense(Expense expense) throws BudgetExceededException {
        double newTotal = getTotalExpenses() + expense.getAmount();
        if (newTotal > budget) {
            double remaining = getRemainingBudget();
            throw new BudgetExceededException(String.format(
                    "Expense of $%,.2f exceeds remaining budget of $%,.2f! (Allocated Budget: $%,.2f)",
                    expense.getAmount(), remaining, budget));
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
        if (budget <= 0) return 0.0;
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
