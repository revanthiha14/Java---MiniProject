package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ScheduleConflictException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.CallSheet;
import com.cineflow.model.DaylightRequirement;
import com.cineflow.model.Scene;
import com.cineflow.model.SceneStatus;
import com.cineflow.repository.Repository;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Core production management service handling script scenes, shooting schedules,
 * daylight priorities, and official Call Sheets.
 * Demonstrates:
 *  - Queue Collection: PriorityQueue<Scene> for priority-driven shoot ordering
 *  - List Collection: ArrayList<Scene>
 *  - Set Collection: HashSet and TreeSet
 *  - Method Overloading
 *  - Custom Exceptions (ScheduleConflictException, ResourceNotFoundException, ValidationException)
 *  - Custom Functional Interfaces (CostEstimator, ConflictValidator)
 *  - Lambdas and Stream API
 *  - IO Streams: Writing and reading text files with PrintWriter, BufferedWriter, BufferedReader
 */
public class ProductionService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Repository<Scene, String> sceneRepository;
    private final Repository<CallSheet, String> callSheetRepository;
    private final Queue<Scene> shootingPriorityQueue; // Demonstrates Queue (PriorityQueue)

    private String activeMovieId = "MOV-01";

    public ProductionService(Repository<Scene, String> sceneRepository,
                             Repository<CallSheet, String> callSheetRepository) {
        this.sceneRepository = sceneRepository;
        this.callSheetRepository = callSheetRepository;
        this.shootingPriorityQueue = new PriorityQueue<>();
        this.activeMovieId = "MOV-01";
        refreshPriorityQueue();
    }

    public void setActiveMovieId(String activeMovieId) {
        this.activeMovieId = activeMovieId;
        refreshPriorityQueue();
    }

    public String getActiveMovieId() {
        return activeMovieId;
    }

    /**
     * Refreshes the priority queue of unscheduled or pending scenes for active movie.
     */
    public synchronized void refreshPriorityQueue() {
        shootingPriorityQueue.clear();
        for (Scene scene : sceneRepository.findAll()) {
            if ((activeMovieId == null || activeMovieId.equals(scene.getMovieId())) &&
                (scene.getStatus() == SceneStatus.DRAFT || scene.getStatus() == SceneStatus.SCHEDULED)) {
                shootingPriorityQueue.offer(scene);
            }
        }
    }

    public void addScene(Scene scene) throws ValidationException {
        if (scene == null) {
            throw new ValidationException("scene", "Scene object cannot be null");
        }
        if (scene.getId() == null || scene.getId().trim().isEmpty()) {
            throw new ValidationException("id", "Scene ID cannot be blank");
        }
        if (scene.getSceneNumber() <= 0) {
            throw new ValidationException("sceneNumber", "Scene number must be greater than zero");
        }
        if (scene.getScriptPages() <= 0) {
            throw new ValidationException("scriptPages", "Script pages must be greater than zero");
        }
        sceneRepository.save(scene);
        refreshPriorityQueue();
    }

    // Method Overloading Demonstration: scheduleScene variants
    public void scheduleScene(String sceneId, LocalDate date, String timeSlot)
            throws ResourceNotFoundException, ScheduleConflictException, ValidationException {
        scheduleScene(sceneId, date, timeSlot, null);
    }

    public void scheduleScene(String sceneId, LocalDate date, String timeSlot, String locationId)
            throws ResourceNotFoundException, ScheduleConflictException, ValidationException {
        if (date == null) {
            throw new ValidationException("date", "Shoot date cannot be null");
        }
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            throw new ValidationException("timeSlot", "Time slot cannot be empty (e.g., '07:00 - 13:00')");
        }

        Scene scene = getSceneById(sceneId);

        // Conflict Detection Algorithm: Check all other scheduled scenes
        for (Scene existing : sceneRepository.findAll()) {
            if (!existing.getId().equals(sceneId) && existing.hasScheduleConflict(date, timeSlot)) {
                // If they share the same location or actors, report conflict
                if (locationId != null && locationId.equals(existing.getLocationId())) {
                    throw new ScheduleConflictException("Location " + locationId, date, timeSlot,
                            "Already reserved for Scene #" + existing.getSceneNumber());
                }
                // Check actor overlaps using Set intersection
                Set<String> commonActors = new HashSet<>(scene.getRequiredActorIds());
                commonActors.retainAll(existing.getRequiredActorIds());
                if (!commonActors.isEmpty()) {
                    throw new ScheduleConflictException("Actor(s) " + commonActors, date, timeSlot,
                            "Booked for overlapping Scene #" + existing.getSceneNumber());
                }
            }
        }

        scene.setScheduledDate(date);
        scene.setShootTimeSlot(timeSlot);
        if (locationId != null) {
            scene.setLocationId(locationId);
        }
        scene.setStatus(SceneStatus.SCHEDULED);
        sceneRepository.save(scene);
        refreshPriorityQueue();
    }

    // Method Overloading Demonstration: filterScenes variants
    public List<Scene> filterScenes(SceneStatus status) {
        return filterScenes(s -> s.getStatus() == status);
    }

    public List<Scene> filterScenes(DaylightRequirement daylight) {
        return filterScenes(s -> s.getDaylightRequirement() == daylight);
    }

    public List<Scene> filterScenes(Predicate<Scene> predicate) {
        return sceneRepository.findAll().stream()
                .filter(predicate)
                .sorted(Comparator.comparingInt(Scene::getSceneNumber))
                .collect(Collectors.toList());
    }

    public Scene getSceneById(String id) throws ResourceNotFoundException {
        return sceneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scene", id));
    }

    public List<Scene> getAllScenes() {
        return sceneRepository.findAll().stream()
                .filter(s -> activeMovieId == null || activeMovieId.equals(s.getMovieId()))
                .sorted(Comparator.comparingInt(Scene::getSceneNumber))
                .collect(Collectors.toList());
    }

    public List<Scene> getAllScenesGlobal() {
        return sceneRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Scene::getSceneNumber))
                .collect(Collectors.toList());
    }

    /**
     * Polls the next most urgent scene to be shot from the PriorityQueue.
     * Demonstrates Queue operation.
     */
    public Scene pollNextPriorityScene() {
        return shootingPriorityQueue.poll();
    }

    /**
     * Peeks at the next scene in the priority queue without removing it.
     */
    public Scene peekNextPriorityScene() {
        return shootingPriorityQueue.peek();
    }

    public int getPriorityQueueSize() {
        return shootingPriorityQueue.size();
    }

    public List<Scene> getPriorityQueueAsList() {
        // PriorityQueue iterator does not guarantee sorted order; sort a copy by compareTo
        List<Scene> list = new ArrayList<>(shootingPriorityQueue);
        Collections.sort(list);
        return list;
    }

    public void markSceneFilmed(String sceneId) throws ResourceNotFoundException {
        Scene scene = getSceneById(sceneId);
        scene.setStatus(SceneStatus.COMPLETED);
        sceneRepository.save(scene);
        refreshPriorityQueue();
    }

    /**
     * Estimates scene shooting cost using a custom Functional Interface.
     */
    public double estimateSceneCost(String sceneId, int shootDays, CostEstimator estimator)
            throws ResourceNotFoundException {
        Scene scene = getSceneById(sceneId);
        return estimator.estimate(scene, shootDays);
    }

    /**
     * Validates a scheduling proposal using a custom ConflictValidator Functional Interface.
     */
    public boolean validateCustomRule(String sceneId, LocalDate date, String slot, ConflictValidator validator)
            throws ResourceNotFoundException {
        Scene scene = getSceneById(sceneId);
        return validator.isValid(scene, date, slot);
    }

    // Call Sheet Operations
    public void registerCallSheet(CallSheet callSheet) {
        if (callSheet != null) {
            callSheetRepository.save(callSheet);
        }
    }

    public List<CallSheet> getAllCallSheets() {
        return callSheetRepository.findAll();
    }

    /**
     * Exports a Call Sheet directly to a disk file using IO Streams (PrintWriter & BufferedWriter).
     */
    public void exportCallSheetToFile(CallSheet callSheet, String filePath) throws IOException {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            pw.print(callSheet.generateDetailedReport());
            pw.flush();
        }
    }

    /**
     * Reads a text file using IO Streams (BufferedReader & FileReader) and returns file contents.
     */
    public String readExportedFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public Set<String> getUniqueLocationIdsUsed() {
        return sceneRepository.findAll().stream()
                .map(Scene::getLocationId)
                .filter(loc -> loc != null && !loc.trim().isEmpty())
                .collect(Collectors.toSet()); // Demonstrates Set
    }

    public Set<LocalDate> getScheduledDates() {
        return sceneRepository.findAll().stream()
                .map(Scene::getScheduledDate)
                .filter(d -> d != null)
                .collect(Collectors.toCollection(TreeSet::new)); // Demonstrates TreeSet
    }
}
