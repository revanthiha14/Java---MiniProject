package com.cineflow.model;

import com.cineflow.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a scheduled reservation for a physical production inventory item (prop, costume, or equipment).
 * Enforces quantity reservation over time windows using half-open interval semantics.
 */
public class EquipmentReservation {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private String reservationId;
    private String itemId;
    private String movieId;
    private int sceneNumber; // 0 indicates movie-level general allocation
    private int quantity;
    private String startDateTime; // YYYY-MM-DD HH:mm
    private String endDateTime;   // YYYY-MM-DD HH:mm
    private String status;        // PENDING, CONFIRMED, CANCELLED, CHECKED_OUT, RETURNED
    private String assignmentId;  // Linked ItemAssignment when checked out, or "NONE"
    private String notes;

    public EquipmentReservation(String reservationId, String itemId, String movieId, int sceneNumber,
                                int quantity, String startDateTime, String endDateTime, String notes)
            throws ValidationException {
        this(reservationId, itemId, movieId, sceneNumber, quantity, startDateTime, endDateTime,
                "CONFIRMED", "NONE", notes);
    }

    public EquipmentReservation(String reservationId, String itemId, String movieId, int sceneNumber,
                                int quantity, String startDateTime, String endDateTime, String status,
                                String assignmentId, String notes) throws ValidationException {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new ValidationException("Reservation ID", "Reservation ID cannot be empty.");
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new ValidationException("Item ID", "Item ID reference cannot be empty.");
        }
        if (movieId == null || movieId.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID reference cannot be empty.");
        }
        if (sceneNumber < 0) {
            throw new ValidationException("Scene Number", "Scene number cannot be negative.");
        }
        if (quantity <= 0) {
            throw new ValidationException("Quantity", "Reservation quantity must be greater than zero.");
        }

        String normStart = parseAndNormalizeDateTime(startDateTime, "Start Date/Time");
        String normEnd = parseAndNormalizeDateTime(endDateTime, "End Date/Time");

        LocalDateTime startDt = LocalDateTime.parse(normStart, DATE_TIME_FORMATTER);
        LocalDateTime endDt = LocalDateTime.parse(normEnd, DATE_TIME_FORMATTER);
        if (!endDt.isAfter(startDt)) {
            throw new ValidationException("Reservation Interval",
                    "End date/time (" + normEnd + ") must be later than start date/time (" + normStart + ").");
        }

