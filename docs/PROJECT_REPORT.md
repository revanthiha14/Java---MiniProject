# CineFlow: Cinema Production Management System
## 2nd-Year College Object-Oriented Programming (Java) Mini-Project Report

**Course**: Object-Oriented Programming (Java)  
**Academic Year**: 2026  

---

## 1. Project Overview & Problem Statement
In the entertainment industry, film studios must oversee multiple movies progressing through different phases of production. Coordinating cast members, crew teams, scene schedules, and movie budgets using unstructured spreadsheets or paper records often leads to financial overruns and organizational errors.

**CineFlow** is a console-based Java Cinema Production Management System created to manage:
1. **Production Houses / Studios**: Storing studio identity, headquarters, and catalog of movies.
2. **Movie Management**: Tracking movies, directors, genres, release years, budgets, and lifecycle states (`PLANNING`, `PRODUCTION`, `POST_PRODUCTION`, `RELEASED`).
3. **Cast and Crew Roster Management (`CastManager`)**: Adding, searching, filtering, and removing actors and technical crew members under an object-oriented inheritance hierarchy.
4. **Scene Management & Conflict Detection**: Recording scenes, locations, shooting dates, and preventing double-booking filming locations via custom schedule conflict checking.
5. **Budget & Expense Tracking**: Recording expenditures and preventing budget overruns via custom exception handling.
6. **Audience & Critic Reviews (`ReviewManager`)**: Enforcing domain rules (reviews allowed only after release, rating between 1 and 5 stars, average rating calculations, star distribution breakdown, and rating-based filtering).
7. **Text File Persistence**: Saving and loading all project data to and from human-readable text files (`.txt`) with resilient parsing.

---

## 2. Key Object-Oriented Programming (OOP) Concepts Demonstrated

### 2.1 Classes and Objects
Every entity in the film domain is modeled as a distinct class:
- `ProductionHouse`
- `Movie`
- `Scene`
- `Expense`
- `Review`
- `Person`, `Actor`, `CrewMember`
- `CastManager`, `ReviewManager`, `MovieManager`, `FileService`

### 2.2 Encapsulation & Single Responsibility Principle (SRP)
All classes protect their internal state with `private` member variables and provide public getters and setters.
- `CastManager` encapsulates roster management (adding, removing, searching, and filtering actors vs. crew), ensuring that `Movie` remains a focused domain model.
- `ReviewManager` encapsulates audience feedback rules, star rating calculations, and distribution statistics without duplicating review storage.
- Domain validation methods ensure that changes to state remain valid (e.g., verifying budget constraints before adding expenses, ensuring ratings are strictly within 1 to 5, and requiring non-empty names and descriptions).

### 2.3 Inheritance & Abstraction
- **Abstract Class**: `Person` serves as an abstract base class containing shared fields (`id`, `name`, `email`) and an abstract method `public abstract String getRole()`.
- **Subclasses**:
  - `Actor` extends `Person` and adds `characterName`.
  - `CrewMember` extends `Person` and adds `department`.
- Both subclasses use `super(...)` to initialize parent fields and override `getRole()`.

### 2.4 Polymorphism & Method Overriding
- `Actor` and `CrewMember` override `getRole()` and `displayInfo()`.
- In `Movie` and `CastManager`, `Person` references are managed polymorphically. In `CastManager`, dynamic type checking (`instanceof`) and downcasting are used in `getActors()` and `getCrewMembers()` to retrieve specialized lists cleanly.

### 2.5 Collections Framework
- `ArrayList`: Used for dynamic lists of movies, scenes, expenses, cast/crew personnel, and reviews.
- `HashMap`: Used in `MovieManager` (`Map<String, ProductionHouse>`) for quick key-value retrieval of studios by ID, and in `ReviewManager.getRatingDistribution()` for star frequency counting.

### 2.6 Custom Checked Exception Hierarchy
The system establishes a clean, checked exception hierarchy rooted in `CineFlowException`:
```
java.lang.Exception
  └── com.cineflow.exception.CineFlowException
        ├── BudgetExceededException
        ├── ResourceNotFoundException
        ├── ScheduleConflictException
        └── ValidationException
```
- **`CineFlowException`**: Base checked exception for all application-specific domain errors.
- **`BudgetExceededException`**: Thrown when an expense amount would exceed the remaining movie budget ceiling.
- **`ResourceNotFoundException`**: Thrown when a requested movie, production house, scene, or personnel member cannot be found by its identifier.
- **`ScheduleConflictException`**: Thrown when scheduling multiple scenes at the same filming location on the same shoot date.
- **`ValidationException`**: Thrown when domain data violates business rules (e.g., empty names, negative budgets/expenses, rating outside 1–5, reviews before release, duplicate IDs).

