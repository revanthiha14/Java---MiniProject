package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
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
import com.cineflow.model.ApprovalStatus;
import com.cineflow.model.AssetType;
import com.cineflow.model.InventoryItem;
import com.cineflow.model.ItemAssignment;
import com.cineflow.model.ItemCategory;
import com.cineflow.model.PromotionalAsset;
import com.cineflow.model.Review;
import com.cineflow.model.Scene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central management service for studios, movies, and production data.
 * Demonstrates: HashMap usage for key-value lookups, ArrayList management,
 * modular service design, and checked exception handling (ResourceNotFoundException, ValidationException).
 */
public class MovieManager {
    private final Map<String, ProductionHouse> studios;
    private final FileService fileService;
    private final CastManager castManager;
    private final ReviewManager reviewManager;
    private final InventoryManager inventoryManager;
    private final PromotionManager promotionManager;
    private final ReportService reportService;
    private String activeStudioId;
    private String activeMovieId;

    public MovieManager() {
        this.studios = new HashMap<>();
        this.fileService = new FileService();
        this.castManager = new CastManager();
        this.reviewManager = new ReviewManager();
        this.inventoryManager = new InventoryManager();
        this.promotionManager = new PromotionManager();
        this.reportService = new ReportService(this, this.inventoryManager, this.promotionManager);
        initializeData();
    }

    private void initializeData() {
        try {
            if (fileService.hasExistingData()) {
                loadData();
                ensureScreenplayFilesExist();
                ensureCallSheetFilesExist();
                ensureDistributionFilesExist();
                if (!fileService.hasExistingInventoryData()) {
                    seedDefaultInventory();
                    fileService.saveInventory(inventoryManager);
                }
                if (!fileService.hasExistingPromotionalData()) {
                    seedDefaultPromotionalAssets();
                    fileService.savePromotionalAssets(promotionManager);
                }
            } else {
                seedInitialData();
                saveData();
            }
        } catch (Exception e) {
            System.err.println("[WARNING] Data initialization issue (" + e.getMessage() + "). Seeding default records.");
            seedInitialData();
        }
    }

    private void ensureScreenplayFilesExist() {
        for (Movie m : getAllMovies()) {
            File f = fileService.getScreenplayFile(m);
            if (f == null || !f.exists() || f.length() == 0) {
                autoSyncScreenplayFile(m);
            }
        }
    }

    private void ensureCallSheetFilesExist() {
        for (Movie m : getAllMovies()) {
            if (m.getCallSheets().isEmpty()) {
                seedDefaultCallSheetsForMovie(m);
            }
            for (CallSheet cs : m.getCallSheets()) {
                File f = fileService.getCallSheetFile(m, cs);
                if (f == null || !f.exists() || f.length() == 0) {
                    autoSyncCallSheetFile(m, cs);
                }
            }
        }
    }

    private void ensureDistributionFilesExist() {
        for (Movie m : getAllMovies()) {
            if (m.getDistributionReleases().isEmpty()) {
                seedDefaultDistributionForMovie(m);
            }
            File f = fileService.getDistributionFile(m);
            if (f == null || !f.exists() || f.length() == 0) {
                autoSyncDistributionFile(m);
            }
        }
    }

    private void seedDefaultDistributionForMovie(Movie m) {
        if (m == null) return;
        try {
            String theDate = (m.getReleaseYear() <= 2024)
                    ? m.getReleaseYear() + "-12-05"
                    : m.getReleaseYear() + "-11-20";
            String theStatus = switch (m.getStatus()) {
                case RELEASED -> "CLOSED_RUN";
                case POST_PRODUCTION -> "ADVANCE_BOOKING_OPEN";
                default -> "SCHEDULED";
            };
            int screens = switch (m.getGenre().toLowerCase()) {
                case "action" -> 5500;
                case "sci-fi" -> 4200;
                case "neo-noir" -> 3800;
                default -> 2500;
            };
            String distributor = switch (m.getGenre().toLowerCase()) {
                case "action" -> "Mythri & AA Films Worldwide";
                case "sci-fi" -> "Paramount & Warner Bros.";
                case "neo-noir" -> "Warner Bros. Pictures";
                default -> "PVR Inox Pictures";
            };
            String ott = "Netflix";
            String digDate = (m.getReleaseYear() <= 2024)
                    ? (m.getReleaseYear() + 1) + "-01-28"
                    : (m.getReleaseYear() + 1) + "-01-15";

            DistributionRelease rel1 = new DistributionRelease("REL-01", "Domestic / Primary Region",
                    distributor, theDate, theStatus, screens, ott, digDate,
                    "45-day theatrical exclusivity; Dolby Atmos, IMAX & standard DCI screens.");
            m.addDistributionRelease(rel1);

            DistributionRelease rel2 = new DistributionRelease("REL-02", "Overseas / International",
                    "Global Film Alliance", theDate, theStatus,
                    Math.max(1000, screens / 3), ott, digDate,
                    "Simultaneous multi-language subtitled release across 45+ international territories.");
            m.addDistributionRelease(rel2);
        } catch (Exception e) {
            System.err.println("[WARNING] Could not seed default distribution for " + m.getTitle() + ": " + e.getMessage());
        }
    }

