package com.cineflow.model;

import com.cineflow.exception.ValidationException;

/**
 * Tracks the assignment/checkout of an inventory item to a specific Movie and Scene.
 * Enforces reference tracking, quantity validation, and return status lifecycle.
 */
public class ItemAssignment {
    private String assignmentId;
    private String itemId;
    private String movieId;
    private int sceneNumber; // 0 indicates movie-level general allocation
    private int quantity;
    private String status; // "CHECKED_OUT", "RETURNED", "DAMAGED", "LOST"
    private String assignedDate;
    private String returnDate;
    private String notes;

    public ItemAssignment(String assignmentId, String itemId, String movieId, int sceneNumber,
                          int quantity, String assignedDate, String notes) throws ValidationException {
        this(assignmentId, itemId, movieId, sceneNumber, quantity, "CHECKED_OUT", assignedDate, "TBD", notes);
    }

    public ItemAssignment(String assignmentId, String itemId, String movieId, int sceneNumber,
                          int quantity, String status, String assignedDate, String returnDate,
                          String notes) throws ValidationException {
        if (assignmentId == null || assignmentId.trim().isEmpty()) {
            throw new ValidationException("Assignment ID", "Assignment ID cannot be empty.");
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
            throw new ValidationException("Quantity", "Assigned quantity must be greater than zero.");
        }

        this.assignmentId = assignmentId.trim();
        this.itemId = itemId.trim();
        this.movieId = movieId.trim();
        this.sceneNumber = sceneNumber;
        this.quantity = quantity;
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "CHECKED_OUT";
        this.assignedDate = (assignedDate != null && !assignedDate.trim().isEmpty()) ? assignedDate.trim() : "TBD";
        this.returnDate = (returnDate != null && !returnDate.trim().isEmpty()) ? returnDate.trim() : "TBD";
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) throws ValidationException {
        if (assignmentId == null || assignmentId.trim().isEmpty()) {
            throw new ValidationException("Assignment ID", "Assignment ID cannot be empty.");
        }
        this.assignmentId = assignmentId.trim();
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) throws ValidationException {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new ValidationException("Item ID", "Item ID reference cannot be empty.");
        }
        this.itemId = itemId.trim();
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) throws ValidationException {
        if (movieId == null || movieId.trim().isEmpty()) {
            throw new ValidationException("Movie ID", "Movie ID reference cannot be empty.");
        }
        this.movieId = movieId.trim();
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
            throw new ValidationException("Quantity", "Quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : "CHECKED_OUT";
    }

    public String getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(String assignedDate) {
        this.assignedDate = (assignedDate != null && !assignedDate.trim().isEmpty()) ? assignedDate.trim() : "TBD";
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = (returnDate != null && !returnDate.trim().isEmpty()) ? returnDate.trim() : "TBD";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes.trim() : "";
    }

    public boolean isCheckedOut() {
        return "CHECKED_OUT".equalsIgnoreCase(status);
    }

    public boolean isReturned() {
        return "RETURNED".equalsIgnoreCase(status);
    }

    public String getSceneReference() {
        return (sceneNumber > 0) ? "Scene #" + sceneNumber : "Movie-Wide";
    }

    @Override
    public String toString() {
        return String.format("[%s] Item: %-8s -> %-8s (%-10s) | Qty: %-2d | Status: %-11s | Out: %-10s | Ret: %-10s",
                assignmentId, itemId, movieId, getSceneReference(), quantity, status, assignedDate, returnDate);
    }
}
