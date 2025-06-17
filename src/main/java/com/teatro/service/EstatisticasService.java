package com.teatro.service;

import com.teatro.database.DatabaseConnection;
import com.teatro.util.TeatroLogger;
import java.sql.*;
import java.util.*;

/**
 * Serviço para buscar estatísticas do sistema usando as views SQL.
 */
public class EstatisticasService {
    private static EstatisticasService instance;
    private final TeatroLogger logger = TeatroLogger.getInstance();
    private final DatabaseConnection dbConnection;

    private EstatisticasService() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public static synchronized EstatisticasService getInstance() {
        if (instance == null) {
            instance = new EstatisticasService();
        }
        return instance;
    }

    /**
     * Busca todas as estatísticas do sistema.
     * @return Map com todas as estatísticas
     */
    public Map<String, Object> buscarEstatisticas() {
        Map<String, Object> estatisticas = new HashMap<>();
        
        try {
            estatisticas.put("pecaMaisVendida", buscarPecaMaisVendida());
            estatisticas.put("pecaMenosVendida", buscarPecaMenosVendida());
            estatisticas.put("sessaoMaiorOcupacao", buscarSessaoMaiorOcupacao());
            estatisticas.put("sessaoMenorOcupacao", buscarSessaoMenorOcupacao());
            estatisticas.put("pecaMaisLucrativa", buscarPecaMaisLucrativa());
            estatisticas.put("pecaMenosLucrativa", buscarPecaMenosLucrativa());
            estatisticas.put("lucroMedioPorPeca", buscarLucroMedioPorPeca());
            
        } catch (Exception e) {
            logger.error("Erro ao buscar estatísticas: " + e.getMessage());
            // Retorna estatísticas vazias em caso de erro
            return criarEstatisticasVazias();
        }
        
        return estatisticas;
    }

    /**
     * Busca a peça mais vendida.
     */
    private Map<String, Object> buscarPecaMaisVendida() {
        String sql = """
            SELECT nome_peca, total_ingressos_vendidos 
            FROM estatisticas_vendas_peca 
            WHERE total_ingressos_vendidos > 0 
            ORDER BY total_ingressos_vendidos DESC 
            LIMIT 1
            """;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return Map.of(
                    "nome", rs.getString("nome_peca"),
                    "totalVendas", rs.getInt("total_ingressos_vendidos")
                );
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar peça mais vendida: " + e.getMessage());
        }
        