    private void seedDefaultCallSheetsForMovie(Movie m) {
        if (m == null) return;
        try {
            int totalDays = (m.getScenes().isEmpty()) ? 30 : Math.max(30, m.getScenes().size() * 5);
            CallSheet cs1 = new CallSheet("CS-01", 1, totalDays, "2026-11-10", "06:30 AM",
                    "Soundstage A - Main Production Rig (" + m.getTitle() + " Set)",
                    "Indoor Stage (Climate Controlled, Ambient 21°C)",
                    "Nearest Emergency Medical Center (Emergency: 911 / 108 / 999)", "WRAPPED");
            if (!m.getScenes().isEmpty()) {
                cs1.addScheduledScene(m.getScenes().get(0).getSceneNumber());
            }
            for (Person p : m.getCastAndCrew()) {
                if (p instanceof Actor a) {
                    cs1.addCastCall(new CastCallEntry(a.getId(), a.getName(), a.getCharacterName(),
                            "05:30 AM", "06:15 AM", "07:30 AM", "Wardrobe check & prosthetic fitting"));
                    break;
                }
            }
            if (cs1.getCastCalls().isEmpty()) {
                cs1.addCastCall(new CastCallEntry("ACT-01", "Lead Talent", "Main Protagonist",
                        "05:30 AM", "06:15 AM", "07:30 AM", "Wardrobe check & on-camera rehearsal"));
            }
            cs1.setDepartmentNotes("CAMERA: Primary A & B camera packages on Technocrane.\n"
                    + "SOUND: Soundstage isolation baffles engaged; wireless lavaliers on lead talent.\n"
                    + "SFX/STUNT: Controlled atmosphere haze test passed; safety officer on set.\n"
                    + "CATERING: Hot breakfast 06:00 AM | Catered hot lunch 12:30 PM (Courtesy 30 min).");
            cs1.recordWrap((!m.getScenes().isEmpty() ? m.getScenes().get(0).getScriptPages() : 2.5),
                    "First camera setup achieved on schedule. Day 1 principal photography wrapped cleanly.");
            m.addCallSheet(cs1);

            CallSheet cs2 = new CallSheet("CS-02", 2, totalDays, "2026-11-15", "07:00 AM",
                    "Exterior Set / Stage 2 Backlot",
                    "Partly Cloudy 24°C | Sunrise: 06:14 AM | Sunset: 05:50 PM | Wind: 6 mph",
                    "Nearest Emergency Medical Center (Emergency: 911 / 108 / 999)", "IN_PROGRESS");
            if (m.getScenes().size() > 1) {
                cs2.addScheduledScene(m.getScenes().get(1).getSceneNumber());
            }
            for (Person p : m.getCastAndCrew()) {
                if (p instanceof Actor a) {
                    cs2.addCastCall(new CastCallEntry(a.getId(), a.getName(), a.getCharacterName(),
                            "05:45 AM", "06:30 AM", "08:00 AM", "Exterior lighting match, action costume"));
                }
            }
            if (cs2.getCastCalls().isEmpty()) {
                cs2.addCastCall(new CastCallEntry("ACT-01", "Lead Talent", "Main Protagonist",
                        "05:45 AM", "06:30 AM", "08:00 AM", "Exterior lighting match, action costume"));
            }
            cs2.setDepartmentNotes("CAMERA: Steadicam operator standing by for tracking shots.\n"
                    + "SOUND: High-wind windshields on boom microphones.\n"
                    + "CATERING: Hot breakfast 06:15 AM | Hot lunch 13:00 PM.");
            m.addCallSheet(cs2);
        } catch (Exception e) {
            System.err.println("[WARNING] Could not seed default call sheets for " + m.getTitle() + ": " + e.getMessage());
        }
    }

    public void addStudio(ProductionHouse studio) throws ValidationException {
        if (studio == null) {
            throw new ValidationException("Production House", "Cannot register a null production house.");
        }
        if (studios.containsKey(studio.getId().toUpperCase())) {
            throw new ValidationException("Studio ID",
                    "Production House with ID '" + studio.getId() + "' already exists.");
        }
        studios.put(studio.getId().toUpperCase(), studio);
        if (activeStudioId == null) {
            activeStudioId = studio.getId().toUpperCase();
        }
    }

    public ProductionHouse getStudio(String id) {
        if (id == null) return null;
        return studios.get(id.trim().toUpperCase());
    }

    public ProductionHouse getStudioOrThrow(String id) throws ResourceNotFoundException {
        if (id == null || id.trim().isEmpty()) {
            throw new ResourceNotFoundException("Production House", "Empty ID");
        }
        ProductionHouse ph = studios.get(id.trim().toUpperCase());
        if (ph == null) {
            throw new ResourceNotFoundException("Production House", id.trim());
        }
        return ph;
    }

    public List<ProductionHouse> getAllStudios() {
        return new ArrayList<>(studios.values());
    }

    public ProductionHouse getActiveStudio() {
        if (activeStudioId == null && !studios.isEmpty()) {
            activeStudioId = studios.keySet().iterator().next();
        }
        return (activeStudioId != null) ? studios.get(activeStudioId) : null;
    }

    public void setActiveStudioId(String studioId) throws ResourceNotFoundException {
        if (studioId == null || studioId.trim().isEmpty()) {
            throw new ResourceNotFoundException("Production House", "Empty ID");
        }
        String key = studioId.trim().toUpperCase();
        if (studios.containsKey(key)) {
            this.activeStudioId = key;
            ProductionHouse ph = studios.get(key);
            if (!ph.getMovies().isEmpty()) {
                this.activeMovieId = ph.getMovies().get(0).getId();
            } else {
                this.activeMovieId = null;
            }
        } else {
            throw new ResourceNotFoundException("Production House", studioId.trim());
        }
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

    public void setActiveMovieId(String movieId) throws ResourceNotFoundException {
        if (movieId == null || movieId.trim().isEmpty()) {
            throw new ResourceNotFoundException("Movie", "Empty ID");
        }
        String trimmed = movieId.trim();

        // First check in active studio
        ProductionHouse currentStudio = getActiveStudio();
        if (currentStudio != null && currentStudio.findMovieById(trimmed) != null) {
            this.activeMovieId = trimmed;
            return;
        }

        // If not in active studio, search all studios and switch active studio too
        for (ProductionHouse ph : studios.values()) {
            Movie m = ph.findMovieById(trimmed);
            if (m != null) {
                this.activeStudioId = ph.getId().toUpperCase();
                this.activeMovieId = m.getId();
                return;
            }
        }

        throw new ResourceNotFoundException("Movie", trimmed);
    }

    public void addMovieToActiveStudio(Movie movie) throws ResourceNotFoundException, ValidationException {
        if (movie == null) {
            throw new ValidationException("Movie", "Cannot add null movie.");
        }
        ProductionHouse studio = getActiveStudio();
        if (studio == null) {
            throw new ResourceNotFoundException("Production House", "No active production house selected");
        }
        if (findMovieAnywhere(movie.getId()) != null) {
            throw new ValidationException("Movie ID",
                    "Movie with ID '" + movie.getId() + "' already exists in the system.");
        }
        studio.addMovie(movie);
        this.activeMovieId = movie.getId();
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
        if (movieId == null) return null;
        String trimmed = movieId.trim();
        for (ProductionHouse ph : studios.values()) {
            Movie m = ph.findMovieById(trimmed);
            if (m != null) return m;
        }
        return null;
    }

    public Movie findMovieOrThrow(String movieId) throws ResourceNotFoundException {
        Movie m = findMovieAnywhere(movieId);
        if (m == null) {
            throw new ResourceNotFoundException("Movie", (movieId != null) ? movieId.trim() : "null");
        }
        return m;
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
                    || (!m.getReviews().isEmpty() && reviewManager.calculateAverageRating(m) >= minRating);

            if (matchesQuery && matchesGenre && matchesRating) {
                results.add(m);
            }
        }
        return results;
    }

