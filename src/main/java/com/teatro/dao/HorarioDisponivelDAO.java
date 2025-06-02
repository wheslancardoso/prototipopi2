package com.teatro.dao;

import com.teatro.database.DatabaseConnection;
import com.teatro.model.HorarioDisponivel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por operações de acesso a dados relacionados aos horários disponíveis.
 */
public class HorarioDisponivelDAO {
    
    /**
     * Busca todos os horários disponíveis para um determinado tipo de sessão.
     * 
     * @param tipoSessao Tipo de sessão (Manhã, Tarde, Noite)
     * @return Lista de horários disponíveis
     */
    public List<HorarioDisponivel> buscarPorTipoSessao(String tipoSessao) {
        List<HorarioDisponivel> horarios = new ArrayList<>();
        
        String sql = "SELECT id, tipo_sessao, horario, ordem FROM horarios_disponiveis WHERE tipo_sessao = ? ORDER BY ordem";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipoSessao);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String tipo = rs.getString("tipo_sessao");
                    LocalTime horario = rs.getTime("horario").toLocalTime();
                    int ordem = rs.getInt("ordem");
                    
                    horarios.add(new HorarioDisponivel(id, tipo, horario, ordem));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar horários disponíveis: " + e.getMessage());
        }
        
        return horarios;
    }
    
    /**
     * Busca um horário disponível pelo ID.
     * 
     * @param id ID do horário disponível
     * @return Horário disponível ou null se não encontrado
     */
    public HorarioDisponivel buscarPorId(Long id) {
        String sql = "SELECT id, tipo_sessao, horario, ordem FROM horarios_disponiveis WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo_sessao");
                    LocalTime horario = rs.getTime("horario").toLocalTime();
                    int ordem = rs.getInt("ordem");
                    
                    return new HorarioDisponivel(id, tipo, horario, ordem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar horário disponível: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Busca todos os horários disponíveis.
     * 
     * @return Lista de todos os horários disponíveis
     */
    public List<HorarioDisponivel> buscarTodos() {
        List<HorarioDisponivel> horarios = new ArrayList<>();
        
        String sql = "SELECT id, tipo_sessao, horario, ordem FROM horarios_disponiveis ORDER BY tipo_sessao, ordem";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Long id = rs.getLong("id");
                String tipo = rs.getString("tipo_sessao");
                LocalTime horario = rs.getTime("horario").toLocalTime();
                int ordem = rs.getInt("ordem");
                
                horarios.add(new HorarioDisponivel(id, tipo, horario, ordem));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os horários disponíveis: " + e.getMessage());
        }
        
        return horarios;
    }
}
