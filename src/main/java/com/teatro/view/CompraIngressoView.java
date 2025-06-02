package com.teatro.view;

import com.teatro.model.*;
import javafx.geometry.Insets;
import com.teatro.view.SessoesViewModerna;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompraIngressoView {
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private Sessao sessao;
    private Area areaSelecionada;
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;

    public CompraIngressoView(Teatro teatro, Usuario usuario, Stage stage, Sessao sessao) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
        this.sessao = sessao;
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Compra de Ingressos");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ecf0f1;");

        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("""
            -fx-background-color: white;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            -fx-background-radius: 5;
            """);

        // Título
        Label titulo = new Label("Compra de Ingressos");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Informações da sessão
        VBox infoSessao = new VBox(10);
        Label nomeEvento = new Label(sessao.getNome());
        nomeEvento.setStyle("-fx-font-size: 18;");
        Label dataHora = new Label(sessao.getDataFormatada() + " - " + sessao.getHorario());
        dataHora.setStyle("-fx-font-size: 16;");
        infoSessao.getChildren().addAll(nomeEvento, dataHora);

        // Seleção de área
        VBox areaBox = new VBox(10);
        Label areaLabel = new Label("Selecione a área:");
        areaLabel.setStyle("-fx-font-size: 16;");

        ComboBox<Area> areaComboBox = new ComboBox<>();
        areaComboBox.setStyle("""
            -fx-pref-width: 300;
            -fx-font-size: 14;
            """);

        // Cria uma lista ordenada das áreas
        List<Area> areasOrdenadas = new ArrayList<>(sessao.getAreas());
        Collections.sort(areasOrdenadas);
        areaComboBox.getItems().addAll(areasOrdenadas);

        areaComboBox.setOnAction(e -> {
            areaSelecionada = areaComboBox.getValue();
        });

        areaBox.getChildren().addAll(areaLabel, areaComboBox);

        // Botões de ação
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER);

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
            new SessoesViewModerna(teatro, usuario, stage).show();
        });

        Button escolherPoltronaButton = new Button("Escolher Poltrona");
        escolherPoltronaButton.setStyle("""
            -fx-background-color: #2ecc71;
            -fx-text-fill: white;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-background-radius: 5;
            """);
        escolherPoltronaButton.setOnAction(e -> {
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

        botoesBox.getChildren().addAll(voltarButton, escolherPoltronaButton);

        container.getChildren().addAll(titulo, infoSessao, areaBox, botoesBox);
        root.setCenter(container);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private VBox criarCardFilme(Evento evento) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("""
            -fx-background-color: #f8f9fa;
            -fx-border-color: #dee2e6;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            """);
        card.setAlignment(Pos.CENTER);

        // Imagem do filme (placeholder)
        Rectangle poster = new Rectangle(180, 250);
        poster.setStyle("-fx-fill: #ddd;");

        Label titulo = new Label(evento.getNome());
        titulo.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        card.getChildren().addAll(poster, titulo);
        return card;
    }
} 