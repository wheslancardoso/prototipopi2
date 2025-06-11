package com.teatro.view.util;

import javafx.scene.control.TextField;
import java.util.regex.Pattern;

/**
 * Utilitários para validação de campos e aplicação de máscaras.
 */
public class ValidationUtils {
    
    // Padrões de validação
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    private static final Pattern CPF_PATTERN = Pattern.compile(
        "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}[\\s-]?\\d{4}$"
    );
    
    /**
     * Valida se um email está no formato correto.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Valida se um CPF está no formato correto e é válido.
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null || !CPF_PATTERN.matcher(cpf).matches()) {
            return false;
        }
        
        // Remove caracteres não numéricos
        String cpfNumerico = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cpfNumerico.length() != 11) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais
        if (cpfNumerico.matches("(\\d)\\1{10}")) {
            return false;
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
        return Character.getNumericValue(cpfNumerico.charAt(9)) == primeiroDigito &&
               Character.getNumericValue(cpfNumerico.charAt(10)) == segundoDigito;
    }
    
    /**
     * Valida se um telefone está no formato correto.
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Aplica máscara de CPF em um TextField.
     */
    public static void addCpfMask(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            
            // Remove tudo que não é dígito
            String digits = newValue.replaceAll("[^\\d]", "");
            
            // Limita a 11 dígitos
            if (digits.length() > 11) {
                digits = digits.substring(0, 11);
            }
            
            // Aplica a máscara
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 3 || i == 6) {
                    formatted.append(".");
                } else if (i == 9) {
                    formatted.append("-");
                }
                formatted.append(digits.charAt(i));
            }
            
            // Atualiza o campo apenas se mudou
            String formattedText = formatted.toString();
            if (!formattedText.equals(newValue)) {
                textField.setText(formattedText);
                textField.positionCaret(formattedText.length());
            }
        });
    }
    
    /**
     * Aplica máscara de telefone em um TextField.
     */
    public static void addPhoneMask(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            
            // Remove tudo que não é dígito
            String digits = newValue.replaceAll("[^\\d]", "");
            
            // Limita a 11 dígitos
            if (digits.length() > 11) {
                digits = digits.substring(0, 11);
            }
            
            // Aplica a máscara
            StringBuilder formatted = new StringBuilder();
            if (digits.length() > 0) {
                formatted.append("(");
                
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 2) {
                        formatted.append(") ");
                    } else if (i == 7 && digits.length() == 11) {
                        formatted.append("-");
                    } else if (i == 6 && digits.length() == 10) {
                        formatted.append("-");
                    }
                    formatted.append(digits.charAt(i));
                }
            }
            
            // Atualiza o campo apenas se mudou
            String formattedText = formatted.toString();
            if (!formattedText.equals(newValue)) {
                textField.setText(formattedText);
                textField.positionCaret(formattedText.length());
            }
        });
    }
    
    /**
     * Adiciona validação visual de email em tempo real.
     */
    public static void addEmailValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                textField.getStyleClass().removeAll("error", "success");
                return;
            }
            
            textField.getStyleClass().removeAll("error", "success");
            
            if (isValidEmail(newValue)) {
                textField.getStyleClass().add("success");
            } else if (newValue.length() > 3) { // Só mostra erro depois de digitar um pouco
                textField.getStyleClass().add("error");
            }
        });
    }
    
    /**
     * Adiciona validação visual de CPF em tempo real.
     */
    public static void addCpfValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                textField.getStyleClass().removeAll("error", "success");
                return;
            }
            
            textField.getStyleClass().removeAll("error", "success");
            
            // Remove formatação para validar
            String cpfLimpo = newValue.replaceAll("[^\\d]", "");
            
            if (cpfLimpo.length() == 11) {
                if (isValidCpf(newValue)) {
                    textField.getStyleClass().add("success");
                } else {
                    textField.getStyleClass().add("error");
                }
            }
        });
    }
    
    /**
     * Força apenas números em um TextField.
     */
    public static void addNumericValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    /**
     * Força apenas letras e espaços em um TextField.
     */
    public static void addAlphaValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                textField.setText(newValue.replaceAll("[^a-zA-ZÀ-ÿ\\s]", ""));
            }
        });
    }
    
    /**
     * Limita o número de caracteres em um TextField.
     */
    public static void addLengthLimit(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > maxLength) {
                textField.setText(newValue.substring(0, maxLength));
            }
        });
    }
    
    /**
     * Valida se uma string não é nula nem vazia.
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Valida se uma string tem o tamanho mínimo.
     */
    public static boolean hasMinLength(String value, int minLength) {
        return value != null && value.length() >= minLength;
    }
    
    /**
     * Valida se duas strings são iguais.
     */
    public static boolean areEqual(String value1, String value2) {
        if (value1 == null && value2 == null) return true;
        if (value1 == null || value2 == null) return false;
        return value1.equals(value2);
    }
}