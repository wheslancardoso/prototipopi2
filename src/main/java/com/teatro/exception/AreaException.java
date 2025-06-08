package com.teatro.exception;

/**
 * Exceção lançada quando ocorre um erro relacionado a áreas do teatro.
 */
public class AreaException extends TeatroException {
    
    public AreaException(String message) {
        super(message);
    }
    
    public AreaException(String message, Throwable cause) {
        super(message, cause);
    }
} 