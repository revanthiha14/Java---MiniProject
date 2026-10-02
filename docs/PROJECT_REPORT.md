# CineFlow: Movie Production Management System
## Academic Mini Project Report

**Institution**: Sri Sivasubramaniya Nadar College of Engineering, Kalavakkam – 603 110  
*(An Autonomous Institution, Affiliated to Anna University, Chennai)*  
**Department**: Computer Science and Engineering  
**Course**: Object-Oriented Programming (Java)  
**Academic Year**: 2026  

---

## 1. Problem Statement
Modern filmmaking and OTT feature productions involve complex, multi-tiered operations coordinating hundreds of human resources (directors, actors, gaffers, cinematographers, sound engineers), physical assets (cameras, high-end lenses, sound rigs, shooting locations), departmental financial ceilings, and multi-stage shooting schedules (pre-production, production, post-production). 

Manual coordination or fragmented spreadsheets frequently lead to:
1. **Severe Schedule Conflicts**: Double-booking expensive filming locations or key talent across simultaneous scenes.
2. **Daylight & Weather Inefficiencies**: Shooting scenes out of optimal sunlight sequence, wasting fleeting golden-hour lighting windows.
3. **Catastrophic Budget Overruns**: Departments expending funds past approved ceilings without real-time audit controls.
4. **Disorganized Communications**: Inconsistent distribution of daily production Call Sheets to crew and talent.

**Objective**: To develop a robust, modular, terminal-based Java application that automates end-to-end movie production workflows—from script breakdown and daylight-driven priority shooting queues to conflict-free scheduling, automated call sheets, dynamic talent payroll calculations, and departmental budget audits.

---

## 2. Motivation for the Problem
Film production is an industry where *every minute of wasted camera time equates to thousands of dollars in lost budget*. A single key actor arriving at the wrong time or an uninspected camera package can halt a 150-person crew. Building **CineFlow** provides an industry-grade simulation of digital studio management while exercising every core tenet of Object-Oriented Software Engineering—polymorphic payroll models, generic persistent repositories, priority scheduling queues, functional estimation interfaces, and exception handling.

---

## 3. Scope and Limitations

### Scope
- **Script Breakdown**: Structuring script scenes with page counts, lighting conditions (Day Exterior, Night, Golden Hour, Interior Studio), and cast/equipment requirements.
- **Priority Shooting Queue**: Dynamic queueing prioritizing weather-dependent scenes and critical sequences.
- **Talent & Crew Management**: Multi-level contracts with distinct remuneration models for Lead/Supporting Actors, Union Crew Members (IATSE/DGA), and Directors.
- **Asset Booking & Inventory**: Equipment rentals with automated insurance calculations and location bookings with municipal filming permits.
- **Schedule Conflict Engine**: Real-time detection of overlapping talent or location reservations.
- **Daily Call Sheet Generator**: Exporting official production call sheets with call times, wardrobe fittings, and emergency contacts to persistent disk files.
- **Budget Variance Monitoring**: Department-level budget ceilings with strict overrun prevention and audit transaction logs.

### Limitations
- The system currently operates via an interactive terminal interface (CLI) rather than a graphical web/mobile GUI.
- Calendar scheduling is managed at day and time-slot granularity rather than continuous real-time GPS telemetry.
- Storage uses standard Java Object Streams and formatted text reports rather than an external distributed SQL/NoSQL cluster.

---

## 4. Explore Design Alternatives (CO3 - K6)

| Design Dimension | Selected Design in CineFlow | Alternative Considered | Rationale for Selection |
| :--- | :--- | :--- | :--- |
| **Persistence Mechanism** | File-based Generic `FileRepository<T, ID>` using Object Streams & formatted text exports. | External relational database (e.g., MySQL or PostgreSQL via JDBC). | The project guidelines require a clean, self-contained terminal application without third-party external DB setup burdens. Java I/O streams provide portable, zero-configuration file storage. |
| **Scheduling Representation** | Priority Queue (`PriorityQueue<Scene>`) utilizing `Comparable<Scene>`. | Plain List (`ArrayList<Scene>`) with manual bubble-sorting. | Real movie shoots are dynamic; a PriorityQueue guarantees $O(\log n)$ insertion and $O(1)$ lookup for the next highest-urgency scene (e.g., daylight weather dependency). |
| **Payroll Calculation** | Dynamic runtime polymorphism (`Person.calculateRemuneration()`). | Monolithic `switch-case` checking an employee role string. | Monolithic switches violate the Open/Closed Principle (OCP). Polymorphic dispatch allows adding new roles (e.g., `StuntDouble`, `ExecutiveProducer`) without touching existing calculation code. |
| **Budget Storage** | `TreeMap<Department, Double>` for allocated funds and `HashMap` for spent ledgers. | Two-dimensional primitive arrays `double[][]`. | `TreeMap` ensures that departments are always printed in consistent, sorted order across financial reports while providing $O(\log k)$ lookup safety. |

