# CineFlow: UML Class Diagram & Architecture
**Project**: CineFlow - Cinema Production Management System  
**Level**: 2nd-Year College Object-Oriented Programming (Java) Mini-Project  

---

## 1. Visual Mermaid Class Diagram

```mermaid
classDiagram
    direction TB

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

    %% Lifecycle Enum
    class MovieStatus {
        <<enumeration>>
        PLANNING
        PRODUCTION
        POST_PRODUCTION
        RELEASED
    }

    %% Review Entity
    class Review {
        -String reviewerName
        -int rating
        -String comment
        +Review(reviewerName, rating, comment)
        +getReviewerName() String
        +getRating() int
        +getComment() String
        +toString() String
    }

    %% Core Domain Entities
    class Scene {
        -int sceneNumber
        -String title
        -String location
        -String status
        +Scene(sceneNumber, title, location, status)
        +getSceneNumber() int
        +setSceneNumber(number) void
        +getTitle() String
        +setTitle(title) void
        +getLocation() String
        +setLocation(loc) void
        +getStatus() String
        +setStatus(status) void
        +toString() String
    }

    class Expense {
        -String id
        -String category
        -double amount
        -String description
        +Expense(id, category, amount, description)
        +getId() String
        +getCategory() String
        +getAmount() double
        +getDescription() String
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
        -List~Scene~ scenes
        -List~Person~ castAndCrew
        -List~Expense~ expenses
        -List~Review~ reviews
        +Movie(id, title, genre, director, year, budget, status)
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
        +addPerson(Person) void
        +addExpense(Expense) void
        +addReview(Review) boolean
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
        +toString() String
    }

    %% Custom Exception
    class BudgetExceededException {
        +BudgetExceededException(message: String)
    }

    %% Service & File Handling
    class FileService {
        -String dataDir
        +FileService()
        +FileService(dataDir: String)
        +saveAllData(studios: List~ProductionHouse~) void
        +loadAllData() List~ProductionHouse~
        +hasExistingData() boolean
    }

    class MovieManager {
        -Map~String, ProductionHouse~ studios
        -FileService fileService
        -String activeStudioId
        -String activeMovieId
        +MovieManager()
        +addStudio(ProductionHouse) void
        +getStudio(id: String) ProductionHouse
        +getAllStudios() List~ProductionHouse~
        +getAllMovies() List~Movie~
        +getActiveStudio() ProductionHouse
        +setActiveStudioId(id: String) boolean
        +getActiveMovie() Movie
        +setActiveMovieId(id: String) boolean
        +addMovieToActiveStudio(Movie) void
        +searchMovies(query, genre, minRating) List~Movie~
        +saveData() void
        +loadData() void
        +seedInitialData() void
    }

    %% Presentation / CLI
    class ConsoleMenu {
        -MovieManager manager
        -Scanner scanner
        +ConsoleMenu(manager: MovieManager)
        +start() void
    }

    class Main {
        +main(args: String[]) void
    }

    %% Relationships
    Movie --> MovieStatus : Uses
    Movie *-- Review : Aggregation (1 to Many)
    ProductionHouse *-- Movie : Aggregation (1 to Many)
    Movie *-- Scene : Composition (1 to Many)
    Movie *-- Expense : Composition (1 to Many)
    Movie o-- Person : Polymorphic Association (1 to Many)
    Movie ..> BudgetExceededException : Throws on budget overrun

    MovieManager o-- ProductionHouse : Manages via HashMap
    MovieManager o-- FileService : Uses for Text File I/O
    ConsoleMenu o-- MovieManager : Delegates business operations
    ConsoleMenu ..> BudgetExceededException : Handles exception
    Main ..> MovieManager : Initializes
    Main ..> ConsoleMenu : Launches
```

---

## 2. Summary of Key OOP Concepts Demonstrated

1. **Classes and Objects**: Separation of domain entities (`Movie`, `ProductionHouse`, `Scene`, `Expense`, `Review`).
2. **Encapsulation**: Private fields accessed through getters/setters with validation logic (`addReview` checks `RELEASED` status and rating 1-5).
3. **Inheritance & Abstraction**: Base abstract class `Person` extended by `Actor` and `CrewMember`.
4. **Polymorphism & Method Overriding**: `Actor` and `CrewMember` override `getRole()`. In `Movie`, `List<Person>` stores both actors and crew polymorphically.
5. **Enumerations**: `MovieStatus` encapsulates lifecycle phases (`PLANNING`, `PRODUCTION`, `POST_PRODUCTION`, `RELEASED`).
6. **Collections Framework**:
   - `ArrayList` for movies, scenes, personnel, expenses, reviews.
   - `HashMap` in `MovieManager` for $O(1)$ key-value studio lookups by ID.
7. **Custom Exception Handling**: `BudgetExceededException` checked exception enforces budget ceilings.
8. **File Handling (I/O Streams)**: Human-readable text files in `data/` using `BufferedReader` and `PrintWriter` with pipe delimiters.
