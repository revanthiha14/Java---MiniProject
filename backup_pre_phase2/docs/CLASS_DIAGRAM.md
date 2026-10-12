# CineFlow: UML Class Diagram & Architecture
**Project**: CineFlow - Cinema Production Management System  
**Level**: 2nd-Year College Object-Oriented Programming (Java) Mini-Project  

---

## 1. Visual Mermaid Class Diagram

```mermaid
classDiagram
    direction TB

    %% Custom Exception Hierarchy (Inheritance)
    class Exception {
        <<external>>
    }

    class CineFlowException {
        +CineFlowException(message: String)
        +CineFlowException(message: String, cause: Throwable)
    }

    class BudgetExceededException {
        -double attemptedAmount
        -double remainingBudget
        -double totalBudget
        +BudgetExceededException(message: String)
        +BudgetExceededException(attemptedAmount, remainingBudget, totalBudget)
        +getAttemptedAmount() double
        +getRemainingBudget() double
        +getTotalBudget() double
    }

    class ResourceNotFoundException {
        -String resourceType
        -String resourceId
        +ResourceNotFoundException(message: String)
        +ResourceNotFoundException(resourceType, resourceId)
        +getResourceType() String
        +getResourceId() String
    }

    class ScheduleConflictException {
        -String locationOrEntity
        -String conflictDate
        -String reason
        +ScheduleConflictException(message: String)
        +ScheduleConflictException(locationOrEntity, conflictDate, reason)
        +getLocationOrEntity() String
        +getConflictDate() String
        +getReason() String
    }

    class ValidationException {
        -String fieldName
        +ValidationException(message: String)
        +ValidationException(fieldName, message)
        +getFieldName() String
    }

    Exception <|-- CineFlowException : Extends
    CineFlowException <|-- BudgetExceededException : Inheritance
    CineFlowException <|-- ResourceNotFoundException : Inheritance
    CineFlowException <|-- ScheduleConflictException : Inheritance
    CineFlowException <|-- ValidationException : Inheritance

    %% Abstract Base Class & Subclasses (Inheritance & Polymorphism)
    class Person {
        <<abstract>>
        -String id
        -String name
        -String email
        +Person(id, name, email)
        +getId() String
        +setId(id) void
        +getName() String
        +setName(name) void
        +getEmail() String
        +setEmail(email) void
        +getRole()* String
        +displayInfo() void
        +toString() String
    }

    class Actor {
        -String characterName
        +Actor(id, name, email, characterName)
        +getCharacterName() String
        +setCharacterName(characterName) void
        +getRole() String
    }

    class CrewMember {
        -String department
        +CrewMember(id, name, email, department)
        +getDepartment() String
        +setDepartment(department) void
        +getRole() String
    }

    Person <|-- Actor : Inheritance
    Person <|-- CrewMember : Inheritance

    %% Lifecycle & Production Enums
    class MovieStatus {
        <<enumeration>>
        PLANNING
        PRODUCTION
        POST_PRODUCTION
        RELEASED
    }

    class ProductionStage {
        <<enumeration>>
        SCRIPT_NARRATION
        POSTER_PHOTOSHOOT
        PRINCIPAL_PHOTOGRAPHY
        PICTURE_EDITING
        AUDIO_AND_DUBBING
        VFX_AND_CGI
        POSTER_RELEASE
        TEASER_RELEASE
        TRAILER_RELEASE
        PRESS_MEET
        PROMOTIONAL_CAMPAIGN
        THEATRICAL_RELEASE
        +getPhaseName() String
        +getDisplayName() String
        +getSequenceOrder() int
    }

    %% Production Milestone Entity
    class ProductionMilestone {
        -String id
        -ProductionStage stage
        -String title
        -String targetDate
        -String status
        -String responsibleLead
        -boolean optional
        -String notes
        +ProductionMilestone(id, stage, targetDate)
        +ProductionMilestone(id, stage, title, targetDate, status, lead, optional, notes)
        +getId() String
        +setId(id) void
        +getStage() ProductionStage
        +setStage(stage) void
        +getTitle() String
        +setTitle(title) void
        +getTargetDate() String
        +setTargetDate(targetDate) void
        +getStatus() String
        +setStatus(status) void
        +getResponsibleLead() String
        +setResponsibleLead(lead) void
        +isOptional() boolean
        +setOptional(optional) void
        +getNotes() String
        +setNotes(notes) void
        +isCompleted() boolean
        +isInProgress() boolean
        +getStatusBadge() String
        +toString() String
    }

    %% Voice Dubbing Entity
    class DubbingTrack {
        -String trackId
        -String characterName
        -String voiceArtist
        -String language
        -String status
        -int retakeCount
        -String retakeReason
        -String studioLocation
        +DubbingTrack(trackId, characterName, voiceArtist, language)
        +DubbingTrack(trackId, characterName, voiceArtist, language, status, retakes, reason, studio)
        +getTrackId() String
        +setTrackId(id) void
        +getCharacterName() String
        +setCharacterName(name) void
        +getVoiceArtist() String
        +setVoiceArtist(artist) void
        +getLanguage() String
        +setLanguage(lang) void
        +getStatus() String
        +setStatus(status) void
        +getRetakeCount() int
        +setRetakeCount(count) void
        +getRetakeReason() String
        +setRetakeReason(reason) void
        +getStudioLocation() String
        +setStudioLocation(studio) void
        +logRetake(reason) void
        +approveTrack() void
        +isApproved() boolean
        +getStatusBadge() String
        +toString() String
    }

    %% Post-Production Enums & Tasks
    class PostProductionType {
        <<enumeration>>
        EDITING
        SOUND_MIXING
        VFX
        RENDERING
        +getDisplayName() String
    }

    class PostProductionTask {
        -String taskId
        -PostProductionType type
        -String taskName
        -String leadSpecialist
        -String status
        -int progressPercentage
        -String technicalSpecs
        -String notes
        +PostProductionTask(taskId, type, taskName, lead)
        +PostProductionTask(taskId, type, taskName, lead, status, progress, specs, notes)
        +getTaskId() String
        +setTaskId(id) void
        +getType() PostProductionType
        +setType(type) void
        +getTaskName() String
        +setTaskName(name) void
        +getLeadSpecialist() String
        +setLeadSpecialist(lead) void
        +getStatus() String
        +setStatus(status) void
        +getProgressPercentage() int
        +setProgressPercentage(progress) void
        +getTechnicalSpecs() String
        +setTechnicalSpecs(specs) void
        +getNotes() String
        +setNotes(notes) void
        +updateProgress(percentage, status, notes) void
        +markCompleted() void
        +isCompleted() boolean
        +getStatusBadge() String
        +getProgressBar() String
        +toString() String
    }

    %% Daily Call Sheet & Cast Call Entities
    class CastCallEntry {
        -String actorId
        -String actorName
        -String characterName
        -String pickupTime
        -String makeupCall
        -String onSetCall
        -String instructions
        +CastCallEntry(actorId, actorName, characterName, pickupTime, makeupCall, onSetCall, instructions)
        +getActorId() String
        +getActorName() String
        +getCharacterName() String
        +getPickupTime() String
        +getMakeupCall() String
        +getOnSetCall() String
        +getInstructions() String
        +toSerializedString() String
        +fromSerializedString(data) CastCallEntry
        +toString() String
    }

    class CallSheet {
        -String sheetId
        -int dayNumber
        -int totalDays
        -String shootDate
        -String generalCrewCall
        -String shootingLocation
        -String weatherAndSun
        -String nearestHospital
        -String status
        -List~Integer~ scheduledSceneNumbers
        -List~CastCallEntry~ castCalls
        -String departmentNotes
        -double pagesShot
        -String wrapNotes
        +CallSheet(sheetId, dayNumber, totalDays, shootDate, generalCrewCall, location, weather, hospital, status)
        +getSheetId() String
        +getDayNumber() int
        +getTotalDays() int
        +getShootDate() String
        +getGeneralCrewCall() String
        +getShootingLocation() String
        +getWeatherAndSun() String
        +getNearestHospital() String
        +getStatus() String
        +getScheduledSceneNumbers() List~Integer~
        +addScheduledScene(sceneNumber) void
        +removeScheduledScene(sceneNumber) boolean
        +getCastCalls() List~CastCallEntry~
        +addCastCall(entry) void
        +removeCastCall(actorId) boolean
        +getDepartmentNotes() String
        +setDepartmentNotes(notes) void
        +getPagesShot() double
        +setPagesShot(pages) void
        +getWrapNotes() String
        +setWrapNotes(notes) void
        +recordWrap(pagesShot, wrapNotes) void
        +calculateTotalScheduledPages(movie) double
        +getFormattedCallSheet(movie) String
        +toString() String
    }

    %% Theatrical & OTT Distribution Entity
    class DistributionRelease {
        -String releaseId
        -String region
        -String distributor
        -String theatricalReleaseDate
        -String theatricalStatus
        -int screenCount
        -String ottPlatform
        -String digitalReleaseDate
        -String notes
        +DistributionRelease(releaseId, region, distributor, theatricalReleaseDate)
        +DistributionRelease(releaseId, region, distributor, theatricalDate, theatricalStatus, screenCount, ottPlatform, digitalDate, notes)
        +getReleaseId() String
        +setReleaseId(id) void
        +getRegion() String
        +setRegion(region) void
        +getDistributor() String
        +setDistributor(distributor) void
        +getTheatricalReleaseDate() String
        +setTheatricalReleaseDate(date) void
        +getTheatricalStatus() String
        +setTheatricalStatus(status) void
        +getScreenCount() int
        +setScreenCount(count) void
        +getOttPlatform() String
        +setOttPlatform(platform) void
        +hasOttPlatform() boolean
        +getDigitalReleaseDate() String
        +setDigitalReleaseDate(date) void
        +hasDigitalReleaseDate() boolean
        +getNotes() String
        +setNotes(notes) void
        +toString() String
    }

    %% Review Entity
    class Review {
        -String reviewerName
        -int rating
        -String comment
        +Review(reviewerName, rating, comment)
        +getReviewerName() String
        +setReviewerName(name) void
        +getRating() int
        +setRating(rating) void
        +getComment() String
        +setComment(comment) void
        +toString() String
    }

    %% Core Domain Entities
    class Scene {
        -int sceneNumber
        -String title
        -String location
        -String status
        -String shootingDate
        -String slugline
        -double scriptPages
        -String scriptContent
        +Scene(sceneNumber, title, location, status)
        +Scene(sceneNumber, title, location, status, shootingDate)
        +Scene(sceneNumber, title, location, status, shootingDate, slugline, pages, content)
        +getSceneNumber() int
        +setSceneNumber(number) void
        +getTitle() String
        +setTitle(title) void
        +getLocation() String
        +setLocation(loc) void
        +getStatus() String
        +setStatus(status) void
        +getShootingDate() String
        +setShootingDate(date) void
        +getSlugline() String
        +setSlugline(slug) void
        +getScriptPages() double
        +setScriptPages(pages) void
        +getScriptContent() String
        +setScriptContent(content) void
        +hasScript() boolean
        +updateScript(slug, pages, content) void
        +getFormattedScript() String
        +toString() String
    }

    class Expense {
        -String id
        -String category
        -double amount
        -String description
        +Expense(id, category, amount, description)
        +getId() String
        +setId(id) void
        +getCategory() String
        +setCategory(category) void
        +getAmount() double
        +setAmount(amount) void
        +getDescription() String
        +setDescription(desc) void
        +toString() String
    }

    class Movie {
        -String id
        -String title
        -String genre
        -String director
        -int releaseYear
        -double budget
        -MovieStatus status
        -String screenplayWriter
        -String scriptDraftVersion
        -String synopsis
        -List~Scene~ scenes
        -List~Person~ castAndCrew
        -List~Expense~ expenses
        -List~Review~ reviews
        -List~ProductionMilestone~ masterSchedule
        -List~DubbingTrack~ dubbingTracks
        -List~PostProductionTask~ postProductionTasks
        -List~CallSheet~ callSheets
        -List~DistributionRelease~ distributionReleases
        +Movie(id, title, genre, director, year, budget)
        +Movie(id, title, genre, director, year, budget, status)
        +getScreenplayWriter() String
        +setScreenplayWriter(writer) void
        +getScriptDraftVersion() String
        +setScriptDraftVersion(draft) void
        +getSynopsis() String
        +setSynopsis(synopsis) void
        +getTotalScriptPages() double
        +getEstimatedRuntimeMinutes() double
        +getScenesWithScriptCount() int
        +getFormattedScreenplay() String
        +getMasterSchedule() List~ProductionMilestone~
        +initializeDefaultSchedule() void
        +findMilestoneById(id) ProductionMilestone
        +findMilestoneByStage(stage) ProductionMilestone
        +getCompletedMilestonesCount() int
        +getFormattedTimeline() String
        +validateScheduleSequence() List~String~
        +getDubbingTracks() List~DubbingTrack~
        +initializeDefaultDubbingTracks() void
        +addDubbingTrack(DubbingTrack) void
        +findDubbingTrackById(trackId) DubbingTrack
        +getDubbingTracksByLanguage(lang) List~DubbingTrack~
        +getApprovedDubbingTracksCount() int
        +getTotalRetakesCount() int
        +getFormattedDubbingSummary() String
        +getPostProductionTasks() List~PostProductionTask~
        +initializeDefaultPostProductionTasks() void
        +addPostProductionTask(PostProductionTask) void
        +findPostProductionTaskById(taskId) PostProductionTask
        +getPostProductionTasksByType(PostProductionType) List~PostProductionTask~
        +getAveragePostProductionProgress() double
        +getCompletedPostProductionTasksCount() int
        +getFormattedPostProductionDashboard() String
        +getDistributionReleases() List~DistributionRelease~
        +addDistributionRelease(DistributionRelease) void
        +removeDistributionRelease(releaseId) boolean
        +findDistributionReleaseById(releaseId) DistributionRelease
        +getPrimaryDistributionRelease() DistributionRelease
        +getTheatricalReleaseDate() String
        +getReleaseRegion() String
        +getDistributor() String
        +getTheatricalStatus() String
        +getOttPlatform() String
        +getDigitalReleaseDate() String
        +getTotalTheatricalScreens() int
        +getFormattedDistributionSummary() String
        +getId() String
        +getTitle() String
        +getGenre() String
        +getDirector() String
        +getReleaseYear() int
        +getBudget() double
        +getStatus() MovieStatus
        +setStatus(status) void
        +getScenes() List~Scene~
        +getCastAndCrew() List~Person~
        +getExpenses() List~Expense~
        +getReviews() List~Review~
        +addScene(Scene) void
        +scheduleScene(sceneNumber, shootingDate) void
        +findSceneByNumber(sceneNumber) Scene
        +getSceneOrThrow(sceneNumber) Scene
        +addPerson(Person) void
        +removePerson(personId) boolean
        +findPersonById(personId) Person
        +addExpense(Expense) void
        +addReview(Review) void
        +getAverageRating() double
        +getTotalExpenses() double
        +getRemainingBudget() double
        +getBudgetUtilizationPercentage() double
        +toString() String
    }

    class ProductionHouse {
        -String id
        -String name
        -String headquarters
        -List~Movie~ movies
        +ProductionHouse(id, name, headquarters)
        +getId() String
        +getName() String
        +getHeadquarters() String
        +getMovies() List~Movie~
        +addMovie(Movie) void
        +findMovieById(id) Movie
        +getMovieOrThrow(id) Movie
        +toString() String
    }

    %% Service & Management Classes
    class CastManager {
        +CastManager()
        +addPerson(Movie, Person) void
        +addActor(Movie, Actor) void
        +addCrewMember(Movie, CrewMember) void
        +removePersonById(Movie, personId) boolean
        +findPersonById(Movie, personId) Person
        +searchPersonnel(Movie, query) List~Person~
        +getActors(Movie) List~Actor~
        +getCrewMembers(Movie) List~CrewMember~
        +getAllPersonnel(Movie) List~Person~
    }

    class ReviewManager {
        +ReviewManager()
        +addReview(Movie, Review) void
        +calculateAverageRating(Movie) double
        +getRatingDistribution(Movie) Map~Integer, Integer~
        +filterMoviesByMinRating(movies, minRating) List~Movie~
        +getReviews(Movie) List~Review~
        +getTotalReviewCount(Movie) int
    }

    class FileService {
        -String dataDir
        +FileService()
        +FileService(dataDir: String)
        +saveAllData(studios: List~ProductionHouse~) void
        +loadAllData() List~ProductionHouse~
        +hasExistingData() boolean
        +exportScreenplayToFile(Movie) File
        +exportScheduleToFile(Movie) File
        +exportDubbingToFile(Movie) File
        +exportPostProductionToFile(Movie) File
        +exportCallSheetToFile(Movie, CallSheet) File
        +exportDistributionToFile(Movie) File
    }

    class MovieManager {
        -Map~String, ProductionHouse~ studios
        -FileService fileService
        -CastManager castManager
        -ReviewManager reviewManager
        -String activeStudioId
        -String activeMovieId
        +MovieManager()
        +addStudio(ProductionHouse) void
        +getStudio(id: String) ProductionHouse
        +getStudioOrThrow(id: String) ProductionHouse
        +getAllStudios() List~ProductionHouse~
        +getAllMovies() List~Movie~
        +getActiveStudio() ProductionHouse
        +setActiveStudioId(id: String) void
        +getActiveMovie() Movie
        +setActiveMovieId(id: String) void
        +addMovieToActiveStudio(Movie) void
        +findMovieAnywhere(id: String) Movie
        +findMovieOrThrow(id: String) Movie
        +searchMovies(query, genre, minRating) List~Movie~
        +getCastManager() CastManager
        +getReviewManager() ReviewManager
        +getFileService() FileService
        +exportActiveMovieScreenplay() File
        +exportActiveMovieSchedule() File
        +exportActiveMovieDubbing() File
        +exportActiveMoviePostProduction() File
        +exportActiveMovieCallSheet(CallSheet) File
        +exportActiveMovieDistribution() File
        +saveData() void
        +loadData() void
        +seedInitialData() void
    }

    %% Presentation / CLI
    class ConsoleMenu {
        -MovieManager manager
        -CastManager castManager
        -ReviewManager reviewManager
        -Scanner scanner
        +ConsoleMenu(manager: MovieManager)
        +ConsoleMenu(manager, castManager, reviewManager)
        +start() void
    }

    class Main {
        +main(args: String[]) void
    }
```

