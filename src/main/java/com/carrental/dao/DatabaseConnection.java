package com.carrental.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager using Singleton pattern
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Database configuration
    private static final String URL = "jdbc:postgresql://localhost:5432/vehiclerental";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    
    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Gets the singleton instance of DatabaseConnection
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Gets the database connection
     */
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Closes the database connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
} 