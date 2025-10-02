package com.carrental;

import com.carrental.ui.MenuManager;

/**
 * Main entry point for the Vehicle Rental Application
 * 
 * This application is a terminal-based vehicle rental system that implements:
 * - Layered architecture (Model, DAO, Service, UI)
 * - User authentication with SHA-256 password hashing
 * - Role-based access control (ADMIN, CORPORATE, INDIVIDUAL)
 * - Vehicle management (CRUD operations)
 * - Rental management with transaction support
 * - Business rules validation
 * - Deposit management
 * 
 * @author Vehicle Rental Team
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            // Initialize and start the menu system
            MenuManager menuManager = new MenuManager();
            menuManager.start();
            
        } catch (Exception e) {
            System.err.println("Uygulama başlatılırken bir hata oluştu: " + e.getMessage());
            System.err.println("Lütfen veritabanı bağlantınızı kontrol edin.");
            e.printStackTrace();
        }
    }
}