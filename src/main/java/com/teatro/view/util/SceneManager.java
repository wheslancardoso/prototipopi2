package com.teatro.view.util;

import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gerenciador de cenas e navegação da aplicação.
 * Implementa o padrão Singleton.
 */
public class SceneManager {
    
    private static SceneManager instance;
    private Stage primaryStage;
    private Usuario usuarioLogado;
    private Teatro teatro;
    private Map<String, Object> userData;
    
    private SceneManager() {
        this.teatro = Teatro.getInstance();
        this.userData = new HashMap<>();
    }
    
    /**
     * Obtém a instância única do SceneManager.
     */
    public static synchronized SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }
    
    /**
     * Define o stage principal da aplicação.
     */
    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Obtém o stage principal.
     */
    public Stage getStage() {
        return primaryStage;
    }
    
    /**
     * Define o usuário logado na sessão.
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }
    
    /**
     * Obtém o usuário logado.
     */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    /**
     * Obtém a instância do teatro.
     */
    public Teatro getTeatro() {
        return teatro;
    }
    
    /**
     * Carrega uma nova cena no stage principal.
     */
    public void loadScene(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        // Injeta dependências no controller se necessário
        Object controller = loader.getController();
        injectDependencies(controller);
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();
    }
    
    /**
     * Carrega uma nova cena no stage principal com dimensões específicas.
     */
    public void loadScene(String fxmlPath, String title, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        // Injeta dependências no controller se necessário
        Object controller = loader.getController();
        injectDependencies(controller);
        
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();
    }
    
    /**
     * Abre uma janela modal.
     */
    public Stage openModal(String fxmlPath, String title, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        // Injeta dependências no controller se necessário
        Object controller = loader.getController();
        injectDependencies(controller);
        
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(primaryStage);
        modalStage.setTitle(title);
        modalStage.setResizable(false);
        
        Scene scene = new Scene(root, width, height);
        modalStage.setScene(scene);
        
        // Se o controller tem método setStage, chama ele
        if (controller != null) {
            try {
                controller.getClass().getMethod("setStage", Stage.class).invoke(controller, modalStage);
            } catch (Exception e) {
                // Método setStage não existe ou não é acessível, continua normalmente
            }
        }
        
        modalStage.showAndWait();
        return modalStage;
    }
    
    /**
     * Injeta dependências comuns nos controllers.
     */
    private void injectDependencies(Object controller) {
        if (controller == null) return;
        
        // Injeta o stage se o controller tem método setStage
        try {
            controller.getClass().getMethod("setStage", Stage.class).invoke(controller, primaryStage);
        } catch (Exception e) {
            // Método não existe ou não é acessível, continua normalmente
        }
        
        // Injeta o usuário logado se o controller tem método setUsuario
        if (usuarioLogado != null) {
            try {
                controller.getClass().getMethod("setUsuario", Usuario.class).invoke(controller, usuarioLogado);
            } catch (Exception e) {
                // Método não existe ou não é acessível, continua normalmente
            }
        }
        
        // Injeta o teatro se o controller tem método setTeatro
        try {
            controller.getClass().getMethod("setTeatro", Teatro.class).invoke(controller, teatro);
        } catch (Exception e) {
            // Método não existe ou não é acessível, continua normalmente
        }
    }
    
    /**
     * Navega para a tela de login.
     */
    public void goToLogin() {
        try {
            usuarioLogado = null; // Limpa a sessão
            loadScene("/com/teatro/view/fxml/login.fxml", "Sistema de Teatro - Login");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela de login", e);
        }
    }
    
    /**
     * Navega para o dashboard baseado no tipo de usuário.
     */
    public void goToDashboard() {
        try {
            if (usuarioLogado == null) {
                goToLogin();
                return;
            }
            
            if ("ADMIN".equals(usuarioLogado.getTipoUsuario())) {
                loadScene("/com/teatro/view/fxml/dashboard.fxml", "Sistema de Teatro - Dashboard");
            } else {
                loadScene("/com/teatro/view/fxml/sessoes.fxml", "Sistema de Teatro - Sessões");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar dashboard", e);
        }
    }
    
    /**
     * Navega para a tela de sessões.
     */
    public void goToSessoes() {
        try {
            loadScene("/com/teatro/view/fxml/sessoes.fxml", "Sistema de Teatro - Sessões");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela de sessões", e);
        }
    }
    
    /**
     * Sai da aplicação.
     */
    public void exit() {
        if (primaryStage != null) {
            primaryStage.close();
        }
        System.exit(0);
    }
    
    /**
     * Armazena um dado para ser usado entre telas.
     */
    public void setUserData(String key, Object value) {
        if (value == null) {
            userData.remove(key);
        } else {
            userData.put(key, value);
        }
    }
    
    /**
     * Recupera um dado armazenado entre telas.
     */
    public Object getUserData(String key) {
        return userData.get(key);
    }
    
    /**
     * Limpa todos os dados armazenados.
     */
    public void clearUserData() {
        userData.clear();
    }
}