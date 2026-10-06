package com.cineflow.service;

import com.cineflow.model.Actor;
import com.cineflow.model.CrewMember;
import com.cineflow.model.Expense;
import com.cineflow.model.Movie;
import com.cineflow.model.MovieStatus;
import com.cineflow.model.ProductionHouse;
import com.cineflow.model.Review;
import com.cineflow.model.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central management service for studios, movies, and production data.
 * Demonstrates: HashMap usage for key-value lookups, ArrayList management, and modular service design.
 */
public class MovieManager {
    private final Map<String, ProductionHouse> studios;
    private final FileService fileService;
    private String activeStudioId;
    private String activeMovieId;

    public MovieManager() {
        this.studios = new HashMap<>();
        this.fileService = new FileService();
        initializeData();
    }

    private void initializeData() {
        try {
            if (fileService.hasExistingData()) {
                loadData();
            } else {
                seedInitialData();
                saveData();
            }
        } catch (Exception e) {
            System.err.println("Note: Using seeded initial data (" + e.getMessage() + ")");
            seedInitialData();
        }
    }

    public void addStudio(ProductionHouse studio) {
        studios.put(studio.getId(), studio);
        if (activeStudioId == null) {
            activeStudioId = studio.getId();
        }
    }

    public ProductionHouse getStudio(String id) {
        return studios.get(id);
    }

    public List<ProductionHouse> getAllStudios() {
        return new ArrayList<>(studios.values());
    }

    public ProductionHouse getActiveStudio() {
        if (activeStudioId == null && !studios.isEmpty()) {
            activeStudioId = studios.keySet().iterator().next();
        }
        return studios.get(activeStudioId);
    }

    public boolean setActiveStudioId(String studioId) {
        if (studios.containsKey(studioId)) {
            this.activeStudioId = studioId;
            ProductionHouse ph = studios.get(studioId);
            if (!ph.getMovies().isEmpty()) {
                this.activeMovieId = ph.getMovies().get(0).getId();
            } else {
                this.activeMovieId = null;
            }
            return true;
        }
        return false;
    }

    public Movie getActiveMovie() {
        ProductionHouse studio = getActiveStudio();
        if (studio == null) return null;

        if (activeMovieId != null) {
            Movie movie = studio.findMovieById(activeMovieId);
            if (movie != null) return movie;
        }

        if (!studio.getMovies().isEmpty()) {
            Movie first = studio.getMovies().get(0);
            this.activeMovieId = first.getId();
            return first;
        }

        return null;
    }

    public boolean setActiveMovieId(String movieId) {
        // First check in active studio
        ProductionHouse currentStudio = getActiveStudio();
        if (currentStudio != null && currentStudio.findMovieById(movieId) != null) {
            this.activeMovieId = movieId;
            return true;
        }

        // If not in active studio, search all studios and switch active studio too
        for (ProductionHouse ph : studios.values()) {
            Movie m = ph.findMovieById(movieId);
            if (m != null) {
                this.activeStudioId = ph.getId();
                this.activeMovieId = m.getId();
                return true;
            }
        }

        return false;
    }

    public void addMovieToActiveStudio(Movie movie) {
        ProductionHouse studio = getActiveStudio();
        if (studio != null) {
            studio.addMovie(movie);
            this.activeMovieId = movie.getId();
        }
    }

    /**
     * Retrieves all movies across all registered studios.
     */
    public List<Movie> getAllMovies() {
        List<Movie> all = new ArrayList<>();
        for (ProductionHouse ph : studios.values()) {
            all.addAll(ph.getMovies());
        }
        return all;
    }

    /**
     * Finds a movie by ID across all studios.
     */
    public Movie findMovieAnywhere(String movieId) {
        for (ProductionHouse ph : studios.values()) {
            Movie m = ph.findMovieById(movieId);
            if (m != null) return m;
        }
        return null;
    }

    /**
     * Searches movies by optional ID/title query, genre, and minimum average rating.
     * Demonstrates simple iteration, String matching, and conditional filtering.
     */
    public List<Movie> searchMovies(String query, String genre, double minRating) {
        List<Movie> results = new ArrayList<>();
        String q = (query != null) ? query.trim().toLowerCase() : "";
        String g = (genre != null) ? genre.trim().toLowerCase() : "";

        for (Movie m : getAllMovies()) {
            boolean matchesQuery = q.isEmpty()
                    || m.getId().toLowerCase().contains(q)
                    || m.getTitle().toLowerCase().contains(q);

            boolean matchesGenre = g.isEmpty()
                    || m.getGenre().toLowerCase().contains(g);

            boolean matchesRating = (minRating <= 0.0)
                    || (!m.getReviews().isEmpty() && m.getAverageRating() >= minRating);

            if (matchesQuery && matchesGenre && matchesRating) {
                results.add(m);
            }
        }
        return results;
    }

    public void saveData() throws IOException {
        fileService.saveAllData(getAllStudios());
    }

    public void loadData() throws IOException {
        List<ProductionHouse> loaded = fileService.loadAllData();
        studios.clear();
        for (ProductionHouse ph : loaded) {
            studios.put(ph.getId(), ph);
        }
        if (!studios.isEmpty()) {
            activeStudioId = studios.keySet().iterator().next();
            ProductionHouse active = studios.get(activeStudioId);
            if (!active.getMovies().isEmpty()) {
                activeMovieId = active.getMovies().get(0).getId();
            }
        }
    }

