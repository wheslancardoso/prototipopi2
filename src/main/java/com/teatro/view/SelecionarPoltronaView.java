package com.teatro.view;

import com.teatro.model.*;
import com.teatro.controller.SessaoController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class SelecionarPoltronaView {
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private Sessao sessao;
    private Area area;
    private SessaoController sessaoController;
    private List<ToggleButton> poltronas;
    private Label totalLabel;
    private double valorTotal = 0.0;
    private Integer poltronaSelecionada = null;
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;

    public SelecionarPoltronaView(Teatro teatro, Usuario usuario, Stage stage, Sessao sessao, Area area) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
        this.sessao = sessao;
        this.area = area;
        this.sessaoController = new SessaoController();
        this.poltronas = new ArrayList<>();
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Seleção de Poltronas");

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

        // Informações da sessão
        VBox infoBox = new VBox(10);
        Label infoLabel = new Label(String.format("Sessão: %s - %s", sessao.getNome(), sessao.getHorario()));
        Label areaLabel = new Label("Área: " + area.getNome());
        totalLabel = new Label("Poltrona selecionada: Nenhuma");
        infoBox.getChildren().addAll(infoLabel, areaLabel, totalLabel);

        // Grid de poltronas
        GridPane gridPoltronas = new GridPane();
        gridPoltronas.setHgap(5);
        gridPoltronas.setVgap(5);
        gridPoltronas.setAlignment(Pos.CENTER);

        int colunas = 10;
        for (int i = 0; i < area.getCapacidadeTotal(); i++) {
            ToggleButton poltrona = new ToggleButton(String.format("%d", i + 1));
            poltrona.setPrefSize(50, 50);
            
            if (!area.getPoltronasDisponiveisList().contains(i + 1)) {
                poltrona.setDisable(true);
                poltrona.setStyle("-fx-background-color: #e74c3c;");
            } else {
                poltrona.setStyle("""
                    -fx-background-color: #2ecc71;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    """);
            }

            final int numeroPoltrona = i + 1;
            poltrona.setOnAction(e -> {
                // Desmarca todas as outras poltronas
                for (ToggleButton outraPoltrona : poltronas) {
                    if (outraPoltrona != poltrona) {
                        outraPoltrona.setSelected(false);
                    }
                }

                if (poltrona.isSelected()) {
                    valorTotal = area.getPreco();
                    poltronaSelecionada = numeroPoltrona;
                } else {
                    valorTotal = 0.0;
                    poltronaSelecionada = null;
                }
                atualizarTotal();
            });

            gridPoltronas.add(poltrona, i % colunas, i / colunas);
            poltronas.add(poltrona);
        }

        ScrollPane scrollPane = new ScrollPane(gridPoltronas);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

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
            new CompraIngressoView(teatro, usuario, stage, sessao).show();
        });

        Button confirmarButton = new Button("Confirmar");
        confirmarButton.setStyle("""
            -fx-background-color: #2ecc71;
            -fx-text-fill: white;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-background-radius: 5;
            """);
        confirmarButton.setOnAction(e -> confirmarSelecao());

        botoesBox.getChildren().addAll(voltarButton, confirmarButton);

        container.getChildren().addAll(infoBox, scrollPane, botoesBox);
        root.setCenter(container);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private void atualizarTotal() {
        if (poltronaSelecionada != null) {
            totalLabel.setText(String.format("Poltrona selecionada: %d", poltronaSelecionada));
        } else {
            totalLabel.setText("Poltrona selecionada: Nenhuma");
        }
    }

    private void confirmarSelecao() {
        if (poltronaSelecionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecione uma poltrona.");
            alert.showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Compra");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(String.format("Confirmar a compra da poltrona %d por R$ %.2f?", 
            poltronaSelecionada, valorTotal));

        if (confirmacao.showAndWait().get() == ButtonType.OK) {
            processarCompra();
        }
    }

    private void processarCompra() {
        if (poltronaSelecionada != null) {
            teatro.comprarIngresso(usuario.getCpf(), null, sessao, area, poltronaSelecionada);
        }
        
        new ImpressaoIngressoView(teatro, usuario, stage).show();
    }
} 