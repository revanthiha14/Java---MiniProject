# CineFlow 2.0: Cinema Production Management & Studio Ecosystem
## Academic Mini Project Report

**Institution**: Sri Sivasubramaniya Nadar College of Engineering, Kalavakkam – 603 110  
*(An Autonomous Institution, Affiliated to Anna University, Chennai)*  
**Department**: Computer Science and Engineering  
**Course**: Object-Oriented Programming (Java)  
**Academic Year**: 2026  

---

## 1. Problem Statement
Real-world filmmaking enterprises—such as Horizon Studios, Warner Bros, Paramount Pictures, A24, and Mythri Movie Makers—do not operate on a single isolated movie in a vacuum. Instead, a modern film studio ecosystem manages **multiple production houses**, each stewarding an active slate of **multiple movie projects** progressing concurrently across distinct production stages:
1. **Pre-Production**: Script breakdown, screenplay dialogue analysis, casting lead/supporting actors, location scouting, storyboard consultations.
2. **In-Production / Principal Photography**: Active shooting, priority daylight scheduling, screenplay rehearsal, daily production Call Sheets, and equipment dispatch.
3. **Post-Production**: Film editing, orchestral score recording, foley audio, CGI/VFX rendering, and color grading suites.
4. **Released**: Complete theatrical distribution and streaming rollout.

Manual coordination across spreadsheets and closed binary files fails when:
1. **Multi-Studio Ecosystem Chaos**: Studios cannot manage multiple production houses, cross-studio catalogs, or separate studio headquarters and slates.
2. **Opaque Data Storage**: Traditional databases hide data in complex binary formats or external database servers. Directors, writers, and evaluators need **human-readable text files** (`.txt`) they can inspect, edit in Notepad anytime, and live-reload immediately.
3. **Fragmented Script & Screenplay Breakdowns**: Screenplays require scene-by-scene action and dialogue integration alongside technical parameters (pages, daylight windows, camera packages, cast).
4. **Talent & Location Contention**: Overlapping actors or camera packages scheduled concurrently without conflict detection.
5. **Budget Leakage**: Film productions require dedicated, department-level financial ledgers isolated per movie.

**Objective**: To design and build **CineFlow 2.0**—a comprehensive, modular, terminal-based Java application simulating a complete Cinema Production Management & Studio Ecosystem. It manages multiple production houses, multiple feature films per studio across all production phases, full scene screenplay dialogues, and a transparent **text-file persistence engine** (`data/*.txt` and `data/scripts/*.txt`) allowing bidirectional live synchronization with external text editors.

---

## 2. Motivation for the Problem
In feature filmmaking, every wasted hour on set costs thousands of dollars. Developing a multi-studio production management system provides an authentic software engineering simulation of studio operations while exercising every core concept of the Java Object-Oriented Programming rubric:
- **Registry Pattern & Multi-Studio State**: `StudioService` manages a central registry of `ProductionHouse` entities via `LinkedHashMap`.
- **Aggregation & Multi-Project Slate**: Each `ProductionHouse` aggregates a catalog of `Movie` objects.
- **Composition & Encapsulation**: Each `Movie` maintains its dedicated `BudgetService`, scenes list with full screenplay text, and priority shooting queue.
- **Human-Readable Text-File Persistence**: Native Java IO Streams (`BufferedReader`, `BufferedWriter`, `PrintWriter`, `FileReader`, `FileWriter`) reading and writing structured text databases with live-reload support.
- **Dynamic Polymorphism**: Diverse remuneration models across Actors, Union Crew Members, and Directors.
- **Collections Framework**: PriorityQueue for shooting urgency, Lists for scenes, Sets for unique cast/equipment, Maps for departmental ledgers.
- **Custom Exception Handling**: Enforcing budget ceilings and location/talent schedule overlaps.

---

## 3. Scope and Features