    /**
     * Seeds realistic initial film production data.
     */
    public void seedInitialData() {
        studios.clear();

        // Studio 1: Horizon Studios
        ProductionHouse studio1 = new ProductionHouse("PH-101", "Horizon Studios", "Los Angeles & Mumbai");

        // Movie 1: Interstellar Journey
        Movie m1 = new Movie("MOV-01", "Interstellar Journey", "Sci-Fi", "Marcus Sterling", 2026, 1500000.0, MovieStatus.PRODUCTION);
        m1.addScene(new Scene(1, "Deep Orbit Discovery", "Soundstage A (CGI)", "COMPLETED"));
        m1.addScene(new Scene(2, "Spacewalk Sensor Repair", "Exterior Orbital Rig", "IN_PROGRESS"));
        m1.addScene(new Scene(3, "Atmospheric Entry Bridge", "Flight Deck Set", "PLANNED"));

        m1.addPerson(new Actor("ACT-101", "Liam Vance", "liam@apex.com", "Commander Jax"));
        m1.addPerson(new Actor("ACT-102", "Dr. Lyra Thorne", "lyra@apex.com", "Chief Scientist"));
        m1.addPerson(new CrewMember("CRW-201", "Elena Vance", "elena@cinematography.com", "Cinematography"));
        m1.addPerson(new CrewMember("CRW-202", "Chloe Bennett", "chloe@vfx.com", "VFX & Sound"));

        try {
            m1.addExpense(new Expense("EXP-01", "Visual Effects", 250000.0, "Zero-G wire rigging and 3D composites"));
            m1.addExpense(new Expense("EXP-02", "Camera Gear", 120000.0, "High-speed sensor camera rental"));
            m1.addExpense(new Expense("EXP-03", "Costumes", 45000.0, "Hero astronaut suits and tailoring"));
        } catch (Exception ignored) {}

        // Movie 2: Shadows of the Past
        Movie m2 = new Movie("MOV-02", "Shadows of the Past", "Thriller", "Evelyn Blackwood", 2027, 800000.0, MovieStatus.PLANNING);
        m2.addScene(new Scene(1, "Rainy Pier Encounter", "Dock Warehouse #4", "COMPLETED"));
        m2.addScene(new Scene(2, "Interrogation Room", "Studio Stage B", "PLANNED"));

        m2.addPerson(new Actor("ACT-103", "Robert Sterling", "robert@talent.com", "Detective Vance"));
        m2.addPerson(new CrewMember("CRW-203", "Marcus Hill", "marcus@sound.com", "Sound & Foley"));

        try {
            m2.addExpense(new Expense("EXP-01", "Screenplay Rights", 35000.0, "Acquisition of original noir script"));
            m2.addExpense(new Expense("EXP-02", "Set Design", 60000.0, "Warehouse set construction and rain rig"));
        } catch (Exception ignored) {}

        studio1.addMovie(m1);
        studio1.addMovie(m2);

        // Studio 2: Mythri Movie Makers
        ProductionHouse studio2 = new ProductionHouse("PH-102", "Mythri Movie Makers", "Hyderabad, India");
        Movie m3 = new Movie("MOV-03", "Pushpa: The Rule", "Action Drama", "Sukumar", 2026, 2500000.0, MovieStatus.RELEASED);
        m3.addScene(new Scene(1, "Forest Border Ambush", "Maredumilli Forest", "COMPLETED"));
        m3.addScene(new Scene(2, "Port Cargo Standoff", "Visakhapatnam Port", "IN_PROGRESS"));

        m3.addPerson(new Actor("ACT-104", "Allu Arjun", "allu@mythri.com", "Pushpa Raj"));
        m3.addPerson(new Actor("ACT-105", "Fahadh Faasil", "fahadh@mythri.com", "Bhanwar Singh Shekhawat"));
        m3.addPerson(new CrewMember("CRW-204", "Miroslaw Brozek", "miroslaw@camera.com", "Director of Photography"));
        m3.addPerson(new CrewMember("CRW-205", "Devi Sri Prasad", "dsp@music.com", "Music & Background Score"));

        try {
            m3.addExpense(new Expense("EXP-01", "Stunts & Action", 400000.0, "Forest convoy chase and blast effects"));
            m3.addExpense(new Expense("EXP-02", "Art Direction", 180000.0, "Timber check-post replica set"));
        } catch (Exception ignored) {}

        // Add initial reviews to released movie MOV-03
        m3.addReview(new Review("John FilmCritic", 5, "Remarkable action sequences and gripping drama!"));
        m3.addReview(new Review("Sarah Jenkins", 4, "High voltage performances and outstanding music."));

        studio2.addMovie(m3);

        // Studio 3: Warner Bros. Pictures
        ProductionHouse studio3 = new ProductionHouse("PH-103", "Warner Bros. Pictures", "Burbank, California");
        Movie m4 = new Movie("MOV-04", "The Dark Horizon", "Neo-Noir", "Christopher Nolan", 2027, 3000000.0, MovieStatus.POST_PRODUCTION);
        m4.addScene(new Scene(1, "Rooftop Chase", "Gotham Heights", "PLANNED"));
        m4.addPerson(new Actor("ACT-106", "Christian Bale", "bale@warner.com", "Bruce Wayne"));
        m4.addPerson(new CrewMember("CRW-206", "Hoyte van Hoytema", "hoyte@camera.com", "Cinematography"));
        try {
            m4.addExpense(new Expense("EXP-01", "Practical Effects", 500000.0, "IMAX rig and miniature models"));
        } catch (Exception ignored) {}

        studio3.addMovie(m4);

        addStudio(studio1);
        addStudio(studio2);
        addStudio(studio3);

        this.activeStudioId = studio1.getId();
        this.activeMovieId = m1.getId();
    }
}
