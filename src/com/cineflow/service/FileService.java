package com.cineflow.service;

import com.cineflow.model.Actor;
import com.cineflow.model.CallSheet;
import com.cineflow.model.CastCallEntry;
import com.cineflow.model.CrewMember;
import com.cineflow.model.DistributionRelease;
import com.cineflow.model.DubbingTrack;
import com.cineflow.model.Expense;
import com.cineflow.model.Movie;
import com.cineflow.model.MovieStatus;
import com.cineflow.model.Person;
import com.cineflow.model.PostProductionTask;
import com.cineflow.model.PostProductionType;
import com.cineflow.model.ProductionHouse;
import com.cineflow.model.ProductionMilestone;
import com.cineflow.model.ProductionStage;
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
     * Saves all production houses, movies, scenes, cast/crew, expenses, reviews, schedules, dubbing, post-production, screenplays, call sheets, and distribution to text files.
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
        saveSchedules(studios);
        saveDubbing(studios);
        savePostProduction(studios);
        saveScreenplays(studios);
        saveCallSheets(studios);
        saveDistribution(studios);
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
            writer.println("# MovieID|StudioID|Title|Genre|Director|ReleaseYear|Budget|Status|ScreenplayWriter|ScriptDraft|Synopsis");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    String syn = (m.getSynopsis() != null) ? m.getSynopsis().replace("\n", "\\n") : "";
                    writer.println(m.getId() + "|" + ph.getId() + "|" + m.getTitle() + "|"
                            + m.getGenre() + "|" + m.getDirector() + "|"
                            + m.getReleaseYear() + "|" + m.getBudget() + "|"
                            + m.getStatus().name() + "|"
                            + m.getScreenplayWriter() + "|"
                            + m.getScriptDraftVersion() + "|"
                            + syn);
                }
            }
        }
    }

    private void saveScenes(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "scenes.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|SceneNumber|Title|Location|Status|ShootingDate|Slugline|ScriptPages|ScriptContent");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (Scene sc : m.getScenes()) {
                        String content = (sc.getScriptContent() != null) ? sc.getScriptContent().replace("\n", "\\n") : "";
                        writer.println(m.getId() + "|" + sc.getSceneNumber() + "|"
                                + sc.getTitle() + "|" + sc.getLocation() + "|"
                                + sc.getStatus() + "|" + sc.getShootingDate() + "|"
                                + sc.getSlugline() + "|" + sc.getScriptPages() + "|"
                                + content);
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

    private void saveSchedules(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "schedules.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|MilestoneID|Stage|Title|TargetDate|Status|ResponsibleLead|IsOptional|Notes");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (ProductionMilestone ms : m.getMasterSchedule()) {
                        String notesSafe = (ms.getNotes() != null) ? ms.getNotes().replace("\n", "\\n") : "";
                        writer.println(m.getId() + "|" + ms.getId() + "|" + ms.getStage().name() + "|"
                                + ms.getTitle() + "|" + ms.getTargetDate() + "|" + ms.getStatus() + "|"
                                + ms.getResponsibleLead() + "|" + ms.isOptional() + "|" + notesSafe);
                    }
                }
            }
        }
    }

    private void saveDubbing(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "dubbing.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|TrackID|CharacterName|VoiceArtist|Language|Status|RetakeCount|RetakeReason|StudioLocation");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (DubbingTrack dt : m.getDubbingTracks()) {
                        String retakeSafe = (dt.getRetakeReason() != null) ? dt.getRetakeReason().replace("\n", " ").replace("|", "/") : "";
                        writer.println(m.getId() + "|" + dt.getTrackId() + "|" + dt.getCharacterName() + "|"
                                + dt.getVoiceArtist() + "|" + dt.getLanguage() + "|" + dt.getStatus() + "|"
                                + dt.getRetakeCount() + "|" + retakeSafe + "|" + dt.getStudioLocation());
                    }
                }
            }
        }
    }

    private void savePostProduction(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "post_production.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|TaskID|Type|TaskName|LeadSpecialist|Status|ProgressPercentage|TechnicalSpecs|Notes");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (PostProductionTask pt : m.getPostProductionTasks()) {
                        String notesSafe = (pt.getNotes() != null) ? pt.getNotes().replace("\n", " ").replace("|", "/") : "";
                        writer.println(m.getId() + "|" + pt.getTaskId() + "|" + pt.getType().name() + "|"
                                + pt.getTaskName() + "|" + pt.getLeadSpecialist() + "|" + pt.getStatus() + "|"
                                + pt.getProgressPercentage() + "|" + pt.getTechnicalSpecs() + "|" + notesSafe);
                    }
                }
            }
        }
    }

    private void saveScreenplays(List<ProductionHouse> studios) throws IOException {
        File scriptsDir = new File(dataDir, "scripts");
        if (!scriptsDir.exists()) {
            scriptsDir.mkdirs();
        }
        for (ProductionHouse ph : studios) {
            for (Movie m : ph.getMovies()) {
                exportScreenplayToFile(m);
            }
        }
    }

    private void saveCallSheets(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "call_sheets.txt");
        File csDir = new File(dataDir, "call_sheets");
        if (!csDir.exists()) {
            csDir.mkdirs();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|SheetID|DayNumber|TotalDays|ShootDate|GeneralCrewCall|Location|Weather|Hospital|Status|ScheduledScenes|PagesShot|WrapNotes|DeptNotes|CastCalls");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (CallSheet cs : m.getCallSheets()) {
                        StringBuilder scSb = new StringBuilder();
                        for (int i = 0; i < cs.getScheduledSceneNumbers().size(); i++) {
                            if (i > 0) scSb.append(";");
                            scSb.append(cs.getScheduledSceneNumbers().get(i));
                        }
                        String wrapSafe = (cs.getWrapNotes() != null) ? cs.getWrapNotes().replace("\n", "\\n").replace("|", "/") : "";
                        String deptSafe = (cs.getDepartmentNotes() != null) ? cs.getDepartmentNotes().replace("\n", "\\n").replace("|", "/") : "";
                        StringBuilder castSb = new StringBuilder();
                        for (int i = 0; i < cs.getCastCalls().size(); i++) {
                            if (i > 0) castSb.append("~");
                            castSb.append(cs.getCastCalls().get(i).toSerializedString());
                        }
                        writer.println(m.getId() + "|" + cs.getSheetId() + "|" + cs.getDayNumber() + "|"
                                + cs.getTotalDays() + "|" + cs.getShootDate() + "|" + cs.getGeneralCrewCall() + "|"
                                + cs.getShootingLocation().replace("|", "/") + "|"
                                + cs.getWeatherAndSun().replace("|", "/") + "|"
                                + cs.getNearestHospital().replace("|", "/") + "|"
                                + cs.getStatus() + "|" + scSb.toString() + "|" + cs.getPagesShot() + "|"
                                + wrapSafe + "|" + deptSafe + "|" + castSb.toString());

                        // Also auto-export individual formatted call sheet text document
                        exportCallSheetToFile(m, cs);
                    }
                }
            }
        }
    }

    private void saveDistribution(List<ProductionHouse> studios) throws IOException {
        File file = new File(dataDir, "distribution.txt");
        File distDir = new File(dataDir, "distribution");
        if (!distDir.exists()) {
            distDir.mkdirs();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("# MovieID|ReleaseID|Region|Distributor|TheatricalDate|TheatricalStatus|ScreenCount|OTTPlatform|DigitalDate|Notes");
            for (ProductionHouse ph : studios) {
                for (Movie m : ph.getMovies()) {
                    for (DistributionRelease dr : m.getDistributionReleases()) {
                        String notesSafe = (dr.getNotes() != null) ? dr.getNotes().replace("\n", " ").replace("|", "/") : "";
                        writer.println(m.getId() + "|" + dr.getReleaseId() + "|"
                                + dr.getRegion().replace("|", "/") + "|"
                                + dr.getDistributor().replace("|", "/") + "|"
                                + dr.getTheatricalReleaseDate() + "|"
                                + dr.getTheatricalStatus() + "|"
                                + dr.getScreenCount() + "|"
                                + dr.getOttPlatform().replace("|", "/") + "|"
                                + dr.getDigitalReleaseDate() + "|"
                                + notesSafe);
                    }
                    exportDistributionToFile(m);
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
                            if (parts.length >= 9 && !parts[8].trim().isEmpty()) {
                                movie.setScreenplayWriter(parts[8].trim());
                            }
                            if (parts.length >= 10 && !parts[9].trim().isEmpty()) {
                                movie.setScriptDraftVersion(parts[9].trim());
                            }
                            if (parts.length >= 11 && !parts[10].trim().isEmpty()) {
                                movie.setSynopsis(parts[10].replace("\\n", "\n").trim());
                            }
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
                            String shootingDate = (parts.length >= 6 && !parts[5].trim().isEmpty()) ? parts[5].trim() : "TBD";
                            String slugline = (parts.length >= 7 && !parts[6].trim().isEmpty()) ? parts[6].trim() : null;
                            double pages = 1.0;
                            if (parts.length >= 8 && !parts[7].trim().isEmpty()) {
                                try {
                                    pages = Double.parseDouble(parts[7].trim());
                                } catch (Exception ignored) {}
                            }
                            String scriptContent = (parts.length >= 9) ? parts[8].replace("\\n", "\n").trim() : "";

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                movie.getScenes().add(new Scene(sceneNum, title, location, status, shootingDate, slugline, pages, scriptContent));
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

        // 7. Load Master Production Schedules
        File scheduleFile = new File(dataDir, "schedules.txt");
        if (scheduleFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(scheduleFile))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 8) {
                        try {
                            String movieId = parts[0].trim();
                            String milestoneId = parts[1].trim();
                            ProductionStage stage = ProductionStage.valueOf(parts[2].trim().toUpperCase());
                            String title = parts[3].trim();
                            String targetDate = parts[4].trim();
                            String status = parts[5].trim();
                            String lead = parts[6].trim();
                            boolean optional = Boolean.parseBoolean(parts[7].trim());
                            String notes = (parts.length >= 9) ? parts[8].replace("\\n", "\n").trim() : "";

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                ProductionMilestone existing = movie.findMilestoneById(milestoneId);
                                if (existing != null) {
                                    existing.setStage(stage);
                                    existing.setTitle(title);
                                    existing.setTargetDate(targetDate);
                                    existing.setStatus(status);
                                    existing.setResponsibleLead(lead);
                                    existing.setOptional(optional);
                                    existing.setNotes(notes);
                                } else {
                                    ProductionMilestone ms = new ProductionMilestone(milestoneId, stage, title, targetDate, status, lead, optional, notes);
                                    movie.getMasterSchedule().add(ms);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] schedules.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] schedules.txt line " + lineNum + " malformed: " + line);
                    }
                }
            }
        }

        // 8. Load Dubbing Tracks
        File dubbingFile = new File(dataDir, "dubbing.txt");
        if (dubbingFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(dubbingFile))) {
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
                            String trackId = parts[1].trim();
                            String characterName = parts[2].trim();
                            String voiceArtist = parts[3].trim();
                            String language = parts[4].trim();
                            String status = parts[5].trim();
                            int retakes = (parts.length >= 7) ? Integer.parseInt(parts[6].trim()) : 0;
                            String retakeReason = (parts.length >= 8) ? parts[7].trim() : "";
                            String studio = (parts.length >= 9) ? parts[8].trim() : "Main Studio";

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                DubbingTrack existing = movie.findDubbingTrackById(trackId);
                                if (existing != null) {
                                    existing.setCharacterName(characterName);
                                    existing.setVoiceArtist(voiceArtist);
                                    existing.setLanguage(language);
                                    existing.setStatus(status);
                                    existing.setRetakeCount(retakes);
                                    existing.setRetakeReason(retakeReason);
                                    existing.setStudioLocation(studio);
                                } else {
                                    movie.addDubbingTrack(new DubbingTrack(trackId, characterName, voiceArtist, language, status, retakes, retakeReason, studio));
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] dubbing.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] dubbing.txt line " + lineNum + " malformed: " + line);
                    }
                }
            }
        }

        // 9. Load Post-Production Tasks
        File postFile = new File(dataDir, "post_production.txt");
        if (postFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(postFile))) {
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
                            String taskId = parts[1].trim();
                            PostProductionType type = PostProductionType.valueOf(parts[2].trim().toUpperCase());
                            String taskName = parts[3].trim();
                            String lead = parts[4].trim();
                            String status = parts[5].trim();
                            int progress = (parts.length >= 7) ? Integer.parseInt(parts[6].trim()) : 0;
                            String specs = (parts.length >= 8) ? parts[7].trim() : "Standard Spec";
                            String notes = (parts.length >= 9) ? parts[8].trim() : "";

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                PostProductionTask existing = movie.findPostProductionTaskById(taskId);
                                if (existing != null) {
                                    existing.setType(type);
                                    existing.setTaskName(taskName);
                                    existing.setLeadSpecialist(lead);
                                    existing.setStatus(status);
                                    existing.setProgressPercentage(progress);
                                    existing.setTechnicalSpecs(specs);
                                    existing.setNotes(notes);
                                } else {
                                    movie.addPostProductionTask(new PostProductionTask(taskId, type, taskName, lead, status, progress, specs, notes));
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] post_production.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] post_production.txt line " + lineNum + " malformed: " + line);
                    }
                }
            }
        }

        // 10. Load Call Sheets
        File csFile = new File(dataDir, "call_sheets.txt");
        if (csFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(csFile))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|", -1);
                    if (parts.length >= 10) {
                        try {
                            String movieId = parts[0].trim();
                            String sheetId = parts[1].trim();
                            int dayNum = Integer.parseInt(parts[2].trim());
                            int totalDays = Integer.parseInt(parts[3].trim());
                            String shootDate = parts[4].trim();
                            String crewCall = parts[5].trim();
                            String loc = parts[6].trim();
                            String weather = parts[7].trim();
                            String hospital = parts[8].trim();
                            String status = parts[9].trim();
                            String scenesStr = (parts.length >= 11) ? parts[10].trim() : "";
                            double pagesShot = (parts.length >= 12 && !parts[11].trim().isEmpty()) ? Double.parseDouble(parts[11].trim()) : 0.0;
                            String wrapNotes = (parts.length >= 13) ? parts[12].replace("\\n", "\n").trim() : "";
                            String deptNotes = (parts.length >= 14) ? parts[13].replace("\\n", "\n").trim() : "";
                            String castCallsStr = (parts.length >= 15) ? parts[14].trim() : "";

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                CallSheet existing = movie.findCallSheetById(sheetId);
                                CallSheet cs = (existing != null) ? existing : new CallSheet(sheetId, dayNum, totalDays, shootDate, crewCall, loc, weather, hospital, status);
                                cs.setDayNumber(dayNum);
                                cs.setTotalDays(totalDays);
                                cs.setShootDate(shootDate);
                                cs.setGeneralCrewCall(crewCall);
                                cs.setShootingLocation(loc);
                                cs.setWeatherAndSun(weather);
                                cs.setNearestHospital(hospital);
                                cs.setStatus(status);
                                cs.setPagesShot(pagesShot);
                                cs.setWrapNotes(wrapNotes);
                                if (!deptNotes.isEmpty()) cs.setDepartmentNotes(deptNotes);

                                cs.getScheduledSceneNumbers().clear();
                                if (!scenesStr.isEmpty()) {
                                    for (String sNum : scenesStr.split(";")) {
                                        try {
                                            if (!sNum.trim().isEmpty()) cs.addScheduledScene(Integer.parseInt(sNum.trim()));
                                        } catch (Exception ignored) {}
                                    }
                                }

                                cs.getCastCalls().clear();
                                if (!castCallsStr.isEmpty()) {
                                    for (String cData : castCallsStr.split("~")) {
                                        CastCallEntry cEntry = CastCallEntry.fromSerializedString(cData);
                                        if (cEntry != null) cs.addCastCall(cEntry);
                                    }
                                }

                                if (existing == null) {
                                    movie.addCallSheet(cs);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] call_sheets.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] call_sheets.txt line " + lineNum + " malformed: " + line);
                    }
                }
            }
        }

        // 11. Load Distribution Deals
        File distFile = new File(dataDir, "distribution.txt");
        if (distFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(distFile))) {
                String line;
                int lineNum = 0;
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\|", -1);
                    if (parts.length >= 6) {
                        try {
                            String movieId = parts[0].trim();
                            String relId = parts[1].trim();
                            String reg = parts[2].trim();
                            String dist = parts[3].trim();
                            String theDate = parts[4].trim();
                            String theStatus = parts[5].trim();
                            int screens = (parts.length >= 7 && !parts[6].trim().isEmpty()) ? Integer.parseInt(parts[6].trim()) : 0;
                            String ott = (parts.length >= 8) ? parts[7].trim() : "TBD / Pending Deal";
                            String digDate = (parts.length >= 9) ? parts[8].trim() : "TBD";
                            String notes = (parts.length >= 10) ? parts[9].trim() : "";

                            Movie movie = movieMap.get(movieId);
                            if (movie != null) {
                                DistributionRelease existing = movie.findDistributionReleaseById(relId);
                                if (existing != null) {
                                    existing.setRegion(reg);
                                    existing.setDistributor(dist);
                                    existing.setTheatricalReleaseDate(theDate);
                                    existing.setTheatricalStatus(theStatus);
                                    existing.setScreenCount(screens);
                                    existing.setOttPlatform(ott);
                                    existing.setDigitalReleaseDate(digDate);
                                    existing.setNotes(notes);
                                } else {
                                    movie.addDistributionRelease(new DistributionRelease(relId, reg, dist, theDate, theStatus, screens, ott, digDate, notes));
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("[WARNING] distribution.txt line " + lineNum + " skipped: " + e.getMessage());
                        }
                    } else {
                        System.err.println("[WARNING] distribution.txt line " + lineNum + " malformed: " + line);
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

    /**
     * Exports a complete formatted screenplay for a movie to data/scripts/<MovieID>_screenplay.txt.
     */
    public File exportScreenplayToFile(Movie movie) throws IOException {
        if (movie == null) {
            throw new IOException("Cannot export null movie screenplay.");
        }
        File scriptsDir = new File(dataDir, "scripts");
        if (!scriptsDir.exists()) {
            scriptsDir.mkdirs();
        }
        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        File targetFile = new File(scriptsDir, movie.getId() + "_" + safeTitle + "_Screenplay.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile))) {
            pw.print(movie.getFormattedScreenplay());
        }
        return targetFile;
    }

    /**
     * Retrieves the file path handle to the movie's dedicated screenplay document.
     */
    public File getScreenplayFile(Movie movie) {
        if (movie == null) return null;
        File scriptsDir = new File(dataDir, "scripts");
        if (!scriptsDir.exists()) {
            scriptsDir.mkdirs();
        }
        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        return new File(scriptsDir, movie.getId() + "_" + safeTitle + "_Screenplay.txt");
    }

    /**
     * Reads the formatted screenplay directly from the movie's saved text document.
     * If the document does not exist yet, it is generated and read.
     */
    public String readScreenplayFromFile(Movie movie) throws IOException {
        File scriptFile = getScreenplayFile(movie);
        if (scriptFile == null || !scriptFile.exists() || scriptFile.length() == 0) {
            exportScreenplayToFile(movie);
            scriptFile = getScreenplayFile(movie);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Exports the master production & release roadmap to data/schedules/<MovieID>_Schedule.txt.
     */
    public File exportScheduleToFile(Movie movie) throws IOException {
        if (movie == null) {
            throw new IOException("Cannot export null movie schedule.");
        }
        File schedulesDir = new File(dataDir, "schedules");
        if (!schedulesDir.exists()) {
            schedulesDir.mkdirs();
        }
        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        File targetFile = new File(schedulesDir, movie.getId() + "_" + safeTitle + "_Schedule.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile))) {
            pw.print(movie.getFormattedTimeline());
            List<String> warnings = movie.validateScheduleSequence();
            if (!warnings.isEmpty()) {
                pw.println("\n[CHRONOLOGY AUDIT WARNINGS]");
                for (String w : warnings) {
                    pw.println("  ! " + w);
                }
            }
        }
        return targetFile;
    }

    /**
     * Exports the multi-language voice dubbing cue manifest to data/dubbing/<MovieID>_Dubbing_Manifest.txt.
     */
    public File exportDubbingToFile(Movie movie) throws IOException {
        if (movie == null) {
            throw new IOException("Cannot export null movie dubbing manifest.");
        }
        File dubbingDir = new File(dataDir, "dubbing");
        if (!dubbingDir.exists()) {
            dubbingDir.mkdirs();
        }
        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        File targetFile = new File(dubbingDir, movie.getId() + "_" + safeTitle + "_Dubbing_Manifest.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile))) {
            pw.print(movie.getFormattedDubbingSummary());
        }
        return targetFile;
    }

    /**
     * Exports the post-production workflow report (Editing, Sound Mixing, VFX, Rendering) to data/post_production/<MovieID>_PostProduction_Manifest.txt.
     */
    public File exportPostProductionToFile(Movie movie) throws IOException {
        if (movie == null) {
            throw new IOException("Cannot export null movie post-production manifest.");
        }
        File postDir = new File(dataDir, "post_production");
        if (!postDir.exists()) {
            postDir.mkdirs();
        }
        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        File targetFile = new File(postDir, movie.getId() + "_" + safeTitle + "_PostProduction_Manifest.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile))) {
            pw.print(movie.getFormattedPostProductionDashboard());
        }
        return targetFile;
    }

    /**
     * Exports a single daily production call sheet to data/call_sheets/<MovieID>_<Title>_CallSheet_Day<DayNumber>.txt.
     */
    public File exportCallSheetToFile(Movie movie, CallSheet sheet) throws IOException {
        if (movie == null || sheet == null) {
            throw new IOException("Cannot export null movie or call sheet.");
        }
        File csDir = new File(dataDir, "call_sheets");
        if (!csDir.exists()) {
            csDir.mkdirs();
        }
        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        File targetFile = new File(csDir, movie.getId() + "_" + safeTitle + "_CallSheet_Day" + sheet.getDayNumber() + ".txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile))) {
            pw.print(sheet.getFormattedCallSheet(movie));
        }
        return targetFile;
    }

    /**
     * Retrieves the file handle for a movie's daily call sheet text document.
     */
    public File getCallSheetFile(Movie movie, CallSheet sheet) {
        if (movie == null || sheet == null) return null;
        File csDir = new File(dataDir, "call_sheets");
        if (!csDir.exists()) {
            csDir.mkdirs();
        }
        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        return new File(csDir, movie.getId() + "_" + safeTitle + "_CallSheet_Day" + sheet.getDayNumber() + ".txt");
    }

    /**
     * Exports a comprehensive theatrical and OTT distribution manifest report to data/distribution/<MovieID>_<Title>_Distribution_Manifest.txt.
     */
    public File exportDistributionToFile(Movie movie) throws IOException {
        if (movie == null) {
            throw new IOException("Cannot export null movie distribution manifest.");
        }
        File distDir = new File(dataDir, "distribution");
        if (!distDir.exists()) {
            distDir.mkdirs();
        }
        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        File targetFile = new File(distDir, movie.getId() + "_" + safeTitle + "_Distribution_Manifest.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile))) {
            pw.print(movie.getFormattedDistributionSummary());
        }
        return targetFile;
    }

    public File getDistributionFile(Movie movie) {
        if (movie == null) return null;
        File distDir = new File(dataDir, "distribution");
        if (!distDir.exists()) {
            distDir.mkdirs();
        }
        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        return new File(distDir, movie.getId() + "_" + safeTitle + "_Distribution_Manifest.txt");
    }
}

