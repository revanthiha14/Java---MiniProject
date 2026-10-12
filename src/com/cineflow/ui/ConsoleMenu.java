package com.cineflow.ui;

import com.cineflow.exception.BudgetExceededException;
import com.cineflow.exception.CineFlowException;
import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ScheduleConflictException;
import com.cineflow.exception.ValidationException;
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
import com.cineflow.model.ApprovalStatus;
import com.cineflow.model.AssetType;
import com.cineflow.model.InventoryItem;
import com.cineflow.model.ItemAssignment;
import com.cineflow.model.ItemCategory;
import com.cineflow.model.PromotionalAsset;
import com.cineflow.service.CastManager;
import com.cineflow.service.InventoryManager;
import com.cineflow.service.MovieManager;
import com.cineflow.service.PromotionManager;
import com.cineflow.service.ReportService;
import com.cineflow.service.ReviewManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Text-based console user interface for CineFlow.
 * Demonstrates: Console Input Handling (Scanner), Menu Loops, Sub-menus,
 * Formatted Output, Exception Handling (5 custom exceptions), and Graceful Error Recovery.
 */
public class ConsoleMenu {
    private final MovieManager manager;
    private final CastManager castManager;
    private final ReviewManager reviewManager;
    private final InventoryManager inventoryManager;
    private final PromotionManager promotionManager;
    private final ReportService reportService;
    private final Scanner scanner;

    public ConsoleMenu(MovieManager manager) {
        this(manager, manager.getCastManager(), manager.getReviewManager());
    }

    public ConsoleMenu(MovieManager manager, CastManager castManager, ReviewManager reviewManager) {
        this.manager = manager;
        this.castManager = (castManager != null) ? castManager : new CastManager();
        this.reviewManager = (reviewManager != null) ? reviewManager : new ReviewManager();
        this.inventoryManager = (manager != null) ? manager.getInventoryManager() : new InventoryManager();
        this.promotionManager = (manager != null) ? manager.getPromotionManager() : new PromotionManager();
        this.reportService = (manager != null) ? manager.getReportService() : new ReportService(manager, this.inventoryManager, this.promotionManager);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printHeader();
            System.out.println(" 1. View & Switch Active Movie");
            System.out.println(" 2. Add New Movie to Active Studio");
            System.out.println(" 3. Search Movies (ID, Name, Genre, Rating)");
            System.out.println(" 4. Update Movie Status (Lifecycle)");
            System.out.println(" 5. Movie Statistics / Overview");
            System.out.println(" 6. Manage Scenes & Screenplay (Add, Edit, View Scripts)");
            System.out.println(" 7. Master Production & Release Schedule (Full Roadmap)");
            System.out.println(" 8. Daily Call Sheets & Shooting Schedule (Cast Calls, Crew, Emergency)");
            System.out.println(" 9. Voice Dubbing Management (Artists, Languages, Retakes)");
            System.out.println(" 10. Post-Production Management (Editing, Sound, VFX, Rendering)");
            System.out.println(" 11. Theatrical & OTT Distribution (Release Regions, Distributors, Streaming)");
            System.out.println(" 12. Manage Cast & Crew (View, Add Actor/Crew)");
            System.out.println(" 13. Manage Budget & Expenses");
            System.out.println(" 14. Add Movie Review");
            System.out.println(" 15. View Movie Reviews");
            System.out.println(" 16. Manage Production Houses (View, Switch, Add)");
            System.out.println(" 17. Props, Costumes & Equipment (Inventory, Checkout, Return)");
            System.out.println(" 18. Poster & Promotional Assets (Approval Workflow & Revisions)");
            System.out.println(" 19. Production Reports & Executive Dashboard (KPIs & Export)");
            System.out.println(" 20. Save Data to Text Files");
            System.out.println(" 21. Load Data from Text Files");
            System.out.println(" 22. Drop Movie / Cancel Production");
            System.out.println(" 0. Exit");
            System.out.println("==================================================");
            System.out.print("Enter choice (0-22): ");

            int choice = readInt();
            System.out.println();

            switch (choice) {
                case 1 -> viewAndSwitchMovie();
                case 2 -> addNewMovie();
                case 3 -> searchMovies();
                case 4 -> updateMovieStatus();
                case 5 -> viewMovieStatistics();
                case 6 -> manageScenes();
                case 7 -> manageMasterSchedule();
                case 8 -> manageCallSheets();
                case 9 -> manageDubbing();
                case 10 -> managePostProduction();
                case 11 -> manageDistribution();
                case 12 -> manageCastAndCrew();
                case 13 -> manageBudgetAndExpenses();
                case 14 -> addMovieReview();
                case 15 -> viewMovieReviews();
                case 16 -> manageProductionHouses();
                case 17 -> manageInventory();
                case 18 -> managePromotionalAssets();
                case 19 -> viewProductionReports();
                case 20 -> saveData();
                case 21 -> loadData();
                case 22 -> dropMovie();
                case 0 -> {
                    System.out.println("Saving current state before exiting...");
                    try {
                        manager.saveData();
                        System.out.println("Data saved successfully.");
                    } catch (IOException e) {
                        System.err.println("Could not auto-save data: " + e.getMessage());
                    }
                    System.out.println("Thank you for using CineFlow! Goodbye.");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please choose between 0 and 22.");
            }

            if (running) {
                pause();
            }
        }
    }

    private void printHeader() {
        System.out.println("\n==================================================");
        System.out.println("         CINEFLOW - FILM PRODUCTION SYSTEM        ");
        System.out.println("==================================================");
        ProductionHouse studio = manager.getActiveStudio();
        Movie movie = manager.getActiveMovie();

        String studioName = (studio != null) ? studio.getName() + " [" + studio.getId() + "]" : "None Selected";
        String movieTitle = (movie != null)
                ? movie.getTitle() + " [" + movie.getId() + "] (" + movie.getStatus() + ")"
                : "None Selected";

        System.out.println(" Active Studio : " + studioName);
        System.out.println(" Active Movie  : " + movieTitle);
        if (movie != null) {
            System.out.printf(" Screenplay    : %s (Draft: %s | %.1f Pages)%n",
                    movie.getScreenplayWriter(), movie.getScriptDraftVersion(), movie.getTotalScriptPages());
        }
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // 1 & 2. MOVIE MANAGEMENT
    // =========================================================================
    private void viewAndSwitchMovie() {
        ProductionHouse studio = manager.getActiveStudio();
        if (studio == null) {
            System.out.println("Please select a studio first.");
            return;
        }

        System.out.println("--- MOVIES IN " + studio.getName().toUpperCase() + " ---");
        List<Movie> movies = studio.getMovies();
        if (movies.isEmpty()) {
            System.out.println("No movies found under this studio.");
            return;
        }

        Movie activeMovie = manager.getActiveMovie();
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            String mark = (activeMovie != null && m.getId().equalsIgnoreCase(activeMovie.getId())) ? " * [ACTIVE]" : "";
            System.out.printf(" %d. %s%s%n", (i + 1), m.toString(), mark);
        }

        System.out.print("\nEnter Movie ID to switch (or press Enter to keep current): ");
        String id = readLine();
        if (!id.isEmpty()) {
            try {
                manager.setActiveMovieId(id);
                System.out.println("Active Movie switched to: " + manager.getActiveMovie().getTitle());
            } catch (ResourceNotFoundException e) {
                System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
            }
        }
    }

