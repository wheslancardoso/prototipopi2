package com.teatro.dao;

import com.teatro.database.DatabaseConnection;
import com.teatro.model.Area;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {
    public List<Area> listarTodas() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT id, nome, preco, capacidade_total FROM areas";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Area area = new Area(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getInt("capacidade_total")
                );
                areas.add(area);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return areas;
    }
} 