### Scope
- **Multi-Studio Management**: Management of 5+ pre-seeded global production houses (Horizon Studios, Warner Bros, Paramount, A24, Mythri Movie Makers) with full capabilities to add new studios and switch active studios dynamically.
- **Multi-Movie Slate per Studio**: Management of 11+ feature films across Pre-Production, In-Production, Post-Production, and Released stages, with instant runtime project switching.
- **Screenplay & Script Breakdown**: Scene breakdowns tracking scene number, title, page count, daylight requirements (Day Exterior, Night Exterior, Golden Hour, Interior Studio), urgency priority, and full screenplay action and dialogue text.
- **Human-Readable Text-File Database**: All studios (`data/studios.txt`), movies (`data/movies.txt`), scenes and screenplay scripts (`data/scenes_script.txt`), and budgets (`data/budgets.txt`) are stored as clean, readable text files editable in Notepad or any text editor.
- **Bidirectional Live Reload**: Modify any script dialogue, scene, or movie title in Notepad, hit "Live Reload", and the running Java application immediately updates without restart.
- **Screenplay Document Export**: Automatically exports full, formatted screenplay script breakdown documents to `data/scripts/<MovieId>_<Title>_Screenplay.txt`.
- **Urgency-Driven Priority Queue**: Automatic queueing via `PriorityQueue<Scene>` prioritizing weather-dependent and golden-hour scenes.
- **Central Talent & Crew Roster**: Central studio database of directors, lead/supporting actors, and technical crew with specialized certifications and union rules.
- **Central Equipment & Location Pool**: Studio inventory of cinema camera packages, anamorphic lenses, sound rigs, and soundstages/exterior locations.
- **Schedule Conflict Engine**: Real-time detection of overlapping talent or location reservations.
- **Daily Shooting Plan (Call Sheet)**: Generates and exports official production call sheets with call times, emergency contacts, and stage notes to disk files.
- **Dedicated Project Budgets**: Departmental financial ledgers and variance audits isolated per movie.

---

## 4. Explore Design Alternatives (CO3 - K6)

| Design Dimension | Selected Design in CineFlow 2.0 | Alternative Considered | Rationale for Selection |
| :--- | :--- | :--- | :--- |
| **Studio Architecture** | Two-Tier Ecosystem: `StudioService` manages `Map<String, ProductionHouse>`, each aggregating `Map<String, Movie>`. | Single-studio flat catalog or global arrays. | Real filmmaking spans multiple major studios and independent houses. Hierarchical management ensures project financial isolation, multi-studio switching, and independent slates. |
| **Persistence Engine** | Human-readable delimited and tagged text files (`data/*.txt`) with native Java I/O streams. | Binary object serialization (`.ser`) or external SQL database. | Binary files cannot be inspected or edited by directors or evaluators in Notepad. External databases require heavy installation and drivers. Human-readable text files allow instant editing in text editors, live reloading, and zero external setup. |
| **Script Integration** | Embedded screenplay action & dialogue blocks per scene with standalone script export. | Plain title and synopsis strings without dialogue. | Screenwriters and directors work directly with dialogue and action. Rich screenplay text makes script breakdown authentic and practical. |
| **Terminal UI Presentation** | Clean, box-formatted studio headers (`ConsoleUI`) with UTF-8 support and simple English terms. | Bulky multi-line ASCII banner graphics. | Bulky ASCII art distorts on varying terminal dimensions and triggers encoding errors (`?`) on Windows CMD. Simple, box-formatted headers ensure 100% readability across all platforms. |
| **Scheduling Engine** | Priority Queue (`PriorityQueue<Scene>`) utilizing `Comparable<Scene>`. | Plain List (`ArrayList<Scene>`) with manual sorting. | Guarantees $O(\log n)$ priority insertion and $O(1)$ lookup for the most urgent sequence (e.g., golden-hour sun window). |
| **Talent Payroll** | Dynamic runtime polymorphism (`Person.calculateRemuneration()`). | Monolithic switch-case on role strings. | Open/Closed Principle (OCP): new talent categories can be added without modifying payroll auditing routines. |

---

## 5. Modules Split-up

The codebase is organized into 39 Java source files across 8 decoupled packages adhering to the Single Responsibility Principle:

