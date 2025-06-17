package com.teatro.util;

import com.teatro.database.DatabaseConnection;
import com.teatro.util.TeatroLogger;
import java.sql.*;

/**
 * Utilitário para migrar senhas existentes em texto plano para formato hasheado.
 * Esta classe deve ser executada uma única vez para atualizar senhas antigas.
 */
public class PasswordMigration {
    
    private static final TeatroLogger logger = TeatroLogger.getInstance();
    
    /**
     * Migra todas as senhas em texto plano para formato hasheado.
     * @return Número de senhas migradas
     */
    public static int migrarSenhas() {
        int senhasMigradas = 0;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Buscar todos os usuários
            String selectSql = "SELECT id, senha FROM usuarios";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 ResultSet rs = selectStmt.executeQuery()) {
                
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String senhaAtual = rs.getString("senha");
                    
                    // Verificar se a senha já está hasheada
                    if (!PasswordHasher.isHashed(senhaAtual)) {
                        // Hash da senha em texto plano
                        String senhaHasheada = PasswordHasher.hashPassword(senhaAtual);
                        
                        // Atualizar no banco
                        String updateSql = "UPDATE usuarios SET senha = ? WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, senhaHasheada);
                            updateStmt.setLong(2, id);
                            updateStmt.executeUpdate();
                            senhasMigradas++;
                            
                            logger.info("Senha migrada para usuário ID: " + id);
                        }
                    }
                }
            }
            
            logger.info("Migração concluída. " + senhasMigradas + " senhas foram migradas.");
            return senhasMigradas;
            
        } catch (SQLException e) {
            logger.error("Erro durante a migração de senhas: " + e.getMessage());
            throw new RuntimeException("Erro durante a migração de senhas", e);
        }
    }
    
    /**
     * Verifica quantas senhas ainda estão em texto plano.
     * @return Número de senhas em texto plano
     */
    public static int contarSenhasEmTextoPlano() {
        int senhasEmTextoPlano = 0;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT COUNT(*) FROM usuarios WHERE senha NOT LIKE '%:%'";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    senhasEmTextoPlano = rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Erro ao contar senhas em texto plano: " + e.getMessage());
            throw new RuntimeException("Erro ao contar senhas em texto plano", e);
        }
        
        return senhasEmTextoPlano;
    }
    
    /**
     * Executa a migração se necessário.
     * @return true se a migração foi executada, false se não era necessária
     */
    public static boolean executarMigracaoSeNecessario() {
        int senhasEmTextoPlano = contarSenhasEmTextoPlano();
        
        if (senhasEmTextoPlano > 0) {
            logger.info("Encontradas " + senhasEmTextoPlano + " senhas em texto plano. Iniciando migração...");
            migrarSenhas();
            return true;
        } else {
            logger.info("Todas as senhas já estão no formato hasheado.");
            return false;
        }
    }
} 