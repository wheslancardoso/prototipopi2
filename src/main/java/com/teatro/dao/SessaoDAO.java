package com.teatro.dao;

import com.teatro.database.DatabaseConnection;
import com.teatro.model.HorarioDisponivel;
import com.teatro.model.Sessao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por operações de acesso a dados relacionados às sessões.
 */
public class SessaoDAO {
    
    /**
     * Atualiza o horário específico de uma sessão.
     * 
     * @param sessao Sessão a ser atualizada
     * @return true se a atualização foi bem-sucedida, false caso contrário
     */
    public boolean atualizarHorarioEspecifico(Sessao sessao) {
        String sql = "UPDATE sessoes SET horario_especifico_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (sessao.getHorarioEspecifico() != null) {
                stmt.setLong(1, sessao.getHorarioEspecifico().getId());
            } else {
                stmt.setNull(1, Types.BIGINT);
            }
            
            stmt.setLong(2, sessao.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar horário específico da sessão: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Atualiza a data de uma sessão.
     * 
     * @param sessao Sessão a ser atualizada
     * @return true se a atualização foi bem-sucedida, false caso contrário
     */
    public boolean atualizarDataSessao(Sessao sessao) {
        String sql = "UPDATE sessoes SET data_sessao = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(sessao.getDataSessao()));
            stmt.setLong(2, sessao.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar data da sessão: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca uma sessão pelo ID.
     * 
     * @param id ID da sessão
     * @return Sessão encontrada ou null se não encontrada
     */
    public Sessao buscarPorId(Long id) {
        String sql = "SELECT s.id, s.horario, s.data_sessao, s.horario_especifico_id, " +
                     "e.nome as evento_nome " +
                     "FROM sessoes s " +
                     "JOIN eventos e ON s.evento_id = e.id " +
                     "WHERE s.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String horario = rs.getString("horario");
                    LocalDate dataSessao = rs.getDate("data_sessao").toLocalDate();
                    
                    Sessao sessao = new Sessao(horario, dataSessao);
                    sessao.setId(id);
                    sessao.setNome(rs.getString("evento_nome"));
                    
                    // Busca o horário específico se existir
                    Long horarioEspecificoId = rs.getLong("horario_especifico_id");
                    if (!rs.wasNull()) {
                        HorarioDisponivelDAO horarioDAO = new HorarioDisponivelDAO();
                        HorarioDisponivel horarioEspecifico = horarioDAO.buscarPorId(horarioEspecificoId);
                        sessao.setHorarioEspecifico(horarioEspecifico);
                    }
                    
                    return sessao;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar sessão: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Busca todas as sessões de um evento.
     * 
     * @param eventoId ID do evento
     * @return Lista de sessões do evento
     */
    public List<Sessao> buscarPorEvento(Long eventoId) {
        List<Sessao> sessoes = new ArrayList<>();
        
        String sql = "SELECT s.id, s.horario, s.data_sessao, s.horario_especifico_id, " +
                     "e.nome as evento_nome " +
                     "FROM sessoes s " +
                     "JOIN eventos e ON s.evento_id = e.id " +
                     "WHERE s.evento_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, eventoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String horario = rs.getString("horario");
                    LocalDate dataSessao = rs.getDate("data_sessao").toLocalDate();
                    
                    Sessao sessao = new Sessao(horario, dataSessao);
                    sessao.setId(id);
                    sessao.setNome(rs.getString("evento_nome"));
                    
                    // Busca o horário específico se existir
                    Long horarioEspecificoId = rs.getLong("horario_especifico_id");
                    if (!rs.wasNull()) {
                        HorarioDisponivelDAO horarioDAO = new HorarioDisponivelDAO();
                        HorarioDisponivel horarioEspecifico = horarioDAO.buscarPorId(horarioEspecificoId);
                        sessao.setHorarioEspecifico(horarioEspecifico);
                    }
                    
                    sessoes.add(sessao);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar sessões por evento: " + e.getMessage());
        }
        
        return sessoes;
    }
}
