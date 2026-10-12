package com.cineflow.exception;

/**
 * Custom checked exception thrown when a requested resource (movie, production house,
 * scene, or personnel record) cannot be found by its identifier.
 * Demonstrates: Custom Exception Hierarchy and Domain Error Handling.
 */
public class ResourceNotFoundException extends CineFlowException {
    private static final long serialVersionUID = 1L;

    private final String resourceType;
    private final String resourceId;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = "Resource";
        this.resourceId = "Unknown";
    }

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s with ID '%s' was not found in the production repository.", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}
