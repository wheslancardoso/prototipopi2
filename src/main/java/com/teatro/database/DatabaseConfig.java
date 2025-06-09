package com.teatro.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe responsável por gerenciar as configurações do banco de dados.
 * Implementa o padrão Singleton para garantir uma única instância das configurações.
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "database.properties";
    private static DatabaseConfig instance;
    private final Properties properties;
    
    private DatabaseConfig() {
        properties = new Properties();
        carregarConfiguracoes();
    }
    
    /**
     * Obtém a instância única da classe DatabaseConfig.
     * @return A instância de DatabaseConfig
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    /**
     * Carrega as configurações do arquivo properties.
     */
    private void carregarConfiguracoes() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Não foi possível encontrar o arquivo " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar configurações do banco de dados", e);
        }
    }
    
    /**
     * Obtém a URL de conexão do banco de dados.
     * @return A URL de conexão
     */
    public String getUrl() {
        return properties.getProperty("db.url", "jdbc:mysql://localhost:3306/teatro_db");
    }
    
    /**
     * Obtém o usuário do banco de dados.
     * @return O nome do usuário
     */
    public String getUsuario() {
        return properties.getProperty("db.usuario", "root");
    }
    
    /**
     * Obtém a senha do banco de dados.
     * @return A senha
     */
    public String getSenha() {
        return properties.getProperty("db.senha", "root");
    }
    
    /**
     * Obtém o driver JDBC.
     * @return O nome da classe do driver
     */
    public String getDriver() {
        return properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }
    
    /**
     * Obtém o tamanho máximo do pool de conexões.
     * @return O tamanho máximo do pool
     */
    public int getMaxPoolSize() {
        return Integer.parseInt(properties.getProperty("db.pool.maxSize", "10"));
    }
    
    /**
     * Obtém o tempo máximo de espera por uma conexão (em milissegundos).
     * @return O tempo máximo de espera
     */
    public int getMaxWait() {
        return Integer.parseInt(properties.getProperty("db.pool.maxWait", "5000"));
    }
    
    public int getMinIdle() {
        return Integer.parseInt(properties.getProperty("db.pool.minIdle", "5"));
    }
    
    public int getIdleTimeout() {
        return Integer.parseInt(properties.getProperty("db.pool.idleTimeout", "300000"));
    }
    
    public boolean getCachePrepStmts() {
        return Boolean.parseBoolean(properties.getProperty("db.pool.cachePrepStmts", "true"));
    }
    
    public int getPrepStmtCacheSize() {
        return Integer.parseInt(properties.getProperty("db.pool.prepStmtCacheSize", "250"));
    }
    
    public int getPrepStmtCacheSqlLimit() {
        return Integer.parseInt(properties.getProperty("db.pool.prepStmtCacheSqlLimit", "2048"));
    }
    
    public boolean getUseServerPrepStmts() {
        return Boolean.parseBoolean(properties.getProperty("db.pool.useServerPrepStmts", "true"));
    }
} 