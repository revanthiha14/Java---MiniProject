package com.cineflow.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Terminal UI presentation and user prompt helper.
 * Enforces best practice: "Prompt messages during reading input and displaying output".
 */
public final class ConsoleUI {
    private static final Scanner scanner = new Scanner(System.in);

    // ANSI Color Codes
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";

    private ConsoleUI() {}

    public static void printBanner() {
        System.out.println("========================================================================");
        System.out.println("           HORIZON STUDIOS - MOVIE PRODUCTION MANAGEMENT SYSTEM         ");
        System.out.println("========================================================================");
    }

    public static void printStudioHeader(String studioName, String movieTitle, String phase) {
        System.out.println("========================================================================");
        System.out.println("           " + studioName.toUpperCase() + " - MOVIE PRODUCTION MANAGEMENT SYSTEM");
        System.out.println("========================================================================");
        if (movieTitle != null && !movieTitle.isEmpty()) {
            System.out.println("Active Movie: " + movieTitle + (phase != null && !phase.isEmpty() ? " (" + phase + ")" : ""));
            System.out.println("------------------------------------------------------------------------");
        }
    }

    public static void printSectionHeader(String title) {
        System.out.println("\n" + CYAN + BOLD + ">>> " + title.toUpperCase() + " <<<" + RESET);
        System.out.println(CYAN + "-".repeat(Math.max(40, title.length() + 8)) + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "[SUCCESS] " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "[WARNING] " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + "[ERROR] " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(BLUE + "[INFO] " + message + RESET);
    }

    /**
     * Reads a line of text with a prompt message.
     */
    public static String promptString(String prompt) {
        System.out.print(YELLOW + prompt + ": " + RESET);
        return scanner.nextLine().trim();
    }

    /**
     * Reads a required non-empty string, reprompting if blank.
     */
    public static String promptNonEmptyString(String prompt) {
        while (true) {
            System.out.print(YELLOW + prompt + " (required): " + RESET);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            printWarning("Input cannot be empty. Please re-enter.");
        }
    }

    /**
     * Reads an integer within [min, max] range with automatic validation and error messaging.
     */
    public static int promptInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(YELLOW + prompt + " [" + min + " - " + max + "]: " + RESET);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                printWarning("Number must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printWarning("Invalid input. Please enter an integer number.");
            }
        }
    }

    /**
     * Reads a double value within [min, max] range.
     */
    public static double promptDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(YELLOW + prompt + " [Min " + min + "]: $" + RESET);
            String input = scanner.nextLine().trim().replace("$", "").replace(",", "");
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                printWarning("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printWarning("Invalid decimal number. Please try again.");
            }
        }
    }

    /**
     * Reads a date in YYYY-MM-DD format.
     */
    public static LocalDate promptDate(String prompt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print(YELLOW + prompt + " (format: YYYY-MM-DD): " + RESET);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, dtf);
            } catch (DateTimeParseException e) {
                printWarning("Invalid date format! Example: 2026-11-15");
            }
        }
    }

    /**
     * Prompts for yes/no confirmation.
     */
    public static boolean promptConfirmation(String prompt) {
        System.out.print(YELLOW + prompt + " (y/n): " + RESET);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.startsWith("y");
    }

    public static void pauseForUser() {
        System.out.print("\n" + CYAN + "Press [ENTER] to continue..." + RESET);
        scanner.nextLine();
    }
}
