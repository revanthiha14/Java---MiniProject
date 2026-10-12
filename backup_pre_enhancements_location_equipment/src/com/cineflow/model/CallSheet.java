package com.cineflow.model;

import com.cineflow.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Daily Production Call Sheet & Daily Shooting Schedule.
 * In professional film production, this is the master operational document issued every day by the 1st AD.
 * Coordinates: Shooting day count, date, general crew call, basecamp location, sun/weather conditions,
 * emergency hospital protocols, scheduled scenes & page counts, actor call times, department notes, and DSR wrap reports.
 */
public class CallSheet {
    private String sheetId;
    private int dayNumber;
    private int totalDays;
    private String shootDate;
    private String generalCrewCall;
    private String shootingLocation;
    private String weatherAndSun;
    private String nearestHospital;
    private String status; // SCHEDULED, IN_PROGRESS, WRAPPED, DELAYED
    private List<Integer> scheduledSceneNumbers;
    private List<CastCallEntry> castCalls;
    private String departmentNotes;
    private double pagesShot;
    private String wrapNotes;

    public CallSheet(String sheetId, int dayNumber, int totalDays, String shootDate,
                     String generalCrewCall, String shootingLocation, String weatherAndSun,
                     String nearestHospital, String status) throws ValidationException {
        if (sheetId == null || sheetId.trim().isEmpty()) {
            throw new ValidationException("Sheet ID", "Call Sheet ID cannot be blank.");
        }
        if (dayNumber <= 0) {
            throw new ValidationException("Day Number", "Shooting day number must be a positive integer.");
        }
        this.sheetId = sheetId.trim().toUpperCase();
        this.dayNumber = dayNumber;
        this.totalDays = (totalDays >= dayNumber) ? totalDays : dayNumber;
        this.shootDate = (shootDate != null && !shootDate.trim().isEmpty()) ? shootDate.trim() : "TBD";
        this.generalCrewCall = (generalCrewCall != null && !generalCrewCall.trim().isEmpty()) ? generalCrewCall.trim() : "07:00 AM";
        this.shootingLocation = (shootingLocation != null && !shootingLocation.trim().isEmpty()) ? shootingLocation.trim() : "Main Studio Stage";
        this.weatherAndSun = (weatherAndSun != null && !weatherAndSun.trim().isEmpty())
                ? weatherAndSun.trim() : "Indoor Stage (Climate Controlled)";
        this.nearestHospital = (nearestHospital != null && !nearestHospital.trim().isEmpty())
                ? nearestHospital.trim() : "Nearest Emergency Medical Center (Emergency: 911 / 108 / 999)";
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "SCHEDULED";
        this.scheduledSceneNumbers = new ArrayList<>();
        this.castCalls = new ArrayList<>();
        this.departmentNotes = "CAMERA: Standard A & B camera package ready.\n"
                + "SOUND: Wireless lavaliers on lead actors; boom mic on all setups.\n"
                + "GRIP/ELEC: Overhead lighting rig pre-lit 30 min before crew call.\n"
                + "CATERING: Hot breakfast at 06:30 AM | Catered lunch 6 hrs after crew call.";
        this.pagesShot = 0.0;
        this.wrapNotes = "No wrap notes recorded yet.";
    }

    public String getSheetId() {
        return sheetId;
    }

    public void setSheetId(String sheetId) throws ValidationException {
        if (sheetId == null || sheetId.trim().isEmpty()) {
            throw new ValidationException("Sheet ID", "Call Sheet ID cannot be blank.");
        }
        this.sheetId = sheetId.trim().toUpperCase();
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        if (dayNumber > 0) this.dayNumber = dayNumber;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        if (totalDays >= dayNumber) this.totalDays = totalDays;
    }

    public String getShootDate() {
        return shootDate;
    }

    public void setShootDate(String shootDate) {
        this.shootDate = (shootDate != null && !shootDate.trim().isEmpty()) ? shootDate.trim() : "TBD";
    }

    public String getGeneralCrewCall() {
        return generalCrewCall;
    }

    public void setGeneralCrewCall(String generalCrewCall) {
        this.generalCrewCall = (generalCrewCall != null && !generalCrewCall.trim().isEmpty()) ? generalCrewCall.trim() : "07:00 AM";
    }

    public String getShootingLocation() {
        return shootingLocation;
    }

    public void setShootingLocation(String shootingLocation) {
        this.shootingLocation = (shootingLocation != null && !shootingLocation.trim().isEmpty()) ? shootingLocation.trim() : "Main Studio Stage";
    }

