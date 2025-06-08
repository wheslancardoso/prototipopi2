package com.teatro.exception;

/**
 * Exceção lançada quando um usuário não é encontrado no sistema.
 */
public class UsuarioNaoEncontradoException extends TeatroException {
    
    /**
     * Constrói uma nova exceção com a mensagem especificada.
     * @param cpf O CPF do usuário não encontrado
     */
    public UsuarioNaoEncontradoException(String cpf) {
        super(String.format("Usuário com CPF %s não encontrado", cpf));
    }
    
    /**
     * Constrói uma nova exceção com a mensagem especificada.
     * @param id O ID do usuário não encontrado
     */
    public UsuarioNaoEncontradoException(Long id) {
        super(String.format("Usuário com ID %d não encontrado", id));
    }
} 