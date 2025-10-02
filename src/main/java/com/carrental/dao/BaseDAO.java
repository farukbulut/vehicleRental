package com.carrental.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base Data Access Object class
 * Provides common database operations and connection management
 */
public abstract class BaseDAO {
    
    protected final Connection connection;
    
    /**
     * Constructor initializes database connection
     */
    protected BaseDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Gets the database connection
     */
    protected Connection getConnection() {
        return connection;
    }
}