```text
com.cineflow/
├── Main.java                          # Entrypoint, Studio DI coordinator & text file auto-loader
├── model/
│   ├── ProductionHouse.java           # Studio entity aggregating multiple movies (LinkedHashMap)
│   ├── Movie.java                     # Movie project entity (Scenes, Budget, Queue, StudioId)
│   ├── Identifiable.java              # Generic interface <ID>
│   ├── Reportable.java                # Text report generation contract
│   ├── CostTrackable.java             # Expense tracking contract
│   ├── Schedulable.java               # Conflict-check contract
│   ├── Department.java                # Enum of film divisions (9 departments)
│   ├── ProductionPhase.java           # Enum of film stages (Pre, In, Post, Released)
│   ├── SceneStatus.java               # Enum of scene statuses (Draft, Scheduled, Completed)
│   ├── DaylightRequirement.java       # Enum of lighting conditions
│   ├── Scene.java                     # Scene domain entity with screenplay dialogue (Comparable)
│   ├── CallSheet.java                 # Daily Call Sheet / Shooting Plan entity
│   ├── personnel/
│   │   ├── Person.java                # Abstract base person (this() chaining, overloaded remuneration)
│   │   ├── Actor.java                 # Extends Person (Set<String> skills, billing)
│   │   ├── CrewMember.java            # Extends Person (String[] certs, union)
│   │   └── Director.java              # Extends Person (Creative vision, profit points)
│   └── asset/
│       ├── ProductionAsset.java       # Abstract asset (Identifiable, CostTrackable)
│       ├── Equipment.java             # Extends ProductionAsset (insurance fees)
│       └── Location.java              # Extends ProductionAsset (municipal permit fees)
├── repository/
│   ├── Repository.java                # Generic interface <T, ID>
│   └── FileRepository.java            # Generic in-memory repository implementation
├── service/
│   ├── StudioService.java             # Central registry for multiple Production Houses
│   ├── FilePersistenceService.java    # Text-file I/O engine (studios, movies, scenes, scripts, budgets)
│   ├── CostEstimator.java             # Custom @FunctionalInterface
│   ├── ConflictValidator.java         # Custom @FunctionalInterface
│   ├── ProductionService.java         # Scene management & PriorityQueue
│   ├── PersonnelService.java          # Cast & Crew payroll & registry
│   ├── AssetService.java              # Equipment & location bookings
│   └── BudgetService.java             # Department budgets & transactions
├── exception/
│   ├── CineFlowException.java         # Base checked exception
│   ├── ResourceNotFoundException.java # Subclass
│   ├── ScheduleConflictException.java # Subclass
│   ├── BudgetExceededException.java   # Subclass
│   └── ValidationException.java       # Subclass
├── util/
│   ├── ConsoleUI.java                 # Clean box header, multiline input prompts, validators
│   ├── DataGenerator.java             # Pre-seeds 5 studios, 11 movies, scripts, and budgets
│   └── CollectionUtils.java           # Generic collection & stream utilities
└── ui/
    └── MenuController.java            # 15-Option interactive terminal menu & test runner
```

---

## 6. Object-Oriented Features Used (Concept-to-Code Mapping)

| Object-Oriented Concept | Specific Implementation in CineFlow 2.0 |
| :--- | :--- |
| **Classes & Objects** | `StudioService`, `ProductionHouse`, `Movie`, `Scene`, `CallSheet`, `ExpenseRecord`, `Equipment`, `Location`. |
| **Interfaces** | `Identifiable<ID>`, `Reportable`, `CostTrackable`, `Schedulable`, `Repository<T, ID>`. |
| **Abstract Classes** | `Person` (base for personnel), `ProductionAsset` (base for physical assets). |
| **Inheritance Hierarchies** | `Person` $\rightarrow$ `Actor`, `CrewMember`, `Director`; `ProductionAsset` $\rightarrow$ `Equipment`, `Location`. |
| **Constructors & Chaining** | Default and overloaded constructors using `this(...)` and `super(...)` across `Movie`, `ProductionHouse`, `Person`, `CrewMember`, and `Director`. |
| **Arrays & Strings** | `String[] certifications` in `CrewMember`, `String[] productionChecklist` in `Scene`; array resizing with `Arrays.copyOf()`; string formatting and multiline screenplay text blocks. |
| **IO Streams** | Native Java IO: `PrintWriter`, `BufferedWriter`, `FileReader`, `BufferedReader` for human-readable text databases (`studios.txt`, `movies.txt`, `scenes_script.txt`, `budgets.txt`, and formatted screenplays in `data/scripts/`). |
| **Exception Handling** | Custom checked hierarchy: `CineFlowException` base, subclasses `ResourceNotFoundException`, `ScheduleConflictException`, `BudgetExceededException`, `ValidationException`. |
| **Packages** | 8 distinct modular packages (`model`, `personnel`, `asset`, `repository`, `service`, `exception`, `util`, `ui`). |
| **Polymorphism** | Dynamic dispatch on `person.calculateRemuneration(days)` and `asset.computeTotalCost()`. Compile-time method overloading on services and entities. |
| **Method Overloading** | Overloaded `calculateRemuneration()`, `addScene()`, `addScenes()`, `assignActor()`, `assignCrew()`, `bookEquipment()`, `allocateBudget()`, `logExpense()`, `scheduleScene()`. |
| **Method Overriding** | `@Override` of `calculateRemuneration()`, `computeTotalCost()`, `generateDetailedReport()`, `compareTo()`, `toString()`, `equals()`, `hashCode()`. |
| **Generic Types** | Generic interface `Repository<T extends Identifiable<ID>, ID>`, generic class `FileRepository<T, ID>`, generic utilities `CollectionUtils.<T, R>extractUnique()`. |
| **Collections - List** | `ArrayList<Scene>`, `ArrayList<ExpenseRecord>`, `List<Movie>`, `List<Person>`, `List<ProductionHouse>`. |
| **Collections - Set** | `HashSet<String>` for unique scene assignments; `TreeSet<String>` for sorted actor skills; `TreeSet<LocalDate>` for scheduled dates. |
| **Collections - Queue** | `PriorityQueue<Scene>` prioritized by urgency level, daylight requirements, and scene numbers. |
| **Collections - Map** | `LinkedHashMap<String, ProductionHouse>` (studio registry), `LinkedHashMap<String, Movie>` (studio movie catalog), `TreeMap<Department, Double>` (sorted department ledger), `HashMap<Department, Double>` (spent budget). |
| **Functional Interfaces & Lambdas** | Custom `@FunctionalInterface CostEstimator`, `@FunctionalInterface ConflictValidator`; Java built-ins `Predicate<T>`, `Comparator<T>`. Stream pipelines (`.filter()`, `.map()`, `.sorted()`, `.collect()`, `.reduce()`). |

