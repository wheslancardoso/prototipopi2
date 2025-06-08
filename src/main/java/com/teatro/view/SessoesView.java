package com.teatro.view;

import com.teatro.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * Versão modernizada da tela de seleção de sessões.
 */
public class SessoesView {
    private final Teatro teatro;
    private final Usuario usuario;
    private final Stage stage;
    
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;
    
    // Cores do tema
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String CARD_BACKGROUND = "white";

    private Evento eventoSelecionado;

    public SessoesView(Teatro teatro, Usuario usuario, Stage stage) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
        this.eventoSelecionado = null;
    }

    public SessoesView(Teatro teatro, Usuario usuario, Stage stage, Evento eventoSelecionado) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
        this.eventoSelecionado = eventoSelecionado;
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Sessões Disponíveis");

        // Container principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Barra superior
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Container central com scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        VBox contentContainer = new VBox(30);
        contentContainer.setPadding(new Insets(30, 40, 40, 40));
        
        // Título da página
        Label pageTitle = new Label("Sessões Disponíveis");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        pageTitle.setTextFill(Color.web(TEXT_COLOR));
        
        VBox eventsContainer = new VBox(25);
        if (eventoSelecionado != null) {
            eventsContainer.getChildren().add(criarCardEvento(eventoSelecionado));
        } else {
        for (Evento evento : teatro.getEventos()) {
            eventsContainer.getChildren().add(criarCardEvento(evento));
            }
        }
        
        contentContainer.getChildren().addAll(pageTitle, eventsContainer);
        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 40, 15, 40));
        topBar.setSpacing(20);
        topBar.setStyle("-fx-background-color: " + PRIMARY_COLOR + ";");
        
        // Logo ou título do sistema
        Label systemTitle = new Label("Sistema de Teatro");
        systemTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        systemTitle.setTextFill(Color.WHITE);
        
        // Informações do usuário
        HBox userInfo = new HBox();
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        userInfo.setSpacing(10);
        
        Label userName = new Label(usuario.getNome());
        userName.setTextFill(Color.WHITE);
        
        Button logoutButton = new Button("Sair");
        logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 3; -fx-cursor: hand;");
        
        logoutButton.setOnAction(e -> {
            new LoginView(stage).show();
        });
        
        userInfo.getChildren().addAll(userName, logoutButton);
        
        // Botão voltar para o dashboard
        Button homeButton = new Button("Dashboard");
        homeButton.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-cursor: hand;");
        
        homeButton.setOnAction(e -> {
            new DashboardView(teatro, usuario, stage).show();
        });
        
        // Espaçador para empurrar o userInfo para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(systemTitle, homeButton, spacer, userInfo);
        
        return topBar;
    }

    private Node criarCardEvento(Evento evento) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teatro/view/EventoItem.fxml"));
            loader.setController(new EventoItemController());
            HBox eventoItem = loader.load();
            EventoItemController controller = loader.getController();
            controller.configurarEvento(evento, teatro, usuario, stage);
            return eventoItem;
        } catch (Exception e) {
            e.printStackTrace();
            HBox card = new HBox();
            card.getChildren().add(new Label(evento.getNome()));
            return card;
        }
    }
}
