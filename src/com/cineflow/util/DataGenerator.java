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
import com.cineflow.service.StudioService;
import java.time.LocalDate;

/**
 * Pre-seeds realistic multi-studio production data across 5 prominent production houses
 * and 11 feature films, complete with script screenplay dialogues and department budgets.
 */
public final class DataGenerator {

    private DataGenerator() {}

    public static void seedProductionData(ProductionService productionService,
                                         PersonnelService personnelService,
                                         AssetService assetService,
                                         BudgetService budgetService) {
        StudioService studioService = new StudioService();
        seedAllProductionHouses(studioService, productionService, personnelService, assetService, budgetService);
    }

    public static void seedProductionData(ProductionHouse studio,
                                         ProductionService productionService,
                                         PersonnelService personnelService,
                                         AssetService assetService,
                                         BudgetService budgetService) {
        StudioService studioService = new StudioService();
        studioService.registerStudio(studio);
        seedAllProductionHouses(studioService, productionService, personnelService, assetService, budgetService);
    }

    public static void seedCentralPersonnelAndAssets(PersonnelService personnelService,
                                                    AssetService assetService) {
        if (!personnelService.getAllPersonnel().isEmpty() && !assetService.getAllAssets().isEmpty()) {
            return;
        }

        try {
            // =================================================================
            // 1. CENTRAL PERSONNEL POOL (Directors, Actors, Technical Crew)
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

            Director dir4 = new Director("DIR-104", "Christopher Nolan", 5500.0,
                    "Non-linear cinematic puzzles filmed with IMAX practical pyrotechnics", 5.0, 150000.0);
            dir4.recordDaysWorked(20);
            personnelService.registerPerson(dir4);

            Director dir5 = new Director("DIR-105", "Sukumar", 5000.0,
                    "Gritty rustic folklore epics with stylized character heroism and rustic depth", 4.0, 120000.0);
            dir5.recordDaysWorked(18);
            personnelService.registerPerson(dir5);

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

            Actor starActor = new Actor("ACT-205", "Allu Arjun", 6000.0, "Pushpa Raj", 1);
            starActor.addSkill("High-Octane Action Choreography");
            starActor.addSkill("Rural Dialect Mastery");
            starActor.setStuntQualified(true);
            starActor.recordDaysWorked(14);
            personnelService.registerPerson(starActor);

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
            // 2. CENTRAL ASSETS POOL (Equipment & Locations)
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
        } catch (ValidationException e) {
            System.err.println("Notice seeding personnel/assets: " + e.getMessage());
        }
    }

