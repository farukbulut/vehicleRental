package com.carrental.model;

import com.carrental.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Deposit extends BaseModel{
    private int rentalId;
    private BigDecimal depositAmount;
    private PaymentStatus depositStatus;
    private LocalDateTime depositDate;
    private LocalDateTime refundDate;
    private BigDecimal refundAmount;
    private String notes;

    // Navigation property
    private Rental rental;

    // Default constructor
    public Deposit() {}

    // Constructor
    public Deposit(int rentalId, BigDecimal depositAmount) {
        this.rentalId = rentalId;
        this.depositAmount = depositAmount;
        this.depositStatus = PaymentStatus.PENDING;
        this.depositDate = LocalDateTime.now();
        this.refundAmount = BigDecimal.ZERO;
    }

    // Business logic methods
    public boolean isPending() {
        return depositStatus == PaymentStatus.PENDING;
    }

    public boolean isPaid() {
        return depositStatus == PaymentStatus.PAID;
    }

    public boolean isRefunded() {
        return depositStatus == PaymentStatus.REFUNDED;
    }

    public void processRefund(BigDecimal amount, String refundNotes) {
        this.refundAmount = amount;
        this.refundDate = LocalDateTime.now();
        this.depositStatus = PaymentStatus.REFUNDED;
        this.notes = refundNotes;
    }

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public PaymentStatus getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(PaymentStatus depositStatus) {
        this.depositStatus = depositStatus;
    }

    public LocalDateTime getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDateTime depositDate) {
        this.depositDate = depositDate;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }
}
