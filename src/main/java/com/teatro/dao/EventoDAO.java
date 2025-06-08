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
        String sql = "SELECT id, nome FROM eventos ORDER BY nome";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                String nome = rs.getString("nome");
                eventos.add(new Evento(id, nome));
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar eventos: " + e.getMessage());
            throw new TeatroException("Erro ao listar eventos", e);
        }
        return eventos;
    }
} 