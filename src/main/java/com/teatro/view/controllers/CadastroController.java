package com.teatro.view.controllers;

import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import com.teatro.model.factory.UsuarioComumFactory;
import com.teatro.model.factory.UsuarioFactory;
import com.teatro.view.util.SceneManager;
import com.teatro.view.util.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller para a tela de cadastro de usuário.
 */
public class CadastroController implements Initializable {
    
    @FXML
    private TextField cadastro_txtNome;
    
    @FXML
    private TextField cadastro_txtCpf;
    
    @FXML
    private TextField cadastro_txtEmail;
    
    @FXML
    private TextField cadastro_txtTelefone;
    
    @FXML
    private TextField cadastro_txtEndereco;
    
    @FXML
    private PasswordField cadastro_txtSenha;
    
    @FXML
    private PasswordField cadastro_txtConfirmarSenha;
    
    @FXML
    private Button cadastro_btnCadastrar;
    
    @FXML
    private Button cadastro_btnCancelar;
    
    @FXML
    private Label cadastro_lblErro;
    
    @FXML
    private Label cadastro_lblSucesso;
    
    private Teatro teatro;
    private SceneManager sceneManager;
    private Stage currentStage;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teatro = Teatro.getInstance();
        sceneManager = SceneManager.getInstance();
        
        // Configurações iniciais
        cadastro_lblErro.setVisible(false);
        cadastro_lblSucesso.setVisible(false);
        
        // Aplicar máscaras nos campos
        setupFieldMasks();
        
        // Validação em tempo real
        setupRealTimeValidation();
        
