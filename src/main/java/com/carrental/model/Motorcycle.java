package com.carrental.model;

import com.carrental.model.enums.VehicleType;

import java.math.BigDecimal;

public class Motorcycle extends Vehicle {

    public Motorcycle() {
        super();
    }

    public Motorcycle(String brand, String model, int year, String color, String plateNumber,
                      BigDecimal valueTl, int capacity, String enginePower, String fuelType, String transmission) {
        super(brand, model, year, color, plateNumber, valueTl, capacity, enginePower, fuelType, transmission);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.MOTORCYCLE;
    }

    @Override
    public BigDecimal getHourlyRate() {
        return new BigDecimal("25.00");
    }

    @Override
    public BigDecimal getDailyRate() {
        return new BigDecimal("150.00");
    }

    @Override
    public BigDecimal getWeeklyRate() {
        return new BigDecimal("900.00");
    }

    @Override
    public BigDecimal getMonthlyRate() {
        return new BigDecimal("3000.00");
    }

    @Override
    public String getVehicleInfo() {
        return String.format("Motorcycle: %s %s (%d) - %s, %s",
                brand, model, year, enginePower, transmission);
    }
}