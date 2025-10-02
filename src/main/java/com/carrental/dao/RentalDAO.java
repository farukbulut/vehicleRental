package com.carrental.dao;

import com.carrental.model.Rental;
import com.carrental.model.enums.PricingType;
import com.carrental.model.enums.RentalStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Rental entity
 */
public class RentalDAO {
    
    private final Connection connection;
    
    public RentalDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Creates a new rental in the database
     */
    public Rental create(Rental rental) throws SQLException {
        String sql = "INSERT INTO rentals (user_id, vehicle_id, start_date, end_date, pricing_type, " +
                     "rental_duration, unit_price, total_amount, deposit_amount, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?::VARCHAR, ?, ?, ?, ?, ?::VARCHAR, ?, ?) RETURNING id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, rental.getUserId());
            stmt.setLong(2, rental.getVehicleId());
            stmt.setTimestamp(3, Timestamp.valueOf(rental.getStartDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(rental.getEndDate()));
            stmt.setString(5, rental.getPricingType().name());
            stmt.setInt(6, rental.getRentalDuration());
            stmt.setBigDecimal(7, rental.getUnitPrice());
            stmt.setBigDecimal(8, rental.getTotalAmount());
            stmt.setBigDecimal(9, rental.getDepositAmount());
            stmt.setString(10, rental.getStatus().name());
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rental.setId(rs.getLong("id"));
            }
            return rental;
        }
    }
    
    /**
     * Finds a rental by ID
     */
    public Optional<Rental> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM rentals WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToRental(rs));
            }
            return Optional.empty();
        }
    }
    
    /**
     * Gets all rentals for a specific user
     */
    public List<Rental> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM rentals WHERE user_id = ? ORDER BY created_at DESC";
        List<Rental> rentals = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        }
        return rentals;
    }
    
    /**
     * Gets all rentals for a specific vehicle
     */
    public List<Rental> findByVehicleId(Long vehicleId) throws SQLException {
        String sql = "SELECT * FROM rentals WHERE vehicle_id = ? ORDER BY start_date DESC";
        List<Rental> rentals = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        }
        return rentals;
    }
    
    /**
     * Gets active rentals for a specific user
     */
    public List<Rental> findActiveRentalsByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM rentals WHERE user_id = ? AND status = 'ACTIVE' ORDER BY start_date DESC";
        List<Rental> rentals = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        }
        return rentals;
    }
    
    /**
     * Checks if a vehicle has any conflicting rentals in the given date range
     */
    public boolean hasConflictingRentals(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate, Long excludeRentalId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rentals WHERE vehicle_id = ? AND status = 'ACTIVE' " +
                     "AND id != ? AND NOT (end_date <= ? OR start_date >= ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, vehicleId);
            stmt.setLong(2, excludeRentalId != null ? excludeRentalId : -1);
            stmt.setTimestamp(3, Timestamp.valueOf(startDate));
            stmt.setTimestamp(4, Timestamp.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
    
    /**
     * Updates a rental
     */
    public void update(Rental rental) throws SQLException {
        String sql = "UPDATE rentals SET user_id = ?, vehicle_id = ?, start_date = ?, end_date = ?, " +
                     "pricing_type = ?::VARCHAR, rental_duration = ?, unit_price = ?, total_amount = ?, " +
                     "deposit_amount = ?, status = ?::VARCHAR, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, rental.getUserId());
            stmt.setLong(2, rental.getVehicleId());
            stmt.setTimestamp(3, Timestamp.valueOf(rental.getStartDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(rental.getEndDate()));
            stmt.setString(5, rental.getPricingType().name());
            stmt.setInt(6, rental.getRentalDuration());
            stmt.setBigDecimal(7, rental.getUnitPrice());
            stmt.setBigDecimal(8, rental.getTotalAmount());
            stmt.setBigDecimal(9, rental.getDepositAmount());
            stmt.setString(10, rental.getStatus().name());
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(12, rental.getId());
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Updates rental status
     */
    public void updateStatus(Long rentalId, RentalStatus status) throws SQLException {
        String sql = "UPDATE rentals SET status = ?::VARCHAR, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, rentalId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Gets all rentals (for admin)
     */
    public List<Rental> findAll() throws SQLException {
        String sql = "SELECT * FROM rentals ORDER BY created_at DESC";
        List<Rental> rentals = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        }
        return rentals;
    }
    
    /**
     * Maps ResultSet to Rental object
     */
    private Rental mapResultSetToRental(ResultSet rs) throws SQLException {
        Rental rental = new Rental();
        rental.setId(rs.getLong("id"));
        rental.setUserId(rs.getInt("user_id"));
        rental.setVehicleId(rs.getInt("vehicle_id"));
        
        Timestamp startDate = rs.getTimestamp("start_date");
        if (startDate != null) {
            rental.setStartDate(startDate.toLocalDateTime());
        }
        
        Timestamp endDate = rs.getTimestamp("end_date");
        if (endDate != null) {
            rental.setEndDate(endDate.toLocalDateTime());
        }
        
        rental.setPricingType(PricingType.valueOf(rs.getString("pricing_type")));
        rental.setRentalDuration(rs.getInt("rental_duration"));
        rental.setUnitPrice(rs.getBigDecimal("unit_price"));
        rental.setTotalAmount(rs.getBigDecimal("total_amount"));
        rental.setDepositAmount(rs.getBigDecimal("deposit_amount"));
        rental.setStatus(RentalStatus.valueOf(rs.getString("status")));
        
        return rental;
    }
} 