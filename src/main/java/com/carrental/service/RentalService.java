package com.carrental.service;

import com.carrental.dao.*;
import com.carrental.exception.BusinessRuleException;
import com.carrental.exception.VehicleNotFoundException;
import com.carrental.model.*;
import com.carrental.model.enums.PaymentStatus;
import com.carrental.model.enums.PricingType;
import com.carrental.model.enums.RentalStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for rental operations with transaction management
 */
public class RentalService {
    
    private final RentalDAO rentalDAO;
    private final VehicleDAO vehicleDAO;
    private final DepositDAO depositDAO;
    private final Connection connection;
    
    public RentalService() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.rentalDAO = new RentalDAO();
        this.vehicleDAO = new VehicleDAO();
        this.depositDAO = new DepositDAO();
    }
    
    /**
     * Creates a new rental with transaction management
     */
    public Rental createRental(User user, Long vehicleId, LocalDateTime startDate, 
                              LocalDateTime endDate, PricingType pricingType, int duration) 
            throws SQLException, BusinessRuleException, VehicleNotFoundException {
        
        try {
            connection.setAutoCommit(false);
            
            Optional<Vehicle> vehicleOpt = vehicleDAO.findById(vehicleId);
            if (vehicleOpt.isEmpty()) {
                throw new VehicleNotFoundException(vehicleId);
            }
            
            Vehicle vehicle = vehicleOpt.get();
            
            BusinessRules.validateRentalPeriod(startDate, endDate);
            BusinessRules.validateCorporateRental(user, pricingType, duration);
            BusinessRules.validateHighValueVehicleRental(user, vehicle);
            
            if (!vehicle.isAvailable()) {
                throw new BusinessRuleException("Vehicle is not available");
            }
            
            if (rentalDAO.hasConflictingRentals(vehicleId, startDate, endDate, null)) {
                throw new BusinessRuleException("Vehicle is already rented for this period");
            }
            
            BigDecimal unitPrice = vehicle.calculateRentalCost(pricingType, 1);
            BigDecimal totalAmount = vehicle.calculateRentalCost(pricingType, duration);
            BigDecimal depositAmount = vehicle.getDepositAmount();
            
            Rental rental = new Rental(
                user.getId().intValue(),
                vehicleId.intValue(),
                startDate,
                endDate,
                pricingType,
                duration,
                unitPrice,
                totalAmount,
                depositAmount
            );
            
            rental = rentalDAO.create(rental);
            
            if (depositAmount.compareTo(BigDecimal.ZERO) > 0) {
                Deposit deposit = new Deposit(rental.getId().intValue(), depositAmount);
                deposit.setDepositStatus(PaymentStatus.PAID);
                depositDAO.create(deposit);
            }
            
            vehicleDAO.updateAvailability(vehicleId, false);
            
            connection.commit();
            
            return rental;
            
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            
            if (e instanceof SQLException) {
                throw (SQLException) e;
            } else if (e instanceof BusinessRuleException) {
                throw (BusinessRuleException) e;
            } else if (e instanceof VehicleNotFoundException) {
                throw (VehicleNotFoundException) e;
            } else {
                throw new SQLException("Error creating rental", e);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error re-enabling auto-commit: " + e.getMessage());
            }
        }
    }
    
    /**
     * Cancels a rental with transaction management
     */
    public void cancelRental(Long rentalId) throws SQLException, BusinessRuleException {
        try {
            connection.setAutoCommit(false);
            
            Optional<Rental> rentalOpt = rentalDAO.findById(rentalId);
            if (rentalOpt.isEmpty()) {
                throw new BusinessRuleException("Rental not found");
            }
            
            Rental rental = rentalOpt.get();
            
            if (rental.getStatus() != RentalStatus.ACTIVE) {
                throw new BusinessRuleException("Only active rentals can be cancelled");
            }
            
            rentalDAO.updateStatus(rentalId, RentalStatus.CANCELLED);
            
            Optional<Deposit> depositOpt = depositDAO.findByRentalId(rentalId);
            if (depositOpt.isPresent()) {
                Deposit deposit = depositOpt.get();
                deposit.processRefund(deposit.getDepositAmount(), "Refunded due to rental cancellation");
                depositDAO.update(deposit);
            }
            
            vehicleDAO.updateAvailability((long) rental.getVehicleId(), true);
            
            connection.commit();
            
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            
            if (e instanceof SQLException) {
                throw (SQLException) e;
            } else if (e instanceof BusinessRuleException) {
                throw (BusinessRuleException) e;
            } else {
                throw new SQLException("Error cancelling rental", e);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error re-enabling auto-commit: " + e.getMessage());
            }
        }
    }
    
    public List<Rental> getUserRentals(Long userId) throws SQLException {
        return rentalDAO.findByUserId(userId);
    }
    
    public List<Rental> getAllRentals() throws SQLException {
        return rentalDAO.findAll();
    }
    
    public List<Rental> getActiveUserRentals(Long userId) throws SQLException {
        return rentalDAO.findActiveRentalsByUserId(userId);
    }
} 