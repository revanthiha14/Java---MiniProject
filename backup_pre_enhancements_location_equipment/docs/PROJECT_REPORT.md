# CineFlow: Cinema Production Management System
## 2nd-Year College Object-Oriented Programming (Java) Mini-Project Report

**Course**: Object-Oriented Programming (Java)
**Academic Year**: 2026

---

## 1. Project Overview & Problem Statement
In commercial film and cinema production, studios must coordinate dozens of interdependent workflows simultaneously: casting, screenplay drafts, shooting schedules, location call sheets, voice dubbing, visual effects (VFX), physical inventory (props, costumes, equipment), marketing approvals (posters, teasers, trailers), multi-territory theatrical/OTT distributions, and multi-crore budgets. Relying on disconnected spreadsheets and paper notes inevitably causes double-booking, missing props, unauthorized teaser leaks, and catastrophic budget overruns.

**CineFlow** is an enterprise-grade, console-based Java cinema production management system designed to coordinate the entire cinematic lifecycle from initial script narration to global theatrical premiere:
1. **Production Houses / Studios**: Managing studio identity, headquarters, and catalog portfolios (`PH-101`, `PH-102`, `PH-103`).
2. **Movie Management**: Tracking active movies, directors, genres, release years, budgets, and lifecycle states (`PLANNING`, `PRODUCTION`, `POST_PRODUCTION`, `RELEASED`).
3. **Screenplay & Scene Management**: Scene sluglines, locations, page counts, shooting dates, formatted script generation, and automatic schedule conflict detection preventing location double-booking.
4. **Cast & Crew Roster (`CastManager`)**: Polymorphic personnel management under `Person` (`Actor` and `CrewMember`), role downcasting, and department tracking.
5. **Budget & Expense Tracking**: Expense ledger recording and budget ceiling enforcement via custom exception handling (`BudgetExceededException`).
6. **Audience & Critic Reviews (`ReviewManager`)**: Enforcing release-date preconditions, 1â€“5 star ratings, arithmetic mean ratings, and star distribution analytics.
7. **Master Production & Release Schedule**: 12-stage production pipeline milestones (`ProductionMilestone`, `ProductionStage`), chronology sequence validation, and delay alerts.
8. **Daily Call Sheets & Shooting Schedule**: Daily call sheets (`CallSheet`), actor call times (`CastCallEntry`), safety hospital notes, weather conditions, and wrap report logs.
9. **Voice Dubbing & Localization**: Multi-language dubbing tracks (`DubbingTrack`), voice artist assignments, retake tracking, and approval sign-offs.
10. **Post-Production Management**: Specialized pipeline tasks (`PostProductionTask`, `PostProductionType`) covering Picture Editing, Sound Mixing, VFX (CGI), and DCP Rendering.
11. **Theatrical & OTT Distribution**: Regional territories, distributors, screen counts, theatrical status lifecycles, and OTT streaming premiere dates (`DistributionRelease`).
12. **Props, Costumes & Equipment Management (`InventoryManager`)**: Physical asset tracking (`InventoryItem`, `ItemCategory`), availability math, checkout to movies & scenes (`ItemAssignment`), return reconciliation (good, damaged, lost), and condition grading.
13. **Poster & Promotional Asset Management (`PromotionManager`)**: Marketing assets (`PromotionalAsset`, `AssetType`, `ApprovalStatus`) covering posters, teasers, trailers, banners, and social assets with multi-round revision bumping and approval/rejection workflows.
14. **Production Reports & Executive Dashboard (`ReportService`)**: Consolidated executive KPIs, financial ledgers, shooting progress, dubbing/VFX status, pending marketing approvals, inventory allocations, schedule milestones, and text report file export to `data/reports/`.
15. **Text-File Persistence (`FileService`)**: Pipe-delimited persistence across 9 text files and specialized export directories (`data/scripts/`, `data/call_sheets/`, `data/distribution/`, `data/reports/`) with fault-tolerant parsing and idempotent seeding preventing duplicate records across cold restarts.

---

## 2. Key Object-Oriented Programming (OOP) Concepts Demonstrated

