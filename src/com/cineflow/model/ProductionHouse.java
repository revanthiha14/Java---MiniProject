package com.cineflow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the film studio / production house managing multiple movies.
 * Demonstrates:
 *  - Aggregation: 1 ProductionHouse has many Movies (1-to-many relationship)
 *  - Map Collection Framework (LinkedHashMap for indexed, ordered movie catalog)
 *  - State management for active movie focus
 *  - Constructor chaining with this()
 */
public class ProductionHouse implements Identifiable<String>, Reportable, Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String headquarters;
    private int establishedYear;
    private final Map<String, Movie> movies; // Demonstrates Map collection
    private String activeMovieId;

    public ProductionHouse() {
        this("PH-101", "Horizon Studios", "Los Angeles & Mumbai", 2005);
    }

    public ProductionHouse(String id, String name, String headquarters, int establishedYear) {
        this.id = id;
        this.name = name;
        this.headquarters = headquarters;
        this.establishedYear = establishedYear;
        this.movies = new LinkedHashMap<>();
        this.activeMovieId = null;
    }

    public void addMovie(Movie movie) {
        if (movie != null && movie.getId() != null) {
            movies.put(movie.getId(), movie);
            if (activeMovieId == null) {
                activeMovieId = movie.getId();
            }
        }
    }

    public Optional<Movie> getMovie(String movieId) {
        if (movieId == null) return Optional.empty();
        return Optional.ofNullable(movies.get(movieId));
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies.values());
    }

    public Movie getActiveMovie() {
        if (activeMovieId != null && movies.containsKey(activeMovieId)) {
            return movies.get(activeMovieId);
        }
        if (!movies.isEmpty()) {
            Movie first = movies.values().iterator().next();
            activeMovieId = first.getId();
            return first;
        }
        return null;
    }

    public boolean setActiveMovie(Movie movie) {
        if (movie != null && movies.containsKey(movie.getId())) {
            this.activeMovieId = movie.getId();
            return true;
        }
        return false;
    }

    public boolean setActiveMovie(String movieId) {
        return setActiveMovieId(movieId);
    }

    public boolean setActiveMovieId(String movieId) {
        if (movieId != null && movies.containsKey(movieId)) {
            this.activeMovieId = movieId;
            return true;
        }
        return false;
    }

    public int getMovieCount() {
        return movies.size();
    }

    public boolean removeMovie(String movieId) {
        if (movieId != null && movies.containsKey(movieId)) {
            movies.remove(movieId);
            if (movieId.equals(activeMovieId)) {
                activeMovieId = movies.isEmpty() ? null : movies.keySet().iterator().next();
            }
            return true;
        }
        return false;
    }

    public void clearMovies() {
        movies.clear();
        activeMovieId = null;
    }

    @Override
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

    public int getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(int establishedYear) {
        this.establishedYear = establishedYear;
    }

    public String getActiveMovieId() {
        return activeMovieId;
    }

    @Override
    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append(String.format("                PRODUCTION HOUSE: %s\n", name.toUpperCase()));
        sb.append("========================================================================\n");
        sb.append(String.format("Studio ID      : %s | Est. Year: %d\n", id, establishedYear));
        sb.append(String.format("Headquarters   : %s\n", headquarters));
        sb.append(String.format("Active Movies  : %d projects in catalog\n", movies.size()));
        sb.append("------------------------------------------------------------------------\n");
        sb.append("PRODUCTION SLATE / MOVIE LIST:\n");
        if (movies.isEmpty()) {
            sb.append("  (No movies currently registered in studio catalog)\n");
        } else {
            for (Movie m : movies.values()) {
                String marker = m.getId().equals(activeMovieId) ? " [ACTIVE]" : "";
                sb.append(String.format("  • [%s] %-28s | %-16s | %s%s\n",
                        m.getId(), m.getTitle(), m.getGenre(), m.getProductionPhase().name(), marker));
            }
        }
        sb.append("========================================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %s, HQ: %s) - %d Active Movies", name, id, headquarters, movies.size());
    }
}