---

## 7. Sample Data & Pre-Seeded Catalog

CineFlow 2.0 comes pre-seeded with 5 realistic film production houses and 11 feature films across different stages:

1. **Horizon Studios (`PH-101`)** (Los Angeles & Mumbai | Est. 2005):
   - *Interstellar Journey* (`MOV-01`, Sci-Fi, In-Production / Shooting) - 6 scenes with full sci-fi scripts, $2.95M budget.
   - *Shadows of the Past* (`MOV-02`, Psychological Thriller, Pre-Production) - 4 scenes, $1.05M budget.
   - *The Royal Heritage* (`MOV-03`, Historical Drama, Post-Production) - 4 filmed scenes, $3.6M budget.
2. **Warner Bros. Pictures (`PH-102`)** (Burbank, California | Est. 1923):
   - *The Dark Horizon* (`MOV-04`, Action / Neo-Noir, Pre-Production)
   - *Dune Odyssey* (`MOV-05`, Sci-Fi Epic, In-Production)
3. **Paramount Pictures (`PH-103`)** (Hollywood, California | Est. 1912):
   - *Mission: Retribution* (`MOV-06`, Action / Thriller, In-Production)
   - *Gladiator Chronicles* (`MOV-07`, Historical Action, Post-Production)
4. **A24 Studios (`PH-104`)** (New York City | Est. 2012):
   - *Past Echoes* (`MOV-08`, Indie Drama, Released)
   - *The Lighthouse Secret* (`MOV-09`, Psychological Horror, In-Production)
5. **Mythri Movie Makers (`PH-105`)** (Hyderabad, India | Est. 2015):
   - *Pushpa: The Rule* (`MOV-10`, Action / Drama, In-Production) - Red sanders forest sequence with rustic dialogue.
   - *Devara: Part 1* (`MOV-11`, Action Epic, Post-Production) - Stormy ocean armada sequence.

---

## 8. Verification & Faculty Evaluation Results

The automated test suite demo (accessible directly via **Menu Option [15]**) evaluates all 8 primary OOP rubrics:

```text
============================================================
[Test 1/8] Inheritance & Dynamic Polymorphism Dispatch
[SUCCESS] Polymorphism Passed: Actor=$13000, Crew=$11500, Director=$16500

[Test 2/8] Method Overloading
[SUCCESS] Method Overloading Passed: assignRole() variants resolved correctly.

[Test 3/8] Collections - PriorityQueue Shooting Urgency
[SUCCESS] PriorityQueue Passed: Urgent golden hour scene polled first ahead of priority 3 interior scene.

[Test 4/8] Custom Exception: BudgetExceededException
[SUCCESS] Exception Passed: Caught BudgetExceededException as expected: CRITICAL OVERRUN: Department 'Stunts & Special Actions' attempted to spend $6000.00, but only $5000.00 remains in allocation!

[Test 5/8] Custom Exception: ScheduleConflictException
[SUCCESS] Exception Passed: Caught ScheduleConflictException as expected: Schedule conflict detected for 'Location LOC-501' on 2026-11-11 (08:00 - 12:00): Already reserved for Scene #991

[Test 6/8] Generic Types: Repository<T, ID>
[SUCCESS] Generic Repository Passed: Save, Query, and Delete verified.

[Test 7/8] Functional Interfaces & Lambda Expressions
[SUCCESS] Lambda Passed: Custom CostEstimator evaluated $5600.00

[Test 8/8] Java IO Streams: File Export & Verification
[SUCCESS] IO Streams Passed: File successfully written via PrintWriter and verified via BufferedReader.

============================================================
[SUCCESS] EVALUATION TEST RESULTS: 8 / 8 TEST SUITES PASSED (100%)
============================================================
```

---

## 9. Conclusion
CineFlow 2.0 demonstrates how enterprise-grade software architecture can be designed in pure Java without third-party dependencies. By combining a multi-studio registry (`StudioService`), multi-movie slates (`ProductionHouse`), screenplay-driven scene management (`Scene`), and a human-readable text-file database (`FilePersistenceService`), the system provides an authentic filmmaking workflow while satisfying 100% of the academic evaluation criteria.
