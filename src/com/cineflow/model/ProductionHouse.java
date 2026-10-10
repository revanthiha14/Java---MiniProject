package com.cineflow.model;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Film Production House / Studio.
 * Demonstrates: Aggregation (ProductionHouse contains Movies), Encapsulation,
 * and Exception Handling (ValidationException, ResourceNotFoundException).
 */
public class ProductionHouse {
    private String id;
    private String name;
    private String headquarters;
    private List<Movie> movies;

    public ProductionHouse(String id, String name, String headquarters) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Studio ID", "Studio ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Studio Name", "Studio name cannot be empty.");
        }
        if (headquarters == null || headquarters.trim().isEmpty()) {
            throw new ValidationException("Headquarters", "Headquarters cannot be empty.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.headquarters = headquarters.trim();
        this.movies = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Studio ID", "Studio ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Studio Name", "Studio name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) throws ValidationException {
        if (headquarters == null || headquarters.trim().isEmpty()) {
            throw new ValidationException("Headquarters", "Headquarters cannot be empty.");
        }
        this.headquarters = headquarters.trim();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void addMovie(Movie movie) throws ValidationException {
        if (movie == null) {
            throw new ValidationException("Movie", "Cannot add null movie to production house.");
        }
        if (findMovieById(movie.getId()) != null) {
            throw new ValidationException("Movie ID", "Movie with ID '" + movie.getId() + "' already exists in " + name + ".");
        }
        movies.add(movie);
    }

    public Movie findMovieById(String movieId) {
        if (movieId == null) return null;
        for (Movie m : movies) {
            if (m.getId().equalsIgnoreCase(movieId.trim())) {
                return m;
            }
        }
        return null;
    }

    public Movie getMovieOrThrow(String movieId) throws ResourceNotFoundException {
        Movie m = findMovieById(movieId);
        if (m == null) {
            throw new ResourceNotFoundException("Movie", movieId);
        }
        return m;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-25s | HQ: %-25s | Movies: %d",
                id, name, headquarters, movies.size());
    }
}
