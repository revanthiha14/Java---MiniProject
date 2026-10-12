package com.cineflow.service;

import com.cineflow.exception.ValidationException;
import com.cineflow.model.Movie;
import com.cineflow.model.MovieStatus;
import com.cineflow.model.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class responsible for managing audience/critic reviews and ratings.
 * Demonstrates: Separation of Concerns, Domain Business Rule Enforcement,
 * Statistical Calculations, Map Collections, and ValidationException propagation.
 */
public class ReviewManager {

    public ReviewManager() {
    }

    /**
     * Coordinates adding a review to a movie.
     * Enforces the business rules:
     * 1. Movie cannot be null.
     * 2. Movie status must be RELEASED.
     * 3. Review rating must be strictly between 1 and 5 stars.
     */
    public void addReview(Movie movie, Review review) throws ValidationException {
        if (movie == null) {
            throw new ValidationException("Movie", "Movie reference cannot be null.");
        }
        if (review == null) {
            throw new ValidationException("Review", "Review reference cannot be null.");
        }

        // Rule: Only released movies can receive reviews
        if (movie.getStatus() != MovieStatus.RELEASED) {
            throw new ValidationException("Movie Status",
                    "Reviews can only be submitted for movies with RELEASED status! (Current: " + movie.getStatus() + ")");
        }

        // Rule: Rating must be between 1 and 5
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new ValidationException("Review Rating",
                    "Rating must be between 1 and 5 stars (got: " + review.getRating() + ").");
        }

        // Delegate storage to the movie's review collection
        movie.addReview(review);
    }

    /**
     * Calculates the arithmetic mean average rating for the specified movie.
     */
    public double calculateAverageRating(Movie movie) {
        if (movie == null) {
            return 0.0;
        }
        List<Review> reviews = movie.getReviews();
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
     * Returns a distribution map counting occurrences of each star rating (1 through 5).
     * Demonstrates: HashMap usage for frequency counting.
     */
    public Map<Integer, Integer> getRatingDistribution(Movie movie) {
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int star = 1; star <= 5; star++) {
            distribution.put(star, 0);
        }

        if (movie != null) {
            for (Review r : movie.getReviews()) {
                int star = r.getRating();
                if (star >= 1 && star <= 5) {
                    distribution.put(star, distribution.get(star) + 1);
                }
            }
        }

        return distribution;
    }

    /**
     * Filters a list of movies by a minimum average rating threshold.
     * Only movies with at least one review and average rating >= minRating are returned.
     */
    public List<Movie> filterMoviesByMinRating(List<Movie> movies, double minRating) {
        List<Movie> filtered = new ArrayList<>();
        if (movies == null) {
            return filtered;
        }

        for (Movie m : movies) {
            if (!m.getReviews().isEmpty() && calculateAverageRating(m) >= minRating) {
                filtered.add(m);
            }
        }

        return filtered;
    }

    /**
     * Returns all reviews associated with the specified movie.
     */
    public List<Review> getReviews(Movie movie) {
        if (movie == null) {
            return new ArrayList<>();
        }
        return movie.getReviews();
    }

    /**
     * Returns the total number of reviews for the specified movie.
     */
    public int getTotalReviewCount(Movie movie) {
        if (movie == null) {
            return 0;
        }
        return movie.getReviews().size();
    }
}
