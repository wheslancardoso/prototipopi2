package com.teatro.dao;

import com.teatro.database.DatabaseConnection;
import com.teatro.model.Ingresso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class IngressoDAO {
    
    public boolean salvar(Ingresso ingresso) {
        String sql = "INSERT INTO ingressos (usuario_id, sessao_id, area_id, numero_poltrona, valor, codigo) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, ingresso.getUsuarioId());
            stmt.setLong(2, ingresso.getSessaoId());
            stmt.setLong(3, ingresso.getAreaId());
            stmt.setInt(4, ingresso.getNumeroPoltrona());
            stmt.setDouble(5, ingresso.getValor());
            stmt.setString(6, ingresso.getCodigo());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Integer> buscarPoltronasOcupadas(Long sessaoId, Long areaId) {
        String sql = "SELECT numero_poltrona FROM ingressos WHERE sessao_id = ? AND area_id = ?";
        List<Integer> poltronasOcupadas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, sessaoId);
            stmt.setLong(2, areaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                poltronasOcupadas.add(rs.getInt("numero_poltrona"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return poltronasOcupadas;
    }
    
    public List<Ingresso> buscarPorUsuarioId(Long usuarioId) {
        String sql = """
            SELECT i.*, e.nome as evento_nome, s.horario, a.nome as area_nome, 
                   u.nome as usuario_nome, u.cpf as usuario_cpf, u.email as usuario_email,
                   u.telefone as usuario_telefone, u.tipo_usuario as usuario_tipo
            FROM ingressos i
            JOIN sessoes s ON i.sessao_id = s.id
            JOIN eventos e ON s.evento_id = e.id
            JOIN areas a ON i.area_id = a.id
            JOIN usuarios u ON i.usuario_id = u.id
            WHERE i.usuario_id = ?
            ORDER BY i.data_compra DESC
            """;
        
        List<Ingresso> ingressos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Ingresso ingresso = new Ingresso();
                ingresso.setId(rs.getLong("id"));
                ingresso.setUsuarioId(rs.getLong("usuario_id"));
                ingresso.setSessaoId(rs.getLong("sessao_id"));
                ingresso.setAreaId(rs.getLong("area_id"));
                ingresso.setNumeroPoltrona(rs.getInt("numero_poltrona"));
                ingresso.setValor(rs.getDouble("valor"));
                ingresso.setDataCompra(rs.getTimestamp("data_compra"));
                ingresso.setEventoNome(rs.getString("evento_nome"));
                ingresso.setHorario(rs.getString("horario"));
                ingresso.setAreaNome(rs.getString("area_nome"));
                ingressos.add(ingresso);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingressos;
    }
    
    public boolean atualizarStatusPoltrona(Long sessaoId, Long areaId, int numeroPoltrona, boolean ocupada) {
        String sql = "UPDATE sessoes_areas SET poltronas_disponiveis = poltronas_disponiveis + ? WHERE sessao_id = ? AND area_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ocupada ? -1 : 1);
            stmt.setLong(2, sessaoId);
            stmt.setLong(3, areaId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isPoltronaOcupada(Long sessaoId, Long areaId, int numeroPoltrona) {
        String sql = "SELECT COUNT(*) FROM ingressos WHERE sessao_id = ? AND area_id = ? AND numero_poltrona = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, sessaoId);
            stmt.setLong(2, areaId);
            stmt.setInt(3, numeroPoltrona);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Map<String, Object> buscarEstatisticasVendas() {
        Map<String, Object> estatisticas = new HashMap<>();
        
        // Peça com mais ingressos vendidos
        String sqlMaisVendidos = """
            SELECT e.nome, COUNT(*) as total_vendas
            FROM ingressos i
            JOIN sessoes s ON i.sessao_id = s.id
            JOIN eventos e ON s.evento_id = e.id
            GROUP BY e.id, e.nome
            ORDER BY total_vendas DESC
            LIMIT 1
        """;
        
        // Peça com menos ingressos vendidos
        String sqlMenosVendidos = """
            SELECT e.nome, COUNT(*) as total_vendas
            FROM ingressos i
            JOIN sessoes s ON i.sessao_id = s.id
            JOIN eventos e ON s.evento_id = e.id
            GROUP BY e.id, e.nome
            ORDER BY total_vendas ASC
            LIMIT 1
        """;
        
        // Sessão com maior ocupação
        String sqlMaiorOcupacao = """
            SELECT e.nome, s.horario, s.data_sessao,
                   COUNT(*) * 100.0 / (
                       SELECT SUM(capacidade_total)
                       FROM areas
                   ) as ocupacao
            FROM ingressos i
            JOIN sessoes s ON i.sessao_id = s.id
            JOIN eventos e ON s.evento_id = e.id
            GROUP BY s.id, e.nome, s.horario, s.data_sessao
            ORDER BY ocupacao DESC
            LIMIT 1
        """;
        
        // Sessão com menor ocupação
        String sqlMenorOcupacao = """
            SELECT e.nome, s.horario, s.data_sessao,
                   COUNT(*) * 100.0 / (
                       SELECT SUM(capacidade_total)
                       FROM areas
                   ) as ocupacao
            FROM ingressos i
            JOIN sessoes s ON i.sessao_id = s.id
            JOIN eventos e ON s.evento_id = e.id
            GROUP BY s.id, e.nome, s.horario, s.data_sessao
            ORDER BY ocupacao ASC
            LIMIT 1
        """;
        
        // Peça mais lucrativa
        String sqlMaisLucrativa = """
            SELECT e.nome, SUM(i.valor) as total_faturamento
            FROM ingressos i
            JOIN sessoes s ON i.sessao_id = s.id
            JOIN eventos e ON s.evento_id = e.id
            GROUP BY e.id, e.nome
            ORDER BY total_faturamento DESC
            LIMIT 1
        """;
        
        // Peça menos lucrativa
        String sqlMenosLucrativa = """
            SELECT e.nome, SUM(i.valor) as total_faturamento
            FROM ingressos i
            JOIN sessoes s ON i.sessao_id = s.id
            JOIN eventos e ON s.evento_id = e.id
            GROUP BY e.id, e.nome
            ORDER BY total_faturamento ASC
            LIMIT 1
        """;
        
        // Lucro médio por peça
        String sqlLucroMedio = """
            SELECT e.nome, AVG(i.valor) as media_faturamento
            FROM ingressos i
            JOIN sessoes s ON i.sessao_id = s.id
            JOIN eventos e ON s.evento_id = e.id
            GROUP BY e.id, e.nome
        """;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Busca peça mais vendida
            try (PreparedStatement stmt = conn.prepareStatement(sqlMaisVendidos);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estatisticas.put("pecaMaisVendida", Map.of(
                        "nome", rs.getString("nome"),
                        "totalVendas", rs.getInt("total_vendas")
                    ));
                }
            }
            
            // Busca peça menos vendida
            try (PreparedStatement stmt = conn.prepareStatement(sqlMenosVendidos);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estatisticas.put("pecaMenosVendida", Map.of(
                        "nome", rs.getString("nome"),
                        "totalVendas", rs.getInt("total_vendas")
                    ));
                }
            }
            
            // Busca sessão com maior ocupação
            try (PreparedStatement stmt = conn.prepareStatement(sqlMaiorOcupacao);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estatisticas.put("sessaoMaiorOcupacao", Map.of(
                        "nome", rs.getString("nome"),
                        "horario", rs.getString("horario"),
                        "data", rs.getDate("data_sessao").toString(),
                        "ocupacao", String.format("%.2f%%", rs.getDouble("ocupacao"))
                    ));
                }
            }
            
            // Busca sessão com menor ocupação
            try (PreparedStatement stmt = conn.prepareStatement(sqlMenorOcupacao);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estatisticas.put("sessaoMenorOcupacao", Map.of(
                        "nome", rs.getString("nome"),
                        "horario", rs.getString("horario"),
                        "data", rs.getDate("data_sessao").toString(),
                        "ocupacao", String.format("%.2f%%", rs.getDouble("ocupacao"))
                    ));
                }
            }
            
            // Busca peça mais lucrativa
            try (PreparedStatement stmt = conn.prepareStatement(sqlMaisLucrativa);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estatisticas.put("pecaMaisLucrativa", Map.of(
                        "nome", rs.getString("nome"),
                        "faturamento", String.format("R$ %.2f", rs.getDouble("total_faturamento"))
                    ));
                }
            }
            
            // Busca peça menos lucrativa
            try (PreparedStatement stmt = conn.prepareStatement(sqlMenosLucrativa);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estatisticas.put("pecaMenosLucrativa", Map.of(
                        "nome", rs.getString("nome"),
                        "faturamento", String.format("R$ %.2f", rs.getDouble("total_faturamento"))
                    ));
                }
            }
            
            // Busca lucro médio por peça
            List<Map<String, Object>> lucroMedioPorPeca = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sqlLucroMedio);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lucroMedioPorPeca.add(Map.of(
                        "nome", rs.getString("nome"),
                        "mediaFaturamento", String.format("R$ %.2f", rs.getDouble("media_faturamento"))
                    ));
                }
                estatisticas.put("lucroMedioPorPeca", lucroMedioPorPeca);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return estatisticas;
    }
} 