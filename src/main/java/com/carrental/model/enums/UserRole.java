package com.carrental.model.enums;

public enum UserRole {
    ADMIN("Admin - Full system access"),
    INDIVIDUAL("Individual Customer - Personal rentals"),
    CORPORATE("Corporate Customer - Business rentals");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}