package com.teatro.dao;

import com.teatro.model.Ingresso;
import com.teatro.exception.TeatroException;
import com.teatro.exception.IngressoException;
import com.teatro.exception.PoltronaOcupadaException;
import com.teatro.util.TeatroLogger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.teatro.dao.SessaoDAO;
import com.teatro.database.DatabaseConnection;
import com.teatro.model.Sessao;
import com.teatro.dao.EventoDAO;
import com.teatro.model.Evento;
import com.teatro.dao.AreaDAO;
import com.teatro.model.Area;

/**
 * Implementação do DAO para a entidade Ingresso.
 */
public class IngressoDAO implements DAO<Ingresso, Long> {
    
    private final TeatroLogger logger = TeatroLogger.getInstance();
    private final Connection connection;
    
    public IngressoDAO(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void salvar(Ingresso ingresso) {
        String sql = "INSERT INTO ingressos (usuario_id, sessao_id, area_id, numero_poltrona, valor, data_compra, codigo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherStatement(stmt, ingresso);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IngressoException("Erro ao salvar ingresso: nenhuma linha afetada");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ingresso.setId(generatedKeys.getLong(1));
                } else {
                    throw new IngressoException("Erro ao salvar ingresso: ID não gerado");
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao salvar ingresso: " + e.getMessage());
            throw new TeatroException("Erro ao salvar ingresso", e);
        }
    }
    
    @Override
    public void atualizar(Ingresso ingresso) {
        String sql = "UPDATE ingressos SET usuario_id = ?, sessao_id = ?, area_id = ?, numero_poltrona = ?, valor = ?, data_compra = ?, codigo = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            preencherStatement(stmt, ingresso);
            stmt.setLong(8, ingresso.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IngressoException("Ingresso com ID " + ingresso.getId() + " não encontrado");
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar ingresso: " + e.getMessage());
            throw new TeatroException("Erro ao atualizar ingresso", e);
        }
    }
    
