package com.cineflow.ui;

import com.cineflow.exception.BudgetExceededException;
import com.cineflow.exception.CineFlowException;
import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ScheduleConflictException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.Actor;
import com.cineflow.model.CrewMember;
import com.cineflow.model.Expense;
import com.cineflow.model.Movie;
import com.cineflow.model.MovieStatus;
import com.cineflow.model.Person;
import com.cineflow.model.ProductionHouse;
import com.cineflow.model.Review;
import com.cineflow.model.Scene;
import com.cineflow.service.MovieManager;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Text-based console user interface for CineFlow.
 * Demonstrates: Console Input Handling (Scanner), Menu Loops, Sub-menus,
 * Formatted Output, Exception Handling (5 custom exceptions), and Graceful Error Recovery.
 */
public class ConsoleMenu {
    private final MovieManager manager;
    private final Scanner scanner;

    public ConsoleMenu(MovieManager manager) {
        this.manager = manager;
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
            System.out.println(" 6. Manage Scenes (View, Add, Update, Schedule Shoot)");
            System.out.println(" 7. Manage Cast & Crew (View, Add Actor/Crew)");
            System.out.println(" 8. Manage Budget & Expenses");
            System.out.println(" 9. Add Movie Review");
            System.out.println(" 10. View Movie Reviews");
            System.out.println(" 11. Manage Production Houses (View, Switch, Add)");
            System.out.println(" 12. Save Data to Text Files");
            System.out.println(" 13. Load Data from Text Files");
            System.out.println(" 0. Exit");
            System.out.println("==================================================");
            System.out.print("Enter choice (0-13): ");

            int choice = readInt();
            System.out.println();

            switch (choice) {
                case 1 -> viewAndSwitchMovie();
                case 2 -> addNewMovie();
                case 3 -> searchMovies();
                case 4 -> updateMovieStatus();
                case 5 -> viewMovieStatistics();
                case 6 -> manageScenes();
                case 7 -> manageCastAndCrew();
                case 8 -> manageBudgetAndExpenses();
                case 9 -> addMovieReview();
                case 10 -> viewMovieReviews();
                case 11 -> manageProductionHouses();
                case 12 -> saveData();
                case 13 -> loadData();
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
                default -> System.out.println("Invalid option. Please choose between 0 and 13.");
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
            System.out.println("\nMovie '" + title + "' added successfully with status: " + initialStatus);
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
        System.out.printf (" Remaining      : $%,.2f (%.1f%% spent)%n",
                movie.getRemainingBudget(), movie.getBudgetUtilizationPercentage());
        System.out.println(" Total Scenes   : " + movie.getScenes().size());
        System.out.println(" Cast & Crew    : " + movie.getCastAndCrew().size() + " members");
        System.out.println(" Total Reviews  : " + movie.getReviews().size());

        if (movie.getReviews().isEmpty()) {
            System.out.println(" Average Rating : No reviews yet");
        } else {
            System.out.printf (" Average Rating : %.1f / 5.0 Stars (%d review%s)%n",
                    movie.getAverageRating(), movie.getReviews().size(),
                    movie.getReviews().size() == 1 ? "" : "s");
        }
        System.out.println("==================================================");
    }

    // =========================================================================
    // 6. SCENE MANAGEMENT & SHOOTING SCHEDULE CONFLICT HANDLING
    // =========================================================================
    private void manageScenes() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("--- SCENE MANAGEMENT FOR: " + movie.getTitle() + " ---");
        System.out.println(" 1. View All Scenes");
        System.out.println(" 2. Add New Scene");
        System.out.println(" 3. Update Scene Status");
        System.out.println(" 4. Schedule Scene Shoot Date");
        System.out.println(" 0. Back to Main Menu");
        System.out.print("Enter choice: ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                List<Scene> scenes = movie.getScenes();
                if (scenes.isEmpty()) {
                    System.out.println("No scenes recorded for this movie.");
                } else {
                    System.out.println("\n--- SCENE LIST ---");
                    for (Scene sc : scenes) {
                        System.out.println(" " + sc);
                    }
                }
            }
            case 2 -> {
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

                try {
                    Scene sc = new Scene(sceneNum, title, location, status, shootDate);
                    movie.addScene(sc);
                    System.out.println("\nScene #" + sceneNum + " added successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                } catch (ScheduleConflictException e) {
                    System.out.println("\n[SCHEDULE CONFLICT] " + e.getMessage());
                }
            }
            case 3 -> {
                List<Scene> scenes = movie.getScenes();
                if (scenes.isEmpty()) {
                    System.out.println("No scenes to update.");
                    return;
                }
                System.out.print("Enter Scene Number to update: ");
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
            case 4 -> {
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
    // 7. CAST AND CREW MANAGEMENT (POLYMORPHISM & INHERITANCE)
    // =========================================================================
    private void manageCastAndCrew() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected.");
            return;
        }

        System.out.println("--- CAST & CREW FOR: " + movie.getTitle() + " ---");
        System.out.println(" 1. View Cast & Crew List");
        System.out.println(" 2. Add Actor");
        System.out.println(" 3. Add Crew Member");
        System.out.println(" 0. Back to Main Menu");
        System.out.print("Enter choice: ");

        int choice = readInt();
        switch (choice) {
            case 1 -> {
                List<Person> personnel = movie.getCastAndCrew();
                if (personnel.isEmpty()) {
                    System.out.println("No cast or crew added yet.");
                } else {
                    System.out.println("\n----------------------------------------------------------------------------------");
                    System.out.printf("  %-8s %-22s | %-26s | %s%n", "ID", "Name", "Role/Specialization", "Email");
                    System.out.println("----------------------------------------------------------------------------------");
                    for (Person p : personnel) {
                        p.displayInfo();
                    }
                    System.out.println("----------------------------------------------------------------------------------");
                }
            }
            case 2 -> {
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
                    movie.addPerson(actor);
                    System.out.println("\nActor '" + name + "' added successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 3 -> {
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
                    movie.addPerson(crew);
                    System.out.println("\nCrew Member '" + name + "' added successfully!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("Invalid option.");
        }
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
                System.out.printf("Remaining Budget : $%,12.2f%n%n", movie.getRemainingBudget());

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
            movie.addReview(review);
            System.out.println("\nReview added successfully!");
            System.out.printf("Updated Average Rating: %.1f / 5.0 Stars (%d review%s)%n",
                    movie.getAverageRating(), movie.getReviews().size(),
                    movie.getReviews().size() == 1 ? "" : "s");
        } catch (ValidationException e) {
            System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
            if (movie.getStatus() != MovieStatus.RELEASED) {
                System.out.println("Tip: Use Option 4 to update the movie lifecycle to RELEASED before submitting reviews.");
            }
        }
    }

    // =========================================================================
    // 10. VIEW MOVIE REVIEWS
    // =========================================================================
    private void viewMovieReviews() {
        Movie movie = manager.getActiveMovie();
        if (movie == null) {
            System.out.println("No active movie selected. Please select a movie first.");
            return;
        }

        System.out.println("--- REVIEWS FOR: " + movie.getTitle() + " ---");
        List<Review> reviews = movie.getReviews();
        if (reviews.isEmpty()) {
            System.out.println("No reviews recorded for this movie yet.");
            if (movie.getStatus() != MovieStatus.RELEASED) {
                System.out.println("(Current movie status is " + movie.getStatus() + ". Reviews open once RELEASED.)");
            }
            return;
        }

        System.out.printf("Average Rating: %.1f / 5.0 Stars (based on %d review%s)%n%n",
                movie.getAverageRating(), reviews.size(), reviews.size() == 1 ? "" : "s");
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
    // 12 & 13. FILE HANDLING (SAVE / LOAD)
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

    private void pause() {
        if (!scanner.hasNextLine()) {
            return;
        }
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
