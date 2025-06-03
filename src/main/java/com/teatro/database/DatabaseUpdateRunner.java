package com.teatro.database;

/**
 * Classe principal para execução do script de atualização do banco de dados.
 * Esta classe pode ser chamada diretamente ou a partir da classe Main.
 */
public class DatabaseUpdateRunner {
    
    /**
     * Método principal para executar os scripts de atualização do banco de dados.
     * 
     * @param args Não utilizado
     */
    public static void main(String[] args) {
        updateDatabase();
    }
    
    /**
     * Executa as atualizações necessárias no banco de dados.
     * 
     * @return true se atualização foi bem-sucedida, false caso contrário
     */
    public static boolean updateDatabase() {
        System.out.println("Verificando necessidade de atualização do banco de dados...");
        
        // Verifica se a coluna horario_especifico_id já existe na tabela ingressos
        boolean colunaExiste = DatabaseUpdater.columnExists("ingressos", "horario_especifico_id");
        
        if (!colunaExiste) {
            System.out.println("A coluna 'horario_especifico_id' não existe. Aplicando atualização...");
            
            // Caminho para o script de atualização
            String scriptPath = "src/main/resources/update_ingressos_table.sql";
            
            // Executa o script de atualização
            boolean resultado = DatabaseUpdater.executeScriptFromFile(scriptPath);
            
            if (resultado) {
                System.out.println("Atualização concluída com sucesso!");
            } else {
                System.err.println("Falha ao atualizar o banco de dados!");
            }
            
            return resultado;
        } else {
            System.out.println("Banco de dados já está atualizado. Nenhuma ação necessária.");
            return true;
        }
    }
}
