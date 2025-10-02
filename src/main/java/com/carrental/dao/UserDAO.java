package com.carrental.dao;

import com.carrental.model.User;
import com.carrental.model.enums.UserRole;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entity
 */
public class UserDAO {
    
    private final Connection connection;
    
    public UserDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Creates a new user in the database
     */
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (first_name, last_name, email, password_hash, phone, birth_date, role, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?::VARCHAR, ?, ?, ?) RETURNING id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getPhone());
            stmt.setDate(6, Date.valueOf(user.getBirthDate()));
            stmt.setString(7, user.getRole().name());
            stmt.setBoolean(8, user.isActive());
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getLong("id"));
            }
            return user;
        }
    }
    
    /**
     * Finds a user by email
     */
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND is_active = true";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
        }
    }
    
    /**
     * Finds a user by ID
     */
    public Optional<User> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? AND is_active = true";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
        }
    }
    
    /**
     * Gets all users
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users WHERE is_active = true ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    /**
     * Updates a user
     */
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ?, " +
                     "birth_date = ?, role = ?::VARCHAR, is_active = ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setDate(5, Date.valueOf(user.getBirthDate()));
            stmt.setString(6, user.getRole().name());
            stmt.setBoolean(7, user.isActive());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(9, user.getId());
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Checks if email already exists
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
    
    /**
     * Maps ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        
        Date birthDate = rs.getDate("birth_date");
        if (birthDate != null) {
            user.setBirthDate(birthDate.toLocalDate());
        }
        
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
} 