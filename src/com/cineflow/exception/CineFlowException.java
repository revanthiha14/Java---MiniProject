package com.cineflow.exception;

/**
 * Base checked exception class for all CineFlow production domain errors.
 * Demonstrates:
 *  - Custom Exception hierarchy
 *  - Exception chaining and message propagation
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
