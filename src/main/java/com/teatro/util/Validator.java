package com.teatro.util;

import com.teatro.exception.TeatroException;
import java.util.regex.Pattern;

/**
 * Classe utilitária para validações comuns do sistema.
 */
public class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    private static final Pattern CPF_PATTERN = Pattern.compile(
        "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$"
    );
    
    private static final Pattern TELEFONE_PATTERN = Pattern.compile(
        "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}[\\s-]?\\d{4}$"
    );
    
    /**
     * Valida um endereço de e-mail.
     * @param email O e-mail a ser validado
     * @throws TeatroException Se o e-mail for inválido
     */
    public static void validarEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new TeatroException("E-mail inválido: " + email);
        }
    }
    
    /**
     * Valida um número de CPF.
     * @param cpf O CPF a ser validado
     * @throws TeatroException Se o CPF for inválido
     */
    public static void validarCpf(String cpf) {
        if (cpf == null || !CPF_PATTERN.matcher(cpf).matches()) {
            throw new TeatroException("CPF inválido: " + cpf);
        }
        
        // Remove caracteres não numéricos
        String cpfNumerico = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cpfNumerico.length() != 11) {
            throw new TeatroException("CPF deve conter 11 dígitos: " + cpf);
        }
        
        // Verifica se todos os dígitos são iguais
        if (cpfNumerico.matches("(\\d)\\1{10}")) {
            throw new TeatroException("CPF não pode ter todos os dígitos iguais: " + cpf);
        }
        
        // Calcula primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpfNumerico.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito > 9) primeiroDigito = 0;
        
        // Calcula segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpfNumerico.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito > 9) segundoDigito = 0;
        
        // Verifica se os dígitos calculados são iguais aos dígitos informados
        if (Character.getNumericValue(cpfNumerico.charAt(9)) != primeiroDigito ||
            Character.getNumericValue(cpfNumerico.charAt(10)) != segundoDigito) {
            throw new TeatroException("CPF inválido (dígitos verificadores incorretos): " + cpf);
        }
    }
    
    /**
     * Valida um número de telefone.
     * @param telefone O telefone a ser validado
     * @throws TeatroException Se o telefone for inválido
     */
    public static void validarTelefone(String telefone) {
        if (telefone == null || !TELEFONE_PATTERN.matcher(telefone).matches()) {
            throw new TeatroException("Telefone inválido: " + telefone);
        }
    }
    
    /**
     * Valida se um objeto não é nulo.
     * @param valor O objeto a ser validado
     * @param nomeCampo O nome do campo (para mensagem de erro)
     * @throws TeatroException Se o objeto for nulo
     */
    public static void validarNaoNulo(Object valor, String nomeCampo) {
        if (valor == null) {
            throw new TeatroException(nomeCampo + " não pode ser nulo");
        }
    }
    
    /**
     * Valida se uma string não é nula ou vazia.
     * @param valor A string a ser validada
     * @param nomeCampo O nome do campo (para mensagem de erro)
     * @throws TeatroException Se a string for nula ou vazia
     */
    public static void validarStringNaoVazia(String valor, String nomeCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new TeatroException(nomeCampo + " não pode ser nulo ou vazio");
        }
    }
    
    /**
     * Valida se um número é positivo.
     * @param valor O número a ser validado
     * @param nomeCampo O nome do campo para mensagem de erro
     * @throws TeatroException Se o número não for positivo
     */
    public static void validarNumeroPositivo(int valor, String nomeCampo) {
        if (valor <= 0) {
            throw new TeatroException(nomeCampo + " deve ser positivo");
        }
    }
    
    /**
     * Valida se um número é positivo.
     * @param valor O número a ser validado
     * @param nomeCampo O nome do campo para mensagem de erro
     * @throws TeatroException Se o número não for positivo
     */
    public static void validarNumeroPositivo(double valor, String nomeCampo) {
        if (valor <= 0) {
            throw new TeatroException(nomeCampo + " deve ser positivo");
        }
    }
    
    /**
     * Valida se um número está dentro de um intervalo.
     * @param valor O número a ser validado
     * @param min O valor mínimo (inclusive)
     * @param max O valor máximo (inclusive)
     * @param nomeCampo O nome do campo para mensagem de erro
     * @throws TeatroException Se o número estiver fora do intervalo
     */
    public static void validarNumeroNoIntervalo(int valor, int min, int max, String nomeCampo) {
        if (valor < min || valor > max) {
            throw new TeatroException(nomeCampo + " deve estar entre " + min + " e " + max);
        }
    }
    
    /**
     * Valida se um número está dentro de um intervalo.
     * @param valor O número a ser validado
     * @param min O valor mínimo (inclusive)
     * @param max O valor máximo (inclusive)
     * @param nomeCampo O nome do campo para mensagem de erro
     * @throws TeatroException Se o número estiver fora do intervalo
     */
    public static void validarNumeroNoIntervalo(double valor, double min, double max, String nomeCampo) {
        if (valor < min || valor > max) {
            throw new TeatroException(nomeCampo + " deve estar entre " + min + " e " + max);
        }
    }
} 