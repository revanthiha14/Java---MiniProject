package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.Actor;
import com.cineflow.model.CrewMember;
import com.cineflow.model.Movie;
import com.cineflow.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing cast and crew personnel rosters for movies.
 * Demonstrates: Separation of Concerns, Delegation, Polymorphism (handling Person subclasses),
 * and Checked Exception Handling (ValidationException, ResourceNotFoundException).
 */
public class CastManager {

    public CastManager() {
    }

    /**
     * Adds any Person (Actor or CrewMember) to the specified movie.
     * Enforces non-null validation and duplicate person ID prevention.
     */
    public void addPerson(Movie movie, Person person) throws ValidationException {
        if (movie == null) {
            throw new ValidationException("Movie", "Movie reference cannot be null.");
        }
        if (person == null) {
            throw new ValidationException("Person", "Person reference cannot be null.");
        }

        // Check for duplicate ID within this movie's cast and crew
        for (Person existing : movie.getCastAndCrew()) {
            if (existing.getId().equalsIgnoreCase(person.getId().trim())) {
                throw new ValidationException("Person ID",
                        "Person with ID '" + person.getId().trim() + "' already exists in movie '" + movie.getTitle() + "'.");
            }
        }

        movie.getCastAndCrew().add(person);
    }

    /**
     * Convenience method to add an Actor to the specified movie.
     */
    public void addActor(Movie movie, Actor actor) throws ValidationException {
        addPerson(movie, actor);
    }

    /**
     * Convenience method to add a CrewMember to the specified movie.
     */
    public void addCrewMember(Movie movie, CrewMember crewMember) throws ValidationException {
        addPerson(movie, crewMember);
    }

    /**
     * Removes a cast or crew member from the movie by their ID.
     * Throws ResourceNotFoundException if no matching member is found.
     */
    public boolean removePersonById(Movie movie, String personId) throws ResourceNotFoundException, ValidationException {
        if (movie == null) {
            throw new ValidationException("Movie", "Movie reference cannot be null.");
        }
        if (personId == null || personId.trim().isEmpty()) {
            throw new ValidationException("Person ID", "Person ID cannot be empty.");
        }

        String targetId = personId.trim();
        List<Person> personnel = movie.getCastAndCrew();
        for (int i = 0; i < personnel.size(); i++) {
            if (personnel.get(i).getId().equalsIgnoreCase(targetId)) {
                personnel.remove(i);
                return true;
            }
        }

        throw new ResourceNotFoundException("Cast/Crew Member", targetId);
    }

    /**
     * Searches for a person by their ID within the specified movie.
     */
    public Person findPersonById(Movie movie, String personId) throws ResourceNotFoundException {
        if (movie == null || personId == null || personId.trim().isEmpty()) {
            throw new ResourceNotFoundException("Cast/Crew Member", (personId != null) ? personId.trim() : "Empty ID");
        }

        String targetId = personId.trim();
        for (Person p : movie.getCastAndCrew()) {
            if (p.getId().equalsIgnoreCase(targetId)) {
                return p;
            }
        }

        throw new ResourceNotFoundException("Cast/Crew Member", targetId);
    }

    /**
     * Searches for personnel in the movie by keyword matching ID, name, or role.
     */
    public List<Person> searchPersonnel(Movie movie, String query) {
        List<Person> results = new ArrayList<>();
        if (movie == null) {
            return results;
        }

        String q = (query != null) ? query.trim().toLowerCase() : "";
        for (Person p : movie.getCastAndCrew()) {
            if (q.isEmpty()
                    || p.getId().toLowerCase().contains(q)
                    || p.getName().toLowerCase().contains(q)
                    || p.getRole().toLowerCase().contains(q)) {
                results.add(p);
            }
        }

        return results;
    }

    /**
     * Retrieves all Actor instances for the specified movie using instanceof filtering.
     * Demonstrates: Runtime Polymorphism and Downcasting.
     */
    public List<Actor> getActors(Movie movie) {
        List<Actor> actors = new ArrayList<>();
        if (movie != null) {
            for (Person p : movie.getCastAndCrew()) {
                if (p instanceof Actor a) {
                    actors.add(a);
                }
            }
        }
        return actors;
    }

    /**
     * Retrieves all CrewMember instances for the specified movie using instanceof filtering.
     * Demonstrates: Runtime Polymorphism and Downcasting.
     */
    public List<CrewMember> getCrewMembers(Movie movie) {
        List<CrewMember> crew = new ArrayList<>();
        if (movie != null) {
            for (Person p : movie.getCastAndCrew()) {
                if (p instanceof CrewMember c) {
                    crew.add(c);
                }
            }
        }
        return crew;
    }

    /**
     * Returns the full list of cast and crew members for the specified movie.
     */
    public List<Person> getAllPersonnel(Movie movie) {
        if (movie == null) {
            return new ArrayList<>();
        }
        return movie.getCastAndCrew();
    }
}
