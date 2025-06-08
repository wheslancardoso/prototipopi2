package com.teatro.view;

import com.teatro.model.IngressoModerno;
import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Versão modernizada da tela de impressão de ingressos.
 */
public class ImpressaoIngressoView {
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private List<IngressoModerno> ingressos;
    
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;
    
    // Cores do tema
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#2ecc71";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String CARD_BACKGROUND = "white";

    public ImpressaoIngressoView(Teatro teatro, Usuario usuario, Stage stage, List<IngressoModerno> ingressos) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
        ingressos.sort((a, b) -> b.getDataCompra().compareTo(a.getDataCompra()));
        this.ingressos = ingressos;
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Impressão de Ingressos");

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
        Label pageTitle = new Label("Compra Realizada com Sucesso!");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        pageTitle.setTextFill(Color.web(SECONDARY_COLOR));
        
        // Mensagem de sucesso
        Label mensagemSucesso = new Label("Seus ingressos estão prontos para impressão.");
        mensagemSucesso.setFont(Font.font("System", 16));
        mensagemSucesso.setTextFill(Color.web(TEXT_COLOR));
        
        // Container para os ingressos
        VBox ingressosContainer = new VBox(20);
        
        // Cria um card para cada ingresso
        for (IngressoModerno ingresso : ingressos) {
            ingressosContainer.getChildren().add(criarCardIngresso(ingresso));
        }
        
        // Botões de ação
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        botoesBox.setPadding(new Insets(20, 0, 0, 0));
        
        contentContainer.getChildren().addAll(pageTitle, mensagemSucesso, ingressosContainer, botoesBox);
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
        
        // Botão Dashboard
        Button dashboardButton = new Button("Dashboard");
        dashboardButton.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-cursor: hand;");
        dashboardButton.setOnAction(e -> {
            new DashboardView(teatro, usuario, stage).show();
        });
        
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
        
        // Espaçador para empurrar o userInfo para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(systemTitle, dashboardButton, spacer, userInfo);
        
        return topBar;
    }
    
    private VBox criarCardIngresso(IngressoModerno ingresso) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 8;");
        
        // Título do evento
        Label eventoLabel = new Label(ingresso.getEventoNome());
        eventoLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        eventoLabel.setTextFill(Color.web(TEXT_COLOR));
        
        // Separador
        Separator separator = new Separator();
        
        // Grid com informações do ingresso
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(10);
        
        String titleStyle = "-fx-font-weight: bold; -fx-text-fill: " + TEXT_COLOR + ";";
        String valueStyle = "-fx-text-fill: " + TEXT_COLOR + ";";
        
        // Horário
        Label horarioLabel = new Label("Horário:");
        horarioLabel.setStyle(titleStyle);
        
        Label horarioValor = new Label(ingresso.getHorario());
        horarioValor.setStyle(valueStyle);
        
        // Área
        Label areaLabel = new Label("Área:");
        areaLabel.setStyle(titleStyle);
        
        Label areaValor = new Label(ingresso.getAreaNome());
        areaValor.setStyle(valueStyle);
        
        // Poltrona
        Label poltronaLabel = new Label("Poltrona:");
        poltronaLabel.setStyle(titleStyle);
        
        Label poltronaValor = new Label(String.valueOf(ingresso.getNumeroPoltrona()));
        poltronaValor.setStyle(valueStyle);
        
        // Valor
        Label valorLabel = new Label("Valor:");
        valorLabel.setStyle(titleStyle);
        
        Label valorValor = new Label(String.format("R$ %.2f", ingresso.getValor()));
        valorValor.setStyle(valueStyle);
        
        // Código
        Label codigoLabel = new Label("Código:");
        codigoLabel.setStyle(titleStyle);
        
        Label codigoValor = new Label(ingresso.getCodigo());
        codigoValor.setStyle("-fx-font-family: monospace; " + valueStyle);
        
        // Adiciona os itens ao grid
        infoGrid.add(horarioLabel, 0, 0);
        infoGrid.add(horarioValor, 1, 0);
        infoGrid.add(areaLabel, 0, 1);
        infoGrid.add(areaValor, 1, 1);
        infoGrid.add(poltronaLabel, 0, 2);
        infoGrid.add(poltronaValor, 1, 2);
        infoGrid.add(valorLabel, 2, 0);
        infoGrid.add(valorValor, 3, 0);
        infoGrid.add(codigoLabel, 2, 1);
        infoGrid.add(codigoValor, 3, 1);
        
        // Botão para imprimir
        Button imprimirButton = new Button("Imprimir Ingresso");
        imprimirButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14; -fx-cursor: hand; -fx-background-radius: 4;");
        
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(imprimirButton);
        
        imprimirButton.setOnAction(e -> imprimirIngresso(ingresso));
        
        card.getChildren().addAll(eventoLabel, separator, infoGrid, buttonBox);
        return card;
    }
    
    private void imprimirIngresso(IngressoModerno ingresso) {
        // Cria uma nova janela para simular a impressão
        Stage impressaoStage = new Stage();
        impressaoStage.initModality(Modality.APPLICATION_MODAL);
        impressaoStage.setTitle("Impressão de Ingresso");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");
        
        // Texto do ingresso formatado
        TextArea ingressoTexto = new TextArea();
        ingressoTexto.setEditable(false);
        ingressoTexto.setPrefWidth(400);
        ingressoTexto.setPrefHeight(300);
        ingressoTexto.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
        
        // Formata o texto do ingresso
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           INGRESSO DE TEATRO           \n");
        sb.append("========================================\n\n");
        
        sb.append("Evento: ").append(ingresso.getEventoNome()).append("\n");
        sb.append("Horário: ").append(ingresso.getHorario()).append("\n");
        sb.append("Área: ").append(ingresso.getAreaNome()).append("\n");
        sb.append("Poltrona: ").append(ingresso.getNumeroPoltrona()).append("\n");
        sb.append("Valor: R$ ").append(String.format("%.2f", ingresso.getValor())).append("\n");
        sb.append("Código: ").append(ingresso.getCodigo()).append("\n");
        
        try {
            sb.append("Data da Compra: ").append(
                ingresso.getDataCompra().toLocalDateTime().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                )
            ).append("\n");
        } catch (Exception e) {
            sb.append("Data da Compra: Não disponível\n");
        }
        
        sb.append("\nObrigado pela sua compra!\n");
        sb.append("========================================\n");
        
        ingressoTexto.setText(sb.toString());
        
        // Botão de fechar
        Button fecharButton = new Button("Fechar");
        fecharButton.setStyle(
            "-fx-background-color: " + SECONDARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 20; " +
            "-fx-font-size: 14; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 4;"
        );
        fecharButton.setOnAction(e -> impressaoStage.close());
        
        // Adiciona os componentes ao layout
        root.getChildren().addAll(ingressoTexto, fecharButton);
        
        // Configura e exibe a cena
        Scene scene = new Scene(root, 460, 500);
        impressaoStage.setScene(scene);
        impressaoStage.show();
    }
}
