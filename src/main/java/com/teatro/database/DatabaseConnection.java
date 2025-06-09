package com.teatro.database;

import com.teatro.util.TeatroLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados.
 * Implementa o padrão Singleton para garantir uma única instância de conexão.
 * Implementa um pool de conexões simples usando BlockingQueue.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final String url;
    private final String user;
    private final String password;
    private final BlockingQueue<Connection> connectionPool;
    private final int poolSize;
    private final TeatroLogger logger = TeatroLogger.getInstance();
    
    private DatabaseConnection() {
        DatabaseConfig config = DatabaseConfig.getInstance();
        this.url = config.getUrl();
        this.user = config.getUsuario();
        this.password = config.getSenha();
        this.poolSize = config.getMaxPoolSize();
        this.connectionPool = new ArrayBlockingQueue<>(poolSize);
        
        try {
            Class.forName(config.getDriver());
            logger.info("Driver JDBC carregado com sucesso");
            
            // Inicializa o pool com conexões
            for (int i = 0; i < poolSize; i++) {
                Connection conn = DriverManager.getConnection(url, user, password);
                connectionPool.offer(conn);
            }
            logger.info("Pool de conexões inicializado com {} conexões", poolSize);
            
            // Criar views de estatísticas ao inicializar
            criarViewsEstatisticas();
        } catch (ClassNotFoundException e) {
            logger.error("Erro ao carregar driver JDBC: {}", e.getMessage());
            throw new RuntimeException("Driver JDBC não encontrado", e);
        } catch (SQLException e) {
            logger.error("Erro ao inicializar pool de conexões: {}", e.getMessage());
            throw new RuntimeException("Erro ao inicializar pool de conexões", e);
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = connectionPool.poll();
            if (conn == null || conn.isClosed()) {
                // Se não houver conexão disponível ou estiver fechada, cria uma nova
                conn = DriverManager.getConnection(url, user, password);
            }
            return conn;
        } catch (SQLException e) {
            logger.error("Erro ao obter conexão do pool: {}", e.getMessage());
            throw e;
        }
    }
    
    public void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    connectionPool.offer(conn);
                }
            } catch (SQLException e) {
                logger.error("Erro ao liberar conexão: {}", e.getMessage());
            }
        }
    }
    
    public void closeConnection() {
        Connection conn;
        while ((conn = connectionPool.poll()) != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Erro ao fechar conexão: {}", e.getMessage());
            }
        }
        logger.info("Pool de conexões fechado");
    }
    
    /**
     * Cria ou atualiza as views de estatísticas no banco de dados.
     */
    private void criarViewsEstatisticas() {
        Connection conn = null;
        try {
            conn = getConnection();
            ViewsEstatisticas views = new ViewsEstatisticas(conn);
            views.criarViews();
            logger.info("Views de estatísticas criadas/atualizadas com sucesso");
        } catch (SQLException e) {
            logger.error("Erro ao criar views de estatísticas: {}", e.getMessage());
            throw new RuntimeException("Erro ao criar views de estatísticas", e);
        } finally {
            if (conn != null) {
                releaseConnection(conn);
            }
        }
    }
} 