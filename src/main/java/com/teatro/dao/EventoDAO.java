package com.teatro.dao;

import com.teatro.model.Evento;
import com.teatro.util.TeatroLogger;
import com.teatro.exception.TeatroException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    private final TeatroLogger logger = TeatroLogger.getInstance();
    private final Connection connection;

    public EventoDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Evento> listarTodos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT id, nome, poster FROM eventos ORDER BY nome";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                String nome = rs.getString("nome");
                String poster = rs.getString("poster");
                eventos.add(new Evento(id, nome, poster));
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar eventos: " + e.getMessage());
            throw new TeatroException("Erro ao listar eventos", e);
        }
        return eventos;
    }

    public Evento buscarPorId(Long id) {
        String sql = "SELECT id, nome, poster FROM eventos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Evento(rs.getLong("id"), rs.getString("nome"), rs.getString("poster"));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar evento por id: " + e.getMessage());
            throw new TeatroException("Erro ao buscar evento por id", e);
        }
        return null;
    }
} 