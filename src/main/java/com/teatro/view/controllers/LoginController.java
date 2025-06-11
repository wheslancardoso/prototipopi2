package com.teatro.view.controllers;

import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import com.teatro.view.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller para a tela de login.
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField login_txtIdentificador;
    
    @FXML
    private PasswordField login_txtSenha;
    
    @FXML
    private Button login_btnEntrar;
    
    @FXML
    private Button login_btnEsqueceuSenha;
    
    @FXML
    private Button login_btnCadastrar;
    
    @FXML
    private Label login_lblErro;
    
    private Teatro teatro;
    private Stage stage;
    private SceneManager sceneManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teatro = Teatro.getInstance();
        sceneManager = SceneManager.getInstance();
        
        // Configurações iniciais
        login_lblErro.setVisible(false);
        
        // Event listeners para Enter key
        login_txtIdentificador.setOnAction(event -> login_txtSenha.requestFocus());
        login_txtSenha.setOnAction(event -> handleLogin());
        
        // Limpar erro quando usuário digitar
        login_txtIdentificador.textProperty().addListener((obs, oldVal, newVal) -> hideError());
        login_txtSenha.textProperty().addListener((obs, oldVal, newVal) -> hideError());
    }
    
    /**
     * Define o stage principal.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        sceneManager.setStage(stage);
    }
    
    /**
     * Manipula o evento de login.
     */
    @FXML
    private void handleLogin() {
        String identificador = login_txtIdentificador.getText().trim();
        String senha = login_txtSenha.getText();
        
        // Validações básicas
        if (identificador.isEmpty() || senha.isEmpty()) {
            showError("Por favor, preencha todos os campos.");
            return;
        }
        
        try {
            // Tenta autenticar o usuário
            Optional<Usuario> usuarioOpt = teatro.autenticarUsuario(identificador, senha);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                hideError();
                
                // Define o usuário na sessão
                sceneManager.setUsuarioLogado(usuario);
                
                // Redireciona baseado no tipo de usuário
                if ("ADMIN".equals(usuario.getTipoUsuario())) {
                    sceneManager.loadScene("/com/teatro/view/fxml/dashboard.fxml", "Sistema de Teatro - Dashboard");
                } else {
                    sceneManager.loadScene("/com/teatro/view/fxml/sessoes.fxml", "Sistema de Teatro - Sessões");
                }
            } else {
                showError("CPF/Email ou senha incorretos.");
                login_txtSenha.clear();
                login_txtIdentificador.requestFocus();
            }
        } catch (Exception e) {
            showError("Erro ao realizar login: " + e.getMessage());
        }
    }
    
    /**
     * Manipula o evento de "Esqueceu a senha".
     */
    @FXML
    private void handleEsqueceuSenha() {
        try {
            sceneManager.openModal("/com/teatro/view/fxml/recuperacao-senha.fxml", 
                                 "Recuperação de Senha", 500, 600);
        } catch (Exception e) {
            showError("Erro ao abrir tela de recuperação de senha.");
        }
    }
    
    /**
     * Manipula o evento de cadastro.
     */
    @FXML
    private void handleCadastrar() {
        try {
            sceneManager.openModal("/com/teatro/view/fxml/cadastro.fxml", 
                                 "Cadastro de Usuário", 600, 800);
        } catch (Exception e) {
            showError("Erro ao abrir tela de cadastro.");
        }
    }
    
    /**
     * Exibe uma mensagem de erro.
     */
    private void showError(String message) {
        login_lblErro.setText(message);
        login_lblErro.setVisible(true);
    }
    
    /**
     * Oculta a mensagem de erro.
     */
    private void hideError() {
        login_lblErro.setVisible(false);
    }
    
    /**
     * Limpa os campos do formulário.
     */
    public void clearForm() {
        login_txtIdentificador.clear();
        login_txtSenha.clear();
        hideError();
        login_txtIdentificador.requestFocus();
    }
}