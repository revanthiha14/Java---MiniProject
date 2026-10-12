package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ScheduleConflictException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.LocationBooking;
import com.cineflow.model.ProductionLocation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service managing filming locations and cross-movie location bookings.
 * Follows the decoupled service architecture established across CineFlow.
 * Enforces half-open interval conflict detection across ALL studio productions.
 */
public class LocationManager {
    private final Map<String, ProductionLocation> locations;
    private final List<LocationBooking> bookings;

    public LocationManager() {
        this.locations = new LinkedHashMap<>();
        this.bookings = new ArrayList<>();
    }

    // =========================================================================
    // LOCATION MANAGEMENT
    // =========================================================================

    public void addLocation(ProductionLocation location) throws ValidationException {
        if (location == null) {
            throw new ValidationException("Production Location", "Cannot add null location.");
        }
        String key = location.getId().trim().toUpperCase();
        if (locations.containsKey(key)) {
            throw new ValidationException("Location ID", "Location with ID '" + location.getId() + "' already exists.");
        }
        locations.put(key, location);
    }

    public ProductionLocation findLocationById(String locationId) {
        if (locationId == null) return null;
        return locations.get(locationId.trim().toUpperCase());
    }

    public ProductionLocation getLocationOrThrow(String locationId) throws ResourceNotFoundException {
        ProductionLocation loc = findLocationById(locationId);
        if (loc == null) {
            throw new ResourceNotFoundException("Production Location", (locationId != null ? locationId.trim() : "null"));
        }
        return loc;
    }

    public List<ProductionLocation> getAllLocations() {
        return new ArrayList<>(locations.values());
    }

    public List<ProductionLocation> searchLocations(String query, String type, String status) {
        List<ProductionLocation> results = new ArrayList<>();
        String q = (query != null) ? query.trim().toLowerCase() : "";
        String t = (type != null) ? type.trim().toUpperCase() : "";
        String s = (status != null) ? status.trim().toUpperCase() : "";

        for (ProductionLocation loc : locations.values()) {
            boolean matchesQuery = q.isEmpty()
                    || loc.getId().toLowerCase().contains(q)
                    || loc.getName().toLowerCase().contains(q)
                    || loc.getAddress().toLowerCase().contains(q)
                    || loc.getContactPerson().toLowerCase().contains(q)
                    || loc.getNotes().toLowerCase().contains(q);

            boolean matchesType = t.isEmpty() || loc.getLocationType().equalsIgnoreCase(t);
            boolean matchesStatus = s.isEmpty() || loc.getAvailabilityStatus().equalsIgnoreCase(s);

            if (matchesQuery && matchesType && matchesStatus) {
                results.add(loc);
            }
        }
        return results;
    }

    public void updateLocation(String locationId, String name, String address, String type,
                               String contactPerson, String contactDetails, int capacity,
                               String status, Double rentalCost, String notes)
            throws ResourceNotFoundException, ValidationException {
        ProductionLocation loc = getLocationOrThrow(locationId);
        if (name != null && !name.trim().isEmpty()) loc.setName(name);
        if (address != null && !address.trim().isEmpty()) loc.setAddress(address);
        if (type != null && !type.trim().isEmpty()) loc.setLocationType(type);
        if (contactPerson != null && !contactPerson.trim().isEmpty()) loc.setContactPerson(contactPerson);
        if (contactDetails != null && !contactDetails.trim().isEmpty()) loc.setContactDetails(contactDetails);
        if (capacity >= 0) loc.setCapacity(capacity);
        if (status != null && !status.trim().isEmpty()) loc.setAvailabilityStatus(status);
        if (rentalCost != null && rentalCost >= 0.0) loc.setRentalCostPerDay(rentalCost);
        if (notes != null) loc.setNotes(notes);
    }

    // =========================================================================
    // AVAILABILITY & CONFLICT DETECTION
    // =========================================================================

