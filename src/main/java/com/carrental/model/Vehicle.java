package com.carrental.model;

import com.carrental.model.enums.PricingType;
import com.carrental.model.enums.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Vehicle extends BaseModel {
    protected String brand;
    protected String model;
    protected int year;
    protected String color;
    protected String plateNumber;
    protected BigDecimal valueTl;
    protected int capacity;
    protected String enginePower;
    protected String fuelType;
    protected String transmission;
    protected boolean isAvailable;

    // Default constructor
    public Vehicle() {}

    // Constructor
    public Vehicle(String brand, String model, int year, String color, String plateNumber,
                   BigDecimal valueTl, int capacity, String enginePower, String fuelType, String transmission) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.plateNumber = plateNumber;
        this.valueTl = valueTl;
        this.capacity = capacity;
        this.enginePower = enginePower;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.isAvailable = true;
        this.setCreatedAt(LocalDateTime.now());
    }

    // Abstract methods that must be implemented by subclasses
    public abstract VehicleType getVehicleType();
    public abstract BigDecimal getHourlyRate();
    public abstract BigDecimal getDailyRate();
    public abstract BigDecimal getWeeklyRate();
    public abstract BigDecimal getMonthlyRate();
    public abstract String getVehicleInfo();

    // Business logic methods
    public boolean requiresDeposit() {
        return valueTl.compareTo(new BigDecimal("2000000")) > 0;
    }

    public BigDecimal getDepositAmount() {
        if (requiresDeposit()) {
            return valueTl.multiply(new BigDecimal("0.10")); // 10% deposit
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateRentalCost(PricingType pricingType, int duration) {
        BigDecimal rate;
        switch (pricingType) {
            case HOURLY:
                rate = getHourlyRate();
                break;
            case DAILY:
                rate = getDailyRate();
                break;
            case WEEKLY:
                rate = getWeeklyRate();
                break;
            case MONTHLY:
                rate = getMonthlyRate();
                break;
            default:
                throw new IllegalArgumentException("Invalid pricing type: " + pricingType);
        }
        return rate.multiply(new BigDecimal(duration));
    }

    public String getDisplayName() {
        return brand + " " + model + " (" + year + ")";
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public BigDecimal getValueTl() {
        return valueTl;
    }

    public void setValueTl(BigDecimal valueTl) {
        this.valueTl = valueTl;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(String enginePower) {
        this.enginePower = enginePower;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}