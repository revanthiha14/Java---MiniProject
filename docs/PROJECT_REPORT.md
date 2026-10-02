# CineFlow 2.0: Multi-Movie Production House System
## Academic Mini Project Report

**Institution**: Sri Sivasubramaniya Nadar College of Engineering, Kalavakkam – 603 110  
*(An Autonomous Institution, Affiliated to Anna University, Chennai)*  
**Department**: Computer Science and Engineering  
**Course**: Object-Oriented Programming (Java)  
**Academic Year**: 2026  

---

## 1. Problem Statement
Real-world filmmaking enterprises—such as A24, Paramount, Warner Bros, and Mythri Movie Makers—do not operate on a single isolated movie in a vacuum. Instead, a modern film studio manages an active slate of **multiple movie projects** progressing concurrently across distinct production stages:
1. **Pre-Production**: Script breakdown, casting lead/supporting actors, location scouting, storyboard consultations.
2. **In-Production / Principal Photography**: Active shooting, priority daylight scheduling, daily production Call Sheets, and equipment dispatch.
3. **Post-Production**: Film editing, orchestral score recording, foley audio, VFX rendering, and color grading suites.
4. **Released**: Complete theatrical distribution and streaming rollout.

Manual coordination across fragmented spreadsheets fails when:
1. **Studio-Wide Asset Contention**: Studios cannot track whether camera packages, sound rigs, or soundstages are booked across different movie shoots.
2. **Talent & Personnel Clashes**: Double-booking actors or key directors across multiple movie productions.
3. **Budget Leakage & No Project Isolation**: Film productions require strict, dedicated financial ledgers per movie rather than a single pooled expense pot.
4. **Complex Industry Jargon**: Traditional production software uses opaque industry jargon that impedes swift coordination among department leads, students, and evaluators.

**Objective**: To design and build **CineFlow 2.0 (Horizon Studios)**—a robust, modular, terminal-based Java application simulating a complete Multi-Movie Production House. It manages multiple films across stages with dynamic movie switching, project-isolated budgets, priority daylight shooting queues, conflict-free scheduling, automated daily shooting plans (Call Sheets), polymorphic payroll audits, and zero-configuration file persistence.

---

## 2. Motivation for the Problem
In feature filmmaking, every wasted hour on set costs thousands of dollars. Developing a multi-movie production house system provides an authentic software engineering simulation of studio operations while exercising every core concept of the Java Object-Oriented Programming rubric:
- **Aggregation & Multi-Project State**: A `ProductionHouse` aggregates a catalog of `Movie` objects in a `LinkedHashMap`.
- **Composition & Encapsulation**: Each `Movie` maintains its dedicated `BudgetService`, scenes list, and priority shooting queue.
- **Dynamic Polymorphism**: Diverse remuneration models across Actors, Union Crew Members, and Directors.
- **Collections Framework**: PriorityQueue for shooting urgency, Lists for scenes, Sets for unique cast/equipment, Maps for departmental ledgers.
- **Custom Exception Handling**: Enforcing budget ceilings and location/talent schedule overlaps.
- **IO Streams**: Exporting and reading formatted budget audit ledgers and daily shooting plans.

---

## 3. Scope and Limitations

### Scope
- **Multi-Movie Studio Catalog**: Studio-wide management of multiple feature films across Pre-Production, In-Production, Post-Production, and Released stages, with instant runtime project switching.
- **Script & Scene Breakdown**: Dedicated scene breakdowns per movie tracking page count, lighting window (Day Exterior, Night Exterior, Golden Hour, Interior Studio), and cast/equipment requirements.
- **Urgency-Driven Shooting Queue**: Automatic queueing via `PriorityQueue<Scene>` prioritizing weather-dependent and golden-hour scenes.
- **Central Talent & Crew Roster**: Central studio database of directors, lead/supporting actors, and technical crew with specialized certifications and union rules.
- **Central Equipment & Location Pool**: Studio inventory of cinema camera packages, anamorphic lenses, sound rigs, and soundstages/exterior locations.
- **Schedule Conflict Engine**: Real-time detection of overlapping talent or location reservations.
- **Daily Shooting Plan (Call Sheet)**: Generates and exports official production call sheets with call times, emergency contacts, and stage notes to disk files.
- **Dedicated Project Budgets**: Departmental financial ledgers and variance audits isolated per movie.