    /**
     * Checks if a location is free for the specified date and half-open time interval [start, end).
     * Two bookings conflict when:
     * - They refer to the same location
     * - Their dates and time intervals overlap (newStart < existingEnd && existingStart < newEnd)
     * Cancelled bookings do NOT block availability.
     */
    public boolean isLocationAvailable(String locationId, String date, String startTime, String endTime)
            throws ValidationException {
        String normDate = LocationBooking.parseAndNormalizeDate(date);
        String normStart = LocationBooking.parseAndNormalizeTime(startTime, "Start Time");
        String normEnd = LocationBooking.parseAndNormalizeTime(endTime, "End Time");

        LocalDateTime reqStart = LocalDateTime.of(LocalDate.parse(normDate), LocalTime.parse(normStart));
        LocalDateTime reqEnd = LocalDateTime.of(LocalDate.parse(normDate), LocalTime.parse(normEnd));

        if (!reqEnd.isAfter(reqStart)) {
            throw new ValidationException("Time Range", "End time must be later than start time.");
        }

        for (LocationBooking b : bookings) {
            if (b.isCancelled()) continue;
            if (!b.getLocationId().equalsIgnoreCase(locationId.trim())) continue;
            if (!b.isConfirmed() && !b.isCompleted()) continue; // Only confirmed/completed block

            LocalDateTime bStart = b.getStartDateTime();
            LocalDateTime bEnd = b.getEndDateTime();

            // Interval rule: newStart < existingEnd && existingStart < newEnd
            if (reqStart.isBefore(bEnd) && bStart.isBefore(reqEnd)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds any overlapping confirmed or completed bookings for a location and time window.
     */
    public List<LocationBooking> getConflictingBookings(String locationId, String date, String startTime, String endTime)
            throws ValidationException {
        List<LocationBooking> conflicts = new ArrayList<>();
        String normDate = LocationBooking.parseAndNormalizeDate(date);
        String normStart = LocationBooking.parseAndNormalizeTime(startTime, "Start Time");
        String normEnd = LocationBooking.parseAndNormalizeTime(endTime, "End Time");

        LocalDateTime reqStart = LocalDateTime.of(LocalDate.parse(normDate), LocalTime.parse(normStart));
        LocalDateTime reqEnd = LocalDateTime.of(LocalDate.parse(normDate), LocalTime.parse(normEnd));

        for (LocationBooking b : bookings) {
            if (b.isCancelled()) continue;
            if (!b.getLocationId().equalsIgnoreCase(locationId.trim())) continue;
            if (!b.isConfirmed() && !b.isCompleted()) continue;

            LocalDateTime bStart = b.getStartDateTime();
            LocalDateTime bEnd = b.getEndDateTime();

            if (reqStart.isBefore(bEnd) && bStart.isBefore(reqEnd)) {
                conflicts.add(b);
            }
        }
        return conflicts;
    }

    /**
     * Finds any overlapping PENDING bookings (used to generate non-blocking user warnings).
     */
    public List<LocationBooking> getPendingOverlaps(String locationId, String date, String startTime, String endTime)
            throws ValidationException {
        List<LocationBooking> pendingList = new ArrayList<>();
        String normDate = LocationBooking.parseAndNormalizeDate(date);
        String normStart = LocationBooking.parseAndNormalizeTime(startTime, "Start Time");
        String normEnd = LocationBooking.parseAndNormalizeTime(endTime, "End Time");

        LocalDateTime reqStart = LocalDateTime.of(LocalDate.parse(normDate), LocalTime.parse(normStart));
        LocalDateTime reqEnd = LocalDateTime.of(LocalDate.parse(normDate), LocalTime.parse(normEnd));

        for (LocationBooking b : bookings) {
            if (!b.isPending()) continue;
            if (!b.getLocationId().equalsIgnoreCase(locationId.trim())) continue;

            LocalDateTime bStart = b.getStartDateTime();
            LocalDateTime bEnd = b.getEndDateTime();

            if (reqStart.isBefore(bEnd) && bStart.isBefore(reqEnd)) {
                pendingList.add(b);
            }
        }
        return pendingList;
    }

    // =========================================================================
    // BOOKING OPERATIONS
    // =========================================================================

    /**
     * Creates a new booking after checking location existence, duplicate booking ID,
     * and confirmed schedule conflicts across all movies.
     * Returns a list of warning messages (e.g. if overlapping pending bookings exist).
     */
    public List<String> createBooking(LocationBooking booking)
            throws ResourceNotFoundException, ScheduleConflictException, ValidationException {
        if (booking == null) {
            throw new ValidationException("Booking", "Cannot create null booking.");
        }

        // Validate location exists
        ProductionLocation location = getLocationOrThrow(booking.getLocationId());

        // Validate unique booking ID
        if (findBookingById(booking.getBookingId()) != null) {
            throw new ValidationException("Booking ID", "Booking ID '" + booking.getBookingId() + "' already exists.");
        }

        // Conflict detection: only check confirmed/completed if this new booking is confirmed or pending
        List<String> warnings = new ArrayList<>();
        if (!booking.isCancelled()) {
            List<LocationBooking> conflicts = getConflictingBookings(booking.getLocationId(),
                    booking.getBookingDate(), booking.getStartTime(), booking.getEndTime());

            if (!conflicts.isEmpty()) {
                LocationBooking first = conflicts.get(0);
                throw new ScheduleConflictException(location.getName() + " [" + location.getId() + "]",
                        booking.getBookingDate() + " " + booking.getStartTime() + "-" + booking.getEndTime(),
                        "Location conflict! Already booked by Movie '" + first.getMovieId()
                                + "' (" + first.getSceneReference() + ") from "
                                + first.getStartTime() + " to " + first.getEndTime()
                                + " [Booking ID: " + first.getBookingId() + "].");
            }

            // Pending booking warning
            List<LocationBooking> pendingOverlaps = getPendingOverlaps(booking.getLocationId(),
                    booking.getBookingDate(), booking.getStartTime(), booking.getEndTime());
            for (LocationBooking p : pendingOverlaps) {
                if (!p.getBookingId().equalsIgnoreCase(booking.getBookingId())) {
                    warnings.add("Slot overlaps with PENDING booking [" + p.getBookingId()
                            + "] for Movie '" + p.getMovieId() + "' (" + p.getSceneReference()
                            + ", " + p.getStartTime() + " - " + p.getEndTime() + ").");
                }
            }
        }

        bookings.add(booking);
        return warnings;
    }

    public List<String> createBooking(String bookingId, String locationId, String movieId, int sceneNumber,
                                      String bookingDate, String startTime, String endTime, String status,
                                      double rentalCost, String notes)
            throws ResourceNotFoundException, ScheduleConflictException, ValidationException {
        LocationBooking booking = new LocationBooking(bookingId, locationId, movieId, sceneNumber,
                bookingDate, startTime, endTime, status, rentalCost, notes);
        return createBooking(booking);
    }

    public void confirmBooking(String bookingId) throws ResourceNotFoundException, ScheduleConflictException, ValidationException {
        LocationBooking b = getBookingOrThrow(bookingId);
        if (b.isConfirmed()) return;
        if (b.isCancelled()) {
            throw new ValidationException("Booking Status", "Cannot confirm a cancelled booking directly. Create a new booking or update status.");
        }

        // Check conflicts before confirming
        for (LocationBooking other : bookings) {
            if (other == b || other.isCancelled()) continue;
            if ((other.isConfirmed() || other.isCompleted()) && b.conflictsWith(other)) {
                throw new ScheduleConflictException(b.getLocationId(), b.getBookingDate(),
                        "Cannot confirm booking [" + bookingId + "]. Slot overlaps with confirmed booking ["
                                + other.getBookingId() + "] for Movie '" + other.getMovieId() + "'.");
            }
        }
        b.setStatus("CONFIRMED");
    }

    public void cancelBooking(String bookingId) throws ResourceNotFoundException {
        LocationBooking b = getBookingOrThrow(bookingId);
        b.setStatus("CANCELLED");
    }

    public void completeBooking(String bookingId) throws ResourceNotFoundException {
        LocationBooking b = getBookingOrThrow(bookingId);
        b.setStatus("COMPLETED");
    }

    public LocationBooking findBookingById(String bookingId) {
        if (bookingId == null) return null;
        for (LocationBooking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(bookingId.trim())) {
                return b;
            }
        }
        return null;
    }

    public LocationBooking getBookingOrThrow(String bookingId) throws ResourceNotFoundException {
        LocationBooking b = findBookingById(bookingId);
        if (b == null) {
            throw new ResourceNotFoundException("Location Booking", (bookingId != null ? bookingId.trim() : "null"));
        }
        return b;
    }

    public List<LocationBooking> getBookingsForMovie(String movieId) {
        List<LocationBooking> list = new ArrayList<>();
        if (movieId == null) return list;
        for (LocationBooking b : bookings) {
            if (b.getMovieId().equalsIgnoreCase(movieId.trim())) {
                list.add(b);
            }
        }
        return list;
    }

    public List<LocationBooking> getBookingsForScene(String movieId, int sceneNumber) {
        List<LocationBooking> list = new ArrayList<>();
        if (movieId == null) return list;
        for (LocationBooking b : bookings) {
            if (b.getMovieId().equalsIgnoreCase(movieId.trim()) && b.getSceneNumber() == sceneNumber) {
                list.add(b);
            }
        }
        return list;
    }

    public List<LocationBooking> getBookingsForLocation(String locationId) {
        List<LocationBooking> list = new ArrayList<>();
        if (locationId == null) return list;
        for (LocationBooking b : bookings) {
            if (b.getLocationId().equalsIgnoreCase(locationId.trim())) {
                list.add(b);
            }
        }
        return list;
    }

    public List<LocationBooking> getBookingsForDate(String date) {
        List<LocationBooking> list = new ArrayList<>();
        if (date == null) return list;
        for (LocationBooking b : bookings) {
            if (b.getBookingDate().equalsIgnoreCase(date.trim())) {
                list.add(b);
            }
        }
        return list;
    }

    public List<LocationBooking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public void addLoadedBooking(LocationBooking booking) {
        if (booking != null) {
            bookings.add(booking);
        }
    }

    public double calculateTotalRentalCostForMovie(String movieId) {
        if (movieId == null) return 0.0;
        double sum = 0.0;
        for (LocationBooking b : bookings) {
            if (b.getMovieId().equalsIgnoreCase(movieId.trim()) && !b.isCancelled()) {
                sum += b.getRentalCost();
            }
        }
        return sum;
    }

    public double calculateTotalRentalCostForLocation(String locationId) {
        if (locationId == null) return 0.0;
        double sum = 0.0;
        for (LocationBooking b : bookings) {
            if (b.getLocationId().equalsIgnoreCase(locationId.trim()) && !b.isCancelled()) {
                sum += b.getRentalCost();
            }
        }
        return sum;
    }

    public double calculateTotalRentalCost() {
        double sum = 0.0;
        for (LocationBooking b : bookings) {
            if (!b.isCancelled()) {
                sum += b.getRentalCost();
            }
        }
        return sum;
    }

    public String generateNextBookingId() {
        int nextNum = bookings.size() + 1;
        String id;
        do {
            id = "BKG-" + String.format("%03d", nextNum++);
        } while (findBookingById(id) != null);
        return id;
    }

    public String generateNextLocationId() {
        int nextNum = locations.size() + 1;
        String id;
        do {
            id = "LOC-" + String.format("%02d", nextNum++);
        } while (findLocationById(id) != null);
        return id;
    }

    public void clear() {
        locations.clear();
        bookings.clear();
    }
}
