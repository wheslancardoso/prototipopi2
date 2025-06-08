package com.teatro.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface genérica para operações de acesso a dados.
 * @param <T> Tipo da entidade
 * @param <ID> Tipo do ID da entidade
 */
public interface DAO<T, ID> {
    /**
     * Salva uma entidade no banco de dados.
     * @param entity Entidade a ser salva
     */
    void salvar(T entity);

    /**
     * Atualiza uma entidade existente no banco de dados.
     * @param entity Entidade a ser atualizada
     */
    void atualizar(T entity);

    /**
     * Remove uma entidade do banco de dados pelo ID.
     * @param id ID da entidade a ser removida
     */
    void remover(ID id);

    /**
     * Busca uma entidade pelo ID.
     * @param id ID da entidade
     * @return Optional contendo a entidade se encontrada
     */
    Optional<T> buscarPorId(ID id);

    /**
     * Lista todas as entidades.
     * @return Lista de entidades
     */
    List<T> listarTodos();

    /**
     * Verifica se uma entidade existe pelo ID.
     * @param id ID da entidade
     * @return true se a entidade existe, false caso contrário
     */
    boolean existe(ID id);
} 