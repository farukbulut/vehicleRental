package com.carrental.model.enums;

public enum PricingType {
    HOURLY("Hourly", 1),
    DAILY("Daily", 24),
    WEEKLY("Weekly", 168), // 24 * 7
    MONTHLY("Monthly", 720); // 24 * 30

    private final String displayName;
    private final int hoursMultiplier;

    PricingType(String displayName, int hoursMultiplier) {
        this.displayName = displayName;
        this.hoursMultiplier = hoursMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getHoursMultiplier() {
        return hoursMultiplier;
    }
}

