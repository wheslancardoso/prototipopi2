package com.teatro.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária para executar scripts SQL de atualização no banco de dados.
 */
public class DatabaseUpdater {

    /**
     * Executa um script SQL lido de um arquivo
     *
     * @param scriptPath Caminho para o arquivo contendo o script SQL
     * @return true se executado com sucesso, false caso contrário
     */
    public static boolean executeScriptFromFile(String scriptPath) {
        try {
            String script = new String(Files.readAllBytes(Paths.get(scriptPath)));
            return executeScript(script);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de script: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Executa um script SQL informado como string
     *
     * @param script String contendo o script SQL a ser executado
     * @return true se executado com sucesso, false caso contrário
     */
    public static boolean executeScript(String script) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            
            // Divide o script por ";" para executar cada comando separadamente
            String[] commands = script.split(";");
            
            for (String command : commands) {
                String trimmedCommand = command.trim();
                if (!trimmedCommand.isEmpty()) {
                    System.out.println("Executando: " + trimmedCommand);
                    stmt.execute(trimmedCommand);
                }
            }
            
            conn.commit();
            System.out.println("Script SQL executado com sucesso!");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Erro ao executar script SQL: " + e.getMessage());
            e.printStackTrace();
            
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            return false;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Verifica se uma coluna existe em uma tabela
     * 
     * @param tableName Nome da tabela
     * @param columnName Nome da coluna
     * @return true se a coluna existir, false caso contrário
     */
    public static boolean columnExists(String tableName, String columnName) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Tenta executar uma query que seleciona a coluna
            // Se não lançar exceção, a coluna existe
            stmt.execute("SELECT " + columnName + " FROM " + tableName + " LIMIT 1");
            return true;
            
        } catch (SQLException e) {
            // Se lançar exceção, a coluna não existe
            return false;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
