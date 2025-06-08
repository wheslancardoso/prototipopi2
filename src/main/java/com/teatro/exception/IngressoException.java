package com.teatro.exception;

/**
 * Exceção lançada quando ocorre um erro relacionado a ingressos.
 */
public class IngressoException extends TeatroException {
    
    public IngressoException(String message) {
        super(message);
    }
    
    public IngressoException(String message, Throwable cause) {
        super(message, cause);
    }
} 