    public CastManager getCastManager() {
        return castManager;
    }

    public ReviewManager getReviewManager() {
        return reviewManager;
    }

    public FileService getFileService() {
        return fileService;
    }

    public File exportActiveMovieScreenplay() throws IOException, ResourceNotFoundException {
        Movie active = getActiveMovie();
        if (active == null) {
            throw new ResourceNotFoundException("Active Movie", "No active movie selected to export script.");
        }
        return fileService.exportScreenplayToFile(active);
    }

    public File getActiveMovieScreenplayFile() {
        Movie active = getActiveMovie();
        if (active == null) {
            return null;
        }
        return fileService.getScreenplayFile(active);
    }

    public String readActiveMovieScreenplayFromFile() throws IOException {
        Movie active = getActiveMovie();
        if (active == null) {
            return "";
        }
        return fileService.readScreenplayFromFile(active);
    }

    public void autoSyncScreenplayFile(Movie movie) {
        try {
            if (movie != null) {
                fileService.exportScreenplayToFile(movie);
            }
        } catch (Exception e) {
            System.err.println("[WARNING] Could not auto-sync screenplay file: " + e.getMessage());
        }
    }

    public File exportActiveMovieSchedule() throws IOException, ResourceNotFoundException {
        Movie active = getActiveMovie();
        if (active == null) {
            throw new ResourceNotFoundException("Active Movie", "No active movie selected to export schedule.");
        }
        return fileService.exportScheduleToFile(active);
    }

    public File exportActiveMovieDubbing() throws IOException, ResourceNotFoundException {
        Movie active = getActiveMovie();
        if (active == null) {
            throw new ResourceNotFoundException("Active Movie", "No active movie selected to export dubbing.");
        }
        return fileService.exportDubbingToFile(active);
    }

    public File exportActiveMoviePostProduction() throws IOException, ResourceNotFoundException {
        Movie active = getActiveMovie();
        if (active == null) {
            throw new ResourceNotFoundException("Active Movie", "No active movie selected to export post-production.");
        }
        return fileService.exportPostProductionToFile(active);
    }

    public File exportActiveMovieCallSheet(CallSheet sheet) throws IOException, ResourceNotFoundException {
        Movie active = getActiveMovie();
        if (active == null) {
            throw new ResourceNotFoundException("Active Movie", "No active movie selected to export call sheet.");
        }
        return fileService.exportCallSheetToFile(active, sheet);
    }

    public File getActiveMovieCallSheetFile(CallSheet sheet) {
        Movie active = getActiveMovie();
        if (active == null || sheet == null) return null;
        return fileService.getCallSheetFile(active, sheet);
    }

    public void autoSyncCallSheetFile(Movie movie, CallSheet sheet) {
        try {
            if (movie != null && sheet != null) {
                fileService.exportCallSheetToFile(movie, sheet);
            }
        } catch (Exception e) {
            System.err.println("[WARNING] Could not auto-sync call sheet file: " + e.getMessage());
        }
    }

    public File exportActiveMovieDistribution() throws IOException, ResourceNotFoundException {
        Movie active = getActiveMovie();
        if (active == null) {
            throw new ResourceNotFoundException("Active Movie", "No active movie selected to export distribution.");
        }
        return fileService.exportDistributionToFile(active);
    }

    public File getActiveMovieDistributionFile() {
        Movie active = getActiveMovie();
        if (active == null) return null;
        return fileService.getDistributionFile(active);
    }