        this.reservationId = reservationId.trim().toUpperCase();
        this.itemId = itemId.trim().toUpperCase();
        this.movieId = movieId.trim().toUpperCase();
        this.sceneNumber = sceneNumber;
        this.quantity = quantity;
        this.startDateTime = normStart;
        this.endDateTime = normEnd;
        this.status = normalizeStatus(status);
        this.assignmentId = (assignmentId != null && !assignmentId.trim().isEmpty()) ? assignmentId.trim().toUpperCase() : "NONE";
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public static String parseAndNormalizeDateTime(String input, String fieldName) throws ValidationException {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " cannot be empty (expected YYYY-MM-DD HH:mm).");
        }
        String trimmed = input.trim();
        // If provided as "YYYY-MM-DD" without time, default to 08:00 for start, 18:00 for end
        if (trimmed.length() == 10 && !trimmed.contains(" ")) {
            try {
                LocalDate d = LocalDate.parse(trimmed, DATE_FORMATTER);
                LocalTime t = fieldName.toLowerCase().contains("end") ? LocalTime.of(18, 0) : LocalTime.of(8, 0);
                return LocalDateTime.of(d, t).format(DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new ValidationException(fieldName, "Invalid date format '" + input + "'. Expected YYYY-MM-DD.");
            }
        }

        try {
            // First attempt direct parsing with DATE_TIME_FORMATTER
            LocalDateTime dt = LocalDateTime.parse(trimmed, DATE_TIME_FORMATTER);
            return dt.format(DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ignored) {}

        // Attempt parsing with date and 12-hour or alternative formats (e.g. 2026-11-10 09:00 AM)
        try {
            int spaceIdx = trimmed.indexOf(' ');
            if (spaceIdx > 0) {
                String dPart = trimmed.substring(0, spaceIdx).trim();
                String tPart = trimmed.substring(spaceIdx + 1).trim();
                LocalDate date = LocalDate.parse(dPart, DATE_FORMATTER);
                String normTime = LocationBooking.parseAndNormalizeTime(tPart, fieldName);
                LocalTime time = LocalTime.parse(normTime, TIME_FORMATTER);
                return LocalDateTime.of(date, time).format(DATE_TIME_FORMATTER);
            }
        } catch (Exception ex) {
            throw new ValidationException(fieldName,
                    "Invalid date/time format '" + input + "'. Expected 'YYYY-MM-DD HH:mm' (e.g. 2026-11-10 09:00).");
        }

        throw new ValidationException(fieldName,
                "Invalid date/time format '" + input + "'. Expected 'YYYY-MM-DD HH:mm' (e.g. 2026-11-10 09:00).");
    }

    private String normalizeStatus(String s) {
        if (s == null || s.trim().isEmpty()) return "CONFIRMED";
        String upper = s.trim().toUpperCase();
        return switch (upper) {
            case "PENDING", "CANCELLED", "CHECKED_OUT", "RETURNED" -> upper;
            default -> "CONFIRMED";
        };
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) throws ValidationException {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new ValidationException("Reservation ID", "Reservation ID cannot be empty.");
        }
        this.reservationId = reservationId.trim().toUpperCase();
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) throws ValidationException {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new ValidationException("Item ID", "Item ID cannot be empty.");
        }
        this.itemId = itemId.trim().toUpperCase();
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) throws ValidationException {
        if (movieId == null || movieId.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID cannot be empty.");
        }
        this.movieId = movieId.trim().toUpperCase();
    }

    public int getSceneNumber() {
        return sceneNumber;
    }

    public void setSceneNumber(int sceneNumber) throws ValidationException {
        if (sceneNumber < 0) {
            throw new ValidationException("Scene Number", "Scene number cannot be negative.");
        }
        this.sceneNumber = sceneNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException("Quantity", "Reservation quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setInterval(String startDateTime, String endDateTime) throws ValidationException {
        String normStart = parseAndNormalizeDateTime(startDateTime, "Start Date/Time");
        String normEnd = parseAndNormalizeDateTime(endDateTime, "End Date/Time");
        LocalDateTime startDt = LocalDateTime.parse(normStart, DATE_TIME_FORMATTER);
        LocalDateTime endDt = LocalDateTime.parse(normEnd, DATE_TIME_FORMATTER);
        if (!endDt.isAfter(startDt)) {
            throw new ValidationException("Reservation Interval",
                    "End date/time (" + normEnd + ") must be later than start date/time (" + normStart + ").");
        }
        this.startDateTime = normStart;
        this.endDateTime = normEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = normalizeStatus(status);
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = (assignmentId != null && !assignmentId.trim().isEmpty()) ? assignmentId.trim().toUpperCase() : "NONE";
    }

    public boolean hasAssignment() {
        return assignmentId != null && !assignmentId.isEmpty() && !"NONE".equalsIgnoreCase(assignmentId);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public LocalDateTime getParsedStartDateTime() {
        return LocalDateTime.parse(startDateTime, DATE_TIME_FORMATTER);
    }

    public LocalDateTime getParsedEndDateTime() {
        return LocalDateTime.parse(endDateTime, DATE_TIME_FORMATTER);
    }

    public LocalDate getStartDate() {
        return getParsedStartDateTime().toLocalDate();
    }

    public LocalDate getEndDate() {
        return getParsedEndDateTime().toLocalDate();
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }

    public boolean isCheckedOut() {
        return "CHECKED_OUT".equalsIgnoreCase(status);
    }

    public boolean isReturned() {
        return "RETURNED".equalsIgnoreCase(status);
    }

    public boolean isActive() {
        return isConfirmed() || isCheckedOut();
    }

    /**
     * Checks if this reservation overlaps with a given time window using half-open interval:
     * newStart < existingEnd && existingStart < newEnd.
     */
    public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
        if (isCancelled() || isReturned()) return false;
        LocalDateTime resStart = getParsedStartDateTime();
        LocalDateTime resEnd = getParsedEndDateTime();
        return start.isBefore(resEnd) && resStart.isBefore(end);
    }

    public String getSceneReference() {
        return (sceneNumber > 0) ? "Scene #" + sceneNumber : "Movie-Wide";
    }

    @Override
    public String toString() {
        String asgTag = hasAssignment() ? " [Asg: " + assignmentId + "]" : "";
        return String.format("[%s] Item: %-8s -> %-8s (%-10s) | Qty: %-2d | %s to %s | Status: %-11s%s%s",
                reservationId, itemId, movieId, getSceneReference(), quantity,
                startDateTime, endDateTime, status, asgTag, notes.isEmpty() ? "" : " | Notes: " + notes);
    }
}
