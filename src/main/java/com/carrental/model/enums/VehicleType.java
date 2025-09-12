package com.carrental.model.enums;

public enum VehicleType {
    AUTOMOBILE("Automobile"),
    HELICOPTER("Helicopter"),
    MOTORCYCLE("Motorcycle");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
