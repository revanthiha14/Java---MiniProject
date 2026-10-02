package com.cineflow.util;

import com.cineflow.exception.BudgetExceededException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.CallSheet;
import com.cineflow.model.DaylightRequirement;
import com.cineflow.model.Department;
import com.cineflow.model.Movie;
import com.cineflow.model.ProductionHouse;
import com.cineflow.model.ProductionPhase;
import com.cineflow.model.Scene;
import com.cineflow.model.SceneStatus;
import com.cineflow.model.asset.Equipment;
import com.cineflow.model.asset.Equipment.EquipmentCategory;
import com.cineflow.model.asset.Location;
import com.cineflow.model.asset.Location.LocationType;
import com.cineflow.model.personnel.Actor;
import com.cineflow.model.personnel.CrewMember;
import com.cineflow.model.personnel.Director;
import com.cineflow.service.AssetService;
import com.cineflow.service.BudgetService;
import com.cineflow.service.PersonnelService;
import com.cineflow.service.ProductionService;
import java.time.LocalDate;

/**
 * Pre-seeds realistic multi-movie production data for the film studio:
 * "Horizon Studios" (Multi-Project Studio).
 *
 * Pre-seeded Movies:
 *  1. Interstellar Journey (MOV-01) - Sci-Fi | In-Production / Shooting
 *  2. Shadows of the Past (MOV-02) - Psychological Thriller | Pre-Production / Casting
 *  3. The Royal Heritage (MOV-03) - Historical Drama | Post-Production / Editing
 */
public final class DataGenerator {

    private DataGenerator() {}

    public static void seedProductionData(ProductionService productionService,
                                         PersonnelService personnelService,
                                         AssetService assetService,
                                         BudgetService budgetService) {
        ProductionHouse defaultStudio = new ProductionHouse("PH-101", "Horizon Studios", "Los Angeles & Mumbai", 2005);
        seedProductionData(defaultStudio, productionService, personnelService, assetService, budgetService);
    }