        // Focus na ordem correta
        setupTabOrder();
    }
    
    /**
     * Define o stage atual.
     */
    public void setStage(Stage stage) {
        this.currentStage = stage;
    }
    
    /**
     * Configura as máscaras dos campos.
     */
    private void setupFieldMasks() {
        // Máscara para CPF
        ValidationUtils.addCpfMask(cadastro_txtCpf);
        
        // Máscara para telefone
        ValidationUtils.addPhoneMask(cadastro_txtTelefone);
        
        // Validação de email em tempo real
        ValidationUtils.addEmailValidation(cadastro_txtEmail);
    }
    
    /**
     * Configura validação em tempo real.
     */
    private void setupRealTimeValidation() {
        // Limpar mensagens quando usuário digitar
        cadastro_txtNome.textProperty().addListener((obs, oldVal, newVal) -> hideMessages());
        cadastro_txtCpf.textProperty().addListener((obs, oldVal, newVal) -> hideMessages());
        cadastro_txtEmail.textProperty().addListener((obs, oldVal, newVal) -> hideMessages());
        cadastro_txtTelefone.textProperty().addListener((obs, oldVal, newVal) -> hideMessages());
        cadastro_txtSenha.textProperty().addListener((obs, oldVal, newVal) -> hideMessages());
        cadastro_txtConfirmarSenha.textProperty().addListener((obs, oldVal, newVal) -> hideMessages());
        
        // Validação da confirmação de senha
        cadastro_txtConfirmarSenha.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !newVal.equals(cadastro_txtSenha.getText())) {
                cadastro_txtConfirmarSenha.getStyleClass().add("error");
            } else {
                cadastro_txtConfirmarSenha.getStyleClass().remove("error");
            }
        });
    }
    
    /**
     * Configura a ordem de tabulação.
     */
    private void setupTabOrder() {
        cadastro_txtNome.setOnAction(event -> cadastro_txtCpf.requestFocus());
        cadastro_txtCpf.setOnAction(event -> cadastro_txtEmail.requestFocus());
        cadastro_txtEmail.setOnAction(event -> cadastro_txtTelefone.requestFocus());
        cadastro_txtTelefone.setOnAction(event -> cadastro_txtEndereco.requestFocus());
        cadastro_txtEndereco.setOnAction(event -> cadastro_txtSenha.requestFocus());
        cadastro_txtSenha.setOnAction(event -> cadastro_txtConfirmarSenha.requestFocus());
        cadastro_txtConfirmarSenha.setOnAction(event -> handleCadastrar());
    }
    
    /**
     * Manipula o evento de cadastro.
     */
    @FXML
    private void handleCadastrar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }
        
        try {
            // Desabilitar botão durante o processamento
            cadastro_btnCadastrar.setDisable(true);
            cadastro_btnCadastrar.setText("Cadastrando...");
            
            // Criar usuário usando Factory Method
            UsuarioFactory factory = new UsuarioComumFactory();
            Usuario novoUsuario = factory.criarUsuario(
                cadastro_txtNome.getText().trim(),
                cadastro_txtCpf.getText().trim(),
                cadastro_txtEmail.getText().trim(),
                cadastro_txtSenha.getText(),
                cadastro_txtEndereco.getText().trim(),
                cadastro_txtTelefone.getText().trim()
            );
            
            // Validar CPF
            if (!novoUsuario.validarCPF()) {
                showError("CPF inválido. Verifique os dígitos digitados.");
                return;
            }
            
            // Tentar cadastrar
            if (teatro.cadastrarUsuario(novoUsuario)) {
                showSuccess("Cadastro realizado com sucesso!");
                
                // Aguardar 2 segundos e fechar modal
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            if (currentStage != null) {
                                currentStage.close();
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
        } catch (Exception e) {
            showError("Erro ao realizar cadastro: " + e.getMessage());
        } finally {
            // Reabilitar botão
            cadastro_btnCadastrar.setDisable(false);
            cadastro_btnCadastrar.setText("Criar Conta");
        }
    }
    
    /**
     * Manipula o evento de cancelar.
     */
    @FXML
    private void handleCancelar() {
        if (currentStage != null) {
            currentStage.close();
        }
    }
    
    /**
     * Valida todos os campos do formulário.
     */
    private boolean validarCampos() {
        // Limpar estilos de erro
        removeErrorStyles();
        
        boolean valid = true;
        StringBuilder errors = new StringBuilder();
        
        // Nome
        if (cadastro_txtNome.getText().trim().isEmpty()) {
            cadastro_txtNome.getStyleClass().add("error");
            errors.append("Nome é obrigatório.\n");
            valid = false;
        } else if (cadastro_txtNome.getText().trim().length() < 2) {
            cadastro_txtNome.getStyleClass().add("error");
            errors.append("Nome deve ter pelo menos 2 caracteres.\n");
            valid = false;
        }
        
        // CPF
        if (cadastro_txtCpf.getText().trim().isEmpty()) {
            cadastro_txtCpf.getStyleClass().add("error");
            errors.append("CPF é obrigatório.\n");
            valid = false;
        } else if (!ValidationUtils.isValidCpf(cadastro_txtCpf.getText())) {
            cadastro_txtCpf.getStyleClass().add("error");
            errors.append("CPF inválido.\n");
            valid = false;
        }
        
        // Email
        if (cadastro_txtEmail.getText().trim().isEmpty()) {
            cadastro_txtEmail.getStyleClass().add("error");
            errors.append("E-mail é obrigatório.\n");
            valid = false;
        } else if (!ValidationUtils.isValidEmail(cadastro_txtEmail.getText())) {
            cadastro_txtEmail.getStyleClass().add("error");
            errors.append("E-mail inválido.\n");
            valid = false;
        }
        
        // Telefone
        if (cadastro_txtTelefone.getText().trim().isEmpty()) {
            cadastro_txtTelefone.getStyleClass().add("error");
            errors.append("Telefone é obrigatório.\n");
            valid = false;
        }
        
        // Senha
        if (cadastro_txtSenha.getText().isEmpty()) {
            cadastro_txtSenha.getStyleClass().add("error");
            errors.append("Senha é obrigatória.\n");
            valid = false;
        } else if (cadastro_txtSenha.getText().length() < 6) {
            cadastro_txtSenha.getStyleClass().add("error");
            errors.append("Senha deve ter pelo menos 6 caracteres.\n");
            valid = false;
        }
        
        // Confirmar senha
        if (!cadastro_txtSenha.getText().equals(cadastro_txtConfirmarSenha.getText())) {
            cadastro_txtConfirmarSenha.getStyleClass().add("error");
            errors.append("Senhas não coincidem.\n");
            valid = false;
        }
        
        if (!valid) {
            showError(errors.toString().trim());
        }
        
        return valid;
    }
    
    /**
     * Remove estilos de erro dos campos.
     */
    private void removeErrorStyles() {
        cadastro_txtNome.getStyleClass().remove("error");
        cadastro_txtCpf.getStyleClass().remove("error");
        cadastro_txtEmail.getStyleClass().remove("error");
        cadastro_txtTelefone.getStyleClass().remove("error");
        cadastro_txtSenha.getStyleClass().remove("error");
        cadastro_txtConfirmarSenha.getStyleClass().remove("error");
    }
    
    /**
     * Exibe uma mensagem de erro.
     */
    private void showError(String message) {
        cadastro_lblErro.setText(message);
        cadastro_lblErro.setVisible(true);
        cadastro_lblSucesso.setVisible(false);
    }
    
    /**
     * Exibe uma mensagem de sucesso.
     */
    private void showSuccess(String message) {
        cadastro_lblSucesso.setText(message);
        cadastro_lblSucesso.setVisible(true);
        cadastro_lblErro.setVisible(false);
    }
    
    /**
     * Oculta as mensagens.
     */
    private void hideMessages() {
        cadastro_lblErro.setVisible(false);
        cadastro_lblSucesso.setVisible(false);
    }
    
    /**
     * Limpa todos os campos do formulário.
     */
    public void clearForm() {
        cadastro_txtNome.clear();
        cadastro_txtCpf.clear();
        cadastro_txtEmail.clear();
        cadastro_txtTelefone.clear();
        cadastro_txtEndereco.clear();
        cadastro_txtSenha.clear();
        cadastro_txtConfirmarSenha.clear();
        hideMessages();
        removeErrorStyles();
        cadastro_txtNome.requestFocus();
    }
}