package com.cineflow.exception;

/**
 * Thrown when an actor, crew member, equipment asset, location, or scene
 * cannot be located by its identifier.
 */
public class ResourceNotFoundException extends CineFlowException {
    private static final long serialVersionUID = 1L;

    private final String resourceType;
    private final String resourceId;

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
