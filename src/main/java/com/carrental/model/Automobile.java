package com.carrental.model;

import com.carrental.model.enums.VehicleType;

import java.math.BigDecimal;

public class Automobile extends Vehicle {

    public Automobile() {
        super();
    }

    public Automobile(String brand, String model, int year, String color, String plateNumber,
                      BigDecimal valueTl, int capacity, String enginePower, String fuelType, String transmission) {
        super(brand, model, year, color, plateNumber, valueTl, capacity, enginePower, fuelType, transmission);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.AUTOMOBILE;
    }

    @Override
    public BigDecimal getHourlyRate() {
        return new BigDecimal("50.00");
    }

    @Override
    public BigDecimal getDailyRate() {
        return new BigDecimal("300.00");
    }

    @Override
    public BigDecimal getWeeklyRate() {
        return new BigDecimal("1800.00");
    }

    @Override
    public BigDecimal getMonthlyRate() {
        return new BigDecimal("6000.00");
    }

    @Override
    public String getVehicleInfo() {
        return String.format("Automobile: %s %s (%d) - %s, %s, %d passengers",
                brand, model, year, fuelType, transmission, capacity);
    }
}
