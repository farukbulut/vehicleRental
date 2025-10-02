package com.carrental.service;

import com.carrental.dao.UserDAO;
import com.carrental.exception.AuthenticationException;
import com.carrental.exception.BusinessRuleException;
import com.carrental.model.User;
import com.carrental.model.enums.UserRole;
import com.carrental.util.PasswordHasher;
import com.carrental.util.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Service class for authentication and user management
 */
public class AuthService {
    
    private final UserDAO userDAO;
    private User currentUser;
    
    public AuthService() throws SQLException {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Registers a new user
     */
    public User register(String firstName, String lastName, String email, String password,
                        String phone, LocalDate birthDate, UserRole role) 
            throws SQLException, BusinessRuleException {
        
        // Validate inputs
        if (!ValidationUtils.isNotEmpty(firstName)) {
            throw new BusinessRuleException("First name is required");
        }
        
        if (!ValidationUtils.isNotEmpty(lastName)) {
            throw new BusinessRuleException("Last name is required");
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            throw new BusinessRuleException("Invalid email format");
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            throw new BusinessRuleException("Password must be at least 6 characters long");
        }
        
        if (!ValidationUtils.isValidPhone(phone)) {
            throw new BusinessRuleException("Invalid phone number format");
        }
        
        if (birthDate == null || birthDate.isAfter(LocalDate.now().minusYears(18))) {
            throw new BusinessRuleException("User must be at least 18 years old");
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            throw new BusinessRuleException("Email already registered");
        }
        
        // Hash password
        String passwordHash = PasswordHasher.hashPassword(password);
        
        // Create user
        User user = new User(firstName, lastName, email, passwordHash, phone, birthDate, role);
        return userDAO.create(user);
    }
    
    /**
     * Authenticates a user with email and password
     */
    public User login(String email, String password) throws SQLException, AuthenticationException {
        
        // Validate inputs
        if (!ValidationUtils.isValidEmail(email)) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        if (!ValidationUtils.isNotEmpty(password)) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        // Find user by email
        Optional<User> userOpt = userDAO.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        User user = userOpt.get();
        
        // Verify password
        if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        // Check if user is active
        if (!user.isActive()) {
            throw new AuthenticationException("User account is inactive");
        }
        
        this.currentUser = user;
        return user;
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Gets the currently logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Checks if current user is admin
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }
    
    /**
     * Checks if current user is corporate
     */
    public boolean isCorporate() {
        return currentUser != null && currentUser.getRole() == UserRole.CORPORATE;
    }
    
    /**
     * Checks if current user is individual
     */
    public boolean isIndividual() {
        return currentUser != null && currentUser.getRole() == UserRole.INDIVIDUAL;
    }
    
    /**
     * Updates user profile
     */
    public void updateProfile(User user) throws SQLException, BusinessRuleException {
        if (!ValidationUtils.isNotEmpty(user.getFirstName())) {
            throw new BusinessRuleException("First name is required");
        }
        
        if (!ValidationUtils.isNotEmpty(user.getLastName())) {
            throw new BusinessRuleException("Last name is required");
        }
        
        if (!ValidationUtils.isValidPhone(user.getPhone())) {
            throw new BusinessRuleException("Invalid phone number format");
        }
        
        userDAO.update(user);
        
        // Update current user if it's the same user
        if (currentUser != null && currentUser.getId().equals(user.getId())) {
            this.currentUser = user;
        }
    }
} 