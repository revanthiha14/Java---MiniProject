package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents an audience or critic review for a released movie.
 * Stores reviewer name, star rating (1-5), and a review comment.
 * Demonstrates: Encapsulation, Data Validation, and Exception Handling.
 */
public class Review {
    private String reviewerName;
    private int rating;
    private String comment;

    public Review(String reviewerName, int rating, String comment) throws ValidationException {
        if (reviewerName == null || reviewerName.trim().isEmpty()) {
            throw new ValidationException("Reviewer Name", "Reviewer name cannot be empty.");
        }
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating", "Star rating must be an integer between 1 and 5 (got: " + rating + ").");
        }
        if (comment == null || comment.trim().isEmpty()) {
            throw new ValidationException("Comment", "Review comment cannot be empty.");
        }
        this.reviewerName = reviewerName.trim();
        this.rating = rating;
        this.comment = comment.trim();
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) throws ValidationException {
        if (reviewerName == null || reviewerName.trim().isEmpty()) {
            throw new ValidationException("Reviewer Name", "Reviewer name cannot be empty.");
        }
        this.reviewerName = reviewerName.trim();
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) throws ValidationException {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating", "Star rating must be between 1 and 5.");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) throws ValidationException {
        if (comment == null || comment.trim().isEmpty()) {
            throw new ValidationException("Comment", "Review comment cannot be empty.");
        }
        this.comment = comment.trim();
    }

    @Override
    public String toString() {
        return String.format("[%d/5 Stars] %s: \"%s\"", rating, reviewerName, comment);
    }
}
