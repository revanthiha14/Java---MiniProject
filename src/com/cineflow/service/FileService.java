package com.cineflow.service;

import com.cineflow.model.Actor;
import com.cineflow.model.CrewMember;
import com.cineflow.model.Expense;
import com.cineflow.model.Movie;
import com.cineflow.model.MovieStatus;
import com.cineflow.model.Person;
import com.cineflow.model.ProductionHouse;
import com.cineflow.model.Review;
import com.cineflow.model.Scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles saving and loading application data using plain text files.
 * Demonstrates: File I/O (BufferedReader, PrintWriter), String Tokenization/Parsing,
 * Robust Error Handling (skipping malformed records gracefully), and IOException propagation.
 */
public class FileService {
    private final String dataDir;

    public FileService() {
        this("data");
    }

    public FileService(String dataDir) {
        this.dataDir = dataDir;
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Saves all production houses, movies, scenes, cast/crew, expenses, and reviews to text files.
     */
    public void saveAllData(List<ProductionHouse> studios) throws IOException {
        if (studios == null) {
            throw new IOException("Cannot save null studio collection.");
        }
        saveStudios(studios);
        saveMovies(studios);
        saveScenes(studios);
        saveCastAndCrew(studios);
        saveExpenses(studios);
        saveReviews(studios);
    }

    private void saveStudios(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "studios.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# ID|Name|Headquarters");
            for (ProductionHouse ph : studios) {
                writer.println(ph.getId() + "|" + ph.getName() + "|" + ph.getHeadquarters());
            }
        }
    }

    private void saveMovies(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "movies.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|StudioID|Title|Genre|Director|ReleaseYear|Budget|Status");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    writer.println(m.getId() + "|" + ph.getId() + "|" + m.getTitle() + "|"
                            + m.getGenre() + "|" + m.getDirector() + "|"
                            + m.getReleaseYear() + "|" + m.getBudget() + "|"
                            + m.getStatus().name());
                }
            }
        }
    }

    private void saveScenes(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "scenes.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|SceneNumber|Title|Location|Status|ShootingDate");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (Scene sc : m.getScenes()) {
                        writer.println(m.getId() + "|" + sc.getSceneNumber() + "|"
                                + sc.getTitle() + "|" + sc.getLocation() + "|"
                                + sc.getStatus() + "|" + sc.getShootingDate());
                    }
                }
            }
        }
    }

    private void saveCastAndCrew(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "cast_crew.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|Type|PersonID|Name|Email|RoleOrDepartment");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (Person p : m.getCastAndCrew()) {
                        if (p instanceof Actor a) {
                            writer.println(m.getId() + "|ACTOR|" + a.getId() + "|" + a.getName()
                                    + "|" + a.getEmail() + "|" + a.getCharacterName());
                        } else if (p instanceof CrewMember c) {
                            writer.println(m.getId() + "|CREW|" + c.getId() + "|" + c.getName()
                                    + "|" + c.getEmail() + "|" + c.getDepartment());
                        }
                    }
                }
            }
        }
    }

    private void saveExpenses(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "expenses.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|ExpenseID|Category|Amount|Description");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (Expense exp : m.getExpenses()) {
                        writer.println(m.getId() + "|" + exp.getId() + "|" + exp.getCategory()
                                + "|" + exp.getAmount() + "|" + exp.getDescription());
                    }
                }
            }
        }
    }

    private void saveReviews(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "reviews.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|ReviewerName|Rating|Comment");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (Review r : m.getReviews()) {
                        writer.println(m.getId() + "|" + r.getReviewerName() + "|"
                                + r.getRating() + "|" + r.getComment());
                    }
                }
            }
        }
    }

    /**
     * Loads all production houses, movies, scenes, cast/crew, expenses, and reviews from text files.
     * Skips malformed records gracefully with explanatory warnings without crashing.
     */
    public List<ProductionHouse> loadAllData() throws IOException {
        Map<String, ProductionHouse> studioMap = new HashMap<>();
        Map<String, Movie> movieMap = new HashMap<>();

        // 1. Load Studios
        File studioFile = new File(dataDir, "studios.txt");
        if (studioFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(studioFile))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3) {
                        try {
                            ProductionHouse ph = new ProductionHouse(parts[0].trim(), parts[1].trim(), parts[2].trim());
                            studioMap.put(ph.getId(), ph);
                        } catch (Exception e) {
                            System.err.println("[WARNING] studios.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] studios.txt line " + lineNum + " malformed (insufficient fields): " + line);
                    }
                }
            }
        }

        // 2. Load Movies
        File movieFile = new File(dataDir, "movies.txt");
        if (movieFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(movieFile))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 7) {
                        try {
                            String movieId = parts[0].trim();
                            String studioId = parts[1].trim();
                            String title = parts[2].trim();
                            String genre = parts[3].trim();
                            String director = parts[4].trim();
                            int releaseYear = Integer.parseInt(parts[5].trim());
                            double budget = Double.parseDouble(parts[6].trim());

                            MovieStatus status = MovieStatus.PLANNING;
                            if (parts.length >= 8) {
                                try {
                                    status = MovieStatus.valueOf(parts[7].trim().toUpperCase());
                                } catch (Exception ignored) {}
                            }

                            Movie movie = new Movie(movieId, title, genre, director, releaseYear, budget, status);
                            movieMap.put(movieId, movie);

                            ProductionHouse ph = studioMap.get(studioId);
                            if (ph != null) {
                                ph.getMovies().add(movie);
                            } else {
                                System.err.println("[WARNING] Movie '" + movieId + "' references unknown Studio ID: " + studioId);
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] movies.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] movies.txt line " + lineNum + " malformed: " + line);
                    }
                }
            }
        }

        // 3. Load Scenes
        File sceneFile = new File(dataDir, "scenes.txt");
        if (sceneFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(sceneFile))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 5) {
                        try {
                            String movieId = parts[0].trim();
                            int sceneNum = Integer.parseInt(parts[1].trim());
                            String title = parts[2].trim();
                            String location = parts[3].trim();
                            String status = parts[4].trim();
                            String shootingDate = (parts.length >= 6) ? parts[5].trim() : "TBD";

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                movie.getScenes().add(new Scene(sceneNum, title, location, status, shootingDate));
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] scenes.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] scenes.txt line " + lineNum + " malformed: " + line);
                    }
                }
            }
        }

        // 4. Load Cast and Crew
        File castCrewFile = new File(dataDir, "cast_crew.txt");
        if (castCrewFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(castCrewFile))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        try {
                            String movieId = parts[0].trim();
                            String type = parts[1].trim();
                            String personId = parts[2].trim();
                            String name = parts[3].trim();
                            String email = parts[4].trim();
                            String roleOrDept = parts[5].trim();

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                if ("ACTOR".equalsIgnoreCase(type)) {
                                    movie.getCastAndCrew().add(new Actor(personId, name, email, roleOrDept));
                                } else if ("CREW".equalsIgnoreCase(type)) {
                                    movie.getCastAndCrew().add(new CrewMember(personId, name, email, roleOrDept));
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] cast_crew.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] cast_crew.txt line " + lineNum + " malformed: " + line);
                    }
                }
            }
        }

        // 5. Load Expenses
        File expenseFile = new File(dataDir, "expenses.txt");
        if (expenseFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(expenseFile))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 5) {
                        try {
                            String movieId = parts[0].trim();
                            String expId = parts[1].trim();
                            String category = parts[2].trim();
                            double amount = Double.parseDouble(parts[3].trim());
                            String desc = parts[4].trim();

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                movie.getExpenses().add(new Expense(expId, category, amount, desc));
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] expenses.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] expenses.txt line " + lineNum + " malformed: " + line);
                    }
                }
            }
        }

        // 6. Load Reviews
        File reviewFile = new File(dataDir, "reviews.txt");
        if (reviewFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(reviewFile))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 4) {
                        try {
                            String movieId = parts[0].trim();
                            String reviewerName = parts[1].trim();
                            int rating = Integer.parseInt(parts[2].trim());
                            String comment = parts[3].trim();

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                movie.getReviews().add(new Review(reviewerName, rating, comment));
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] reviews.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] reviews.txt line " + lineNum + " malformed: " + line);
                    }
                }
            }
        }

        return new ArrayList<>(studioMap.values());
    }

    public boolean hasExistingData() {
        File f = new File(dataDir, "studios.txt");
        return f.exists() && f.length() > 0;
    }
}
