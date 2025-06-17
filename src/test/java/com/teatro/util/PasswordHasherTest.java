package com.teatro.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe PasswordHasher.
 */
public class PasswordHasherTest {
    
    private String senhaTeste;
    
    @BeforeEach
    void setUp() {
        senhaTeste = "minhaSenha123";
    }
    
    @Test
    void testHashPassword() {
        // Testa se o hash é gerado corretamente
        String hash = PasswordHasher.hashPassword(senhaTeste);
        
        assertNotNull(hash);
        assertTrue(hash.contains(":"));
        assertNotEquals(senhaTeste, hash);
        
        // Verifica se o formato está correto (salt:hash)
        String[] parts = hash.split(":", 2);
        assertEquals(2, parts.length);
        assertNotNull(parts[0]); // salt
        assertNotNull(parts[1]); // hash
    }
    
    @Test
    void testVerifyPassword() {
        // Testa verificação de senha hasheada
        String hash = PasswordHasher.hashPassword(senhaTeste);
        
        assertTrue(PasswordHasher.verifyPassword(senhaTeste, hash));
        assertFalse(PasswordHasher.verifyPassword("senhaErrada", hash));
        assertFalse(PasswordHasher.verifyPassword("", hash));
    }
    
    @Test
    void testVerifyPasswordWithPlainText() {
        // Testa compatibilidade com senhas em texto plano (para migração)
        String senhaPlano = "senha123";
        
        assertTrue(PasswordHasher.verifyPassword(senhaPlano, senhaPlano));
        assertFalse(PasswordHasher.verifyPassword("senhaErrada", senhaPlano));
    }
    
    @Test
    void testIsHashed() {
        // Testa identificação de senhas hasheadas
        String hash = PasswordHasher.hashPassword(senhaTeste);
        
        assertTrue(PasswordHasher.isHashed(hash));
        assertFalse(PasswordHasher.isHashed(senhaTeste));
        assertFalse(PasswordHasher.isHashed(""));
        assertFalse(PasswordHasher.isHashed(null));
    }
    
    @Test
    void testHashPasswordWithSalt() {
        // Testa hash com salt específico
        String salt = PasswordHasher.generateSalt();
        String hash1 = PasswordHasher.hashPassword(senhaTeste, salt);
        String hash2 = PasswordHasher.hashPassword(senhaTeste, salt);
        
        assertEquals(hash1, hash2); // Mesmo salt deve gerar mesmo hash
        
        String salt2 = PasswordHasher.generateSalt();
        String hash3 = PasswordHasher.hashPassword(senhaTeste, salt2);
        
        assertNotEquals(hash1, hash3); // Salts diferentes devem gerar hashes diferentes
    }
    
    @Test
    void testGenerateSalt() {
        // Testa geração de salt
        String salt1 = PasswordHasher.generateSalt();
        String salt2 = PasswordHasher.generateSalt();
        
        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2); // Salts devem ser únicos
    }
    
    @Test
    void testMultipleHashes() {
        // Testa que hashes múltiplos da mesma senha são diferentes (devido ao salt)
        String hash1 = PasswordHasher.hashPassword(senhaTeste);
        String hash2 = PasswordHasher.hashPassword(senhaTeste);
        
        assertNotEquals(hash1, hash2); // Devido ao salt aleatório
        
        // Mas ambos devem verificar corretamente
        assertTrue(PasswordHasher.verifyPassword(senhaTeste, hash1));
        assertTrue(PasswordHasher.verifyPassword(senhaTeste, hash2));
    }
} 