    public static void seedAllProductionHouses(StudioService studioService,
                                             ProductionService productionService,
                                             PersonnelService personnelService,
                                             AssetService assetService,
                                             BudgetService defaultBudgetService) {
        try {
            LocalDate today = LocalDate.now();

            // Seed central personnel pool and equipment/locations
            seedCentralPersonnelAndAssets(personnelService, assetService);

            // =================================================================
            // 3. STUDIO 1: HORIZON STUDIOS (PH-101)
            // =================================================================
            ProductionHouse s1 = new ProductionHouse("PH-101", "Horizon Studios", "Los Angeles & Mumbai", 2005);

            // MOV-01: Interstellar Journey
            Movie m1 = new Movie("MOV-01", "PH-101", "Interstellar Journey", "Sci-Fi", "Marcus Sterling",
                    ProductionPhase.PRODUCTION, 2026);
            BudgetService b1 = m1.getBudgetService();
            b1.allocateBudget(Department.DIRECTING, 350000.0, "Director fees, creative consultants");
            b1.allocateBudget(Department.CAMERA, 320000.0, "Cinematography package & DP contracts");
            b1.allocateBudget(Department.LIGHTING, 180000.0, "Gaffer team, lighting grids, generators");
            b1.allocateBudget(Department.SOUND, 140000.0, "Sound mixer, boom ops, foley capture");
            b1.allocateBudget(Department.ART_AND_PROPS, 400000.0, "Spacecraft sets, practical alien props");
            b1.allocateBudget(Department.COSTUME_AND_MAKEUP, 220000.0, "Exosuit wardrobes, prosthetic makeup");
            b1.allocateBudget(Department.VFX_AND_POST, 600000.0, "CGI rendering, volume wall, editing");
            b1.allocateBudget(Department.STUNTS, 150000.0, "Zero-G wire rigging & stunt coordinators");
            b1.allocateBudget(Department.LOGISTICS_AND_CATERING, 140000.0, "Location transport, cast catering");

            b1.logExpense(Department.ART_AND_PROPS, 45000.0, "Cockpit flight console fabrication", "Art Director");
            b1.logExpense(Department.CAMERA, 28000.0, "High-speed camera sensor rental deposit", "DP Elena Vance");
            b1.logExpense(Department.COSTUME_AND_MAKEUP, 15000.0, "Commander hero spacesuit fitting", "Wardrobe Lead");

            if (defaultBudgetService != null) {
                for (Department d : Department.values()) {
                    double alloc = b1.getAllocatedForDepartment(d);
                    if (alloc > 0) defaultBudgetService.allocateBudget(d, alloc);
                }
            }

            Scene sc1 = new Scene("SCN-01", "MOV-01", 1, "The Discovery at Station 9",
                    "Commander Jax and Dr. Thorne inspect the anomalous crystalline signal in deep orbit.",
                    "[INT. APEX STATION 9 - OBSERVATION DOME - NIGHT]\n" +
                    "The observation dome vibrates with deep harmonic hums from the crystalline core.\n" +
                    "Commander Jax monitors the energy flux.\n\n" +
                    "JAX\n\"The resonance is accelerating, Lyra. It's not a stellar echo.\"\n\n" +
                    "DR. THORNE\n(checking the spectrometer)\n\"Look at the broadcast wavelength, Jax. It's scanning our ship.\"",
                    4.5, DaylightRequirement.INTERIOR_STUDIO, 2, 7.0);
            sc1.assignActors("ACT-201", "ACT-202");
            sc1.assignEquipment("EQ-401", "EQ-402", "EQ-403");
            sc1.setLocationId("LOC-501");
            sc1.setScheduledDate(today.plusDays(1));
            sc1.setShootTimeSlot("07:00 - 14:00");
            sc1.setStatus(SceneStatus.SCHEDULED);

            Scene sc2 = new Scene("SCN-02", "MOV-01", 2, "Desert Rover Ambush",
                    "Malakor's scavengers intercept the transport crawler during high noon desert crossing.",
                    "[EXT. BLACK ROCK DUNE RIDGE - DAY]\n" +
                    "Twin suns beat down relentlessly. Two armed sand-skiffs breach the perimeter.\n\n" +
                    "GOVERNOR MALAKOR\n(through loudspeaker)\n\"Halt the crawler and surrender the crystalline core, Commander!\"\n\n" +
                    "JAX\n\"Brace for impact! Gunner, return fire on their treads!\"",
                    6.0, DaylightRequirement.DAY_EXTERIOR, 1, 9.5);
            sc2.assignActors("ACT-201", "ACT-203", "ACT-204");
            sc2.assignEquipment("EQ-401", "EQ-404");
            sc2.setLocationId("LOC-502");
            sc2.setScheduledDate(today.plusDays(3));
            sc2.setShootTimeSlot("08:00 - 17:30");
            sc2.setStatus(SceneStatus.SCHEDULED);

            Scene sc3 = new Scene("SCN-03", "MOV-01", 3, "Twilight Rooftop Confrontation",
                    "Jax faces Governor Malakor atop the Citadel spires as the twin suns set.",
                    "[EXT. CITADEL APEX ROOFTOP - GOLDEN HOUR]\n" +
                    "The sky burns in vivid amber and violet. The wind whips Jax's tactical duster.\n\n" +
                    "MALAKOR\n\"You crossed three galaxies to die on an abandoned roof?\"\n\n" +
                    "JAX\n\"I came to make sure you never leave it.\"",
                    3.0, DaylightRequirement.GOLDEN_HOUR, 1, 3.5);
            sc3.assignActors("ACT-201", "ACT-203");
            sc3.assignEquipment("EQ-401", "EQ-402");
            sc3.setLocationId("LOC-503");
            sc3.setScheduledDate(today.plusDays(5));
            sc3.setShootTimeSlot("17:00 - 20:00");
            sc3.setStatus(SceneStatus.SCHEDULED);

            Scene sc4 = new Scene("SCN-04", "MOV-01", 4, "Cockpit Zero-G Emergency Descent",
                    "Pilot Kira maneuvers the battered vessel through planetary atmospheric turbulence.",
                    "[INT. VESSEL COCKPIT - NIGHT]\n" +
                    "Red alarms strobe violently as g-forces pin Kira against the flight harness.\n\n" +
                    "KIRA\n\"Atmospheric thrusters failing! Hold on to something!\"",
                    5.0, DaylightRequirement.INTERIOR_STUDIO, 3, 6.0);
            sc4.assignActors("ACT-201", "ACT-204");
            sc4.assignEquipment("EQ-401", "EQ-403");
            sc4.setLocationId("LOC-501");
            sc4.setStatus(SceneStatus.DRAFT);

            Scene sc5 = new Scene("SCN-05", "MOV-01", 5, "Night Perimeter Infiltration",
                    "Dr. Thorne hacks the subterranean power array under cover of nocturnal blizzard.",
                    "[EXT. SUBTERRANEAN GENERATOR VAULT - NIGHT]\n" +
                    "Snow howls across the access hatch. Thorne splices the optical circuit bypass.\n\n" +
                    "DR. THORNE\n\"Security override locked in. Opening blast doors now.\"",
                    4.0, DaylightRequirement.NIGHT_EXTERIOR, 2, 6.5);
            sc5.assignActors("ACT-202");
            sc5.assignEquipment("EQ-401", "EQ-404");
            sc5.setLocationId("LOC-503");
            sc5.setStatus(SceneStatus.DRAFT);

            Scene sc6 = new Scene("SCN-06", "MOV-01", 6, "Prologue: Galactic Council Briefing",
                    "Archival transmission initiating the Aether expedition.",
                    "[INT. COUNCIL CHAMBER ARCHIVES - DAY]\n" +
                    "Holographic star maps revolve in gentle loops around the council podium.\n\n" +
                    "COUNCIL ENVOY\n\"This mission remains off the official records. Godspeed, Commander.\"",
                    2.0, DaylightRequirement.INTERIOR_STUDIO, 4, 3.0);
            sc6.assignActors("ACT-203");
            sc6.setLocationId("LOC-501");
            sc6.setStatus(SceneStatus.COMPLETED);

            m1.addScenes(sc1, sc2, sc3, sc4, sc5, sc6);
            m1.assignActors("ACT-201", "ACT-202", "ACT-203", "ACT-204");
            m1.assignCrew("CRW-301", "CRW-302", "CRW-303", "CRW-304", "CRW-305");
            m1.bookEquipment("EQ-401", "EQ-402", "EQ-403", "EQ-404");
            m1.bookLocations("LOC-501", "LOC-502", "LOC-503");

            productionService.addScene(sc1);
            productionService.addScene(sc2);
            productionService.addScene(sc3);
            productionService.addScene(sc4);
            productionService.addScene(sc5);
            productionService.addScene(sc6);

            CallSheet cs1 = new CallSheet("CS-01", "MOV-01", 1, today.plusDays(1), "06:30 AM",
                    "Apex Soundstage 4, Stage Floor B", "Interior Stage (Controlled temp 20°C)",
                    "Culver City Emergency Center, 4200 Overland Ave (Ph: 310-836-7000)");
            cs1.addScene(sc1);
            cs1.setCastCallTime("Aiden Cross (Jax)", "06:45 AM (Hair & Prosthetics)");
            cs1.setCastCallTime("Dr. Lyra Thorne (Lyra)", "07:15 AM (Wardrobe)");
            cs1.setDepartmentCallTime(Department.CAMERA, "06:00 AM (Camera Prep & Calibration)");
            cs1.setDepartmentCallTime(Department.LIGHTING, "05:30 AM (Volume Wall Power-up)");
            cs1.setDepartmentCallTime(Department.SOUND, "06:30 AM (Wireless Mics & Boom)");
            m1.addCallSheet(cs1);
            productionService.registerCallSheet(cs1);

            // MOV-02: Shadows of the Past
            Movie m2 = new Movie("MOV-02", "PH-101", "Shadows of the Past", "Psychological Thriller", "Evelyn Blackwood",
                    ProductionPhase.PRE_PRODUCTION, 2027);
            BudgetService b2 = m2.getBudgetService();
            b2.allocateBudget(Department.DIRECTING, 180000.0, "Director fees, casting directors");
            b2.allocateBudget(Department.CAMERA, 160000.0, "Vintage prime lenses & anamorphic package");
            b2.allocateBudget(Department.SOUND, 90000.0, "Atmospheric sound design & score");
            b2.allocateBudget(Department.ART_AND_PROPS, 210000.0, "Neo-noir detective office");
            b2.allocateBudget(Department.COSTUME_AND_MAKEUP, 110000.0, "Period wardrobe and trench coats");
            b2.allocateBudget(Department.LOGISTICS_AND_CATERING, 90000.0, "City permit logistics");
            b2.allocateBudget(Department.VFX_AND_POST, 160000.0, "Digital rain enhancements");

            b2.logExpense(Department.DIRECTING, 12000.0, "Screenplay adaptation rights", "Producer");
            b2.logExpense(Department.ART_AND_PROPS, 8500.0, "Noir detective office set design blueprint", "Production Designer");

            Scene sc201 = new Scene("SCN-201", "MOV-02", 1, "Rainy Harbor Detective Meet",
                    "Detective Vance meets an anonymous whistleblower at dock pier 14 under rain.",
                    "[EXT. HARBOR PIER 14 - NIGHT]\n" +
                    "Rain hammers against the rusted steel dock pilings. Detective Vance lights a cigarette.\n\n" +
                    "WHISTLEBLOWER\n\"You shouldn't have dug into the mayoral ledger, Vance.\"\n\n" +
                    "VANCE\n\"Someone has to count the bodies.\"",
                    3.5, DaylightRequirement.NIGHT_EXTERIOR, 2, 5.0);
            sc201.assignActors("ACT-202");
            sc201.assignEquipment("EQ-401", "EQ-403");
            sc201.setLocationId("LOC-503");
            sc201.setStatus(SceneStatus.DRAFT);

            Scene sc202 = new Scene("SCN-202", "MOV-02", 2, "Interrogation Room Silence",
                    "An intense psychological duel between the chief investigator and prime suspect.",
                    "[INT. PRECINCT INTERROGATION ROOM - DAY]\n" +
                    "The harsh fluorescent bulb buzzes. A heavy metal tape recorder clicks on.\n\n" +
                    "SUSPECT\n\"You think silence is an admission? Silence is the only truth left.\"",
                    4.0, DaylightRequirement.INTERIOR_STUDIO, 1, 6.0);
            sc202.assignActors("ACT-202", "ACT-203");
            sc202.assignEquipment("EQ-401");
            sc202.setLocationId("LOC-501");
            sc202.setScheduledDate(today.plusDays(10));
            sc202.setShootTimeSlot("09:00 - 16:00");
            sc202.setStatus(SceneStatus.SCHEDULED);

            Scene sc203 = new Scene("SCN-203", "MOV-02", 3, "Lake House Flashback",
                    "Memory sequence revealing the crucial betrayal that sparked the conspiracy.",
                    "[EXT. LAKE HOUSE BOAT DOCK - GOLDEN HOUR]\n" +
                    "Water ripples softly in the dying orange light. A sealed envelope changes hands.",
                    2.5, DaylightRequirement.GOLDEN_HOUR, 1, 3.0);
            sc203.assignActors("ACT-202");
            sc203.setLocationId("LOC-502");
            sc203.setStatus(SceneStatus.DRAFT);

            Scene sc204 = new Scene("SCN-204", "MOV-02", 4, "Subway Platform Chase",
                    "Paced foot pursuit through steam-filled underground commuter platform.",
                    "[INT. CENTRAL METRO STATION - NIGHT]\n" +
                    "Footsteps echo through tiled tunnels. An express train screams past, shaking the floor.",
                    5.0, DaylightRequirement.INTERIOR_STUDIO, 3, 7.0);
            sc204.assignActors("ACT-203");
            sc204.assignEquipment("EQ-401", "EQ-403");
            sc204.setLocationId("LOC-501");
            sc204.setStatus(SceneStatus.DRAFT);

            m2.addScenes(sc201, sc202, sc203, sc204);
            productionService.addScene(sc201);
            productionService.addScene(sc202);
            productionService.addScene(sc203);
            productionService.addScene(sc204);

            // MOV-03: The Royal Heritage
            Movie m3 = new Movie("MOV-03", "PH-101", "The Royal Heritage", "Historical Drama", "Arthur Pendelton",
                    ProductionPhase.POST_PRODUCTION, 2026);
            BudgetService b3 = m3.getBudgetService();
            b3.allocateBudget(Department.DIRECTING, 450000.0, "Director & historical consultant");
            b3.allocateBudget(Department.CAMERA, 400000.0, "65mm large format camera package");
            b3.allocateBudget(Department.LIGHTING, 220000.0, "Candlelight simulation rigs");
            b3.allocateBudget(Department.SOUND, 200000.0, "Symphony orchestra recording and foley");
            b3.allocateBudget(Department.ART_AND_PROPS, 650000.0, "Throne room sets and royal props");
            b3.allocateBudget(Department.COSTUME_AND_MAKEUP, 580000.0, "Handwoven royal court attire");
            b3.allocateBudget(Department.VFX_AND_POST, 900000.0, "Post-production editing and crowd replication");
            b3.allocateBudget(Department.LOGISTICS_AND_CATERING, 200000.0, "Estate castle transport and catering");

            b3.logExpense(Department.COSTUME_AND_MAKEUP, 480000.0, "18th century handwoven royal attire", "Lead Costumer");
            b3.logExpense(Department.CAMERA, 350000.0, "65mm large format camera rentals", "DP Elena Vance");
            b3.logExpense(Department.VFX_AND_POST, 720000.0, "Orchestral foley & color grading suite", "Chloe Bennett");

            Scene sc301 = new Scene("SCN-301", "MOV-03", 1, "Coronation Banquet",
                    "The grand crowning ceremony inside the cathedral with royal nobility.",
                    "[INT. ROYAL CATHEDRAL NAVE - DAY]\n" +
                    "A hundred tallow candles flicker. The Archbishop raises the gilded crown.",
                    6.0, DaylightRequirement.INTERIOR_STUDIO, 2, 8.0);
            sc301.setStatus(SceneStatus.COMPLETED);

            Scene sc302 = new Scene("SCN-302", "MOV-03", 2, "Battle of Ashford Fields",
                    "Decisive medieval clash between the royal guard and rebel battalions.",
                    "[EXT. ASHFORD MIST VALLEY - DAY]\n" +
                    "Royal banners whip in the gale. Cavalry shields slam together with thunderous force.",
                    8.5, DaylightRequirement.DAY_EXTERIOR, 1, 12.0);
            sc302.setStatus(SceneStatus.COMPLETED);

            Scene sc303 = new Scene("SCN-303", "MOV-03", 3, "Council Chamber Treason",
                    "Secret betrayal unveiled during late evening royal cabinet deliberations.",
                    "[INT. KING'S PRIVY CHAMBER - NIGHT]\n" +
                    "The Chancellor burns a ciphered letter in the fireplace as guards assemble outside.",
                    4.0, DaylightRequirement.INTERIOR_STUDIO, 3, 6.0);
            sc303.setStatus(SceneStatus.COMPLETED);

            Scene sc304 = new Scene("SCN-304", "MOV-03", 4, "Final Farewell at Sea Port",
                    "The exiled prince boards the royal frigate at sunset as the crowd watches.",
                    "[EXT. ANCHORAGE PIER - GOLDEN HOUR]\n" +
                    "The frigate's sails catch the wind. A final handkerchief waves from the shoreline.",
                    3.0, DaylightRequirement.GOLDEN_HOUR, 1, 4.0);
            sc304.setStatus(SceneStatus.COMPLETED);

            m3.addScenes(sc301, sc302, sc303, sc304);
            productionService.addScene(sc301);
            productionService.addScene(sc302);
            productionService.addScene(sc303);
            productionService.addScene(sc304);

            s1.addMovie(m1);
            s1.addMovie(m2);
            s1.addMovie(m3);
            s1.setActiveMovie(m1);

            // =================================================================
            // 4. STUDIO 2: WARNER BROS. PICTURES (PH-102)
            // =================================================================
            ProductionHouse s2 = new ProductionHouse("PH-102", "Warner Bros. Pictures", "Burbank, California", 1923);

            Movie m4 = new Movie("MOV-04", "PH-102", "The Dark Horizon", "Action / Neo-Noir", "Christopher Nolan",
                    ProductionPhase.PRE_PRODUCTION, 2027);
            m4.getBudgetService().allocateBudget(Department.DIRECTING, 600000.0, "Nolan directorial package");
            m4.getBudgetService().allocateBudget(Department.CAMERA, 800000.0, "IMAX 15-perf 70mm cameras");
            m4.getBudgetService().allocateBudget(Department.STUNTS, 900000.0, "Practical skyscraper wire rigging");
            m4.getBudgetService().logExpense(Department.DIRECTING, 50000.0, "Concept storyboards & consulting", "Line Producer");

            Scene sc401 = new Scene("SCN-401", "MOV-04", 1, "Skyscraper Heist Infiltration",
                    "Operatives BASE-jump through midnight fog into financial headquarters penthouse.",
                    "[EXT. PENTHOUSE BALCONY - NIGHT]\n" +
                    "Rain slices horizontally across the glass. The glass-cutter emits an infrared hum.",
                    5.0, DaylightRequirement.NIGHT_EXTERIOR, 1, 8.0);
            sc401.setStatus(SceneStatus.DRAFT);
            m4.addScene(sc401);
            productionService.addScene(sc401);

            Movie m5 = new Movie("MOV-05", "PH-102", "Dune Odyssey", "Sci-Fi Epic", "Denis Villeneuve",
                    ProductionPhase.PRODUCTION, 2026);
            m5.getBudgetService().allocateBudget(Department.CAMERA, 950000.0, "Large-format Arri Alexa 65 lenses");
            m5.getBudgetService().allocateBudget(Department.VFX_AND_POST, 1500000.0, "Desert spice volume rendering");
            m5.getBudgetService().logExpense(Department.CAMERA, 120000.0, "Desert dust hermetic seal rig", "DP Elena Vance");

            Scene sc501 = new Scene("SCN-501", "MOV-05", 1, "Sandworm Sighting at Dune Crest",
                    "Tremors ripple beneath the desert harvester as a colossal shadow rises.",
                    "[EXT. ARRAKIS SHIFTING DUNES - DAYLIGHT EXTERIOR]\n" +
                    "The sand thrums with sub-bass acoustic shockwaves. The horizon bends.",
                    7.0, DaylightRequirement.DAY_EXTERIOR, 1, 10.0);
            sc501.setStatus(SceneStatus.SCHEDULED);
            sc501.setScheduledDate(today.plusDays(4));
            sc501.setShootTimeSlot("06:00 - 16:00");
            m5.addScene(sc501);
            productionService.addScene(sc501);

            s2.addMovie(m4);
            s2.addMovie(m5);
            s2.setActiveMovie(m5);

            // =================================================================
            // 5. STUDIO 3: PARAMOUNT PICTURES (PH-103)
            // =================================================================
            ProductionHouse s3 = new ProductionHouse("PH-103", "Paramount Pictures", "Hollywood, California", 1912);

            Movie m6 = new Movie("MOV-06", "PH-103", "Mission: Retribution", "Action / Thriller", "Christopher McQuarrie",
                    ProductionPhase.PRODUCTION, 2026);
            m6.getBudgetService().allocateBudget(Department.STUNTS, 1200000.0, "Speed-flying parachute and helicopter stunts");
            m6.getBudgetService().allocateBudget(Department.CAMERA, 700000.0, "Cockpit mini-cameras and drone swarm");
            m6.getBudgetService().logExpense(Department.STUNTS, 180000.0, "Helicopter acrobatics safety rig", "Stunt Lead");

            Scene sc601 = new Scene("SCN-601", "MOV-06", 1, "Cargo Plane Jump Over Alps",
                    "Ethan parachutes at night into frozen alpine canyon amidst pursuit.",
                    "[EXT. HIGH ALPS AIRSPACE - NIGHT]\n" +
                    "The cargo ramp drops into freezing darkness. Wind screams at 300 knots.",
                    4.0, DaylightRequirement.NIGHT_EXTERIOR, 1, 7.0);
            sc601.setStatus(SceneStatus.SCHEDULED);
            sc601.setScheduledDate(today.plusDays(6));
            sc601.setShootTimeSlot("20:00 - 02:00");
            m6.addScene(sc601);
            productionService.addScene(sc601);

            Movie m7 = new Movie("MOV-07", "PH-103", "Gladiator Chronicles", "Historical Action", "Ridley Scott",
                    ProductionPhase.POST_PRODUCTION, 2026);
            m7.getBudgetService().allocateBudget(Department.VFX_AND_POST, 1200000.0, "Colosseum audience replication");
            m7.getBudgetService().logExpense(Department.VFX_AND_POST, 600000.0, "Historical foley and orchestral mix", "Mixer");

            Scene sc701 = new Scene("SCN-701", "MOV-07", 1, "Colosseum Chariot Battle",
                    "Gladiators clash on burning sands as the Roman senate roars.",
                    "[EXT. COLOSSEUM ARENA - DAYLIGHT EXTERIOR]\n" +
                    "Iron chariot wheels kick up scarlet dust. Bronze blades clash.",
                    8.0, DaylightRequirement.DAY_EXTERIOR, 1, 12.0);
            sc701.setStatus(SceneStatus.COMPLETED);
            m7.addScene(sc701);
            productionService.addScene(sc701);

            s3.addMovie(m6);
            s3.addMovie(m7);
            s3.setActiveMovie(m6);

            // =================================================================
            // 6. STUDIO 4: A24 STUDIOS (PH-104)
            // =================================================================
            ProductionHouse s4 = new ProductionHouse("PH-104", "A24 Studios", "New York City", 2012);

            Movie m8 = new Movie("MOV-08", "PH-104", "Past Echoes", "Indie Drama", "Celine Song",
                    ProductionPhase.RELEASED, 2025);
            m8.getBudgetService().allocateBudget(Department.DIRECTING, 200000.0, "Indie auteur package");
            m8.getBudgetService().allocateBudget(Department.SOUND, 150000.0, "Subway foley and acoustic piano");
            m8.getBudgetService().logExpense(Department.DIRECTING, 180000.0, "Festival distribution deliverables", "Producer");

            Scene sc801 = new Scene("SCN-801", "MOV-08", 1, "Subway Platform Reunion",
                    "Two childhood sweethearts meet across the platform after twenty-four years.",
                    "[INT. EAST VILLAGE SUBWAY PLATFORM - NIGHT]\n" +
                    "Train headlights rake across the platform tiles. Their eyes meet in quiet realization.",
                    3.0, DaylightRequirement.INTERIOR_STUDIO, 3, 4.0);
            sc801.setStatus(SceneStatus.COMPLETED);
            m8.addScene(sc801);
            productionService.addScene(sc801);

            Movie m9 = new Movie("MOV-09", "PH-104", "The Lighthouse Secret", "Psychological Horror", "Robert Eggers",
                    ProductionPhase.PRODUCTION, 2026);
            m9.getBudgetService().allocateBudget(Department.CAMERA, 300000.0, "Black & white 35mm orthochromatic film stock");
            m9.getBudgetService().logExpense(Department.CAMERA, 45000.0, "Rare vintage Baltar lenses", "DP");

            Scene sc901 = new Scene("SCN-901", "MOV-09", 1, "Storm Lantern Watch",
                    "A keeper battles howling winds to keep the lighthouse flame alive.",
                    "[INT. LIGHTHOUSE LANTERN ROOM - NIGHT EXTERIOR]\n" +
                    "Salt spray batters the glass panes. Shadows rotate relentlessly.",
                    4.5, DaylightRequirement.NIGHT_EXTERIOR, 1, 6.0);
            sc901.setStatus(SceneStatus.DRAFT);
            m9.addScene(sc901);
            productionService.addScene(sc901);

            s4.addMovie(m8);
            s4.addMovie(m9);
            s4.setActiveMovie(m9);

            // =================================================================
            // 7. STUDIO 5: MYTHRI MOVIE MAKERS (PH-105)
            // =================================================================
            ProductionHouse s5 = new ProductionHouse("PH-105", "Mythri Movie Makers", "Hyderabad, India", 2015);

            Movie m10 = new Movie("MOV-10", "PH-105", "Pushpa: The Rule", "Action / Drama", "Sukumar",
                    ProductionPhase.PRODUCTION, 2026);
            m10.getBudgetService().allocateBudget(Department.STUNTS, 1500000.0, "Seshachalam forest heavy truck chase sequence");
            m10.getBudgetService().allocateBudget(Department.ART_AND_PROPS, 1200000.0, "Red sanders practical timber depot");
            m10.getBudgetService().logExpense(Department.STUNTS, 250000.0, "Forest ravine wire rigging and stunt convoy", "Stunt Master");

            Scene sc1001 = new Scene("SCN-1001", "MOV-10", 1, "Red Sanders Forest Ambush",
                    "Pushpa confronts special police task force at the rocky forest river bend.",
                    "[EXT. SESHACHALAM MISTY FOREST - DAYLIGHT EXTERIOR]\n" +
                    "Diesel smoke billows through teak trees. Pushpa steps out of the truck cab with quiet authority.\n\n" +
                    "PUSHPA\n\"Pushpa ante flower anukuntiva? Fire-u!\"",
                    6.5, DaylightRequirement.DAY_EXTERIOR, 1, 9.0);
            sc1001.assignActors("ACT-205");
            sc1001.setLocationId("LOC-502");
            sc1001.setStatus(SceneStatus.SCHEDULED);
            sc1001.setScheduledDate(today.plusDays(2));
            sc1001.setShootTimeSlot("07:00 - 17:00");
            m10.addScene(sc1001);
            productionService.addScene(sc1001);

            Movie m11 = new Movie("MOV-11", "PH-105", "Devara: Part 1", "Action Epic", "Koratala Siva",
                    ProductionPhase.POST_PRODUCTION, 2026);
            m11.getBudgetService().allocateBudget(Department.VFX_AND_POST, 1800000.0, "High-seas storm CGI and underwater grading");
            m11.getBudgetService().logExpense(Department.VFX_AND_POST, 750000.0, "Anirudh score master suite recording", "Sound Lead");

            Scene sc1101 = new Scene("SCN-1101", "MOV-11", 1, "Midnight Coastal Armada Attack",
                    "Warriors plunge from stormy cliffs onto intercepted smuggling vessels.",
                    "[EXT. STORMY OCEAN CLIFFS - NIGHT EXTERIOR]\n" +
                    "Lightning reveals thirty boats riding twenty-foot black swells.",
                    5.5, DaylightRequirement.NIGHT_EXTERIOR, 1, 8.0);
            sc1101.setStatus(SceneStatus.COMPLETED);
            m11.addScene(sc1101);
            productionService.addScene(sc1101);

            s5.addMovie(m10);
            s5.addMovie(m11);
            s5.setActiveMovie(m10);

            // Register all 5 Studios in StudioService
            studioService.registerStudio(s1);
            studioService.registerStudio(s2);
            studioService.registerStudio(s3);
            studioService.registerStudio(s4);
            studioService.registerStudio(s5);

            // Active Studio defaults to Horizon Studios
            studioService.setActiveStudio("PH-101");
            productionService.setActiveMovieId("MOV-01");

        } catch (ValidationException | BudgetExceededException e) {
            System.err.println("Warning while seeding studio data: " + e.getMessage());
        }
    }
}
