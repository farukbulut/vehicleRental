package com.carrental.exception;

/**
 * Exception thrown when a vehicle is not found
 */
public class VehicleNotFoundException extends Exception {
    
    public VehicleNotFoundException(String message) {
        super(message);
    }
    
    public VehicleNotFoundException(Long vehicleId) {
        super("Vehicle not found with ID: " + vehicleId);
    }
} 