    private void addNewMovie() {
        ProductionHouse studio = manager.getActiveStudio();
        if (studio == null) {
            System.out.println("Please select a studio first.");
            return;
        }

        System.out.println("--- ADD NEW MOVIE TO " + studio.getName().toUpperCase() + " ---");
        System.out.print("Enter Movie ID (e.g., MOV-05): ");
        String id = readLine();
        System.out.print("Enter Movie Title: ");
        String title = readLine();
        System.out.print("Enter Genre: ");
        String genre = readLine();
        System.out.print("Enter Director: ");
        String director = readLine();
        System.out.print("Enter Release Year: ");
        int year = readInt();
        System.out.print("Enter Total Allocated Budget ($): ");
        double budget = readDouble();

        System.out.println("Select Initial Status:");
        System.out.println(" 1. PLANNING (Default)");
        System.out.println(" 2. PRODUCTION");
        System.out.println(" 3. POST_PRODUCTION");
        System.out.println(" 4. RELEASED");
        System.out.print("Enter choice (1-4): ");
        int statusChoice = readInt();
        MovieStatus initialStatus = switch (statusChoice) {
            case 2 -> MovieStatus.PRODUCTION;
            case 3 -> MovieStatus.POST_PRODUCTION;
            case 4 -> MovieStatus.RELEASED;
            default -> MovieStatus.PLANNING;
        };

        try {
            Movie movie = new Movie(id, title, genre, director, year, budget, initialStatus);
            manager.addMovieToActiveStudio(movie);
            manager.autoSyncScreenplayFile(movie);
            System.out.println("\nMovie '" + title + "' added successfully with status: " + initialStatus);
            System.out.println("[DOCUMENT INITIALIZED] Screenplay text document created at: "
                    + manager.getActiveMovieScreenplayFile().getPath());
        } catch (ValidationException e) {
            System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. SEARCH & FILTER MOVIES (BY ID, NAME, GENRE, MIN RATING)
    // =========================================================================
    private void searchMovies() {
        System.out.println("--- SEARCH & FILTER MOVIES ---");
        System.out.println(" 1. Search by Movie ID or Title keyword");
        System.out.println(" 2. Filter by Genre");
        System.out.println(" 3. Filter by Minimum Average Rating");
        System.out.println(" 4. View All Movies Across All Studios");
        System.out.print("Select search type (1-4): ");
        int type = readInt();

        List<Movie> results;
        switch (type) {
            case 1 -> {
                System.out.print("Enter Movie ID or Title keyword: ");
                String query = readLine();
                results = manager.searchMovies(query, "", 0.0);
            }
            case 2 -> {
                System.out.print("Enter Genre (e.g. Sci-Fi, Thriller, Action): ");
                String genre = readLine();
                results = manager.searchMovies("", genre, 0.0);
            }
            case 3 -> {
                System.out.print("Enter Minimum Average Rating (1.0 to 5.0): ");
                double minRating = readDouble();
                results = manager.searchMovies("", "", minRating);
            }
            case 4 -> results = manager.getAllMovies();
            default -> {
                System.out.println("Invalid search option.");
                return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("\nNo movies matched your search criteria.");
            return;
        }

        System.out.println("\nSearch Results (" + results.size() + " movie(s) found):");
        for (int i = 0; i < results.size(); i++) {
            Movie m = results.get(i);
            System.out.printf(" %d. %s%n", (i + 1), m.toString());
        }

        System.out.print("\nEnter Movie ID to set as Active Movie (or press Enter to skip): ");
        String selId = readLine();
        if (!selId.isEmpty()) {
            try {
                manager.setActiveMovieId(selId);
                System.out.println("Active movie switched to: " + manager.getActiveMovie().getTitle());
            } catch (ResourceNotFoundException e) {
                System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // 4. MOVIE STATUS / LIFECYCLE MANAGEMENT
    // =========================================================================
    private void updateMovieStatus() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected. Please select a movie first.");
            return;
        }

        System.out.println("--- UPDATE MOVIE STATUS ---");
        System.out.println("Movie: " + movie.getTitle() + " [" + movie.getId() + "]");
        System.out.println("Current Lifecycle Status: " + movie.getStatus());

        if (movie.getStatus() == MovieStatus.DROPPED) {
            System.out.println("\n[INVALID OPERATION] Movie '" + movie.getTitle()
                    + "' is DROPPED / CANCELLED and cannot be moved back into active production.");
            return;
        }

        System.out.println("\nSelect New Status:");
        System.out.println(" 1. PLANNING");
        System.out.println(" 2. PRODUCTION");
        System.out.println(" 3. POST_PRODUCTION");
        System.out.println(" 4. RELEASED");
        System.out.println(" 0. Cancel");
        System.out.print("Enter choice (0-4): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> movie.setStatus(MovieStatus.PLANNING);
            case 2 -> movie.setStatus(MovieStatus.PRODUCTION);
            case 3 -> movie.setStatus(MovieStatus.POST_PRODUCTION);
            case 4 -> movie.setStatus(MovieStatus.RELEASED);
            case 0 -> {
                System.out.println("Status update cancelled.");
                return;
            }
            default -> {
                System.out.println("Invalid choice. Status not changed.");
                return;
            }
        }
        System.out.println("Status of '" + movie.getTitle() + "' successfully updated to: " + movie.getStatus());
    }

    // =========================================================================
    // 5. MOVIE STATISTICS / OVERVIEW
    // =========================================================================
    private void viewMovieStatistics() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected. Please select a movie first.");
            return;
        }

        System.out.println("==================================================");
        System.out.println("           MOVIE STATISTICS & OVERVIEW            ");
        System.out.println("==================================================");
        System.out.println(" Movie ID       : " + movie.getId());
        System.out.println(" Title          : " + movie.getTitle());
        System.out.println(" Genre          : " + movie.getGenre());
        System.out.println(" Director       : " + movie.getDirector());
        System.out.println(" Release Year   : " + movie.getReleaseYear());
        System.out.println(" Status         : " + movie.getStatus());
        System.out.printf (" Budget         : $%,.2f%n", movie.getBudget());
        System.out.printf (" Total Expenses : $%,.2f%n", movie.getTotalExpenses());
        if (movie.getStatus() == MovieStatus.DROPPED) {
            System.out.printf (" Financial Loss : $%,.2f (Production Cancelled - Incurred Expenses)%n",
                    movie.getFinancialLoss());
            System.out.printf (" Salvaged Budget: $%,.2f (Unspent Allocated Budget)%n",
                    movie.getRemainingBudget());
        } else {
            System.out.printf (" Remaining      : $%,.2f (%.1f%% spent)%n",
                    movie.getRemainingBudget(), movie.getBudgetUtilizationPercentage());
        }
        System.out.println(" Total Scenes   : " + movie.getScenes().size());
        System.out.printf (" Cast & Crew    : %d members (%d actors, %d crew)%n",
                castManager.getAllPersonnel(movie).size(),
                castManager.getActors(movie).size(),
                castManager.getCrewMembers(movie).size());
        System.out.println(" Total Reviews  : " + reviewManager.getTotalReviewCount(movie));

        if (movie.getReviews().isEmpty()) {
            System.out.println(" Average Rating : No reviews yet");
        } else {
            System.out.printf (" Average Rating : %.1f / 5.0 Stars (%d review%s)%n",
                    reviewManager.calculateAverageRating(movie), reviewManager.getTotalReviewCount(movie),
                    reviewManager.getTotalReviewCount(movie) == 1 ? "" : "s");
        }
        if (!movie.getDistributionReleases().isEmpty()) {
            System.out.printf (" Distribution   : %s (%s) | Status: %s%n",
                    movie.getReleaseRegion(), movie.getDistributor(), movie.getTheatricalStatus());
            System.out.printf (" Release Dates  : Theatrical: %s | OTT: %s (%s)%n",
                    movie.getTheatricalReleaseDate(),
                    movie.getOttPlatform().isEmpty() ? "TBD" : movie.getOttPlatform(),
                    movie.getDigitalReleaseDate().isEmpty() ? "Date Pending" : movie.getDigitalReleaseDate());
        }
        System.out.println("==================================================");
    }

    // =========================================================================
    // 6. SCENE & SCREENPLAY MANAGEMENT (SCRIPTWRITING & SCHEDULE CONFLICTS)
    // =========================================================================
    private void manageScenes() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("\n--- SCENE & SCREENPLAY MANAGEMENT: " + movie.getTitle() + " ---");
        System.out.printf(" Screenplay Writer : %s%n", movie.getScreenplayWriter());
        System.out.printf(" Script Draft      : %s | Total: %.1f Pages (Est. Runtime: ~%.0f min)%n",
                movie.getScriptDraftVersion(), movie.getTotalScriptPages(), movie.getEstimatedRuntimeMinutes());
        System.out.printf(" Script Coverage   : %d of %d scenes have written screenplay scripts%n",
                movie.getScenesWithScriptCount(), movie.getScenes().size());
        java.io.File scriptDoc = manager.getActiveMovieScreenplayFile();
        System.out.printf(" Script Document   : %s%n", (scriptDoc != null ? scriptDoc.getPath() : "data/scripts/"));
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. View All Scenes Summary (Status & Script Badges)");
        System.out.println(" 2. Add New Scene (With Script Option)");
        System.out.println(" 3. Add / Modify Scene Script (Slugline, Pages, Dialogue)");
        System.out.println(" 4. View Screenplay for a Specific Scene");
        System.out.println(" 5. View Full Movie Screenplay (Complete Formatted Script)");
        System.out.println(" 6. Export Full Screenplay to File (data/scripts/)");
        System.out.println(" 7. Edit Movie Script Details (Writer, Draft, Logline)");
        System.out.println(" 8. Update Scene Status (PLANNED / IN_PROGRESS / COMPLETED)");
        System.out.println(" 9. Schedule Scene Shoot Date");
        System.out.println(" 0. Back to Main Menu");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter choice (0-9): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                List<Scene> scenes = movie.getScenes();
                if (scenes.isEmpty()) {
                    System.out.println("No scenes recorded for this movie.");
                } else {
                    System.out.println("\n--- SCENE & SCRIPT LIST FOR: " + movie.getTitle() + " ---");
                    for (Scene sc : scenes) {
                        System.out.println(" " + sc);
                    }
                    System.out.printf("%nTotal Screenplay Length: %.1f Pages (~%.0f min)%n",
                            movie.getTotalScriptPages(), movie.getEstimatedRuntimeMinutes());
                }
            }
            case 2 -> {
                if (movie.getStatus() == MovieStatus.DROPPED) {
                    System.out.println("\n[INVALID OPERATION] Movie '" + movie.getTitle()
                            + "' is DROPPED / CANCELLED. Cannot add scenes to cancelled production.");
                    return;
                }
                System.out.print("Enter Scene Number: ");
                int sceneNum = readInt();
                System.out.print("Enter Scene Title: ");
                String title = readLine();
                System.out.print("Enter Location / Setting: ");
                String location = readLine();
                System.out.print("Enter Status (PLANNED / IN_PROGRESS / COMPLETED) [Default PLANNED]: ");
                String status = readLine();
                System.out.print("Enter Shooting Date (YYYY-MM-DD or press Enter for TBD): ");
                String shootDate = readLine();

                System.out.print("Would you like to write the screenplay script for this scene now? (y/n) [n]: ");
                String wantScript = readLine();
                String slugline = null;
                double scriptPages = 1.0;
                String scriptContent = null;

                if (wantScript.equalsIgnoreCase("y") || wantScript.equalsIgnoreCase("yes")) {
                    System.out.print("Enter Slugline (e.g. INT. " + location.toUpperCase() + " - DAY) [Press Enter for default]: ");
                    String customSlug = readLine();
                    if (!customSlug.isEmpty()) slugline = customSlug;

                    System.out.print("Enter Estimated Script Pages (e.g. 2.5) [Default 1.0]: ");
                    double p = readDouble();
                    if (p > 0) scriptPages = p;

                    System.out.println("\n--- ENTER SCREENPLAY DIALOGUE & ACTION ---");
                    scriptContent = readMultiLineScriptText();
                }

                try {
                    Scene sc = new Scene(sceneNum, title, location, status, shootDate, slugline, scriptPages, scriptContent);
                    movie.addScene(sc);
                    manager.autoSyncScreenplayFile(movie);
                    System.out.println("\nScene #" + sceneNum + " added successfully!");
                    if (sc.hasScript()) {
                        System.out.printf("Screenplay attached: %.1f pages registered.%n", sc.getScriptPages());
                    }
                    System.out.println("[DOCUMENT SYNC] Screenplay text file auto-saved to: "
                            + manager.getActiveMovieScreenplayFile().getPath());
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                } catch (ScheduleConflictException e) {
                    System.out.println("\n[SCHEDULE CONFLICT] " + e.getMessage());
                }
            }
            case 3 -> {
                List<Scene> scenes = movie.getScenes();
                if (scenes.isEmpty()) {
                    System.out.println("No scenes available. Please add a scene first.");
                    return;
                }
                System.out.println("\n--- AVAILABLE SCENES FOR SCRIPTING ---");
                for (Scene sc : scenes) {
                    String badge = sc.hasScript()
                            ? String.format("[Existing Script: %.1f pgs]", sc.getScriptPages())
                            : "[No Script Content Yet]";
                    System.out.printf(" Scene #%-3d: %-25s | Loc: %-20s %s%n",
                            sc.getSceneNumber(), sc.getTitle(), sc.getLocation(), badge);
                }
                System.out.print("\nEnter Scene Number to add or modify script: ");
                int sceneNum = readInt();

                try {
                    Scene target = movie.getSceneOrThrow(sceneNum);
                    System.out.println("\n==================================================");
                    System.out.printf(" EDITING SCRIPT FOR SCENE #%d: %s%n", target.getSceneNumber(), target.getTitle().toUpperCase());
                    System.out.println("==================================================");
                    System.out.println("Current Slugline: " + target.getSlugline());
                    System.out.printf("Current Pages   : %.1f%n", target.getScriptPages());
                    System.out.println("--------------------------------------------------");
                    if (target.hasScript()) {
                        System.out.println("CURRENT SCRIPT PREVIEW:");
                        System.out.println(target.getScriptContent());
                    } else {
                        System.out.println("[No screenplay dialogue entered yet]");
                    }
                    System.out.println("--------------------------------------------------");

                    System.out.print("Enter New Slugline [Press Enter to keep current]: ");
                    String newSlug = readLine();
                    if (!newSlug.isEmpty()) {
                        target.setSlugline(newSlug);
                    }

                    System.out.print("Enter Script Page Count (e.g. 2.5) [Press Enter to keep current (" + target.getScriptPages() + ")]: ");
                    String pageInput = readLine();
                    if (!pageInput.isEmpty()) {
                        try {
                            double p = Double.parseDouble(pageInput);
                            if (p > 0) target.setScriptPages(p);
                        } catch (Exception ignored) {}
                    }

                    System.out.println("\nEnter New Screenplay Dialogue & Action text:");
                    System.out.println("(Press Enter directly twice or type ':done' on blank line to finish)");
                    String newContent = readMultiLineScriptText();
                    if (!newContent.isEmpty()) {
                        target.setScriptContent(newContent);
                        System.out.println("\nScreenplay dialogue updated!");
                    } else {
                        System.out.println("\n(Retaining existing screenplay dialogue)");
                    }

                    manager.autoSyncScreenplayFile(movie);
                    System.out.printf("%nScript for Scene #%d ('%s') updated successfully!%n",
                            target.getSceneNumber(), target.getTitle());
                    System.out.printf("Total Movie Script Length now: %.1f Pages (Est: ~%.0f min)%n",
                            movie.getTotalScriptPages(), movie.getEstimatedRuntimeMinutes());
                    System.out.println("[DOCUMENT SYNC] Screenplay text file auto-saved to: "
                            + manager.getActiveMovieScreenplayFile().getPath());
                } catch (ResourceNotFoundException e) {
                    System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
                }
            }
            case 4 -> {
                List<Scene> scenes = movie.getScenes();
                if (scenes.isEmpty()) {
                    System.out.println("No scenes recorded.");
                    return;
                }
                System.out.print("Enter Scene Number to view script: ");
                int sceneNum = readInt();
                try {
                    Scene target = movie.getSceneOrThrow(sceneNum);
                    System.out.println();
                    System.out.println(target.getFormattedScript());
                } catch (ResourceNotFoundException e) {
                    System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
                }
            }
            case 5 -> {
                try {
                    java.io.File docFile = manager.getActiveMovieScreenplayFile();
                    System.out.println("\n==================================================");
                    System.out.println(" MOVIE SCREENPLAY DOCUMENT VIEWER                 ");
                    System.out.println(" Document Path: " + docFile.getPath());
                    System.out.println("==================================================");
                    String docText = manager.readActiveMovieScreenplayFromFile();
                    System.out.println(docText.isEmpty() ? movie.getFormattedScreenplay() : docText);
                } catch (Exception e) {
                    System.out.println(movie.getFormattedScreenplay());
                }
            }
            case 6 -> {
                try {
                    java.io.File exported = manager.exportActiveMovieScreenplay();
                    System.out.println("\n==================================================");
                    System.out.println(" SCREENPLAY EXPORTED SUCCESSFULLY!               ");
                    System.out.println("==================================================");
                    System.out.println("File Location: " + exported.getAbsolutePath());
                    System.out.printf("Total Scenes : %d | Total Pages: %.1f | Est: ~%.0f min%n",
                            movie.getScenes().size(), movie.getTotalScriptPages(), movie.getEstimatedRuntimeMinutes());
                    System.out.println("You can open this text file to read the complete screenplay.");
                } catch (Exception e) {
                    System.out.println("\n[EXPORT ERROR] Failed to export screenplay: " + e.getMessage());
                }
            }
            case 7 -> {
                System.out.println("\n--- EDIT MOVIE SCREENPLAY METADATA ---");
                System.out.println("Current Screenplay Writer : " + movie.getScreenplayWriter());
                System.out.println("Current Script Draft      : " + movie.getScriptDraftVersion());
                System.out.println("Current Logline / Synopsis: " + movie.getSynopsis());
                System.out.println("--------------------------------------------------");

                System.out.print("Enter Screenplay Writer [Press Enter to keep]: ");
                String writer = readLine();
                if (!writer.isEmpty()) movie.setScreenplayWriter(writer);

                System.out.print("Enter Script Draft Version [Press Enter to keep]: ");
                String draft = readLine();
                if (!draft.isEmpty()) movie.setScriptDraftVersion(draft);

                System.out.print("Enter Story Logline / Synopsis [Press Enter to keep]: ");
                String syn = readLine();
                if (!syn.isEmpty()) movie.setSynopsis(syn);

                manager.autoSyncScreenplayFile(movie);
                System.out.println("\nMovie screenplay details updated successfully!");
                System.out.println("[DOCUMENT SYNC] Screenplay text file auto-saved to: "
                        + manager.getActiveMovieScreenplayFile().getPath());
            }
            case 8 -> {
                if (movie.getStatus() == MovieStatus.DROPPED) {
                    System.out.println("\n[INVALID OPERATION] Movie '" + movie.getTitle()
                            + "' is DROPPED / CANCELLED. Cannot update scene status on cancelled production.");
                    return;
                }
                List<Scene> scenes = movie.getScenes();
                if (scenes.isEmpty()) {
                    System.out.println("No scenes to update.");
                    return;
                }
                System.out.print("Enter Scene Number to update status: ");
                int sceneNum = readInt();
                try {
                    Scene target = movie.getSceneOrThrow(sceneNum);
                    System.out.println("Current status: " + target.getStatus());
                    System.out.print("Enter new status (PLANNED / IN_PROGRESS / COMPLETED): ");
                    String newStatus = readLine();
                    if (!newStatus.isEmpty()) {
                        target.setStatus(newStatus);
                        System.out.println("Scene #" + sceneNum + " status updated to " + target.getStatus());
                    }
                } catch (ResourceNotFoundException e) {
                    System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
                }
            }
            case 9 -> {
                if (movie.getStatus() == MovieStatus.DROPPED) {
                    System.out.println("\n[INVALID OPERATION] Movie '" + movie.getTitle()
                            + "' is DROPPED / CANCELLED. Cannot schedule scenes on cancelled production.");
                    return;
                }
                List<Scene> scenes = movie.getScenes();
                if (scenes.isEmpty()) {
                    System.out.println("No scenes to schedule.");
                    return;
                }
                System.out.print("Enter Scene Number to schedule: ");
                int sceneNum = readInt();
                try {
                    Scene target = movie.getSceneOrThrow(sceneNum);
                    System.out.printf("Scheduling Scene #%d ('%s') at '%s'%n",
                            target.getSceneNumber(), target.getTitle(), target.getLocation());
                    System.out.println("Current Shooting Date: " + target.getShootingDate());
                    System.out.print("Enter New Shooting Date (YYYY-MM-DD): ");
                    String newDate = readLine();

                    movie.scheduleScene(sceneNum, newDate);
                    System.out.println("\nScene #" + sceneNum + " successfully scheduled for " + newDate + "!");
                } catch (ResourceNotFoundException e) {
                    System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
                } catch (ScheduleConflictException e) {
                    System.out.println("\n[SCHEDULE CONFLICT ERROR] " + e.getMessage());
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // 7. MASTER PRODUCTION & RELEASE SCHEDULE (FULL PIPELINE ROADMAP)
    // =========================================================================
    private void manageMasterSchedule() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("\n--- MASTER PRODUCTION & RELEASE SCHEDULE: " + movie.getTitle().toUpperCase() + " ---");
        System.out.printf(" Director: %s | Status: %s | Progress: %d/%d Milestones Completed (%.0f%%)%n",
                movie.getDirector(), movie.getStatus(),
                movie.getCompletedMilestonesCount(), movie.getMasterSchedule().size(),
                movie.getMasterSchedule().isEmpty() ? 0.0 : (movie.getCompletedMilestonesCount() * 100.0 / movie.getMasterSchedule().size()));
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. View Complete Production & Release Roadmap (Timeline)");
        System.out.println(" 2. Schedule / Update Milestone Target Date & Lead");
        System.out.println(" 3. Update Milestone Status (PLANNED / IN_PROGRESS / COMPLETED / DELAYED)");
        System.out.println(" 4. Add Custom or Optional Production Milestone (e.g., Press Meet, Audio Launch)");
        System.out.println(" 5. Validate Pipeline Chronology & Industry Sequence Rules");
        System.out.println(" 6. Export Master Schedule to File (data/schedules/)");
        System.out.println(" 0. Back to Main Menu");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter choice (0-6): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                System.out.println();
                System.out.println(movie.getFormattedTimeline());
                List<String> warnings = movie.validateScheduleSequence();
                if (!warnings.isEmpty()) {
                    System.out.println("[CHRONOLOGY CONFLICT DETECTED]");
                    for (String w : warnings) {
                        System.out.println("  ! " + w);
                    }
                    System.out.println("  Please update the conflicting target dates to maintain proper industry order.\n");
                }
            }
            case 2 -> {
                System.out.println("\n--- ALL PRODUCTION MILESTONES ---");
                for (ProductionMilestone ms : movie.getMasterSchedule()) {
                    System.out.println(" " + ms);
                }
                System.out.print("\nEnter Milestone ID to schedule (e.g., MS-01): ");
                String msId = readLine();
                ProductionMilestone target = movie.findMilestoneById(msId);
                if (target == null) {
                    System.out.println("\n[ERROR] Milestone '" + msId + "' not found.");
                    return;
                }
                System.out.println("\nSelected Milestone: " + target.getTitle());
                System.out.println("Current Target Date : " + target.getTargetDate());
                System.out.println("Current Lead        : " + target.getResponsibleLead());
                System.out.println("Current Notes       : " + target.getNotes());

                System.out.print("\nEnter New Target Date (YYYY-MM-DD) [Press Enter to keep current]: ");
                String newDate = readLine();
                if (!newDate.isEmpty()) {
                    target.setTargetDate(newDate);
                }

                System.out.print("Enter Responsible Lead / Dept [Press Enter to keep current]: ");
                String newLead = readLine();
                if (!newLead.isEmpty()) {
                    target.setResponsibleLead(newLead);
                }

                System.out.print("Enter Notes / Description [Press Enter to keep current]: ");
                String newNotes = readLine();
                if (!newNotes.isEmpty()) {
                    target.setNotes(newNotes);
                }

                System.out.println("\nMilestone [" + target.getId() + "] updated successfully!");
                List<String> warnings = movie.validateScheduleSequence();
                if (!warnings.isEmpty()) {
                    System.out.println("\n[CHRONOLOGY WARNING]:");
                    for (String w : warnings) {
                        System.out.println("  ! " + w);
                    }
                }
            }
            case 3 -> {
                System.out.println("\n--- ALL PRODUCTION MILESTONES ---");
                for (ProductionMilestone ms : movie.getMasterSchedule()) {
                    System.out.println(" " + ms);
                }
                System.out.print("\nEnter Milestone ID to update status (e.g., MS-01): ");
                String msId = readLine();
                ProductionMilestone target = movie.findMilestoneById(msId);
                if (target == null) {
                    System.out.println("\n[ERROR] Milestone '" + msId + "' not found.");
                    return;
                }
                System.out.println("Current Status: " + target.getStatus());
                System.out.print("Enter New Status (PLANNED / IN_PROGRESS / COMPLETED / DELAYED): ");
                String newStatus = readLine();
                if (!newStatus.isEmpty()) {
                    target.setStatus(newStatus);
                    System.out.println("\nMilestone [" + target.getId() + "] status updated to: " + target.getStatus());
                }
            }
            case 4 -> {
                System.out.println("\n--- ADD OPTIONAL / CUSTOM PRODUCTION EVENT ---");
                System.out.println("Choose Production Phase for this Event:");
                System.out.println(" 1. Pre-Production (e.g. Workshop, Script Reading)");
                System.out.println(" 2. Production (e.g. Action Choreography, Song Shoot)");
                System.out.println(" 3. Post-Production (e.g. Color Grading DI, Final Mix)");
                System.out.println(" 4. Promotion & Release (e.g. Press Meet, Audio Launch, Fan Tour)");
                System.out.print("Enter phase option (1-4): ");
                int phaseOpt = readInt();
                ProductionStage stage = switch (phaseOpt) {
                    case 1 -> ProductionStage.SCRIPT_NARRATION;
                    case 2 -> ProductionStage.PRINCIPAL_PHOTOGRAPHY;
                    case 3 -> ProductionStage.PICTURE_EDITING;
                    default -> ProductionStage.PRESS_MEET;
                };

                int nextIndex = movie.getMasterSchedule().size() + 1;
                String newId = "MS-" + String.format("%02d", nextIndex);
                System.out.print("Enter Event Title (e.g., Grand Audio Launch / Press Meet): ");
                String title = readLine();
                if (title.isEmpty()) {
                    System.out.println("Title cannot be empty.");
                    return;
                }
                System.out.print("Enter Target Date (YYYY-MM-DD or press Enter for TBD): ");
                String date = readLine();
                System.out.print("Enter Responsible Lead (e.g., PR Head, Music Director): ");
                String lead = readLine();
                System.out.print("Enter Event Notes / Agenda: ");
                String notes = readLine();

                try {
                    ProductionMilestone customMs = new ProductionMilestone(newId, stage, title, date, "PLANNED", lead, true, notes);
                    movie.getMasterSchedule().add(customMs);
                    System.out.println("\nOptional Event [" + newId + "] '" + title + "' added successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 5 -> {
                System.out.println("\n--- PRODUCTION PIPELINE & MARKETING DEPENDENCY AUDIT ---");
                System.out.println("Enforcing Industry Best Practices:");
                System.out.println("  1. Pre-Production (Story Narration -> Poster Shoot)");
                System.out.println("  2. Production (Principal Photography Filming)");
                System.out.println("  3. Post-Production (Picture Editing -> Audio/Dubbing -> VFX/CGI)");
                System.out.println("  4. Marketing Order (First Look Poster -> Teaser -> Trailer)");
                System.out.println("  5. Release (Press Meet [Optional] -> Promotions -> Theatrical Release)");
                System.out.println("--------------------------------------------------------------------------------");

                List<String> warnings = movie.validateScheduleSequence();
                if (warnings.isEmpty()) {
                    System.out.println(" [OK] PERFECT CHRONOLOGY! All scheduled dates follow the proper filmmaking");
                    System.out.println("      and marketing sequence without any conflicts.");
                } else {
                    System.out.println(" [!] CHRONOLOGY & SEQUENCE CONFLICTS DETECTED (" + warnings.size() + "):");
                    for (String w : warnings) {
                        System.out.println("   * " + w);
                    }
                    System.out.println("\n Industry Rule: Marketing releases MUST follow the strict chronological order:");
                    System.out.println(" First Look Poster  -->  Teaser  -->  Official Trailer  -->  Theatrical Release");
                }
            }
            case 6 -> {
                try {
                    java.io.File exported = manager.exportActiveMovieSchedule();
                    System.out.println("\n==================================================");
                    System.out.println(" MASTER SCHEDULE EXPORTED SUCCESSFULLY!          ");
                    System.out.println("==================================================");
                    System.out.println("File Location: " + exported.getAbsolutePath());
                    System.out.printf("Total Milestones: %d | Completed: %d%n",
                            movie.getMasterSchedule().size(), movie.getCompletedMilestonesCount());
                    System.out.println("You can open this text file to inspect the complete production roadmap.");
                } catch (Exception e) {
                    System.out.println("\n[EXPORT ERROR] Failed to export master schedule: " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // 8. DAILY CALL SHEETS & SHOOTING SCHEDULE (CAST CALLS, CREW, EMERGENCY)
    // =========================================================================
    private void manageCallSheets() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("\n--- DAILY CALL SHEETS & SHOOTING SCHEDULE: " + movie.getTitle().toUpperCase() + " ---");
        System.out.printf(" Total Call Sheets: %d | Total Scenes in Movie: %d scenes%n",
                movie.getCallSheets().size(), movie.getScenes().size());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. View All Daily Call Sheets Summary");
        System.out.println(" 2. Generate New Daily Call Sheet (Date, Location, Crew Call, Hospital)");
        System.out.println(" 3. View Full Formatted Call Sheet Document (Live Preview)");
        System.out.println(" 4. Schedule Scenes on a Call Sheet (Attach Scene Numbers)");
        System.out.println(" 5. Assign / Modify Cast Call Times (Pickup, Makeup, On-Set, Notes)");
        System.out.println(" 6. Update Department & Crew Directives (Camera, Sound, Stunts, Catering)");
        System.out.println(" 7. Record Daily Shooting Report (DSR Wrap & Pages Completed)");
        System.out.println(" 8. Export Call Sheet to Text File (data/call_sheets/)");
        System.out.println(" 0. Back to Main Menu");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter choice (0-8): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                List<CallSheet> sheets = movie.getCallSheets();
                if (sheets.isEmpty()) {
                    System.out.println("No daily call sheets created for this movie yet.");
                } else {
                    System.out.println("\n========================================================================================================");
                    System.out.printf(" %-8s | %-12s | %-12s | %-10s | %-26s | %-8s | %-10s%n",
                            "SHEET ID", "SHOOT DAY", "DATE", "CREW CALL", "LOCATION", "SCENES", "STATUS");
                    System.out.println("----------+--------------+--------------+------------+----------------------------+----------+------------");
                    for (CallSheet cs : sheets) {
                        String locStr = cs.getShootingLocation();
                        if (locStr.length() > 26) locStr = locStr.substring(0, 23) + "...";
                        System.out.printf(" %-8s | Day %-2d of %-3d | %-12s | %-10s | %-26s | %-8d | %-10s%n",
                                cs.getSheetId(), cs.getDayNumber(), cs.getTotalDays(), cs.getShootDate(),
                                cs.getGeneralCrewCall(), locStr, cs.getScheduledSceneNumbers().size(), cs.getStatus());
                    }
                    System.out.println("========================================================================================================");
                }
            }
            case 2 -> {
                if (movie.getStatus() == MovieStatus.DROPPED) {
                    System.out.println("\n[INVALID OPERATION] Movie '" + movie.getTitle()
                            + "' is DROPPED / CANCELLED. Cannot create daily call sheets for cancelled production.");
                    return;
                }
                System.out.println("\n--- CREATE NEW DAILY CALL SHEET ---");
                System.out.print("Enter Call Sheet ID (e.g. CS-03, press Enter to auto-generate): ");
                String sheetId = readLine();
                if (sheetId.isEmpty()) {
                    sheetId = "CS-" + String.format("%02d", movie.getCallSheets().size() + 1);
                }
                System.out.print("Enter Shooting Day Number (e.g. 1, 2, 14): ");
                int dayNum = readInt();
                System.out.print("Enter Total Planned Shooting Days for Movie (e.g. 40) [Default 40]: ");
                int totalDays = readInt();
                if (totalDays < dayNum) totalDays = Math.max(dayNum, 40);

                System.out.print("Enter Shoot Date (YYYY-MM-DD or e.g. 2026-11-20): ");
                String shootDate = readLine();
                System.out.print("Enter General Crew Call Time (e.g. 06:30 AM): ");
                String crewCall = readLine();
                System.out.print("Enter Basecamp / Shooting Location (e.g. Pinewood Stage 3): ");
                String loc = readLine();
                System.out.print("Enter Weather Forecast & Sun Times (or press Enter for default): ");
                String weather = readLine();
                System.out.print("Enter Nearest Emergency Hospital & Contact: ");
                String hospital = readLine();

                try {
                    CallSheet cs = new CallSheet(sheetId, dayNum, totalDays, shootDate, crewCall, loc, weather, hospital, "SCHEDULED");

                    if (!movie.getScenes().isEmpty()) {
                        System.out.println("\nAvailable scenes in movie:");
                        for (Scene sc : movie.getScenes()) {
                            System.out.printf(" Scene #%d: %s (%s, %.1f pgs)%n",
                                    sc.getSceneNumber(), sc.getTitle(), sc.getLocation(), sc.getScriptPages());
                        }
                        System.out.print("Enter scene numbers to schedule on this call sheet (comma-separated, e.g. 1,2 or press Enter): ");
                        String scInput = readLine();
                        if (!scInput.isEmpty()) {
                            for (String s : scInput.split(",")) {
                                try {
                                    int sn = Integer.parseInt(s.trim());
                                    if (movie.findSceneByNumber(sn) != null) {
                                        cs.addScheduledScene(sn);
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    }

                    movie.addCallSheet(cs);
                    manager.autoSyncCallSheetFile(movie, cs);
                    System.out.println("\nCall Sheet '" + cs.getSheetId() + "' (Day " + cs.getDayNumber() + ") created successfully!");
                    java.io.File csFile = manager.getActiveMovieCallSheetFile(cs);
                    if (csFile != null) {
                        System.out.println("[DOCUMENT SAVED] Call sheet exported to: " + csFile.getPath());
                    }
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 3 -> {
                List<CallSheet> sheets = movie.getCallSheets();
                if (sheets.isEmpty()) {
                    System.out.println("No call sheets recorded.");
                    return;
                }
                System.out.print("Enter Call Sheet ID (e.g. CS-01) or Shoot Day number: ");
                String query = readLine().trim();
                CallSheet target = movie.findCallSheetById(query);
                if (target == null) {
                    try {
                        int dn = Integer.parseInt(query);
                        target = movie.findCallSheetByDay(dn);
                    } catch (Exception ignored) {}
                }
                if (target == null) {
                    System.out.println("Call Sheet not found.");
                    return;
                }
                System.out.println();
                System.out.println(target.getFormattedCallSheet(movie));
            }
            case 4 -> {
                System.out.print("Enter Call Sheet ID to manage scenes: ");
                String id = readLine();
                CallSheet cs = movie.findCallSheetById(id);
                if (cs == null) {
                    System.out.println("Call sheet not found.");
                    return;
                }
                System.out.println("Current scheduled scenes: " + cs.getScheduledSceneNumbers());
                System.out.println("Available scenes in movie:");
                for (Scene sc : movie.getScenes()) {
                    System.out.printf(" Scene #%d: %s (%s, %.1f pgs)%n",
                            sc.getSceneNumber(), sc.getTitle(), sc.getLocation(), sc.getScriptPages());
                }
                System.out.print("Enter Scene Number to toggle (add/remove): ");
                int scNum = readInt();
                if (cs.getScheduledSceneNumbers().contains(scNum)) {
                    cs.removeScheduledScene(scNum);
                    System.out.println("Removed Scene #" + scNum + " from call sheet.");
                } else {
                    if (movie.findSceneByNumber(scNum) != null) {
                        cs.addScheduledScene(scNum);
                        System.out.println("Added Scene #" + scNum + " to call sheet.");
                    } else {
                        System.out.println("Scene #" + scNum + " does not exist in this movie.");
                    }
                }
                manager.autoSyncCallSheetFile(movie, cs);
                System.out.println("[DOCUMENT SYNC] Call sheet updated.");
            }
            case 5 -> {
                System.out.print("Enter Call Sheet ID to manage talent calls: ");
                String id = readLine();
                CallSheet cs = movie.findCallSheetById(id);
                if (cs == null) {
                    System.out.println("Call sheet not found.");
                    return;
                }
                System.out.println("\n--- CAST CALL TIMES FOR " + cs.getSheetId() + " (DAY " + cs.getDayNumber() + ") ---");
                for (CastCallEntry c : cs.getCastCalls()) {
                    System.out.println(" " + c);
                }
                System.out.println("\n 1. Add / Update Cast Call Time");
                System.out.println(" 2. Remove Cast Member from Call Sheet");
                System.out.print("Choose (1-2): ");
                int cChoice = readInt();
                if (cChoice == 1) {
                    System.out.print("Enter Actor ID (e.g. ACT-01): ");
                    String actId = readLine();
                    System.out.print("Enter Actor Name: ");
                    String name = readLine();
                    System.out.print("Enter Character Role: ");
                    String role = readLine();
                    System.out.print("Enter Pickup Time (e.g. 05:45 AM or On Own): ");
                    String pickup = readLine();
                    System.out.print("Enter Hair & Makeup Call (e.g. 06:30 AM): ");
                    String mu = readLine();
                    System.out.print("Enter On-Set / Ready to Roll Call (e.g. 08:00 AM): ");
                    String onSet = readLine();
                    System.out.print("Enter Special Instructions (wardrobe, props, prosthetics): ");
                    String notes = readLine();

                    cs.addCastCall(new CastCallEntry(actId, name, role, pickup, mu, onSet, notes));
                    manager.autoSyncCallSheetFile(movie, cs);
                    System.out.println("\nCast call for " + name + " (" + role + ") saved to call sheet!");
                } else if (cChoice == 2) {
                    System.out.print("Enter Actor ID to remove: ");
                    String actId = readLine();
                    if (cs.removeCastCall(actId)) {
                        manager.autoSyncCallSheetFile(movie, cs);
                        System.out.println("Removed actor " + actId + " from call sheet.");
                    } else {
                        System.out.println("Actor ID not found on call sheet.");
                    }
                }
            }
            case 6 -> {
                System.out.print("Enter Call Sheet ID to update department directives: ");
                String id = readLine();
                CallSheet cs = movie.findCallSheetById(id);
                if (cs == null) {
                    System.out.println("Call sheet not found.");
                    return;
                }
                System.out.println("\nCurrent Department Notes:");
                System.out.println(cs.getDepartmentNotes());
                System.out.println("\nEnter new Department Directives (Camera, Sound, Stunts, Grip, Catering):");
                System.out.println("(Type ':done' on a blank line or press Enter twice to finish)");
                String newNotes = readMultiLineScriptText();
                if (!newNotes.isEmpty()) {
                    cs.setDepartmentNotes(newNotes);
                    manager.autoSyncCallSheetFile(movie, cs);
                    System.out.println("\nDepartment notes updated!");
                }
            }
            case 7 -> {
                System.out.print("Enter Call Sheet ID to record DSR Wrap report: ");
                String id = readLine();
                CallSheet cs = movie.findCallSheetById(id);
                if (cs == null) {
                    System.out.println("Call sheet not found.");
                    return;
                }
                System.out.println("Current Status: " + cs.getStatus());
                System.out.print("Enter New Status (SCHEDULED / IN_PROGRESS / WRAPPED / DELAYED): ");
                String st = readLine();
                if (!st.isEmpty()) cs.setStatus(st);

                System.out.printf("Scheduled Pages: %.1f | Current Pages Shot: %.1f%n",
                        cs.calculateTotalScheduledPages(movie), cs.getPagesShot());
                System.out.print("Enter Actual Pages Shot Today (e.g. 2.5): ");
                double p = readDouble();
                if (p >= 0) cs.setPagesShot(p);

                System.out.print("Enter Wrap Time & Daily Production Notes (e.g. Wrapped at 19:30, zero injuries): ");
                String wn = readLine();
                if (!wn.isEmpty()) cs.setWrapNotes(wn);

                manager.autoSyncCallSheetFile(movie, cs);
                System.out.println("\nDaily Shooting Report (DSR) updated successfully!");
                java.io.File csFile = manager.getActiveMovieCallSheetFile(cs);
                if (csFile != null) {
                    System.out.println("[DOCUMENT SYNC] Call sheet updated: " + csFile.getPath());
                }
            }
            case 8 -> {
                System.out.print("Enter Call Sheet ID to export: ");
                String id = readLine();
                CallSheet cs = movie.findCallSheetById(id);
                if (cs == null) {
                    System.out.println("Call sheet not found.");
                    return;
                }
                try {
                    java.io.File exported = manager.exportActiveMovieCallSheet(cs);
                    System.out.println("\n==================================================");
                    System.out.println(" CALL SHEET EXPORTED SUCCESSFULLY!               ");
                    System.out.println("==================================================");
                    System.out.println("File Location: " + exported.getAbsolutePath());
                    System.out.println("You can print or distribute this text document to cast and crew.");
                } catch (Exception e) {
                    System.out.println("\n[EXPORT ERROR] Failed to export call sheet: " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid choice. Please choose between 0 and 8.");
        }
    }

    // =========================================================================
    // 9. VOICE DUBBING & LOCALIZATION MANAGEMENT
    // =========================================================================
    private void manageDubbing() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("\n--- VOICE DUBBING & LOCALIZATION: " + movie.getTitle().toUpperCase() + " ---");
        System.out.printf(" Tracks: %d | Approved: %d/%d | Total Retakes Logged: %d%n",
                movie.getDubbingTracks().size(), movie.getApprovedDubbingTracksCount(),
                movie.getDubbingTracks().size(), movie.getTotalRetakesCount());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. View All Dubbing Tracks & Multi-Language Status");
        System.out.println(" 2. Add New Dubbing Track (Character, Artist, Language, Studio)");
        System.out.println(" 3. Update Recording Status (PLANNED / IN_PROGRESS / RECORDED / APPROVED)");
        System.out.println(" 4. Log Voice Retake (Increments Counter & Captures Retake Reason)");
        System.out.println(" 5. Filter Dubbing Tracks by Language");
        System.out.println(" 6. Export Dubbing Cue Manifest to File (data/dubbing/)");
        System.out.println(" 0. Back to Main Menu");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter choice (0-6): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                System.out.println();
                System.out.println(movie.getFormattedDubbingSummary());
            }
            case 2 -> {
                System.out.println("\n--- REGISTER NEW DUBBING TRACK ---");
                int nextNum = movie.getDubbingTracks().size() + 1;
                String trackId = "DUB-" + String.format("%02d", nextNum);

                System.out.print("Enter Character Name (e.g. Commander Jax / Pushpa Raj): ");
                String charName = readLine();
                System.out.print("Enter Voice Artist / Actor Name: ");
                String artist = readLine();
                System.out.print("Enter Dubbing Language (e.g. English, Hindi, Telugu, Tamil, Japanese): ");
                String lang = readLine();
                System.out.print("Enter Dubbing Studio Location [Default 'Main Scoring Stage']: ");
                String studio = readLine();

                try {
                    DubbingTrack track = new DubbingTrack(trackId, charName, artist, lang, "PLANNED", 0, "", studio);
                    movie.addDubbingTrack(track);
                    System.out.println("\nDubbing Track [" + trackId + "] registered successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 3 -> {
                System.out.println("\n--- SELECT DUBBING TRACK TO UPDATE STATUS ---");
                for (DubbingTrack dt : movie.getDubbingTracks()) {
                    System.out.println(" " + dt);
                }
                System.out.print("\nEnter Track ID (e.g. DUB-01): ");
                String id = readLine();
                DubbingTrack target = movie.findDubbingTrackById(id);
                if (target == null) {
                    System.out.println("\n[ERROR] Dubbing track '" + id + "' not found.");
                    return;
                }
                System.out.println("Current Status: " + target.getStatus());
                System.out.print("Enter New Status (PLANNED / IN_PROGRESS / RECORDED / APPROVED): ");
                String newStatus = readLine();
                if (!newStatus.isEmpty()) {
                    target.setStatus(newStatus);
                    System.out.println("\nTrack [" + target.getTrackId() + "] status updated to: " + target.getStatus());
                }
            }
            case 4 -> {
                System.out.println("\n--- LOG VOICE RETAKE ---");
                for (DubbingTrack dt : movie.getDubbingTracks()) {
                    System.out.println(" " + dt);
                }
                System.out.print("\nEnter Track ID to log retake (e.g. DUB-01): ");
                String id = readLine();
                DubbingTrack target = movie.findDubbingTrackById(id);
                if (target == null) {
                    System.out.println("\n[ERROR] Dubbing track '" + id + "' not found.");
                    return;
                }
                System.out.printf("Selected Track: %s | Char: %s (%s) | Current Retakes: %d%n",
                        target.getTrackId(), target.getCharacterName(), target.getLanguage(), target.getRetakeCount());
                System.out.print("Enter Reason for Retake (e.g. Lip sync mismatch, emotional tone adjustment, breathing noise): ");
                String reason = readLine();

                target.logRetake(reason);
                System.out.println("\n[RETAKE LOGGED] Retake #" + target.getRetakeCount() + " recorded!");
                System.out.println("Track status transitioned to: " + target.getStatusBadge().trim());
            }
            case 5 -> {
                System.out.print("Enter Language to filter (e.g. Hindi, Telugu, English): ");
                String lang = readLine();
                List<DubbingTrack> filtered = movie.getDubbingTracksByLanguage(lang);
                if (filtered.isEmpty()) {
                    System.out.println("No dubbing tracks found for language '" + lang + "'.");
                } else {
                    System.out.println("\n--- DUBBING TRACKS FOR: " + lang.toUpperCase() + " (" + filtered.size() + ") ---");
                    for (DubbingTrack dt : filtered) {
                        System.out.println(" " + dt);
                    }
                }
            }
            case 6 -> {
                try {
                    java.io.File exported = manager.exportActiveMovieDubbing();
                    System.out.println("\n==================================================");
                    System.out.println(" DUBBING MANIFEST EXPORTED SUCCESSFULLY!          ");
                    System.out.println("==================================================");
                    System.out.println("File Location: " + exported.getAbsolutePath());
                    System.out.printf("Total Tracks: %d | Approved: %d%n",
                            movie.getDubbingTracks().size(), movie.getApprovedDubbingTracksCount());
                    System.out.println("You can open this text file to review the voice localization sheet.");
                } catch (Exception e) {
                    System.out.println("\n[EXPORT ERROR] Failed to export dubbing manifest: " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // 9. POST-PRODUCTION MANAGEMENT (EDITING, SOUND MIXING, VFX, RENDERING)
    // =========================================================================
    private void managePostProduction() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("\n--- POST-PRODUCTION WORKFLOW MANAGEMENT: " + movie.getTitle().toUpperCase() + " ---");
        System.out.printf(" Overall Progress: %.1f%% Completed | Finished: %d of %d Tasks%n",
                movie.getAveragePostProductionProgress(),
                movie.getCompletedPostProductionTasksCount(),
                movie.getPostProductionTasks().size());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. View Complete Post-Production Dashboard (Editing, Sound, VFX, Rendering)");
        System.out.println(" 2. Update Task Progress & Status (0-100%, Technical Notes)");
        System.out.println(" 3. Add Custom Post-Production Task Work Order");
        System.out.println(" 4. Filter Tasks by Discipline (Editing, Sound Mixing, VFX, Rendering)");
        System.out.println(" 5. Quality Control (QC) Sign-off (Mark Task as 100% Completed)");
        System.out.println(" 6. Export Post-Production Technical Manifest to File (data/post_production/)");
        System.out.println(" 0. Back to Main Menu");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter choice (0-6): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                System.out.println();
                System.out.println(movie.getFormattedPostProductionDashboard());
            }
            case 2 -> {
                System.out.println("\n--- ALL POST-PRODUCTION TASKS ---");
                for (PostProductionTask pt : movie.getPostProductionTasks()) {
                    System.out.println(" " + pt);
                }
                System.out.print("\nEnter Task ID to update (e.g. POST-01): ");
                String id = readLine();
                PostProductionTask target = movie.findPostProductionTaskById(id);
                if (target == null) {
                    System.out.println("\n[ERROR] Task '" + id + "' not found.");
                    return;
                }
                System.out.printf("Selected Task: %s [%s] - %s%n", target.getTaskId(), target.getType(), target.getTaskName());
                System.out.printf("Current Progress: %d%% | Status: %s | Specs: %s%n",
                        target.getProgressPercentage(), target.getStatus(), target.getTechnicalSpecs());

                System.out.print("Enter New Progress Percentage (0-100) [Press Enter to keep current (" + target.getProgressPercentage() + "%)]: ");
                String progInput = readLine();
                int newProg = target.getProgressPercentage();
                if (!progInput.isEmpty()) {
                    try {
                        newProg = Integer.parseInt(progInput);
                    } catch (Exception ignored) {}
                }

                System.out.print("Enter New Status (PLANNED / IN_PROGRESS / QUALITY_CHECK / COMPLETED / REVISION_REQUESTED) [Press Enter to keep current]: ");
                String newStatus = readLine();

                System.out.print("Enter Technical Notes / Progress Log [Press Enter to keep current]: ");
                String newNotes = readLine();

                target.updateProgress(newProg, newStatus.isEmpty() ? target.getStatus() : newStatus,
                        newNotes.isEmpty() ? target.getNotes() : newNotes);
                System.out.println("\nTask [" + target.getTaskId() + "] successfully updated! Progress now: " + target.getProgressBar());
            }
            case 3 -> {
                System.out.println("\n--- CREATE CUSTOM POST-PRODUCTION TASK ---");
                System.out.println("Select Discipline:");
                System.out.println(" 1. EDITING (Picture & Video Editing)");
                System.out.println(" 2. SOUND_MIXING (Sound Design & Audio Mixing)");
                System.out.println(" 3. VFX (Visual Effects & CGI Animation)");
                System.out.println(" 4. RENDERING (Render Farm & Master Export)");
                System.out.print("Enter choice (1-4): ");
                int typeChoice = readInt();
                PostProductionType type = switch (typeChoice) {
                    case 1 -> PostProductionType.EDITING;
                    case 2 -> PostProductionType.SOUND_MIXING;
                    case 3 -> PostProductionType.VFX;
                    default -> PostProductionType.RENDERING;
                };

                int nextIndex = movie.getPostProductionTasks().size() + 1;
                String taskId = "POST-" + String.format("%02d", nextIndex);

                System.out.print("Enter Task Name: ");
                String name = readLine();
                System.out.print("Enter Lead Specialist / Facility Vendor: ");
                String lead = readLine();
                System.out.print("Enter Technical Specifications: ");
                String specs = readLine();
                System.out.print("Enter Notes / Workflow description: ");
                String notes = readLine();

                try {
                    PostProductionTask task = new PostProductionTask(taskId, type, name, lead, "PLANNED", 0, specs, notes);
                    movie.addPostProductionTask(task);
                    System.out.println("\nPost-Production Task [" + taskId + "] added successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 4 -> {
                System.out.println("\nSelect Discipline to Filter:");
                System.out.println(" 1. Picture & Video Editing");
                System.out.println(" 2. Sound Design & Audio Mixing");
                System.out.println(" 3. Visual Effects & CGI Animation");
                System.out.println(" 4. Render Farm & Master Export");
                System.out.print("Enter choice (1-4): ");
                int filterChoice = readInt();
                PostProductionType filterType = switch (filterChoice) {
                    case 1 -> PostProductionType.EDITING;
                    case 2 -> PostProductionType.SOUND_MIXING;
                    case 3 -> PostProductionType.VFX;
                    default -> PostProductionType.RENDERING;
                };

                List<PostProductionTask> list = movie.getPostProductionTasksByType(filterType);
                System.out.printf("%n--- %s TASKS (%d) ---%n", filterType.getDisplayName().toUpperCase(), list.size());
                if (list.isEmpty()) {
                    System.out.println("No tasks found under this discipline.");
                } else {
                    for (PostProductionTask pt : list) {
                        System.out.println(" " + pt);
                        if (pt.getNotes() != null && !pt.getNotes().isEmpty()) {
                            System.out.println("      Notes: " + pt.getNotes());
                        }
                    }
                }
            }
            case 5 -> {
                System.out.println("\n--- QUALITY CONTROL (QC) SIGN-OFF ---");
                for (PostProductionTask pt : movie.getPostProductionTasks()) {
                    System.out.println(" " + pt);
                }
                System.out.print("\nEnter Task ID to sign off & approve as COMPLETED: ");
                String id = readLine();
                PostProductionTask target = movie.findPostProductionTaskById(id);
                if (target == null) {
                    System.out.println("\n[ERROR] Task '" + id + "' not found.");
                    return;
                }
                target.markCompleted();
                System.out.println("\n[APPROVED] Task [" + target.getTaskId() + "] '" + target.getTaskName()
                        + "' has been signed off as 100% COMPLETED!");
            }
            case 6 -> {
                try {
                    java.io.File exported = manager.exportActiveMoviePostProduction();
                    System.out.println("\n==================================================");
                    System.out.println(" POST-PRODUCTION MANIFEST EXPORTED!               ");
                    System.out.println("==================================================");
                    System.out.println("File Location: " + exported.getAbsolutePath());
                    System.out.printf("Total Tasks: %d | Progress: %.1f%%%n",
                            movie.getPostProductionTasks().size(), movie.getAveragePostProductionProgress());
                    System.out.println("You can open this text file to inspect the complete technical post pipeline.");
                } catch (Exception e) {
                    System.out.println("\n[EXPORT ERROR] Failed to export post-production manifest: " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // 11. THEATRICAL & OTT DISTRIBUTION MANAGEMENT
    // =========================================================================
    private void manageDistribution() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("\n--- THEATRICAL & OTT DISTRIBUTION: " + movie.getTitle().toUpperCase() + " ---");
        System.out.printf(" Total Territories/Deals: %d | Total Theatrical Screens: %,d%n",
                movie.getDistributionReleases().size(), movie.getTotalTheatricalScreens());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. View Complete Distribution & Release Dashboard");
        System.out.println(" 2. Add New Distribution Deal (Region, Distributor, Theatrical & OTT)");
        System.out.println(" 3. Update Theatrical Status & Screen Count");
        System.out.println(" 4. Set / Update OTT Streaming Platform & Digital Premiere Date");
        System.out.println(" 5. Remove Distribution Deal by ID");
        System.out.println(" 6. Export Distribution Manifest to File (data/distribution/)");
        System.out.println(" 0. Back to Main Menu");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter choice (0-6): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                System.out.println();
                System.out.println(movie.getFormattedDistributionSummary());
            }
            case 2 -> {
                System.out.println("\n--- ADD NEW DISTRIBUTION DEAL ---");
                int nextIdx = movie.getDistributionReleases().size() + 1;
                String defaultId = "REL-" + String.format("%02d", nextIdx);
                System.out.print("Enter Release ID [Press Enter for " + defaultId + "]: ");
                String idInput = readLine();
                String releaseId = idInput.isEmpty() ? defaultId : idInput;

                System.out.print("Enter Release Region / Territory (e.g. North America (Domestic), India, Europe, Global): ");
                String region = readLine();
                if (region.isEmpty()) region = "Domestic";

                System.out.print("Enter Theatrical Distributor (e.g. Warner Bros. Pictures, PVR INOX, Universal): ");
                String distributor = readLine();
                if (distributor.isEmpty()) distributor = "Independent Direct Distribution";

                System.out.print("Enter Theatrical Release Date (YYYY-MM-DD): ");
                String theatDate = readLine();
                if (theatDate.isEmpty()) theatDate = "TBD";

                System.out.println("Select Theatrical Status:");
                System.out.println(" 1. SCHEDULED (Default)");
                System.out.println(" 2. ADVANCE_BOOKING_OPEN");
                System.out.println(" 3. RUNNING_IN_THEATERS");
                System.out.println(" 4. CLOSED_RUN");
                System.out.println(" 5. POSTPONED");
                System.out.print("Enter choice (1-5): ");
                int statChoice = readInt();
                String status = switch (statChoice) {
                    case 2 -> "ADVANCE_BOOKING_OPEN";
                    case 3 -> "RUNNING_IN_THEATERS";
                    case 4 -> "CLOSED_RUN";
                    case 5 -> "POSTPONED";
                    default -> "SCHEDULED";
                };

                System.out.print("Enter Screen Count (e.g. 3500): ");
                int screens = readInt();
                if (screens < 0) screens = 0;

                System.out.print("Enter OTT Platform (e.g. Netflix, Prime Video, Disney+ Hotstar, or leave empty if None/Pending): ");
                String ott = readLine();

                System.out.print("Enter Digital / Streaming Premiere Date (YYYY-MM-DD, or leave empty if Pending): ");
                String digDate = readLine();

                System.out.print("Enter Distribution Notes / Windowing terms: ");
                String notes = readLine();

                try {
                    DistributionRelease deal = new DistributionRelease(
                            releaseId, region, distributor, theatDate, status, screens, ott, digDate, notes);
                    movie.addDistributionRelease(deal);
                    manager.autoSyncDistributionFile(movie);
                    System.out.println("\n[SUCCESS] Distribution Deal [" + releaseId + "] for region '" + region + "' added successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 3 -> {
                System.out.println("\n--- UPDATE THEATRICAL STATUS & SCREENS ---");
                List<DistributionRelease> list = movie.getDistributionReleases();
                if (list.isEmpty()) {
                    System.out.println("No distribution deals available.");
                    return;
                }
                for (DistributionRelease r : list) {
                    System.out.printf(" %-8s | %-24s | %-24s | %s | %,d screens%n",
                            r.getReleaseId(), r.getRegion(), r.getDistributor(), r.getTheatricalStatus(), r.getScreenCount());
                }
                System.out.print("\nEnter Release ID to update: ");
                String id = readLine();
                DistributionRelease target = movie.findDistributionReleaseById(id);
                if (target == null) {
                    System.out.println("\n[ERROR] Distribution release '" + id + "' not found.");
                    return;
                }

                System.out.printf("Current Status: %s | Current Screens: %,d%n", target.getTheatricalStatus(), target.getScreenCount());
                System.out.println("Select New Status:");
                System.out.println(" 1. SCHEDULED");
                System.out.println(" 2. ADVANCE_BOOKING_OPEN");
                System.out.println(" 3. RUNNING_IN_THEATERS");
                System.out.println(" 4. CLOSED_RUN");
                System.out.println(" 5. POSTPONED");
                System.out.println(" 6. Keep Current (" + target.getTheatricalStatus() + ")");
                System.out.print("Enter choice (1-6): ");
                int st = readInt();
                switch (st) {
                    case 1 -> target.setTheatricalStatus("SCHEDULED");
                    case 2 -> target.setTheatricalStatus("ADVANCE_BOOKING_OPEN");
                    case 3 -> target.setTheatricalStatus("RUNNING_IN_THEATERS");
                    case 4 -> target.setTheatricalStatus("CLOSED_RUN");
                    case 5 -> target.setTheatricalStatus("POSTPONED");
                    default -> {}
                }

                System.out.print("Enter New Screen Count [Press Enter to keep " + target.getScreenCount() + "]: ");
                String scInput = readLine();
                if (!scInput.isEmpty()) {
                    try {
                        target.setScreenCount(Integer.parseInt(scInput));
                    } catch (Exception ignored) {}
                }

                System.out.print("Enter New Theatrical Release Date [Press Enter to keep " + target.getTheatricalReleaseDate() + "]: ");
                String dtInput = readLine();
                if (!dtInput.isEmpty()) {
                    target.setTheatricalReleaseDate(dtInput);
                }

                manager.autoSyncDistributionFile(movie);
                System.out.println("\n[UPDATED] Deal [" + target.getReleaseId() + "] updated: Status = "
                        + target.getTheatricalStatus() + ", Screens = " + target.getScreenCount() + ", Date = " + target.getTheatricalReleaseDate());
            }
            case 4 -> {
                System.out.println("\n--- SET / UPDATE OTT STREAMING & DIGITAL PREMIERE ---");
                List<DistributionRelease> list = movie.getDistributionReleases();
                if (list.isEmpty()) {
                    System.out.println("No distribution deals available.");
                    return;
                }
                for (DistributionRelease r : list) {
                    System.out.printf(" %-8s | %-24s | OTT: %-18s | Digital Premiere: %s%n",
                            r.getReleaseId(), r.getRegion(),
                            r.hasOttPlatform() ? r.getOttPlatform() : "[Pending / Unsold]",
                            r.hasDigitalReleaseDate() ? r.getDigitalReleaseDate() : "[Pending]");
                }
                System.out.print("\nEnter Release ID to update: ");
                String id = readLine();
                DistributionRelease target = movie.findDistributionReleaseById(id);
                if (target == null) {
                    System.out.println("\n[ERROR] Distribution release '" + id + "' not found.");
                    return;
                }

                System.out.printf("Current OTT Platform: %s | Current Digital Date: %s%n",
                        target.hasOttPlatform() ? target.getOttPlatform() : "None",
                        target.hasDigitalReleaseDate() ? target.getDigitalReleaseDate() : "None");

                System.out.print("Enter OTT Platform (e.g. Netflix, Prime Video, Disney+ Hotstar, or 'NONE' to clear): ");
                String ott = readLine();
                if (!ott.isEmpty()) {
                    if (ott.equalsIgnoreCase("NONE")) {
                        target.setOttPlatform("");
                    } else {
                        target.setOttPlatform(ott);
                    }
                }

                System.out.print("Enter Digital / Streaming Premiere Date (YYYY-MM-DD, or 'NONE' to clear): ");
                String digDate = readLine();
                if (!digDate.isEmpty()) {
                    if (digDate.equalsIgnoreCase("NONE")) {
                        target.setDigitalReleaseDate("");
                    } else {
                        target.setDigitalReleaseDate(digDate);
                    }
                }

                System.out.print("Update Notes [Press Enter to keep current]: ");
                String noteInput = readLine();
                if (!noteInput.isEmpty()) {
                    target.setNotes(noteInput);
                }

                manager.autoSyncDistributionFile(movie);
                System.out.println("\n[UPDATED] OTT & Digital Distribution details saved for [" + target.getReleaseId() + "]!");
            }
            case 5 -> {
                System.out.println("\n--- REMOVE DISTRIBUTION DEAL ---");
                for (DistributionRelease r : movie.getDistributionReleases()) {
                    System.out.printf(" %-8s | %-24s | %s%n", r.getReleaseId(), r.getRegion(), r.getDistributor());
                }
                System.out.print("\nEnter Release ID to remove: ");
                String id = readLine();
                if (movie.removeDistributionRelease(id)) {
                    manager.autoSyncDistributionFile(movie);
                    System.out.println("\n[SUCCESS] Distribution deal '" + id + "' removed.");
                } else {
                    System.out.println("\n[ERROR] Deal '" + id + "' not found.");
                }
            }
            case 6 -> {
                try {
                    java.io.File exported = manager.exportActiveMovieDistribution();
                    System.out.println("\n==================================================");
                    System.out.println(" DISTRIBUTION MANIFEST EXPORTED!                  ");
                    System.out.println("==================================================");
                    System.out.println("File Location: " + exported.getAbsolutePath());
                    System.out.printf("Total Release Deals: %d | Total Screens: %,d%n",
                            movie.getDistributionReleases().size(), movie.getTotalTheatricalScreens());
                    System.out.println("You can open this text file to inspect theatrical windows and OTT rights.");
                } catch (Exception e) {
                    System.out.println("\n[EXPORT ERROR] Failed to export distribution manifest: " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // 12. CAST AND CREW MANAGEMENT (POLYMORPHISM & INHERITANCE)
    // =========================================================================
    private void manageCastAndCrew() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("--- CAST & CREW FOR: " + movie.getTitle() + " ---");
        System.out.println(" 1. View Full Cast & Crew Roster");
        System.out.println(" 2. View Actors Only");
        System.out.println(" 3. View Crew Members Only");
        System.out.println(" 4. Search Personnel (by ID, Name, or Role)");
        System.out.println(" 5. Add Actor");
        System.out.println(" 6. Add Crew Member");
        System.out.println(" 7. Remove Member by ID");
        System.out.println(" 0. Back to Main Menu");
        System.out.print("Enter choice: ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                List<Person> personnel = castManager.getAllPersonnel(movie);
                if (personnel.isEmpty()) {
                    System.out.println("No cast or crew added yet.");
                } else {
                    displayPersonnelTable(personnel);
                }
            }
            case 2 -> {
                List<Actor> actors = castManager.getActors(movie);
                if (actors.isEmpty()) {
                    System.out.println("No actors found in this movie.");
                } else {
                    System.out.println("\n--- ACTORS ROSTER (" + actors.size() + " member" + (actors.size() == 1 ? "" : "s") + ") ---");
                    displayPersonnelTable(new ArrayList<>(actors));
                }
            }
            case 3 -> {
                List<CrewMember> crew = castManager.getCrewMembers(movie);
                if (crew.isEmpty()) {
                    System.out.println("No crew members found in this movie.");
                } else {
                    System.out.println("\n--- CREW ROSTER (" + crew.size() + " member" + (crew.size() == 1 ? "" : "s") + ") ---");
                    displayPersonnelTable(new ArrayList<>(crew));
                }
            }
            case 4 -> {
                System.out.print("Enter search keyword (ID, name, or role): ");
                String query = readLine();
                List<Person> matched = castManager.searchPersonnel(movie, query);
                if (matched.isEmpty()) {
                    System.out.println("No personnel found matching '" + query + "'.");
                } else {
                    System.out.println("\n--- SEARCH RESULTS (" + matched.size() + " found) ---");
                    displayPersonnelTable(matched);
                }
            }
            case 5 -> {
                System.out.print("Enter Person ID (e.g. ACT-105): ");
                String id = readLine();
                System.out.print("Enter Actor's Name: ");
                String name = readLine();
                System.out.print("Enter Email: ");
                String email = readLine();
                System.out.print("Enter Character Role: ");
                String charName = readLine();

                try {
                    Actor actor = new Actor(id, name, email, charName);
                    castManager.addActor(movie, actor);
                    System.out.println("\nActor '" + name + "' added successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 6 -> {
                System.out.print("Enter Person ID (e.g. CRW-205): ");
                String id = readLine();
                System.out.print("Enter Crew Member's Name: ");
                String name = readLine();
                System.out.print("Enter Email: ");
                String email = readLine();
                System.out.print("Enter Department (e.g. Camera, Sound, VFX, Directing): ");
                String dept = readLine();

                try {
                    CrewMember crew = new CrewMember(id, name, email, dept);
                    castManager.addCrewMember(movie, crew);
                    System.out.println("\nCrew Member '" + name + "' added successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 7 -> {
                System.out.print("Enter Person ID to remove (e.g. ACT-101): ");
                String id = readLine();
                try {
                    castManager.removePersonById(movie, id);
                    System.out.println("\nPerson with ID '" + id + "' was successfully removed from '" + movie.getTitle() + "'.");
                } catch (ResourceNotFoundException e) {
                    System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    private void displayPersonnelTable(List<Person> personnel) {
        System.out.println("\n----------------------------------------------------------------------------------");
        System.out.printf("  %-8s %-22s | %-26s | %s%n", "ID", "Name", "Role/Specialization", "Email");
        System.out.println("----------------------------------------------------------------------------------");
        for (Person p : personnel) {
            p.displayInfo();
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    // =========================================================================
    // 8. BUDGET & EXPENSES (DEMONSTRATES CUSTOM EXCEPTION)
    // =========================================================================
    private void manageBudgetAndExpenses() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("--- BUDGET & EXPENSES FOR: " + movie.getTitle() + " ---");
        System.out.println(" 1. View Budget Overview & Expenses");
        System.out.println(" 2. Add New Expense");
        System.out.println(" 0. Back to Main Menu");
        System.out.print("Enter choice: ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                System.out.printf("%nAllocated Budget : $%,12.2f%n", movie.getBudget());
                System.out.printf("Total Spent      : $%,12.2f%n", movie.getTotalExpenses());
                if (movie.getStatus() == MovieStatus.DROPPED) {
                    System.out.printf("Financial Loss   : $%,12.2f (Production Cancelled)%n", movie.getFinancialLoss());
                    System.out.printf("Salvaged Budget  : $%,12.2f%n%n", movie.getRemainingBudget());
                } else {
                    System.out.printf("Remaining Budget : $%,12.2f%n%n", movie.getRemainingBudget());
                }

                List<Expense> expenses = movie.getExpenses();
                if (expenses.isEmpty()) {
                    System.out.println("No expenses recorded yet.");
                } else {
                    System.out.println("Expense Records:");
                    for (Expense exp : expenses) {
                        System.out.println("  " + exp);
                    }
                }
            }
            case 2 -> {
                if (movie.getStatus() == MovieStatus.DROPPED) {
                    System.out.println("\n[INVALID OPERATION] Movie '" + movie.getTitle()
                            + "' is DROPPED / CANCELLED. Cannot incur new expenses on cancelled production.");
                    return;
                }
                System.out.print("Enter Expense ID (e.g. EXP-04): ");
                String id = readLine();
                System.out.print("Enter Category (e.g. VFX, Equipment, Costumes, Logistics): ");
                String cat = readLine();
                System.out.print("Enter Amount ($): ");
                double amount = readDouble();
                System.out.print("Enter Description: ");
                String desc = readLine();

                try {
                    Expense exp = new Expense(id, cat, amount, desc);
                    movie.addExpense(exp);
                    System.out.println("\nExpense added successfully!");
                    System.out.printf("Updated Remaining Budget: $%,.2f%n", movie.getRemainingBudget());
                } catch (BudgetExceededException e) {
                    System.out.println("\n[BUDGET EXCEEDED ERROR] " + e.getMessage());
                    System.out.println("The expense was NOT added due to insufficient allocated funds.");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // 9. ADD MOVIE REVIEW
    // =========================================================================
    private void addMovieReview() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected. Please select a movie first.");
            return;
        }

        System.out.println("--- ADD REVIEW FOR: " + movie.getTitle() + " ---");
        System.out.print("Enter Reviewer Name: ");
        String reviewer = readLine();
        System.out.print("Enter Star Rating (1 to 5): ");
        int rating = readInt();
        System.out.print("Enter Review Comment: ");
        String comment = readLine();

        try {
            Review review = new Review(reviewer, rating, comment);
            reviewManager.addReview(movie, review);
            System.out.println("\nReview added successfully!");
            System.out.printf("Updated Average Rating: %.1f / 5.0 Stars (%d review%s)%n",
                    reviewManager.calculateAverageRating(movie), reviewManager.getTotalReviewCount(movie),
                    reviewManager.getTotalReviewCount(movie) == 1 ? "" : "s");
        } catch (ValidationException e) {
            System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
            if (movie.getStatus() != MovieStatus.RELEASED) {
                System.out.println("Tip: Use Option 4 to update the movie lifecycle to RELEASED before submitting reviews.");
            }
        }
    }

    // =========================================================================
    // 10. VIEW MOVIE REVIEWS & RATING DISTRIBUTION
    // =========================================================================
    private void viewMovieReviews() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected. Please select a movie first.");
            return;
        }

        System.out.println("--- REVIEWS FOR: " + movie.getTitle() + " ---");
        List<Review> reviews = reviewManager.getReviews(movie);
        if (reviews.isEmpty()) {
            System.out.println("No reviews recorded for this movie yet.");
            if (movie.getStatus() != MovieStatus.RELEASED) {
                System.out.println("(Current movie status is " + movie.getStatus() + ". Reviews open once RELEASED.)");
            }
            return;
        }

        System.out.printf("Average Rating: %.1f / 5.0 Stars (based on %d review%s)%n%n",
                reviewManager.calculateAverageRating(movie), reviews.size(), reviews.size() == 1 ? "" : "s");

        // Star breakdown via ReviewManager
        Map<Integer, Integer> distribution = reviewManager.getRatingDistribution(movie);
        System.out.println("Rating Distribution:");
        for (int star = 5; star >= 1; star--) {
            int count = distribution.getOrDefault(star, 0);
            String stars = "*".repeat(star);
            System.out.printf("  %-5s (%d Star%s) : %d review%s%n",
                    stars, star, (star == 1 ? " " : "s"), count, (count == 1 ? "" : "s"));
        }
        System.out.println();

        for (int i = 0; i < reviews.size(); i++) {
            System.out.printf(" %d. %s%n", (i + 1), reviews.get(i));
        }
    }

    // =========================================================================
    // 11. MANAGE PRODUCTION HOUSES
    // =========================================================================
    private void manageProductionHouses() {
        System.out.println("--- PRODUCTION HOUSE MANAGEMENT ---");
        System.out.println(" 1. View & Switch Active Production House");
        System.out.println(" 2. Add New Production House");
        System.out.println(" 0. Back to Main Menu");
        System.out.print("Enter choice: ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                List<ProductionHouse> studios = manager.getAllStudios();
                if (studios.isEmpty()) {
                    System.out.println("No production houses registered.");
                    return;
                }
                ProductionHouse active = manager.getActiveStudio();
                for (int i = 0; i < studios.size(); i++) {
                    ProductionHouse ph = studios.get(i);
                    String mark = (active != null && ph.getId().equalsIgnoreCase(active.getId())) ? " * [ACTIVE]" : "";
                    System.out.printf(" %d. %s%s%n", (i + 1), ph.toString(), mark);
                }
                System.out.print("\nEnter Studio ID to switch (or press Enter to keep current): ");
                String id = readLine();
                if (!id.isEmpty()) {
                    try {
                        manager.setActiveStudioId(id);
                        System.out.println("Active Studio switched to: " + manager.getActiveStudio().getName());
                    } catch (ResourceNotFoundException e) {
                        System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
                    }
                }
            }
            case 2 -> {
                System.out.print("Enter Studio ID (e.g., PH-104): ");
                String id = readLine();
                System.out.print("Enter Studio Name: ");
                String name = readLine();
                System.out.print("Enter Headquarters: ");
                String hq = readLine();

                try {
                    ProductionHouse ph = new ProductionHouse(id, name, hq);
                    manager.addStudio(ph);
                    System.out.println("\nProduction House '" + name + "' registered successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    // =========================================================================
    // 17. PROPS, COSTUMES & EQUIPMENT INVENTORY MANAGEMENT
    // =========================================================================
    private void manageInventory() {
        Movie activeMovie = manager.getActiveMovie();
        String movieTitle = (activeMovie != null) ? activeMovie.getTitle() + " [" + activeMovie.getId() + "]" : "None Selected";

        System.out.println("\n--- PROPS, COSTUMES & EQUIPMENT INVENTORY ---");
        System.out.printf(" Total Units: %d | Available: %d | In-Use: %d | Damaged: %d | Lost: %d%n",
                inventoryManager.getTotalInventoryUnitsCount(),
                inventoryManager.getAvailableUnitsCount(),
                inventoryManager.getAssignedUnitsCount(),
                inventoryManager.getDamagedUnitsCount(),
                inventoryManager.getLostUnitsCount());
        System.out.println(" Active Movie : " + movieTitle);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. View Complete Inventory Catalog (Stock & Availability)");
        System.out.println(" 2. Search & Filter Inventory (Keyword, Category, Condition)");
        System.out.println(" 3. Add New Inventory Item (Prop, Costume, Equipment)");
        System.out.println(" 4. Update Inventory Item (Stock Count, Condition, Notes)");
        System.out.println(" 5. Checkout / Assign Item to Movie & Scene");
        System.out.println(" 6. Return Checked-Out Item (Record Good, Damaged, Lost Units)");
        System.out.println(" 7. View Current Movie's Item Assignments");
        System.out.println(" 8. View All Active Checkouts Across Studios");
        System.out.println(" 0. Back to Main Menu");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter choice (0-8): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> displayInventoryTable(inventoryManager.getAllItems());
            case 2 -> {
                System.out.print("Enter search keyword (name, ID, or notes): ");
                String query = readLine();
                System.out.println("Filter by Category:");
                System.out.println(" 1. All Categories");
                System.out.println(" 2. Props Only");
                System.out.println(" 3. Costumes Only");
                System.out.println(" 4. Equipment Only");
                System.out.print("Choose (1-4): ");
                int cChoice = readInt();
                ItemCategory cat = switch (cChoice) {
                    case 2 -> ItemCategory.PROP;
                    case 3 -> ItemCategory.COSTUME;
                    case 4 -> ItemCategory.EQUIPMENT;
                    default -> null;
                };
                System.out.print("Filter by Condition (e.g. MINT, GOOD, FAIR, DAMAGED, or press Enter for any): ");
                String cond = readLine();

                List<InventoryItem> results = inventoryManager.searchItems(query, cat, cond);
                if (results.isEmpty()) {
                    System.out.println("\nNo inventory items matched your search criteria.");
                } else {
                    System.out.printf("%nFound %d matching item(s):%n", results.size());
                    displayInventoryTable(results);
                }
            }
            case 3 -> {
                System.out.println("\n--- ADD NEW INVENTORY ITEM ---");
                System.out.print("Enter Item ID (e.g. PROP-05, CST-04, EQP-04): ");
                String id = readLine();
                System.out.print("Enter Item Name: ");
                String name = readLine();
                System.out.println("Select Category:");
                System.out.println(" 1. PROP");
                System.out.println(" 2. COSTUME");
                System.out.println(" 3. EQUIPMENT");
                System.out.print("Choose (1-3): ");
                int catChoice = readInt();
                ItemCategory category = switch (catChoice) {
                    case 2 -> ItemCategory.COSTUME;
                    case 3 -> ItemCategory.EQUIPMENT;
                    default -> ItemCategory.PROP;
                };
                System.out.print("Enter Total Quantity Owned: ");
                int qty = readInt();
                System.out.print("Enter Condition (e.g. MINT, GOOD, FAIR, DAMAGED) [Default: GOOD]: ");
                String condition = readLine();
                if (condition.isEmpty()) condition = "GOOD";
                System.out.print("Enter Storage / Usage Notes: ");
                String notes = readLine();

                try {
                    InventoryItem item = new InventoryItem(id, name, category, qty, condition, notes);
                    inventoryManager.addItem(item);
                    System.out.printf("%n[SUCCESS] %s [%s] '%s' added with %d available units!%n",
                            category.name(), id, name, qty);
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 4 -> {
                System.out.print("Enter Item ID to update: ");
                String id = readLine();
                try {
                    InventoryItem item = inventoryManager.getItemOrThrow(id);
                    System.out.printf("Selected: [%s] %s (%s) | Total: %d | Avail: %d | Condition: %s%n",
                            item.getId(), item.getName(), item.getCategory().name(),
                            item.getTotalQuantity(), item.getAvailableQuantity(), item.getCondition());

                    System.out.print("Enter New Total Quantity [Press Enter to keep " + item.getTotalQuantity() + "]: ");
                    String qtyStr = readLine();
                    if (!qtyStr.isEmpty()) {
                        try {
                            item.setTotalQuantity(Integer.parseInt(qtyStr));
                        } catch (Exception e) {
                            System.out.println("[ERROR] " + e.getMessage());
                        }
                    }

                    System.out.print("Enter New Condition [Press Enter to keep '" + item.getCondition() + "']: ");
                    String newCond = readLine();
                    if (!newCond.isEmpty()) item.setCondition(newCond);

                    System.out.print("Enter New Notes [Press Enter to keep current]: ");
                    String newNotes = readLine();
                    if (!newNotes.isEmpty()) item.setNotes(newNotes);

                    System.out.println("\n[SUCCESS] Inventory item [" + item.getId() + "] updated successfully!");
                } catch (ResourceNotFoundException e) {
                    System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
                }
            }
            case 5 -> {
                System.out.println("\n--- CHECKOUT / ASSIGN ITEM TO MOVIE & SCENE ---");
                Movie movie = manager.getActiveMovie();
                if (movie == null) {
                    System.out.println("No active movie selected. Please select a movie first.");
                    return;
                }
                List<InventoryItem> availableItems = inventoryManager.getAllItems();
                if (availableItems.isEmpty()) {
                    System.out.println("Inventory is empty. Please add items first.");
                    return;
                }
                displayInventoryTable(availableItems);

                System.out.print("\nEnter Item ID to checkout: ");
                String id = readLine();
                System.out.printf("Assign to Active Movie '%s' [%s]? (Press Enter for yes, or type alternate Movie ID): ",
                        movie.getTitle(), movie.getId());
                String movInput = readLine();
                Movie targetMovie = movie;
                if (!movInput.isEmpty()) {
                    targetMovie = manager.findMovieAnywhere(movInput);
                    if (targetMovie == null) {
                        System.out.println("[ERROR] Movie with ID '" + movInput + "' not found.");
                        return;
                    }
                }

                System.out.println("Available scenes in '" + targetMovie.getTitle() + "':");
                for (Scene sc : targetMovie.getScenes()) {
                    System.out.printf(" Scene #%d: %s (%s)%n", sc.getSceneNumber(), sc.getTitle(), sc.getLocation());
                }
                System.out.print("Enter Scene Number to attach (or 0 for Movie-Wide assignment): ");
                int scNum = readInt();

                System.out.print("Enter Quantity to checkout: ");
                int qty = readInt();

                System.out.print("Enter Checkout / Shoot Date (YYYY-MM-DD): ");
                String date = readLine();
                if (date.isEmpty()) date = "2026-10-12";

                System.out.print("Enter Purpose / Scene Notes: ");
                String notes = readLine();

                try {
                    ItemAssignment asg = inventoryManager.checkoutItem(id, targetMovie, scNum, qty, date, notes);
                    System.out.printf("%n[SUCCESS] Assignment [%s] created! Checked out %d unit(s) of [%s] to '%s' (%s).%n",
                            asg.getAssignmentId(), qty, id, targetMovie.getTitle(), asg.getSceneReference());
                } catch (ResourceNotFoundException e) {
                    System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 6 -> {
                System.out.println("\n--- RETURN CHECKED-OUT INVENTORY ITEM ---");
                List<ItemAssignment> activeAsgs = inventoryManager.getActiveAssignments();
                if (activeAsgs.isEmpty()) {
                    System.out.println("No items are currently checked out.");
                    return;
                }
                displayAssignmentsTable(activeAsgs);

                System.out.print("\nEnter Assignment ID to return (e.g. ASG-001): ");
                String asgId = readLine();
                ItemAssignment asg = inventoryManager.findAssignmentById(asgId);
                if (asg == null || !asg.isCheckedOut()) {
                    System.out.println("[ERROR] Active assignment not found for ID '" + asgId + "'.");
                    return;
                }
                System.out.printf("Selected Assignment [%s]: %d unit(s) of [%s] checked out to %s.%n",
                        asg.getAssignmentId(), asg.getQuantity(), asg.getItemId(), asg.getMovieId());

                System.out.printf("Enter Quantity Returned in GOOD condition [0-%d]: ", asg.getQuantity());
                int good = readInt();
                System.out.printf("Enter Quantity Returned DAMAGED [0-%d]: ", asg.getQuantity() - good);
                int dmg = readInt();
                System.out.printf("Enter Quantity LOST / UNACCOUNTED [0-%d]: ", asg.getQuantity() - good - dmg);
                int lost = readInt();

                System.out.print("Enter Return Date (YYYY-MM-DD): ");
                String retDate = readLine();
                if (retDate.isEmpty()) retDate = "2026-10-12";

                System.out.print("Enter Return / Inspection Notes: ");
                String retNotes = readLine();

                try {
                    inventoryManager.returnItem(asgId, good, dmg, lost, retDate, retNotes);
                    System.out.printf("%n[SUCCESS] Assignment [%s] reconciled! (Good: %d, Damaged: %d, Lost: %d)%n",
                            asgId, good, dmg, lost);
                } catch (Exception e) {
                    System.out.println("\n[ERROR] " + e.getMessage());
                }
            }
            case 7 -> {
                Movie movie = manager.getActiveMovie();
                if (movie == null) {
                    System.out.println("No active movie selected.");
                    return;
                }
                List<ItemAssignment> movieAsgs = inventoryManager.getAssignmentsForMovie(movie.getId());
                if (movieAsgs.isEmpty()) {
                    System.out.println("No inventory assignments recorded for '" + movie.getTitle() + "'.");
                } else {
                    System.out.printf("%n--- INVENTORY ASSIGNMENTS FOR: %s (%d) ---%n",
                            movie.getTitle().toUpperCase(), movieAsgs.size());
                    displayAssignmentsTable(movieAsgs);
                }
            }
            case 8 -> {
                List<ItemAssignment> active = inventoryManager.getActiveAssignments();
                if (active.isEmpty()) {
                    System.out.println("No active checkouts across any productions.");
                } else {
                    System.out.printf("%n--- ALL ACTIVE CHECKOUTS ACROSS STUDIOS (%d) ---%n", active.size());
                    displayAssignmentsTable(active);
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option. Please choose between 0 and 8.");
        }
    }

    private void displayInventoryTable(List<InventoryItem> items) {
        if (items.isEmpty()) {
            System.out.println("No inventory items found.");
            return;
        }
        System.out.println("\n=========================================================================================================");
        System.out.printf(" %-8s | %-28s | %-12s | %-6s | %-6s | %-6s | %-5s | %-9s | %s%n",
                "ITEM ID", "ITEM NAME", "CATEGORY", "TOTAL", "AVAIL", "IN-USE", "DMG", "CONDITION", "STATUS BADGE");
        System.out.println("---------+------------------------------+--------------+--------+--------+--------+-------+-----------+-----------------");
        for (InventoryItem itm : items) {
            String nameStr = itm.getName();
            if (nameStr.length() > 28) nameStr = nameStr.substring(0, 25) + "...";
            System.out.printf(" %-8s | %-28s | %-12s | %-6d | %-6d | %-6d | %-5d | %-9s | %s%n",
                    itm.getId(), nameStr, itm.getCategory().name(),
                    itm.getTotalQuantity(), itm.getAvailableQuantity(), itm.getAssignedQuantity(),
                    itm.getDamagedQuantity(), itm.getCondition(), itm.getAvailabilityBadge());
        }
        System.out.println("=========================================================================================================");
    }

    private void displayAssignmentsTable(List<ItemAssignment> assignments) {
        if (assignments.isEmpty()) {
            System.out.println("No item assignments found.");
            return;
        }
        System.out.println("\n=========================================================================================================");
        System.out.printf(" %-8s | %-8s | %-8s | %-12s | %-4s | %-12s | %-10s | %s%n",
                "ASG ID", "ITEM ID", "MOVIE ID", "SCENE", "QTY", "STATUS", "OUT DATE", "RETURN DATE");
        System.out.println("---------+----------+----------+--------------+------+--------------+------------+--------------------");
        for (ItemAssignment a : assignments) {
            System.out.printf(" %-8s | %-8s | %-8s | %-12s | %-4d | %-12s | %-10s | %s%n",
                    a.getAssignmentId(), a.getItemId(), a.getMovieId(),
                    a.getSceneReference(), a.getQuantity(), a.getStatus(),
                    a.getAssignedDate(), a.getReturnDate());
        }
        System.out.println("=========================================================================================================");
    }

    // =========================================================================
    // 18. POSTER & PROMOTIONAL ASSETS MANAGEMENT
    // =========================================================================
    private void managePromotionalAssets() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected. Please select a movie first.");
            return;
        }

        List<PromotionalAsset> movieAssets = promotionManager.getAssetsForMovie(movie.getId());
        int approved = 0, pending = 0, rejected = 0;
        for (PromotionalAsset a : movieAssets) {
            if (a.isApproved()) approved++;
            else if (a.isPending()) pending++;
            else if (a.isRejected()) rejected++;
        }

        System.out.println("\n--- POSTER & PROMOTIONAL ASSETS: " + movie.getTitle().toUpperCase() + " ---");
        System.out.printf(" Total Assets: %d | Approved: %d | Pending Review: %d | Revisions Required: %d%n",
                movieAssets.size(), approved, pending, rejected);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. View Promotional Assets for Active Movie");
        System.out.println(" 2. Add New Promotional Asset (Poster, Teaser, Trailer, Banner, Social)");
        System.out.println(" 3. Search & Filter Promotional Assets");
        System.out.println(" 4. Approve Promotional Asset (Sign-Off for Public Release)");
        System.out.println(" 5. Reject Promotional Asset (Request Creative Revisions)");
        System.out.println(" 6. Submit New Asset Revision (Upload New File / URL & Increment Rev)");
        System.out.println(" 7. View All Promotional Assets Across All Movies");
        System.out.println(" 0. Back to Main Menu");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter choice (0-7): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> displayPromotionalAssetsTable(movieAssets);
            case 2 -> {
                System.out.println("\n--- ADD NEW PROMOTIONAL ASSET ---");
                int nextIdx = promotionManager.getAllAssets().size() + 1;
                String defaultId = "AST-" + String.format("%02d", nextIdx);
                System.out.print("Enter Asset ID [Press Enter for " + defaultId + "]: ");
                String idInput = readLine();
                String assetId = idInput.isEmpty() ? defaultId : idInput;

                System.out.println("Select Asset Type:");
                System.out.println(" 1. POSTER (First Look / Theatrical Poster)");
                System.out.println(" 2. TEASER (Official Teaser Video)");
                System.out.println(" 3. TRAILER (Theatrical Trailer)");
                System.out.println(" 4. BANNER (Web / Billboard Banner)");
                System.out.println(" 5. SOCIAL_MEDIA (Motion Poster / Reel)");
                System.out.print("Choose (1-5): ");
                int tChoice = readInt();
                AssetType type = switch (tChoice) {
                    case 2 -> AssetType.TEASER;
                    case 3 -> AssetType.TRAILER;
                    case 4 -> AssetType.BANNER;
                    case 5 -> AssetType.SOCIAL_MEDIA;
                    default -> AssetType.POSTER;
                };

                System.out.print("Enter Asset Title: ");
                String title = readLine();
                System.out.print("Enter File Path or Cloud URL (e.g. assets/posters/hero.jpg): ");
                String path = readLine();
                System.out.print("Enter Language (e.g. English, Telugu, Hindi, Multilingual): ");
                String lang = readLine();
                System.out.print("Enter Platform (e.g. Theaters & IMAX, YouTube 4K, Instagram): ");
                String platform = readLine();

                try {
                    PromotionalAsset asset = new PromotionalAsset(assetId, movie.getId(), type, title, path, lang, platform);
                    promotionManager.addAsset(asset);
                    System.out.printf("%n[SUCCESS] Asset [%s] '%s' added with status: %s!%n",
                            assetId, title, asset.getStatusBadge().trim());
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 3 -> {
                System.out.print("Enter search keyword (title, ID, or platform): ");
                String q = readLine();
                System.out.println("Filter by Type (0: All, 1: Poster, 2: Teaser, 3: Trailer, 4: Banner, 5: Social): ");
                int tOpt = readInt();
                AssetType type = switch (tOpt) {
                    case 1 -> AssetType.POSTER;
                    case 2 -> AssetType.TEASER;
                    case 3 -> AssetType.TRAILER;
                    case 4 -> AssetType.BANNER;
                    case 5 -> AssetType.SOCIAL_MEDIA;
                    default -> null;
                };
                System.out.println("Filter by Status (0: All, 1: Pending, 2: Approved, 3: Rejected): ");
                int sOpt = readInt();
                ApprovalStatus status = switch (sOpt) {
                    case 1 -> ApprovalStatus.PENDING;
                    case 2 -> ApprovalStatus.APPROVED;
                    case 3 -> ApprovalStatus.REJECTED;
                    default -> null;
                };

                List<PromotionalAsset> matches = promotionManager.searchAssets(q, movie.getId(), type, status);
                if (matches.isEmpty()) {
                    System.out.println("\nNo promotional assets matched your criteria.");
                } else {
                    System.out.printf("%nFound %d matching asset(s):%n", matches.size());
                    displayPromotionalAssetsTable(matches);
                }
            }
            case 4 -> {
                System.out.println("\n--- APPROVE PROMOTIONAL ASSET ---");
                displayPromotionalAssetsTable(movieAssets);
                System.out.print("\nEnter Asset ID to approve: ");
                String id = readLine();
                PromotionalAsset target = promotionManager.findAssetById(id);
                if (target == null) {
                    System.out.println("[ERROR] Asset '" + id + "' not found.");
                    return;
                }
                System.out.printf("Selected: [%s] '%s' (%s) | Status: %s%n",
                        target.getAssetId(), target.getTitle(), target.getAssetType().name(), target.getStatusBadge().trim());
                System.out.print("Enter Approver Name (e.g. Marketing Lead / Producer): ");
                String approver = readLine();
                System.out.print("Enter Approval Notes / Sign-Off Remarks: ");
                String notes = readLine();

                target.approve(approver, notes);
                System.out.printf("%n[APPROVED] Asset [%s] '%s' is now APPROVED for public release!%n",
                        target.getAssetId(), target.getTitle());
            }
            case 5 -> {
                System.out.println("\n--- REJECT PROMOTIONAL ASSET (REQUEST REVISIONS) ---");
                displayPromotionalAssetsTable(movieAssets);
                System.out.print("\nEnter Asset ID to reject: ");
                String id = readLine();
                PromotionalAsset target = promotionManager.findAssetById(id);
                if (target == null) {
                    System.out.println("[ERROR] Asset '" + id + "' not found.");
                    return;
                }
                System.out.printf("Selected: [%s] '%s' (%s)%n", target.getAssetId(), target.getTitle(), target.getAssetType().name());
                System.out.print("Enter Reviewer Name (e.g. Creative Director): ");
                String reviewer = readLine();
                System.out.print("Enter Rejection Reason & Revision Requirements: ");
                String reason = readLine();

                target.reject(reviewer, reason);
                System.out.printf("%n[REJECTED] Asset [%s] '%s' status transitioned to REJECTED. Revision requested.%n",
                        target.getAssetId(), target.getTitle());
            }
            case 6 -> {
                System.out.println("\n--- SUBMIT NEW ASSET REVISION ---");
                displayPromotionalAssetsTable(movieAssets);
                System.out.print("\nEnter Asset ID to submit new revision: ");
                String id = readLine();
                PromotionalAsset target = promotionManager.findAssetById(id);
                if (target == null) {
                    System.out.println("[ERROR] Asset '" + id + "' not found.");
                    return;
                }
                System.out.printf("Current Version: v%d | Path: %s%n", target.getRevisionNumber(), target.getFilePathOrUrl());
                System.out.print("Enter New File Path or Cloud URL [Press Enter to keep current]: ");
                String newPath = readLine();
                System.out.print("Enter Revision Description / Changelog: ");
                String notes = readLine();

                try {
                    target.submitRevision(newPath, notes);
                    System.out.printf("%n[REVISION UPLOADED] Asset [%s] bumped to revision v%d. Status reset to PENDING for re-review!%n",
                            target.getAssetId(), target.getRevisionNumber());
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 7 -> {
                List<PromotionalAsset> all = promotionManager.getAllAssets();
                if (all.isEmpty()) {
                    System.out.println("No promotional assets registered across any movies.");
                } else {
                    System.out.printf("%n--- ALL PROMOTIONAL ASSETS ACROSS ALL MOVIES (%d) ---%n", all.size());
                    displayPromotionalAssetsTable(all);
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option. Please choose between 0 and 7.");
        }
    }

    private void displayPromotionalAssetsTable(List<PromotionalAsset> assets) {
        if (assets.isEmpty()) {
            System.out.println("No promotional assets found.");
            return;
        }
        System.out.println("\n=========================================================================================================");
        System.out.printf(" %-8s | %-12s | %-24s | %-10s | %-16s | %-5s | %-12s%n",
                "ASSET ID", "TYPE", "TITLE", "LANGUAGE", "PLATFORM", "REV", "STATUS");
        System.out.println("---------+--------------+--------------------------+------------+------------------+-------+--------------");
        for (PromotionalAsset a : assets) {
            String titleStr = a.getTitle();
            if (titleStr.length() > 24) titleStr = titleStr.substring(0, 21) + "...";
            String platStr = a.getPlatform();
            if (platStr.length() > 16) platStr = platStr.substring(0, 13) + "...";
            System.out.printf(" %-8s | %-12s | %-24s | %-10s | %-16s | v%-4d | %s%n",
                    a.getAssetId(), a.getAssetType().name(), titleStr,
                    a.getLanguage(), platStr, a.getRevisionNumber(), a.getStatusBadge().trim());
            if (a.getApprovalDetails() != null && !a.getApprovalDetails().isEmpty()) {
                System.out.printf("          Details: %s%n", a.getApprovalDetails());
            }
        }
        System.out.println("=========================================================================================================");
    }

    // =========================================================================
    // 19. PRODUCTION REPORTS & EXECUTIVE DASHBOARD
    // =========================================================================
    private void viewProductionReports() {
        Movie movie = manager.getActiveMovie();
        System.out.println("\n--- PRODUCTION REPORTS & EXECUTIVE DASHBOARD ---");
        System.out.printf(" Active Movie  : %s%n", (movie != null ? movie.getTitle() + " [" + movie.getId() + "]" : "None"));
        System.out.printf(" Active Studio : %s%n", (manager.getActiveStudio() != null ? manager.getActiveStudio().getName() : "None"));
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. View Live Executive Production Dashboard (KPIs & High-Level Health)");
        System.out.println(" 2. View Comprehensive Analytical Production Audit Report (Full Multi-Section)");
        System.out.println(" 3. View Studio Portfolio Financial & Movie Report");
        System.out.println(" 4. Export Movie Production Report to Text File (data/reports/)");
        System.out.println(" 0. Back to Main Menu");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter choice (0-4): ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                System.out.println();
                System.out.println(reportService.generateExecutiveDashboard(movie));
            }
            case 2 -> {
                System.out.println();
                System.out.println(reportService.generateComprehensiveAnalyticalReport(movie));
            }
            case 3 -> {
                System.out.println();
                System.out.println(reportService.generateStudioConsolidatedReport(manager.getActiveStudio()));
            }
            case 4 -> {
                if (movie == null) {
                    System.out.println("No active movie selected to export.");
                    return;
                }
                try {
                    File exported = reportService.exportProductionReportToFile(movie);
                    System.out.println("\n==================================================");
                    System.out.println(" PRODUCTION REPORT EXPORTED SUCCESSFULLY!         ");
                    System.out.println("==================================================");
                    System.out.println("File Location: " + exported.getAbsolutePath());
                    System.out.println("You can open this text document to review executive KPIs and detailed ledgers.");
                } catch (Exception e) {
                    System.out.println("\n[EXPORT ERROR] Failed to export production report: " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option. Please choose between 0 and 4.");
        }
    }

    // =========================================================================
    // 20 & 21. FILE HANDLING (SAVE / LOAD)
    // =========================================================================
    private void saveData() {
        System.out.println("Saving all production data to TXT files in data/ directory...");
        try {
            manager.saveData();
            System.out.println("Data saved successfully!");
            System.out.println("Files updated: data/studios.txt, movies.txt, scenes.txt, cast_crew.txt, expenses.txt, reviews.txt");
        } catch (IOException e) {
            System.err.println("[FILE ERROR] Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        System.out.println("Loading production data from TXT files in data/ directory...");
        try {
            manager.loadData();
            System.out.println("Data loaded successfully from text files!");
        } catch (IOException e) {
            System.err.println("[FILE ERROR] Error loading data: " + e.getMessage());
        }
    }

    // =========================================================================
    // 22. DROP MOVIE / CANCEL PRODUCTION
    // =========================================================================
    private void dropMovie() {
        System.out.println("--- DROP MOVIE / CANCEL PRODUCTION ---");
        System.out.print("Enter Movie ID to drop (e.g. MOV-01): ");
        String movieId = readLine();
        if (movieId.isEmpty()) {
            System.out.println("\n[ERROR] Movie ID cannot be empty.");
            return;
        }

        try {
            Movie movie = manager.findMovieOrThrow(movieId);

            if (movie.getStatus() == MovieStatus.DROPPED) {
                System.out.println("\n[VALIDATION ERROR] Movie '" + movie.getTitle()
                        + "' [" + movie.getId() + "] is already DROPPED / CANCELLED.");
                System.out.printf("Incurred financial loss was already recorded as $%,.2f.%n", movie.getFinancialLoss());
                return;
            }

            double actualExpenses = movie.getTotalExpenses();
            double plannedBudget = movie.getBudget();
            double salvagedBudget = movie.getRemainingBudget();

            System.out.println("\nMovie Found:");
            System.out.println("  Movie ID         : " + movie.getId());
            System.out.println("  Title            : " + movie.getTitle());
            System.out.println("  Director         : " + movie.getDirector());
            System.out.println("  Current Status   : " + movie.getStatus());
            System.out.printf ("  Planned Budget   : $%,.2f%n", plannedBudget);
            System.out.printf ("  Actual Expenses  : $%,.2f (Financial loss upon cancellation)%n", actualExpenses);
            System.out.printf ("  Salvaged Funds   : $%,.2f (Unspent budget)%n", salvagedBudget);
            System.out.println("\nWARNING: Dropping this movie will cancel its production permanently.");
            System.out.print("Are you sure you want to drop this movie? (yes/no): ");
            String confirmation = readLine();

            if (!confirmation.equalsIgnoreCase("yes") && !confirmation.equalsIgnoreCase("y")) {
                System.out.println("\nOperation cancelled. Movie was not dropped.");
                return;
            }

            double recordedLoss = manager.dropMovie(movieId);
            System.out.println("\n==================================================");
            System.out.println(" MOVIE PRODUCTION SUCCESSFULLY CANCELLED         ");
            System.out.println("==================================================");
            System.out.println(" Movie '" + movie.getTitle() + "' status has been set to DROPPED.");
            System.out.printf (" Financial Loss Incurred: $%,.2f (Based on actual expenses)%n", recordedLoss);
            System.out.printf (" Unspent Budget Salvaged: $%,.2f%n", movie.getRemainingBudget());
            System.out.println(" Movie records and historical expense entries have been preserved.");
            System.out.println("==================================================");

        } catch (ResourceNotFoundException e) {
            System.out.println("\n[RESOURCE NOT FOUND] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
        }
    }

    // =========================================================================
    // HELPER INPUT METHODS
    // =========================================================================
    private String readLine() {
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    private int readInt() {
        while (true) {
            if (!scanner.hasNextLine()) {
                return 0;
            }
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid integer. Please enter a valid number: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            if (!scanner.hasNextLine()) {
                return 0.0;
            }
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter a valid amount: ");
            }
        }
    }

    private String readMultiLineScriptText() {
        System.out.println(" (Enter screenplay dialogue & action lines below.)");
        System.out.println(" (Type ':done' on a new line or press Enter twice to finish):");
        StringBuilder sb = new StringBuilder();
        int consecutiveEmptyLines = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase(":done")) {
                break;
            }
            if (line.trim().isEmpty()) {
                consecutiveEmptyLines++;
                if (consecutiveEmptyLines >= 2) {
                    break;
                }
                sb.append("\n");
            } else {
                consecutiveEmptyLines = 0;
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private void pause() {
        if (!scanner.hasNextLine()) {
            return;
        }
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
