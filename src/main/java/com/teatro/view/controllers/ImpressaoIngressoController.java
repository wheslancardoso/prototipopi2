package com.teatro.view.controllers;

import com.teatro.model.*;
import com.teatro.service.IngressoService;
import com.teatro.view.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ImpressaoIngressoController implements Initializable {

    @FXML private Label impressao_lblUsuario;
    @FXML private VBox impressao_containerIngressos;
    @FXML private VBox impressao_containerVazio;
    @FXML private HBox impressao_containerBotoes;
    @FXML private Button impressao_btnComprarNovo;

    private Teatro teatro;
    private Usuario usuario;
    private SceneManager sceneManager;
    private List<IngressoModerno> ingressos;
    private IngressoService ingressoService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        teatro = sceneManager.getTeatro();
        usuario = sceneManager.getUsuarioLogado();
        ingressoService = IngressoService.getInstance();

        if (usuario != null) {
            impressao_lblUsuario.setText(usuario.getNome());
        }

        // Verificar se há ingressos passados pela navegação (compra recente)
        Object ingressosData = sceneManager.getUserData("ingressos_comprados");
        if (ingressosData instanceof List) {
            this.ingressos = (List<IngressoModerno>) ingressosData;
            // Limpar dados após usar
            sceneManager.setUserData("ingressos_comprados", null);
        } else {
            // Carregar ingressos do usuário do banco de dados
            carregarIngressosDoUsuario();
        }

        exibirIngressos();
    }

    private void carregarIngressosDoUsuario() {
        try {
            if (usuario != null) {
                List<Ingresso> ingressosBanco = ingressoService.buscarPorUsuario(usuario.getCpf());
                this.ingressos = new ArrayList<>();
                
                // Converter para IngressoModerno
                for (Ingresso ingresso : ingressosBanco) {
                    IngressoModerno ingressoModerno = new IngressoModerno(
                        ingresso.getId(),
                        ingresso.getEventoNome(),
                        ingresso.getHorario(),
                        ingresso.getDataSessao(),
                        ingresso.getAreaNome(),
                        ingresso.getNumeroPoltrona(),
                        ingresso.getValor(),
                        ingresso.getDataCompra(),
                        ingresso.getCodigo()
                    );
                    this.ingressos.add(ingressoModerno);
                }
                
                // Ordenar por data de compra (mais recentes primeiro)
                this.ingressos.sort((a, b) -> b.getDataCompra().compareTo(a.getDataCompra()));
            }
        } catch (Exception e) {
            this.ingressos = new ArrayList<>();
            mostrarErro("Erro ao carregar ingressos: " + e.getMessage());
        }
    }

    private void exibirIngressos() {
        if (ingressos == null || ingressos.isEmpty()) {
            exibirMensagemVazia();
        } else {
            exibirListaIngressos();
        }
    }

    private void exibirMensagemVazia() {
        impressao_containerIngressos.setVisible(false);
        impressao_containerIngressos.setManaged(false);
        impressao_containerBotoes.setVisible(false);
        impressao_containerBotoes.setManaged(false);
        
        impressao_containerVazio.setVisible(true);
        impressao_containerVazio.setManaged(true);
    }

    private void exibirListaIngressos() {
        impressao_containerVazio.setVisible(false);
        impressao_containerVazio.setManaged(false);
        
        impressao_containerIngressos.setVisible(true);
        impressao_containerIngressos.setManaged(true);
        impressao_containerBotoes.setVisible(true);
        impressao_containerBotoes.setManaged(true);

        // Limpar container
        impressao_containerIngressos.getChildren().clear();

        // Criar card para cada ingresso
        for (IngressoModerno ingresso : ingressos) {
            VBox cardIngresso = criarCardIngresso(ingresso);
            impressao_containerIngressos.getChildren().add(cardIngresso);
        }
    }

    private VBox criarCardIngresso(IngressoModerno ingresso) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("ingresso-card");

        // Título do evento
        Label eventoLabel = new Label(ingresso.getEventoNome());
        eventoLabel.getStyleClass().add("evento-title");

        // Separador
        Separator separator = new Separator();

        // Grid com informações do ingresso
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(10);
        infoGrid.getStyleClass().add("info-grid");

        // Horário
        Label horarioLabel = new Label("Horário:");
        horarioLabel.getStyleClass().add("info-title");
        Label horarioValor = new Label(ingresso.getHorario());
        horarioValor.getStyleClass().add("info-value");

        // Área
        Label areaLabel = new Label("Área:");
        areaLabel.getStyleClass().add("info-title");
        Label areaValor = new Label(ingresso.getAreaNome());
        areaValor.getStyleClass().add("info-value");

        // Poltrona
        Label poltronaLabel = new Label("Poltrona:");
        poltronaLabel.getStyleClass().add("info-title");
        Label poltronaValor = new Label(String.valueOf(ingresso.getNumeroPoltrona()));
        poltronaValor.getStyleClass().add("info-value");

        // Valor
        Label valorLabel = new Label("Valor:");
        valorLabel.getStyleClass().add("info-title");
        Label valorValor = new Label(String.format("R$ %.2f", ingresso.getValor()));
        valorValor.getStyleClass().add("info-value");

        // Código
        Label codigoLabel = new Label("Código:");
        codigoLabel.getStyleClass().add("info-title");
        Label codigoValor = new Label(ingresso.getCodigo());
        codigoValor.getStyleClass().addAll("info-value", "code");

        // Adicionar ao grid
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

        // Botão imprimir
        Button btnImprimir = new Button("Imprimir Ingresso");
        btnImprimir.getStyleClass().add("imprimir-button");
        btnImprimir.setOnAction(e -> imprimirIngresso(ingresso));

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(btnImprimir);

        card.getChildren().addAll(eventoLabel, separator, infoGrid, buttonBox);
        return card;
    }

    private void imprimirIngresso(IngressoModerno ingresso) {
        // Criar modal de impressão
        Stage impressaoStage = new Stage();
        impressaoStage.initModality(Modality.APPLICATION_MODAL);
        impressaoStage.setTitle("Impressão de Ingresso");

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("impressao-modal");

        // Área de texto do ingresso
        TextArea ingressoTexto = new TextArea();
        ingressoTexto.setEditable(false);
        ingressoTexto.setPrefWidth(400);
        ingressoTexto.setPrefHeight(300);
        ingressoTexto.getStyleClass().add("ingresso-texto");

        // Formatar texto do ingresso
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

        // Botão fechar
        Button btnFechar = new Button("Fechar");
        btnFechar.getStyleClass().add("fechar-button");
        btnFechar.setOnAction(e -> impressaoStage.close());

        root.getChildren().addAll(ingressoTexto, btnFechar);

        Scene scene = new Scene(root, 460, 500);
        impressaoStage.setScene(scene);
        impressaoStage.show();
    }

    @FXML
    private void handleComprarNovo() {
        sceneManager.goToSessoes();
    }

    @FXML
    private void handleNovaCompra() {
        sceneManager.goToSessoes();
    }

    @FXML
    private void handleImprimirTodos() {
        if (ingressos != null && !ingressos.isEmpty()) {
            for (IngressoModerno ingresso : ingressos) {
                imprimirIngresso(ingresso);
            }
        }
    }

    @FXML
    private void handleDashboard() {
        sceneManager.goToDashboard();
    }

    @FXML
    private void handleSair() {
        sceneManager.goToLogin();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}