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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Versão modernizada da tela de compra de ingressos.
 */
public class CompraIngressoViewModerna {
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private Sessao sessao;
    private Area areaSelecionada;
    
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;
    
    // Cores do tema
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#2ecc71";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String CARD_BACKGROUND = "white";


    public CompraIngressoViewModerna(Teatro teatro, Usuario usuario, Stage stage, Sessao sessao) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
        this.sessao = sessao;
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Compra de Ingressos");

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
        Label pageTitle = new Label("Compra de Ingressos");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        pageTitle.setTextFill(Color.web(TEXT_COLOR));
        
        // Card com informações do evento e seleção de área
        VBox infoCard = createInfoCard();
        
        contentContainer.getChildren().addAll(pageTitle, infoCard);
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
        
        // Botão voltar para sessões
        Button backButton = new Button("Voltar para Sessões");
        backButton.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-cursor: hand;");
        
        backButton.setOnAction(e -> {
            new SessoesViewModerna(teatro, usuario, stage).show();
        });
        
        // Espaçador para empurrar o userInfo para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(systemTitle, backButton, spacer, userInfo);
        
        return topBar;
    }
    
    private VBox createInfoCard() {
        VBox card = new VBox(20);
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 25;"
        );
        
        // Informações do evento
        VBox eventInfo = new VBox(5);
        
        Label eventoNome = new Label(sessao.getNome());
        eventoNome.setFont(Font.font("System", FontWeight.BOLD, 24));
        eventoNome.setTextFill(Color.web(TEXT_COLOR));
        
        // Informações da sessão (horário apenas)
        HBox sessaoInfo = new HBox(10);
        sessaoInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label horarioLabel = new Label("Horário:");
        horarioLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Label horarioValor = new Label(sessao.getHorario());
        horarioValor.setFont(Font.font("System", 14));
        
        sessaoInfo.getChildren().addAll(horarioLabel, horarioValor);
        
        eventInfo.getChildren().addAll(eventoNome, sessaoInfo);
        
        // Separador
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");
        
        // Seleção de área
        VBox areaSelectionBox = new VBox(15);
        
        Label areaLabel = new Label("Selecione a área do teatro:");
        areaLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        ComboBox<Area> areaComboBox = new ComboBox<>();
        areaComboBox.setPromptText("Selecione uma área");
        areaComboBox.setStyle("""
            -fx-pref-width: 300;
            -fx-background-color: white;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 4;
            """);
        
        // Cria uma lista ordenada das áreas
        List<Area> areasOrdenadas = new ArrayList<>(sessao.getAreas());
        Collections.sort(areasOrdenadas);
        areaComboBox.getItems().addAll(areasOrdenadas);
        
        areaComboBox.setOnAction(e -> {
            areaSelecionada = areaComboBox.getValue();
        });
        
        // Informações sobre a área selecionada
        VBox areaInfoBox = new VBox(10);
        areaInfoBox.setVisible(false);
        
        Label precoLabel = new Label("Preço por ingresso:");
        precoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Label precoValor = new Label();
        precoValor.setFont(Font.font("System", 14));
        
        Label disponibilidadeLabel = new Label("Poltronas disponíveis:");
        disponibilidadeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Label disponibilidadeValor = new Label();
        disponibilidadeValor.setFont(Font.font("System", 14));
        
        areaInfoBox.getChildren().addAll(precoLabel, precoValor, disponibilidadeLabel, disponibilidadeValor);
        
        // Atualiza as informações quando uma área é selecionada
        areaComboBox.setOnAction(e -> {
            areaSelecionada = areaComboBox.getValue();
            if (areaSelecionada != null) {
                precoValor.setText(String.format("R$ %.2f", areaSelecionada.getPreco()));
                disponibilidadeValor.setText(areaSelecionada.getPoltronasDisponiveis() + " de " + areaSelecionada.getCapacidadeTotal());
                areaInfoBox.setVisible(true);
            } else {
                areaInfoBox.setVisible(false);
            }
        });
        
        areaSelectionBox.getChildren().addAll(areaLabel, areaComboBox, areaInfoBox);
        
        // Botões de ação
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button voltarButton = new Button("Voltar");
        voltarButton.setStyle("""
            -fx-background-color: #95a5a6;
            -fx-text-fill: white;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-background-radius: 4;
            """);
        voltarButton.setOnAction(e -> {
            new SessoesViewModerna(teatro, usuario, stage).show();
        });
        
        Button continuarButton = new Button("Escolher Poltrona");
        continuarButton.setStyle(
            "-fx-background-color: " + SECONDARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10 20;" +
            "-fx-font-size: 14;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 4;" +
            "-fx-font-weight: bold;"
        );
        continuarButton.setOnAction(e -> {
            if (areaSelecionada != null) {
                new SelecionarPoltronaViewModerna(teatro, usuario, stage, sessao, areaSelecionada).show();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, selecione uma área.");
                alert.showAndWait();
            }
        });
        
        buttonBox.getChildren().addAll(voltarButton, continuarButton);
        
        card.getChildren().addAll(eventInfo, separator, areaSelectionBox, buttonBox);
        return card;
    }
}
