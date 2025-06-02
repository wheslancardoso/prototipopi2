package com.teatro.view;

import com.teatro.model.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;

public class EstatisticasView {
    private Teatro teatro;
    private Stage stage;

    public EstatisticasView(Teatro teatro, Stage stage) {
        this.teatro = teatro;
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Estatísticas");

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
        Label titulo = new Label("Estatísticas do Teatro");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Seções de estatísticas
        VBox estatisticasVendas = criarSecaoEstatisticasVendas();
        VBox estatisticasLucratividade = criarSecaoEstatisticasLucratividade();

        container.getChildren().addAll(titulo, estatisticasVendas, estatisticasLucratividade);
        root.setCenter(container);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
    }

    private VBox criarSecaoEstatisticasVendas() {
        VBox secao = new VBox(15);
        secao.setPadding(new Insets(20));
        secao.setStyle("""
            -fx-background-color: #f8f9fa;
            -fx-border-color: #dee2e6;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            """);

        Label tituloSecao = new Label("Estatísticas de Vendas");
        tituloSecao.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Map<String, Integer> estatisticas = teatro.getEstatisticasVendas();

        // Cria cards para cada estatística
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        // Evento com mais ingressos vendidos
        VBox cardMaisVendido = criarCardEstatistica(
            "Evento Mais Vendido",
            estatisticas.getOrDefault("eventoMaisVendido", 0) + " ingressos"
        );
        grid.add(cardMaisVendido, 0, 0);

        // Evento com menos ingressos vendidos
        VBox cardMenosVendido = criarCardEstatistica(
            "Evento Menos Vendido",
            estatisticas.getOrDefault("eventoMenosVendido", 0) + " ingressos"
        );
        grid.add(cardMenosVendido, 1, 0);

        // Sessão com maior ocupação
        VBox cardMaiorOcupacao = criarCardEstatistica(
            "Sessão com Maior Ocupação",
            estatisticas.getOrDefault("sessaoMaisOcupada", 0) + " poltronas"
        );
        grid.add(cardMaiorOcupacao, 0, 1);

        // Sessão com menor ocupação
        VBox cardMenorOcupacao = criarCardEstatistica(
            "Sessão com Menor Ocupação",
            estatisticas.getOrDefault("sessaoMenosOcupada", 0) + " poltronas"
        );
        grid.add(cardMenorOcupacao, 1, 1);

        secao.getChildren().addAll(tituloSecao, grid);
        return secao;
    }

    private VBox criarSecaoEstatisticasLucratividade() {
        VBox secao = new VBox(15);
        secao.setPadding(new Insets(20));
        secao.setStyle("""
            -fx-background-color: #f8f9fa;
            -fx-border-color: #dee2e6;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            """);

        Label tituloSecao = new Label("Estatísticas de Lucratividade");
        tituloSecao.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Map<String, Double> estatisticas = teatro.getEstatisticasLucratividade();

        // Cria cards para cada estatística
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        // Evento mais lucrativo
        VBox cardMaisLucrativo = criarCardEstatistica(
            "Evento Mais Lucrativo",
            String.format("R$ %.2f", estatisticas.getOrDefault("eventoMaisLucrativo", 0.0))
        );
        grid.add(cardMaisLucrativo, 0, 0);

        // Evento menos lucrativo
        VBox cardMenosLucrativo = criarCardEstatistica(
            "Evento Menos Lucrativo",
            String.format("R$ %.2f", estatisticas.getOrDefault("eventoMenosLucrativo", 0.0))
        );
        grid.add(cardMenosLucrativo, 1, 0);

        // Sessão mais lucrativa
        VBox cardSessaoMaisLucrativa = criarCardEstatistica(
            "Sessão Mais Lucrativa",
            String.format("R$ %.2f", estatisticas.getOrDefault("sessaoMaisLucrativa", 0.0))
        );
        grid.add(cardSessaoMaisLucrativa, 0, 1);

        // Sessão menos lucrativa
        VBox cardSessaoMenosLucrativa = criarCardEstatistica(
            "Sessão Menos Lucrativa",
            String.format("R$ %.2f", estatisticas.getOrDefault("sessaoMenosLucrativa", 0.0))
        );
        grid.add(cardSessaoMenosLucrativa, 1, 1);

        // Lucro médio por área
        VBox cardLucroMedio = criarCardEstatistica(
            "Lucro Médio por Área",
            String.format("R$ %.2f", estatisticas.getOrDefault("lucroMedioPorArea", 0.0))
        );
        grid.add(cardLucroMedio, 0, 2, 2, 1);

        secao.getChildren().addAll(tituloSecao, grid);
        return secao;
    }

    private VBox criarCardEstatistica(String titulo, String valor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e9ecef;
            -fx-border-radius: 5;
            -fx-background-radius: 5;
            -fx-min-width: 200;
            """);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 14; -fx-text-fill: #6c757d;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }
} 