package com.cineflow.model;

import com.cineflow.exception.BudgetExceededException;
import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ScheduleConflictException;
import com.cineflow.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Movie project.
 * Demonstrates: Composition, Polymorphic Collections (List of Person),
 * Domain Business Rule Validation, and Exception Handling.
 */
public class Movie {
    private String id;
    private String title;
    private String genre;
    private String director;
    private int releaseYear;
    private double budget;
    private MovieStatus status;
    private String screenplayWriter; // Screenwriter (e.g. Christopher Nolan, Jonathan Nolan)
    private String scriptDraftVersion; // e.g. "Draft 1.0 (Shooting Script)"
    private String synopsis; // Screenplay logline / story synopsis

    private List<Scene> scenes;
    private List<Person> castAndCrew; // Polymorphic collection storing both Actor and CrewMember
    private List<Expense> expenses;
    private List<Review> reviews;
    private List<ProductionMilestone> masterSchedule;
    private List<DubbingTrack> dubbingTracks;
    private List<PostProductionTask> postProductionTasks;
    private List<CallSheet> callSheets;
    private List<DistributionRelease> distributionReleases;

    public Movie(String id, String title, String genre, String director, int releaseYear, double budget)
            throws ValidationException {
        this(id, title, genre, director, releaseYear, budget, MovieStatus.PLANNING);
    }