### 2.1 Classes and Objects
Every entity in the cinema production domain is modeled as an encapsulated Java class:
- **Core Entities**: `Movie`, `ProductionHouse`, `Scene`, `Expense`, `Review`
- **Talent & Crew**: `Person`, `Actor`, `CrewMember`
- **Physical & Marketing Assets**: `InventoryItem`, `ItemAssignment`, `PromotionalAsset`
- **Production Operations**: `CallSheet`, `CastCallEntry`, `ProductionMilestone`, `DubbingTrack`, `PostProductionTask`, `DistributionRelease`
- **Service Layer**: `MovieManager`, `CastManager`, `ReviewManager`, `InventoryManager`, `PromotionManager`, `ReportService`, `FileService`
- **Presentation**: `ConsoleMenu`, `Main`

### 2.2 Encapsulation & Single Responsibility Principle (SRP)
All classes protect internal state with `private` member variables and provide validated getters and setters.
- `InventoryManager` encapsulates inventory stock invariants ($total = available + assigned + damaged + lost$).
- `PromotionManager` encapsulates asset revision bumping and the pending/approved/rejected state machine.
- `ReportService` encapsulates reporting and statistical aggregation without mutating underlying entities.
- `CastManager` encapsulates talent roster queries and downcasting.
- `ReviewManager` encapsulates review preconditions and statistical distribution computation.

### 2.3 Inheritance & Abstraction
- **Abstract Base Class**: `Person` serves as an abstract base class containing common fields (`id`, `name`, `email`) and an abstract method `public abstract String getRole()`.
- **Subclasses**:
  - `Actor extends Person` (adds `characterName`, overrides `getRole()`).
  - `CrewMember extends Person` (adds `department`, overrides `getRole()`).
- Both subclasses use `super(...)` to initialize inherited state.

### 2.4 Polymorphism & Dynamic Method Dispatch
- `Actor` and `CrewMember` override `getRole()` and `displayInfo()`.
- In `Movie` and `CastManager`, `Person` references are managed polymorphically in `List<Person>`. Dynamic type inspection (`instanceof`) with safe pattern downcasting is used in `getActors()` and `getCrewMembers()`.

### 2.5 Enumerations (Enums)
Six strongly-typed Java enums eliminate magic numbers and invalid string literals:
1. `MovieStatus`: `PLANNING`, `PRODUCTION`, `POST_PRODUCTION`, `RELEASED`
2. `ProductionStage`: 12 film roadmap stages from script narration to worldwide theatrical release
3. `PostProductionType`: `EDITING`, `SOUND_MIXING`, `VFX`, `RENDERING`
4. `ItemCategory`: `PROP`, `COSTUME`, `EQUIPMENT`
5. `AssetType`: `POSTER`, `TEASER`, `TRAILER`, `BANNER`, `SOCIAL_MEDIA`
6. `ApprovalStatus`: `PENDING`, `APPROVED`, `REJECTED`

### 2.6 Custom Checked Exception Hierarchy
A structured exception hierarchy rooted in `CineFlowException` guarantees robust compile-time error checking:
```
java.lang.Exception
  â””â”€â”€ com.cineflow.exception.CineFlowException
        â”œâ”€â”€ BudgetExceededException
        â”œâ”€â”€ ResourceNotFoundException
        â”œâ”€â”€ ScheduleConflictException
        â””â”€â”€ ValidationException
```
- **`CineFlowException`**: Base checked exception for all application domain errors.
- **`BudgetExceededException`**: Thrown when spending would breach the allocated movie budget ceiling.
- **`ResourceNotFoundException`**: Thrown when a movie, scene, studio, inventory item, or promotional asset ID cannot be found.
- **`ScheduleConflictException`**: Thrown when scheduling multiple scenes at the same location on the same shoot date.
- **`ValidationException`**: Thrown when data violates business invariants (e.g. empty names, negative stock, returning more items than checked out, reviewing unreleased movies, duplicate IDs).

### 2.7 Collections Framework
- `ArrayList`: Dynamic ordered collections for scenes, personnel, expenses, reviews, assignments, milestones, call sheets, and releases.
- `HashMap` and `LinkedHashMap`: Fast $O(1)$ lookups for studios by ID, inventory items by ID, promotional assets by ID, and star rating frequency distributions.

### 2.8 Text-File Persistence & Java I/O
- Lightweight, human-readable pipe-delimited text persistence in `data/`:
  - `studios.txt`, `movies.txt`, `scenes.txt`, `cast_crew.txt`, `expenses.txt`, `reviews.txt`
  - `schedules.txt`, `dubbing.txt`, `post_production.txt`, `call_sheets.txt`, `distribution.txt`
  - `inventory.txt`, `item_assignments.txt`, `promotional_assets.txt`
