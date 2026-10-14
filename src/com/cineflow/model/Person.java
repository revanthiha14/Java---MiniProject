package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Abstract base class representing a person involved in movie production.
 * Demonstrates: Abstraction, Encapsulation, Validation, and Polymorphism.
 */
public abstract class Person implements Identifiable<String> {
    private String id;
    private String name;
    private String email;

    public Person(String id, String name, String email) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Person ID", "Person ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Person Name", "Person name cannot be empty.");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new ValidationException("Person Email", "A valid email address containing '@' is required.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.email = email.trim();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Person ID", "Person ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Person Name", "Person name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new ValidationException("Person Email", "A valid email address containing '@' is required.");
        }
        this.email = email.trim();
    }

    /**
     * Abstract method implemented by subclasses to return their specific role.
     * Demonstrates dynamic method dispatch and polymorphism.
     */
    public abstract String getRole();

    public void displayInfo() {
        System.out.printf("  [%-8s] %-22s | %-26s | %s%n", id, name, getRole(), email);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", id, name, getRole());
    }
}
