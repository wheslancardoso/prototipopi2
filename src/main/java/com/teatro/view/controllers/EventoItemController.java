package com.teatro.view.controllers;

import com.teatro.model.*;
import com.teatro.view.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller refatorado para o item de evento (EventoItem.fxml).
 * Agora integrado com o sistema FXML e SceneManager.
 */
public class EventoItemController implements Initializable {
    
    @FXML
    private Label labelNomeEvento;
    
    @FXML
    private ImageView posterImageView;
    
    @FXML
    private HBox sessoesBox;
    
    private Evento evento;
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private SceneManager sceneManager;
    
    // Mapeamento de posters dos eventos
    private static final Map<String, String> EVENTO_POSTER_MAP = Map.of(
        "Hamlet", "hamletposter.jpg",
        "O Auto da Compadecida", "compadecidaposter.jpg",
        "O Fantasma da Opera", "ofantasmadaoperaposter.jpg"
    );
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        teatro = sceneManager.getTeatro();
        usuario = sceneManager.getUsuarioLogado();
        stage = sceneManager.getStage();
    }
    
    /**
     * Configura o evento para este item.
     * Este método deve ser chamado após o carregamento do FXML.
     */
    public void configurarEvento(Evento evento) {
        this.evento = evento;
        
        if (evento != null) {
            carregarInformacaoEvento();
            criarBotoesSessoes();
        }
    }
    
    /**
     * Método de compatibilidade com o código existente.
     */
    public void configurarEvento(Evento evento, Teatro teatro, Usuario usuario, Stage stage) {
        this.evento = evento;
        this.teatro = teatro != null ? teatro : this.teatro;
        this.usuario = usuario != null ? usuario : this.usuario;
        this.stage = stage != null ? stage : this.stage;
        
        configurarEvento(evento);
    }
    
    /**
     * Carrega as informações básicas do evento.
     */
    private void carregarInformacaoEvento() {
        // Nome do evento
        labelNomeEvento.setText(evento.getNome());
        
        // Poster do evento
        carregarPosterEvento();
    }
    
    /**
     * Carrega o poster do evento.
     */
    private void carregarPosterEvento() {
        String posterFile = determinaPosterFile();
        InputStream imgStream = getClass().getResourceAsStream("/posters/" + posterFile);
        
        if (imgStream != null) {
            try {
                Image image = new Image(imgStream);
                posterImageView.setImage(image);
                
                // Configurar propriedades da ImageView
                posterImageView.setFitWidth(120);
                posterImageView.setFitHeight(160);
                posterImageView.setPreserveRatio(true);
                posterImageView.setSmooth(true);
                
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem: " + posterFile);
                carregarImagemPadrao();
            }
        } else {
            carregarImagemPadrao();
        }
    }
    
    /**
     * Determina qual arquivo de poster usar.
     */
    private String determinaPosterFile() {
        if (evento.getPoster() != null && !evento.getPoster().isEmpty()) {
            return evento.getPoster();
        }
        
        // Usar mapeamento baseado no nome do evento
        return EVENTO_POSTER_MAP.getOrDefault(evento.getNome(), "default.jpg");
    }
    
    /**
     * Carrega uma imagem padrão quando não encontra o poster.
     */
    private void carregarImagemPadrao() {
        InputStream defaultStream = getClass().getResourceAsStream("/posters/default.jpg");
        if (defaultStream != null) {
            posterImageView.setImage(new Image(defaultStream));
        } else {
            // Se nem a imagem padrão existe, criar uma ImageView vazia
            posterImageView.setImage(null);
        }
    }
    
    /**
     * Cria os botões para as sessões do evento.
     */
    private void criarBotoesSessoes() {
        sessoesBox.getChildren().clear();
        
        if (evento.getSessoes() == null || evento.getSessoes().isEmpty()) {
            Label semSessoes = new Label("Nenhuma sessão disponível");
            semSessoes.getStyleClass().add("no-sessions-label");
            sessoesBox.getChildren().add(semSessoes);
            return;
        }
        
        for (Sessao sessao : evento.getSessoes()) {
            Button btnSessao = criarBotaoSessao(sessao);
            sessoesBox.getChildren().add(btnSessao);
        }
    }
    
    /**
     * Cria um botão para uma sessão específica.
     */
    private Button criarBotaoSessao(Sessao sessao) {
        Button btn = new Button(sessao.getHorario());
        btn.getStyleClass().add("sessao-button");
        
        // Configurar estilo
        btn.setStyle("""
            -fx-background-color: #3498db; 
            -fx-text-fill: white; 
            -fx-font-weight: bold; 
            -fx-background-radius: 6; 
            -fx-padding: 8 22; 
            -fx-font-size: 15px; 
            -fx-cursor: hand;
            """);
        
        // Efeitos de hover
        btn.setOnMouseEntered(e -> 
            btn.setStyle("""
                -fx-background-color: #217dbb; 
                -fx-text-fill: white; 
                -fx-font-weight: bold; 
                -fx-background-radius: 6; 
                -fx-padding: 8 22; 
                -fx-font-size: 15px; 
                -fx-cursor: hand;
                """)
        );
        
        btn.setOnMouseExited(e -> 
            btn.setStyle("""
                -fx-background-color: #3498db; 
                -fx-text-fill: white; 
                -fx-font-weight: bold; 
                -fx-background-radius: 6; 
                -fx-padding: 8 22; 
                -fx-font-size: 15px; 
                -fx-cursor: hand;
                """)
        );
        
        // Ação do botão
        btn.setOnAction(e -> navegarParaCompraSessao(sessao));
        
        return btn;
    }
    
    /**
     * Navega para a tela de compra da sessão selecionada.
     */
    private void navegarParaCompraSessao(Sessao sessao) {
        try {
            // Verificar se há áreas disponíveis para a sessão
            if (teatro.getAreasDisponiveis(sessao).isEmpty()) {
                mostrarAviso("Esta sessão não possui áreas disponíveis no momento.");
                return;
            }
            
            // Navegar para a tela de compra de ingressos
            sceneManager.loadScene("/com/teatro/view/fxml/compra-ingresso.fxml", 
                                 "Sistema de Teatro - Compra de Ingressos");
            
            // TODO: Passar a sessão para o controller de compra
            // Isso pode ser feito através do SceneManager expandido ou através de um estado global
            
        } catch (Exception e) {
            mostrarErro("Erro ao abrir tela de compra: " + e.getMessage());
        }
    }
    
    /**
     * Exibe uma mensagem de aviso.
     */
    private void mostrarAviso(String mensagem) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.WARNING
        );
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    /**
     * Exibe uma mensagem de erro.
     */
    private void mostrarErro(String mensagem) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    /**
     * Obtém o evento configurado.
     */
    public Evento getEvento() {
        return evento;
    }
    
    /**
     * Verifica se o evento foi configurado.
     */
    public boolean isEventoConfigurado() {
        return evento != null;
    }
    
    /**
     * Atualiza as informações do evento (útil para refresh).
     */
    public void atualizarEvento() {
        if (evento != null) {
            carregarInformacaoEvento();
            criarBotoesSessoes();
        }
    }
    
    /**
     * Define dependências manualmente (usado em contextos especiais).
     */
    public void setDependencias(Teatro teatro, Usuario usuario, Stage stage) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
    }
}