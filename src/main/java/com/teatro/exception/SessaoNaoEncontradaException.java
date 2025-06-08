package com.teatro.exception;

/**
 * Exceção lançada quando uma sessão não é encontrada.
 */
public class SessaoNaoEncontradaException extends TeatroException {
    
    /**
     * Constrói uma nova exceção com a mensagem especificada.
     * @param id O ID da sessão não encontrada
     */
    public SessaoNaoEncontradaException(Long id) {
        super(String.format("Sessão com ID %d não encontrada", id));
    }
    
    /**
     * Constrói uma nova exceção com a mensagem especificada.
     * @param eventoNome O nome do evento
     * @param horario O horário da sessão
     */
    public SessaoNaoEncontradaException(String eventoNome, String horario) {
        super(String.format("Sessão do evento %s no horário %s não encontrada", eventoNome, horario));
    }

    public SessaoNaoEncontradaException(String message) {
        super(message);
    }
    
    public SessaoNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
} 