- Dedicated document exports:
  - `data/scripts/<MovieID>_<Title>_Screenplay.txt`
  - `data/call_sheets/<MovieID>_<SheetID>_CallSheet.txt`
  - `data/distribution/<MovieID>_Distribution_Manifest.txt`
  - `data/reports/<MovieID>_<Title>_Production_Report.txt`
- **Fault-Tolerant & Idempotent**: Corrupted lines are skipped with logged warnings without application termination. Cold restarts check existing data files and do not seed duplicate records.

---

## 3. Class Structure & Responsibilities

| Class / Enum | Package | Type | Description |
|---|---|---|---|
| `Main` | `com.cineflow` | Concrete Class | Application entry point; initializes services and launches console menu. |
| `Person` | `com.cineflow.model` | Abstract Class | Abstract base entity for film personnel (`id`, `name`, `email`, abstract `getRole()`). |
| `Actor` | `com.cineflow.model` | Concrete Class | Extends `Person`; represents cast talent with character name. |
| `CrewMember` | `com.cineflow.model` | Concrete Class | Extends `Person`; represents technical crew with production department. |
| `Movie` | `com.cineflow.model` | Concrete Class | Core movie entity; aggregates scenes, cast/crew, expenses, reviews, schedules, dubbing, VFX, and releases. |
| `ProductionHouse` | `com.cineflow.model` | Concrete Class | Represents a production studio; aggregates studio movies. |
| `Scene` | `com.cineflow.model` | Concrete Class | Models a screenplay scene with slugline, script pages, shooting date, and location. |
| `Expense` | `com.cineflow.model` | Concrete Class | Represents an expenditure entry with category, amount, and description. |
| `Review` | `com.cineflow.model` | Concrete Class | Represents an audience/critic review with 1â€“5 star rating and comment. |
| `ProductionMilestone` | `com.cineflow.model` | Concrete Class | Represents a milestone in the master film roadmap with stage and status. |
| `DubbingTrack` | `com.cineflow.model` | Concrete Class | Represents a multi-language voice dubbing track with artist, language, and retakes. |
| `PostProductionTask` | `com.cineflow.model` | Concrete Class | Represents post-production work (editing, sound mix, VFX, DCP rendering). |
| `CallSheet` | `com.cineflow.model` | Concrete Class | Represents daily shooting call sheets with call times, scenes, weather, and hospital. |
| `CastCallEntry` | `com.cineflow.model` | Concrete Class | Serialized entry for individual actor call times (pickup, HMU, on-set). |
| `DistributionRelease` | `com.cineflow.model` | Concrete Class | Represents theatrical territories, distributors, screens, and OTT streaming deals. |
| `InventoryItem` | `com.cineflow.model` | Concrete Class | Represents physical props, costumes, and equipment with stock quantities, condition, and notes. |
| `ItemAssignment` | `com.cineflow.model` | Concrete Class | Links a checked-out inventory item to a specific movie and scene with checkout/return dates. |
| `PromotionalAsset` | `com.cineflow.model` | Concrete Class | Represents posters, teasers, trailers, and social assets with approval status and revisions. |
| `MovieStatus` | `com.cineflow.model` | Enum | Lifecycle states: `PLANNING`, `PRODUCTION`, `POST_PRODUCTION`, `RELEASED`. |
| `ProductionStage` | `com.cineflow.model` | Enum | 12 roadmap phases from script narration to theatrical premiere. |
| `PostProductionType` | `com.cineflow.model` | Enum | Post-production categories: `EDITING`, `SOUND_MIXING`, `VFX`, `RENDERING`. |
| `ItemCategory` | `com.cineflow.model` | Enum | Inventory categories: `PROP`, `COSTUME`, `EQUIPMENT`. |
| `AssetType` | `com.cineflow.model` | Enum | Marketing asset types: `POSTER`, `TEASER`, `TRAILER`, `BANNER`, `SOCIAL_MEDIA`. |
| `ApprovalStatus` | `com.cineflow.model` | Enum | Marketing approval states: `PENDING`, `APPROVED`, `REJECTED`. |
| `CineFlowException` | `com.cineflow.exception` | Concrete Class | Base checked exception for CineFlow; extends `Exception`. |
| `BudgetExceededException` | `com.cineflow.exception` | Concrete Class | Thrown when expense exceeds allocated movie budget ceiling. |
| `ResourceNotFoundException` | `com.cineflow.exception` | Concrete Class | Thrown when a movie, scene, person, studio, asset, or item is not found. |
| `ScheduleConflictException` | `com.cineflow.exception` | Concrete Class | Thrown when a filming location is double-booked on the same shoot date. |
| `ValidationException` | `com.cineflow.exception` | Concrete Class | Thrown on invalid input data or business rule violations. |
| `CastManager` | `com.cineflow.service` | Concrete Class | Service managing cast/crew personnel rosters, search, and downcasting. |
| `ReviewManager` | `com.cineflow.service` | Concrete Class | Service managing audience feedback, ratings, and statistical distributions. |
| `InventoryManager` | `com.cineflow.service` | Concrete Class | Service managing props, costumes, and equipment catalog, checkouts, and returns. |
| `PromotionManager` | `com.cineflow.service` | Concrete Class | Service managing promotional asset approvals, rejections, revisions, and filtering. |
| `ReportService` | `com.cineflow.service` | Concrete Class | Service generating executive dashboards, analytical audit reports, and file exports. |
| `FileService` | `com.cineflow.service` | Concrete Class | Reads and writes plain-text records in `data/` and specialized export folders. |
| `MovieManager` | `com.cineflow.service` | Concrete Class | Central coordination service maintaining studios, active movie context, and sub-services. |
| `ConsoleMenu` | `com.cineflow.ui` | Concrete Class | Interactive console interface with 22 top-level menu options and rich submenus. |

