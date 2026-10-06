package com.cineflow.exception;

/**
 * Custom checked exception thrown when an expense exceeds the
 * allocated budget of a movie.
 * Demonstrates: Custom Exception Handling.
 */
public class BudgetExceededException extends Exception {
    public BudgetExceededException(String message) {
        super(message);
    }
}
