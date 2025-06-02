package com.teatro.view;

import com.teatro.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SessoesView {
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;

    public SessoesView(Teatro teatro, Usuario usuario, Stage stage) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Sessões Disponíveis");

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
        Label titulo = new Label("Sessões Disponíveis");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Lista de eventos
        VBox listaEventos = new VBox(20);
        for (Evento evento : teatro.getEventos()) {
            listaEventos.getChildren().add(criarCardEvento(evento));
        }

        ScrollPane scrollPane = new ScrollPane(listaEventos);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        // Botão Voltar
        Button voltarButton = new Button("Voltar");
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

    private VBox criarCardEvento(Evento evento) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("""
            -fx-background-color: #f8f9fa;
            -fx-border-color: #dee2e6;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            """);

        // Informações do evento
        Label nomeLabel = new Label("Espetáculo: " + evento.getNome());
        nomeLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // ComboBox para seleção de horário
        ComboBox<Sessao> horarioComboBox = new ComboBox<>();
        horarioComboBox.setPromptText("Selecione um horário");
        horarioComboBox.setMaxWidth(Double.MAX_VALUE);
        horarioComboBox.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #dee2e6;
            -fx-border-radius: 3;
            -fx-padding: 5;
            """);

        // Adiciona as sessões ao ComboBox
        for (Sessao sessao : evento.getSessoes()) {
            horarioComboBox.getItems().add(sessao);
        }

        // Customiza a exibição das sessões no ComboBox
        horarioComboBox.setCellFactory(lv -> new ListCell<Sessao>() {
            @Override
            protected void updateItem(Sessao sessao, boolean empty) {
                super.updateItem(sessao, empty);
                if (empty || sessao == null) {
                    setText(null);
                } else {
                    setText(sessao.getDataFormatada() + " - " + sessao.getHorario());
                }
            }
        });
        horarioComboBox.setButtonCell(horarioComboBox.getCellFactory().call(null));

        Button selecionarButton = new Button("Selecionar Horário");
        selecionarButton.setStyle("""
            -fx-background-color: #2ecc71;
            -fx-text-fill: white;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-background-radius: 5;
            """);
        selecionarButton.setDisable(true);

        // Habilita o botão apenas quando um horário é selecionado
        horarioComboBox.setOnAction(e -> {
            selecionarButton.setDisable(horarioComboBox.getValue() == null);
        });

        selecionarButton.setOnAction(e -> {
            Sessao sessaoSelecionada = horarioComboBox.getValue();
            if (sessaoSelecionada != null) {
                new CompraIngressoView(teatro, usuario, stage, sessaoSelecionada).show();
            }
        });

        card.getChildren().addAll(nomeLabel, horarioComboBox, selecionarButton);
        return card;
    }
} 