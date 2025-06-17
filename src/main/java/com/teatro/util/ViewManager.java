package com.teatro.util;

import javafx.stage.Stage;

/**
 * Gerenciador de transições de tela para evitar problemas de recursos do JavaFX
 * 
 * ATENÇÃO: Esta classe está temporariamente desabilitada para resolver problemas de estabilidade.
 * Para reativar, descomente as linhas nas views que chamam ViewManager.prepararStageParaNovaCena(stage)
 */
public class ViewManager {
    
    /**
     * Limpa recursos do JavaFX de forma segura
     * ATENÇÃO: Método temporariamente simplificado para evitar problemas
     */
    public static void limparRecursosJavaFX(Stage stage) {
        try {
            TeatroLogger.logLimpezaRecursos("Iniciando limpeza de recursos (modo seguro)");
            
            // Verifica se o stage existe
            if (stage == null) {
                TeatroLogger.logLimpezaRecursos("Stage é null, pulando limpeza");
                return;
            }
            
            // Apenas força a coleta de lixo de forma suave
            System.gc();
            
            TeatroLogger.logLimpezaRecursos("Limpeza concluída com sucesso (modo seguro)");
            
        } catch (Exception e) {
            TeatroLogger.getInstance().error("Erro durante limpeza de recursos", e);
        }
    }
    
    /**
     * Prepara o stage para uma nova cena
     * ATENÇÃO: Método temporariamente simplificado para evitar problemas
     */
    public static void prepararStageParaNovaCena(Stage stage) {
        if (stage == null) {
            TeatroLogger.getInstance().warn("Stage é null, pulando preparação");
            return;
        }
        
        // Temporariamente desabilitado para resolver problemas de estabilidade
        // limparRecursosJavaFX(stage);
        
        // Reseta propriedades do stage se necessário
        try {
            stage.setResizable(true);
            stage.setMaximized(false);
        } catch (Exception e) {
            TeatroLogger.getInstance().warn("Erro ao resetar propriedades do stage: " + e.getMessage());
        }
    }
} 