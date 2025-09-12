package com.carrental.model;

import com.carrental.model.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class User extends BaseModel{
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String phone;
    private LocalDate birthDate;
    private UserRole role;
    private boolean isActive;

    // Default constructor
    public User() {}

    // Constructor for new user creation
    public User(String firstName, String lastName, String email, String passwordHash,
                String phone, LocalDate birthDate, UserRole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.birthDate = birthDate;
        this.role = role;
        this.setCreatedAt(LocalDateTime.now());
        this.isActive = true;
    }

    // Business logic methods
    public int getAge() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public boolean isEligibleForHighValueRental() {
        return getAge() >= 30;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
