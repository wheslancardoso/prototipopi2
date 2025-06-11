package com.teatro.view.controllers;

import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import com.teatro.view.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {
    protected Teatro teatro;
    protected Usuario usuario;
    protected SceneManager sceneManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.sceneManager = SceneManager.getInstance();
        this.teatro = sceneManager.getTeatro();
        this.usuario = sceneManager.getUsuarioLogado();
        
        inicializarComponentes();
        configurarEventos();
        carregarDados();
    }
    
    protected abstract void inicializarComponentes();
    protected abstract void configurarEventos();
    protected abstract void carregarDados();
    
    protected void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    protected void mostrarSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    protected void mostrarConfirmacao(String titulo, String mensagem, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                onConfirm.run();
            }
        });
    }
    
    @FXML
    protected void handleSair() {
        sceneManager.goToLogin();
    }
    
    @FXML
    protected void handleDashboard() {
        try {
            sceneManager.loadScene("/com/teatro/view/fxml/dashboard.fxml",
                                 "Sistema de Teatro - Dashboard");
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao navegar para dashboard: " + e.getMessage());
        }
    }
}
