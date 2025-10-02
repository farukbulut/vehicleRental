package com.carrental.exception;

/**
 * Exception thrown when business rules are violated
 */
public class BusinessRuleException extends Exception {
    
    public BusinessRuleException(String message) {
        super(message);
    }
    
    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
} 