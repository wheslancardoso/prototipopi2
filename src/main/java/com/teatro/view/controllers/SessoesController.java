package com.teatro.view.controllers;

import com.teatro.model.Evento;
import com.teatro.model.Sessao;
import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import com.teatro.view.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller para a tela de sessões disponíveis.
 */
public class SessoesController implements Initializable {
    
    @FXML
    private Label sessoes_lblLogo;
    
    @FXML
    private Label sessoes_lblUsuario;
    
    @FXML
    private Label sessoes_lblTitulo;
    
    @FXML
    private Button sessoes_btnDashboard;
    
    @FXML
    private Button sessoes_btnSair;
    
    @FXML
    private VBox sessoes_containerEventos;
    
    @FXML
    private VBox sessoes_containerVazio;
    
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private SceneManager sceneManager;
    private Evento eventoSelecionado;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        teatro = sceneManager.getTeatro();
        usuario = sceneManager.getUsuarioLogado();
        
        // Configurar interface com base no usuário
        if (usuario != null) {
            sessoes_lblUsuario.setText(usuario.getNome());
            
            // Ocultar botão dashboard se não for admin
            if (!"ADMIN".equals(usuario.getTipoUsuario())) {
                sessoes_btnDashboard.setVisible(false);
                sessoes_btnDashboard.setManaged(false);
            }
        }
        
        // Carregar eventos
        carregarEventos();
    }
    
    /**
     * Define o usuário logado.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (sessoes_lblUsuario != null) {
            sessoes_lblUsuario.setText(usuario.getNome());
        }
    }
    
    /**
     * Define o evento selecionado (usado quando vem de uma tela específica).
     */
    public void setEventoSelecionado(Evento evento) {
        this.eventoSelecionado = evento;
    }
    
    /**
     * Carrega os eventos disponíveis.
     */
    private void carregarEventos() {
        try {
            List<Evento> eventos = teatro.getEventos();
            
            if (eventos.isEmpty()) {
                mostrarContainerVazio();
                return;
            }
            
            sessoes_containerEventos.getChildren().clear();
            
            // Se há um evento específico selecionado, mostra apenas ele
            if (eventoSelecionado != null) {
                Node eventoCard = criarCardEvento(eventoSelecionado);
                if (eventoCard != null) {
                    sessoes_containerEventos.getChildren().add(eventoCard);
                }
            } else {
                // Caso contrário, mostra todos os eventos
                for (Evento evento : eventos) {
                    Node eventoCard = criarCardEvento(evento);
                    if (eventoCard != null) {
                        sessoes_containerEventos.getChildren().add(eventoCard);
                    }
                }
            }
            
            mostrarContainerEventos();
            
        } catch (Exception e) {
            mostrarErro("Erro ao carregar eventos: " + e.getMessage());
        }
    }
    
    /**
     * Cria um card para um evento.
     */
    private Node criarCardEvento(Evento evento) {
        try {
            // Tentar carregar o FXML do EventoItem
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teatro/view/fxml/EventoItem.fxml"));
            HBox eventoItem = loader.load();
            
            // Configurar o controller do item
            Object controller = loader.getController();
            if (controller instanceof EventoItemController) {
                ((EventoItemController) controller).configurarEvento(evento, teatro, usuario, stage);
            }
            
            return eventoItem;
            
        } catch (Exception e) {
            // Fallback: criar card manualmente
            return criarCardEventoManual(evento);
        }
    }
    
    /**
     * Cria um card de evento manualmente (fallback).
     */
    private Node criarCardEventoManual(Evento evento) {
        HBox card = new HBox(24);
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.getStyleClass().add("evento-card");
        card.setPadding(new Insets(18, 24, 18, 24));
        
        // Imagem do poster
        ImageView poster = new ImageView();
        poster.setFitWidth(120);
        poster.setFitHeight(160);
        poster.setPreserveRatio(true);
        
        // Carregar imagem do poster
        String posterFile = evento.getPoster() != null ? evento.getPoster() : "default.jpg";
        InputStream imgStream = getClass().getResourceAsStream("/posters/" + posterFile);
        if (imgStream != null) {
            poster.setImage(new Image(imgStream));
        }
        
        // Informações do evento
        VBox infoBox = new VBox(12);
        infoBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        
        // Nome do evento
        Label nomeEvento = new Label(evento.getNome());
        nomeEvento.getStyleClass().add("evento-title");
        
        // Label "Sessões Disponíveis"
        Label lblSessoes = new Label("Sessões Disponíveis:");
        lblSessoes.getStyleClass().add("sessoes-label");
        
        // Container para botões das sessões
        HBox sessoesBox = new HBox(12);
        sessoesBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // Criar botões para cada sessão
        for (Sessao sessao : evento.getSessoes()) {
            Button btnSessao = new Button(sessao.getHorario());
            btnSessao.getStyleClass().add("sessao-button");
            
            // Evento do botão
            btnSessao.setOnAction(e -> {
                try {
                    // Navegar para compra de ingresso
                    sceneManager.loadScene("/com/teatro/view/fxml/compra-ingresso.fxml", 
                                         "Sistema de Teatro - Compra de Ingressos");
                    
                    // TODO: Passar a sessão para o controller de compra
                    
                } catch (Exception ex) {
                    mostrarErro("Erro ao abrir tela de compra: " + ex.getMessage());
                }
            });
            
            sessoesBox.getChildren().add(btnSessao);
        }
        
        infoBox.getChildren().addAll(nomeEvento, lblSessoes, sessoesBox);
        card.getChildren().addAll(poster, infoBox);
        
        return card;
    }
    
    /**
     * Mostra o container de eventos.
     */
    private void mostrarContainerEventos() {
        sessoes_containerEventos.setVisible(true);
        sessoes_containerEventos.setManaged(true);
        sessoes_containerVazio.setVisible(false);
        sessoes_containerVazio.setManaged(false);
    }
    
    /**
     * Mostra o container vazio.
     */
    private void mostrarContainerVazio() {
        sessoes_containerEventos.setVisible(false);
        sessoes_containerEventos.setManaged(false);
        sessoes_containerVazio.setVisible(true);
        sessoes_containerVazio.setManaged(true);
    }
    
    /**
     * Manipula o evento do botão Dashboard.
     */
    @FXML
    private void handleDashboard() {
        try {
            sceneManager.goToDashboard();
        } catch (Exception e) {
            mostrarErro("Erro ao navegar para o dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Manipula o evento do botão Sair.
     */
    @FXML
    private void handleSair() {
        try {
            sceneManager.goToLogin();
        } catch (Exception e) {
            mostrarErro("Erro ao fazer logout: " + e.getMessage());
        }
    }
    
    /**
     * Exibe uma mensagem de erro.
     */
    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    /**
     * Recarrega a lista de eventos.
     */
    public void recarregarEventos() {
        carregarEventos();
    }
}