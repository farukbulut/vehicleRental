package com.carrental.model;

import com.carrental.model.enums.VehicleType;

import java.math.BigDecimal;

public class Helicopter extends Vehicle {

    public Helicopter() {
        super();
    }

    public Helicopter(String brand, String model, int year, String color, String plateNumber,
                      BigDecimal valueTl, int capacity, String enginePower, String fuelType, String transmission) {
        super(brand, model, year, color, plateNumber, valueTl, capacity, enginePower, fuelType, transmission);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.HELICOPTER;
    }

    @Override
    public BigDecimal getHourlyRate() {
        return new BigDecimal("2000.00");
    }

    @Override
    public BigDecimal getDailyRate() {
        return new BigDecimal("15000.00");
    }

    @Override
    public BigDecimal getWeeklyRate() {
        return new BigDecimal("90000.00");
    }

    @Override
    public BigDecimal getMonthlyRate() {
        return new BigDecimal("300000.00");
    }

    @Override
    public String getVehicleInfo() {
        return String.format("Helicopter: %s %s (%d) - %s power, %d passengers",
                brand, model, year, enginePower, capacity);
    }
}