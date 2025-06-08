package com.teatro.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária para logging.
 * Implementa o padrão Singleton para garantir uma única instância do logger.
 */
public class TeatroLogger {
    private static TeatroLogger instance;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private TeatroLogger() {}
    
    /**
     * Obtém a instância única da classe TeatroLogger.
     * @return A instância de TeatroLogger
     */
    public static synchronized TeatroLogger getInstance() {
        if (instance == null) {
            instance = new TeatroLogger();
        }
        return instance;
    }
    
    private String formatMessage(String level, String message) {
        return String.format("[%s] [%s] %s", 
            LocalDateTime.now().format(formatter),
            level,
            message);
    }
    
    /**
     * Registra uma mensagem de debug.
     * @param message A mensagem a ser registrada
     */
    public void debug(String message) {
        System.out.println(formatMessage("DEBUG", message));
    }
    
    /**
     * Registra uma mensagem de debug com parâmetros.
     * @param format O formato da mensagem
     * @param args Os argumentos da mensagem
     */
    public void debug(String format, Object... args) {
        debug(String.format(format, args));
    }
    
    /**
     * Registra uma mensagem de informação.
     * @param message A mensagem a ser registrada
     */
    public void info(String message) {
        System.out.println(formatMessage("INFO", message));
    }
    
    /**
     * Registra uma mensagem de informação com parâmetros.
     * @param format O formato da mensagem
     * @param args Os argumentos da mensagem
     */
    public void info(String format, Object... args) {
        info(String.format(format, args));
    }
    
    /**
     * Registra uma mensagem de aviso.
     * @param message A mensagem a ser registrada
     */
    public void warn(String message) {
        System.err.println(formatMessage("WARN", message));
    }
    
    /**
     * Registra uma mensagem de aviso com parâmetros.
     * @param format O formato da mensagem
     * @param args Os argumentos da mensagem
     */
    public void warn(String format, Object... args) {
        warn(String.format(format, args));
    }
    
    /**
     * Registra uma mensagem de erro.
     * @param message A mensagem a ser registrada
     */
    public void error(String message) {
        System.err.println(formatMessage("ERROR", message));
    }
    
    /**
     * Registra uma mensagem de erro com parâmetros.
     * @param format O formato da mensagem
     * @param args Os argumentos da mensagem
     */
    public void error(String format, Object... args) {
        error(String.format(format, args));
    }
    
    /**
     * Registra uma mensagem de erro com uma exceção.
     * @param message A mensagem a ser registrada
     * @param throwable A exceção a ser registrada
     */
    public void error(String message, Throwable throwable) {
        error(message + "\n" + throwable.toString());
        for (StackTraceElement element : throwable.getStackTrace()) {
            System.err.println("\tat " + element);
        }
    }
} 