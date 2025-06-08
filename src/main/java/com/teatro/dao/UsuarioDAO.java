package com.teatro.dao;

import com.teatro.model.Usuario;
import java.util.Optional;

/**
 * Interface específica para operações de DAO de Usuário.
 */
public interface UsuarioDAO extends DAO<Usuario, Long> {
    /**
     * Busca um usuário pelo CPF.
     * @param cpf CPF do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> buscarPorCpf(String cpf);

    /**
     * Busca um usuário pelo email.
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> buscarPorEmail(String email);

    /**
     * Autentica um usuário pelo CPF e senha.
     * @param cpf CPF do usuário
     * @param senha Senha do usuário
     * @return Optional contendo o usuário se autenticado com sucesso
     */
    Optional<Usuario> autenticar(String cpf, String senha);
} 