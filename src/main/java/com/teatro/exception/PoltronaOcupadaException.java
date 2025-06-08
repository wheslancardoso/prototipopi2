package com.teatro.exception;

/**
 * Exceção lançada quando uma poltrona está ocupada e não pode ser reservada.
 */
public class PoltronaOcupadaException extends TeatroException {
    
    /**
     * Constrói uma nova exceção com a mensagem especificada.
     * @param numeroPoltrona O número da poltrona que está ocupada
     * @param areaNome O nome da área onde a poltrona está
     */
    public PoltronaOcupadaException(int numeroPoltrona, String areaNome) {
        super(String.format("A poltrona %d da área %s já está ocupada", numeroPoltrona, areaNome));
    }

    public PoltronaOcupadaException(String message) {
        super(message);
    }
    
    public PoltronaOcupadaException(String message, Throwable cause) {
        super(message, cause);
    }
} 