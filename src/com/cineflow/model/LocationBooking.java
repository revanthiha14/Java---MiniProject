package com.cineflow.model;

import com.cineflow.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a scheduled filming reservation for a ProductionLocation.
 * Enforces half-open interval conflict checks, date/time validity, and status transitions.
 */
public class LocationBooking {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private String bookingId;
    private String locationId;
    private String movieId;
    private int sceneNumber; // 0 indicates movie-level general location booking
    private String bookingDate; // YYYY-MM-DD
    private String startTime;   // HH:mm (24-hour)
    private String endTime;     // HH:mm (24-hour)
    private String status;      // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private double rentalCost;
    private String notes;

    public LocationBooking(String bookingId, String locationId, String movieId, int sceneNumber,
                           String bookingDate, String startTime, String endTime, double rentalCost,
                           String notes) throws ValidationException {
        this(bookingId, locationId, movieId, sceneNumber, bookingDate, startTime, endTime,
                "CONFIRMED", rentalCost, notes);
    }

    public LocationBooking(String bookingId, String locationId, String movieId, int sceneNumber,
                           String bookingDate, String startTime, String endTime, String status,
                           double rentalCost, String notes) throws ValidationException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new ValidationException("Booking ID", "Booking ID cannot be empty.");
        }
        if (locationId == null || locationId.trim().isEmpty()) {
            throw new ValidationException("Location ID", "Location ID reference cannot be empty.");
        }
        if (movieId == null || movieId.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID reference cannot be empty.");
        }
        if (sceneNumber < 0) {
            throw new ValidationException("Scene Number", "Scene number cannot be negative.");
        }
        if (rentalCost < 0.0) {
            throw new ValidationException("Rental Cost", "Rental cost cannot be negative.");
        }

        String normDate = parseAndNormalizeDate(bookingDate);
        String normStart = parseAndNormalizeTime(startTime, "Start Time");
        String normEnd = parseAndNormalizeTime(endTime, "End Time");

        LocalTime startLt = LocalTime.parse(normStart, TIME_FORMATTER);
        LocalTime endLt = LocalTime.parse(normEnd, TIME_FORMATTER);
        if (!endLt.isAfter(startLt)) {
            throw new ValidationException("Time Range",
                    "End time (" + normEnd + ") must be later than start time (" + normStart + ").");
        }

        this.bookingId = bookingId.trim().toUpperCase();
        this.locationId = locationId.trim().toUpperCase();
        this.movieId = movieId.trim().toUpperCase();
        this.sceneNumber = sceneNumber;
        this.bookingDate = normDate;
        this.startTime = normStart;
        this.endTime = normEnd;
        this.status = normalizeStatus(status);
        this.rentalCost = rentalCost;
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public static String parseAndNormalizeDate(String dateStr) throws ValidationException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ValidationException("Booking Date", "Booking date cannot be empty (expected YYYY-MM-DD).");
        }
        try {
            LocalDate d = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            return d.format(DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Booking Date", "Invalid date format '" + dateStr + "'. Expected YYYY-MM-DD.");
        }
    }

    public static String parseAndNormalizeTime(String timeStr, String fieldName) throws ValidationException {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " cannot be empty (expected HH:mm).");
        }
        String trimmed = timeStr.trim().toUpperCase();
        // Support standard HH:mm or 12-hour format like 09:00 AM / 2:30 PM
        try {
            if (trimmed.contains("AM") || trimmed.contains("PM")) {
                DateTimeFormatter df12 = DateTimeFormatter.ofPattern("h:mm a");
                DateTimeFormatter df12Pad = DateTimeFormatter.ofPattern("hh:mm a");
                LocalTime lt;
                try {
                    lt = LocalTime.parse(trimmed, df12Pad);
                } catch (Exception ex) {
                    lt = LocalTime.parse(trimmed, df12);
                }
                return lt.format(TIME_FORMATTER);
            } else {
                LocalTime lt = LocalTime.parse(trimmed, TIME_FORMATTER);
                return lt.format(TIME_FORMATTER);
            }
        } catch (Exception e) {
            throw new ValidationException(fieldName, "Invalid time format '" + timeStr + "'. Expected 24-hr HH:mm (e.g. 09:30, 14:00).");
        }
    }

    private String normalizeStatus(String s) {
        if (s == null || s.trim().isEmpty()) return "CONFIRMED";
        String upper = s.trim().toUpperCase();
        return switch (upper) {
            case "PENDING", "CANCELLED", "COMPLETED" -> upper;
            default -> "CONFIRMED";
        };
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) throws ValidationException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new ValidationException("Booking ID", "Booking ID cannot be empty.");
        }
        this.bookingId = bookingId.trim().toUpperCase();
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) throws ValidationException {
        if (locationId == null || locationId.trim().isEmpty()) {
            throw new ValidationException("Location ID", "Location ID cannot be empty.");
        }
        this.locationId = locationId.trim().toUpperCase();
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

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) throws ValidationException {
        this.bookingDate = parseAndNormalizeDate(bookingDate);
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setTimeInterval(String startTime, String endTime) throws ValidationException {
        String normStart = parseAndNormalizeTime(startTime, "Start Time");
        String normEnd = parseAndNormalizeTime(endTime, "End Time");
        LocalTime startLt = LocalTime.parse(normStart, TIME_FORMATTER);
        LocalTime endLt = LocalTime.parse(normEnd, TIME_FORMATTER);
        if (!endLt.isAfter(startLt)) {
            throw new ValidationException("Time Range",
                    "End time (" + normEnd + ") must be later than start time (" + normStart + ").");
        }
        this.startTime = normStart;
        this.endTime = normEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = normalizeStatus(status);
    }

    public double getRentalCost() {
        return rentalCost;
    }

    public void setRentalCost(double rentalCost) throws ValidationException {
        if (rentalCost < 0.0) {
            throw new ValidationException("Rental Cost", "Rental cost cannot be negative.");
        }
        this.rentalCost = rentalCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public LocalDate getParsedBookingDate() {
        return LocalDate.parse(bookingDate, DATE_FORMATTER);
    }

    public LocalTime getParsedStartTime() {
        return LocalTime.parse(startTime, TIME_FORMATTER);
    }

    public LocalTime getParsedEndTime() {
        return LocalTime.parse(endTime, TIME_FORMATTER);
    }

    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(getParsedBookingDate(), getParsedStartTime());
    }

    public LocalDateTime getEndDateTime() {
        return LocalDateTime.of(getParsedBookingDate(), getParsedEndTime());
    }

    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    /**
     * Checks if this booking's time slot conflicts with another booking using the
     * half-open interval rule:
     * newStart < existingEnd && existingStart < newEnd.
     * Note: Cancelled bookings do NOT conflict.
     */
    public boolean conflictsWith(LocationBooking other) {
        if (other == null || this.isCancelled() || other.isCancelled()) {
            return false;
        }
        if (!this.locationId.equalsIgnoreCase(other.locationId)) {
            return false;
        }
        LocalDateTime startA = this.getStartDateTime();
        LocalDateTime endA = this.getEndDateTime();
        LocalDateTime startB = other.getStartDateTime();
        LocalDateTime endB = other.getEndDateTime();

        // Half-open interval rule: startA < endB && startB < endA
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    public String getSceneReference() {
        return (sceneNumber > 0) ? "Scene #" + sceneNumber : "Movie-Wide";
    }

    @Override
    public String toString() {
        return String.format("[%s] Loc: %-8s | Movie: %-8s (%-10s) | Date: %s [%s - %s] | Status: %-9s | Cost: $%,8.2f%s",
                bookingId, locationId, movieId, getSceneReference(), bookingDate, startTime, endTime,
                status, rentalCost, notes.isEmpty() ? "" : " | Notes: " + notes);
    }
}
