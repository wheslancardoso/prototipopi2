package com.teatro.util;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Gerenciador de transições de tela para evitar problemas de recursos do JavaFX
 */
public class ViewManager {
    
    /**
     * Limpa recursos do JavaFX de forma segura
     */
    public static void limparRecursosJavaFX(Stage stage) {
        try {
            TeatroLogger.logLimpezaRecursos("Iniciando limpeza de recursos");
            
            // Limpa a cena atual se existir
            if (stage.getScene() != null) {
                Scene sceneAtual = stage.getScene();
                sceneAtual.setRoot(null);
                TeatroLogger.logLimpezaRecursos("Cena anterior limpa");
            }
            
            // Força a coleta de lixo
            System.gc();
            
            // Aguarda um pouco para permitir que a coleta seja executada
            Thread.sleep(50);
            
            TeatroLogger.logLimpezaRecursos("Limpeza concluída com sucesso");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            TeatroLogger.getInstance().error("Interrupção durante limpeza de recursos", e);
        } catch (Exception e) {
            TeatroLogger.getInstance().error("Erro durante limpeza de recursos", e);
        }
    }
    
    /**
     * Prepara o stage para uma nova cena
     */
    public static void prepararStageParaNovaCena(Stage stage) {
        limparRecursosJavaFX(stage);
        
        // Reseta propriedades do stage se necessário
        stage.setResizable(true);
        stage.setMaximized(false);
    }
} 