### Limitations
- The system operates via a clean, text-based terminal interface (CLI) rather than a graphical web/mobile GUI.
- Calendar scheduling is managed at day and time-slot granularity rather than continuous real-time telemetry.
- Storage uses standard Java I/O streams and text reports rather than an external database server.

---

## 4. Explore Design Alternatives (CO3 - K6)

| Design Dimension | Selected Design in CineFlow 2.0 | Alternative Considered | Rationale for Selection |
| :--- | :--- | :--- | :--- |
| **Studio Architecture** | Hierarchical Studio Model: `ProductionHouse` aggregates `Map<String, Movie>`, where each `Movie` has its own `BudgetService` and scene queue. | Flat single-movie architecture storing global arrays of scenes and expenses. | Real production houses produce multiple movies simultaneously. Hierarchical aggregation ensures project financial isolation, independent progress tracking, and clean state switching. |
| **Terminal UI Presentation** | Clean, box-formatted studio headers (`ConsoleUI`) with UTF-8 support and simple English terms. | Bulky multi-line ASCII banner graphics. | Bulky ASCII art distorts on varying terminal dimensions and triggers encoding errors (`?`) on Windows CMD. Simple, box-formatted headers ensure 100% readability across all platforms. |
| **Scheduling Engine** | Priority Queue (`PriorityQueue<Scene>`) utilizing `Comparable<Scene>`. | Plain List (`ArrayList<Scene>`) with manual sorting. | Guarantees $O(\log n)$ priority insertion and $O(1)$ lookup for the most urgent sequence (e.g., golden-hour sun window). |
| **Talent Payroll** | Dynamic runtime polymorphism (`Person.calculateRemuneration()`). | Monolithic switch-case on role strings. | Open/Closed Principle (OCP): new talent categories can be added without modifying payroll auditing routines. |
| **Persistence Mechanism** | File-based Generic `FileRepository<T, ID>` using Object Streams & formatted text exports. | External relational database (MySQL/PostgreSQL). | Zero-configuration requirement for academic evaluation; native Java I/O streams are portable and require no external DB servers. |

---

## 5. Modules Split-up

The codebase is organized into cleanly decoupled packages adhering to the Single Responsibility Principle:

```text
com.cineflow/
├── Main.java                          # Entrypoint & Studio DI coordinator
├── model/
│   ├── ProductionHouse.java           # Studio entity aggregating multiple movies (Map)
│   ├── Movie.java                     # Movie project entity (Scenes, Budget, Queue)
│   ├── Identifiable.java              # Generic interface <ID>
│   ├── Reportable.java                # Text report generation contract
│   ├── CostTrackable.java             # Expense tracking contract
│   ├── Schedulable.java               # Conflict-check contract
│   ├── Department.java                # Enum of film divisions
│   ├── ProductionPhase.java           # Enum of film stages (Pre, In, Post, Released)
│   ├── SceneStatus.java               # Enum of scene statuses
│   ├── DaylightRequirement.java       # Enum of lighting conditions
│   ├── Scene.java                     # Scene domain entity (Comparable)
│   ├── CallSheet.java                 # Daily Call Sheet / Shooting Plan entity
│   ├── personnel/
│   │   ├── Person.java                # Abstract base person (this() chaining)
│   │   ├── Actor.java                 # Extends Person (Set<String> skills, billing)
│   │   ├── CrewMember.java            # Extends Person (String[] certs, union)
│   │   └── Director.java              # Extends CrewMember (Multilevel inheritance)
│   └── asset/
│       ├── ProductionAsset.java       # Abstract asset (Identifiable, CostTrackable)
│       ├── Equipment.java             # Extends ProductionAsset (insurance fees)
│       └── Location.java              # Extends ProductionAsset (municipal permit fees)
├── repository/
│   ├── Repository.java                # Generic interface <T, ID>
│   └── FileRepository.java            # Generic Object Stream implementation
├── service/
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
│   ├── ConsoleUI.java                 # Clean box header, input prompts, validators
│   ├── DataGenerator.java             # Pre-seeds 3 distinct movies under Horizon Studios
│   └── CollectionUtils.java           # Generic collection & stream utilities
└── ui/
    └── MenuController.java            # 12-Option interactive terminal menu & test runner
```

---

## 6. Object-Oriented Features Used (Concept-to-Code Mapping)