    public void autoSyncDistributionFile(Movie movie) {
        try {
            if (movie != null) {
                fileService.exportDistributionToFile(movie);
            }
        } catch (Exception e) {
            System.err.println("[WARNING] Could not auto-sync distribution file: " + e.getMessage());
        }
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public PromotionManager getPromotionManager() {
        return promotionManager;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public void saveData() throws IOException {
        fileService.saveAllData(getAllStudios(), inventoryManager, promotionManager);
    }

    public void loadData() throws IOException {
        List<ProductionHouse> loaded = fileService.loadAllData(inventoryManager, promotionManager);
        if (loaded == null || loaded.isEmpty()) {
            System.out.println("[INFO] No studio records loaded from files. Preserving current in-memory data.");
            return;
        }
        studios.clear();
        for (ProductionHouse ph : loaded) {
            studios.put(ph.getId().toUpperCase(), ph);
        }
        if (!studios.isEmpty()) {
            activeStudioId = studios.keySet().iterator().next();
            ProductionHouse active = studios.get(activeStudioId);
            if (!active.getMovies().isEmpty()) {
                activeMovieId = active.getMovies().get(0).getId();
            } else {
                activeMovieId = null;
            }
        }
    }

    /**
     * Seeds realistic initial film production data.
     */
    public void seedInitialData() {
        studios.clear();
        try {
            // Studio 1: Horizon Studios
            ProductionHouse studio1 = new ProductionHouse("PH-101", "Horizon Studios", "Los Angeles & Mumbai");

            // Movie 1: Interstellar Journey
            Movie m1 = new Movie("MOV-01", "Interstellar Journey", "Sci-Fi", "Marcus Sterling", 2026, 1500000.0, MovieStatus.PRODUCTION);
            m1.setScreenplayWriter("Marcus Sterling & Jonathan Nolan");
            m1.setScriptDraftVersion("Final Shooting Draft v3.2");
            m1.setSynopsis("A deep-space expedition into the Cygnus Rift encounters an ancient harmonic anomaly broadcasting from a dying neutron star.");

            m1.addScene(new Scene(1, "Deep Orbit Discovery", "Soundstage A (CGI)", "COMPLETED", "2026-11-10",
                    "INT. RESEARCH VESSEL 'AURA' - ORBIT", 2.5,
                    "COMMANDER JAX peers through the reinforced viewport into the shimmering Cygnus Rift.\n\n"
                    + "JAX\nTelemetry confirmed. Lyra, are you seeing these harmonic spikes?\n\n"
                    + "DR. LYRA THORNE adjusts the spectrometer array. Her hands freeze on the console.\n\n"
                    + "LYRA\n(whispering)\nIt isn't cosmic background radiation, Jax. It's rhythmic. Something is broadcasting back."));

            m1.addScene(new Scene(2, "Spacewalk Sensor Repair", "Exterior Orbital Rig", "IN_PROGRESS", "2026-11-15",
                    "EXT. HULL SENSOR GANTRY - ZERO-G", 3.0,
                    "Solar flare interference crackles through the helmet comms. Micro-meteorites ping against Jax's tether.\n\n"
                    + "JAX\nTether tension is red-lining! Give me ten seconds on this optical data bus!\n\n"
                    + "LYRA (V.O.)\n(distorted static)\nPull back, Jax! The flare front arrives in four seconds!"));

            m1.addScene(new Scene(3, "Atmospheric Entry Bridge", "Flight Deck Set", "PLANNED", "2026-11-20",
                    "INT. FLIGHT DECK BRIDGE - DAY", 2.0,
                    "Warning klaxons echo across the command bay as glowing plasma licks the forward viewing screens.\n\n"
                    + "JAX\nDescent angle is steep at thirty-eight degrees! Lock the retro-thrusters now!\n\n"
                    + "LYRA\nThrusters locked! Bracing for thermal impact!"));

            m1.addPerson(new Actor("ACT-101", "Liam Vance", "liam@apex.com", "Commander Jax"));
            m1.addPerson(new Actor("ACT-102", "Dr. Lyra Thorne", "lyra@apex.com", "Chief Scientist"));
            m1.addPerson(new CrewMember("CRW-201", "Elena Vance", "elena@cinematography.com", "Cinematography"));
            m1.addPerson(new CrewMember("CRW-202", "Chloe Bennett", "chloe@vfx.com", "VFX & Sound"));

            m1.addExpense(new Expense("EXP-01", "Visual Effects", 250000.0, "Zero-G wire rigging and 3D composites"));
            m1.addExpense(new Expense("EXP-02", "Camera Gear", 120000.0, "High-speed sensor camera rental"));
            m1.addExpense(new Expense("EXP-03", "Costumes", 45000.0, "Hero astronaut suits and tailoring"));

            // Movie 2: Shadows of the Past
            Movie m2 = new Movie("MOV-02", "Shadows of the Past", "Thriller", "Evelyn Blackwood", 2027, 800000.0, MovieStatus.PLANNING);
            m2.setScreenplayWriter("Evelyn Blackwood");
            m2.setScriptDraftVersion("Draft 2.0 (Noir Revision)");
            m2.setSynopsis("A haunted private investigator is pulled into a waterfront conspiracy when an unsolved homicide resurfaces.");

            m2.addScene(new Scene(1, "Rainy Pier Encounter", "Dock Warehouse #4", "COMPLETED", "2027-01-10",
                    "EXT. DOCK WAREHOUSE #4 - RAINY NIGHT", 2.5,
                    "Rain hammers against rusted corrugated iron. Fog rolls off the harbor basin.\n"
                    + "DETECTIVE VANCE strikes a match under his drenched fedora.\n\n"
                    + "VANCE\n(voiceover)\nIn this city, the rain doesn't wash away sins. It just drowns the evidence."));

            m2.addScene(new Scene(2, "Interrogation Room", "Studio Stage B", "PLANNED", "2027-01-15",
                    "INT. PRECINCT INTERROGATION ROOM - NIGHT", 2.0,
                    "A solitary overhead bulb swings gently over a scuffed table.\n\n"
                    + "VANCE\nSit down, Miller. Let's talk about what really happened on the pier."));

            m2.addPerson(new Actor("ACT-103", "Robert Sterling", "robert@talent.com", "Detective Vance"));
            m2.addPerson(new CrewMember("CRW-203", "Marcus Hill", "marcus@sound.com", "Sound & Foley"));

            m2.addExpense(new Expense("EXP-01", "Screenplay Rights", 35000.0, "Acquisition of original noir script"));
            m2.addExpense(new Expense("EXP-02", "Set Design", 60000.0, "Warehouse set construction and rain rig"));

            studio1.addMovie(m1);
            studio1.addMovie(m2);

            // Studio 2: Mythri Movie Makers
            ProductionHouse studio2 = new ProductionHouse("PH-102", "Mythri Movie Makers", "Hyderabad, India");
            Movie m3 = new Movie("MOV-03", "Pushpa: The Rule", "Action Drama", "Sukumar", 2026, 2500000.0, MovieStatus.RELEASED);
            m3.setScreenplayWriter("Sukumar");
            m3.setScriptDraftVersion("Director's Shooting Script");
            m3.setSynopsis("Pushpa Raj consolidates his red sanders empire while facing the tactical wrath of SP Bhanwar Singh Shekhawat.");

            m3.addScene(new Scene(1, "Forest Border Ambush", "Maredumilli Forest", "COMPLETED", "2026-03-05",
                    "EXT. MAREDUMILLI DENSE FOREST - DAY", 3.5,
                    "Exhaust smoke curls through the misty teak groves. Trucks rumble across the forest floor.\n"
                    + "PUSHPA steps down onto the red soil, sliding his sleeve up with quiet authority.\n\n"
                    + "PUSHPA\nPushpa ante flower anukuntiva? Fire-u! Taggedele!"));

            m3.addScene(new Scene(2, "Port Cargo Standoff", "Visakhapatnam Port", "IN_PROGRESS", "2026-03-12",
                    "EXT. VISAKHAPATNAM PORT BAY - NIGHT", 3.0,
                    "Industrial floodlights cast stark shadows across container stacks.\n"
                    + "BHANWAR SINGH steps out from behind a customs gantry, twirling his baton.\n\n"
                    + "BHANWAR SINGH\nParty leda Pushpa? Not a single log moves past this checkpoint.\n\n"
                    + "PUSHPA\n(smiling calmly)\nThis entire port moves when Pushpa says move, sir."));

            m3.addPerson(new Actor("ACT-104", "Allu Arjun", "allu@mythri.com", "Pushpa Raj"));
            m3.addPerson(new Actor("ACT-105", "Fahadh Faasil", "fahadh@mythri.com", "Bhanwar Singh Shekhawat"));
            m3.addPerson(new CrewMember("CRW-204", "Miroslaw Brozek", "miroslaw@camera.com", "Director of Photography"));
            m3.addPerson(new CrewMember("CRW-205", "Devi Sri Prasad", "dsp@music.com", "Music & Background Score"));

            m3.addExpense(new Expense("EXP-01", "Stunts & Action", 400000.0, "Forest convoy chase and blast effects"));
            m3.addExpense(new Expense("EXP-02", "Art Direction", 180000.0, "Timber check-post replica set"));

            // Reviews for released movie MOV-03
            m3.addReview(new Review("John FilmCritic", 5, "Remarkable action sequences and gripping drama!"));
            m3.addReview(new Review("Sarah Jenkins", 4, "High voltage performances and outstanding music."));

            studio2.addMovie(m3);

            // Studio 3: Warner Bros. Pictures
            ProductionHouse studio3 = new ProductionHouse("PH-103", "Warner Bros. Pictures", "Burbank, California");
            Movie m4 = new Movie("MOV-04", "The Dark Horizon", "Neo-Noir", "Christopher Nolan", 2027, 3000000.0, MovieStatus.POST_PRODUCTION);
            m4.setScreenplayWriter("Christopher Nolan & Jonathan Nolan");
            m4.setScriptDraftVersion("IMAX Production Screenplay");
            m4.setSynopsis("A masked detective discovers that an elite syndicate is orchestrating controlled citywide power blackouts.");

            m4.addScene(new Scene(1, "Rooftop Chase", "Gotham Heights", "PLANNED", "2027-04-01",
                    "EXT. GOTHAM SKYLINE - RAINY NIGHT", 3.0,
                    "Thunder reverberates across the steel spires. Lightning illuminates the flooded streets.\n"
                    + "BRUCE WAYNE crouches beside a stone gargoyle, his radio receiver pulsing.\n\n"
                    + "BRUCE\n(into comm)\nThe power grid didn't fail on its own, Alfred. It was severed from the apex."));

            m4.addPerson(new Actor("ACT-106", "Christian Bale", "bale@warner.com", "Bruce Wayne"));
            m4.addPerson(new CrewMember("CRW-206", "Hoyte van Hoytema", "hoyte@camera.com", "Cinematography"));
            m4.addExpense(new Expense("EXP-01", "Practical Effects", 500000.0, "IMAX rig and miniature models"));

            studio3.addMovie(m4);

            // Seed master production & release schedules for all demo movies
            // 1. Interstellar Journey (In Production)
            setMilestone(m1, "MS-01", "2026-09-01", "COMPLETED", "Marcus Sterling");
            setMilestone(m1, "MS-02", "2026-10-15", "COMPLETED", "Publicity Team & Cast");
            setMilestone(m1, "MS-03", "2026-11-10", "IN_PROGRESS", "Marcus Sterling & Elena Vance");
            setMilestone(m1, "MS-04", "2027-01-15", "PLANNED", "Chief Editor");
            setMilestone(m1, "MS-05", "2027-02-20", "PLANNED", "Sound Supervisor");
            setMilestone(m1, "MS-06", "2027-03-30", "PLANNED", "Chloe Bennett (VFX Lead)");
            setMilestone(m1, "MS-07", "2027-04-15", "PLANNED", "Studio Marketing");
            setMilestone(m1, "MS-08", "2027-05-01", "PLANNED", "Digital Media Team");
            setMilestone(m1, "MS-09", "2027-06-10", "PLANNED", "Studio Distribution Head");
            setMilestone(m1, "MS-10", "2027-06-25", "PLANNED", "PR Officer & Cast");
            setMilestone(m1, "MS-11", "2027-07-01", "PLANNED", "Cast & PR Lead");
            setMilestone(m1, "MS-12", "2027-07-20", "PLANNED", "Global Distributors");

            // 2. Shadows of the Past (In Planning)
            setMilestone(m2, "MS-01", "2026-12-01", "COMPLETED", "Evelyn Blackwood");
            setMilestone(m2, "MS-02", "2027-01-05", "PLANNED", "Publicity Team");
            setMilestone(m2, "MS-03", "2027-01-10", "PLANNED", "Evelyn Blackwood");
            setMilestone(m2, "MS-04", "2027-03-01", "PLANNED", "Editor");
            setMilestone(m2, "MS-05", "2027-03-20", "PLANNED", "Marcus Hill (Sound)");
            setMilestone(m2, "MS-06", "2027-04-15", "PLANNED", "Colorist & Post Lead");
            setMilestone(m2, "MS-07", "2027-05-01", "PLANNED", "Marketing Team");
            setMilestone(m2, "MS-08", "2027-05-20", "PLANNED", "Digital Media Team");
            setMilestone(m2, "MS-09", "2027-06-15", "PLANNED", "Distribution Lead");
            setMilestone(m2, "MS-10", "2027-06-28", "PLANNED", "PR Lead");
            setMilestone(m2, "MS-11", "2027-07-05", "PLANNED", "Cast & PR Lead");
            setMilestone(m2, "MS-12", "2027-07-25", "PLANNED", "Global Distributors");

            // 3. Pushpa: The Rule (Fully Released)
            setMilestone(m3, "MS-01", "2025-01-10", "COMPLETED", "Sukumar");
            setMilestone(m3, "MS-02", "2025-02-15", "COMPLETED", "Mythri Publicity Team");
            setMilestone(m3, "MS-03", "2025-03-05", "COMPLETED", "Sukumar & Miroslaw Brozek");
            setMilestone(m3, "MS-04", "2026-06-15", "COMPLETED", "Chief Editor Naveen");
            setMilestone(m3, "MS-05", "2026-08-10", "COMPLETED", "Devi Sri Prasad (Music/Audio)");
            setMilestone(m3, "MS-06", "2026-09-20", "COMPLETED", "Mythri VFX Team");
            setMilestone(m3, "MS-07", "2026-10-05", "COMPLETED", "Marketing Team");
            setMilestone(m3, "MS-08", "2026-10-24", "COMPLETED", "Digital Media Team");
            setMilestone(m3, "MS-09", "2026-11-17", "COMPLETED", "Mythri Distribution");
            setMilestone(m3, "MS-10", "2026-11-27", "COMPLETED", "PR Officer (Grand Press Meet)");
            setMilestone(m3, "MS-11", "2026-12-01", "COMPLETED", "Allu Arjun & Team Tour");
            setMilestone(m3, "MS-12", "2026-12-05", "COMPLETED", "Global Theaters & Distributors");

            // 4. The Dark Horizon (Post-Production)
            setMilestone(m4, "MS-01", "2026-08-10", "COMPLETED", "Christopher Nolan");
            setMilestone(m4, "MS-02", "2026-09-25", "COMPLETED", "WB Creative Team");
            setMilestone(m4, "MS-03", "2026-11-01", "COMPLETED", "Christopher Nolan & Hoyte");
            setMilestone(m4, "MS-04", "2027-02-10", "IN_PROGRESS", "Lee Smith (Chief Editor)");
            setMilestone(m4, "MS-05", "2027-03-15", "PLANNED", "Sound Supervisor");
            setMilestone(m4, "MS-06", "2027-04-20", "PLANNED", "DNEG VFX Team");
            setMilestone(m4, "MS-07", "2027-05-15", "PLANNED", "WB Marketing");
            setMilestone(m4, "MS-08", "2027-06-01", "PLANNED", "WB Digital");
            setMilestone(m4, "MS-09", "2027-07-04", "PLANNED", "WB Distribution");
            setMilestone(m4, "MS-10", "2027-07-18", "PLANNED", "PR Head (Press Conference)");
            setMilestone(m4, "MS-11", "2027-07-25", "PLANNED", "Global Press Tour");
            setMilestone(m4, "MS-12", "2027-08-12", "PLANNED", "Warner Bros. Worldwide");

            // Seed Dubbing and Post-Production for m1 (Interstellar Journey)
            DubbingTrack dt1 = m1.findDubbingTrackById("DUB-01");
            if (dt1 != null) {
                dt1.setCharacterName("Commander Jax");
                dt1.setVoiceArtist("Liam Vance");
                dt1.setLanguage("English (Original)");
                dt1.setStatus("RECORDED");
                dt1.setStudioLocation("Pinewood Studios, London");
            }
            DubbingTrack dt2 = m1.findDubbingTrackById("DUB-02");
            if (dt2 != null) {
                dt2.setCharacterName("Commander Jax");
                dt2.setVoiceArtist("Sharad Kelkar");
                dt2.setLanguage("Hindi");
                dt2.setStatus("RETAKES_NEEDED");
                dt2.setRetakeCount(2);
                dt2.setRetakeReason("Lip sync match on scene 2 airlock line");
                dt2.setStudioLocation("YRF Studios, Mumbai");
            }
            DubbingTrack dt3 = m1.findDubbingTrackById("DUB-03");
            if (dt3 != null) {
                dt3.setCharacterName("Dr. Lyra Thorne");
                dt3.setVoiceArtist("Dr. Lyra Thorne");
                dt3.setLanguage("English (Original)");
                dt3.setStatus("APPROVED");
                dt3.setStudioLocation("Abbey Road, London");
            }

            PostProductionTask pt1 = m1.findPostProductionTaskById("POST-01");
            if (pt1 != null) pt1.updateProgress(75, "IN_PROGRESS", "Assembly cut of first 2 acts locked");
            PostProductionTask pt3 = m1.findPostProductionTaskById("POST-03");
            if (pt3 != null) pt3.updateProgress(45, "IN_PROGRESS", "Foley and sound design for zero-G orbital rig");
            PostProductionTask pt5 = m1.findPostProductionTaskById("POST-05");
            if (pt5 != null) pt5.updateProgress(65, "IN_PROGRESS", "Cygnus Rift cosmic particle simulation 65% done");

            // Seed Dubbing and Post-Production for m3 (Pushpa: The Rule - Fully Released)
            m3.getDubbingTracks().clear();
            m3.addDubbingTrack(new DubbingTrack("DUB-01", "Pushpa Raj", "Allu Arjun", "Telugu (Original)", "APPROVED", 0, "", "Prasad Labs, Hyderabad"));
            m3.addDubbingTrack(new DubbingTrack("DUB-02", "Pushpa Raj", "Shreyas Talpade", "Hindi", "APPROVED", 2, "Thaggedele dialogue modulation in port standoff", "Yash Raj Studios, Mumbai"));
            m3.addDubbingTrack(new DubbingTrack("DUB-03", "Bhanwar Singh Shekhawat", "Fahadh Faasil", "Malayalam (Original)", "APPROVED", 0, "", "Lal Media, Kochi"));
            m3.addDubbingTrack(new DubbingTrack("DUB-04", "Bhanwar Singh Shekhawat", "Rajesh Kava", "Hindi", "APPROVED", 1, "Police gantry confrontation pacing", "Supa Studio, Mumbai"));
            m3.addDubbingTrack(new DubbingTrack("DUB-05", "Pushpa Raj", "Deepak", "Tamil", "APPROVED", 0, "", "Four Frames, Chennai"));

            for (PostProductionTask pt : m3.getPostProductionTasks()) {
                pt.markCompleted();
            }

            // Seed Call Sheets for m1 (Interstellar Journey)
            CallSheet cs1 = new CallSheet("CS-01", 1, 42, "2026-11-10", "06:30 AM",
                    "Soundstage A - Orbital Airlock Rig (Pinewood Studios, London)",
                    "Indoor Stage (Climate Controlled, Ambient 21°C)",
                    "Wexham Park Hospital, Slough (Emergency: 999)", "WRAPPED");
            cs1.addScheduledScene(1);
            cs1.addCastCall(new CastCallEntry("ACT-01", "Liam Vance", "Commander Jax",
                    "05:30 AM", "06:15 AM", "07:30 AM", "Spacesuit harness fitting, cheek telemetry prosthetic"));
            cs1.addCastCall(new CastCallEntry("ACT-02", "Sarah Jenkins", "Dr. Lyra Thorne",
                    "06:00 AM", "06:45 AM", "08:00 AM", "Astrophysics prop tablet, science lab jumpsuit"));
            cs1.setDepartmentNotes("CAMERA: ARRI Alexa Mini LF on Technocrane 30. Prime lenses: 35mm & 50mm.\n"
                    + "SOUND: Soundstage isolation baffles engaged; radio mic on Liam Vance.\n"
                    + "SFX/STUNT: Hydraulic gimbal test passed; safety harnesses checked by stunt coordinator.\n"
                    + "CATERING: Hot breakfast 06:00 AM | Catered lunch 12:30 PM (Courtesy 30 min).");
            cs1.recordWrap(2.5, "First camera setup achieved on schedule at 08:30 AM. Zero-G wire harness checked. Day 1 wrapped at 18:45.");
            m1.addCallSheet(cs1);

            CallSheet cs2 = new CallSheet("CS-02", 2, 42, "2026-11-15", "07:00 AM",
                    "Exterior Orbital Rig - Stage 3 Backlot (London)",
                    "Partly Cloudy 14°C | Sunrise 07:18 AM | Sunset 16:15 PM | Wind: 8 mph",
                    "Wexham Park Hospital, Slough (Emergency: 999)", "IN_PROGRESS");
            cs2.addScheduledScene(2);
            cs2.addCastCall(new CastCallEntry("ACT-01", "Liam Vance", "Commander Jax",
                    "05:45 AM", "06:30 AM", "08:00 AM", "Full pressurized helmet visor, wirework descent rig"));
            cs2.setDepartmentNotes("CAMERA: Handheld rig A-cam, drone camera package B-cam.\n"
                    + "SOUND: Windscreen blimp for exterior mics; dual redundancy wireless transmitters.\n"
                    + "SFX/STUNT: High-angle descent wire rig with safety mats; crane arm inspection passed.\n"
                    + "CATERING: Breakfast 06:15 AM - 07:30 AM | Lunch 13:00 PM.");
            m1.addCallSheet(cs2);

            addStudio(studio1);
            addStudio(studio2);
            addStudio(studio3);

            this.activeStudioId = studio1.getId().toUpperCase();
            this.activeMovieId = m1.getId();

            seedDefaultInventory();
            seedDefaultPromotionalAssets();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to seed default data: " + e.getMessage());
        }
    }

    public void seedDefaultInventory() {
        try {
            inventoryManager.clear();
            // Props
            InventoryItem p1 = new InventoryItem("PROP-01", "Harmonic Spectrometer", ItemCategory.PROP, 3, "MINT", "Hero hand prop used by Dr. Lyra Thorne in Cygnus Rift research");
            InventoryItem p2 = new InventoryItem("PROP-02", "Airlock Optical Data Bus Cable", ItemCategory.PROP, 5, "GOOD", "Braided high-tension fiber prop used in spacewalk scene");
            InventoryItem p3 = new InventoryItem("PROP-03", "Antique Waterfront Revolver (Replica)", ItemCategory.PROP, 2, "GOOD", "Inert metal replica prop for Detective Vance in Dock Warehouse");
            InventoryItem p4 = new InventoryItem("PROP-04", "Red Sanders Timber Log Props", ItemCategory.PROP, 10, "FAIR", "Lightweight painted foam prop logs for forest convoy standoff");

            // Costumes
            InventoryItem c1 = new InventoryItem("CST-01", "Hero Pressurized Astronaut Suit", ItemCategory.COSTUME, 2, "GOOD", "Lead spacesuit with internal LED visor illumination for Liam Vance");
            InventoryItem c2 = new InventoryItem("CST-02", "Heavy Noir Trenchcoat & Fedora", ItemCategory.COSTUME, 3, "GOOD", "Custom water-resistant wool costume for Detective Vance");
            InventoryItem c3 = new InventoryItem("CST-03", "Custom Red Sanders Forester Attire", ItemCategory.COSTUME, 4, "GOOD", "Weathered cotton shirt and lungi costume for Pushpa Raj");

            // Equipment
            InventoryItem e1 = new InventoryItem("EQP-01", "ARRI Alexa Mini LF Camera Package", ItemCategory.EQUIPMENT, 2, "MINT", "High-speed large-format digital cinema camera system with cage");
            InventoryItem e2 = new InventoryItem("EQP-02", "Technocrane 30-Foot Telescopic Crane", ItemCategory.EQUIPMENT, 1, "MINT", "Motorized gyro-stabilized telescopic camera crane for Soundstage A");
            InventoryItem e3 = new InventoryItem("EQP-03", "Sound Devices Scorpio 32-Track Recorder", ItemCategory.EQUIPMENT, 2, "EXCELLENT", "Multi-channel field audio recorder with wireless receiver rack");

            inventoryManager.addItem(p1);
            inventoryManager.addItem(p2);
            inventoryManager.addItem(p3);
            inventoryManager.addItem(p4);
            inventoryManager.addItem(c1);
            inventoryManager.addItem(c2);
            inventoryManager.addItem(c3);
            inventoryManager.addItem(e1);
            inventoryManager.addItem(e2);
            inventoryManager.addItem(e3);

            // Checkout sample items to demo movies & scenes
            Movie m1 = findMovieAnywhere("MOV-01");
            if (m1 != null) {
                inventoryManager.checkoutItem("PROP-01", m1, 1, 1, "2026-11-10", "Hero prop for orbital lab sequence");
                inventoryManager.checkoutItem("PROP-02", m1, 2, 2, "2026-11-15", "Tether harness cable for spacewalk scene");
                inventoryManager.checkoutItem("CST-01", m1, 1, 1, "2026-11-10", "Hero spacesuit for Liam Vance");
                inventoryManager.checkoutItem("EQP-01", m1, 1, 1, "2026-11-10", "A-Camera primary package");
            }
            Movie m2 = findMovieAnywhere("MOV-02");
            if (m2 != null) {
                inventoryManager.checkoutItem("PROP-03", m2, 1, 1, "2027-01-10", "Hero prop weapon for rainy pier encounter");
                inventoryManager.checkoutItem("CST-02", m2, 1, 1, "2027-01-10", "Waterproof noir wardrobe");
            }
            Movie m3 = findMovieAnywhere("MOV-03");
            if (m3 != null) {
                ItemAssignment asg = inventoryManager.checkoutItem("PROP-04", m3, 1, 6, "2026-03-05", "Checkpost standoff logs");
                inventoryManager.returnItem(asg.getAssignmentId(), 6, 0, 0, "2026-03-10", "Action scene wrapped cleanly");
            }
        } catch (Exception e) {
            System.err.println("[WARNING] Could not seed default inventory: " + e.getMessage());
        }
    }

    public void seedDefaultPromotionalAssets() {
        try {
            promotionManager.clear();
            // MOV-01 Promotional Assets
            PromotionalAsset a1 = new PromotionalAsset("AST-01", "MOV-01", AssetType.POSTER,
                    "Official Theatrical One-Sheet", "assets/posters/interstellar_hero_poster.jpg",
                    "English", "Theaters & IMAX Billboards", ApprovalStatus.APPROVED, 2,
                    "APPROVED by Studio Marketing Head (IMAX title treatment approved)", "2026-10-01");
            PromotionalAsset a2 = new PromotionalAsset("AST-02", "MOV-01", AssetType.TEASER,
                    "Cygnus Rift 4K Atmospheric Teaser", "https://cineflow.cdn/trailers/interstellar_teaser_4k.mp4",
                    "English", "YouTube 4K & Social Media", ApprovalStatus.APPROVED, 1,
                    "APPROVED by Director Marcus Sterling", "2026-10-05");
            PromotionalAsset a3 = new PromotionalAsset("AST-03", "MOV-01", AssetType.TRAILER,
                    "Official Theatrical Trailer #1", "https://cineflow.cdn/trailers/interstellar_trailer_cut1.mp4",
                    "English", "Theaters & Digital Streaming", ApprovalStatus.PENDING, 1,
                    "Awaiting sound mix lock and executive producer sign-off", "2026-10-10");
            PromotionalAsset a4 = new PromotionalAsset("AST-04", "MOV-01", AssetType.SOCIAL_MEDIA,
                    "Motion Poster: Lyra's Discovery", "assets/social/mov01_lyra_motion_square.mp4",
                    "Multilingual", "Instagram, X & TikTok", ApprovalStatus.PENDING, 1,
                    "Colorist review scheduled for Friday", "2026-10-11");

            // MOV-03 Promotional Assets
            PromotionalAsset a5 = new PromotionalAsset("AST-05", "MOV-03", AssetType.POSTER,
                    "Pushpa Raj Thaggedele First Look", "assets/posters/pushpa2_first_look.jpg",
                    "Telugu / Hindi", "Pan-India Billboards & Print", ApprovalStatus.APPROVED, 3,
                    "APPROVED by Mythri Producers & Director Sukumar", "2026-02-15");
            PromotionalAsset a6 = new PromotionalAsset("AST-06", "MOV-03", AssetType.TRAILER,
                    "Mass Theatrical Trailer (5 Languages)", "https://cineflow.cdn/trailers/pushpa2_trailer.mp4",
                    "Pan-India Multilingual", "YouTube & Cinema Theaters", ApprovalStatus.APPROVED, 1,
                    "APPROVED for worldwide multiplex release", "2026-03-01");

            // MOV-04 Promotional Assets
            PromotionalAsset a7 = new PromotionalAsset("AST-07", "MOV-04", AssetType.BANNER,
                    "Gotham Heights IMAX Billboard Banner", "assets/banners/dark_horizon_imax_banner.png",
                    "English", "Times Square & Highway Billboards", ApprovalStatus.REJECTED, 1,
                    "REJECTED by Creative Director (Reason: Shadow contrast too low on IMAX format, increase lighting on skyline)", "2026-09-20");

            promotionManager.addAsset(a1);
            promotionManager.addAsset(a2);
            promotionManager.addAsset(a3);
            promotionManager.addAsset(a4);
            promotionManager.addAsset(a5);
            promotionManager.addAsset(a6);
            promotionManager.addAsset(a7);
        } catch (Exception e) {
            System.err.println("[WARNING] Could not seed default promotional assets: " + e.getMessage());
        }
    }

    private void setMilestone(Movie movie, String msId, String date, String status, String lead) {
        if (movie == null) return;
        ProductionMilestone ms = movie.findMilestoneById(msId);
        if (ms != null) {
            ms.setTargetDate(date);
            ms.setStatus(status);
            ms.setResponsibleLead(lead);
        }
    }
}
