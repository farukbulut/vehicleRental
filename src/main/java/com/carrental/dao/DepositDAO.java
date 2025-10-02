package com.carrental.dao;

import com.carrental.model.Deposit;
import com.carrental.model.enums.PaymentStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Deposit entity
 */
public class DepositDAO {
    
    private final Connection connection;
    
    public DepositDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Creates a new deposit in the database
     */
    public Deposit create(Deposit deposit) throws SQLException {
        String sql = "INSERT INTO deposits (rental_id, deposit_amount, deposit_status, deposit_date, " +
                     "refund_date, refund_amount, notes, created_at, updated_at) " +
                     "VALUES (?, ?, ?::VARCHAR, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, deposit.getRentalId());
            stmt.setBigDecimal(2, deposit.getDepositAmount());
            stmt.setString(3, deposit.getDepositStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(deposit.getDepositDate()));
            
            if (deposit.getRefundDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(deposit.getRefundDate()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            stmt.setBigDecimal(6, deposit.getRefundAmount());
            stmt.setString(7, deposit.getNotes());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                deposit.setId(rs.getLong("id"));
            }
            return deposit;
        }
    }
    
    /**
     * Finds a deposit by ID
     */
    public Optional<Deposit> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM deposits WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToDeposit(rs));
            }
            return Optional.empty();
        }
    }
    
    /**
     * Finds a deposit by rental ID
     */
    public Optional<Deposit> findByRentalId(Long rentalId) throws SQLException {
        String sql = "SELECT * FROM deposits WHERE rental_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, rentalId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToDeposit(rs));
            }
            return Optional.empty();
        }
    }
    
    /**
     * Gets all deposits
     */
    public List<Deposit> findAll() throws SQLException {
        String sql = "SELECT * FROM deposits ORDER BY created_at DESC";
        List<Deposit> deposits = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                deposits.add(mapResultSetToDeposit(rs));
            }
        }
        return deposits;
    }
    
    /**
     * Updates a deposit
     */
    public void update(Deposit deposit) throws SQLException {
        String sql = "UPDATE deposits SET rental_id = ?, deposit_amount = ?, deposit_status = ?::VARCHAR, " +
                     "deposit_date = ?, refund_date = ?, refund_amount = ?, notes = ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, deposit.getRentalId());
            stmt.setBigDecimal(2, deposit.getDepositAmount());
            stmt.setString(3, deposit.getDepositStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(deposit.getDepositDate()));
            
            if (deposit.getRefundDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(deposit.getRefundDate()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            stmt.setBigDecimal(6, deposit.getRefundAmount());
            stmt.setString(7, deposit.getNotes());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(9, deposit.getId());
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Updates deposit status
     */
    public void updateStatus(Long depositId, PaymentStatus status) throws SQLException {
        String sql = "UPDATE deposits SET deposit_status = ?::VARCHAR, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, depositId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Maps ResultSet to Deposit object
     */
    private Deposit mapResultSetToDeposit(ResultSet rs) throws SQLException {
        Deposit deposit = new Deposit();
        deposit.setId(rs.getLong("id"));
        deposit.setRentalId(rs.getInt("rental_id"));
        deposit.setDepositAmount(rs.getBigDecimal("deposit_amount"));
        deposit.setDepositStatus(PaymentStatus.valueOf(rs.getString("deposit_status")));
        
        Timestamp depositDate = rs.getTimestamp("deposit_date");
        if (depositDate != null) {
            deposit.setDepositDate(depositDate.toLocalDateTime());
        }
        
        Timestamp refundDate = rs.getTimestamp("refund_date");
        if (refundDate != null) {
            deposit.setRefundDate(refundDate.toLocalDateTime());
        }
        
        deposit.setRefundAmount(rs.getBigDecimal("refund_amount"));
        deposit.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            deposit.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            deposit.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return deposit;
    }
} 