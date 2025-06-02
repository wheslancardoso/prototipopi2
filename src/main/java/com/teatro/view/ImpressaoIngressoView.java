package com.teatro.view;

import com.teatro.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ImpressaoIngressoView {
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;

    public ImpressaoIngressoView(Teatro teatro, Usuario usuario, Stage stage) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Impressão de Ingressos");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ecf0f1;");

        // Container principal
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("""
            -fx-background-color: white;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            -fx-background-radius: 5;
            """);

        // Título
        Label titulo = new Label("Meus Ingressos");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Lista de ingressos
        VBox listaIngressos = new VBox(10);
        List<Ingresso> ingressos = teatro.buscarIngressosPorCpf(usuario.getCpf());

        if (ingressos.isEmpty()) {
            Label semIngressos = new Label("Você ainda não possui ingressos");
            semIngressos.setStyle("-fx-font-size: 16; -fx-text-fill: #7f8c8d;");
            listaIngressos.getChildren().add(semIngressos);
        } else {
            for (Ingresso ingresso : ingressos) {
                listaIngressos.getChildren().add(criarCardIngresso(ingresso));
            }
        }

        // ScrollPane para a lista de ingressos
        ScrollPane scrollPane = new ScrollPane(listaIngressos);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Botão Voltar
        Button voltarButton = new Button("Voltar");
        voltarButton.setMaxWidth(300);
        voltarButton.setStyle("""
            -fx-background-color: #95a5a6;
            -fx-text-fill: white;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-background-radius: 5;
            """);
        voltarButton.setOnAction(e -> {
            new DashboardView(teatro, usuario, stage).show();
        });

        container.getChildren().addAll(titulo, scrollPane, voltarButton);
        root.setCenter(container);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private VBox criarCardIngresso(Ingresso ingresso) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("""
            -fx-background-color: #f8f9fa;
            -fx-border-color: #dee2e6;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 0);
            """);

        // Título do evento com estilo destacado
        Label eventoLabel = new Label(ingresso.getEventoNome());
        eventoLabel.setStyle("""
            -fx-font-size: 18;
            -fx-font-weight: bold;
            -fx-text-fill: #2c3e50;
            """);

        // Grid para informações do ingresso
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(5);
        infoGrid.setPadding(new Insets(10, 0, 10, 0));

        // Estilo para os labels de título
        String titleStyle = """
            -fx-font-size: 14;
            -fx-font-weight: bold;
            -fx-text-fill: #7f8c8d;
            """;

        // Estilo para os valores
        String valueStyle = """
            -fx-font-size: 14;
            -fx-text-fill: #2c3e50;
            """;

        // Sessão
        Label sessaoTitle = new Label("Sessão:");
        Label sessaoValue = new Label(ingresso.getHorario());
        sessaoTitle.setStyle(titleStyle);
        sessaoValue.setStyle(valueStyle);
        infoGrid.add(sessaoTitle, 0, 0);
        infoGrid.add(sessaoValue, 1, 0);

        // Área
        Label areaTitle = new Label("Área:");
        Label areaValue = new Label(ingresso.getAreaNome());
        areaTitle.setStyle(titleStyle);
        areaValue.setStyle(valueStyle);
        infoGrid.add(areaTitle, 0, 1);
        infoGrid.add(areaValue, 1, 1);

        // Poltrona
        Label poltronaTitle = new Label("Poltrona:");
        Label poltronaValue = new Label(String.valueOf(ingresso.getNumeroPoltrona()));
        poltronaTitle.setStyle(titleStyle);
        poltronaValue.setStyle(valueStyle);
        infoGrid.add(poltronaTitle, 0, 2);
        infoGrid.add(poltronaValue, 1, 2);

        // Valor
        Label valorTitle = new Label("Valor:");
        Label valorValue = new Label(String.format("R$ %.2f", ingresso.getValor()));
        valorTitle.setStyle(titleStyle);
        valorValue.setStyle(valueStyle);
        infoGrid.add(valorTitle, 0, 3);
        infoGrid.add(valorValue, 1, 3);

        // Data da compra
        Label dataTitle = new Label("Data da Compra:");
        Label dataValue = new Label(ingresso.getDataCompra().toString());
        dataTitle.setStyle(titleStyle);
        dataValue.setStyle(valueStyle);
        infoGrid.add(dataTitle, 0, 4);
        infoGrid.add(dataValue, 1, 4);

        // Botão para imprimir
        Button imprimirButton = new Button("Imprimir Ingresso");
        imprimirButton.setStyle("""
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-background-radius: 5;
            -fx-min-width: 150;
            """);
        imprimirButton.setAlignment(Pos.CENTER);

        imprimirButton.setOnAction(e -> imprimirIngresso(ingresso));

        // Container para o botão
        HBox buttonBox = new HBox(imprimirButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        card.getChildren().addAll(
            eventoLabel,
            infoGrid,
            buttonBox
        );

        return card;
    }

    private void imprimirIngresso(Ingresso ingresso) {
        Stage impressaoStage = new Stage();
        impressaoStage.setTitle("Impressão do Ingresso");

        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: white;");

        // Conteúdo do ingresso
        TextArea ingressoTexto = new TextArea(ingresso.toString());
        ingressoTexto.setEditable(false);
        ingressoTexto.setWrapText(true);
        ingressoTexto.setStyle("""
            -fx-font-family: monospace;
            -fx-font-size: 14;
            """);

        Button fecharButton = new Button("Fechar");
        fecharButton.setOnAction(e -> impressaoStage.close());
        fecharButton.setStyle("""
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-background-radius: 5;
            """);

        root.getChildren().addAll(ingressoTexto, fecharButton);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 600, 400);
        impressaoStage.setScene(scene);
        impressaoStage.show();
    }
} 