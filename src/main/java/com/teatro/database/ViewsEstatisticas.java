package com.teatro.database;

import com.teatro.util.TeatroLogger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Classe responsável por criar e gerenciar as views de estatísticas no banco de dados.
 */
public class ViewsEstatisticas {
    private static final TeatroLogger logger = TeatroLogger.getInstance();
    private final Connection connection;

    public ViewsEstatisticas(Connection connection) {
        this.connection = connection;
    }

    /**
     * Cria ou atualiza todas as views de estatísticas.
     */
    public void criarViews() {
        try {
            String sql = carregarScriptSQL();
            try (Statement stmt = connection.createStatement()) {
                // Divide o script em comandos individuais
                String[] comandos = sql.split(";");
                for (String comando : comandos) {
                    if (!comando.trim().isEmpty()) {
                        stmt.execute(comando);
                    }
                }
            }
            logger.info("Views de estatísticas criadas/atualizadas com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao criar views de estatísticas: " + e.getMessage());
            throw new RuntimeException("Erro ao criar views de estatísticas", e);
        }
    }

    /**
     * Carrega o script SQL do arquivo de recursos.
     */
    private String carregarScriptSQL() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    getClass().getResourceAsStream("/db/views_estatisticas.sql")))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            logger.error("Erro ao carregar script SQL: " + e.getMessage());
            throw new RuntimeException("Erro ao carregar script SQL", e);
        }
    }
} 