package com.teatro.service;

import com.teatro.dao.DAO;
import com.teatro.exception.TeatroException;
import com.teatro.util.TeatroLogger;
import java.util.List;
import java.util.Optional;

/**
 * Implementação base abstrata para serviços.
 * @param <T> O tipo da entidade que o serviço manipula
 * @param <ID> O tipo do identificador da entidade
 * @param <D> O tipo do DAO utilizado pelo serviço
 */
public abstract class AbstractService<T, ID, D extends DAO<T, ID>> implements Service<T, ID> {
    
    protected final D dao;
    protected final TeatroLogger logger;
    
    protected AbstractService(D dao) {
        this.dao = dao;
        this.logger = TeatroLogger.getInstance();
    }
    
    @Override
    public T salvar(T entity) {
        try {
            validarAntesSalvar(entity);
            dao.salvar(entity);
            logger.info("Entidade salva com sucesso: {}", entity);
            return entity;
        } catch (Exception e) {
            logger.error("Erro ao salvar entidade: {}", e.getMessage());
            throw new TeatroException("Erro ao salvar entidade", e);
        }
    }
    
    @Override
    public T atualizar(T entity) {
        try {
            validarAntesAtualizar(entity);
            dao.atualizar(entity);
            logger.info("Entidade atualizada com sucesso: {}", entity);
            return entity;
        } catch (Exception e) {
            logger.error("Erro ao atualizar entidade: {}", e.getMessage());
            throw new TeatroException("Erro ao atualizar entidade", e);
        }
    }
    
    @Override
    public void remover(ID id) {
        try {
            validarAntesRemover(id);
            dao.remover(id);
            logger.info("Entidade removida com sucesso: {}", id);
        } catch (Exception e) {
            logger.error("Erro ao remover entidade: {}", e.getMessage());
            throw new TeatroException("Erro ao remover entidade", e);
        }
    }
    
    @Override
    public Optional<T> buscarPorId(ID id) {
        try {
            return dao.buscarPorId(id);
        } catch (Exception e) {
            logger.error("Erro ao buscar entidade por ID: {}", e.getMessage());
            throw new TeatroException("Erro ao buscar entidade por ID", e);
        }
    }
    
    @Override
    public List<T> listarTodos() {
        try {
            return dao.listarTodos();
        } catch (Exception e) {
            logger.error("Erro ao listar entidades: {}", e.getMessage());
            throw new TeatroException("Erro ao listar entidades", e);
        }
    }
    
    @Override
    public boolean existe(ID id) {
        try {
            return dao.existe(id);
        } catch (Exception e) {
            logger.error("Erro ao verificar existência da entidade: {}", e.getMessage());
            throw new TeatroException("Erro ao verificar existência da entidade", e);
        }
    }
    
    /**
     * Valida a entidade antes de salvar.
     * @param entity A entidade a ser validada
     * @throws TeatroException Se a validação falhar
     */
    protected abstract void validarAntesSalvar(T entity);
    
    /**
     * Valida a entidade antes de atualizar.
     * @param entity A entidade a ser validada
     * @throws TeatroException Se a validação falhar
     */
    protected abstract void validarAntesAtualizar(T entity);
    
    /**
     * Valida o ID antes de remover.
     * @param id O ID a ser validado
     * @throws TeatroException Se a validação falhar
     */
    protected abstract void validarAntesRemover(ID id);
} 