---

## 5. Modules Split-up

The codebase is organized into cleanly decoupled packages adhering to the Single Responsibility Principle:

```text
com.cineflow/
├── Main.java                          # Entrypoint & DI coordinator
├── model/
│   ├── Identifiable.java              # Generic interface <ID>
│   ├── Reportable.java                # Text report generation contract
│   ├── CostTrackable.java             # Expense tracking interface
│   ├── Schedulable.java               # Conflict-check contract
│   ├── Department.java                # Enum of film divisions
│   ├── ProductionPhase.java           # Enum of film stages
│   ├── SceneStatus.java               # Enum of scene statuses
│   ├── DaylightRequirement.java       # Enum of lighting conditions
│   ├── Scene.java                     # Scene domain entity (Comparable)
│   ├── CallSheet.java                 # Daily Call Sheet entity
│   ├── personnel/
│   │   ├── Person.java                # Abstract base person (this() chaining)
│   │   ├── Actor.java                 # Extends Person (Set<String> skills)
│   │   ├── CrewMember.java            # Extends Person (String[] certs)
│   │   └── Director.java              # Extends CrewMember (Multilevel inheritance)
│   └── asset/
│       ├── ProductionAsset.java       # Abstract asset (Identifiable, CostTrackable)
│       ├── Equipment.java             # Extends ProductionAsset (insurance fees)
│       └── Location.java              # Extends ProductionAsset (permit fees)
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
│   ├── ConsoleUI.java                 # Terminal formatting, banners, input prompts
│   ├── DataGenerator.java             # Pre-seeded realistic dataset
│   └── CollectionUtils.java           # Generic collection & stream utilities
└── ui/
    └── MenuController.java            # Interactive menu router & test suite runner
```

---

## 6. Object-Oriented Features Used (Concept-to-Code Mapping)

| Object-Oriented Concept | Specific Implementation in CineFlow |
| :--- | :--- |
| **Classes & Objects** | `Scene`, `CallSheet`, `ExpenseRecord`, `Equipment`, `Location` instantiated across lifecycle. |
| **Interfaces** | `Identifiable<ID>`, `Reportable`, `CostTrackable`, `Schedulable`, `Repository<T, ID>`. |
| **Abstract Classes** | `Person` (base for personnel), `ProductionAsset` (base for physical assets). |
| **Inheritance Hierarchies** | `Person` $\rightarrow$ `Actor`; `Person` $\rightarrow$ `CrewMember` $\rightarrow$ `Director` (Multilevel); `ProductionAsset` $\rightarrow$ `Equipment`, `Location`. |
| **Constructors & Chaining** | Default and overloaded constructors using `this(...)` and `super(...)` across all classes (e.g., `Person` has 3 chained constructors; `CrewMember` chains to `Person`). |
| **Arrays & Strings** | `String[] certifications` in `CrewMember`, `String[] productionChecklist` in `Scene`; array resizing with `Arrays.copyOf()`; formatted string templates, truncation, text blocks. |
| **IO Streams** | `ObjectOutputStream` & `ObjectInputStream` for binary state; `PrintWriter`, `BufferedWriter`, `FileReader`, `BufferedReader` for file report export and import. |
| **Exception Handling** | Custom checked hierarchy: `CineFlowException` base, subclasses `ResourceNotFoundException`, `ScheduleConflictException`, `BudgetExceededException`, `ValidationException`. Comprehensive `try-catch-finally`. |
| **Packages** | 6 distinct modular packages (`model`, `personnel`, `asset`, `repository`, `service`, `exception`, `util`, `ui`). |
| **Polymorphism** | Dynamic method dispatch on `person.calculateRemuneration(days)` and `asset.computeTotalCost()`. Compile-time method overloading on services. |
| **Method Overloading** | Overloaded `assignRole()`, `allocateBudget()`, `logExpense()`, `scheduleScene()`, `registerActor()`, `registerCrew()`. |
| **Method Overriding** | `@Override` of `calculateRemuneration()`, `computeTotalCost()`, `generateDetailedReport()`, `compareTo()`, `toString()`, `equals()`, `hashCode()`. |
| **Generic Types** | Generic interface `Repository<T extends Identifiable<ID>, ID>`, generic class `FileRepository<T, ID>`, generic utilities `CollectionUtils.<T, R>extractUnique()`. |
| **Collections - List** | `ArrayList<Scene>`, `ArrayList<ExpenseRecord>`, `List<Person>`. |
| **Collections - Set** | `HashSet<String>` for unique scene assignments; `TreeSet<String>` for sorted actor skills; `TreeSet<LocalDate>` for scheduled dates. |
| **Collections - Queue** | `PriorityQueue<Scene>` prioritized by urgency level, daylight requirements, and scene numbers. |
| **Collections - Map** | `TreeMap<Department, Double>` (sorted department ledger), `HashMap<Department, Double>` (spent budget), `LinkedHashMap<String, String>` (ordered call times). |
| **Functional Interfaces & Lambdas** | Custom `@FunctionalInterface CostEstimator`, `@FunctionalInterface ConflictValidator`; Java built-ins `Predicate<T>`, `Comparator<T>`, `Function<T, R>`. Stream pipelines (`.filter()`, `.map()`, `.sorted()`, `.collect()`, `.reduce()`). |