### Architectural Metrics
- **Concrete Classes**: **30**
- **Abstract Classes**: **1** (`Person`)
- **Total Java Classes**: **31**
- **Java Enums**: **6** (`MovieStatus`, `ProductionStage`, `PostProductionType`, `ItemCategory`, `AssetType`, `ApprovalStatus`)
- **Java Interfaces**: **0**
- **Total Java Source Files**: **37**

---

## 4. Console Menu Structure (Options 0â€“21)

All existing working menu options (1â€“16) are fully preserved. New modules are integrated as options 17â€“19, with Save/Load moved to 20â€“21 and Exit at 0:

- **Option 1**: View & Switch Active Movie
- **Option 2**: Add New Movie to Active Studio
- **Option 3**: Search Movies (ID, Name, Genre, Rating)
- **Option 4**: Update Movie Status (Lifecycle: Planning -> Production -> Post -> Released)
- **Option 5**: Movie Statistics / Overview
- **Option 6**: Manage Scenes & Screenplay (Add, Edit, View Scripts, Conflict Detection)
- **Option 7**: Master Production & Release Schedule (Full Roadmap & Chronology Validation)
- **Option 8**: Daily Call Sheets & Shooting Schedule (Cast Calls, Crew, Emergency Hospital)
- **Option 9**: Voice Dubbing Management (Artists, Languages, Retakes, Approvals)
- **Option 10**: Post-Production Management (Editing, Sound Mixing, VFX, Rendering)
- **Option 11**: Theatrical & OTT Distribution (Release Regions, Distributors, Screens, Streaming)
- **Option 12**: Manage Cast & Crew (View, Add Actor/Crew, Downcasting)
- **Option 13**: Manage Budget & Expenses (Ledger, Remaining Funds, Overrun Protection)
- **Option 14**: Add Movie Review (Release Status Verification & Rating Checks)
- **Option 15**: View Movie Reviews (Average Ratings & Star Frequency Distributions)
- **Option 16**: Manage Production Houses (View, Switch Active Studio, Add New Studio)
- **Option 17 [NEW]**: Props, Costumes & Equipment (Inventory Catalog, Checkouts to Movie/Scene, Returns & Damage Tracking)
- **Option 18 [NEW]**: Poster & Promotional Assets (Approval Workflow, Revisions, Multi-Platform Marketing)
- **Option 19 [NEW]**: Production Reports & Executive Dashboard (Live KPIs, Financial Health, Milestones, Text File Export)
- **Option 20**: Save Data to Text Files
- **Option 21**: Load Data from Text Files
- **Option 0**: Exit (with automatic save confirmation)

---

## 5. Verification and Test Results

A comprehensive automated test suite consisting of **71 verification tests** was executed against the application:

