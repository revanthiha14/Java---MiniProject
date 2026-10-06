package com.cineflow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Film Production House / Studio.
 * Demonstrates: Aggregation (ProductionHouse contains Movies) and Encapsulation.
 */
public class ProductionHouse {
    private String id;
    private String name;
    private String headquarters;
    private List<Movie> movies;

    public ProductionHouse(String id, String name, String headquarters) {
        this.id = id;
        this.name = name;
        this.headquarters = headquarters;
        this.movies = new ArrayList<>();
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

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public Movie findMovieById(String movieId) {
        for (Movie m : movies) {
            if (m.getId().equalsIgnoreCase(movieId)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-25s | HQ: %-25s | Movies: %d",
                id, name, headquarters, movies.size());
    }
}