    public String getWeatherAndSun() {
        return weatherAndSun;
    }

    public void setWeatherAndSun(String weatherAndSun) {
        this.weatherAndSun = (weatherAndSun != null) ? weatherAndSun.trim() : "";
    }

    public String getNearestHospital() {
        return nearestHospital;
    }

    public void setNearestHospital(String nearestHospital) {
        this.nearestHospital = (nearestHospital != null) ? nearestHospital.trim() : "";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && !status.trim().isEmpty()) {
            this.status = status.trim().toUpperCase();
        }
    }

    public List<Integer> getScheduledSceneNumbers() {
        return scheduledSceneNumbers;
    }

    public void addScheduledScene(int sceneNumber) {
        if (!scheduledSceneNumbers.contains(sceneNumber)) {
            scheduledSceneNumbers.add(sceneNumber);
        }
    }

    public boolean removeScheduledScene(int sceneNumber) {
        return scheduledSceneNumbers.remove(Integer.valueOf(sceneNumber));
    }

    public List<CastCallEntry> getCastCalls() {
        return castCalls;
    }

    public void addCastCall(CastCallEntry entry) {
        if (entry != null) {
            // Replace if same actor ID exists
            castCalls.removeIf(c -> c.getActorId().equalsIgnoreCase(entry.getActorId()));
            castCalls.add(entry);
        }
    }

    public boolean removeCastCall(String actorId) {
        if (actorId == null) return false;
        return castCalls.removeIf(c -> c.getActorId().equalsIgnoreCase(actorId.trim()));
    }

    public String getDepartmentNotes() {
        return departmentNotes;
    }

    public void setDepartmentNotes(String departmentNotes) {
        this.departmentNotes = (departmentNotes != null) ? departmentNotes.trim() : "";
    }

    public double getPagesShot() {
        return pagesShot;
    }

    public void setPagesShot(double pagesShot) {
        if (pagesShot >= 0) this.pagesShot = pagesShot;
    }

    public String getWrapNotes() {
        return wrapNotes;
    }

    public void setWrapNotes(String wrapNotes) {
        this.wrapNotes = (wrapNotes != null) ? wrapNotes.trim() : "";
    }

    public void recordWrap(double pagesShot, String wrapNotes) {
        this.pagesShot = pagesShot;
        this.wrapNotes = (wrapNotes != null && !wrapNotes.trim().isEmpty()) ? wrapNotes.trim() : "Wrapped on schedule.";
        this.status = "WRAPPED";
    }

    /**
     * Calculates total scheduled pages for this day based on the movie's scenes.
     */
    public double calculateTotalScheduledPages(Movie movie) {
        if (movie == null) return 0.0;
        double sum = 0.0;
        for (int scNum : scheduledSceneNumbers) {
            Scene sc = movie.findSceneByNumber(scNum);
            if (sc != null) {
                sum += sc.getScriptPages();
            }
        }
        return sum;
    }

    /**
     * Generates a realistic, industry-standard call sheet document formatted with clear visual borders.
     */
    public String getFormattedCallSheet(Movie movie) {
        StringBuilder sb = new StringBuilder();
        String title = (movie != null) ? movie.getTitle().toUpperCase() : "UNTITLED PRODUCTION";
        String director = (movie != null) ? movie.getDirector() : "N/A";

        sb.append("================================================================================\n");
        sb.append("                           CINEFLOW PRODUCTION CALL SHEET                       \n");
        sb.append("================================================================================\n");
        sb.append(String.format(" MOVIE: %-32s | DIRECTOR: %s\n", title, director));
        sb.append(String.format(" CALL SHEET ID: %-25s | DATE: %s\n", sheetId, shootDate));
        sb.append(String.format(" SHOOTING DAY : DAY %d OF %-18d | STATUS: %s\n", dayNumber, totalDays, status));
        sb.append(String.format(" GENERAL CREW CALL: %-20s | ESTIMATED WRAP: 12 Hours\n", generalCrewCall));
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(" SHOOTING LOCATION / SET:\n");
        sb.append("   ").append(shootingLocation).append("\n\n");
        sb.append(" WEATHER & SUN CONDITIONS:\n");
        sb.append("   ").append(weatherAndSun).append("\n\n");
        sb.append(" SAFETY, MEDICAL & EMERGENCY PROTOCOLS:\n");
        sb.append("   Nearest Hospital: ").append(nearestHospital).append("\n");
        sb.append("   On-Set Safety Lead: Safety Officer & Certified Set Medic on Channel 1\n");
        sb.append("================================================================================\n");
        sb.append("                         SCHEDULED SCENES BREAKDOWN                             \n");
        sb.append("================================================================================\n");
        sb.append(String.format(" %-8s | %-38s | %-6s | %-6s | %-10s\n",
                "SCENE #", "TITLE / SLUGLINE", "PAGES", "D/N", "STATUS"));
        sb.append("---------+----------------------------------------+--------+--------+-----------\n");

        double totalPages = 0.0;
        if (scheduledSceneNumbers.isEmpty()) {
            sb.append("   [No scenes currently attached to this daily call sheet]\n");
        } else {
            for (int scNum : scheduledSceneNumbers) {
                Scene sc = (movie != null) ? movie.findSceneByNumber(scNum) : null;
                if (sc != null) {
                    totalPages += sc.getScriptPages();
                    String slug = sc.getSlugline();
                    String dn = slug.toUpperCase().contains("NIGHT") ? "NIGHT" : "DAY";
                    String displayTitle = sc.getTitle();
                    if (displayTitle.length() > 38) displayTitle = displayTitle.substring(0, 35) + "...";
                    sb.append(String.format(" Scene #%-2d| %-38s | %-6.1f | %-6s | %-10s\n",
                            sc.getSceneNumber(), displayTitle, sc.getScriptPages(), dn, sc.getStatus()));
                    sb.append(String.format("         | Slugline: %-52s\n", sc.getSlugline()));
                } else {
                    sb.append(String.format(" Scene #%-2d| [Scene record not found in movie roster]\n", scNum));
                }
            }
        }
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format(" Total Scheduled Pages Today: %.1f Pages (Est. Camera Roll: ~%.0f min runtime)\n",
                totalPages, totalPages));

        sb.append("================================================================================\n");
        sb.append("                            CAST & TALENT CALL TIMES                            \n");
        sb.append("================================================================================\n");
        sb.append(String.format(" %-8s | %-18s | %-16s | %-8s | %-8s | %-8s\n",
                "CAST ID", "ACTOR NAME", "CHARACTER ROLE", "PICKUP", "H/MU", "ON SET"));
        sb.append("---------+--------------------+------------------+----------+----------+---------\n");

        if (castCalls.isEmpty()) {
            sb.append("   [No individual talent call times assigned yet]\n");
        } else {
            for (CastCallEntry cc : castCalls) {
                sb.append(String.format(" %-8s| %-18s | %-16s | %-8s | %-8s | %-8s\n",
                        cc.getActorId(), cc.getActorName(), cc.getCharacterName(),
                        cc.getPickupTime(), cc.getMakeupCall(), cc.getOnSetCall()));
                if (cc.getInstructions() != null && !cc.getInstructions().isBlank()) {
                    sb.append(String.format("         | Instructions: %s\n", cc.getInstructions()));
                }
            }
        }

        sb.append("================================================================================\n");
        sb.append("                          DEPARTMENT & CREW DIRECTIVES                          \n");
        sb.append("================================================================================\n");
        if (departmentNotes == null || departmentNotes.isBlank()) {
            sb.append("   Standard departmental protocols in effect.\n");
        } else {
            String[] lines = departmentNotes.split("\n");
            for (String line : lines) {
                sb.append("   ").append(line).append("\n");
            }
        }

        sb.append("================================================================================\n");
        sb.append("                    DAILY SHOOTING REPORT (DSR WRAP STATUS)                     \n");
        sb.append("================================================================================\n");
        sb.append(String.format(" CURRENT STATUS : %s\n", status));
        sb.append(String.format(" PAGES COMPLETED: %.1f of %.1f Scheduled Pages\n", pagesShot, totalPages));
        sb.append(" WRAP / PRODUCTION NOTES:\n");
        sb.append("   ").append(wrapNotes).append("\n");
        sb.append("================================================================================\n");
        sb.append("       *** REPORT ALL SAFETY HAZARDS TO 1ST AD & MEDIC IMMEDIATELY ***          \n");
        sb.append("================================================================================\n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] Day %d/%d (%s) - Loc: %-22s | Scenes: %-2d | Status: %-10s",
                sheetId, dayNumber, totalDays, shootDate, shootingLocation, scheduledSceneNumbers.size(), status);
    }
}
