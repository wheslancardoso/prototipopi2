package com.teatro.dao;

import com.teatro.model.Sessao;
import com.teatro.model.TipoSessao;
import com.teatro.exception.TeatroException;
import com.teatro.exception.SessaoNaoEncontradaException;
import com.teatro.util.TeatroLogger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe responsável por operações de acesso a dados relacionados às sessões.
 */
public class SessaoDAO implements DAO<Sessao, Long> {
    private final TeatroLogger logger = TeatroLogger.getInstance();
    private final Connection connection;
    
    public SessaoDAO(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void salvar(Sessao sessao) {
        String sql = "INSERT INTO sessoes (nome, tipo_sessao, data) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, sessao.getNome());
            stmt.setString(2, sessao.getTipoSessao().name());
            stmt.setTimestamp(3, sessao.getData());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new TeatroException("Erro ao salvar sessão: nenhuma linha afetada");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sessao.setId(generatedKeys.getLong(1));
                } else {
                    throw new TeatroException("Erro ao salvar sessão: ID não gerado");
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao salvar sessão: " + e.getMessage());
            throw new TeatroException("Erro ao salvar sessão", e);
        }
    }
    
    @Override
    public void atualizar(Sessao sessao) {
        String sql = "UPDATE sessoes SET nome = ?, tipo_sessao = ?, data = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sessao.getNome());
            stmt.setString(2, sessao.getTipoSessao().name());
            stmt.setTimestamp(3, sessao.getData());
            stmt.setLong(4, sessao.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SessaoNaoEncontradaException("Sessão com ID " + sessao.getId() + " não encontrada");
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar sessão: " + e.getMessage());
            throw new TeatroException("Erro ao atualizar sessão", e);
        }
    }
    
    @Override
    public void remover(Long id) {
        String sql = "DELETE FROM sessoes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SessaoNaoEncontradaException("Sessão com ID " + id + " não encontrada");
            }
        } catch (SQLException e) {
            logger.error("Erro ao remover sessão: " + e.getMessage());
            throw new TeatroException("Erro ao remover sessão", e);
        }
    }
    
    @Override
    public Optional<Sessao> buscarPorId(Long id) {
        String sql = "SELECT * FROM sessoes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarSessao(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar sessão por ID: " + e.getMessage());
            throw new TeatroException("Erro ao buscar sessão por ID", e);
        }
    }
    
    @Override
    public List<Sessao> listarTodos() {
        String sql = "SELECT * FROM sessoes ORDER BY data";
        List<Sessao> sessoes = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sessoes.add(montarSessao(rs));
            }
            return sessoes;
        } catch (SQLException e) {
            logger.error("Erro ao listar sessões: " + e.getMessage());
            throw new TeatroException("Erro ao listar sessões", e);
        }
    }
    
    private Sessao montarSessao(ResultSet rs) throws SQLException {
        Sessao sessao = new Sessao();
        sessao.setId(rs.getLong("id"));
        sessao.setEventoId(rs.getLong("evento_id"));
        String horarioStr = rs.getString("horario");
        TipoSessao tipoSessao = null;
        for (TipoSessao ts : TipoSessao.values()) {
            if (ts.getDescricao().equalsIgnoreCase(horarioStr)) {
                tipoSessao = ts;
                break;
            }
        }
        sessao.setTipoSessao(tipoSessao);
        sessao.setData(rs.getTimestamp("data_sessao"));
        return sessao;
    }
    
    public boolean existe(Long id) {
        String sql = "SELECT COUNT(*) FROM sessoes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            logger.error("Erro ao verificar existência da sessão: " + e.getMessage());
            throw new TeatroException("Erro ao verificar existência da sessão", e);
        }
    }
    
    public List<Sessao> buscarPorEvento(Long eventoId) {
        String sql = "SELECT * FROM sessoes WHERE evento_id = ? ORDER BY data_sessao, horario";
        List<Sessao> sessoes = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, eventoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessoes.add(montarSessao(rs));
                }
            }
            return sessoes;
        } catch (SQLException e) {
            logger.error("Erro ao buscar sessões por evento: " + e.getMessage());
            throw new TeatroException("Erro ao buscar sessões por evento", e);
        }
    }
}
