package com.cineflow;

import com.cineflow.service.MovieManager;
import com.cineflow.ui.ConsoleMenu;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Main application entry point for CineFlow - Cinema Production Management System.
 * Realistic 2nd-year college mini-project.
 */
public class Main {
    public static void main(String[] args) {
        // Set standard UTF-8 console output for clear display
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}

        // Initialize management service and launch console interface
        MovieManager movieManager = new MovieManager();
        ConsoleMenu consoleMenu = new ConsoleMenu(movieManager);
        consoleMenu.start();
    }
}
