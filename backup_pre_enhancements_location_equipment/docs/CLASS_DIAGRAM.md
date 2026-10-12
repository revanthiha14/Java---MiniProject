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

    %% Enumerations
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
    }

    class PostProductionType {
        <<enumeration>>
        EDITING
        SOUND_MIXING
        VFX
        RENDERING
    }

    class ItemCategory {
        <<enumeration>>
        PROP
        COSTUME
        EQUIPMENT
        +fromString(val: String)$ ItemCategory
    }

    class AssetType {
        <<enumeration>>
        POSTER
        TEASER
        TRAILER
        BANNER
        SOCIAL_MEDIA
        +fromString(val: String)$ AssetType
    }

    class ApprovalStatus {
        <<enumeration>>
        PENDING
        APPROVED
        REJECTED
        +fromString(val: String)$ ApprovalStatus
    }

    %% Domain Entities
    class Scene {
        -int sceneNumber
        -String title
        -String location
        -String status
        -String shootingDate
        -String slugline
        -double scriptPages
        -String scriptContent
        +Scene(sceneNumber, title, location, status, shootingDate, slugline, scriptPages, scriptContent)
        +getSceneNumber() int
        +getTitle() String
        +getLocation() String
        +getStatus() String
        +getShootingDate() String
        +getSlugline() String
        +getScriptPages() double
        +getScriptContent() String
        +hasScript() boolean
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

    class Review {
        -String reviewerName
        -int rating
        -String comment
        +Review(reviewerName, rating, comment)
        +getReviewerName() String
        +setReviewerName(reviewerName) void
        +getRating() int
        +setRating(rating) void
        +getComment() String
        +setComment(comment) void
        +toString() String
    }

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
        +ProductionMilestone(id, stage, title, targetDate, status, responsibleLead, optional, notes)
        +getId() String
        +getStage() ProductionStage
        +getTitle() String
        +getTargetDate() String
        +getStatus() String
        +getResponsibleLead() String
        +isOptional() boolean
        +getNotes() String
        +isCompleted() boolean
        +setStatus(status) void
        +setTargetDate(targetDate) void
        +setResponsibleLead(lead) void
    }

    class DubbingTrack {
        -String trackId
        -String characterOrRole
        -String voiceArtistName
        -String language
        -String status
        -int retakeCount
        -String retakeNotes
        -String studioLocation
        +DubbingTrack(trackId, characterOrRole, voiceArtistName, language)
        +DubbingTrack(trackId, characterOrRole, voiceArtistName, language, status, retakeCount, retakeNotes, studioLocation)
        +getTrackId() String
        +getLanguage() String
        +getStatus() String
        +isApproved() boolean
        +getRetakeCount() int
        +logRetake(notes) void
        +approve() void
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
        +PostProductionTask(taskId, type, taskName, leadSpecialist)
        +PostProductionTask(taskId, type, taskName, leadSpecialist, status, progressPercentage, technicalSpecs, notes)
        +getTaskId() String
        +getType() PostProductionType
        +getProgressPercentage() int
        +setProgressPercentage(pct) void
        +isCompleted() boolean
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
        -double pagesShot
        -String wrapNotes
        -String departmentNotes
        -List~CastCallEntry~ castCalls
        +CallSheet(sheetId, dayNumber, totalDays, shootDate, generalCrewCall, location, weather, hospital, status)
        +addScheduledScene(sceneNumber) void
        +addCastCall(CastCallEntry) void
        +recordWrap(pages, notes) void
        +getPagesShot() double
        +getCastCalls() List~CastCallEntry~
    }

    class CastCallEntry {
        -String actorId
        -String actorName
        -String characterName
        -String pickupTime
        -String hmuCallTime
        -String onSetCallTime
        -String notes
        +CastCallEntry(actorId, actorName, characterName, pickupTime, hmuCallTime, onSetCallTime, notes)
        +toSerializedString() String
        +fromSerializedString(data)$ CastCallEntry
    }

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
        +DistributionRelease(releaseId, region, distributor, theatricalDate, status, screenCount, ottPlatform, digitalDate, notes)
        +getReleaseId() String
        +getRegion() String
        +getDistributor() String
        +getTheatricalReleaseDate() String
        +getTheatricalStatus() String
        +getScreenCount() int
        +getOttPlatform() String
        +getDigitalReleaseDate() String
    }

    %% Module A: Props, Costumes, Equipment Management
    class InventoryItem {
        -String id
        -String name
        -ItemCategory category
        -int totalQuantity
        -int availableQuantity
        -int damagedQuantity
        -int lostQuantity
        -String condition
        -String notes
        +InventoryItem(id, name, category, totalQuantity, condition, notes)
        +InventoryItem(id, name, category, totalQuantity, avail, dmg, lost, condition, notes)
        +getId() String
        +getName() String
        +getCategory() ItemCategory
        +getTotalQuantity() int
        +getAvailableQuantity() int
        +getAssignedQuantity() int
        +getDamagedQuantity() int
        +getLostQuantity() int
        +getCondition() String
        +getNotes() String
        +checkout(quantity: int) void
        +reconcileReturn(goodQty, damagedQty, lostQty) void
        +getStatusBadge() String
    }

    class ItemAssignment {
        -String assignmentId
        -String itemId
        -String movieId
        -int sceneNumber
        -int quantity
        -String status
        -String assignedDate
        -String returnDate
        -String notes
        +ItemAssignment(assignmentId, itemId, movieId, sceneNumber, quantity, status, assignedDate, returnDate, notes)
        +getAssignmentId() String
        +getItemId() String
        +getMovieId() String
        +getSceneNumber() int
        +getQuantity() int
        +getStatus() String
        +getAssignedDate() String
        +getReturnDate() String
        +getSceneReference() String
        +isCheckedOut() boolean
        +markReturned(returnDate, notes) void
    }

    %% Module B: Poster & Promotional Asset Management
    class PromotionalAsset {
        -String assetId
        -String movieId
        -AssetType assetType
        -String title
        -String filePathOrUrl
        -String language
        -String platform
        -ApprovalStatus approvalStatus
        -int revisionNumber
        -String approvalDetails
        -String lastUpdatedDate
        +PromotionalAsset(assetId, movieId, type, title, filePath, language, platform)
        +PromotionalAsset(assetId, movieId, type, title, filePath, language, platform, status, rev, details, date)
        +getAssetId() String
        +getMovieId() String
        +getAssetType() AssetType
        +getTitle() String
        +getFilePathOrUrl() String
        +getLanguage() String
        +getPlatform() String
        +getApprovalStatus() ApprovalStatus
        +getRevisionNumber() int
        +getApprovalDetails() String
        +approve(approvedBy, notes) void
        +reject(rejectedBy, notes) void
        +submitRevision(newFilePathOrUrl, revisionNotes) void
        +isApproved() boolean
        +isPending() boolean
        +isRejected() boolean
        +getStatusBadge() String
    }

    %% Aggregating Entities
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
        +getId() String
        +getTitle() String
        +getBudget() double
        +getStatus() MovieStatus
        +getScenes() List~Scene~
        +getCastAndCrew() List~Person~
        +getExpenses() List~Expense~
        +getReviews() List~Review~
        +getMasterSchedule() List~ProductionMilestone~
        +getDubbingTracks() List~DubbingTrack~
        +getPostProductionTasks() List~PostProductionTask~
        +getCallSheets() List~CallSheet~
        +getDistributionReleases() List~DistributionRelease~
        +addScene(Scene) void
        +scheduleScene(sceneNumber, shootingDate) void
        +addPerson(Person) void
        +addExpense(Expense) void
        +addReview(Review) void
        +getTotalExpenses() double
        +getRemainingBudget() double
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
    }

    %% Service & Management Classes
    class CastManager {
        +CastManager()
        +addPerson(Movie, Person) void
        +addActor(Movie, Actor) void
        +addCrewMember(Movie, CrewMember) void
        +removePersonById(Movie, personId) boolean
        +findPersonById(Movie, personId) Person
        +getActors(Movie) List~Actor~
        +getCrewMembers(Movie) List~CrewMember~
    }

    class ReviewManager {
        +ReviewManager()
        +addReview(Movie, Review) void
        +calculateAverageRating(Movie) double
        +getRatingDistribution(Movie) Map~Integer, Integer~
        +filterMoviesByMinRating(movies, minRating) List~Movie~
    }

    class InventoryManager {
        -Map~String, InventoryItem~ items
        -List~ItemAssignment~ assignments
        +InventoryManager()
        +addItem(InventoryItem) void
        +findItemById(itemId) InventoryItem
        +getItemOrThrow(itemId) InventoryItem
        +getAllItems() List~InventoryItem~
        +searchItems(query, category, condition) List~InventoryItem~
        +checkoutItem(itemId, Movie, sceneNumber, quantity, date, notes) ItemAssignment
        +returnItem(assignmentId, goodQty, damagedQty, lostQty, date, notes) void
        +getAssignmentsForMovie(movieId) List~ItemAssignment~
        +getAllAssignments() List~ItemAssignment~
        +getAvailableUnitsCount() int
        +getDamagedUnitsCount() int
        +getLostUnitsCount() int
    }

    class PromotionManager {
        -Map~String, PromotionalAsset~ assets
        +PromotionManager()
        +addAsset(PromotionalAsset) void
        +findAssetById(assetId) PromotionalAsset
        +getAssetOrThrow(assetId) PromotionalAsset
        +getAllAssets() List~PromotionalAsset~
        +getAssetsForMovie(movieId) List~PromotionalAsset~
        +searchAssets(query, movieId, type, status) List~PromotionalAsset~
        +approveAsset(assetId, approvedBy, notes) void
        +rejectAsset(assetId, rejectedBy, notes) void
        +submitRevision(assetId, newPath, notes) void
    }

    class ReportService {
        -MovieManager movieManager
        -InventoryManager inventoryManager
        -PromotionManager promotionManager
        +ReportService(movieManager, inventoryManager, promotionManager)
        +generateExecutiveDashboard(Movie) String
        +generateComprehensiveAnalyticalReport(Movie) String
        +generateStudioConsolidatedReport(ProductionHouse) String
        +exportProductionReportToFile(Movie) File
    }

    class FileService {
        -String dataDir
        +FileService()
        +FileService(dataDir: String)
        +saveAllData(studios, inventoryManager, promotionManager) void
        +loadAllData(inventoryManager, promotionManager) List~ProductionHouse~
        +hasExistingData() boolean
        +hasExistingInventoryData() boolean
        +hasExistingPromotionalData() boolean
        +saveInventory(InventoryManager) void
        +loadInventory(InventoryManager) void
        +savePromotionalAssets(PromotionManager) void
        +loadPromotionalAssets(PromotionManager) void
        +exportScreenplayToFile(Movie) File
        +exportScheduleToFile(Movie) File
        +exportCallSheetToFile(Movie, CallSheet) File
        +exportDistributionToFile(Movie) File
        +exportProductionReportToFile(reportContent, filename) boolean
    }

    class MovieManager {
        -Map~String, ProductionHouse~ studios
        -FileService fileService
        -CastManager castManager
        -ReviewManager reviewManager
        -InventoryManager inventoryManager
        -PromotionManager promotionManager
        -ReportService reportService
        -String activeStudioId
        -String activeMovieId
        +MovieManager()
        +getAllStudios() List~ProductionHouse~
        +getAllMovies() List~Movie~
        +getActiveStudio() ProductionHouse
        +getActiveMovie() Movie
        +setActiveStudioId(id) void
        +setActiveMovieId(id) void
        +findMovieAnywhere(id) Movie
        +getInventoryManager() InventoryManager
        +getPromotionManager() PromotionManager
        +getReportService() ReportService
        +saveData() void
        +loadData() void
        +seedInitialData() void
        +seedDefaultInventory() void
        +seedDefaultPromotionalAssets() void
    }

    class ConsoleMenu {
        -MovieManager manager
        -CastManager castManager
        -ReviewManager reviewManager
        -InventoryManager inventoryManager
        -PromotionManager promotionManager
        -ReportService reportService
        -Scanner scanner
        +ConsoleMenu(manager)
        +start() void
        -manageInventory() void
        -managePromotionalAssets() void
        -viewProductionReports() void
    }

    class Main {
        +main(args: String[]) void
    }

    %% Relationships & Multiplicities
    ProductionHouse "1" *-- "*" Movie : Aggregates
    Movie "1" *-- "*" Scene : Contains
    Movie "1" *-- "*" Expense : Contains
    Movie "1" *-- "*" Review : Contains
    Movie "1" *-- "*" Person : Associates
    Movie "1" *-- "*" ProductionMilestone : Tracks
    Movie "1" *-- "*" DubbingTrack : Manages
    Movie "1" *-- "*" PostProductionTask : Coordinates
    Movie "1" *-- "*" CallSheet : Schedules
    Movie "1" *-- "*" DistributionRelease : Releases
    CallSheet "1" *-- "*" CastCallEntry : Contains

    InventoryItem "1" ..> ItemCategory : Uses
    ItemAssignment "*" ..> InventoryItem : Links Item
    ItemAssignment "*" ..> Movie : Links Movie & Scene
    InventoryManager "1" *-- "*" InventoryItem : Manages
    InventoryManager "1" *-- "*" ItemAssignment : Tracks

    PromotionalAsset "1" ..> AssetType : Uses
    PromotionalAsset "1" ..> ApprovalStatus : Uses
    PromotionalAsset "*" ..> Movie : References
    PromotionManager "1" *-- "*" PromotionalAsset : Manages

    MovieManager "1" *-- "1" InventoryManager : Coordinates
    MovieManager "1" *-- "1" PromotionManager : Coordinates
    MovieManager "1" *-- "1" ReportService : Uses
    MovieManager "1" *-- "1" CastManager : Uses
    MovieManager "1" *-- "1" ReviewManager : Uses
    MovieManager "1" *-- "1" FileService : Uses
    ConsoleMenu "1" ..> MovieManager : Drives
    Main ..> MovieManager : Bootstraps
    Main ..> ConsoleMenu : Launches