| Object-Oriented Concept | Specific Implementation in CineFlow 2.0 |
| :--- | :--- |
| **Classes & Objects** | `ProductionHouse`, `Movie`, `Scene`, `CallSheet`, `ExpenseRecord`, `Equipment`, `Location`. |
| **Interfaces** | `Identifiable<ID>`, `Reportable`, `CostTrackable`, `Schedulable`, `Repository<T, ID>`. |
| **Abstract Classes** | `Person` (base for personnel), `ProductionAsset` (base for physical assets). |
| **Inheritance Hierarchies** | `Person` $\rightarrow$ `Actor`; `Person` $\rightarrow$ `CrewMember` $\rightarrow$ `Director` (Multilevel); `ProductionAsset` $\rightarrow$ `Equipment`, `Location`. |
| **Constructors & Chaining** | Default and overloaded constructors using `this(...)` and `super(...)` across `Movie`, `ProductionHouse`, `Person`, `CrewMember`, and `Director`. |
| **Arrays & Strings** | `String[] certifications` in `CrewMember`, `String[] productionChecklist` in `Scene`; array resizing with `Arrays.copyOf()`; string formatting and text blocks. |
| **IO Streams** | `ObjectOutputStream` & `ObjectInputStream` for persistence; `PrintWriter`, `BufferedWriter`, `FileReader`, `BufferedReader` for file report export and import. |
| **Exception Handling** | Custom checked hierarchy: `CineFlowException` base, subclasses `ResourceNotFoundException`, `ScheduleConflictException`, `BudgetExceededException`, `ValidationException`. |
| **Packages** | 7 distinct modular packages (`model`, `personnel`, `asset`, `repository`, `service`, `exception`, `util`, `ui`). |
| **Polymorphism** | Dynamic dispatch on `person.calculateRemuneration(days)` and `asset.computeTotalCost()`. Compile-time method overloading on services and entities. |
| **Method Overloading** | Overloaded `addScene()`, `addScenes()`, `assignActor()`, `assignCrew()`, `bookEquipment()`, `allocateBudget()`, `logExpense()`, `scheduleScene()`. |
| **Method Overriding** | `@Override` of `calculateRemuneration()`, `computeTotalCost()`, `generateDetailedReport()`, `compareTo()`, `toString()`, `equals()`, `hashCode()`. |
| **Generic Types** | Generic interface `Repository<T extends Identifiable<ID>, ID>`, generic class `FileRepository<T, ID>`, generic utilities `CollectionUtils.<T, R>extractUnique()`. |
| **Collections - List** | `ArrayList<Scene>`, `ArrayList<ExpenseRecord>`, `List<Movie>`, `List<Person>`. |
| **Collections - Set** | `HashSet<String>` for unique scene assignments; `TreeSet<String>` for sorted actor skills; `TreeSet<LocalDate>` for scheduled dates. |
| **Collections - Queue** | `PriorityQueue<Scene>` prioritized by urgency level, daylight requirements, and scene numbers. |
| **Collections - Map** | `LinkedHashMap<String, Movie>` (studio movie catalog), `TreeMap<Department, Double>` (sorted department ledger), `HashMap<Department, Double>` (spent budget), `LinkedHashMap<String, String>` (ordered call times). |
| **Functional Interfaces & Lambdas** | Custom `@FunctionalInterface CostEstimator`, `@FunctionalInterface ConflictValidator`; Java built-ins `Predicate<T>`, `Comparator<T>`. Stream pipelines (`.filter()`, `.map()`, `.sorted()`, `.collect()`, `.reduce()`). |

---

## 7. Execution of Test Cases & Sample Output

### Pre-Seeded Multi-Movie Dataset
1. **Interstellar Journey (MOV-01)**: Sci-Fi | Status: **In-Production / Shooting** | Budget: $2,500,000 | 6 Scenes (1 filmed, 3 scheduled, 2 draft).
2. **Shadows of the Past (MOV-02)**: Psychological Thriller | Status: **Pre-Production / Casting** | Budget: $1,000,000 | 4 Scenes.
3. **The Royal Heritage (MOV-03)**: Historical Drama | Status: **Post-Production / Editing** | Budget: $3,600,000 | 4 Scenes (4 filmed, 100% progress).

