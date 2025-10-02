package com.carrental.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates if string is not null or empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates password strength (minimum 6 characters)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Validates phone number format (Turkish format)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Allow formats like: +90-555-1234567, 05551234567, etc.
        String cleaned = phone.replaceAll("[\\s\\-()]", "");
        return cleaned.matches("^(\\+90|0)?[0-9]{10}$");
    }
} 