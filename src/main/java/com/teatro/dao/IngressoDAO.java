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
        String sql = "INSERT INTO ingressos (usuario_id, sessao_id, area_id, numero_poltrona, valor, horario_especifico_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, ingresso.getUsuarioId());
            stmt.setLong(2, ingresso.getSessaoId());
            stmt.setLong(3, ingresso.getAreaId());
            stmt.setInt(4, ingresso.getNumeroPoltrona());
            stmt.setDouble(5, ingresso.getValor());
            stmt.setLong(6, ingresso.getHorarioEspecificoId() != null ? ingresso.getHorarioEspecificoId() : 0);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Salva um ingresso no banco de dados e retorna o ID gerado
     * @param ingresso O ingresso a ser salvo
     * @return O ID do ingresso salvo ou null em caso de erro
     */
    public Long salvarERetornarId(Ingresso ingresso) {
        String sql = "INSERT INTO ingressos (usuario_id, sessao_id, area_id, numero_poltrona, valor, horario_especifico_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, ingresso.getUsuarioId());
            stmt.setLong(2, ingresso.getSessaoId());
            stmt.setLong(3, ingresso.getAreaId());
            stmt.setInt(4, ingresso.getNumeroPoltrona());
            stmt.setDouble(5, ingresso.getValor());
            stmt.setLong(6, ingresso.getHorarioEspecificoId() != null ? ingresso.getHorarioEspecificoId() : 0);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar o ingresso, nenhuma linha afetada.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Falha ao obter o ID do ingresso, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar ingresso: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Integer> buscarPoltronasOcupadas(Long sessaoId, Long areaId, Long horarioEspecificoId) {
        String sql = "SELECT i.numero_poltrona FROM ingressos i " +
                   "JOIN sessoes s ON i.sessao_id = s.id " +
                   "WHERE i.sessao_id = ? AND i.area_id = ? AND i.horario_especifico_id = ?";
        
        List<Integer> poltronasOcupadas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, sessaoId);
            stmt.setLong(2, areaId);
            stmt.setLong(3, horarioEspecificoId != null ? horarioEspecificoId : 0);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int numeroPoltrona = rs.getInt("numero_poltrona");
                if (numeroPoltrona > 0) { // Garante que apenas números de poltrona válidos são adicionados
                    poltronasOcupadas.add(numeroPoltrona);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar poltronas ocupadas para sessão: " + sessaoId + ", área: " + areaId + ", horário: " + horarioEspecificoId);
            e.printStackTrace();
        }
        
        return poltronasOcupadas;
    }
    
    /**
     * Busca poltronas ocupadas para uma combinação específica de sessão, área, horário e data
     * @param sessaoId ID da sessão
     * @param areaId ID da área
     * @param horarioEspecificoId ID do horário específico
     * @param dataSessao Data da sessão no formato YYYY-MM-DD
     * @return Lista de números de poltronas ocupadas
     */
    public List<Integer> buscarPoltronasOcupadas(Long sessaoId, Long areaId, Long horarioEspecificoId, String dataSessao) {
        String sql = "SELECT i.numero_poltrona FROM ingressos i " +
                   "JOIN sessoes s ON i.sessao_id = s.id " +
                   "WHERE i.sessao_id = ? AND i.area_id = ? AND i.horario_especifico_id = ? AND s.data_sessao = ?";
        
        List<Integer> poltronasOcupadas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, sessaoId);
            stmt.setLong(2, areaId);
            stmt.setLong(3, horarioEspecificoId != null ? horarioEspecificoId : 0);
            stmt.setDate(4, java.sql.Date.valueOf(dataSessao));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int numeroPoltrona = rs.getInt("numero_poltrona");
                if (numeroPoltrona > 0) { // Garante que apenas números de poltrona válidos são adicionados
                    poltronasOcupadas.add(numeroPoltrona);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar poltronas ocupadas para sessão: " + sessaoId + ", área: " + areaId + 
                           ", horário: " + horarioEspecificoId + ", data: " + dataSessao);
            e.printStackTrace();
        }
        
        return poltronasOcupadas;
    }
    
    /**
     * Versão sobrecarregada para compatibilidade com código existente
     */
    public List<Integer> buscarPoltronasOcupadas(Long sessaoId, Long areaId) {
        return buscarPoltronasOcupadas(sessaoId, areaId, null);
    }
    
    public List<Ingresso> buscarPorUsuarioId(Long usuarioId) {
        String sql = """
            SELECT i.*, e.nome as evento_nome, s.horario, a.nome as area_nome 
            FROM ingressos i
            JOIN sessoes s ON i.sessao_id = s.id
            JOIN eventos e ON s.evento_id = e.id
            JOIN areas a ON i.area_id = a.id
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
    
    public boolean atualizarFaturamento(Long sessaoId, Long areaId, double valor) {
        String sql = "UPDATE sessoes_areas SET faturamento = faturamento + ? WHERE sessao_id = ? AND area_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, valor);
            stmt.setLong(2, sessaoId);
            stmt.setLong(3, areaId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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