    @Override
    public void remover(Long id) {
        String sql = "DELETE FROM ingressos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IngressoException("Ingresso com ID " + id + " não encontrado");
            }
        } catch (SQLException e) {
            logger.error("Erro ao remover ingresso: " + e.getMessage());
            throw new TeatroException("Erro ao remover ingresso", e);
        }
    }
    
    @Override
    public Optional<Ingresso> buscarPorId(Long id) {
        String sql = "SELECT * FROM ingressos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarIngresso(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar ingresso por ID: " + e.getMessage());
            throw new TeatroException("Erro ao buscar ingresso por ID", e);
        }
    }
    
    @Override
    public List<Ingresso> listarTodos() {
        String sql = "SELECT * FROM ingressos ORDER BY data_compra";
        List<Ingresso> ingressos = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ingressos.add(montarIngresso(rs));
            }
            return ingressos;
        } catch (SQLException e) {
            logger.error("Erro ao listar ingressos: " + e.getMessage());
            throw new TeatroException("Erro ao listar ingressos", e);
        }
    }
    
    @Override
    public boolean existe(Long id) {
        String sql = "SELECT COUNT(*) FROM ingressos WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            logger.error("Erro ao verificar existência do ingresso: {}", e.getMessage());
            throw new TeatroException("Erro ao verificar existência do ingresso", e);
        }
    }
    
    /**
     * Busca ingressos por usuário.
     * @param usuarioId O ID do usuário
     * @return Lista de ingressos do usuário
     */
    public List<Ingresso> buscarPorUsuario(Long usuarioId) {
        String sql = "SELECT * FROM ingressos WHERE usuario_id = ? ORDER BY data_compra";
        List<Ingresso> ingressos = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingressos.add(montarIngresso(rs));
                }
                return ingressos;
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar ingressos do usuário: " + e.getMessage());
            throw new TeatroException("Erro ao buscar ingressos do usuário", e);
        }
    }
    
    /**
     * Busca ingressos por sessão.
     * @param sessaoId O ID da sessão
     * @return Lista de ingressos da sessão
     */
    public List<Ingresso> buscarPorSessao(Long sessaoId) {
        String sql = "SELECT * FROM ingressos WHERE sessao_id = ? ORDER BY data_compra";
        List<Ingresso> ingressos = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, sessaoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingressos.add(montarIngresso(rs));
                }
                return ingressos;
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar ingressos da sessão: " + e.getMessage());
            throw new TeatroException("Erro ao buscar ingressos da sessão", e);
        }
    }
    
    /**
     * Verifica se uma poltrona está ocupada.
     * @param sessaoId O ID da sessão
     * @param areaId O ID da área
     * @param numeroPoltrona O número da poltrona
     * @return true se a poltrona estiver ocupada, false caso contrário
     */
    public boolean poltronaOcupada(Long sessaoId, Long areaId, int numeroPoltrona) {
        String sql = "SELECT COUNT(*) FROM ingressos WHERE sessao_id = ? AND area_id = ? AND numero_poltrona = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, sessaoId);
            stmt.setLong(2, areaId);
            stmt.setInt(3, numeroPoltrona);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            logger.error("Erro ao verificar ocupação da poltrona: " + e.getMessage());
            throw new TeatroException("Erro ao verificar ocupação da poltrona", e);
        }
    }
    
    private void preencherStatement(PreparedStatement stmt, Ingresso ingresso) throws SQLException {
        stmt.setLong(1, ingresso.getUsuarioId());
        stmt.setLong(2, ingresso.getSessaoId());
        stmt.setLong(3, ingresso.getAreaId());
        stmt.setInt(4, ingresso.getNumeroPoltrona());
        stmt.setDouble(5, ingresso.getValor());
        stmt.setTimestamp(6, ingresso.getDataCompra());
        stmt.setString(7, ingresso.getCodigo());
    }
    
    private Ingresso montarIngresso(ResultSet rs) throws SQLException {
        Ingresso ingresso = new Ingresso();
        ingresso.setId(rs.getLong("id"));
        ingresso.setUsuarioId(rs.getLong("usuario_id"));
        ingresso.setSessaoId(rs.getLong("sessao_id"));
        ingresso.setAreaId(rs.getLong("area_id"));
        ingresso.setNumeroPoltrona(rs.getInt("numero_poltrona"));
        ingresso.setValor(rs.getDouble("valor"));
        ingresso.setDataCompra(rs.getTimestamp("data_compra"));
        ingresso.setCodigo(rs.getString("codigo"));
        // Preencher tipoSessao buscando a sessão
        try {
            SessaoDAO sessaoDAO = new SessaoDAO(DatabaseConnection.getInstance().getConnection());
            Long sessaoId = rs.getLong("sessao_id");
            Optional<Sessao> sessaoOpt = sessaoDAO.buscarPorId(sessaoId);
            if (sessaoOpt.isPresent()) {
                Sessao sessao = sessaoOpt.get();
                ingresso.setTipoSessao(sessao.getTipoSessao());
                // Buscar evento e preencher nome
                Long eventoId = sessao.getEventoId();
                if (eventoId != null) {
                    EventoDAO eventoDAO = new EventoDAO(DatabaseConnection.getInstance().getConnection());
                    Evento evento = eventoDAO.buscarPorId(eventoId);
                    if (evento != null) {
                        ingresso.setEventoNome(evento.getNome());
                    }
                }
            }
            // Buscar área e preencher nome da área
            AreaDAO areaDAO = new AreaDAO(DatabaseConnection.getInstance().getConnection());
            Optional<Area> areaOpt = areaDAO.buscarPorId(ingresso.getAreaId());
            areaOpt.ifPresent(area -> ingresso.setAreaNome(area.getNome()));
        } catch (Exception e) {
            logger.error("Erro ao preencher tipoSessao/nome do evento/nome da área do ingresso: " + e.getMessage());
        }
        return ingresso;
    }
} 