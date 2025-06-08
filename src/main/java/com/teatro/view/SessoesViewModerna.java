package com.teatro.view;

import com.teatro.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Versão modernizada da tela de seleção de sessões.
 */
public class SessoesViewModerna {
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

    public SessoesViewModerna(Teatro teatro, Usuario usuario, Stage stage) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
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
        
        // Adiciona os cards de eventos
        VBox eventsContainer = new VBox(25);
        for (Evento evento : teatro.getEventos()) {
            eventsContainer.getChildren().add(criarCardEvento(evento));
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
            new LoginViewModerna(stage).show();
        });
        
        userInfo.getChildren().addAll(userName, logoutButton);
        
        // Botão voltar para o dashboard
        Button homeButton = new Button("Dashboard");
        homeButton.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-cursor: hand;");
        
        homeButton.setOnAction(e -> {
            new DashboardViewModerna(teatro, usuario, stage).show();
        });
        
        // Espaçador para empurrar o userInfo para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(systemTitle, homeButton, spacer, userInfo);
        
        return topBar;
    }

    private VBox criarCardEvento(Evento evento) {
        VBox card = new VBox();
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 20px;" +
            "-fx-spacing: 15px;");

        // Título do evento
        Label titulo = new Label(evento.getNome());
        titulo.setFont(Font.font("System", FontWeight.BOLD, 22));
        titulo.setTextFill(Color.web(TEXT_COLOR));
        
        // Separador
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");
        
        // Container para os controles de seleção
        VBox selectionBox = new VBox(15);
        selectionBox.setAlignment(Pos.CENTER_LEFT);
        
        // Sessões disponíveis
        Label sessoesLabel = new Label("Sessões Disponíveis:");
        sessoesLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Botões para cada sessão disponível
        HBox sessoesContainer = new HBox(10);
        sessoesContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Adiciona botões para cada sessão (Manhã, Tarde, Noite)
        for (String horario : new String[]{"Manhã", "Tarde", "Noite"}) {
            Button btnSessao = new Button(horario);
            btnSessao.setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px 20px;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 4;");
                
            // Cria uma cópia final da variável horario para usar dentro da lambda
            final String horarioFinal = horario;
            
            btnSessao.setOnAction(e -> {
                // Encontra a sessão correspondente no evento
                Sessao sessaoDoEvento = evento.getSessoes().stream()
                    .filter(s -> s.getHorario().equalsIgnoreCase(horarioFinal))
                    .findFirst()
                    .orElse(null);
                    
                if (sessaoDoEvento != null) {
                    System.out.println("Sessão encontrada: " + sessaoDoEvento.getHorario() + " com " + 
                                      sessaoDoEvento.getAreas().size() + " áreas");
                    new CompraIngressoViewModerna(teatro, usuario, stage, sessaoDoEvento).show();
                } else {
                    System.err.println("Sessão não encontrada para o horário: " + horarioFinal);
                    // Mostra uma mensagem de erro para o usuário
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Sessão não encontrada para o horário: " + horarioFinal);
                    alert.showAndWait();
                }
            });
            
            // Verifica se existe uma sessão para este horário
            boolean sessaoExiste = evento.getSessoes().stream()
                .anyMatch(s -> s.getHorario().equalsIgnoreCase(horario));
                
            if (!sessaoExiste) {
                btnSessao.setDisable(true);
                btnSessao.setStyle(
                    "-fx-background-color: #cccccc;" +
                    "-fx-text-fill: #666666;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 8px 20px;" +
                    "-fx-cursor: default;" +
                    "-fx-background-radius: 4;");
            }
            
            sessoesContainer.getChildren().add(btnSessao);
        }
        
        // Adiciona os elementos ao container de seleção
        selectionBox.getChildren().addAll(sessoesLabel, sessoesContainer);
        
        // Adiciona os elementos ao card
        card.getChildren().addAll(titulo, separator, selectionBox);
        
        return card;
    }
}
