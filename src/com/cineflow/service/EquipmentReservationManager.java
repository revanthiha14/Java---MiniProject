package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ScheduleConflictException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.EquipmentReservation;
import com.cineflow.model.InventoryItem;
import com.cineflow.model.ItemAssignment;
import com.cineflow.model.Movie;
import com.cineflow.model.MovieStatus;
import com.cineflow.model.Scene;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service managing equipment, prop, and costume reservations.
 * Enforces cross-movie shared inventory competition, half-open interval availability checks,
 * non-duplicative state transitions, and return reconciliation.
 */
public class EquipmentReservationManager {
    private final InventoryManager inventoryManager;
    private final List<EquipmentReservation> reservations;

    public EquipmentReservationManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        this.reservations = new ArrayList<>();
    }

    // =========================================================================
    // AVAILABILITY CALCULATION (HALF-OPEN INTERVAL & SHARED INVENTORY)
    // =========================================================================

    /**
     * Calculates the maximum available quantity of an inventory item during a requested time window.
     * Formula: Total usable quantity (total - damaged - lost) minus peak committed units during [start, end).
     * Excludes cancelled and returned reservations. Avoids double-counting checked-out reservations.
     */
    public int getAvailableQuantityForPeriod(String itemId, String startDateTimeStr, String endDateTimeStr)
            throws ResourceNotFoundException, ValidationException {
        InventoryItem item = inventoryManager.getItemOrThrow(itemId);

        String normStart = EquipmentReservation.parseAndNormalizeDateTime(startDateTimeStr, "Start Date/Time");
        String normEnd = EquipmentReservation.parseAndNormalizeDateTime(endDateTimeStr, "End Date/Time");
        LocalDateTime reqStart = LocalDateTime.parse(normStart, EquipmentReservation.DATE_TIME_FORMATTER);
        LocalDateTime reqEnd = LocalDateTime.parse(normEnd, EquipmentReservation.DATE_TIME_FORMATTER);

        if (!reqEnd.isAfter(reqStart)) {
            throw new ValidationException("Reservation Interval", "End date/time must be later than start date/time.");
        }

        // Total usable units in studio inventory
        int usableQty = item.getTotalQuantity() - item.getDamagedQuantity() - item.getLostQuantity();
        if (usableQty <= 0) return 0;

        // Active overlapping reservations for this item
        List<EquipmentReservation> overlappingRes = new ArrayList<>();
        for (EquipmentReservation r : reservations) {
            if (!r.getItemId().equalsIgnoreCase(item.getId())) continue;
            if (r.isCancelled() || r.isReturned()) continue;
            if (r.overlapsWith(reqStart, reqEnd)) {
                overlappingRes.add(r);
            }
        }

        // Concurrency event check points
        List<LocalDateTime> checkPoints = new ArrayList<>();
        checkPoints.add(reqStart);
        for (EquipmentReservation r : overlappingRes) {
            LocalDateTime rStart = r.getParsedStartDateTime();
            if (!rStart.isBefore(reqStart) && rStart.isBefore(reqEnd)) {
                checkPoints.add(rStart);
            }
        }

        // Standalone checkouts (created without reservations)
        int standaloneCheckedOut = 0;
        for (ItemAssignment asg : inventoryManager.getActiveAssignments()) {
            if (!asg.getItemId().equalsIgnoreCase(item.getId())) continue;
            boolean isLinked = false;
            for (EquipmentReservation r : reservations) {
                if (asg.getAssignmentId().equalsIgnoreCase(r.getAssignmentId())) {
                    isLinked = true;
                    break;
                }
            }
            if (!isLinked) {
                standaloneCheckedOut += asg.getQuantity();
            }
        }

        int maxConcurrentReserved = 0;
        for (LocalDateTime t : checkPoints) {
            int concurrentAtT = 0;
            for (EquipmentReservation r : overlappingRes) {
                // Half-open interval: [start, end) contains t
                if (!t.isBefore(r.getParsedStartDateTime()) && t.isBefore(r.getParsedEndDateTime())) {
                    concurrentAtT += r.getQuantity();
                }
            }
            if (concurrentAtT > maxConcurrentReserved) {
                maxConcurrentReserved = concurrentAtT;
            }
        }

        int totalCommitted = maxConcurrentReserved + standaloneCheckedOut;
        int available = usableQty - totalCommitted;
        return Math.max(0, available);
    }

    // =========================================================================
    // RESERVATION OPERATIONS
    // =========================================================================

    /**
     * Reserves an inventory item for a movie and optional scene across a future date/time interval.
     */
    public EquipmentReservation reserveItem(String reservationId, String itemId, Movie movie, int sceneNumber,
                                           int quantity, String startDateTime, String endDateTime,
                                           String notes)
            throws ResourceNotFoundException, ValidationException, ScheduleConflictException {
        if (movie == null) {
            throw new ValidationException("Movie", "Movie reference cannot be null.");
        }
        if (movie.getStatus() == MovieStatus.DROPPED) {
            throw new ValidationException("Movie Status",
                    "Cannot reserve equipment for a DROPPED / CANCELLED movie ('" + movie.getTitle() + "').");
        }
        if (quantity <= 0) {
            throw new ValidationException("Quantity", "Reservation quantity must be greater than zero.");
        }

        InventoryItem item = inventoryManager.getItemOrThrow(itemId);

        // Validate scene number if specified
        if (sceneNumber > 0) {
            Scene sc = movie.findSceneByNumber(sceneNumber);
            if (sc == null) {
                throw new ResourceNotFoundException("Scene #" + sceneNumber,
                        "Scene not found in movie '" + movie.getTitle() + "'.");
            }
        }

        String actualResId = (reservationId != null && !reservationId.trim().isEmpty())
                ? reservationId.trim().toUpperCase()
                : generateNextReservationId();

        if (findReservationById(actualResId) != null) {
            throw new ValidationException("Reservation ID", "Reservation ID '" + actualResId + "' already exists.");
        }

        // Check availability against shared multi-movie inventory
        int available = getAvailableQuantityForPeriod(itemId, startDateTime, endDateTime);
        if (quantity > available) {
            throw new ScheduleConflictException(item.getName() + " [" + item.getId() + "]",
                    startDateTime + " to " + endDateTime,
                    "Insufficient equipment stock! Requested " + quantity + " unit(s) of '" + item.getName()
                            + "', but only " + available + " unit(s) are available during that time window across studio productions.");
        }

        EquipmentReservation res = new EquipmentReservation(actualResId, item.getId(), movie.getId(),
                sceneNumber, quantity, startDateTime, endDateTime, "CONFIRMED", "NONE", notes);
        reservations.add(res);
        return res;
    }

    public void confirmReservation(String reservationId)
            throws ResourceNotFoundException, ScheduleConflictException, ValidationException {
        EquipmentReservation res = getReservationOrThrow(reservationId);
        if (res.isConfirmed()) return;
        if (res.isCancelled() || res.isReturned()) {
            throw new ValidationException("Reservation Status",
                    "Cannot re-confirm a " + res.getStatus() + " reservation. Create a new reservation.");
        }

        // Verify available capacity before confirming
        int avail = getAvailableQuantityForPeriod(res.getItemId(), res.getStartDateTime(), res.getEndDateTime());
        if (res.getQuantity() > avail) {
            throw new ScheduleConflictException(res.getItemId(), res.getStartDateTime(),
                    "Cannot confirm reservation [" + reservationId + "]. Required quantity "
                            + res.getQuantity() + " exceeds available stock " + avail + ".");
        }
        res.setStatus("CONFIRMED");
    }

    public void cancelReservation(String reservationId) throws ResourceNotFoundException, ValidationException {
        EquipmentReservation res = getReservationOrThrow(reservationId);
        if (res.isCheckedOut()) {
            throw new ValidationException("Reservation Status",
                    "Cannot cancel reservation [" + reservationId + "] while items are currently checked out. Return items first.");
        }
        res.setStatus("CANCELLED");
    }

    /**
     * Checks equipment out when the reservation period begins.
     * Transitions reservation to CHECKED_OUT and creates a linked ItemAssignment.
     * Avoids duplicate checkout and prevents double-counting.
     */
    public ItemAssignment checkoutReservation(String reservationId, Movie movie)
            throws ResourceNotFoundException, ValidationException {
        EquipmentReservation res = getReservationOrThrow(reservationId);
        if (res.isCheckedOut()) {
            throw new ValidationException("Duplicate Checkout",
                    "Reservation [" + reservationId + "] is already checked out (Assignment: " + res.getAssignmentId() + ").");
        }
        if (res.isCancelled() || res.isReturned()) {
            throw new ValidationException("Reservation Status",
                    "Cannot checkout reservation [" + reservationId + "] because its status is " + res.getStatus() + ".");
        }

        InventoryItem item = inventoryManager.getItemOrThrow(res.getItemId());
        if (item.getAvailableQuantity() < res.getQuantity()) {
            throw new ValidationException("Physical Inventory Depleted",
                    "Cannot checkout " + res.getQuantity() + " unit(s) of '" + item.getName()
                            + "'. Only " + item.getAvailableQuantity() + " physical units currently on shelf.");
        }

        // Deduct from immediate shelf availability
        item.checkout(res.getQuantity());

        // Generate assignment
        String assignmentId = "ASG-RES-" + res.getReservationId().replace("RES-", "");
        // If ID already in assignments, append sequence
        int suffix = 1;
        while (inventoryManager.findAssignmentById(assignmentId) != null) {
            assignmentId = "ASG-RES-" + res.getReservationId().replace("RES-", "") + "-" + suffix++;
        }

        String assignedDate = res.getStartDate().toString();
        String returnExpected = res.getEndDate().toString();
        String notes = "Reservation Checkout: " + res.getNotes();

        ItemAssignment asg = new ItemAssignment(assignmentId, res.getItemId(), res.getMovieId(),
                res.getSceneNumber(), res.getQuantity(), "CHECKED_OUT", assignedDate, returnExpected, notes);
        inventoryManager.addLoadedAssignment(asg);

        res.setStatus("CHECKED_OUT");
        res.setAssignmentId(assignmentId);
        return asg;
    }

    /**
     * Reconciles return of equipment checked out via reservation, logging good, damaged, and lost units.
     */
    public void returnReservation(String reservationId, int goodQty, int damagedQty, int lostQty,
                                  String returnDate, String returnNotes)
            throws ResourceNotFoundException, ValidationException {
        EquipmentReservation res = getReservationOrThrow(reservationId);
        if (!res.isCheckedOut()) {
            throw new ValidationException("Reservation Status",
                    "Reservation [" + reservationId + "] is not in CHECKED_OUT state (Current: " + res.getStatus() + ").");
        }

        int totalReturning = goodQty + damagedQty + lostQty;
        if (totalReturning != res.getQuantity()) {
            throw new ValidationException("Return Quantity Mismatch",
                    "Total returned units (" + totalReturning + ") must match reserved quantity (" + res.getQuantity() + ").");
        }

        // Reconcile linked assignment if present
        if (res.hasAssignment()) {
            ItemAssignment asg = inventoryManager.findAssignmentById(res.getAssignmentId());
            if (asg != null && asg.isCheckedOut()) {
                inventoryManager.returnItem(asg.getAssignmentId(), goodQty, damagedQty, lostQty, returnDate, returnNotes);
            } else {
                InventoryItem item = inventoryManager.getItemOrThrow(res.getItemId());
                item.processReturn(goodQty, damagedQty, lostQty);
            }
        } else {
            InventoryItem item = inventoryManager.getItemOrThrow(res.getItemId());
            item.processReturn(goodQty, damagedQty, lostQty);
        }

        res.setStatus("RETURNED");
        if (returnNotes != null && !returnNotes.trim().isEmpty()) {
            String existing = res.getNotes();
            res.setNotes(existing.isEmpty() ? returnNotes.trim() : existing + " | Return: " + returnNotes.trim());
        }
    }

    // =========================================================================
    // LOOKUPS & QUERIES
    // =========================================================================

    public EquipmentReservation findReservationById(String reservationId) {
        if (reservationId == null) return null;
        for (EquipmentReservation r : reservations) {
            if (r.getReservationId().equalsIgnoreCase(reservationId.trim())) {
                return r;
            }
        }
        return null;
    }

    public EquipmentReservation getReservationOrThrow(String reservationId) throws ResourceNotFoundException {
        EquipmentReservation r = findReservationById(reservationId);
        if (r == null) {
            throw new ResourceNotFoundException("Equipment Reservation", (reservationId != null ? reservationId.trim() : "null"));
        }
        return r;
    }

    public List<EquipmentReservation> getReservationsForMovie(String movieId) {
        List<EquipmentReservation> list = new ArrayList<>();
        if (movieId == null) return list;
        for (EquipmentReservation r : reservations) {
            if (r.getMovieId().equalsIgnoreCase(movieId.trim())) {
                list.add(r);
            }
        }
        return list;
    }

    public List<EquipmentReservation> getReservationsForScene(String movieId, int sceneNumber) {
        List<EquipmentReservation> list = new ArrayList<>();
        if (movieId == null) return list;
        for (EquipmentReservation r : reservations) {
            if (r.getMovieId().equalsIgnoreCase(movieId.trim()) && r.getSceneNumber() == sceneNumber) {
                list.add(r);
            }
        }
        return list;
    }

    public List<EquipmentReservation> getReservationsForItem(String itemId) {
        List<EquipmentReservation> list = new ArrayList<>();
        if (itemId == null) return list;
        for (EquipmentReservation r : reservations) {
            if (r.getItemId().equalsIgnoreCase(itemId.trim())) {
                list.add(r);
            }
        }
        return list;
    }

    public List<EquipmentReservation> getReservationsForDate(String dateStr) {
        List<EquipmentReservation> list = new ArrayList<>();
        if (dateStr == null) return list;
        LocalDate target = LocalDate.parse(dateStr.trim(), EquipmentReservation.DATE_FORMATTER);
        for (EquipmentReservation r : reservations) {
            if (!target.isBefore(r.getStartDate()) && !target.isAfter(r.getEndDate())) {
                list.add(r);
            }
        }
        return list;
    }

    public List<EquipmentReservation> getUpcomingReservations() {
        List<EquipmentReservation> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (EquipmentReservation r : reservations) {
            if (r.isConfirmed() || r.isPending()) {
                if (r.getParsedEndDateTime().isAfter(now)) {
                    list.add(r);
                }
            }
        }
        list.sort(Comparator.comparing(EquipmentReservation::getParsedStartDateTime));
        return list;
    }

    public List<EquipmentReservation> getOverdueReservations() {
        List<EquipmentReservation> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (EquipmentReservation r : reservations) {
            if (r.isCheckedOut() && r.getParsedEndDateTime().isBefore(now)) {
                list.add(r);
            }
        }
        return list;
    }

    public List<ItemAssignment> getOverdueAssignments() {
        List<ItemAssignment> list = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (ItemAssignment a : inventoryManager.getActiveAssignments()) {
            if (a.getReturnDate() != null && !a.getReturnDate().equalsIgnoreCase("TBD")) {
                try {
                    LocalDate ret = LocalDate.parse(a.getReturnDate().trim(), EquipmentReservation.DATE_FORMATTER);
                    if (ret.isBefore(today)) {
                        list.add(a);
                    }
                } catch (Exception ignored) {}
            }
        }
        return list;
    }

    public List<EquipmentReservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public void addLoadedReservation(EquipmentReservation res) {
        if (res != null) {
            reservations.add(res);
        }
    }

    public String generateNextReservationId() {
        int nextNum = reservations.size() + 1;
        String id;
        do {
            id = "RES-" + String.format("%03d", nextNum++);
        } while (findReservationById(id) != null);
        return id;
    }

    public void clear() {
        reservations.clear();
    }
}
