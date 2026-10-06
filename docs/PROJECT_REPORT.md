# CineFlow: Cinema Production Management System
## 2nd-Year College Object-Oriented Programming (Java) Mini-Project Report

**Course**: Object-Oriented Programming (Java)  
**Academic Year**: 2026  

---

## 1. Project Overview & Problem Statement
In the entertainment industry, film studios must oversee multiple movies progressing through different phases of production. Coordinating cast members, crew teams, scene schedules, and movie budgets using unstructured spreadsheets or paper records often leads to financial overruns and organizational errors.

**CineFlow** is a console-based Java Cinema Production Management System created to manage:
1. **Production Houses / Studios**: Storing studio identity, headquarters, and catalog of movies.
2. **Movie Management**: Tracking movies, directors, genres, release years, and budgets.
3. **Cast and Crew Roster**: Managing actors and technical crew members under an object-oriented inheritance hierarchy.
4. **Scene Management**: Recording scene numbers, locations, and progress status.
5. **Budget & Expense Tracking**: Recording expenditures and preventing budget overruns via custom exception handling.
6. **Text File Persistence**: Saving and loading all project data to and from human-readable text files (`.txt`).

---

## 2. Key Object-Oriented Programming (OOP) Concepts Demonstrated

### 2.1 Classes and Objects
Every entity in the film domain is modeled as a distinct class:
- `ProductionHouse`
- `Movie`
- `Scene`
- `Expense`
- `Person`, `Actor`, `CrewMember`

### 2.2 Encapsulation
All classes protect their internal state with `private` member variables and provide public getters and setters. Business validation methods ensure that changes to state remain valid (e.g., verifying budget constraints before adding expenses).

### 2.3 Inheritance & Abstraction
- **Abstract Class**: `Person` serves as an abstract base class containing shared fields (`id`, `name`, `email`) and an abstract method `public abstract String getRole()`.
- **Subclasses**:
  - `Actor` extends `Person` and adds `characterName`.
  - `CrewMember` extends `Person` and adds `department`.
- Both subclasses use `super(...)` to initialize parent fields and override `getRole()`.

### 2.4 Polymorphism & Method Overriding
- `Actor` and `CrewMember` override `getRole()` and `displayInfo()`.
- Inside `Movie`, a single polymorphic collection `List<Person> castAndCrew` stores both actors and crew members. When displaying details, the runtime environment dynamically dispatches the correct subclass method.

### 2.5 Collections Framework
- `ArrayList`: Used for dynamic lists of movies, scenes, expenses, and personnel.
- `HashMap`: Used in `MovieManager` (`Map<String, ProductionHouse>`) for quick key-value retrieval of studios by ID.

### 2.6 Exception Handling
- **Custom Checked Exception**: `BudgetExceededException` is thrown when an expense exceeds the movie's available budget.
- **Checked Exceptions**: `IOException` is handled during file input/output operations.
- **Unchecked Exceptions**: `NumberFormatException` is handled during console menu input parsing to ensure robust execution without crashes.

### 2.7 File Handling (Java I/O)
- Plain text file reading and writing using `BufferedReader`, `PrintWriter`, `FileReader`, and `FileWriter`.
- Pipe-delimited (`|`) format across five text files in `data/`:
  - `studios.txt`
  - `movies.txt`
  - `scenes.txt`
  - `cast_crew.txt`
  - `expenses.txt`

---

## 3. Class Structure & Responsibilities

| Class | Package | Description |
|---|---|---|
| `Main` | `com.cineflow` | Application entry point; initializes services and starts the menu loop. |
| `MovieStatus` | `com.cineflow.model` | Enumeration for movie lifecycle: PLANNING, PRODUCTION, POST_PRODUCTION, RELEASED. |
| `Review` | `com.cineflow.model` | Models an audience/critic review (`reviewerName`, `rating` from 1-5, `comment`). |
| `Person` | `com.cineflow.model` | Abstract base class for film personnel (`id`, `name`, `email`, abstract `getRole()`). |
| `Actor` | `com.cineflow.model` | Extends `Person`; stores character role and overrides `getRole()`. |
| `CrewMember` | `com.cineflow.model` | Extends `Person`; stores department (Camera, Sound, VFX, etc.) and overrides `getRole()`. |
| `Scene` | `com.cineflow.model` | Models a movie scene (`sceneNumber`, `title`, `location`, `status`). |
| `Expense` | `com.cineflow.model` | Models a production expenditure (`id`, `category`, `amount`, `description`). |
| `Movie` | `com.cineflow.model` | Models a movie; aggregates scenes, expenses, polymorphic cast/crew list, reviews, and status. Enforces budget ceiling and review validation. |
| `ProductionHouse` | `com.cineflow.model` | Models a film studio; contains studio details and list of movies. |
| `BudgetExceededException` | `com.cineflow.exception` | Custom checked exception thrown when spending exceeds total budget. |
| `FileService` | `com.cineflow.service` | Reads and writes application records to pipe-delimited text files in `data/` including `reviews.txt`. |
| `MovieManager` | `com.cineflow.service` | Central business manager maintaining studio map (`HashMap`), active studio/movie selection, multi-studio search, and data coordination. |
| `ConsoleMenu` | `com.cineflow.ui` | Interactive console menu using `Scanner` for user choices, lifecycle updates, statistics, search, and formatted outputs. |

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
java -cp bin com.cineflow.Main
```

---

## 5. Viva Preparation Q&A

**Q1: Why did you make `Person` an abstract class?**  
*Answer*: In a movie production, everyone has a specific role (either an actor playing a character or a crew member working in a department). Making `Person` abstract prevents creating generic person instances and forces subclasses to implement `getRole()`.

**Q2: How is polymorphism demonstrated?**  
*Answer*: The `Movie` class contains `List<Person> castAndCrew`. This single list holds both `Actor` and `CrewMember` objects. When iterating over the list and calling `p.getRole()`, Java's dynamic method dispatch calls the overridden method corresponding to the actual object type at runtime.

**Q3: How does your program handle budget overflow?**  
*Answer*: When an expense is added in `Movie.addExpense()`, the method computes `currentExpenses + newAmount`. If this exceeds `budget`, it throws a custom checked exception `BudgetExceededException`. The `ConsoleMenu` catches this exception and alerts the user without adding the expense.

**Q4: How does file handling work?**  
*Answer*: `FileService` uses `PrintWriter` to write records line-by-line with pipe (`|`) delimiters into text files (`studios.txt`, `movies.txt`, etc.). It loads them using `BufferedReader` and reconstructs the domain objects by splitting each line with `split("\\|")`.
