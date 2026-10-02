package com.cineflow.util;

import com.cineflow.exception.BudgetExceededException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.CallSheet;
import com.cineflow.model.DaylightRequirement;
import com.cineflow.model.Department;
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
 * Pre-seeds realistic default production data for the feature film:
 * "Chronicles of Aether" (Sci-Fi / Space Opera).
 * Allows examiners to test all features instantly without typing manual records.
 */
public final class DataGenerator {

    private DataGenerator() {}

    public static void seedProductionData(ProductionService productionService,
                                         PersonnelService personnelService,
                                         AssetService assetService,
                                         BudgetService budgetService) {
        try {
            // 1. Seed Budgets across all departments (Total: $2,500,000)
            budgetService.allocateBudget(Department.DIRECTING, 350000.0, "Director fees, creative consultants");
            budgetService.allocateBudget(Department.CAMERA, 320000.0, "Cinematography package & DP contracts");
            budgetService.allocateBudget(Department.LIGHTING, 180000.0, "Gaffer team, lighting grids, generators");
            budgetService.allocateBudget(Department.SOUND, 140000.0, "Sound mixer, boom ops, foley capture");
            budgetService.allocateBudget(Department.ART_AND_PROPS, 400000.0, "Spacecraft sets, practical alien props");
            budgetService.allocateBudget(Department.COSTUME_AND_MAKEUP, 220000.0, "Exosuit wardrobes, prosthetic makeup");
            budgetService.allocateBudget(Department.VFX_AND_POST, 600000.0, "CGI rendering, volume wall, editing");
            budgetService.allocateBudget(Department.STUNTS, 150000.0, "Zero-G wire rigging & stunt coordinators");
            budgetService.allocateBudget(Department.LOGISTICS_AND_CATERING, 140000.0, "Location transport, cast catering");

            // Seed initial expenses
            budgetService.logExpense(Department.ART_AND_PROPS, 45000.0, "Cockpit flight console fabrication", "Art Director");
            budgetService.logExpense(Department.CAMERA, 28000.0, "High-speed camera sensor rental deposit", "DP Elena Vance");
            budgetService.logExpense(Department.COSTUME_AND_MAKEUP, 15000.0, "Commander hero spacesuit fitting", "Wardrobe Lead");

            // 2. Seed Personnel (Cast & Crew)
            Director director = new Director("DIR-101", "Marcus Sterling", 4200.0,
                    "Grounded science fiction with tactile realism and deep emotional stakes", 3.0, 75000.0);
            director.recordDaysWorked(12);
            personnelService.registerPerson(director);

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

            // Crew Members
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

            // 3. Seed Assets (Equipment & Locations)
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

            // 4. Seed Scenes with diverse daylight and priority ratings
            LocalDate today = LocalDate.now();

            Scene s1 = new Scene("SCN-01", 1, "The Discovery at Station 9",
                    "Commander Jax and Dr. Thorne inspect the anomalous crystalline signal in deep orbit.",
                    4.5, DaylightRequirement.INTERIOR_STUDIO, 2, 7.0);
            s1.assignActors("ACT-201", "ACT-202");
            s1.assignEquipment("EQ-401");
            s1.assignEquipment("EQ-402");
            s1.assignEquipment("EQ-403");
            s1.setLocationId("LOC-501");
            s1.setScheduledDate(today.plusDays(1));
            s1.setShootTimeSlot("07:00 - 14:00");
            s1.setStatus(SceneStatus.SCHEDULED);
            productionService.addScene(s1);

            Scene s2 = new Scene("SCN-02", 2, "Desert Rover Ambush",
                    "Malakor's scavengers intercept the transport crawler during high noon desert crossing.",
                    6.0, DaylightRequirement.DAY_EXTERIOR, 1, 9.5); // Priority 1 (weather/sun dependent)
            s2.assignActors("ACT-201", "ACT-203", "ACT-204");
            s2.assignEquipment("EQ-401");
            s2.assignEquipment("EQ-404");
            s2.setLocationId("LOC-502");
            s2.setScheduledDate(today.plusDays(3));
            s2.setShootTimeSlot("08:00 - 17:30");
            s2.setStatus(SceneStatus.SCHEDULED);
            productionService.addScene(s2);

            Scene s3 = new Scene("SCN-03", 3, "Twilight Rooftop Confrontation",
                    "Jax faces Governor Malakor atop the Citadel spires as the twin suns set.",
                    3.0, DaylightRequirement.GOLDEN_HOUR, 1, 3.5); // Priority 1 (strict 45 min light window)
            s3.assignActors("ACT-201", "ACT-203");
            s3.assignEquipment("EQ-401");
            s3.assignEquipment("EQ-402");
            s3.setLocationId("LOC-503");
            s3.setScheduledDate(today.plusDays(5));
            s3.setShootTimeSlot("17:00 - 20:00");
            s3.setStatus(SceneStatus.SCHEDULED);
            productionService.addScene(s3);

            Scene s4 = new Scene("SCN-04", 4, "Cockpit Zero-G Emergency Descent",
                    "Pilot Kira maneuvers the battered vessel through planetary atmospheric turbulence.",
                    5.0, DaylightRequirement.INTERIOR_STUDIO, 3, 6.0);
            s4.assignActors("ACT-201", "ACT-204");
            s4.assignEquipment("EQ-401");
            s4.assignEquipment("EQ-403");
            s4.setLocationId("LOC-501");
            s4.setStatus(SceneStatus.DRAFT);
            productionService.addScene(s4);

            Scene s5 = new Scene("SCN-05", 5, "Night Perimeter Infiltration",
                    "Dr. Thorne hacks the subterranean power array under cover of nocturnal blizzard.",
                    4.0, DaylightRequirement.NIGHT_EXTERIOR, 2, 6.5);
            s5.assignActors("ACT-202");
            s5.assignEquipment("EQ-401");
            s5.assignEquipment("EQ-404");
            s5.setLocationId("LOC-503");
            s5.setStatus(SceneStatus.DRAFT);
            productionService.addScene(s5);

            Scene s6 = new Scene("SCN-06", 6, "Prologue: Galactic Council Briefing",
                    "Archival transmission initiating the Aether expedition.",
                    2.0, DaylightRequirement.INTERIOR_STUDIO, 4, 3.0);
            s6.assignActors("ACT-203");
            s6.setLocationId("LOC-501");
            s6.setStatus(SceneStatus.COMPLETED); // Already filmed in pre-production
            productionService.addScene(s6);

            // 5. Seed Call Sheet
            CallSheet cs1 = new CallSheet("CS-01", 1, today.plusDays(1), "06:30 AM",
                    "Apex Soundstage 4, Stage Floor B", "Interior Stage (Controlled temp 20°C)",
                    "Culver City Emergency Center, 4200 Overland Ave (Ph: 310-836-7000)");
            cs1.addScene(s1);
            cs1.setCastCallTime("Aiden Cross (Jax)", "06:45 AM (Hair & Prosthetics)");
            cs1.setCastCallTime("Dr. Lyra Thorne (Lyra)", "07:15 AM (Wardrobe)");
            cs1.setDepartmentCallTime(Department.CAMERA, "06:00 AM (Camera Prep & Calibration)");
            cs1.setDepartmentCallTime(Department.LIGHTING, "05:30 AM (Volume Wall Power-up)");
            cs1.setDepartmentCallTime(Department.SOUND, "06:30 AM (Wireless Mics & Boom)");
            productionService.registerCallSheet(cs1);

        } catch (ValidationException | BudgetExceededException e) {
            System.err.println("Warning while seeding data: " + e.getMessage());
        }
    }
}
