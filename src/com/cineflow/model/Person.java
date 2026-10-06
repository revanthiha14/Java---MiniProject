package com.cineflow.model;

/**
 * Abstract base class representing a person involved in movie production.
 * Demonstrates: Abstraction, Encapsulation, and Polymorphism.
 */
public abstract class Person {
    private String id;
    private String name;
    private String email;

    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