### Module A: Props, Costumes, Equipment Management (16 Tests)
1. Add item and retrieve by ID (`[PASS]`)
2. Duplicate Item ID rejection via `ValidationException` (`[PASS]`)
3. Search inventory items by keyword and category (`[PASS]`)
4. Update item name in catalog (`[PASS]`)
5. Update item condition in catalog (`[PASS]`)
6. Create checkout assignment to movie & valid scene (`[PASS]`)
7. Available quantity decremented after checkout (`[PASS]`)
8. Assigned quantity incremented after checkout (`[PASS]`)
9. Checkout with null/invalid movie rejected via `ValidationException` (`[PASS]`)
10. Checkout with non-existent scene rejected via `ResourceNotFoundException` (`[PASS]`)
11. Checkout exceeding available stock rejected via `ValidationException` (`[PASS]`)
12. Assignment marked `RETURNED` upon return (`[PASS]`)
13. Available stock restored for good-condition returns (`[PASS]`)
14. Damaged quantity accurately incremented (`[PASS]`)
15. Assigned quantity reset to 0 upon full return (`[PASS]`)
16. Returning more items than checked out rejected via `ValidationException` (`[PASS]`)

### Module B: Poster & Promotional Asset Management (12 Tests)
17. Add promotional asset and retrieve by ID (`[PASS]`)
18. Initial asset approval status defaults to `PENDING` (`[PASS]`)
19. Initial asset revision number defaults to `1` (`[PASS]`)
20. Duplicate Asset ID rejected via `ValidationException` (`[PASS]`)
21. Approval workflow transitions status to `APPROVED` (`[PASS]`)
22. Approver name and sign-off notes recorded (`[PASS]`)
23. Submitting new revision bumps revision number to `2` (`[PASS]`)
24. Status automatically resets to `PENDING` after revision upload (`[PASS]`)
25. New file path / URL updated on revision (`[PASS]`)
26. Rejection workflow transitions status to `REJECTED` (`[PASS]`)
27. Rejection feedback notes captured for creative revisions (`[PASS]`)
28. Multi-criteria search by movie, type, and platform (`[PASS]`)

### Module C: Production Reports & Dashboard (9 Tests)
29. Executive production dashboard generated without errors (`[PASS]`)
30. Dashboard contains Financial & Budget Overview (`[PASS]`)
31. Dashboard contains Promotional & Marketing Approvals (`[PASS]`)
32. Dashboard contains Props & Equipment Inventory status (`[PASS]`)
33. Comprehensive analytical audit report generated (`[PASS]`)
34. Analytical report contains scene execution & DSR metrics (`[PASS]`)
35. Analytical report contains promotional asset queue (`[PASS]`)
36. Studio portfolio financial and movie report generated (`[PASS]`)
37. Production report exported to file in `data/reports/` (`[PASS]`)

### Empty Data Resilience (3 Tests)
38. Executive dashboard handles null/empty movie safely without NPE or NaN (`[PASS]`)
39. Analytical audit report handles null/empty movie safely (`[PASS]`)
40. Studio report handles null/empty studio safely (`[PASS]`)

### File Persistence & Cold Restart Idempotency (11 Tests)
41. `inventory.txt` created and non-empty (`[PASS]`)
42. `item_assignments.txt` created and non-empty (`[PASS]`)
43. `promotional_assets.txt` created and non-empty (`[PASS]`)
44. Persisted inventory item reloaded successfully across restart (`[PASS]`)
45. Loaded item stock quantities and condition intact (`[PASS]`)
46. Persisted item assignment reloaded successfully (`[PASS]`)
47. Persisted promotional asset reloaded with status and revision intact (`[PASS]`)
48. Loaded item count matches saved count without data loss (`[PASS]`)
49. Loaded asset count matches saved count without data loss (`[PASS]`)
50. Second startup does not duplicate default inventory items (`[PASS]`)
51. Second startup does not duplicate default promotional assets (`[PASS]`)

