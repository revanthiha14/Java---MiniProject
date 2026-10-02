package com.cineflow.service;

import com.cineflow.exception.BudgetExceededException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.DaylightRequirement;
import com.cineflow.model.Department;
import com.cineflow.model.Movie;
import com.cineflow.model.ProductionHouse;
import com.cineflow.model.ProductionPhase;
import com.cineflow.model.Review;
import com.cineflow.model.Scene;
import com.cineflow.model.SceneStatus;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service managing persistent storage of all production houses, movies, scenes,
 * screenplays, scripts, and departmental budgets using human-readable text files.
 *
 * Demonstrates:
 *  - Java IO Streams: BufferedReader, BufferedWriter, FileReader, FileWriter, PrintWriter
 *  - File parsing, serialization, and deserialization of structured script and budget text
 *  - Screenplay document generation for directors and screenwriters
 */
public class FilePersistenceService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String dataDirectory;

    public FilePersistenceService() {
        this("data");
    }

    public FilePersistenceService(String dataDirectory) {
        this.dataDirectory = dataDirectory != null ? dataDirectory : "data";
        new File(this.dataDirectory).mkdirs();
        new File(this.dataDirectory + "/scripts").mkdirs();
    }

    public boolean isDataPersisted() {
        File studioFile = new File(dataDirectory, "studios.txt");
        File sceneFile = new File(dataDirectory, "scenes_script.txt");
        return studioFile.exists() && studioFile.length() > 0 &&
               sceneFile.exists() && sceneFile.length() > 0;
    }

    /**
     * Saves all production houses, movies, scenes, scripts, budgets, and reviews to human-readable text files.
     */
    public synchronized void saveAllData(StudioService studioService, ProductionService productionService)
            throws IOException {
        new File(dataDirectory).mkdirs();
        new File(dataDirectory + "/scripts").mkdirs();

        saveStudios(studioService);
        saveMovies(studioService);
        saveScenesAndScripts(productionService);
        saveBudgets(studioService);
        saveReviews(studioService);
        exportAllMovieScreenplays(studioService, productionService);
    }

    /**
     * Loads all production houses, movies, scenes, scripts, budgets, and reviews from text files into memory.
     */
    public synchronized void loadAllData(StudioService studioService, ProductionService productionService)
            throws IOException {
        if (!isDataPersisted()) {
            return;
        }

        studioService.clear();
        productionService.clearAllScenes();

        loadStudios(studioService);
        loadMovies(studioService);
        loadScenesAndScripts(studioService, productionService);
        loadBudgets(studioService);
        loadReviews(studioService);

        // If no reviews exist on disk yet, seed initial reviews and persist
        File reviewFile = new File(dataDirectory, "reviews.txt");
        if (!reviewFile.exists() || reviewFile.length() == 0) {
            com.cineflow.util.DataGenerator.seedReviews(studioService);
            try {
                saveReviews(studioService);
            } catch (IOException ignored) {}
        }

        ProductionHouse active = studioService.getActiveStudio();
        if (active != null) {
            productionService.setActiveMovieId(active.getActiveMovieId());
            productionService.refreshPriorityQueue();
        }
    }

    // =========================================================================
    // 1. STUDIOS PERSISTENCE (studios.txt)
    // =========================================================================
    private void saveStudios(StudioService studioService) throws IOException {
        File file = new File(dataDirectory, "studios.txt");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8)))) {
            pw.println("# ID|Name|Headquarters|EstablishedYear|ActiveMovieId");
            for (ProductionHouse ph : studioService.getAllStudios()) {
                pw.printf("%s|%s|%s|%d|%s\n",
                        ph.getId(),
                        ph.getName(),
                        ph.getHeadquarters(),
                        ph.getEstablishedYear(),
                        ph.getActiveMovieId() != null ? ph.getActiveMovieId() : "");
            }
            pw.flush();
        }
    }

    private void loadStudios(StudioService studioService) throws IOException {
        File file = new File(dataDirectory, "studios.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|", -1);
                if (parts.length >= 4) {
                    String id = parts[0];
                    String name = parts[1];
                    String hq = parts[2];
                    int year = 2000;
                    try {
                        year = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException ignored) {}

                    ProductionHouse ph = new ProductionHouse(id, name, hq, year);
                    if (parts.length >= 5 && !parts[4].isEmpty()) {
                        ph.setActiveMovieId(parts[4]);
                    }
                    studioService.registerStudio(ph);
                }
            }
        }
    }

    // =========================================================================
    // 2. MOVIES PERSISTENCE (movies.txt)
    // =========================================================================
    private void saveMovies(StudioService studioService) throws IOException {
        File file = new File(dataDirectory, "movies.txt");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8)))) {
            pw.println("# MovieID|StudioID|Title|Genre|Director|Phase|ReleaseYear");
            for (ProductionHouse ph : studioService.getAllStudios()) {
                for (Movie m : ph.getAllMovies()) {
                    pw.printf("%s|%s|%s|%s|%s|%s|%d\n",
                            m.getId(),
                            ph.getId(),
                            m.getTitle(),
                            m.getGenre(),
                            m.getDirectorName(),
                            m.getProductionPhase().name(),
                            m.getEstimatedReleaseYear());
                }
            }
            pw.flush();
        }
    }

    private void loadMovies(StudioService studioService) throws IOException {
        File file = new File(dataDirectory, "movies.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|", -1);
                if (parts.length >= 7) {
                    String id = parts[0];
                    String studioId = parts[1];
                    String title = parts[2];
                    String genre = parts[3];
                    String director = parts[4];
                    ProductionPhase phase = ProductionPhase.PRE_PRODUCTION;
                    try {
                        phase = ProductionPhase.valueOf(parts[5]);
                    } catch (IllegalArgumentException ignored) {}

                    int year = 2026;
                    try {
                        year = Integer.parseInt(parts[6]);
                    } catch (NumberFormatException ignored) {}

                    Movie movie = new Movie(id, studioId, title, genre, director, phase, year);
                    Optional<ProductionHouse> studioOpt = studioService.getStudio(studioId);
                    if (studioOpt.isPresent()) {
                        studioOpt.get().addMovie(movie);
                    }
                }
            }
        }
    }

    // =========================================================================
    // 3. SCENES & SCRIPTS PERSISTENCE (scenes_script.txt)
    // =========================================================================
    private void saveScenesAndScripts(ProductionService productionService) throws IOException {
        File file = new File(dataDirectory, "scenes_script.txt");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8)))) {
            for (Scene s : productionService.getAllScenesGlobal()) {
                pw.println("###SCENE_START###");
                pw.println("id=" + s.getId());
                pw.println("movieId=" + (s.getMovieId() != null ? s.getMovieId() : ""));
                pw.println("sceneNumber=" + s.getSceneNumber());
                pw.println("title=" + s.getTitle());
                pw.println("scriptPages=" + s.getScriptPages());
                pw.println("daylight=" + s.getDaylightRequirement().name());
                pw.println("priority=" + s.getPriority());
                pw.println("status=" + s.getStatus().name());
                pw.println("estimatedHours=" + s.getEstimatedShootHours());
                pw.println("locationId=" + (s.getLocationId() != null ? s.getLocationId() : ""));
                pw.println("scheduledDate=" + (s.getScheduledDate() != null ? s.getScheduledDate().toString() : ""));
                pw.println("timeSlot=" + (s.getShootTimeSlot() != null ? s.getShootTimeSlot() : ""));
                pw.println("actors=" + String.join(",", s.getRequiredActorIds()));
                pw.println("equipment=" + String.join(",", s.getRequiredEquipmentIds()));
                pw.println("synopsis=" + s.getSynopsis());
                pw.println("scriptContent=");
                if (s.getScriptContent() != null && !s.getScriptContent().trim().isEmpty()) {
                    pw.println(s.getScriptContent().trim());
                }
                pw.println("###SCENE_END###");
                pw.println();
            }
            pw.flush();
        }
    }

    private void loadScenesAndScripts(StudioService studioService, ProductionService productionService)
            throws IOException {
        File file = new File(dataDirectory, "scenes_script.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if ("###SCENE_START###".equals(line.trim())) {
                    String id = "";
                    String movieId = "";
                    int sceneNum = 1;
                    String title = "Untitled";
                    double pages = 1.0;
                    DaylightRequirement daylight = DaylightRequirement.INTERIOR_STUDIO;
                    int priority = 3;
                    SceneStatus status = SceneStatus.DRAFT;
                    double hours = 4.0;
                    String locId = "";
                    LocalDate schedDate = null;
                    String timeSlot = "";
                    List<String> actorList = new ArrayList<>();
                    List<String> eqList = new ArrayList<>();
                    String synopsis = "";
                    StringBuilder scriptSb = new StringBuilder();
                    boolean inScript = false;

                    while ((line = br.readLine()) != null) {
                        if ("###SCENE_END###".equals(line.trim())) {
                            break;
                        }
                        if (inScript) {
                            scriptSb.append(line).append("\n");
                        } else if (line.startsWith("scriptContent=")) {
                            inScript = true;
                            String rem = line.substring("scriptContent=".length());
                            if (!rem.trim().isEmpty()) {
                                scriptSb.append(rem).append("\n");
                            }
                        } else if (line.startsWith("id=")) {
                            id = line.substring(3).trim();
                        } else if (line.startsWith("movieId=")) {
                            movieId = line.substring(8).trim();
                        } else if (line.startsWith("sceneNumber=")) {
                            try { sceneNum = Integer.parseInt(line.substring(12).trim()); } catch (Exception ignored) {}
                        } else if (line.startsWith("title=")) {
                            title = line.substring(6).trim();
                        } else if (line.startsWith("scriptPages=")) {
                            try { pages = Double.parseDouble(line.substring(12).trim()); } catch (Exception ignored) {}
                        } else if (line.startsWith("daylight=")) {
                            try { daylight = DaylightRequirement.valueOf(line.substring(9).trim()); } catch (Exception ignored) {}
                        } else if (line.startsWith("priority=")) {
                            try { priority = Integer.parseInt(line.substring(9).trim()); } catch (Exception ignored) {}
                        } else if (line.startsWith("status=")) {
                            try { status = SceneStatus.valueOf(line.substring(7).trim()); } catch (Exception ignored) {}
                        } else if (line.startsWith("estimatedHours=")) {
                            try { hours = Double.parseDouble(line.substring(15).trim()); } catch (Exception ignored) {}
                        } else if (line.startsWith("locationId=")) {
                            locId = line.substring(11).trim();
                        } else if (line.startsWith("scheduledDate=")) {
                            String dStr = line.substring(14).trim();
                            if (!dStr.isEmpty()) {
                                try { schedDate = LocalDate.parse(dStr); } catch (Exception ignored) {}
                            }
                        } else if (line.startsWith("timeSlot=")) {
                            timeSlot = line.substring(9).trim();
                        } else if (line.startsWith("actors=")) {
                            String aStr = line.substring(7).trim();
                            if (!aStr.isEmpty()) {
                                for (String a : aStr.split(",")) {
                                    if (!a.trim().isEmpty()) actorList.add(a.trim());
                                }
                            }
                        } else if (line.startsWith("equipment=")) {
                            String eStr = line.substring(10).trim();
                            if (!eStr.isEmpty()) {
                                for (String eq : eStr.split(",")) {
                                    if (!eq.trim().isEmpty()) eqList.add(eq.trim());
                                }
                            }
                        } else if (line.startsWith("synopsis=")) {
                            synopsis = line.substring(9).trim();
                        }
                    }

                    if (!id.isEmpty()) {
                        Scene sc = new Scene(id, movieId, sceneNum, title, synopsis, scriptSb.toString().trim(),
                                pages, daylight, priority, hours);
                        sc.setStatus(status);
                        if (schedDate != null) sc.setScheduledDate(schedDate);
                        if (!timeSlot.isEmpty()) sc.setShootTimeSlot(timeSlot);
                        if (!locId.isEmpty()) sc.setLocationId(locId);
                        for (String a : actorList) sc.assignActor(a);
                        for (String eq : eqList) sc.assignEquipment(eq);

                        try {
                            productionService.addScene(sc);
                        } catch (ValidationException ignored) {}

                        // Attach to movie in studio catalog
                        for (ProductionHouse ph : studioService.getAllStudios()) {
                            Optional<Movie> mOpt = ph.getMovie(movieId);
                            if (mOpt.isPresent()) {
                                mOpt.get().addScene(sc);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // 4. BUDGETS PERSISTENCE (budgets.txt)
    // =========================================================================
    private void saveBudgets(StudioService studioService) throws IOException {
        File file = new File(dataDirectory, "budgets.txt");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8)))) {
            for (ProductionHouse ph : studioService.getAllStudios()) {
                for (Movie m : ph.getAllMovies()) {
                    BudgetService bs = m.getBudgetService();
                    pw.println("###BUDGET_START###");
                    pw.println("movieId=" + m.getId());

                    for (Department dept : Department.values()) {
                        double alloc = bs.getAllocatedForDepartment(dept);
                        if (alloc > 0) {
                            pw.println("alloc:" + dept.name() + "=" + alloc);
                        }
                    }

                    for (BudgetService.ExpenseRecord er : bs.getExpenseHistory()) {
                        pw.printf("expense:%s|%s|%.2f|%s|%s\n",
                                er.getTransactionId(),
                                er.getDepartment().name(),
                                er.getAmount(),
                                er.getDescription(),
                                er.getApprovedBy());
                    }
                    pw.println("###BUDGET_END###");
                    pw.println();
                }
            }
            pw.flush();
        }
    }

    private void loadBudgets(StudioService studioService) throws IOException {
        File file = new File(dataDirectory, "budgets.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if ("###BUDGET_START###".equals(line.trim())) {
                    String movieId = "";
                    Movie targetMovie = null;

                    while ((line = br.readLine()) != null) {
                        if ("###BUDGET_END###".equals(line.trim())) {
                            break;
                        }
                        if (line.startsWith("movieId=")) {
                            movieId = line.substring(8).trim();
                            for (ProductionHouse ph : studioService.getAllStudios()) {
                                Optional<Movie> mOpt = ph.getMovie(movieId);
                                if (mOpt.isPresent()) {
                                    targetMovie = mOpt.get();
                                    break;
                                }
                            }
                        } else if (targetMovie != null && line.startsWith("alloc:")) {
                            String rem = line.substring(6);
                            int eqIdx = rem.indexOf('=');
                            if (eqIdx > 0) {
                                String deptStr = rem.substring(0, eqIdx).trim();
                                double amt = Double.parseDouble(rem.substring(eqIdx + 1).trim());
                                try {
                                    Department dept = Department.valueOf(deptStr);
                                    targetMovie.getBudgetService().allocateBudget(dept, amt);
                                } catch (Exception ignored) {}
                            }
                        } else if (targetMovie != null && line.startsWith("expense:")) {
                            String rem = line.substring(8);
                            String[] parts = rem.split("\\|", -1);
                            if (parts.length >= 5) {
                                try {
                                    Department dept = Department.valueOf(parts[1]);
                                    double amt = Double.parseDouble(parts[2]);
                                    String desc = parts[3];
                                    String approver = parts[4];
                                    targetMovie.getBudgetService().logExpense(dept, amt, desc, approver);
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // 5. REVIEWS PERSISTENCE (reviews.txt)
    // =========================================================================
    public synchronized void saveReviews(StudioService studioService) throws IOException {
        File file = new File(dataDirectory, "reviews.txt");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8)))) {
            pw.println("# ReviewID|MovieID|ReviewerName|Rating|ReviewDate|Comment");
            for (ProductionHouse ph : studioService.getAllStudios()) {
                for (Movie m : ph.getAllMovies()) {
                    for (Review r : m.getReviews()) {
                        String safeComment = r.getComment().replace("\r\n", " ").replace("\n", " ").replace("|", "-");
                        String safeReviewer = r.getReviewerName().replace("|", "-");
                        pw.printf("%s|%s|%s|%d|%s|%s\n",
                                r.getId(),
                                m.getId(),
                                safeReviewer,
                                r.getRating(),
                                r.getReviewDate().toString(),
                                safeComment);
                    }
                }
            }
            pw.flush();
        }
    }

    public synchronized void loadReviews(StudioService studioService) throws IOException {
        File file = new File(dataDirectory, "reviews.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|", 6);
                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    String movieId = parts[1].trim();
                    String reviewerName = parts[2].trim();
                    int rating = 5;
                    try {
                        rating = Integer.parseInt(parts[3].trim());
                    } catch (NumberFormatException ignored) {}
                    LocalDate date = LocalDate.now();
                    try {
                        date = LocalDate.parse(parts[4].trim());
                    } catch (Exception ignored) {}
                    String comment = parts.length >= 6 ? parts[5].trim() : "";

                    Review review = new Review(id, movieId, reviewerName, rating, comment, date);
                    Optional<Movie> movieOpt = studioService.findMovieById(movieId);
                    movieOpt.ifPresent(m -> m.addReview(review));
                }
            }
        }
    }

    // =========================================================================
    // 6. SCREENPLAY EXPORT (data/scripts/<Movie>_Screenplay.txt)
    // =========================================================================
    private void exportAllMovieScreenplays(StudioService studioService, ProductionService productionService)
            throws IOException {
        for (ProductionHouse ph : studioService.getAllStudios()) {
            for (Movie m : ph.getAllMovies()) {
                List<Scene> scenes = m.getScenes();
                if (!scenes.isEmpty()) {
                    String safeTitle = m.getTitle().replaceAll("[^a-zA-Z0-9_-]", "_");
                    String path = dataDirectory + "/scripts/" + m.getId() + "_" + safeTitle + "_Screenplay.txt";
                    exportMovieScreenplayToFile(m, ph.getName(), scenes, path);
                }
            }
        }
    }

    public void exportMovieScreenplayToFile(Movie movie, String studioName, List<Scene> scenes, String filePath)
            throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8)))) {
            pw.println("========================================================================");
            pw.printf("                    FEATURE FILM SCREENPLAY & SCRIPT                     \n");
            pw.printf("                    TITLE: %s\n", movie.getTitle().toUpperCase());
            pw.println("========================================================================");
            pw.printf("Studio / Production House : %s\n", studioName != null ? studioName : "Horizon Studios");
            pw.printf("Movie ID                  : %s\n", movie.getId());
            pw.printf("Genre                     : %s\n", movie.getGenre());
            pw.printf("Director                  : %s\n", movie.getDirectorName());
            pw.printf("Production Phase          : %s\n", movie.getProductionPhase().getDescription());
            pw.printf("Estimated Release Year    : %d\n", movie.getEstimatedReleaseYear());
            pw.printf("Total Script Breakdown    : %d Scenes Registered\n", scenes.size());
            pw.println("========================================================================\n");

            for (Scene sc : scenes) {
                pw.printf("------------------------------------------------------------------------\n");
                pw.printf("SCENE #%02d - %s\n", sc.getSceneNumber(), sc.getTitle().toUpperCase());
                pw.printf("LIGHTING / ENVIRONMENT: %s | PRIORITY: Level %d\n",
                        sc.getDaylightRequirement().getDescription(), sc.getPriority());
                pw.printf("PAGES: %.1f Pages | ESTIMATED SHOOT TIME: %.1f Hours | STATUS: %s\n",
                        sc.getScriptPages(), sc.getEstimatedShootHours(), sc.getStatus().name());
                pw.printf("SCHEDULED: %s (%s) | LOCATION ASSET: %s\n",
                        sc.getScheduledDate() != null ? sc.getScheduledDate().toString() : "TBD",
                        sc.getShootTimeSlot() != null ? sc.getShootTimeSlot() : "TBD",
                        sc.getLocationId() != null ? sc.getLocationId() : "Unassigned");
                pw.printf("CAST REQUIRED: %s\n", sc.getRequiredActorIds().isEmpty() ? "None" : String.join(", ", sc.getRequiredActorIds()));
                pw.printf("EQUIPMENT REQUIRED: %s\n", sc.getRequiredEquipmentIds().isEmpty() ? "None" : String.join(", ", sc.getRequiredEquipmentIds()));
                pw.printf("\nSYNOPSIS:\n%s\n", sc.getSynopsis());

                pw.printf("\nSCREENPLAY ACTION & DIALOGUE:\n");
                if (sc.getScriptContent() != null && !sc.getScriptContent().trim().isEmpty()) {
                    pw.println(sc.getScriptContent().trim());
                } else {
                    pw.println("  (Dialogue and action description pending rehearsal breakdown)");
                }
                pw.println();
            }
            pw.println("========================================================================");
            pw.println("                         END OF SCRIPT BREAKDOWN                        ");
            pw.println("========================================================================");
            pw.flush();
        }
    }

    public String readTextFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
