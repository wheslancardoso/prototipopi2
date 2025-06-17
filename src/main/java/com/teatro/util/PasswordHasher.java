package com.teatro.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitário para hashing seguro de senhas usando SHA-256 com salt.
 * Implementa o padrão de segurança recomendado para armazenamento de senhas.
 */
public class PasswordHasher {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR = ":";
    
    /**
     * Gera um salt aleatório.
     * @return Salt em formato Base64
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Faz o hash de uma senha com salt.
     * @param password Senha em texto plano
     * @param salt Salt para a senha
     * @return Hash da senha em formato Base64
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo de hash não disponível: " + ALGORITHM, e);
        }
    }
    
    /**
     * Faz o hash de uma senha gerando um salt automaticamente.
     * @param password Senha em texto plano
     * @return String no formato "salt:hash"
     */
    public static String hashPassword(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return salt + SEPARATOR + hash;
    }
    
    /**
     * Verifica se uma senha corresponde ao hash armazenado.
     * @param password Senha em texto plano para verificar
     * @param storedHash Hash armazenado no formato "salt:hash"
     * @return true se a senha corresponder ao hash
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null || !storedHash.contains(SEPARATOR)) {
            // Se não tem separador, provavelmente é uma senha antiga em texto plano
            // Para compatibilidade, comparar diretamente
            return password.equals(storedHash);
        }
        
        String[] parts = storedHash.split(SEPARATOR, 2);
        if (parts.length != 2) {
            return false;
        }
        
        String salt = parts[0];
        String hash = parts[1];
        
        String computedHash = hashPassword(password, salt);
        return hash.equals(computedHash);
    }
    
    /**
     * Verifica se uma senha está em formato hasheado.
     * @param storedHash Hash armazenado
     * @return true se está hasheado, false se está em texto plano
     */
    public static boolean isHashed(String storedHash) {
        return storedHash != null && storedHash.contains(SEPARATOR);
    }
} 