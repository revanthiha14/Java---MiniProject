package com.cineflow.service;

import com.cineflow.model.CallSheet;
import com.cineflow.model.DistributionRelease;
import com.cineflow.model.DubbingTrack;
import com.cineflow.model.Expense;
import com.cineflow.model.InventoryItem;
import com.cineflow.model.ItemAssignment;
import com.cineflow.model.Movie;
import com.cineflow.model.PostProductionTask;
import com.cineflow.model.ProductionHouse;
import com.cineflow.model.ProductionMilestone;
import com.cineflow.model.PromotionalAsset;
import com.cineflow.model.Scene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service generating comprehensive production reports and executive dashboards.
 * Aggregates actual stored project data across finances, scenes, post-production,
 * dubbing, promotional marketing approvals, physical inventory, and schedule milestones.
 * Handles empty and partial data safely without hardcoded report values.
 */
public class ReportService {
    private final MovieManager movieManager;
    private final InventoryManager inventoryManager;
    private final PromotionManager promotionManager;

    public ReportService(MovieManager movieManager, InventoryManager inventoryManager, PromotionManager promotionManager) {
        this.movieManager = movieManager;
        this.inventoryManager = inventoryManager;
        this.promotionManager = promotionManager;
    }

    /**
     * Generates a comprehensive Executive Production Dashboard for the specified movie.
     */
    public String generateExecutiveDashboard(Movie movie) {
        if (movie == null) {
            return "================================================================================\n"
                 + "                     EXECUTIVE PRODUCTION DASHBOARD                     \n"
                 + "================================================================================\n"
                 + " [!] No active movie selected. Please select or add a movie to view dashboard.  \n"
                 + "================================================================================\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(String.format("          EXECUTIVE PRODUCTION DASHBOARD: %s\n", movie.getTitle().toUpperCase()));
        sb.append("================================================================================\n");
        sb.append(String.format(" Movie ID     : %-15s | Director    : %s\n", movie.getId(), movie.getDirector()));
        sb.append(String.format(" Genre        : %-15s | Release Year: %d\n", movie.getGenre(), movie.getReleaseYear()));
        sb.append(String.format(" Status       : %-15s | Screenplay  : %s\n", movie.getStatus(), movie.getScreenplayWriter()));
        sb.append("--------------------------------------------------------------------------------\n\n");

        // 1. FINANCIAL & BUDGET HEALTH
        double budget = movie.getBudget();
        double spent = movie.getTotalExpenses();
        double remaining = movie.getRemainingBudget();
        double spentPct = (budget > 0) ? (spent / budget) * 100.0 : 0.0;
        sb.append("1. FINANCIAL & BUDGET OVERVIEW\n");
        sb.append(String.format("   Allocated Budget : $%,14.2f\n", budget));
        sb.append(String.format("   Total Spent      : $%,14.2f (%.1f%% utilized)\n", spent, spentPct));
        sb.append(String.format("   Remaining Budget : $%,14.2f%s\n", remaining, (remaining < 0 ? " [BUDGET DEFICIT!]" : "")));
        sb.append(String.format("   Expense Entries  : %d logged\n\n", movie.getExpenses().size()));

        // 2. SCENE & SHOOTING PROGRESS
        List<Scene> scenes = movie.getScenes();
        int plannedScenes = 0, inProgScenes = 0, completedScenes = 0;
        for (Scene sc : scenes) {
            if ("COMPLETED".equalsIgnoreCase(sc.getStatus())) completedScenes++;
            else if ("IN_PROGRESS".equalsIgnoreCase(sc.getStatus())) inProgScenes++;
            else plannedScenes++;
        }
        double totalScriptPages = movie.getTotalScriptPages();
        double totalPagesShot = 0.0;
        for (CallSheet cs : movie.getCallSheets()) {
            totalPagesShot += cs.getPagesShot();
        }
        double shootingProgressPct = (totalScriptPages > 0)
                ? Math.min(100.0, (totalPagesShot / totalScriptPages) * 100.0) : 0.0;

        sb.append("2. SCENE & PRINCIPAL PHOTOGRAPHY PROGRESS\n");
        sb.append(String.format("   Total Scenes     : %d (Completed: %d | In-Progress: %d | Planned: %d)\n",
                scenes.size(), completedScenes, inProgScenes, plannedScenes));
        sb.append(String.format("   Script Length    : %.1f Pages (Est. Runtime: ~%.0f minutes)\n",
                totalScriptPages, movie.getEstimatedRuntimeMinutes()));
        sb.append(String.format("   Pages Shot (DSR) : %.1f of %.1f Pages (%.1f%% filmed)\n",
                totalPagesShot, totalScriptPages, shootingProgressPct));
        sb.append(String.format("   Daily Call Sheets: %d scheduled shooting days\n\n", movie.getCallSheets().size()));

        // 3. DUBBING & POST-PRODUCTION PIPELINE
        List<DubbingTrack> dubTracks = movie.getDubbingTracks();
        int approvedDub = movie.getApprovedDubbingTracksCount();
        int retakes = movie.getTotalRetakesCount();
        double postProg = movie.getAveragePostProductionProgress();
        int completedPostTasks = movie.getCompletedPostProductionTasksCount();
        int totalPostTasks = movie.getPostProductionTasks().size();

        sb.append("3. LOCALIZATION & POST-PRODUCTION PIPELINE\n");
        sb.append(String.format("   Voice Dubbing    : %d Tracks (Approved: %d/%d | Retakes Logged: %d)\n",
                dubTracks.size(), approvedDub, dubTracks.size(), retakes));
        sb.append(String.format("   Post-Production  : %.1f%% Completed (%d of %d Tasks Signed Off)\n",
                postProg, completedPostTasks, totalPostTasks));
        sb.append(String.format("   Progress Bar     : %s\n\n", renderBar(postProg)));

        // 4. PROMOTIONAL ASSETS & MARKETING APPROVALS
        List<PromotionalAsset> assets = (promotionManager != null)
                ? promotionManager.getAssetsForMovie(movie.getId()) : new ArrayList<>();
        int approvedAssets = 0, pendingAssets = 0, rejectedAssets = 0;
        for (PromotionalAsset a : assets) {
            if (a.isApproved()) approvedAssets++;
            else if (a.isPending()) pendingAssets++;
            else if (a.isRejected()) rejectedAssets++;
        }

        sb.append("4. PROMOTIONAL & MARKETING ASSET APPROVALS\n");
        sb.append(String.format("   Total Assets     : %d registered for %s\n", assets.size(), movie.getId()));
        sb.append(String.format("   Approval Status  : %d Approved | %d Pending Review | %d Rejected (Revisions Required)\n",
                approvedAssets, pendingAssets, rejectedAssets));
        if (pendingAssets > 0) {
            sb.append(String.format("   [!] ACTION NEEDED: %d promotional asset(s) waiting for management approval.\n", pendingAssets));
        }
        sb.append("\n");

        // 5. INVENTORY & EQUIPMENT STATUS
        List<ItemAssignment> assignments = (inventoryManager != null)
                ? inventoryManager.getAssignmentsForMovie(movie.getId()) : new ArrayList<>();
        int activeInUse = 0;
        for (ItemAssignment a : assignments) {
            if (a.isCheckedOut()) activeInUse += a.getQuantity();
        }
        int totalStudioItems = (inventoryManager != null) ? inventoryManager.getAllItems().size() : 0;
        int availUnits = (inventoryManager != null) ? inventoryManager.getAvailableUnitsCount() : 0;
        int damagedUnits = (inventoryManager != null) ? inventoryManager.getDamagedUnitsCount() : 0;
        int lostUnits = (inventoryManager != null) ? inventoryManager.getLostUnitsCount() : 0;

        sb.append("5. PROPS, COSTUMES & EQUIPMENT INVENTORY\n");
        sb.append(String.format("   Assigned to Film : %d checkout assignments (%d physical units currently on set)\n",
                assignments.size(), activeInUse));
        sb.append(String.format("   Global Inventory : %d catalog items | %d units available | %d damaged | %d lost\n\n",
                totalStudioItems, availUnits, damagedUnits, lostUnits));

        // 6. PRODUCTION MILESTONES, DELAYS & CHRONOLOGY
        List<ProductionMilestone> milestones = movie.getMasterSchedule();
        int completedMilestones = movie.getCompletedMilestonesCount();
        int delayedMilestones = 0;
        for (ProductionMilestone ms : milestones) {
            if ("DELAYED".equalsIgnoreCase(ms.getStatus())) delayedMilestones++;
        }
        List<String> warnings = movie.validateScheduleSequence();

        sb.append("6. PRODUCTION ROADMAP & MILESTONE HEALTH\n");
        sb.append(String.format("   Milestones Done  : %d of %d Completed (%.0f%%)\n",
                completedMilestones, milestones.size(),
                milestones.isEmpty() ? 0.0 : (completedMilestones * 100.0 / milestones.size())));
        sb.append(String.format("   Delayed Events   : %d milestones currently flagged as DELAYED\n", delayedMilestones));
        if (!warnings.isEmpty()) {
            sb.append(String.format("   [!] CHRONOLOGY WARNINGS: %d sequence conflict(s) detected in marketing/production dates!\n", warnings.size()));
        } else {
            sb.append("   [OK] Chronology Sequence: Valid industry workflow order intact.\n");
        }
        sb.append("\n");

        // 7. UPCOMING TARGET DATES
        sb.append("7. UPCOMING SHOOTING & RELEASE DATES\n");
        String nextShoot = findNextShootDate(movie);
        sb.append(String.format("   Next Shoot Date  : %s\n", nextShoot));
        sb.append(String.format("   Theatrical Date  : %s (%s, %s)\n",
                movie.getTheatricalReleaseDate(), movie.getTheatricalStatus(), movie.getReleaseRegion()));
        sb.append(String.format("   OTT Premiere     : %s (Platform: %s)\n",
                movie.getDigitalReleaseDate(), movie.getOttPlatform()));
        sb.append("================================================================================\n");

        return sb.toString();
    }

    /**
     * Generates a detailed analytical breakdown report across all production facets.
     */
    public String generateComprehensiveAnalyticalReport(Movie movie) {
        if (movie == null) {
            return "No active movie selected.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(generateExecutiveDashboard(movie));
        sb.append("\n");

        // SECTION A: DETAILED EXPENSE LEDGER
        sb.append("================================================================================\n");
        sb.append(" SECTION A: DETAILED BUDGET EXPENSE LEDGER\n");
        sb.append("================================================================================\n");
        List<Expense> expenses = movie.getExpenses();
        if (expenses.isEmpty()) {
            sb.append(" [No expenses recorded for this movie yet]\n");
        } else {
            sb.append(String.format(" %-10s | %-20s | %14s | %s\n", "EXPENSE ID", "CATEGORY", "AMOUNT", "DESCRIPTION"));
            sb.append("------------+----------------------+----------------+-----------------------------------\n");
            for (Expense exp : expenses) {
                sb.append(String.format(" %-10s | %-20s | $%,13.2f | %s\n",
                        exp.getId(), exp.getCategory(), exp.getAmount(), exp.getDescription()));
            }
            sb.append("------------+----------------------+----------------+-----------------------------------\n");
            sb.append(String.format(" TOTAL SPENT: $%,.2f | REMAINING BUDGET: $%,.2f\n",
                    movie.getTotalExpenses(), movie.getRemainingBudget()));
        }
        sb.append("\n");

        // SECTION B: DETAILED SCENE & SHOOTING BREAKDOWN
        sb.append("================================================================================\n");
        sb.append(" SECTION B: SCENE SCHEDULE & CALL SHEET EXECUTION\n");
        sb.append("================================================================================\n");
        List<Scene> scenes = movie.getScenes();
        if (scenes.isEmpty()) {
            sb.append(" [No scenes drafted for this movie yet]\n");
        } else {
            sb.append(String.format(" %-8s | %-26s | %-20s | %-12s | %-10s | %s\n",
                    "SCENE #", "TITLE", "LOCATION", "STATUS", "SHOOT DATE", "PAGES"));
            sb.append("---------+----------------------------+----------------------+--------------+------------+--------\n");
            for (Scene sc : scenes) {
                sb.append(String.format(" Scene #%-2d | %-26s | %-20s | %-12s | %-10s | %.1f pgs\n",
                        sc.getSceneNumber(), truncate(sc.getTitle(), 26), truncate(sc.getLocation(), 20),
                        sc.getStatus(), sc.getShootingDate(), sc.getScriptPages()));
            }
        }
        sb.append("\n");

        // SECTION C: PROMOTIONAL ASSETS DETAIL
        sb.append("================================================================================\n");
        sb.append(" SECTION C: PROMOTIONAL & MARKETING ASSETS QUEUE\n");
        sb.append("================================================================================\n");
        List<PromotionalAsset> assets = (promotionManager != null)
                ? promotionManager.getAssetsForMovie(movie.getId()) : new ArrayList<>();
        if (assets.isEmpty()) {
            sb.append(" [No promotional marketing assets registered for this movie yet]\n");
        } else {
            sb.append(String.format(" %-8s | %-12s | %-24s | %-12s | %-10s | %s\n",
                    "ASSET ID", "TYPE", "TITLE", "STATUS", "REVISION", "PLATFORM"));
            sb.append("---------+--------------+--------------------------+--------------+------------+---------------------\n");
            for (PromotionalAsset a : assets) {
                sb.append(String.format(" %-8s | %-12s | %-24s | %-12s | v%-9d | %s\n",
                        a.getAssetId(), a.getAssetType().name(), truncate(a.getTitle(), 24),
                        a.getApprovalStatus().name(), a.getRevisionNumber(), a.getPlatform()));
                if (a.getApprovalDetails() != null && !a.getApprovalDetails().isEmpty()) {
                    sb.append(String.format("          Notes: %s\n", a.getApprovalDetails()));
                }
            }
        }
        sb.append("\n");

        // SECTION D: INVENTORY ASSIGNMENTS DETAIL
        sb.append("================================================================================\n");
        sb.append(" SECTION D: ASSIGNED PROPS, COSTUMES & EQUIPMENT\n");
        sb.append("================================================================================\n");
        List<ItemAssignment> assignments = (inventoryManager != null)
                ? inventoryManager.getAssignmentsForMovie(movie.getId()) : new ArrayList<>();
        if (assignments.isEmpty()) {
            sb.append(" [No inventory items currently checked out to this movie]\n");
        } else {
            sb.append(String.format(" %-8s | %-8s | %-24s | %-10s | %-4s | %-12s | %s\n",
                    "ASG ID", "ITEM ID", "ITEM NAME", "TARGET", "QTY", "STATUS", "CHECKOUT DATE"));
            sb.append("---------+----------+--------------------------+------------+------+--------------+--------------\n");
            for (ItemAssignment asg : assignments) {
                InventoryItem itm = (inventoryManager != null) ? inventoryManager.findItemById(asg.getItemId()) : null;
                String itmName = (itm != null) ? itm.getName() : asg.getItemId();
                sb.append(String.format(" %-8s | %-8s | %-24s | %-10s | %-4d | %-12s | %s\n",
                        asg.getAssignmentId(), asg.getItemId(), truncate(itmName, 24),
                        asg.getSceneReference(), asg.getQuantity(), asg.getStatus(), asg.getAssignedDate()));
            }
        }
        sb.append("\n");

        // SECTION E: MILESTONES & DELAYS DETAIL
        sb.append("================================================================================\n");
        sb.append(" SECTION E: PRODUCTION ROADMAP, DELAYS & CHRONOLOGY\n");
        sb.append("================================================================================\n");
        for (ProductionMilestone ms : movie.getMasterSchedule()) {
            String delayFlag = "DELAYED".equalsIgnoreCase(ms.getStatus()) ? " *** DELAY ALERT ***" : "";
            sb.append(String.format(" %-6s | %-13s | %-12s | %-32s | Lead: %s%s\n",
                    ms.getId(), ms.getStatus(), ms.getTargetDate(), ms.getTitle(),
                    ms.getResponsibleLead(), delayFlag));
        }
        sb.append("================================================================================\n");

        return sb.toString();
    }

    /**
     * Generates a consolidated summary for an entire production studio.
     */
    public String generateStudioConsolidatedReport(ProductionHouse studio) {
        if (studio == null) {
            return "No production house selected.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append(String.format("       STUDIO PORTFOLIO PRODUCTION REPORT: %s\n", studio.getName().toUpperCase()));
        sb.append("================================================================================\n");
        sb.append(String.format(" Studio ID: %-15s | Headquarters: %s\n", studio.getId(), studio.getHeadquarters()));
        sb.append(String.format(" Total Catalog Movies: %d\n", studio.getMovies().size()));
        sb.append("--------------------------------------------------------------------------------\n\n");

        double totalBudget = 0.0;
        double totalSpent = 0.0;
        for (Movie m : studio.getMovies()) {
            totalBudget += m.getBudget();
            totalSpent += m.getTotalExpenses();
        }
        sb.append("STUDIO FINANCIAL SUMMARY:\n");
        sb.append(String.format(" Total Allocated Budget : $%,.2f\n", totalBudget));
        sb.append(String.format(" Total Capital Spent    : $%,.2f (%.1f%%)\n", totalSpent, (totalBudget > 0 ? (totalSpent / totalBudget) * 100.0 : 0.0)));
        sb.append(String.format(" Remaining Studio Funds : $%,.2f\n\n", totalBudget - totalSpent));

        sb.append(String.format(" %-8s | %-24s | %-12s | %-14s | %14s | %14s\n",
                "MOVIE ID", "TITLE", "STATUS", "RELEASE YEAR", "BUDGET", "SPENT"));
        sb.append("---------+--------------------------+--------------+----------------+----------------+----------------\n");
        for (Movie m : studio.getMovies()) {
            sb.append(String.format(" %-8s | %-24s | %-12s | %-14d | $%,13.2f | $%,13.2f\n",
                    m.getId(), truncate(m.getTitle(), 24), m.getStatus(), m.getReleaseYear(),
                    m.getBudget(), m.getTotalExpenses()));
        }
        sb.append("================================================================================\n");

        return sb.toString();
    }

    /**
     * Exports a comprehensive production report to a text file in data/reports/.
     */
    public File exportProductionReportToFile(Movie movie) throws IOException {
        if (movie == null) {
            throw new IOException("Cannot export report for null movie.");
        }
        File repDir = new File("data/reports");
        if (!repDir.exists()) {
            repDir.mkdirs();
        }

        String safeTitle = movie.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
        File targetFile = new File(repDir, movie.getId() + "_" + safeTitle + "_Production_Report.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(targetFile))) {
            pw.print(generateComprehensiveAnalyticalReport(movie));
        }
        return targetFile;
    }

    private String findNextShootDate(Movie movie) {
        String nextDate = "TBD";
        for (Scene sc : movie.getScenes()) {
            if ("PLANNED".equalsIgnoreCase(sc.getStatus()) || "IN_PROGRESS".equalsIgnoreCase(sc.getStatus())) {
                if (sc.getShootingDate() != null && !sc.getShootingDate().equalsIgnoreCase("TBD")) {
                    if (nextDate.equals("TBD") || sc.getShootingDate().compareTo(nextDate) < 0) {
                        nextDate = sc.getShootingDate() + " (Scene #" + sc.getSceneNumber() + ": " + sc.getTitle() + ")";
                    }
                }
            }
        }
        return nextDate;
    }

    private String renderBar(double percentage) {
        int totalBars = 20;
        int filled = (int) Math.round((Math.max(0, Math.min(100, percentage)) / 100.0) * totalBars);
        return "[" + "#".repeat(filled) + "-".repeat(totalBars - filled) + "] " + String.format("%.1f%%", percentage);
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max - 3) + "...";
    }
}
