package com.teatro.service;

import java.util.List;
import java.util.Optional;

/**
 * Interface base para serviços do sistema.
 * @param <T> O tipo da entidade que o serviço manipula
 * @param <ID> O tipo do identificador da entidade
 */
public interface Service<T, ID> {
    
    /**
     * Salva uma nova entidade.
     * @param entity A entidade a ser salva
     * @return A entidade salva
     * @throws TeatroException Se ocorrer um erro ao salvar
     */
    T salvar(T entity);
    
    /**
     * Atualiza uma entidade existente.
     * @param entity A entidade a ser atualizada
     * @return A entidade atualizada
     * @throws TeatroException Se ocorrer um erro ao atualizar
     */
    T atualizar(T entity);
    
    /**
     * Remove uma entidade.
     * @param id O identificador da entidade a ser removida
     * @throws TeatroException Se ocorrer um erro ao remover
     */
    void remover(ID id);
    
    /**
     * Busca uma entidade pelo seu identificador.
     * @param id O identificador da entidade
     * @return Um Optional contendo a entidade, se encontrada
     */
    Optional<T> buscarPorId(ID id);
    
    /**
     * Lista todas as entidades.
     * @return Uma lista contendo todas as entidades
     */
    List<T> listarTodos();
    
    /**
     * Verifica se uma entidade existe.
     * @param id O identificador da entidade
     * @return true se a entidade existe, false caso contrário
     */
    boolean existe(ID id);
} 