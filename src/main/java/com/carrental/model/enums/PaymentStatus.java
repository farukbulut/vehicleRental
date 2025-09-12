package com.carrental.model.enums;

public enum PaymentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    REFUNDED("Refunded");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}