        return Map.of("nome", "N/A", "totalVendas", 0);
    }

    /**
     * Busca a peça menos vendida.
     */
    private Map<String, Object> buscarPecaMenosVendida() {
        String sql = """
            SELECT nome_peca, total_ingressos_vendidos 
            FROM estatisticas_vendas_peca 
            WHERE total_ingressos_vendidos > 0 
            ORDER BY total_ingressos_vendidos ASC 
            LIMIT 1
            """;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return Map.of(
                    "nome", rs.getString("nome_peca"),
                    "totalVendas", rs.getInt("total_ingressos_vendidos")
                );
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar peça menos vendida: " + e.getMessage());
        }
        
        return Map.of("nome", "N/A", "totalVendas", 0);
    }

    /**
     * Busca a sessão com maior ocupação.
     */
    private Map<String, Object> buscarSessaoMaiorOcupacao() {
        String sql = """
            SELECT nome_peca, data_sessao, horario, percentual_ocupacao 
            FROM estatisticas_ocupacao_sessao 
            WHERE percentual_ocupacao > 0 
            ORDER BY percentual_ocupacao DESC 
            LIMIT 1
            """;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return Map.of(
                    "nome", rs.getString("nome_peca"),
                    "data", rs.getDate("data_sessao").toString(),
                    "horario", rs.getString("horario"),
                    "ocupacao", String.format("%.1f%%", rs.getDouble("percentual_ocupacao"))
                );
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar sessão com maior ocupação: " + e.getMessage());
        }
        
        return Map.of("nome", "N/A", "data", "N/A", "horario", "N/A", "ocupacao", "0%");
    }

    /**
     * Busca a sessão com menor ocupação.
     */
    private Map<String, Object> buscarSessaoMenorOcupacao() {
        String sql = """
            SELECT nome_peca, data_sessao, horario, percentual_ocupacao 
            FROM estatisticas_ocupacao_sessao 
            WHERE percentual_ocupacao > 0 
            ORDER BY percentual_ocupacao ASC 
            LIMIT 1
            """;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return Map.of(
                    "nome", rs.getString("nome_peca"),
                    "data", rs.getDate("data_sessao").toString(),
                    "horario", rs.getString("horario"),
                    "ocupacao", String.format("%.1f%%", rs.getDouble("percentual_ocupacao"))
                );
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar sessão com menor ocupação: " + e.getMessage());
        }
        
        return Map.of("nome", "N/A", "data", "N/A", "horario", "N/A", "ocupacao", "0%");
    }

    /**
     * Busca a peça mais lucrativa.
     */
    private Map<String, Object> buscarPecaMaisLucrativa() {
        String sql = """
            SELECT nome_peca, faturamento_total 
            FROM estatisticas_vendas_peca 
            WHERE faturamento_total > 0 
            ORDER BY faturamento_total DESC 
            LIMIT 1
            """;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return Map.of(
                    "nome", rs.getString("nome_peca"),
                    "faturamento", String.format("R$ %.2f", rs.getDouble("faturamento_total"))
                );
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar peça mais lucrativa: " + e.getMessage());
        }
        
        return Map.of("nome", "N/A", "faturamento", "R$ 0,00");
    }

    /**
     * Busca a peça menos lucrativa.
     */
    private Map<String, Object> buscarPecaMenosLucrativa() {
        String sql = """
            SELECT nome_peca, faturamento_total 
            FROM estatisticas_vendas_peca 
            WHERE faturamento_total > 0 
            ORDER BY faturamento_total ASC 
            LIMIT 1
            """;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return Map.of(
                    "nome", rs.getString("nome_peca"),
                    "faturamento", String.format("R$ %.2f", rs.getDouble("faturamento_total"))
                );
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar peça menos lucrativa: " + e.getMessage());
        }
        
        return Map.of("nome", "N/A", "faturamento", "R$ 0,00");
    }

    /**
     * Busca o lucro médio por peça.
     */
    private List<Map<String, Object>> buscarLucroMedioPorPeca() {
        String sql = """
            SELECT nome_peca, valor_medio_ingresso 
            FROM estatisticas_vendas_peca 
            WHERE valor_medio_ingresso > 0 
            ORDER BY valor_medio_ingresso DESC
            """;
        
        List<Map<String, Object>> resultados = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                resultados.add(Map.of(
                    "nome", rs.getString("nome_peca"),
                    "mediaFaturamento", String.format("R$ %.2f", rs.getDouble("valor_medio_ingresso"))
                ));
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar lucro médio por peça: " + e.getMessage());
        }
        
        return resultados;
    }

    /**
     * Cria estatísticas vazias para casos de erro.
     */
    private Map<String, Object> criarEstatisticasVazias() {
        return Map.of(
            "pecaMaisVendida", Map.of("nome", "N/A", "totalVendas", 0),
            "pecaMenosVendida", Map.of("nome", "N/A", "totalVendas", 0),
            "sessaoMaiorOcupacao", Map.of("nome", "N/A", "data", "N/A", "horario", "N/A", "ocupacao", "0%"),
            "sessaoMenorOcupacao", Map.of("nome", "N/A", "data", "N/A", "horario", "N/A", "ocupacao", "0%"),
            "pecaMaisLucrativa", Map.of("nome", "N/A", "faturamento", "R$ 0,00"),
            "pecaMenosLucrativa", Map.of("nome", "N/A", "faturamento", "R$ 0,00"),
            "lucroMedioPorPeca", List.of()
        );
    }
} 