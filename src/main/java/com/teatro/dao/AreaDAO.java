package com.teatro.dao;

import com.teatro.model.Area;
import com.teatro.exception.TeatroException;
import com.teatro.exception.AreaException;
import com.teatro.util.TeatroLogger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AreaDAO implements DAO<Area, Long> {
    private final TeatroLogger logger = TeatroLogger.getInstance();
    private final Connection connection;
    
    public AreaDAO(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void salvar(Area area) {
        String sql = "INSERT INTO areas (nome, preco, capacidade_total) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, area.getNome());
            stmt.setDouble(2, area.getPreco());
            stmt.setInt(3, area.getCapacidadeTotal());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new AreaException("Erro ao salvar área: nenhuma linha afetada");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    area.setId(generatedKeys.getLong(1));
                } else {
                    throw new AreaException("Erro ao salvar área: ID não gerado");
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao salvar área: " + e.getMessage());
            throw new TeatroException("Erro ao salvar área", e);
        }
    }
    
    @Override
    public void atualizar(Area area) {
        String sql = "UPDATE areas SET nome = ?, preco = ?, capacidade_total = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, area.getNome());
            stmt.setDouble(2, area.getPreco());
            stmt.setInt(3, area.getCapacidadeTotal());
            stmt.setLong(4, area.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new AreaException("Área com ID " + area.getId() + " não encontrada");
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar área: " + e.getMessage());
            throw new TeatroException("Erro ao atualizar área", e);
        }
    }
    
    @Override
    public void remover(Long id) {
        String sql = "DELETE FROM areas WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new AreaException("Área com ID " + id + " não encontrada");
            }
        } catch (SQLException e) {
            logger.error("Erro ao remover área: " + e.getMessage());
            throw new TeatroException("Erro ao remover área", e);
        }
    }
    
    @Override
    public Optional<Area> buscarPorId(Long id) {
        String sql = "SELECT * FROM areas WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarArea(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar área por ID: " + e.getMessage());
            throw new TeatroException("Erro ao buscar área por ID", e);
        }
    }
    
    @Override
    public List<Area> listarTodos() {
        String sql = "SELECT * FROM areas ORDER BY nome";
        List<Area> areas = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                areas.add(montarArea(rs));
            }
            return areas;
        } catch (SQLException e) {
            logger.error("Erro ao listar áreas: " + e.getMessage());
            throw new TeatroException("Erro ao listar áreas", e);
        }
    }
    
    @Override
    public boolean existe(Long id) {
        String sql = "SELECT COUNT(*) FROM areas WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            logger.error("Erro ao verificar existência da área: " + e.getMessage());
            throw new TeatroException("Erro ao verificar existência da área", e);
        }
    }
    
    public List<Area> buscarPorSessao(Long sessaoId) {
        String sql = "SELECT a.* FROM areas a " +
                    "INNER JOIN sessoes_areas sa ON a.id = sa.area_id " +
                    "WHERE sa.sessao_id = ? ORDER BY a.nome";
        List<Area> areas = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, sessaoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    areas.add(montarArea(rs));
                }
                return areas;
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar áreas da sessão: " + e.getMessage());
            throw new TeatroException("Erro ao buscar áreas da sessão", e);
        }
    }
    
    private Area montarArea(ResultSet rs) throws SQLException {
        Area area = new Area();
        area.setId(rs.getLong("id"));
        area.setNome(rs.getString("nome"));
        area.setPreco(rs.getDouble("preco"));
        area.setCapacidadeTotal(rs.getInt("capacidade_total"));
        return area;
    }
} 