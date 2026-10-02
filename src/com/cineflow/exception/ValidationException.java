package com.cineflow.exception;

/**
 * Thrown when domain validation rules are violated (e.g., negative budget amount,
 * invalid page count, empty name, malformed date string).
 */
public class ValidationException extends CineFlowException {
    private static final long serialVersionUID = 1L;

    private final String fieldName;

    public ValidationException(String fieldName, String message) {
        super(String.format("Validation failed for field '%s': %s", fieldName, message));
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
