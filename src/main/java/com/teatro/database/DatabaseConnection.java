package com.teatro.database;

import com.teatro.util.TeatroLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados.
 * Implementa o padrão Singleton para garantir uma única instância de conexão.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;
    private final TeatroLogger logger = TeatroLogger.getInstance();
    
    private DatabaseConnection() {
        DatabaseConfig config = DatabaseConfig.getInstance();
        this.url = config.getUrl();
        this.user = config.getUsuario();
        this.password = config.getSenha();
        
        try {
            Class.forName(config.getDriver());
            logger.info("Driver JDBC carregado com sucesso");
        } catch (ClassNotFoundException e) {
            logger.error("Erro ao carregar driver JDBC: {}", e.getMessage());
            throw new RuntimeException("Driver JDBC não encontrado", e);
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Conexão com o banco de dados fechada");
            }
        } catch (SQLException e) {
            logger.error("Erro ao fechar conexão: {}", e.getMessage());
        }
    }
} 