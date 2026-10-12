package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ScheduleConflictException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.EquipmentReservation;
import com.cineflow.model.InventoryItem;
import com.cineflow.model.ItemAssignment;
import com.cineflow.model.ItemCategory;
import com.cineflow.model.Movie;
import com.cineflow.model.MovieStatus;
import com.cineflow.model.Scene;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service managing props, costumes, and equipment inventory.
 * Follows the decoupled service pattern established by CastManager and ReviewManager.
 * Enforces quantity checks, movie/scene reference validation, checkouts, reservations, and returns.
 */
public class InventoryManager {
    private final Map<String, InventoryItem> items;
    private final List<ItemAssignment> assignments;
    private final EquipmentReservationManager reservationManager;

    public InventoryManager() {
        this.items = new LinkedHashMap<>();
        this.assignments = new ArrayList<>();
        this.reservationManager = new EquipmentReservationManager(this);
    }

    public void addItem(InventoryItem item) throws ValidationException {
        if (item == null) {
            throw new ValidationException("Inventory Item", "Cannot add null inventory item.");
        }
        String key = item.getId().trim().toUpperCase();
        if (items.containsKey(key)) {
            throw new ValidationException("Item ID", "Item with ID '" + item.getId() + "' already exists in inventory.");
        }
        items.put(key, item);
    }

    public InventoryItem findItemById(String itemId) {
        if (itemId == null) return null;
        return items.get(itemId.trim().toUpperCase());
    }

    public InventoryItem getItemOrThrow(String itemId) throws ResourceNotFoundException {
        InventoryItem item = findItemById(itemId);
        if (item == null) {
            throw new ResourceNotFoundException("Inventory Item", (itemId != null) ? itemId.trim() : "null");
        }
        return item;
    }

    public List<InventoryItem> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public List<InventoryItem> searchItems(String query, ItemCategory category, String condition) {
        List<InventoryItem> results = new ArrayList<>();
        String q = (query != null) ? query.trim().toLowerCase() : "";
        String cond = (condition != null) ? condition.trim().toLowerCase() : "";

        for (InventoryItem item : items.values()) {
            boolean matchesQuery = q.isEmpty()
                    || item.getId().toLowerCase().contains(q)
                    || item.getName().toLowerCase().contains(q)
                    || item.getNotes().toLowerCase().contains(q);

            boolean matchesCategory = (category == null) || item.getCategory() == category;

            boolean matchesCondition = cond.isEmpty()
                    || item.getCondition().toLowerCase().contains(cond);

            if (matchesQuery && matchesCategory && matchesCondition) {
                results.add(item);
            }
        }
        return results;
    }

    /**
     * Checks out an inventory item to a specified movie and scene.
     * Validates movie and scene reference existences and stock quantity availability.
     */
    public ItemAssignment checkoutItem(String itemId, Movie movie, int sceneNumber,
                                      int quantity, String assignedDate, String notes)
            throws ResourceNotFoundException, ValidationException {
        if (movie == null) {
            throw new ValidationException("Movie", "Movie reference cannot be null.");
        }
        if (movie.getStatus() == MovieStatus.DROPPED) {
            throw new ValidationException("Movie Status",
                    "Cannot checkout inventory items for a DROPPED / CANCELLED movie ('" + movie.getTitle() + "').");
        }
        InventoryItem item = getItemOrThrow(itemId);

        // Validate scene reference if specified
        if (sceneNumber > 0) {
            Scene scene = movie.findSceneByNumber(sceneNumber);
            if (scene == null) {
                throw new ResourceNotFoundException("Scene #" + sceneNumber,
                        "Scene not found in movie '" + movie.getTitle() + "'");
            }
        }

        // Deduct from available quantity
        item.checkout(quantity);

        int nextNum = assignments.size() + 1;
        String assignmentId = "ASG-" + String.format("%03d", nextNum);
        ItemAssignment assignment = new ItemAssignment(assignmentId, item.getId(), movie.getId(),
                sceneNumber, quantity, "CHECKED_OUT", assignedDate, "TBD", notes);
        assignments.add(assignment);
        return assignment;
    }

    /**
     * Returns an assignment, categorizing returned good, damaged, and lost items.
     */
    public void returnItem(String assignmentId, int goodQty, int damagedQty, int lostQty,
                           String returnDate, String returnNotes)
            throws ResourceNotFoundException, ValidationException {
        ItemAssignment assignment = findAssignmentById(assignmentId);
        if (assignment == null) {
            throw new ResourceNotFoundException("Item Assignment", assignmentId);
        }
        if (!assignment.isCheckedOut()) {
            throw new ValidationException("Assignment Status",
                    "Assignment [" + assignmentId + "] is already marked as " + assignment.getStatus() + ".");
        }

        int totalReturning = goodQty + damagedQty + lostQty;
        if (totalReturning != assignment.getQuantity()) {
            throw new ValidationException("Return Quantity Mismatch",
                    "Total returned (" + totalReturning + " = " + goodQty + " good + " + damagedQty
                            + " damaged + " + lostQty + " lost) must match original assigned quantity ("
                            + assignment.getQuantity() + ").");
        }

        InventoryItem item = getItemOrThrow(assignment.getItemId());
        item.processReturn(goodQty, damagedQty, lostQty);

        String newStatus;
        if (lostQty == assignment.getQuantity()) {
            newStatus = "LOST";
        } else if (damagedQty == assignment.getQuantity()) {
            newStatus = "DAMAGED";
        } else {
            newStatus = "RETURNED";
        }

        assignment.setStatus(newStatus);
        assignment.setReturnDate((returnDate != null && !returnDate.trim().isEmpty()) ? returnDate.trim() : "2026-10-12");
        if (returnNotes != null && !returnNotes.trim().isEmpty()) {
            String existingNotes = assignment.getNotes();
            assignment.setNotes(existingNotes.isEmpty() ? returnNotes.trim() : existingNotes + " | Return: " + returnNotes.trim());
        }
    }