    public static void seedProductionData(ProductionHouse studio,
                                         ProductionService productionService,
                                         PersonnelService personnelService,
                                         AssetService assetService,
                                         BudgetService budgetService) {
        try {
            // =================================================================
            // 1. Seed Studio Central Personnel (Cast & Crew Talent Pool)
            // =================================================================
            Director dir1 = new Director("DIR-101", "Marcus Sterling", 4200.0,
                    "Grounded science fiction with tactile realism and deep emotional stakes", 3.0, 75000.0);
            dir1.recordDaysWorked(12);
            personnelService.registerPerson(dir1);

            Director dir2 = new Director("DIR-102", "Evelyn Blackwood", 3800.0,
                    "Atmospheric psychological thrillers with high-contrast shadows and tension", 2.5, 50000.0);
            dir2.recordDaysWorked(8);
            personnelService.registerPerson(dir2);

            Director dir3 = new Director("DIR-103", "Arthur Pendelton", 4500.0,
                    "Grand historical period epics with authentic costuming and orchestral scale", 3.5, 90000.0);
            dir3.recordDaysWorked(15);
            personnelService.registerPerson(dir3);

            // Actors
            Actor leadActor = new Actor("ACT-201", "Aiden Cross", 3500.0, "Commander Jax Vance", 1);
            leadActor.addSkill("Martial Arts Combat");
            leadActor.addSkill("Stunt Wirework");
            leadActor.addSkill("Acoustic Guitar");
            leadActor.setStuntQualified(true);
            leadActor.recordDaysWorked(8);
            personnelService.registerPerson(leadActor);

            Actor coLead = new Actor("ACT-202", "Dr. Lyra Thorne", 3000.0, "Astrophysicist Lyra Thorne", 2);
            coLead.addSkill("Fluent French");
            coLead.addSkill("Scuba Diving");
            coLead.recordDaysWorked(6);
            personnelService.registerPerson(coLead);

            Actor villain = new Actor("ACT-203", "Victor Kael", 2800.0, "Governor Malakor", 3);
            villain.addSkill("Stage Fencing");
            villain.addSkill("Horseback Riding");
            villain.recordDaysWorked(4);
            personnelService.registerPerson(villain);

            Actor supporting = new Actor("ACT-204", "Maya Lin", 1800.0, "Pilot Cadet Kira", 4);
            supporting.addSkill("Aerobatic Flight Maneuvers");
            supporting.recordDaysWorked(5);
            personnelService.registerPerson(supporting);

            // Technical Crew Members
            CrewMember dp = new CrewMember("CRW-301", "Elena Vance", 2200.0, Department.CAMERA, "Director of Photography");
            dp.addCertification("ARRI Certified Cinematographer");
            dp.addCertification("IMAX 70mm Specialist");
            dp.recordDaysWorked(10);
            personnelService.registerPerson(dp);

            CrewMember gaffer = new CrewMember("CRW-302", "Darius Cole", 1400.0, Department.LIGHTING, "Chief Lighting Technician / Gaffer");
            gaffer.addCertification("High-Voltage Safety Certified");
            gaffer.recordDaysWorked(10);
            personnelService.registerPerson(gaffer);

            CrewMember sound = new CrewMember("CRW-303", "Siddharth Rao", 1300.0, Department.SOUND, "Production Sound Mixer");
            sound.addCertification("Dante Level 3 Audio");
            sound.recordDaysWorked(8);
            personnelService.registerPerson(sound);

            CrewMember stuntCoord = new CrewMember("CRW-304", "Rory MacLeod", 1600.0, Department.STUNTS, "Stunt Coordinator");
            stuntCoord.addCertification("SAG-AFTRA Stunt Safety Certified");
            stuntCoord.addCertification("High-Fall Wire Rigging");
            stuntCoord.recordDaysWorked(5);
            personnelService.registerPerson(stuntCoord);

            CrewMember vfxSup = new CrewMember("CRW-305", "Chloe Bennett", 1900.0, Department.VFX_AND_POST, "On-Set VFX Supervisor");
            vfxSup.addCertification("Unreal Engine Virtual Production");
            vfxSup.recordDaysWorked(6);
            personnelService.registerPerson(vfxSup);

            // =================================================================
            // 2. Seed Studio Assets (Equipment & Locations Pool)
            // =================================================================
            Equipment camA = new Equipment("EQ-401", "ARRI Alexa 35 Camera Package", 1200.0, "SN-ARRI-9921",
                    EquipmentCategory.CAMERA_BODY, "PRISTINE", true);
            camA.bookDays(8);
            assetService.registerAsset(camA);

            Equipment lenses = new Equipment("EQ-402", "Cooke Anamorphic /i Full Frame Plus Set", 950.0, "SN-CK-4018",
                    EquipmentCategory.ANAMORPHIC_LENS, "EXCELLENT", true);
            lenses.bookDays(8);
            assetService.registerAsset(lenses);

            Equipment soundRig = new Equipment("EQ-403", "Sound Devices 888 16-Channel Mixer/Recorder", 350.0, "SN-SD-7714",
                    EquipmentCategory.SOUND_RIG, "EXCELLENT", false);
            soundRig.bookDays(8);
            assetService.registerAsset(soundRig);

            Equipment skypanel = new Equipment("EQ-404", "ARRI SkyPanel S360-C Soft LED Light Grid", 650.0, "SN-SK-1120",
                    EquipmentCategory.LIGHTING_KIT, "GOOD", true);
            skypanel.bookDays(6);
            assetService.registerAsset(skypanel);

            Location stage = new Location("LOC-501", "Apex Soundstage 4 (Virtual LED Volume)", 4500.0,
                    "700 Studio Blvd", "Culver City", LocationType.INDOOR_STUDIO, true, 120, 1500.0);
            stage.bookDays(10);
            assetService.registerAsset(stage);

            Location crater = new Location("LOC-502", "Black Rock Desert Alien Outpost", 2200.0,
                    "Playa Highway 34", "Gerlach", LocationType.OUTDOOR_NATURAL, true, 80, 2500.0);
            crater.bookDays(4);
            assetService.registerAsset(crater);

            Location citadel = new Location("LOC-503", "Neo-Brutalist Concrete Citadel", 3200.0,
                    "88 Harbor Promenade", "Seattle", LocationType.URBAN_STREET, true, 90, 3000.0);
            citadel.bookDays(3);
            assetService.registerAsset(citadel);

            LocalDate today = LocalDate.now();

            // =================================================================
            // 3. MOVIE 1: Interstellar Journey (In-Production / Shooting)
            // =================================================================
            Movie m1 = new Movie("MOV-01", "Interstellar Journey", "Sci-Fi", "Marcus Sterling",
                    ProductionPhase.PRODUCTION, 2026);
            BudgetService b1 = m1.getBudgetService();
            // Seed Budgets across all departments (Total: $2,500,000)
            b1.allocateBudget(Department.DIRECTING, 350000.0, "Director fees, creative consultants");
            b1.allocateBudget(Department.CAMERA, 320000.0, "Cinematography package & DP contracts");
            b1.allocateBudget(Department.LIGHTING, 180000.0, "Gaffer team, lighting grids, generators");
            b1.allocateBudget(Department.SOUND, 140000.0, "Sound mixer, boom ops, foley capture");
            b1.allocateBudget(Department.ART_AND_PROPS, 400000.0, "Spacecraft sets, practical alien props");
            b1.allocateBudget(Department.COSTUME_AND_MAKEUP, 220000.0, "Exosuit wardrobes, prosthetic makeup");
            b1.allocateBudget(Department.VFX_AND_POST, 600000.0, "CGI rendering, volume wall, editing");
            b1.allocateBudget(Department.STUNTS, 150000.0, "Zero-G wire rigging & stunt coordinators");
            b1.allocateBudget(Department.LOGISTICS_AND_CATERING, 140000.0, "Location transport, cast catering");

            // Seed initial expenses
            b1.logExpense(Department.ART_AND_PROPS, 45000.0, "Cockpit flight console fabrication", "Art Director");
            b1.logExpense(Department.CAMERA, 28000.0, "High-speed camera sensor rental deposit", "DP Elena Vance");
            b1.logExpense(Department.COSTUME_AND_MAKEUP, 15000.0, "Commander hero spacesuit fitting", "Wardrobe Lead");

            // Sync with default budgetService parameter if provided
            if (budgetService != null && budgetService != b1) {
                for (Department d : Department.values()) {
                    double alloc = b1.getAllocatedForDepartment(d);
                    if (alloc > 0) budgetService.allocateBudget(d, alloc);
                }
                budgetService.logExpense(Department.ART_AND_PROPS, 45000.0, "Cockpit flight console fabrication", "Art Director");
                budgetService.logExpense(Department.CAMERA, 28000.0, "High-speed camera sensor rental deposit", "DP Elena Vance");
                budgetService.logExpense(Department.COSTUME_AND_MAKEUP, 15000.0, "Commander hero spacesuit fitting", "Wardrobe Lead");
            }

            // Scenes for Movie 1
            Scene s1 = new Scene("SCN-01", "MOV-01", 1, "The Discovery at Station 9",
                    "Commander Jax and Dr. Thorne inspect the anomalous crystalline signal in deep orbit.",
                    4.5, DaylightRequirement.INTERIOR_STUDIO, 2, 7.0);
            s1.assignActors("ACT-201", "ACT-202");
            s1.assignEquipment("EQ-401", "EQ-402", "EQ-403");
            s1.setLocationId("LOC-501");
            s1.setScheduledDate(today.plusDays(1));
            s1.setShootTimeSlot("07:00 - 14:00");
            s1.setStatus(SceneStatus.SCHEDULED);

            Scene s2 = new Scene("SCN-02", "MOV-01", 2, "Desert Rover Ambush",
                    "Malakor's scavengers intercept the transport crawler during high noon desert crossing.",
                    6.0, DaylightRequirement.DAY_EXTERIOR, 1, 9.5);
            s2.assignActors("ACT-201", "ACT-203", "ACT-204");
            s2.assignEquipment("EQ-401", "EQ-404");
            s2.setLocationId("LOC-502");
            s2.setScheduledDate(today.plusDays(3));
            s2.setShootTimeSlot("08:00 - 17:30");
            s2.setStatus(SceneStatus.SCHEDULED);

            Scene s3 = new Scene("SCN-03", "MOV-01", 3, "Twilight Rooftop Confrontation",
                    "Jax faces Governor Malakor atop the Citadel spires as the twin suns set.",
                    3.0, DaylightRequirement.GOLDEN_HOUR, 1, 3.5);
            s3.assignActors("ACT-201", "ACT-203");
            s3.assignEquipment("EQ-401", "EQ-402");
            s3.setLocationId("LOC-503");
            s3.setScheduledDate(today.plusDays(5));
            s3.setShootTimeSlot("17:00 - 20:00");
            s3.setStatus(SceneStatus.SCHEDULED);

            Scene s4 = new Scene("SCN-04", "MOV-01", 4, "Cockpit Zero-G Emergency Descent",
                    "Pilot Kira maneuvers the battered vessel through planetary atmospheric turbulence.",
                    5.0, DaylightRequirement.INTERIOR_STUDIO, 3, 6.0);
            s4.assignActors("ACT-201", "ACT-204");
            s4.assignEquipment("EQ-401", "EQ-403");
            s4.setLocationId("LOC-501");
            s4.setStatus(SceneStatus.DRAFT);

            Scene s5 = new Scene("SCN-05", "MOV-01", 5, "Night Perimeter Infiltration",
                    "Dr. Thorne hacks the subterranean power array under cover of nocturnal blizzard.",
                    4.0, DaylightRequirement.NIGHT_EXTERIOR, 2, 6.5);
            s5.assignActors("ACT-202");
            s5.assignEquipment("EQ-401", "EQ-404");
            s5.setLocationId("LOC-503");
            s5.setStatus(SceneStatus.DRAFT);

            Scene s6 = new Scene("SCN-06", "MOV-01", 6, "Prologue: Galactic Council Briefing",
                    "Archival transmission initiating the Aether expedition.",
                    2.0, DaylightRequirement.INTERIOR_STUDIO, 4, 3.0);
            s6.assignActors("ACT-203");
            s6.setLocationId("LOC-501");
            s6.setStatus(SceneStatus.COMPLETED);

            // Register scenes with Movie 1 and ProductionService
            m1.addScenes(s1, s2, s3, s4, s5, s6);
            m1.assignActors("ACT-201", "ACT-202", "ACT-203", "ACT-204");
            m1.assignCrew("CRW-301", "CRW-302", "CRW-303", "CRW-304", "CRW-305");
            m1.bookEquipment("EQ-401", "EQ-402", "EQ-403", "EQ-404");
            m1.bookLocations("LOC-501", "LOC-502", "LOC-503");

            productionService.addScene(s1);
            productionService.addScene(s2);
            productionService.addScene(s3);
            productionService.addScene(s4);
            productionService.addScene(s5);
            productionService.addScene(s6);

            // Call Sheet for Movie 1
            CallSheet cs1 = new CallSheet("CS-01", "MOV-01", 1, today.plusDays(1), "06:30 AM",
                    "Apex Soundstage 4, Stage Floor B", "Interior Stage (Controlled temp 20°C)",
                    "Culver City Emergency Center, 4200 Overland Ave (Ph: 310-836-7000)");
            cs1.addScene(s1);
            cs1.setCastCallTime("Aiden Cross (Jax)", "06:45 AM (Hair & Prosthetics)");
            cs1.setCastCallTime("Dr. Lyra Thorne (Lyra)", "07:15 AM (Wardrobe)");
            cs1.setDepartmentCallTime(Department.CAMERA, "06:00 AM (Camera Prep & Calibration)");
            cs1.setDepartmentCallTime(Department.LIGHTING, "05:30 AM (Volume Wall Power-up)");
            cs1.setDepartmentCallTime(Department.SOUND, "06:30 AM (Wireless Mics & Boom)");

            m1.addCallSheet(cs1);
            productionService.registerCallSheet(cs1);

            // =================================================================
            // 4. MOVIE 2: Shadows of the Past (Pre-Production / Casting)
            // =================================================================
            Movie m2 = new Movie("MOV-02", "Shadows of the Past", "Psychological Thriller", "Evelyn Blackwood",
                    ProductionPhase.PRE_PRODUCTION, 2027);
            BudgetService b2 = m2.getBudgetService();
            b2.allocateBudget(Department.DIRECTING, 180000.0, "Director fees, casting directors");
            b2.allocateBudget(Department.CAMERA, 160000.0, "Vintage prime lenses & anamorphic package");
            b2.allocateBudget(Department.SOUND, 90000.0, "Atmospheric sound design & score composition");
            b2.allocateBudget(Department.ART_AND_PROPS, 210000.0, "Neo-noir detective office & safehouse");
            b2.allocateBudget(Department.COSTUME_AND_MAKEUP, 110000.0, "Period wardrobe and trench coats");
            b2.allocateBudget(Department.LOGISTICS_AND_CATERING, 90000.0, "City permit logistics and crew vans");
            b2.allocateBudget(Department.VFX_AND_POST, 160000.0, "Digital rain enhancements and grade");

            b2.logExpense(Department.DIRECTING, 12000.0, "Screenplay adaptation rights", "Producer");
            b2.logExpense(Department.ART_AND_PROPS, 8500.0, "Noir detective office set design blueprint", "Production Designer");

            Scene s201 = new Scene("SCN-201", "MOV-02", 1, "Rainy Harbor Detective Meet",
                    "Detective Vance meets an anonymous whistleblower at dock pier 14 under rain.",
                    3.5, DaylightRequirement.NIGHT_EXTERIOR, 2, 5.0);
            s201.assignActors("ACT-202");
            s201.assignEquipment("EQ-401", "EQ-403");
            s201.setLocationId("LOC-503");
            s201.setStatus(SceneStatus.DRAFT);

            Scene s202 = new Scene("SCN-202", "MOV-02", 2, "Interrogation Room Silence",
                    "A intense psychological duel between the chief investigator and prime suspect.",
                    4.0, DaylightRequirement.INTERIOR_STUDIO, 1, 6.0);
            s202.assignActors("ACT-202", "ACT-203");
            s202.assignEquipment("EQ-401");
            s202.setLocationId("LOC-501");
            s202.setScheduledDate(today.plusDays(10));
            s202.setShootTimeSlot("09:00 - 16:00");
            s202.setStatus(SceneStatus.SCHEDULED);

            Scene s203 = new Scene("SCN-203", "MOV-02", 3, "Lake House Flashback",
                    "Memory sequence revealing the crucial betrayal that sparked the conspiracy.",
                    2.5, DaylightRequirement.GOLDEN_HOUR, 1, 3.0);
            s203.assignActors("ACT-202");
            s203.setLocationId("LOC-502");
            s203.setStatus(SceneStatus.DRAFT);

            Scene s204 = new Scene("SCN-204", "MOV-02", 4, "Subway Platform Chase",
                    "Paced foot pursuit through steam-filled underground commuter platform.",
                    5.0, DaylightRequirement.INTERIOR_STUDIO, 3, 7.0);
            s204.assignActors("ACT-203");
            s204.assignEquipment("EQ-401", "EQ-403");
            s204.setLocationId("LOC-501");
            s204.setStatus(SceneStatus.DRAFT);

            m2.addScenes(s201, s202, s203, s204);
            m2.assignActors("ACT-202", "ACT-203");
            m2.assignCrew("CRW-301", "CRW-303");
            m2.bookEquipment("EQ-401", "EQ-403");
            m2.bookLocations("LOC-501", "LOC-503");

            productionService.addScene(s201);
            productionService.addScene(s202);
            productionService.addScene(s203);
            productionService.addScene(s204);

            // =================================================================
            // 5. MOVIE 3: The Royal Heritage (Post-Production / Editing)
            // =================================================================
            Movie m3 = new Movie("MOV-03", "The Royal Heritage", "Historical Drama", "Arthur Pendelton",
                    ProductionPhase.POST_PRODUCTION, 2026);
            BudgetService b3 = m3.getBudgetService();
            b3.allocateBudget(Department.DIRECTING, 450000.0, "Director and historical consultant fees");
            b3.allocateBudget(Department.CAMERA, 400000.0, "65mm large format camera rentals & DP");
            b3.allocateBudget(Department.LIGHTING, 220000.0, "Candlelight simulation rigs and arc lights");
            b3.allocateBudget(Department.SOUND, 200000.0, "Symphony orchestra recording and foley");
            b3.allocateBudget(Department.ART_AND_PROPS, 650000.0, "Throne room sets and coronation props");
            b3.allocateBudget(Department.COSTUME_AND_MAKEUP, 580000.0, "Handwoven royal court attire");
            b3.allocateBudget(Department.VFX_AND_POST, 900000.0, "Post-production editing and crowd replication");
            b3.allocateBudget(Department.LOGISTICS_AND_CATERING, 200000.0, "Estate castle transport and catering");

            b3.logExpense(Department.COSTUME_AND_MAKEUP, 480000.0, "18th century handwoven royal attire", "Lead Costumer");
            b3.logExpense(Department.CAMERA, 350000.0, "65mm large format camera rentals", "DP Elena Vance");
            b3.logExpense(Department.VFX_AND_POST, 720000.0, "Orchestral foley & color grading suite", "Chloe Bennett");

            // All scenes completed for Post-Production status
            Scene s301 = new Scene("SCN-301", "MOV-03", 1, "Coronation Banquet",
                    "The grand crowning ceremony inside the cathedral with royal nobility.",
                    6.0, DaylightRequirement.INTERIOR_STUDIO, 2, 8.0);
            s301.assignActors("ACT-201", "ACT-203");
            s301.setLocationId("LOC-501");
            s301.setStatus(SceneStatus.COMPLETED);

            Scene s302 = new Scene("SCN-302", "MOV-03", 2, "Battle of Ashford Fields",
                    "Decisive medieval clash between the royal guard and rebel battalions.",
                    8.5, DaylightRequirement.DAY_EXTERIOR, 1, 12.0);
            s302.assignActors("ACT-201", "ACT-203", "ACT-204");
            s302.setLocationId("LOC-502");
            s302.setStatus(SceneStatus.COMPLETED);

            Scene s303 = new Scene("SCN-303", "MOV-03", 3, "Council Chamber Treason",
                    "Secret betrayal unveiled during late evening royal cabinet deliberations.",
                    4.0, DaylightRequirement.INTERIOR_STUDIO, 3, 6.0);
            s303.assignActors("ACT-203");
            s303.setLocationId("LOC-501");
            s303.setStatus(SceneStatus.COMPLETED);

            Scene s304 = new Scene("SCN-304", "MOV-03", 4, "Final Farewell at Sea Port",
                    "The exiled prince boards the royal frigate at sunset as the crowd watches.",
                    3.0, DaylightRequirement.GOLDEN_HOUR, 1, 4.0);
            s304.assignActors("ACT-201", "ACT-204");
            s304.setLocationId("LOC-503");
            s304.setStatus(SceneStatus.COMPLETED);

            m3.addScenes(s301, s302, s303, s304);
            m3.assignActors("ACT-201", "ACT-203", "ACT-204");
            m3.assignCrew("CRW-301", "CRW-302", "CRW-304", "CRW-305");
            m3.bookEquipment("EQ-401", "EQ-402", "EQ-404");
            m3.bookLocations("LOC-501", "LOC-502", "LOC-503");

            productionService.addScene(s301);
            productionService.addScene(s302);
            productionService.addScene(s303);
            productionService.addScene(s304);

            // Register all 3 movies with Studio
            studio.addMovie(m1);
            studio.addMovie(m2);
            studio.addMovie(m3);

            // Set active movie to Interstellar Journey
            studio.setActiveMovie(m1);
            productionService.setActiveMovieId(m1.getId());

        } catch (ValidationException | BudgetExceededException e) {
            System.err.println("Warning while seeding data: " + e.getMessage());
        }
    }
}
