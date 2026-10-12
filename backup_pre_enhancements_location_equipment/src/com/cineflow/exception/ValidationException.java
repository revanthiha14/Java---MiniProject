package com.cineflow.exception;

/**
 * Custom checked exception thrown when domain validation rules are violated,
 * such as empty required fields, invalid identifiers, duplicate keys,
 * negative budgets or expenses, invalid ratings, or reviewing unreleased movies.
 * Demonstrates: Custom Exception Hierarchy and Input Validation.
 */
public class ValidationException extends CineFlowException {
    private static final long serialVersionUID = 1L;

    private final String fieldName;

    public ValidationException(String message) {
        super(message);
        this.fieldName = "General";
    }

    public ValidationException(String fieldName, String message) {
        super(String.format("Validation error on '%s': %s", fieldName, message));
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
