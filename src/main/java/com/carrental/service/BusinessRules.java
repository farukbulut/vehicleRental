package com.carrental.service;

import com.carrental.model.User;
import com.carrental.model.Vehicle;
import com.carrental.model.enums.PricingType;
import com.carrental.model.enums.UserRole;

import java.time.LocalDateTime;

public class BusinessRules {

    /**
     * Validates if corporate customer meets minimum rental period requirement
     */
    public static void validateCorporateRental(User user, PricingType pricingType, int duration) {
        if (user.getRole() == UserRole.CORPORATE) {
            if (pricingType != PricingType.MONTHLY) {
                throw new IllegalArgumentException("Corporate customers must rent for at least 1 month");
            }
            if (duration < 1) {
                throw new IllegalArgumentException("Corporate customers must rent for at least 1 month duration");
            }
        }
    }

    /**
     * Validates if user meets high-value vehicle rental requirements
     */
    public static void validateHighValueVehicleRental(User user, Vehicle vehicle) {
        if (vehicle.requiresDeposit()) {
            if (!user.isEligibleForHighValueRental()) {
                throw new IllegalArgumentException("High-value vehicle rentals require minimum age of 30");
            }
        }
    }

    /**
     * Validates rental period constraints
     */
    public static void validateRentalPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (startDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
    }
}