### 2.7 File Handling (Java I/O)
- Plain text file reading and writing using `BufferedReader`, `PrintWriter`, `FileReader`, and `FileWriter`.
- Pipe-delimited (`|`) format across six text files in `data/`:
  - `studios.txt`: Studio ID, name, and headquarters.
  - `movies.txt`: Movie ID, studio ID, title, genre, director, release year, budget, and lifecycle status.
  - `scenes.txt`: Movie ID, scene number, title, location, status, and shooting date.
  - `cast_crew.txt`: Movie ID, person type, ID, name, email, and role/department.
  - `expenses.txt`: Movie ID, expense ID, category, amount, and description.
  - `reviews.txt`: Movie ID, reviewer name, rating, and comment.
- **Resilient Fault Handling**: Corrupted lines or format errors in data files are skipped gracefully with logged warnings rather than terminating the application or silently overwriting valid in-memory data with empty collections.

---

## 3. Class Structure & Responsibilities

| Class | Package | Description |
|---|---|---|
| `Main` | `com.cineflow` | Application entry point; initializes services and starts the menu loop. |
| `MovieStatus` | `com.cineflow.model` | Enumeration for movie lifecycle: `PLANNING`, `PRODUCTION`, `POST_PRODUCTION`, `RELEASED`. |
| `Review` | `com.cineflow.model` | Models an audience/critic review (`reviewerName`, `rating` from 1-5, `comment`) with validation. |
| `Person` | `com.cineflow.model` | Abstract base class for film personnel (`id`, `name`, `email`, abstract `getRole()`). |
| `Actor` | `com.cineflow.model` | Extends `Person`; stores character role and overrides `getRole()`. |
| `CrewMember` | `com.cineflow.model` | Extends `Person`; stores department (Camera, Sound, VFX, etc.) and overrides `getRole()`. |
| `Scene` | `com.cineflow.model` | Models a movie scene (`sceneNumber`, `title`, `location`, `status`, `shootingDate`). |
| `Expense` | `com.cineflow.model` | Models a production expenditure (`id`, `category`, `amount`, `description`). |
| `Movie` | `com.cineflow.model` | Models a movie; aggregates scenes, expenses, polymorphic cast/crew list, reviews, and status. Enforces budget ceiling, schedule conflicts, and review validation. |
| `ProductionHouse` | `com.cineflow.model` | Models a film studio; contains studio details and list of movies. |
| `CineFlowException` | `com.cineflow.exception` | Base checked exception for all application errors; extends `Exception`. |
| `BudgetExceededException` | `com.cineflow.exception` | Custom checked exception thrown when spending exceeds total budget; extends `CineFlowException`. |
| `ResourceNotFoundException` | `com.cineflow.exception` | Custom checked exception thrown when a studio, movie, scene, or person is missing; extends `CineFlowException`. |
| `ScheduleConflictException` | `com.cineflow.exception` | Custom checked exception thrown when a filming location is double-booked on the same date; extends `CineFlowException`. |
| `ValidationException` | `com.cineflow.exception` | Custom checked exception thrown on domain validation failure; extends `CineFlowException`. |
| `CastManager` | `com.cineflow.service` | Specializes in personnel roster operations: adding, searching, downcasting actors vs crew, removing by ID, and duplicate prevention. |
| `ReviewManager` | `com.cineflow.service` | Specializes in audience feedback: coordinates reviews, validates release preconditions, calculates averages, star distributions, and filters. |
| `FileService` | `com.cineflow.service` | Reads and writes application records to pipe-delimited text files in `data/` with resilient error skipping. |
| `MovieManager` | `com.cineflow.service` | Central business manager maintaining studio map (`HashMap`), active studio/movie selection, multi-studio search, and data coordination. |
| `ConsoleMenu` | `com.cineflow.ui` | Interactive console menu using `Scanner` for user choices, lifecycle updates, statistics, search, exception catch blocks, and formatted outputs. |

### Architectural Metrics
- **Actual Java Classes (excluding enums)**: **19**
- **Java Enums**: **1** (`MovieStatus`)
- **Java Interfaces**: **0**
- **Total Java Source Files**: **20**

---

## 4. How to Compile and Run

### Using the Batch Script (Windows)
Double-click or execute in terminal:
```bat
run.bat
```

### Manual Compilation & Execution
```bash
# Compile
javac -encoding UTF-8 -d bin src/com/cineflow/exception/*.java src/com/cineflow/model/*.java src/com/cineflow/service/*.java src/com/cineflow/ui/*.java src/com/cineflow/Main.java

# Run
java -Dfile.encoding=UTF-8 -cp bin com.cineflow.Main
```

---

## 5. Verification and Test Results

A full test suite of 18 verification tests was executed against the codebase:

