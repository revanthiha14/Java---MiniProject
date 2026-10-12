package com.cineflow.exception;

/**
 * Base checked exception class for all CineFlow application-specific errors.
 * Serves as the root of the CineFlow exception hierarchy.
 * Demonstrates: Custom Exception Hierarchy, Exception Chaining, and OOP Design.
 */
public class CineFlowException extends Exception {
    private static final long serialVersionUID = 1L;

    public CineFlowException(String message) {
        super(message);
    }

    public CineFlowException(String message, Throwable cause) {
        super(message, cause);
    }
}