### Regression of Existing Features (20 Tests)
52. `MOV-01` loaded and retrieved from active studio (`[PASS]`)
53. `MOV-01` scenes retrieved (`[PASS]`)
54. Scene status updated to `COMPLETED` (`[PASS]`)
55. Cast and crew roster loaded (`[PASS]`)
56. Production houses loaded (`[PASS]`)
57. Expense added within budget (`[PASS]`)
58. Remaining budget accurately decremented (`[PASS]`)
59. Budget ceiling enforced via `BudgetExceededException` (`[PASS]`)
60. Review added to released movie (`[PASS]`)
61. Arithmetic average rating calculated (`[PASS]`)
62. Master production schedule milestones loaded (`[PASS]`)
63. Milestone marked completed (`[PASS]`)
64. Daily call sheets loaded (`[PASS]`)
65. Call sheet talent call entries verified (`[PASS]`)
66. Voice dubbing tracks loaded (`[PASS]`)
67. Dubbing track approved (`[PASS]`)
68. Post-production tasks loaded (`[PASS]`)
69. Post-production task marked completed (`[PASS]`)
70. Distribution deals loaded (`[PASS]`)
71. Distribution territory and theatrical release details intact (`[PASS]`)

**Test Summary**: **71 Tests Run | 71 Passed | 0 Failed (100% Pass Rate)**.

---

## 6. Viva Preparation Q&A

**Q1: What new modules were implemented in CineFlow and why are they necessary?**
*Answer*: We implemented three critical cinema production modules:
1. **Physical Inventory Management (`InventoryManager`)**: Tracks props, costumes, and camera/lighting/audio equipment across three categories (`ItemCategory`). Manages checkouts linked to specific movies and scenes (`ItemAssignment`), enforcing stock invariants ($total = available + assigned + damaged + lost$) and return reconciliation.
2. **Promotional Asset Management (`PromotionManager`)**: Manages posters, teasers, trailers, banners, and social assets (`AssetType`). Enforces a formal marketing sign-off workflow (`ApprovalStatus`: Pending, Approved, Rejected) with multi-round revision bumping (`v1 -> v2`) so unapproved assets are never released to the public.
3. **Production Reports & Dashboard (`ReportService`)**: Consolidates live data across finances, scenes shot, voice dubbing, VFX progress, pending promotional approvals, physical inventory on set, and release milestone delays into high-level executive dashboards and exportable text reports.

**Q2: How does CineFlow validate physical inventory checkouts and returns?**
*Answer*: When an item is checked out (`checkoutItem`):
- It verifies that the target movie exists.
- If a scene number is specified, it verifies that the scene actually belongs to that movie (throwing `ResourceNotFoundException` otherwise).
- It verifies that the requested quantity does not exceed the currently available stock (`availableQuantity >= requestedQty`), throwing `ValidationException` otherwise.
Upon return (`returnItem`), it verifies that the sum of good, damaged, and lost items equals the borrowed quantity, updates the item's available, damaged, and lost counts accordingly, and marks the assignment as `RETURNED`.

**Q3: How does the promotional asset approval and revision workflow work?**
*Answer*: Each asset starts with status `PENDING` at revision `v1`. The creative director can approve the asset (setting status to `APPROVED`) or reject it with written feedback (setting status to `REJECTED`). If changes are needed, the team submits a new file path/URL via `submitRevision()`, which increments the version number (`v1 -> v2`) and automatically resets the approval status back to `PENDING` for re-review.

**Q4: How does `ReportService` handle empty movies or missing records without crashing?**
*Answer*: `ReportService` checks for `null` references and empty collections in every report generator. Rather than throwing `NullPointerException` or dividing by zero (which would yield `NaN`), it safely guards calculations (e.g. `(budget > 0 ? (spent / budget) * 100.0 : 0.0)`) and renders user-friendly placeholder messages (such as `"[No expenses recorded for this movie yet]"`).

**Q5: How does CineFlow avoid seeding duplicate demo data on repeated application restarts?**
*Answer*: In `MovieManager.initializeData()`, before seeding demo inventory or promotional items, the system calls `fileService.hasExistingInventoryData()` and `fileService.hasExistingPromotionalData()`. If the text files already exist and contain records, default seeding is skipped, and user data is loaded directly from disk. This ensures true idempotency and prevents data duplication.

---

## 7. Limitations & Future Enhancements

1. **Relational Database Migration**: Current persistence uses pipe-delimited text files. While lightweight, readable, and ideal for a CSE mini-project, migrating to an embedded SQL database (e.g. SQLite or H2 via JDBC) would provide ACID transactional integrity and relational foreign key constraints.
2. **Multi-User Concurrency**: The system is designed for single-user interactive console operation. Future iterations could add client-server networking (REST API or WebSockets) with concurrent thread synchronization.
3. **GUI / Web Dashboard**: Adding a JavaFX or React web interface would allow producers to visualize Gantt charts, budget burn-down graphs, and promotional poster previews interactively.
