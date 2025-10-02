package com.carrental.ui;

import java.util.Scanner;

/**
 * Utility class for console input/output operations
 */
public class ConsoleUI {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    /**
     * Prints a header with decoration
     */
    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }
    
    /**
     * Prints a separator line
     */
    public static void printSeparator() {
        System.out.println("-".repeat(60));
    }
    
    /**
     * Prints a success message
     */
    public static void printSuccess(String message) {
        System.out.println("\n✓ " + message);
    }
    
    /**
     * Prints an error message
     */
    public static void printError(String message) {
        System.err.println("\n✗ HATA: " + message);
    }
    
    /**
     * Prints an info message
     */
    public static void printInfo(String message) {
        System.out.println("\nℹ " + message);
    }
    
    /**
     * Reads a string input from user
     */
    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }
    
    /**
     * Reads an integer input from user
     */
    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Lütfen geçerli bir sayı giriniz");
            }
        }
    }
    
    /**
     * Reads a long input from user
     */
    public static long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                printError("Lütfen geçerli bir sayı giriniz");
            }
        }
    }
    
    /**
     * Reads a double input from user
     */
    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                printError("Lütfen geçerli bir sayı giriniz");
            }
        }
    }
    
    /**
     * Waits for user to press enter
     */
    public static void waitForEnter() {
        System.out.print("\nDevam etmek için Enter'a basınız...");
        scanner.nextLine();
    }
    
    /**
     * Clears the console (platform independent)
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * Closes the scanner
     */
    public static void close() {
        scanner.close();
    }
} 