    public Movie(String id, String title, String genre, String director, int releaseYear, double budget, MovieStatus status)
            throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID cannot be empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Movie Title", "Movie title cannot be empty.");
        }
        if (genre == null || genre.trim().isEmpty()) {
            throw new ValidationException("Genre", "Movie genre cannot be empty.");
        }
        if (director == null || director.trim().isEmpty()) {
            throw new ValidationException("Director", "Director name cannot be empty.");
        }
        if (releaseYear < 1888 || releaseYear > 2100) {
            throw new ValidationException("Release Year", "Release year must be between 1888 and 2100.");
        }
        if (budget <= 0.0) {
            throw new ValidationException("Budget", "Allocated budget must be greater than zero.");
        }

        this.id = id.trim();
        this.title = title.trim();
        this.genre = genre.trim();
        this.director = director.trim();
        this.releaseYear = releaseYear;
        this.budget = budget;
        this.status = (status != null) ? status : MovieStatus.PLANNING;
        this.screenplayWriter = this.director; // Default writer to director
        this.scriptDraftVersion = "Shooting Draft v1.0";
        this.synopsis = "A " + genre.toLowerCase() + " cinematic feature directed by " + this.director + ".";
        this.scenes = new ArrayList<>();
        this.castAndCrew = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.masterSchedule = new ArrayList<>();
        this.dubbingTracks = new ArrayList<>();
        this.postProductionTasks = new ArrayList<>();
        this.callSheets = new ArrayList<>();
        this.distributionReleases = new ArrayList<>();
        initializeDefaultSchedule();
        initializeDefaultDubbingTracks();
        initializeDefaultPostProductionTasks();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Movie Title", "Movie title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) throws ValidationException {
        if (genre == null || genre.trim().isEmpty()) {
            throw new ValidationException("Genre", "Movie genre cannot be empty.");
        }
        this.genre = genre.trim();
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) throws ValidationException {
        if (director == null || director.trim().isEmpty()) {
            throw new ValidationException("Director", "Director name cannot be empty.");
        }
        this.director = director.trim();
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) throws ValidationException {
        if (releaseYear < 1888 || releaseYear > 2100) {
            throw new ValidationException("Release Year", "Release year must be between 1888 and 2100.");
        }
        this.releaseYear = releaseYear;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) throws ValidationException {
        if (budget <= 0.0) {
            throw new ValidationException("Budget", "Budget must be greater than zero.");
        }
        this.budget = budget;
    }

    public MovieStatus getStatus() {
        return status;
    }

    public void setStatus(MovieStatus status) {
        this.status = (status != null) ? status : MovieStatus.PLANNING;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public List<Person> getCastAndCrew() {
        return castAndCrew;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Adds a scene to the movie after validating against duplicate scene numbers
     * and potential location/schedule conflicts.
     */
    public void addScene(Scene scene) throws ValidationException, ScheduleConflictException {
        if (scene == null) {
            throw new ValidationException("Scene", "Cannot add null scene.");
        }
        for (Scene existing : scenes) {
            if (existing.getSceneNumber() == scene.getSceneNumber()) {
                throw new ValidationException("Scene Number",
                        "Scene #" + scene.getSceneNumber() + " already exists in movie '" + title + "'.");
            }
        }

        // Check for genuine schedule conflict if a shooting date is provided
        if (scene.getShootingDate() != null && !scene.getShootingDate().equalsIgnoreCase("TBD")) {
            for (Scene existing : scenes) {
                if (existing.getShootingDate() != null
                        && existing.getShootingDate().equalsIgnoreCase(scene.getShootingDate())
                        && existing.getLocation().equalsIgnoreCase(scene.getLocation())) {
                    throw new ScheduleConflictException(scene.getLocation(), scene.getShootingDate(),
                            "Scene #" + existing.getSceneNumber() + " ('" + existing.getTitle()
                                    + "') is already scheduled at '" + scene.getLocation() + "' on " + scene.getShootingDate() + ".");
                }
            }
        }

        scenes.add(scene);
    }

    /**
     * Schedules or reschedules an existing scene to a new shooting date.
     * Enforces conflict detection to prevent double-booking the same filming location.
     */
    public void scheduleScene(int sceneNumber, String shootingDate)
            throws ResourceNotFoundException, ValidationException, ScheduleConflictException {
        Scene target = findSceneByNumber(sceneNumber);
        if (target == null) {
            throw new ResourceNotFoundException("Scene", String.valueOf(sceneNumber));
        }
        if (shootingDate == null || shootingDate.trim().isEmpty()) {
            throw new ValidationException("Shooting Date", "Shooting date cannot be empty.");
        }

        String trimmedDate = shootingDate.trim();
        for (Scene existing : scenes) {
            if (existing.getSceneNumber() != sceneNumber
                    && existing.getShootingDate() != null
                    && existing.getShootingDate().equalsIgnoreCase(trimmedDate)
                    && existing.getLocation().equalsIgnoreCase(target.getLocation())) {
                throw new ScheduleConflictException(target.getLocation(), trimmedDate,
                        "Scene #" + existing.getSceneNumber() + " ('" + existing.getTitle()
                                + "') is already scheduled at '" + target.getLocation() + "' on " + trimmedDate + ".");
            }
        }

        target.setShootingDate(trimmedDate);
    }

    public Scene findSceneByNumber(int sceneNumber) {
        for (Scene sc : scenes) {
            if (sc.getSceneNumber() == sceneNumber) {
                return sc;
            }
        }
        return null;
    }

    public Scene getSceneOrThrow(int sceneNumber) throws ResourceNotFoundException {
        Scene sc = findSceneByNumber(sceneNumber);
        if (sc == null) {
            throw new ResourceNotFoundException("Scene", String.valueOf(sceneNumber));
        }
        return sc;
    }

    /**
     * Adds a cast or crew member after validating against duplicate IDs within the movie.
     */
    public void addPerson(Person person) throws ValidationException {
        if (person == null) {
            throw new ValidationException("Person", "Cannot add null person.");
        }
        for (Person p : castAndCrew) {
            if (p.getId().equalsIgnoreCase(person.getId())) {
                throw new ValidationException("Person ID",
                        "Person with ID '" + person.getId() + "' already exists in movie '" + title + "'.");
            }
        }
        castAndCrew.add(person);
    }

    public boolean removePerson(String personId) {
        if (personId == null) return false;
        String trimmed = personId.trim();
        for (int i = 0; i < castAndCrew.size(); i++) {
            if (castAndCrew.get(i).getId().equalsIgnoreCase(trimmed)) {
                castAndCrew.remove(i);
                return true;
            }
        }
        return false;
    }

    public Person findPersonById(String personId) {
        if (personId == null) return null;
        String trimmed = personId.trim();
        for (Person p : castAndCrew) {
            if (p.getId().equalsIgnoreCase(trimmed)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Adds a review to the movie.
     * Enforces business rules:
     * 1. Movie status must be RELEASED.
     * 2. Rating must be between 1 and 5.
     * Throws ValidationException when rules are violated.
     */
    public void addReview(Review review) throws ValidationException {
        if (review == null) {
            throw new ValidationException("Review", "Cannot add null review.");
        }
        if (this.status != MovieStatus.RELEASED) {
            throw new ValidationException("Movie Status",
                    "Reviews can only be added when the movie is RELEASED! Current status is " + this.status + ".");
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new ValidationException("Review Rating",
                    "Star rating must be between 1 and 5 (got: " + review.getRating() + ").");
        }
        reviews.add(review);
    }

    public double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Review r : reviews) {
            sum += r.getRating();
        }
        return sum / reviews.size();
    }

    /**
     * Adds an expense to the movie.
     * Throws:
     * - ValidationException if expense is null or ID is duplicate
     * - BudgetExceededException if the expense exceeds the movie's total allocated budget.
     */
    public void addExpense(Expense expense) throws ValidationException, BudgetExceededException {
        if (expense == null) {
            throw new ValidationException("Expense", "Cannot add null expense.");
        }
        for (Expense existing : expenses) {
            if (existing.getId().equalsIgnoreCase(expense.getId())) {
                throw new ValidationException("Expense ID",
                        "Expense with ID '" + expense.getId() + "' already exists in movie '" + title + "'.");
            }
        }

        double newTotal = getTotalExpenses() + expense.getAmount();
        if (newTotal > budget) {
            double remaining = getRemainingBudget();
            throw new BudgetExceededException(expense.getAmount(), remaining, budget);
        }
        expenses.add(expense);
    }

    public double getTotalExpenses() {
        double sum = 0.0;
        for (Expense e : expenses) {
            sum += e.getAmount();
        }
        return sum;
    }

    public double getRemainingBudget() {
        return budget - getTotalExpenses();
    }

    public double getBudgetUtilizationPercentage() {
        if (budget <= 0.0) return 0.0;
        return (getTotalExpenses() / budget) * 100.0;
    }

    public String getScreenplayWriter() {
        return screenplayWriter;
    }

    public void setScreenplayWriter(String screenplayWriter) {
        this.screenplayWriter = (screenplayWriter != null && !screenplayWriter.trim().isEmpty())
                ? screenplayWriter.trim() : this.director;
    }

    public String getScriptDraftVersion() {
        return scriptDraftVersion;
    }

    public void setScriptDraftVersion(String scriptDraftVersion) {
        this.scriptDraftVersion = (scriptDraftVersion != null && !scriptDraftVersion.trim().isEmpty())
                ? scriptDraftVersion.trim() : "Shooting Draft v1.0";
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = (synopsis != null && !synopsis.trim().isEmpty())
                ? synopsis.trim() : "No synopsis provided.";
    }

    public double getTotalScriptPages() {
        double pages = 0.0;
        for (Scene sc : scenes) {
            pages += sc.getScriptPages();
        }
        return pages;
    }

    public double getEstimatedRuntimeMinutes() {
        // Industry benchmark: 1 script page corresponds to roughly 1 minute of film runtime
        return getTotalScriptPages();
    }

    public int getScenesWithScriptCount() {
        int count = 0;
        for (Scene sc : scenes) {
            if (sc.hasScript()) count++;
        }
        return count;
    }

    /**
     * Generates a fully formatted, industry-standard screenplay script for this entire movie.
     */
    public String getFormattedScreenplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(String.format("                              %s\n", title.toUpperCase()));
        sb.append("================================================================================\n");
        sb.append(String.format(" Screenplay Written by: %s\n", screenplayWriter));
        sb.append(String.format(" Directed by:           %s\n", director));
        sb.append(String.format(" Genre:                 %s | Release Year: %d\n", genre, releaseYear));
        sb.append(String.format(" Script Draft:          %s\n", scriptDraftVersion));
        sb.append(String.format(" Total Scenes:          %d (Scenes with script: %d/%d)\n",
                scenes.size(), getScenesWithScriptCount(), scenes.size()));
        sb.append(String.format(" Screenplay Length:     %.1f Pages (Est. Runtime: ~%.0f minutes)\n",
                getTotalScriptPages(), getEstimatedRuntimeMinutes()));
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format(" LOGLINE / SYNOPSIS:\n   %s\n", synopsis));
        sb.append("================================================================================\n\n");

        if (scenes.isEmpty()) {
            sb.append("   [No scenes currently drafted for this screenplay]\n\n");
        } else {
            for (Scene sc : scenes) {
                sb.append(sc.getFormattedScript()).append("\n\n");
            }
        }
        sb.append("                                *** THE END ***\n");
        sb.append("================================================================================\n");
        return sb.toString();
    }

    public List<ProductionMilestone> getMasterSchedule() {
        return masterSchedule;
    }

    public void initializeDefaultSchedule() {
        if (masterSchedule != null && !masterSchedule.isEmpty()) return;
        try {
            masterSchedule.add(new ProductionMilestone("MS-01", ProductionStage.SCRIPT_NARRATION, "Script & Storyline Narration to Cast",
                    "TBD", "PLANNED", director + " & Screenwriter", false, "Table read and story orientation with lead cast"));
            masterSchedule.add(new ProductionMilestone("MS-02", ProductionStage.POSTER_PHOTOSHOOT, "Look Test & Poster Photoshoot",
                    "TBD", "PLANNED", "Lead Cast & Publicity Team", false, "Character styling tests and high-res poster stills"));
            masterSchedule.add(new ProductionMilestone("MS-03", ProductionStage.PRINCIPAL_PHOTOGRAPHY, "Scene Filming (Principal Photography)",
                    "TBD", "PLANNED", director + " & Cinematographer", false, "Execution of planned script scenes on set and location"));
            masterSchedule.add(new ProductionMilestone("MS-04", ProductionStage.PICTURE_EDITING, "Picture & Video Editing (Assembly to Final Cut)",
                    "TBD", "PLANNED", "Chief Editor", false, "Assembly cut, rough cut, and picture lock"));
            masterSchedule.add(new ProductionMilestone("MS-05", ProductionStage.AUDIO_AND_DUBBING, "Audio Dubbing (Voice-Over / ADR) & Sound Design",
                    "TBD", "PLANNED", "Sound Supervisor & Lead Cast", false, "Actor voice-overs, ADR dubbing, Foley, and background score"));
            masterSchedule.add(new ProductionMilestone("MS-06", ProductionStage.VFX_AND_CGI, "VFX, CGI Animation & Color Grading (DI)",
                    "TBD", "PLANNED", "VFX Supervisor & Colorist", false, "CGI compositing, 3D visual effects, and digital intermediate color grading"));
            masterSchedule.add(new ProductionMilestone("MS-07", ProductionStage.POSTER_RELEASE, "Official First Look & Poster Release",
                    "TBD", "PLANNED", "Studio Marketing & Publicity", false, "Unveil official title logo and first look poster across media"));
            masterSchedule.add(new ProductionMilestone("MS-08", ProductionStage.TEASER_RELEASE, "Official Teaser Release",
                    "TBD", "PLANNED", "Digital Media Team", false, "Atmospheric 60-second teaser establishing movie universe"));
            masterSchedule.add(new ProductionMilestone("MS-09", ProductionStage.TRAILER_RELEASE, "Official Theatrical Trailer Release",
                    "TBD", "PLANNED", "Studio Distribution Head", false, "Theatrical trailer launch in theaters and streaming platforms"));
            masterSchedule.add(new ProductionMilestone("MS-10", ProductionStage.PRESS_MEET, "Press Meet & Media Conference (Optional)",
                    "TBD", "PLANNED", "PR Officer & Cast", true, "Journalist Q&A, cast press conference, and interviews"));
            masterSchedule.add(new ProductionMilestone("MS-11", ProductionStage.PROMOTIONAL_CAMPAIGN, "Promotional Tours, City Campaigns & Marketing",
                    "TBD", "PLANNED", "Cast & PR Lead", false, "Multi-city promotional tours, college visits, and media appearances"));
            masterSchedule.add(new ProductionMilestone("MS-12", ProductionStage.THEATRICAL_RELEASE, "Worldwide Movie Premiere & Theatrical Release",
                    "TBD", "PLANNED", "Studio & Global Distributors", false, "Grand theatrical premiere and global multi-screen release"));
        } catch (ValidationException ignored) {}
    }

    public ProductionMilestone findMilestoneById(String id) {
        if (id == null) return null;
        for (ProductionMilestone ms : masterSchedule) {
            if (ms.getId().equalsIgnoreCase(id.trim())) {
                return ms;
            }
        }
        return null;
    }

    public ProductionMilestone findMilestoneByStage(ProductionStage stage) {
        if (stage == null) return null;
        for (ProductionMilestone ms : masterSchedule) {
            if (ms.getStage() == stage) {
                return ms;
            }
        }
        return null;
    }

    public int getCompletedMilestonesCount() {
        int count = 0;
        for (ProductionMilestone ms : masterSchedule) {
            if (ms.isCompleted()) count++;
        }
        return count;
    }

    public String getFormattedTimeline() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(String.format("       MASTER PRODUCTION & RELEASE ROADMAP: %s\n", title.toUpperCase()));
        sb.append("================================================================================\n");
        sb.append(String.format(" Director: %-25s | Status: %-15s | Year: %d\n", director, status, releaseYear));
        sb.append(String.format(" Pipeline Progress: %d / %d Milestones Completed (%.0f%%)\n",
                getCompletedMilestonesCount(), masterSchedule.size(),
                masterSchedule.isEmpty() ? 0.0 : (getCompletedMilestonesCount() * 100.0 / masterSchedule.size())));
        sb.append("--------------------------------------------------------------------------------\n\n");

        String currentPhase = "";
        for (ProductionMilestone ms : masterSchedule) {
            String phase = ms.getStage().getPhaseName();
            if (!phase.equalsIgnoreCase(currentPhase)) {
                currentPhase = phase;
                sb.append(String.format("[%s]\n", currentPhase.toUpperCase()));
            }
            String opt = ms.isOptional() ? " [OPTIONAL]" : "";
            sb.append(String.format("   %-13s %-6s : %-44s\n",
                    ms.getStatusBadge().trim(), ms.getId(), ms.getTitle() + opt));
            sb.append(String.format("                 Target Date : %-15s | Lead: %s\n",
                    ms.getTargetDate(), ms.getResponsibleLead()));
            if (ms.getNotes() != null && !ms.getNotes().trim().isEmpty()) {
                sb.append(String.format("                 Notes       : %s\n", ms.getNotes()));
            }
            sb.append("\n");
        }
        sb.append("================================================================================\n");
        return sb.toString();
    }

    /**
     * Validates that scheduled dates strictly adhere to proper filmmaking and marketing chronology.
     * Ensures: Script Narration -> Poster Shoot -> Filming -> Picture Editing -> Audio/VFX ->
     * Poster Release -> Teaser -> Trailer -> Promotions -> Theatrical Release.
     */
    public List<String> validateScheduleSequence() {
        List<String> warnings = new ArrayList<>();
        for (int i = 0; i < masterSchedule.size(); i++) {
            ProductionMilestone current = masterSchedule.get(i);
            if (current.getTargetDate() == null || current.getTargetDate().equalsIgnoreCase("TBD")) continue;

            for (int j = i + 1; j < masterSchedule.size(); j++) {
                ProductionMilestone subsequent = masterSchedule.get(j);
                if (subsequent.getTargetDate() == null || subsequent.getTargetDate().equalsIgnoreCase("TBD")) continue;

                // ISO-8601 YYYY-MM-DD string comparison
                if (current.getTargetDate().compareTo(subsequent.getTargetDate()) > 0) {
                    warnings.add(String.format("Sequence Conflict: '%s' (%s) is scheduled AFTER '%s' (%s). "
                            + "Proper workflow requires '%s' to happen before '%s'.",
                            current.getTitle(), current.getTargetDate(),
                            subsequent.getTitle(), subsequent.getTargetDate(),
                            current.getStage().getDisplayName(), subsequent.getStage().getDisplayName()));
                }
            }
        }
        return warnings;
    }

    // =========================================================================
    // DUBBING & MULTI-LANGUAGE VOICE MANAGEMENT
    // =========================================================================
    public List<DubbingTrack> getDubbingTracks() {
        return dubbingTracks;
    }

    public void initializeDefaultDubbingTracks() {
        if (dubbingTracks != null && !dubbingTracks.isEmpty()) return;
        try {
            dubbingTracks.add(new DubbingTrack("DUB-01", "Lead Protagonist", "Original Lead Actor", "English (Original)",
                    "IN_PROGRESS", 1, "Retake scene 2 dialogue pacing", "Main Scoring Stage"));
            dubbingTracks.add(new DubbingTrack("DUB-02", "Lead Protagonist", "Regional Voice Artist A", "Hindi",
                    "PLANNED", 0, "", "YRF Studios, Mumbai"));
            dubbingTracks.add(new DubbingTrack("DUB-03", "Lead Protagonist", "Regional Voice Artist B", "Telugu",
                    "PLANNED", 0, "", "Prasad Labs, Hyderabad"));
            dubbingTracks.add(new DubbingTrack("DUB-04", "Lead Antagonist", "Dubbing Voice Specialist", "English (Original)",
                    "RECORDED", 0, "", "Audio Post Stage 1"));
            dubbingTracks.add(new DubbingTrack("DUB-05", "Lead Antagonist", "Regional Voice Artist C", "Hindi",
                    "PLANNED", 0, "", "Supa Studio, Mumbai"));
            dubbingTracks.add(new DubbingTrack("DUB-06", "Supporting Cast", "Ensemble Voice Cast", "English (Original)",
                    "APPROVED", 0, "", "Voice Booth B"));
        } catch (ValidationException ignored) {}
    }

    public void addDubbingTrack(DubbingTrack track) throws ValidationException {
        if (track == null) {
            throw new ValidationException("Dubbing Track", "Cannot add null dubbing track.");
        }
        for (DubbingTrack existing : dubbingTracks) {
            if (existing.getTrackId().equalsIgnoreCase(track.getTrackId())) {
                throw new ValidationException("Track ID", "Dubbing Track ID '" + track.getTrackId() + "' already exists.");
            }
        }
        dubbingTracks.add(track);
    }

    public DubbingTrack findDubbingTrackById(String trackId) {
        if (trackId == null) return null;
        for (DubbingTrack dt : dubbingTracks) {
            if (dt.getTrackId().equalsIgnoreCase(trackId.trim())) {
                return dt;
            }
        }
        return null;
    }

    public List<DubbingTrack> getDubbingTracksByLanguage(String lang) {
        List<DubbingTrack> matches = new ArrayList<>();
        if (lang == null || lang.trim().isEmpty()) return matches;
        for (DubbingTrack dt : dubbingTracks) {
            if (dt.getLanguage().equalsIgnoreCase(lang.trim())) {
                matches.add(dt);
            }
        }
        return matches;
    }

    public int getApprovedDubbingTracksCount() {
        int count = 0;
        for (DubbingTrack dt : dubbingTracks) {
            if (dt.isApproved()) count++;
        }
        return count;
    }

    public int getTotalRetakesCount() {
        int count = 0;
        for (DubbingTrack dt : dubbingTracks) {
            count += dt.getRetakeCount();
        }
        return count;
    }

    public String getFormattedDubbingSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(String.format("         VOICE DUBBING & LOCALIZATION MANIFEST: %s\n", title.toUpperCase()));
        sb.append("================================================================================\n");
        sb.append(String.format(" Total Tracks Registered : %d | Approved: %d/%d | Total Retakes Logged: %d\n",
                dubbingTracks.size(), getApprovedDubbingTracksCount(), dubbingTracks.size(), getTotalRetakesCount()));
        sb.append("--------------------------------------------------------------------------------\n\n");

        if (dubbingTracks.isEmpty()) {
            sb.append("   [No voice dubbing tracks registered yet]\n\n");
        } else {
            for (DubbingTrack dt : dubbingTracks) {
                sb.append(String.format("   %-16s %-6s | Lang: %-12s | Char: %-18s\n",
                        dt.getStatusBadge().trim(), dt.getTrackId(), dt.getLanguage(), dt.getCharacterName()));
                sb.append(String.format("                  Artist: %-22s | Studio: %s\n",
                        dt.getVoiceArtist(), dt.getStudioLocation()));
                if (dt.getRetakeCount() > 0) {
                    sb.append(String.format("                  Retakes: %d | Reason: %s\n",
                            dt.getRetakeCount(), dt.getRetakeReason()));
                }
                sb.append("\n");
            }
        }
        sb.append("================================================================================\n");
        return sb.toString();
    }

    // =========================================================================
    // POST-PRODUCTION MANAGEMENT (EDITING, SOUND MIXING, VFX, RENDERING)
    // =========================================================================
    public List<PostProductionTask> getPostProductionTasks() {
        return postProductionTasks;
    }

    public void initializeDefaultPostProductionTasks() {
        if (postProductionTasks != null && !postProductionTasks.isEmpty()) return;
        try {
            // 1. Picture & Video Editing
            postProductionTasks.add(new PostProductionTask("POST-01", PostProductionType.EDITING,
                    "Assembly Cut & Scene Stitching", "Lead Assembly Editor",
                    "IN_PROGRESS", 45, "Avid Media Composer / 4K ProRes Proxy", "Initial sequence assembly from camera dailies"));
            postProductionTasks.add(new PostProductionTask("POST-02", PostProductionType.EDITING,
                    "Director's Cut to Picture Lock", director + " & Chief Editor",
                    "PLANNED", 15, "Edit Decision List (EDL) v2.1", "Fine-tuning scene pacing and locking final frame cuts"));

            // 2. Sound Design & Audio Mixing
            postProductionTasks.add(new PostProductionTask("POST-03", PostProductionType.SOUND_MIXING,
                    "Foley, Ambience & ADR Sound Design", "Foley Artist & Supervising Sound Designer",
                    "IN_PROGRESS", 35, "Pro Tools HDX / 96kHz 24-bit Multi-Track", "Footsteps, prop movement, background ambience and dialogue cleanup"));
            postProductionTasks.add(new PostProductionTask("POST-04", PostProductionType.SOUND_MIXING,
                    "Dolby Atmos 7.1.4 Theatrical Final Mix", "Chief Re-Recording Mixer",
                    "PLANNED", 0, "Dolby RMU / 128 Dynamic Spatial Objects", "Master dynamic mix balancing dialogue clarity, Foley, and orchestral score"));

            // 3. Visual Effects (CGI)
            postProductionTasks.add(new PostProductionTask("POST-05", PostProductionType.VFX,
                    "3D CGI Asset Modeling & Dynamics", "VFX Studio Supervisor",
                    "IN_PROGRESS", 60, "Maya / Houdini Pyro Simulation", "Hero vehicle & creature geometry models with physics simulations"));
            postProductionTasks.add(new PostProductionTask("POST-06", PostProductionType.VFX,
                    "Digital Compositing & Chroma Keying", "Lead Nuke Compositor",
                    "PLANNED", 25, "ACES 2065-1 Wide Gamut Color Space", "Multi-layer background rotoscoping, clean plates, and wire removal"));

            // 4. Rendering & DCP Master Export
            postProductionTasks.add(new PostProductionTask("POST-07", PostProductionType.RENDERING,
                    "Render Farm Batch Processing (CGI Frames)", "Render Pipeline Technical Director",
                    "PLANNED", 10, "128-Node GPU Cluster / Redshift & Arnold", "High-resolution frame queue processing and ray-tracing passes"));
            postProductionTasks.add(new PostProductionTask("POST-08", PostProductionType.RENDERING,
                    "DCI Master Package (DCP) & QC Sign-off", "Mastering Lab Engineer",
                    "PLANNED", 0, "DCI-Compliant JPEG 2000 / 250 Mbps / 4K", "Final theatrical digital container creation, SHA-1 checksums, and audio sync check"));
        } catch (ValidationException ignored) {}
    }

    public void addPostProductionTask(PostProductionTask task) throws ValidationException {
        if (task == null) {
            throw new ValidationException("Post-Production Task", "Cannot add null post-production task.");
        }
        for (PostProductionTask existing : postProductionTasks) {
            if (existing.getTaskId().equalsIgnoreCase(task.getTaskId())) {
                throw new ValidationException("Task ID", "Post-production task ID '" + task.getTaskId() + "' already exists.");
            }
        }
        postProductionTasks.add(task);
    }

    public PostProductionTask findPostProductionTaskById(String taskId) {
        if (taskId == null) return null;
        for (PostProductionTask pt : postProductionTasks) {
            if (pt.getTaskId().equalsIgnoreCase(taskId.trim())) {
                return pt;
            }
        }
        return null;
    }

    public List<PostProductionTask> getPostProductionTasksByType(PostProductionType type) {
        List<PostProductionTask> matches = new ArrayList<>();
        if (type == null) return matches;
        for (PostProductionTask pt : postProductionTasks) {
            if (pt.getType() == type) {
                matches.add(pt);
            }
        }
        return matches;
    }

    public double getAveragePostProductionProgress() {
        if (postProductionTasks.isEmpty()) return 0.0;
        int total = 0;
        for (PostProductionTask pt : postProductionTasks) {
            total += pt.getProgressPercentage();
        }
        return (double) total / postProductionTasks.size();
    }

    public int getCompletedPostProductionTasksCount() {
        int count = 0;
        for (PostProductionTask pt : postProductionTasks) {
            if (pt.isCompleted()) count++;
        }
        return count;
    }

    public String getFormattedPostProductionDashboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(String.format("          POST-PRODUCTION WORKFLOW DASHBOARD: %s\n", title.toUpperCase()));
        sb.append("================================================================================\n");
        sb.append(String.format(" Overall Pipeline Progress: %.1f%% Completed (%d of %d Tasks Done)\n",
                getAveragePostProductionProgress(), getCompletedPostProductionTasksCount(), postProductionTasks.size()));
        sb.append("--------------------------------------------------------------------------------\n\n");

        for (PostProductionType type : PostProductionType.values()) {
            List<PostProductionTask> group = getPostProductionTasksByType(type);
            sb.append(String.format(">>> %s (%d Tasks)\n", type.getDisplayName().toUpperCase(), group.size()));
            if (group.isEmpty()) {
                sb.append("    [No tasks registered under this discipline]\n");
            } else {
                for (PostProductionTask pt : group) {
                    sb.append(String.format("    %-15s %-7s : %-32s | %s\n",
                            pt.getStatusBadge().trim(), pt.getTaskId(), pt.getTaskName(), pt.getProgressBar()));
                    sb.append(String.format("                    Lead : %-22s | Specs: %s\n",
                            pt.getLeadSpecialist(), pt.getTechnicalSpecs()));
                    if (pt.getNotes() != null && !pt.getNotes().isEmpty()) {
                        sb.append(String.format("                    Notes: %s\n", pt.getNotes()));
                    }
                    sb.append("\n");
                }
            }
        }
        sb.append("================================================================================\n");
        return sb.toString();
    }

    public List<CallSheet> getCallSheets() {
        return callSheets;
    }

    public void addCallSheet(CallSheet sheet) throws ValidationException {
        if (sheet == null) {
            throw new ValidationException("Call Sheet", "Cannot add null call sheet.");
        }
        for (CallSheet cs : callSheets) {
            if (cs.getSheetId().equalsIgnoreCase(sheet.getSheetId())) {
                throw new ValidationException("Call Sheet ID",
                        "Call Sheet with ID '" + sheet.getSheetId() + "' already exists in movie '" + title + "'.");
            }
        }
        callSheets.add(sheet);
    }

    public boolean removeCallSheet(String sheetId) {
        if (sheetId == null) return false;
        return callSheets.removeIf(cs -> cs.getSheetId().equalsIgnoreCase(sheetId.trim()));
    }

    public CallSheet findCallSheetById(String sheetId) {
        if (sheetId == null) return null;
        for (CallSheet cs : callSheets) {
            if (cs.getSheetId().equalsIgnoreCase(sheetId.trim())) {
                return cs;
            }
        }
        return null;
    }

    public CallSheet findCallSheetByDay(int dayNumber) {
        for (CallSheet cs : callSheets) {
            if (cs.getDayNumber() == dayNumber) {
                return cs;
            }
        }
        return null;
    }

    public List<DistributionRelease> getDistributionReleases() {
        return distributionReleases;
    }

    public void addDistributionRelease(DistributionRelease rel) throws ValidationException {
        if (rel == null) {
            throw new ValidationException("Distribution Release", "Cannot add null release record.");
        }
        for (DistributionRelease dr : distributionReleases) {
            if (dr.getReleaseId().equalsIgnoreCase(rel.getReleaseId())) {
                throw new ValidationException("Release ID",
                        "Release record with ID '" + rel.getReleaseId() + "' already exists in movie '" + title + "'.");
            }
        }
        distributionReleases.add(rel);
    }

    public boolean removeDistributionRelease(String releaseId) {
        if (releaseId == null) return false;
        return distributionReleases.removeIf(dr -> dr.getReleaseId().equalsIgnoreCase(releaseId.trim()));
    }

    public DistributionRelease findDistributionReleaseById(String releaseId) {
        if (releaseId == null) return null;
        for (DistributionRelease dr : distributionReleases) {
            if (dr.getReleaseId().equalsIgnoreCase(releaseId.trim())) {
                return dr;
            }
        }
        return null;
    }

    public DistributionRelease getPrimaryDistributionRelease() {
        if (!distributionReleases.isEmpty()) {
            return distributionReleases.get(0);
        }
        return null;
    }

    public String getTheatricalReleaseDate() {
        DistributionRelease p = getPrimaryDistributionRelease();
        return (p != null) ? p.getTheatricalReleaseDate() : "TBD";
    }

    public String getReleaseRegion() {
        DistributionRelease p = getPrimaryDistributionRelease();
        return (p != null) ? p.getRegion() : "Worldwide";
    }

    public String getDistributor() {
        DistributionRelease p = getPrimaryDistributionRelease();
        return (p != null) ? p.getDistributor() : "Studio Direct";
    }

    public String getTheatricalStatus() {
        DistributionRelease p = getPrimaryDistributionRelease();
        return (p != null) ? p.getTheatricalStatus() : "SCHEDULED";
    }

    public String getOttPlatform() {
        DistributionRelease p = getPrimaryDistributionRelease();
        return (p != null) ? p.getOttPlatform() : "TBD / Pending Deal";
    }

    public String getDigitalReleaseDate() {
        DistributionRelease p = getPrimaryDistributionRelease();
        return (p != null) ? p.getDigitalReleaseDate() : "TBD";
    }

    public int getTotalTheatricalScreens() {
        int sum = 0;
        for (DistributionRelease dr : distributionReleases) {
            sum += dr.getScreenCount();
        }
        return sum;
    }

    public String getFormattedDistributionSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(String.format("         THEATRICAL & DIGITAL (OTT) DISTRIBUTION: %s\n", title.toUpperCase()));
        sb.append("================================================================================\n");
        sb.append(String.format(" Primary Theatrical Date : %s | Status: %s\n", getTheatricalReleaseDate(), getTheatricalStatus()));
        sb.append(String.format(" Primary Distributor     : %s | Region: %s\n", getDistributor(), getReleaseRegion()));
        sb.append(String.format(" Total Global Screens    : %,d Screens across %d Territory Deals\n",
                getTotalTheatricalScreens(), distributionReleases.size()));
        sb.append(String.format(" Digital / OTT Partner   : %s (Streaming Date: %s)\n", getOttPlatform(), getDigitalReleaseDate()));
        sb.append("--------------------------------------------------------------------------------\n\n");

        if (distributionReleases.isEmpty()) {
            sb.append("   [No regional distribution or OTT deals registered yet]\n\n");
        } else {
            sb.append(String.format(" %-8s | %-20s | %-20s | %-10s | %-12s | %-8s | %-16s | %-10s\n",
                    "DEAL ID", "REGION / TERRITORY", "DISTRIBUTOR", "THEATRICAL", "STATUS", "SCREENS", "OTT PLATFORM", "DIGITAL"));
            sb.append("---------+----------------------+----------------------+------------+--------------+----------+------------------+-----------\n");
            for (DistributionRelease dr : distributionReleases) {
                String reg = dr.getRegion();
                if (reg.length() > 20) reg = reg.substring(0, 17) + "...";
                String dist = dr.getDistributor();
                if (dist.length() > 20) dist = dist.substring(0, 17) + "...";
                String ott = dr.getOttPlatform();
                if (ott.length() > 16) ott = ott.substring(0, 13) + "...";
                sb.append(String.format(" %-8s| %-20s | %-20s | %-10s | %-12s | %-8d | %-16s | %-10s\n",
                        dr.getReleaseId(), reg, dist, dr.getTheatricalReleaseDate(), dr.getTheatricalStatus(),
                        dr.getScreenCount(), ott, dr.getDigitalReleaseDate()));
                if (dr.getNotes() != null && !dr.getNotes().isBlank()) {
                    sb.append(String.format("         | Notes: %s\n", dr.getNotes()));
                }
            }
        }
        sb.append("================================================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        String ratingInfo = reviews.isEmpty()
                ? "No Ratings"
                : String.format("%.1f/5.0 Stars (%d review%s)", getAverageRating(), reviews.size(), reviews.size() == 1 ? "" : "s");
        return String.format("[%s] %s (%d) | %s | Status: %s | Budget: $%,.2f | Rating: %s | Script: %.1f pgs",
                id, title, releaseYear, genre, status, budget, ratingInfo, getTotalScriptPages());
    }
}