    public ItemAssignment findAssignmentById(String assignmentId) {
        if (assignmentId == null) return null;
        for (ItemAssignment a : assignments) {
            if (a.getAssignmentId().equalsIgnoreCase(assignmentId.trim())) {
                return a;
            }
        }
        return null;
    }

    public List<ItemAssignment> getAssignmentsForMovie(String movieId) {
        List<ItemAssignment> list = new ArrayList<>();
        if (movieId == null) return list;
        for (ItemAssignment a : assignments) {
            if (a.getMovieId().equalsIgnoreCase(movieId.trim())) {
                list.add(a);
            }
        }
        return list;
    }

    public List<ItemAssignment> getAssignmentsForItem(String itemId) {
        List<ItemAssignment> list = new ArrayList<>();
        if (itemId == null) return list;
        for (ItemAssignment a : assignments) {
            if (a.getItemId().equalsIgnoreCase(itemId.trim())) {
                list.add(a);
            }
        }
        return list;
    }

    public List<ItemAssignment> getActiveAssignments() {
        List<ItemAssignment> list = new ArrayList<>();
        for (ItemAssignment a : assignments) {
            if (a.isCheckedOut()) {
                list.add(a);
            }
        }
        return list;
    }

    public List<ItemAssignment> getAllAssignments() {
        return new ArrayList<>(assignments);
    }

    public void addLoadedAssignment(ItemAssignment assignment) {
        if (assignment != null) {
            assignments.add(assignment);
        }
    }

    public int getTotalInventoryUnitsCount() {
        int total = 0;
        for (InventoryItem i : items.values()) {
            total += i.getTotalQuantity();
        }
        return total;
    }

    public int getAvailableUnitsCount() {
        int avail = 0;
        for (InventoryItem i : items.values()) {
            avail += i.getAvailableQuantity();
        }
        return avail;
    }

    public int getAssignedUnitsCount() {
        int inUse = 0;
        for (InventoryItem i : items.values()) {
            inUse += i.getAssignedQuantity();
        }
        return inUse;
    }

    public int getDamagedUnitsCount() {
        int dmg = 0;
        for (InventoryItem i : items.values()) {
            dmg += i.getDamagedQuantity();
        }
        return dmg;
    }

    public int getLostUnitsCount() {
        int lost = 0;
        for (InventoryItem i : items.values()) {
            lost += i.getLostQuantity();
        }
        return lost;
    }

    // =========================================================================
    // EQUIPMENT RESERVATION DELEGATE METHODS
    // =========================================================================

    public EquipmentReservationManager getReservationManager() {
        return reservationManager;
    }

    public EquipmentReservation reserveItem(String reservationId, String itemId, Movie movie, int sceneNumber,
                                           int quantity, String startDateTime, String endDateTime, String notes)
            throws ResourceNotFoundException, ValidationException, ScheduleConflictException {
        return reservationManager.reserveItem(reservationId, itemId, movie, sceneNumber, quantity, startDateTime, endDateTime, notes);
    }

    public int getAvailableQuantityForPeriod(String itemId, String startDateTime, String endDateTime)
            throws ResourceNotFoundException, ValidationException {
        return reservationManager.getAvailableQuantityForPeriod(itemId, startDateTime, endDateTime);
    }

    public void confirmReservation(String reservationId)
            throws ResourceNotFoundException, ScheduleConflictException, ValidationException {
        reservationManager.confirmReservation(reservationId);
    }

    public void cancelReservation(String reservationId) throws ResourceNotFoundException, ValidationException {
        reservationManager.cancelReservation(reservationId);
    }

    public ItemAssignment checkoutReservation(String reservationId, Movie movie)
            throws ResourceNotFoundException, ValidationException {
        return reservationManager.checkoutReservation(reservationId, movie);
    }

    public void returnReservation(String reservationId, int goodQty, int damagedQty, int lostQty,
                                  String returnDate, String returnNotes)
            throws ResourceNotFoundException, ValidationException {
        reservationManager.returnReservation(reservationId, goodQty, damagedQty, lostQty, returnDate, returnNotes);
    }

    public EquipmentReservation findReservationById(String reservationId) {
        return reservationManager.findReservationById(reservationId);
    }

    public List<EquipmentReservation> getReservationsForMovie(String movieId) {
        return reservationManager.getReservationsForMovie(movieId);
    }

    public List<EquipmentReservation> getReservationsForScene(String movieId, int sceneNumber) {
        return reservationManager.getReservationsForScene(movieId, sceneNumber);
    }

    public List<EquipmentReservation> getReservationsForItem(String itemId) {
        return reservationManager.getReservationsForItem(itemId);
    }

    public List<EquipmentReservation> getUpcomingReservations() {
        return reservationManager.getUpcomingReservations();
    }

    public List<EquipmentReservation> getOverdueReservations() {
        return reservationManager.getOverdueReservations();
    }

    public List<ItemAssignment> getOverdueAssignments() {
        return reservationManager.getOverdueAssignments();
    }

    public List<EquipmentReservation> getAllReservations() {
        return reservationManager.getAllReservations();
    }

    public void addLoadedReservation(EquipmentReservation res) {
        reservationManager.addLoadedReservation(res);
    }

    public void clear() {
        items.clear();
        assignments.clear();
        reservationManager.clear();
    }
}
