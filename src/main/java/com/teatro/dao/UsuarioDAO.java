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
     * Autentica um usuário pelo CPF ou email e senha.
     * @param identificador CPF ou email do usuário
     * @param senha Senha do usuário
     * @return Optional contendo o usuário se autenticado com sucesso
     */
    Optional<Usuario> autenticar(String identificador, String senha);

    /**
     * Autentica um usuário pelo email e senha.
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @return Optional contendo o usuário se autenticado com sucesso
     */
    Optional<Usuario> autenticarPorEmail(String email, String senha);

    /**
     * Verifica se existe um usuário com o CPF e email fornecidos.
     * @param cpf CPF do usuário
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> buscarPorCpfEEmail(String cpf, String email);

    /**
     * Atualiza a senha de um usuário.
     * @param id ID do usuário
     * @param novaSenha Nova senha do usuário
     */
    void atualizarSenha(Long id, String novaSenha);
} 