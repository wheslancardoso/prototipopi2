package com.teatro.exception;

/**
 * Classe base para exceções do domínio do sistema de teatro.
 */
public class TeatroException extends RuntimeException {
    
    /**
     * Constrói uma nova exceção com a mensagem especificada.
     * @param message A mensagem de erro
     */
    public TeatroException(String message) {
        super(message);
    }
    
    /**
     * Constrói uma nova exceção com a mensagem e a causa especificadas.
     * @param message A mensagem de erro
     * @param cause A causa da exceção
     */
    public TeatroException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constrói uma nova exceção com a causa especificada.
     * @param cause A causa da exceção
     */
    public TeatroException(Throwable cause) {
        super(cause);
    }
} 