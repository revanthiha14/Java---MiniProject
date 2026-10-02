package com.cineflow;

import com.cineflow.model.CallSheet;
import com.cineflow.model.Scene;
import com.cineflow.model.asset.ProductionAsset;
import com.cineflow.model.personnel.Person;
import com.cineflow.repository.FileRepository;
import com.cineflow.repository.Repository;
import com.cineflow.service.AssetService;
import com.cineflow.service.BudgetService;
import com.cineflow.service.FilePersistenceService;
import com.cineflow.service.PersonnelService;
import com.cineflow.service.ProductionService;
import com.cineflow.service.StudioService;
import com.cineflow.ui.MenuController;
import com.cineflow.util.DataGenerator;
import java.io.File;

/**
 * Main application entrypoint for CineFlow 2.0 - Cinema Production Management & Studio Ecosystem.
 * Coordinates system initialization, multi-production house management, human-readable
 * text-file persistence (data/*.txt & data/scripts/), and launches the interactive terminal interface.
 */
public class Main {
    public static void main(String[] args) {
        // Enable UTF-8 console output for clean rendering across all Windows terminals
        try {
            System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
            System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ignored) {}

        // Ensure data storage directory exists
        new File("data").mkdirs();
        new File("data/scripts").mkdirs();

        // Initialize Central Studio Registry & Persistence Service
        StudioService studioService = new StudioService();
        FilePersistenceService filePersistenceService = new FilePersistenceService("data");

        // Initialize Generic Repositories (Repository<T, ID>)
        Repository<Scene, String> sceneRepo = new FileRepository<>();
        Repository<Person, String> personRepo = new FileRepository<>();
        Repository<ProductionAsset, String> assetRepo = new FileRepository<>();
        Repository<CallSheet, String> callSheetRepo = new FileRepository<>();

        // Initialize Services
        BudgetService defaultBudgetService = new BudgetService();
        ProductionService productionService = new ProductionService(sceneRepo, callSheetRepo);
        PersonnelService personnelService = new PersonnelService(personRepo);
        AssetService assetService = new AssetService(assetRepo);

        // Always seed central personnel (Directors, Actors, Crew) and assets (Equipment, Locations)
        DataGenerator.seedCentralPersonnelAndAssets(personnelService, assetService);

        // Load data from human-readable text files if already saved; otherwise initialize fresh catalog and save
        try {
            if (filePersistenceService.isDataPersisted()) {
                filePersistenceService.loadAllData(studioService, productionService);
            } else {
                DataGenerator.seedAllProductionHouses(studioService, productionService, personnelService, assetService, defaultBudgetService);
                filePersistenceService.saveAllData(studioService, productionService);
            }
        } catch (Exception e) {
            System.err.println("Notice: Initializing fresh studio data (" + e.getMessage() + ")");
            DataGenerator.seedAllProductionHouses(studioService, productionService, personnelService, assetService, defaultBudgetService);
            try {
                filePersistenceService.saveAllData(studioService, productionService);
            } catch (Exception ignored) {}
        }

        // Launch UI Menu Controller with Multi-Production House Architecture
        MenuController controller = new MenuController(
                studioService, filePersistenceService, productionService, personnelService, assetService, defaultBudgetService);
        controller.start();
    }
}
