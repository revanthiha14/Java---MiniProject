package com.cineflow.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an audience or film critic review and star rating for a movie.
 * Demonstrates:
 *  - Multiple interface implementation (Identifiable<String>, Reportable, Comparable<Review>, Serializable)
 *  - Strict encapsulation and input validation (1-5 star ratings)
 *  - Constructor chaining with this()
 *  - Formatted star display generation
 */
public class Review implements Identifiable<String>, Reportable, Comparable<Review>, Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String movieId;
    private String reviewerName;
    private int rating; // 1 to 5 stars
    private String comment;
    private LocalDate reviewDate;

    /**
     * Default constructor with safe defaults.
     */
    public Review() {
        this("REV-000", "MOV-01", "Anonymous", 5, "Outstanding production and performances!", LocalDate.now());
    }

    /**
     * Constructor without explicit ID (generates timestamp-based ID) and defaults date to today.
     */
    public Review(String movieId, String reviewerName, int rating, String comment) {
        this("REV-" + (System.currentTimeMillis() % 100000), movieId, reviewerName, rating, comment, LocalDate.now());
    }

    /**
     * Constructor with review date defaulting to today.
     */
    public Review(String id, String movieId, String reviewerName, int rating, String comment) {
        this(id, movieId, reviewerName, rating, comment, LocalDate.now());
    }

    /**
     * Full parameterized constructor with validation.
     */
    public Review(String id, String movieId, String reviewerName, int rating, String comment, LocalDate reviewDate) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars. Provided: " + rating);
        }
        this.id = id != null && !id.trim().isEmpty() ? id.trim() : "REV-" + (System.currentTimeMillis() % 100000);
        this.movieId = movieId != null ? movieId.trim() : "";
        this.reviewerName = (reviewerName != null && !reviewerName.trim().isEmpty()) ? reviewerName.trim() : "Anonymous";
        this.rating = rating;
        this.comment = (comment != null) ? comment.trim() : "";
        this.reviewDate = (reviewDate != null) ? reviewDate : LocalDate.now();
    }

    /**
     * Returns a 5-star visual representation (e.g., ★★★★☆).
     */
    public String getStarDisplay() {
        int stars = Math.max(1, Math.min(5, rating));
        return "★".repeat(stars) + "☆".repeat(5 - stars);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars. Provided: " + rating);
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    @Override
    public String generateDetailedReport() {
        return String.format("[%s] %s | %s (%d/5) | Date: %s\n\"%s\"",
                id, reviewerName, getStarDisplay(), rating, reviewDate, comment);
    }

    @Override
    public int compareTo(Review o) {
        if (o == null) return 1;
        // Primary: Most recent reviews first; Secondary: highest rating first
        int dateCmp = o.reviewDate.compareTo(this.reviewDate);
        if (dateCmp != 0) return dateCmp;
        return Integer.compare(o.rating, this.rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review review)) return false;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%d/5 Stars): \"%s\"",
                id, reviewerName, getStarDisplay(), rating, comment);
    }
}