---

## 2. Summary of Key OOP Concepts Demonstrated

1. **Classes and Objects**: Separation of domain entities (`Movie`, `ProductionHouse`, `Scene`, `Expense`, `Review`, `Person`) and service managers (`MovieManager`, `CastManager`, `ReviewManager`, `FileService`).
2. **Encapsulation & Single Responsibility Principle (SRP)**:
   - `CastManager` manages personnel roster operations (add, remove, search, filter).
   - `ReviewManager` manages audience feedback, rating verification, and distribution analytics.
   - Private attributes with validated getters and setters across all entities.
3. **Inheritance & Abstraction**:
   - `Person` is an abstract base class extended by concrete subclasses `Actor` and `CrewMember`.
   - `CineFlowException` is an abstract base checked exception extended by four concrete specialized checked exceptions.
4. **Polymorphism & Dynamic Method Dispatch**:
   - `Actor` and `CrewMember` override `getRole()`.
   - In `Movie` and `CastManager`, `Person` references are handled polymorphically, and `instanceof` with downcasting is demonstrated in `getActors()` and `getCrewMembers()`.
5. **Enumerations**: `MovieStatus` encapsulates the 4 lifecycle states (`PLANNING`, `PRODUCTION`, `POST_PRODUCTION`, `RELEASED`).
6. **Custom Checked Exception Hierarchy**:
   - `CineFlowException extends Exception`
   - `BudgetExceededException extends CineFlowException`
   - `ResourceNotFoundException extends CineFlowException`
   - `ScheduleConflictException extends CineFlowException`
   - `ValidationException extends CineFlowException`
7. **Collections Framework**:
   - `ArrayList` for ordered dynamic collections (movies, scenes, personnel, expenses, reviews).
   - `HashMap` for $O(1)$ studio lookups by ID and frequency counting in `ReviewManager.getRatingDistribution()`.
8. **File Handling (Java I/O)**:
   - Human-readable pipe-delimited text files in `data/` using `BufferedReader`, `PrintWriter`, `FileReader`, and `FileWriter`.
   - Fault-tolerant parsing skips corrupted lines gracefully with logged warnings rather than terminating the application or silently wiping existing memory state.
9. **Production Operations & Release Modeling**:
   - Real-world film studio workflows modeled through specialized entities: `ProductionMilestone` (master roadmap), `DubbingTrack` (multi-language voice dubbing), `PostProductionTask` (sound, VFX, editing), `CallSheet` (daily shooting schedules), `CastCallEntry` (individual actor call times), and `DistributionRelease` (theatrical territories, distributors, screen counts, theatrical status, OTT platform, and digital streaming premiere dates).
   - Dedicated text document generators for screenplays, schedules, dubbing manifests, post-production reports, daily call sheets, and theatrical/OTT distribution manifests under `data/`.
