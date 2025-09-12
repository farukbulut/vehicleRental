package com.carrental.model;

import com.carrental.model.enums.PricingType;
import com.carrental.model.enums.RentalStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class Rental {
    private int userId;
    private int vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PricingType pricingType;
    private int rentalDuration;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private RentalStatus status;

    // Navigation properties (not stored in DB, populated by services)
    private User user;
    private Vehicle vehicle;

    // Default constructor
    public Rental() {}

    // Constructor for new rental
    public Rental(int userId, int vehicleId, LocalDateTime startDate, LocalDateTime endDate,
                  PricingType pricingType, int rentalDuration, BigDecimal unitPrice,
                  BigDecimal totalAmount, BigDecimal depositAmount) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricingType = pricingType;
        this.rentalDuration = rentalDuration;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.depositAmount = depositAmount;
        this.status = RentalStatus.ACTIVE;
    }

    // Business logic methods
    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    public boolean isCompleted() {
        return status == RentalStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == RentalStatus.CANCELLED;
    }

    public boolean hasConflictWith(LocalDateTime checkStartDate, LocalDateTime checkEndDate) {
        return !(endDate.isBefore(checkStartDate) || startDate.isAfter(checkEndDate));
    }

    public Duration getRentalPeriod() {
        return Duration.between(startDate, endDate);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public int getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(int rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}