---

## 7. Execution of Test Cases & Sample Output

CineFlow includes an integrated automated test suite (Menu Option 8) that verifies all core requirements.

### Test Case Results Summary

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

### Sample Screen: Production Dashboard (Option 1)
```text
>>> PRODUCTION EXECUTIVE DASHBOARD <<<
----------------------------------------
Movie Title        : Chronicles of Aether (Feature Film)
Total Script Scenes: 6 | Filmed: 1 | Scheduled: 3 | Draft: 2
Production Progress: [####---------------------] 16.7% Completed
---------------------------------------------------------------------------
Total Budget       : $2,500,000.00
Expended to Date   : $88,000.00 (3.5% utilized)
Remaining Reserve  : $2,412,000.00 (HEALTHY)
---------------------------------------------------------------------------
Talent Roster      : 4 Actors | 6 Technical Crew Members
Physical Assets    : 4 Equipment Units | 3 Filming Locations
Shooting Queue     : 5 scenes pending in PriorityQueue
NEXT URGENT SHOOT  : Scene #02 - "Desert Rover Ambush" (Priority 1 | Day Exterior (Sunlight Dependent))
```

### Sample Screen: Department Budget Variance Ledger (Option 6 -> 1)
```text
>>> DEPARTMENT BUDGET VARIANCE LEDGER <<<
----------------------------------------------------------------------------------------
DEPARTMENT                   | ALLOCATED      | SPENT          | REMAINING      | UTIL %  
----------------------------------------------------------------------------------------
Directing & Creative         | $  350,000.00  | $        0.00  | $  350,000.00  |    0.0%
Camera & Cinematography      | $  320,000.00  | $   28,000.00  | $  292,000.00  |    8.8%
Grip & Electrical / Lighting | $  180,000.00  | $        0.00  | $  180,000.00  |    0.0%
Sound Recording & Mixing     | $  140,000.00  | $        0.00  | $  140,000.00  |    0.0%
Art Department & Props       | $  400,000.00  | $   45,000.00  | $  355,000.00  |   11.3%
Wardrobe & Makeup            | $  220,000.00  | $   15,000.00  | $  205,000.00  |    6.8%
Visual Effects & Post-Prod   | $  600,000.00  | $        0.00  | $  600,000.00  |    0.0%
Stunts & Special Actions     | $  150,000.00  | $        0.00  | $  150,000.00  |    0.0%
Production Logistics         | $  140,000.00  | $        0.00  | $  140,000.00  |    0.0%
----------------------------------------------------------------------------------------
TOTALS: ALLOCATED: $2,500,000.00 | SPENT: $88,000.00 | REMAINING: $2,412,000.00
```

---

## 8. Inference and Future Extensions

### Inference
Developing **CineFlow** proves that applying strict Object-Oriented principles yields software that is highly modular, easily testable, and naturally resilient to bugs:
- **Encapsulation & Validation** prevent corrupted state (e.g., negative budget allocations or blank IDs).
- **Polymorphism** allows frictionless extension of new personnel roles and asset classifications.
- **Generic Repositories** decouple high-level production logic from the underlying storage mechanism.
- **PriorityQueues & Streams** make complex scheduling and financial aggregation expressive and computationally efficient.

### Future Extensions
1. **Multi-Camera Virtual Production Simulator**: Integrating real-time virtual camera tracking metadata and LED volume wall configurations.
2. **REST API & Web UI Layer**: Wrapping the service layer with Spring Boot / REST endpoints and building a React/Next.js frontend dashboard.
3. **Database Integration**: Transitioning the generic `Repository` layer from file-based serialization to JPA/Hibernate with PostgreSQL.
4. **Automated Weather API Integration**: Hooking into live meteorology APIs to automatically reshuffle exterior shooting schedules when rain or cloud cover is detected.
