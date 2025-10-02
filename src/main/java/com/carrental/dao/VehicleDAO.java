package com.carrental.dao;

import com.carrental.model.*;
import com.carrental.model.enums.VehicleType;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Vehicle entity
 */
public class VehicleDAO {
    
    private final Connection connection;
    
    public VehicleDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Creates a new vehicle in the database
     */
    public Vehicle create(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (vehicle_type, brand, model, year, color, plate_number, " +
                     "value_tl, capacity, engine_power, fuel_type, transmission, is_available, created_at, updated_at) " +
                     "VALUES (?::VARCHAR, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getVehicleType().name());
            stmt.setString(2, vehicle.getBrand());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setString(5, vehicle.getColor());
            stmt.setString(6, vehicle.getPlateNumber());
            stmt.setBigDecimal(7, vehicle.getValueTl());
            stmt.setInt(8, vehicle.getCapacity());
            stmt.setString(9, vehicle.getEnginePower());
            stmt.setString(10, vehicle.getFuelType());
            stmt.setString(11, vehicle.getTransmission());
            stmt.setBoolean(12, vehicle.isAvailable());
            stmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                vehicle.setId(rs.getLong("id"));
            }
            return vehicle;
        }
    }
    
    /**
     * Finds a vehicle by ID
     */
    public Optional<Vehicle> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToVehicle(rs));
            }
            return Optional.empty();
        }
    }
    
    /**
     * Gets all vehicles
     */
    public List<Vehicle> findAll() throws SQLException {
        String sql = "SELECT * FROM vehicles ORDER BY created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        return vehicles;
    }
    
    /**
     * Searches vehicles with filters and pagination
     */
    public List<Vehicle> searchVehicles(VehicleType type, String brand, BigDecimal minPrice, 
                                       BigDecimal maxPrice, boolean onlyAvailable, 
                                       int page, int pageSize) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM vehicles WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (type != null) {
            sql.append(" AND vehicle_type = ?::VARCHAR");
            params.add(type.name());
        }
        
        if (brand != null && !brand.trim().isEmpty()) {
            sql.append(" AND LOWER(brand) LIKE LOWER(?)");
            params.add("%" + brand + "%");
        }
        
        if (minPrice != null) {
            sql.append(" AND value_tl >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND value_tl <= ?");
            params.add(maxPrice);
        }
        
        if (onlyAvailable) {
            sql.append(" AND is_available = true");
        }
        
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof BigDecimal) {
                    stmt.setBigDecimal(i + 1, (BigDecimal) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Boolean) {
                    stmt.setBoolean(i + 1, (Boolean) param);
                }
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        
        return vehicles;
    }
    
    /**
     * Updates a vehicle
     */
    public void update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET vehicle_type = ?::VARCHAR, brand = ?, model = ?, year = ?, " +
                     "color = ?, plate_number = ?, value_tl = ?, capacity = ?, engine_power = ?, " +
                     "fuel_type = ?, transmission = ?, is_available = ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getVehicleType().name());
            stmt.setString(2, vehicle.getBrand());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setString(5, vehicle.getColor());
            stmt.setString(6, vehicle.getPlateNumber());
            stmt.setBigDecimal(7, vehicle.getValueTl());
            stmt.setInt(8, vehicle.getCapacity());
            stmt.setString(9, vehicle.getEnginePower());
            stmt.setString(10, vehicle.getFuelType());
            stmt.setString(11, vehicle.getTransmission());
            stmt.setBoolean(12, vehicle.isAvailable());
            stmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(14, vehicle.getId());
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Deletes a vehicle (soft delete - sets is_available to false)
     */
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Checks if plate number already exists
     */
    public boolean plateNumberExists(String plateNumber, Long excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE plate_number = ? AND id != ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, plateNumber);
            stmt.setLong(2, excludeId != null ? excludeId : -1);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
    
    /**
     * Updates vehicle availability status
     */
    public void updateAvailability(Long vehicleId, boolean isAvailable) throws SQLException {
        String sql = "UPDATE vehicles SET is_available = ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isAvailable);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, vehicleId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Maps ResultSet to Vehicle object
     */
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        VehicleType type = VehicleType.valueOf(rs.getString("vehicle_type"));
        
        Vehicle vehicle;
        switch (type) {
            case AUTOMOBILE:
                vehicle = new Automobile();
                break;
            case HELICOPTER:
                vehicle = new Helicopter();
                break;
            case MOTORCYCLE:
                vehicle = new Motorcycle();
                break;
            default:
                throw new SQLException("Unknown vehicle type: " + type);
        }
        
        vehicle.setId(rs.getLong("id"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setColor(rs.getString("color"));
        vehicle.setPlateNumber(rs.getString("plate_number"));
        vehicle.setValueTl(rs.getBigDecimal("value_tl"));
        vehicle.setCapacity(rs.getInt("capacity"));
        vehicle.setEnginePower(rs.getString("engine_power"));
        vehicle.setFuelType(rs.getString("fuel_type"));
        vehicle.setTransmission(rs.getString("transmission"));
        vehicle.setAvailable(rs.getBoolean("is_available"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            vehicle.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            vehicle.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return vehicle;
    }
} 