```

---

## 2. Summary of Key OOP Concepts Demonstrated

1. **Classes and Objects**: Clear separation of domain entities (`Movie`, `ProductionHouse`, `Scene`, `Expense`, `Review`, `Person`, `InventoryItem`, `ItemAssignment`, `PromotionalAsset`), operational workflow entities (`CallSheet`, `CastCallEntry`, `ProductionMilestone`, `DubbingTrack`, `PostProductionTask`, `DistributionRelease`), and service managers (`MovieManager`, `CastManager`, `ReviewManager`, `InventoryManager`, `PromotionManager`, `ReportService`, `FileService`).
2. **Encapsulation & Single Responsibility Principle (SRP)**:
   - `InventoryManager` manages props, costumes, and equipment lifecycle (add, search, checkout to movie/scene, return reconciliation).
   - `PromotionManager` manages promotional asset pipelines, reviews, approval/rejection workflows, and revisions.
   - `ReportService` aggregates cross-domain reporting (KPI dashboards, financial ledgers, filming progress, marketing status, inventory allocations) without modifying underlying records.
   - `CastManager` specializes in talent and crew roster operations.
   - `ReviewManager` specializes in audience and critic feedback analytics.
   - Private attributes with validated getters and setters across all entities preserve business invariants.
3. **Inheritance & Abstraction**:
   - `Person` is an abstract base class extended by concrete subclasses `Actor` and `CrewMember`.
   - `CineFlowException` is an abstract base checked exception extended by four concrete specialized checked exceptions (`BudgetExceededException`, `ResourceNotFoundException`, `ScheduleConflictException`, `ValidationException`).
4. **Polymorphism & Dynamic Method Dispatch**:
   - `Actor` and `CrewMember` override abstract `getRole()` and `displayInfo()`.
   - In `Movie` and `CastManager`, `Person` references are handled polymorphically, and `instanceof` with safe downcasting separates actors from technical crew.
5. **Enumerations**:
   - `MovieStatus`: `PLANNING`, `PRODUCTION`, `POST_PRODUCTION`, `RELEASED`
   - `ProductionStage`: 12 film production roadmap phases
   - `PostProductionType`: `EDITING`, `SOUND_MIXING`, `VFX`, `RENDERING`
   - `ItemCategory`: `PROP`, `COSTUME`, `EQUIPMENT`
   - `AssetType`: `POSTER`, `TEASER`, `TRAILER`, `BANNER`, `SOCIAL_MEDIA`
   - `ApprovalStatus`: `PENDING`, `APPROVED`, `REJECTED`
6. **Custom Checked Exception Hierarchy**:
   - `CineFlowException extends Exception`
   - `BudgetExceededException extends CineFlowException`: enforces allocated budget limits.
   - `ResourceNotFoundException extends CineFlowException`: handles missing movies, scenes, personnel, assets, or inventory items.
   - `ScheduleConflictException extends CineFlowException`: prevents location double-booking on the same shooting date.
   - `ValidationException extends CineFlowException`: enforces non-empty IDs/names, positive quantities, and state transition preconditions.
7. **Collections Framework**:
   - `ArrayList`: Dynamic ordered storage for movies, scenes, expenses, cast/crew, reviews, assignments, milestones, and releases.
   - `HashMap` and `LinkedHashMap`: $O(1)$ fast lookups for studios, inventory catalog items, and promotional assets, maintaining predictable iteration order.
8. **File Handling (Java I/O) & Persistence**:
   - Human-readable pipe-delimited text files in `data/` using `BufferedReader`, `PrintWriter`, `FileReader`, and `FileWriter`.
   - Fault-tolerant parsing skips corrupted lines gracefully with logged warnings rather than terminating the application or silently wiping existing memory state.
   - Persistence verified across cold restarts without duplicate data seeding.

---

## 3. Architecture & Class Metrics

| Metric | Count | Details |
|---|---|---|
| **Concrete Classes** | **30** | `Main`, 4 Exception subclasses, `Actor`, `CrewMember`, `Scene`, `Expense`, `Review`, `ProductionMilestone`, `DubbingTrack`, `PostProductionTask`, `CallSheet`, `CastCallEntry`, `DistributionRelease`, `InventoryItem`, `ItemAssignment`, `PromotionalAsset`, `Movie`, `ProductionHouse`, `CastManager`, `ReviewManager`, `InventoryManager`, `PromotionManager`, `ReportService`, `FileService`, `MovieManager`, `ConsoleMenu` |
| **Abstract Classes** | **1** | `Person` |
| **Java Enums** | **6** | `MovieStatus`, `ProductionStage`, `PostProductionType`, `ItemCategory`, `AssetType`, `ApprovalStatus` |
| **Java Interfaces** | **0** | Clean, student-friendly direct service design |
| **Total Java Source Files** | **37** | Verified via source compilation |
