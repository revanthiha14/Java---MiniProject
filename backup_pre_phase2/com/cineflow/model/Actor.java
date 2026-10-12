package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Represents an Actor in a movie cast.
 * Demonstrates: Inheritance (extends Person) and Method Overriding.
 */
public class Actor extends Person {
    private String characterName;

    public Actor(String id, String name, String email, String characterName) throws ValidationException {
        super(id, name, email);
        if (characterName == null || characterName.trim().isEmpty()) {
            throw new ValidationException("Character Role", "Character role cannot be empty.");
        }
        this.characterName = characterName.trim();
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) throws ValidationException {
        if (characterName == null || characterName.trim().isEmpty()) {
            throw new ValidationException("Character Role", "Character role cannot be empty.");
        }
        this.characterName = characterName.trim();
    }

    @Override
    public String getRole() {
        return "Actor as \"" + characterName + "\"";
    }
}
