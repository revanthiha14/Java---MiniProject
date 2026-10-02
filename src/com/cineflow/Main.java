package com.cineflow;

import com.cineflow.model.CallSheet;
import com.cineflow.model.ProductionHouse;
import com.cineflow.model.Scene;
import com.cineflow.model.asset.ProductionAsset;
import com.cineflow.model.personnel.Person;
import com.cineflow.repository.FileRepository;
import com.cineflow.repository.Repository;
import com.cineflow.service.AssetService;
import com.cineflow.service.BudgetService;
import com.cineflow.service.PersonnelService;
import com.cineflow.service.ProductionService;
import com.cineflow.ui.MenuController;
import com.cineflow.util.DataGenerator;
import java.io.File;

/**
 * Main application entrypoint for CineFlow 2.0 - Multi-Movie Production House System.
 * Coordinates system initialization, dependency injection, multi-movie studio seeding,
 * and launches the interactive terminal interface.
 */
public class Main {
    public static void main(String[] args) {
        // Enable UTF-8 console output
        try {
            System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
            System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ignored) {}

        // Ensure data storage directory exists
        new File("data").mkdirs();

        // Initialize Production House (Studio)
        ProductionHouse productionHouse = new ProductionHouse("PH-101", "Horizon Studios", "Los Angeles & Mumbai", 2005);

        // Initialize Generic Repositories
        Repository<Scene, String> sceneRepo = new FileRepository<>();
        Repository<Person, String> personRepo = new FileRepository<>();
        Repository<ProductionAsset, String> assetRepo = new FileRepository<>();
        Repository<CallSheet, String> callSheetRepo = new FileRepository<>();

        // Initialize Services
        BudgetService defaultBudgetService = new BudgetService();
        ProductionService productionService = new ProductionService(sceneRepo, callSheetRepo);
        PersonnelService personnelService = new PersonnelService(personRepo);
        AssetService assetService = new AssetService(assetRepo);

        // Pre-seed realistic multi-movie catalog across Pre-Production, In-Production, Post-Production
        DataGenerator.seedProductionData(productionHouse, productionService, personnelService, assetService, defaultBudgetService);

        // Launch UI Menu Controller with Multi-Movie Studio Architecture
        MenuController controller = new MenuController(
                productionHouse, productionService, personnelService, assetService, defaultBudgetService);
        controller.start();
    }
}
