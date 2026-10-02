package com.cineflow.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the official daily production Call Sheet issued to cast and crew.
 * Demonstrates:
 *  - List collection (scenes scheduled for the day)
 *  - Map collection (LinkedHashMap for ordered cast/crew call times)
 *  - Formatting and Reportable generation
 */
public class CallSheet implements Identifiable<String>, Reportable {
    private static final long serialVersionUID = 1L;

    private String id;
    private int shootDayNumber;
    private LocalDate shootDate;
    private String generalCrewCallTime;
    private String shootingLocationName;
    private String weatherAdvisory;
    private String nearestHospitalInfo;
    private List<Scene> scheduledScenes;
    private Map<String, String> castCallTimes; // actorName -> callTime
    private Map<Department, String> departmentCallTimes; // Department -> callTime
    private String movieId;

    public CallSheet() {
        this("CS-000", "MOV-01", 1, LocalDate.now(), "06:30 AM", "Soundstage A", "Sunny, 24°C", "Mercy General Hospital (Tel: 911)");
    }

    public CallSheet(String id, int shootDayNumber, LocalDate shootDate, String generalCrewCallTime,
                     String shootingLocationName, String weatherAdvisory, String nearestHospitalInfo) {
        this(id, "MOV-01", shootDayNumber, shootDate, generalCrewCallTime, shootingLocationName, weatherAdvisory, nearestHospitalInfo);
    }

    public CallSheet(String id, String movieId, int shootDayNumber, LocalDate shootDate, String generalCrewCallTime,
                     String shootingLocationName, String weatherAdvisory, String nearestHospitalInfo) {
        this.id = id;
        this.movieId = movieId != null ? movieId : "MOV-01";
        this.shootDayNumber = shootDayNumber;
        this.shootDate = shootDate;
        this.generalCrewCallTime = generalCrewCallTime;
        this.shootingLocationName = shootingLocationName;
        this.weatherAdvisory = weatherAdvisory;
        this.nearestHospitalInfo = nearestHospitalInfo;
        this.scheduledScenes = new ArrayList<>();
        this.castCallTimes = new LinkedHashMap<>();
        this.departmentCallTimes = new LinkedHashMap<>();
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void addScene(Scene scene) {
        if (scene != null) {
            this.scheduledScenes.add(scene);
        }
    }

    public void setCastCallTime(String actorName, String callTime) {
        this.castCallTimes.put(actorName, callTime);
    }

    public void setDepartmentCallTime(Department department, String callTime) {
        this.departmentCallTimes.put(department, callTime);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append(String.format("                     OFFICIAL PRODUCTION CALL SHEET (DAY #%02d)                  \n", shootDayNumber));
        sb.append("========================================================================================\n");
        sb.append(String.format("SHOOT DATE      : %s                    CREW CALL: %s\n", shootDate, generalCrewCallTime));
        sb.append(String.format("LOCATION        : %s\n", shootingLocationName));
        sb.append(String.format("WEATHER FORECAST: %s\n", weatherAdvisory));
        sb.append(String.format("SAFETY/HOSPITAL : %s\n", nearestHospitalInfo));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append("SCHEDULED SCENES FOR THE DAY:\n");
        if (scheduledScenes.isEmpty()) {
            sb.append("  (No scenes scheduled on this call sheet)\n");
        } else {
            for (Scene s : scheduledScenes) {
                sb.append(String.format("  • Scene #%02d [Est. %.1f hrs | Pages: %.1f] - \"%s\" (%s)\n",
                        s.getSceneNumber(), s.getEstimatedShootHours(), s.getScriptPages(), s.getTitle(), s.getDaylightRequirement().getDescription()));
            }
        }
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append("CAST CALL TIMES & WARDROBE FITTINGS:\n");
        if (castCallTimes.isEmpty()) {
            sb.append("  (No cast call times registered)\n");
        } else {
            castCallTimes.forEach((actor, time) ->
                    sb.append(String.format("  • %-30s -> Call Time: %s\n", actor, time)));
        }
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append("DEPARTMENT-SPECIFIC CALL TIMES:\n");
        if (departmentCallTimes.isEmpty()) {
            sb.append("  (Standard crew call applies across all departments)\n");
        } else {
            departmentCallTimes.forEach((dept, time) ->
                    sb.append(String.format("  • %-30s -> Call Time: %s\n", dept.getDisplayName(), time)));
        }
        sb.append("========================================================================================\n");
        return sb.toString();
    }

    // Getters and Setters
    public int getShootDayNumber() {
        return shootDayNumber;
    }

    public void setShootDayNumber(int shootDayNumber) {
        this.shootDayNumber = shootDayNumber;
    }

    public LocalDate getShootDate() {
        return shootDate;
    }

    public void setShootDate(LocalDate shootDate) {
        this.shootDate = shootDate;
    }

    public String getGeneralCrewCallTime() {
        return generalCrewCallTime;
    }

    public void setGeneralCrewCallTime(String generalCrewCallTime) {
        this.generalCrewCallTime = generalCrewCallTime;
    }

    public String getShootingLocationName() {
        return shootingLocationName;
    }

    public void setShootingLocationName(String shootingLocationName) {
        this.shootingLocationName = shootingLocationName;
    }

    public String getWeatherAdvisory() {
        return weatherAdvisory;
    }

    public void setWeatherAdvisory(String weatherAdvisory) {
        this.weatherAdvisory = weatherAdvisory;
    }

    public String getNearestHospitalInfo() {
        return nearestHospitalInfo;
    }

    public void setNearestHospitalInfo(String nearestHospitalInfo) {
        this.nearestHospitalInfo = nearestHospitalInfo;
    }

    public List<Scene> getScheduledScenes() {
        return scheduledScenes;
    }

    public Map<String, String> getCastCallTimes() {
        return castCallTimes;
    }

    public Map<Department, String> getDepartmentCallTimes() {
        return departmentCallTimes;
    }
}
