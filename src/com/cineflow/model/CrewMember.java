package com.cineflow.model;

/**
 * Represents a Crew Member working on a movie.
 * Demonstrates: Inheritance (extends Person) and Method Overriding.
 */
public class CrewMember extends Person {
    private String department;

    public CrewMember(String id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getRole() {
        return "Crew (" + department + ")";
    }
}
