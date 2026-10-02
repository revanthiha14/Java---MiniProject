package com.cineflow;

import com.cineflow.model.CallSheet;
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
 * Main application entrypoint for CineFlow - Movie Production Management System.
 * Coordinates system initialization, dependency injection, sample data seeding,
 * and launches the interactive terminal interface.
 */
public class Main {
    public static void main(String[] args) {
        // Ensure data storage directory exists
        new File("data").mkdirs();

        // Initialize Generic Repositories
        Repository<Scene, String> sceneRepo = new FileRepository<>();
        Repository<Person, String> personRepo = new FileRepository<>();
        Repository<ProductionAsset, String> assetRepo = new FileRepository<>();
        Repository<CallSheet, String> callSheetRepo = new FileRepository<>();

        // Initialize Services
        BudgetService budgetService = new BudgetService();
        ProductionService productionService = new ProductionService(sceneRepo, callSheetRepo);
        PersonnelService personnelService = new PersonnelService(personRepo);
        AssetService assetService = new AssetService(assetRepo);

        // Pre-seed realistic Sci-Fi production: "Chronicles of Aether"
        DataGenerator.seedProductionData(productionService, personnelService, assetService, budgetService);

        // Launch UI Menu Controller
        MenuController controller = new MenuController(
                productionService, personnelService, assetService, budgetService);
        controller.start();
    }
}