1. **Exception Hierarchy**: Confirmed all 4 custom exceptions inherit from `CineFlowException` and `Exception`.
2. **Movie Creation & Search**: Successfully created movies and verified multi-criteria search (keyword, genre, rating).
3. **Production House Management**: Verified studio creation and switching active studio.
4. **Scene Management & Scheduling**: Verified adding scenes and rescheduling shoot dates.
5. **Schedule Conflict Detection**: Verified that attempting to schedule two scenes at the same location on the same date throws `ScheduleConflictException`.
6. **Cast & Crew Hierarchy**: Verified polymorphism across `Actor` and `CrewMember` within `List<Person>`.
7. **Valid Expense Addition**: Verified expense calculation and remaining budget updates.
8. **Budget Exceeded Exception**: Confirmed that expenditures exceeding total budget trigger `BudgetExceededException`.
9. **Resource Not Found Exception**: Confirmed searching/switching nonexistent studio or movie IDs throws `ResourceNotFoundException`.
10. **Validation on Invalid Data**: Confirmed negative budgets, negative scene numbers, and empty fields throw `ValidationException`.
11. **Review Release Blocking**: Confirmed reviews submitted for movies in `PLANNING`, `PRODUCTION`, or `POST_PRODUCTION` are rejected with `ValidationException`.
12. **Rating Bounds Validation**: Confirmed star ratings below 1 or above 5 are rejected with `ValidationException`.
13. **Average Rating Calculation**: Verified arithmetic average calculations across multiple reviews.
14. **Rating-Based Filtering & Statistics**: Verified filtering movies with minimum average ratings.
15. **Data Persistence**: Verified saving and reloading all entities across restarts without data loss.
16. **Malformed File Handling**: Verified that corrupted lines in text files are skipped with warnings rather than crashing the system.
17. **CastManager Roster Operations**: Verified adding, searching (by name/ID), separate actor/crew retrieval, duplicate check, and removing members by ID.
18. **ReviewManager Distribution & Rules**: Verified release status enforcement, star rating distribution computation (1 to 5 stars), and rating filtering.

**Test Summary**: All 18 verification tests passed (100% pass rate).

---

## 6. Viva Preparation Q&A

**Q1: Why did you create `CastManager` and `ReviewManager` instead of keeping all code in `Movie`?**  
*Answer*: In our earlier design, `Movie` was acting as an oversized class handling movie properties, personnel rosters, audience reviews, and financial limits. This violated the **Single Responsibility Principle (SRP)**. By extracting `CastManager` and `ReviewManager`, `Movie` focuses purely on representing a movie entity, while `CastManager` handles personnel roster operations (search, filter, remove, duplicate checks) and `ReviewManager` handles feedback analytics (averages, distributions, release validation). Neither manager duplicates data—both operate directly on the movie's collections.

**Q2: How does `CastManager` demonstrate polymorphism and downcasting?**  
*Answer*: `CastManager` accepts the base class `Person` polymorphically when adding or searching members. When specific lists of actors or technical crew are requested, `getActors()` and `getCrewMembers()` use `instanceof` to inspect the concrete type at runtime and safely downcast to `Actor` or `CrewMember`.

**Q3: How does `ReviewManager` calculate the rating distribution?**  
*Answer*: `ReviewManager.getRatingDistribution()` uses a `Map<Integer, Integer>` (implemented via `HashMap`) to count the frequency of each star rating from 1 to 5 stars. This map is then displayed on the console as a breakdown for critics and studio producers.

**Q4: How does CineFlow detect and handle shooting schedule conflicts?**  
*Answer*: Each `Scene` has a filming location and an optional shooting date. In `Movie.scheduleScene()` and `Movie.addScene()`, before assigning a date, the method checks all other scenes in the movie. If another scene is already scheduled at the same location on the same date, a `ScheduleConflictException` is thrown, preventing double-booking of filming sets.

**Q5: Why did you make `Person` an abstract class?**  
*Answer*: In a movie production, everyone has a specific role (either an actor playing a character or a crew member working in a department). Making `Person` abstract prevents creating generic person instances and forces subclasses to implement `getRole()`.

**Q6: What happens if a file has corrupted or missing lines?**  
*Answer*: `FileService` uses line-by-line reading inside try-catch blocks. If a record contains fewer fields than expected or contains invalid numbers, the exception is caught, a descriptive warning is output, and processing continues for the remaining valid records without crashing. Furthermore, if a file read fails, the system preserves existing in-memory data rather than clearing it.

---

## 7. Limitations & Future Work
1. **Concurrency**: The current version uses single-threaded console input; concurrent multi-user editing would require thread synchronization.
2. **Persistence Storage**: Text files with pipe delimiters are lightweight and human-readable, but a relational database (e.g. SQLite or PostgreSQL via JDBC) would offer transactional ACID guarantees.
3. **Advanced Scheduling**: Currently, schedule conflicts check location-date overlaps within a movie; cross-studio actor availability scheduling could be an extension.
