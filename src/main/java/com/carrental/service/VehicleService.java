package com.carrental.service;

import com.carrental.dao.VehicleDAO;
import com.carrental.exception.BusinessRuleException;
import com.carrental.exception.VehicleNotFoundException;
import com.carrental.model.*;
import com.carrental.model.enums.VehicleType;
import com.carrental.util.ValidationUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service class for vehicle management
 */
public class VehicleService {
    
    private final VehicleDAO vehicleDAO;
    
    public VehicleService() throws SQLException {
        this.vehicleDAO = new VehicleDAO();
    }
    
    /**
     * Creates a new vehicle
     */
    public Vehicle createVehicle(VehicleType type, String brand, String model, int year, 
                                String color, String plateNumber, BigDecimal valueTl, 
                                int capacity, String enginePower, String fuelType, 
                                String transmission) throws SQLException, BusinessRuleException {
        
        // Validate inputs
        if (!ValidationUtils.isNotEmpty(brand)) {
            throw new BusinessRuleException("Brand is required");
        }
        
        if (!ValidationUtils.isNotEmpty(model)) {
            throw new BusinessRuleException("Model is required");
        }
        
        if (year < 1900 || year > 2100) {
            throw new BusinessRuleException("Invalid year");
        }
        
        if (!ValidationUtils.isNotEmpty(plateNumber)) {
            throw new BusinessRuleException("Plate number is required");
        }
        
        if (valueTl == null || valueTl.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Value must be greater than zero");
        }
        
        // Check if plate number already exists
        if (vehicleDAO.plateNumberExists(plateNumber, null)) {
            throw new BusinessRuleException("Plate number already exists");
        }
        
        // Create vehicle based on type
        Vehicle vehicle;
        switch (type) {
            case AUTOMOBILE:
                vehicle = new Automobile(brand, model, year, color, plateNumber, valueTl, 
                                       capacity, enginePower, fuelType, transmission);
                break;
            case HELICOPTER:
                vehicle = new Helicopter(brand, model, year, color, plateNumber, valueTl, 
                                       capacity, enginePower, fuelType, transmission);
                break;
            case MOTORCYCLE:
                vehicle = new Motorcycle(brand, model, year, color, plateNumber, valueTl, 
                                        capacity, enginePower, fuelType, transmission);
                break;
            default:
                throw new BusinessRuleException("Invalid vehicle type");
        }
        
        return vehicleDAO.create(vehicle);
    }
    
    /**
     * Gets a vehicle by ID
     */
    public Vehicle getVehicleById(Long id) throws SQLException, VehicleNotFoundException {
        Optional<Vehicle> vehicleOpt = vehicleDAO.findById(id);
        
        if (vehicleOpt.isEmpty()) {
            throw new VehicleNotFoundException(id);
        }
        
        return vehicleOpt.get();
    }
    
    /**
     * Gets all vehicles
     */
    public List<Vehicle> getAllVehicles() throws SQLException {
        return vehicleDAO.findAll();
    }
    
    /**
     * Searches vehicles with filters and pagination
     */
    public List<Vehicle> searchVehicles(VehicleType type, String brand, BigDecimal minPrice, 
                                       BigDecimal maxPrice, boolean onlyAvailable, 
                                       int page, int pageSize) throws SQLException {
        return vehicleDAO.searchVehicles(type, brand, minPrice, maxPrice, onlyAvailable, page, pageSize);
    }
    
    /**
     * Updates a vehicle
     */
    public void updateVehicle(Vehicle vehicle) throws SQLException, BusinessRuleException {
        // Validate inputs
        if (!ValidationUtils.isNotEmpty(vehicle.getBrand())) {
            throw new BusinessRuleException("Brand is required");
        }
        
        if (!ValidationUtils.isNotEmpty(vehicle.getModel())) {
            throw new BusinessRuleException("Model is required");
        }
        
        if (!ValidationUtils.isNotEmpty(vehicle.getPlateNumber())) {
            throw new BusinessRuleException("Plate number is required");
        }
        
        // Check if plate number already exists for another vehicle
        if (vehicleDAO.plateNumberExists(vehicle.getPlateNumber(), vehicle.getId())) {
            throw new BusinessRuleException("Plate number already exists for another vehicle");
        }
        
        vehicleDAO.update(vehicle);
    }
    
    /**
     * Deletes a vehicle
     */
    public void deleteVehicle(Long id) throws SQLException, VehicleNotFoundException {
        // Check if vehicle exists
        Optional<Vehicle> vehicleOpt = vehicleDAO.findById(id);
        if (vehicleOpt.isEmpty()) {
            throw new VehicleNotFoundException(id);
        }
        
        vehicleDAO.delete(id);
    }
    
    /**
     * Updates vehicle availability
     */
    public void updateVehicleAvailability(Long vehicleId, boolean isAvailable) throws SQLException {
        vehicleDAO.updateAvailability(vehicleId, isAvailable);
    }
} 