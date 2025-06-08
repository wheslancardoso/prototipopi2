package com.teatro.controller;

import com.teatro.dao.SessaoDAO;
import com.teatro.model.Sessao;
import com.teatro.model.TipoSessao;
import com.teatro.util.TeatroLogger;
import com.teatro.exception.TeatroException;
import com.teatro.exception.SessaoNaoEncontradaException;
import com.teatro.database.DatabaseConnection;
import java.sql.SQLException;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class SessaoController {
    private final SessaoDAO sessaoDAO;
    private final TeatroLogger logger;

    public SessaoController() throws SQLException {
        this.sessaoDAO = new SessaoDAO(DatabaseConnection.getInstance().getConnection());
        this.logger = TeatroLogger.getInstance();
    }

    public boolean criarSessao(String eventoNome, TipoSessao tipoSessao, Date dataSessao) {
        try {
            Sessao sessao = new Sessao(eventoNome, tipoSessao, new java.sql.Timestamp(dataSessao.getTime()));
            sessaoDAO.salvar(sessao);
            return true;
        } catch (TeatroException e) {
            logger.error("Erro ao criar sessão: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar sessão: " + e.getMessage());
            throw new TeatroException("Erro ao criar sessão", e);
        }
    }

    public Optional<Sessao> buscarSessao(Long id) {
        try {
            return sessaoDAO.buscarPorId(id);
        } catch (TeatroException e) {
            logger.error("Erro ao buscar sessão: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar sessão: " + e.getMessage());
            throw new TeatroException("Erro ao buscar sessão", e);
        }
    }

    public List<Sessao> listarSessoes() {
        try {
            return sessaoDAO.listarTodos();
        } catch (TeatroException e) {
            logger.error("Erro ao listar sessões: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao listar sessões: " + e.getMessage());
            throw new TeatroException("Erro ao listar sessões", e);
        }
    }

    public List<Sessao> buscarSessoesPorEvento(Long eventoId) {
        try {
            return sessaoDAO.buscarPorEvento(eventoId);
        } catch (TeatroException e) {
            logger.error("Erro ao buscar sessões por evento: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar sessões por evento: " + e.getMessage());
            throw new TeatroException("Erro ao buscar sessões por evento", e);
        }
    }

    public void atualizarSessao(Sessao sessao) {
        try {
            sessaoDAO.atualizar(sessao);
        } catch (TeatroException e) {
            logger.error("Erro ao atualizar sessão: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar sessão: " + e.getMessage());
            throw new TeatroException("Erro ao atualizar sessão", e);
        }
    }

    public void removerSessao(Long id) {
        try {
            sessaoDAO.remover(id);
        } catch (TeatroException e) {
            logger.error("Erro ao remover sessão: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao remover sessão: " + e.getMessage());
            throw new TeatroException("Erro ao remover sessão", e);
        }
    }

    public boolean existeSessao(Long id) {
        try {
            return sessaoDAO.existe(id);
        } catch (TeatroException e) {
            logger.error("Erro ao verificar existência da sessão: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao verificar existência da sessão: " + e.getMessage());
            throw new TeatroException("Erro ao verificar existência da sessão", e);
        }
    }
}