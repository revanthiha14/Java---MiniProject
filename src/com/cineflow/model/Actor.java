package com.cineflow.model;

/**
 * Represents an Actor in a movie cast.
 * Demonstrates: Inheritance (extends Person) and Method Overriding.
 */
public class Actor extends Person {
    private String characterName;

    public Actor(String id, String name, String email, String characterName) {
        super(id, name, email);
        this.characterName = characterName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    @Override
    public String getRole() {
        return "Actor as \"" + characterName + "\"";
    }
}
