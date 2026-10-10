package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents a Crew Member working on a movie.
 * Demonstrates: Inheritance (extends Person) and Method Overriding.
 */
public class CrewMember extends Person {
    private String department;

    public CrewMember(String id, String name, String email, String department) throws ValidationException {
        super(id, name, email);
        if (department == null || department.trim().isEmpty()) {
            throw new ValidationException("Department", "Crew department cannot be empty.");
        }
        this.department = department.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) throws ValidationException {
        if (department == null || department.trim().isEmpty()) {
            throw new ValidationException("Department", "Crew department cannot be empty.");
        }
        this.department = department.trim();
    }

    @Override
    public String getRole() {
        return "Crew (" + department + ")";
    }
}