### Automated Test Suite Execution (Menu Option 12)
```text
>>> AUTOMATED OBJECT-ORIENTED CONCEPT TEST SUITE <<<
----------------------------------------------------
Executing automated test verification for academic evaluation rubric...

[Test 1/8] Inheritance & Dynamic Polymorphism Dispatch
[SUCCESS] Polymorphism Passed: Actor=$13000, Crew=$11500, Director=$16500

[Test 2/8] Method Overloading
[SUCCESS] Method Overloading Passed: assignRole() variants resolved correctly.

[Test 3/8] Collections - PriorityQueue Shooting Urgency
[SUCCESS] PriorityQueue Passed: Urgent golden hour scene polled first ahead of priority 3 interior scene.

[Test 4/8] Custom Exception: BudgetExceededException
[SUCCESS] Exception Passed: Caught BudgetExceededException as expected: CRITICAL OVERRUN: Department 'Stunts & Special Actions' attempted to spend $6000.00, but only $5000.00 remains in allocation!

[Test 5/8] Custom Exception: ScheduleConflictException
[SUCCESS] Exception Passed: Caught ScheduleConflictException as expected: Schedule conflict detected for 'Location LOC-501' on 2026-10-22 (08:00 - 12:00): Already reserved for Scene #1

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

### Sample Screen: Main Menu & Studio Film Slate (Option 2)
```text
========================================================================
           HORIZON STUDIOS - MOVIE PRODUCTION MANAGEMENT SYSTEM         
========================================================================
Active Movie: [MOV-01] Interstellar Journey (PRODUCTION)
------------------------------------------------------------------------
 [1]  🎬 Switch / Select Active Movie
 [2]  🎥 View All Movies in Production House
 [3]  ➕ Add New Movie to Studio
 [4]  📊 Movie Overview & Status
 [5]  📋 Scenes & Shooting List
 [6]  👥 Actors & Crew Members
 [7]  📍 Locations & Equipment
 [8]  📅 Shooting Schedule & Priorities (Priority Queue)
 [9]  💰 Movie Budget & Expenses
 [10] 📝 Daily Shooting Plan (Call Sheet)
 [11] 💾 Save / Export Reports to File
 [12] 🧪 Automated Test Suite Demo (For Evaluation / Faculty)
 [0]  🚪 Exit Application

Select an option [0 - 12]: 2

>>> HORIZON STUDIOS - FILM SLATE & MOVIE CATALOG <<<
----------------------------------------------------
Studio: Horizon Studios (ID: PH-101) | Established: 2005 | HQ: Los Angeles & Mumbai
--------------------------------------------------------------------------------------------------------------
ID       | TITLE                    | GENRE              | DIRECTOR           | STAGE            | PROGRESS     | ACTIVE
--------------------------------------------------------------------------------------------------------------
MOV-01   | Interstellar Journey     | Sci-Fi             | Marcus Sterling    | PRODUCTION       | 1/6 (17%)    | [ACTIVE]
MOV-02   | Shadows of the Past      | Psychological T... | Evelyn Blackwood   | PRE_PRODUCTION   | 0/4 (0%)     |    -   
MOV-03   | The Royal Heritage       | Historical Drama   | Arthur Pendelton   | POST_PRODUCTION  | 4/4 (100%)   |    -   
--------------------------------------------------------------------------------------------------------------
Studio Summary: 3 Movie Projects | 12 Central Talent | 7 Physical Assets
```

---

## 8. Inference and Future Extensions

### Inference
Developing **CineFlow 2.0** proves that adopting an authentic domain hierarchy (`ProductionHouse` $\rightarrow$ `Movie` $\rightarrow$ `Scene` / `BudgetService`) elevates software from an academic exercise into an enterprise-ready system architecture:
- **Hierarchical Modeling**: Isolating budgets, scenes, and priority queues per movie eliminates cross-project data pollution.
- **Polymorphism**: Unified treatment of personnel and assets enables extensible payroll auditing and cost forecasting.
- **Collection Mastery**: Choosing `LinkedHashMap` for the movie slate, `PriorityQueue` for filming order, and `TreeSet` for date sorting directly optimizes real-world film scheduling workflows.

### Future Extensions
1. **Multi-Studio Cloud Synchronization**: Synchronizing multi-studio catalogs across distributed branches via REST APIs.
2. **Automated Weather API Integration**: Live weather querying to dynamically reorder exterior shooting priority queues.
3. **Web Dashboard (Spring Boot + React)**: Providing mobile access for on-set assistant directors and talent agents.
