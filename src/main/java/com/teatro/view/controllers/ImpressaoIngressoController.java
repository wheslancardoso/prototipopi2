package com.teatro.view.controllers;

import com.teatro.model.IngressoModerno;
import com.teatro.service.IngressoService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ImpressaoIngressoController extends BaseController {

    @FXML private Label impressao_lblUsuario;
    @FXML private VBox impressao_containerIngressos;
    @FXML private VBox impressao_containerVazio;
    @FXML private HBox impressao_containerBotoes;

    private List<IngressoModerno> ingressos;
    private IngressoService ingressoService;

    @Override
    protected void inicializarComponentes() {
        if (usuario != null) {
            impressao_lblUsuario.setText(usuario.getNome());
        }
        
        ingressoService = IngressoService.getInstance();
        ingressos = new ArrayList<>();
        
        // Ocultar containers inicialmente
        impressao_containerVazio.setVisible(false);
        impressao_containerVazio.setManaged(false);
        impressao_containerBotoes.setVisible(false);
        impressao_containerBotoes.setManaged(false);
    }

    @Override
    protected void configurarEventos() {
        // Eventos configurados nos botões via FXML
    }

    @Override
    protected void carregarDados() {
        // Verificar se há ingressos passados da compra
        Object ingressosData = sceneManager.getUserData("ingressos");
        if (ingressosData instanceof List) {
            this.ingressos = (List<IngressoModerno>) ingressosData;
        } else {
            // Carregar ingressos do usuário
            carregarIngressosUsuario();
        }
        
        exibirIngressos();
    }

    private void carregarIngressosUsuario() {
        try {
            if (usuario != null) {
                List<Ingresso> ingressosList = ingressoService.buscarPorUsuario(usuario.getCpf());
                ingressos = new ArrayList<>();
                
                for (Ingresso ingresso : ingressosList) {
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
                    ingressos.add(ingressoModerno);
                }
                
                // Ordenar por data de compra (mais recentes primeiro)
                ingressos.sort((a, b) -> b.getDataCompra().compareTo(a.getDataCompra()));
            }
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao carregar ingressos: " + e.getMessage());
        }
    }

    private void exibirIngressos() {
        impressao_containerIngressos.getChildren().clear();
        
        if (ingressos.isEmpty()) {
            mostrarContainerVazio();
        } else {
            mostrarIngressos();
        }
    }

    private void mostrarContainerVazio() {
        impressao_containerVazio.setVisible(true);
        impressao_containerVazio.setManaged(true);
        impressao_containerBotoes.setVisible(false);
        impressao_containerBotoes.setManaged(false);
    }

    private void mostrarIngressos() {
        for (IngressoModerno ingresso : ingressos) {
            VBox cardIngresso = criarCardIngresso(ingresso);
            impressao_containerIngressos.getChildren().add(cardIngresso);
        }
        
        impressao_containerVazio.setVisible(false);
        impressao_containerVazio.setManaged(false);
        impressao_containerBotoes.setVisible(true);
        impressao_containerBotoes.setManaged(true);
    }

    private VBox criarCardIngresso(IngressoModerno ingresso) {
        VBox card = new VBox(15);
        card.getStyleClass().add("ingresso-card");

        // Título do evento
        Label eventoLabel = new Label(ingresso.getEventoNome());
        eventoLabel.getStyleClass().add("evento-title");

        // Separador
        Separator separator = new Separator();

        // Grid com informações
        GridPane infoGrid = new GridPane();
        infoGrid.getStyleClass().add("info-grid");
        infoGrid.setHgap(20);
        infoGrid.setVgap(10);

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

        // Botão de imprimir
        Button imprimirButton = new Button("Imprimir Ingresso");
        imprimirButton.getStyleClass().add("imprimir-button");
        imprimirButton.setOnAction(e -> imprimirIngresso(ingresso));

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(imprimirButton);

        card.getChildren().addAll(eventoLabel, separator, infoGrid, buttonBox);
        return card;
    }

    private void imprimirIngresso(IngressoModerno ingresso) {
        // Criar modal de impressão
        Stage impressaoStage = new Stage();
        impressaoStage.initModality(Modality.APPLICATION_MODAL);
        impressaoStage.setTitle("Impressão de Ingresso");

        VBox root = new VBox(20);
        root.getStyleClass().add("impressao-modal");
        root.setAlignment(Pos.CENTER);

        // Texto do ingresso
        TextArea ingressoTexto = new TextArea();
        ingressoTexto.getStyleClass().add("ingresso-texto");
        ingressoTexto.setEditable(false);
        ingressoTexto.setPrefWidth(400);
        ingressoTexto.setPrefHeight(300);

        // Formatar texto
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
        Button fecharButton = new Button("Fechar");
        fecharButton.getStyleClass().add("fechar-button");
        fecharButton.setOnAction(e -> impressaoStage.close());

        root.getChildren().addAll(ingressoTexto, fecharButton);

        Scene scene = new Scene(root, 460, 500);
        scene.getStylesheets().add(getClass().getResource("/com/teatro/view/css/impressao-ingresso.css").toExternalForm());
        
        impressaoStage.setScene(scene);
        impressaoStage.show();
    }

    @FXML
    private void handleComprarNovo() {
        try {
            sceneManager.loadScene("/com/teatro/view/fxml/sessoes.fxml",
                                 "Sistema de Teatro - Sessões");
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao navegar para sessões: " + e.getMessage());
        }
    }

    @FXML
    private void handleImprimirTodos() {
        if (ingressos.isEmpty()) {
            mostrarErro("Atenção", "Não há ingressos para imprimir.");
            return;
        }

        // Criar modal com todos os ingressos
        Stage impressaoStage = new Stage();
        impressaoStage.initModality(Modality.APPLICATION_MODAL);
        impressaoStage.setTitle("Impressão de Todos os Ingressos");

        VBox root = new VBox(20);
        root.getStyleClass().add("impressao-modal");
        root.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(500, 400);

        TextArea ingressosTexto = new TextArea();
        ingressosTexto.getStyleClass().add("ingresso-texto");
        ingressosTexto.setEditable(false);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ingressos.size(); i++) {
            IngressoModerno ingresso = ingressos.get(i);
            
            sb.append("========================================\n");
            sb.append("           INGRESSO DE TEATRO           \n");
            sb.append("========================================\n\n");
            sb.append("Evento: ").append(ingresso.getEventoNome()).append("\n");
            sb.append("Horário: ").append(ingresso.getHorario()).append("\n");
            sb.append("Área: ").append(ingresso.getAreaNome()).append("\n");
            sb.append("Poltrona: ").append(ingresso.getNumeroPoltrona()).append("\n");
            sb.append("Valor: R$ ").append(String.format("%.2f", ingresso.getValor())).append("\n");
            sb.append("Código: ").append(ingresso.getCodigo()).append("\n");
            sb.append("========================================\n");
            
            if (i < ingressos.size() - 1) {
                sb.append("\n\n");
            }
        }

        ingressosTexto.setText(sb.toString());
        scrollPane.setContent(ingressosTexto);

        Button fecharButton = new Button("Fechar");
        fecharButton.getStyleClass().add("fechar-button");
        fecharButton.setOnAction(e -> impressaoStage.close());

        root.getChildren().addAll(scrollPane, fecharButton);

        Scene scene = new Scene(root, 550, 500);
        scene.getStylesheets().add(getClass().getResource("/com/teatro/view/css/impressao-ingresso.css").toExternalForm());
        
        impressaoStage.setScene(scene);
        impressaoStage.show();
    }

    @FXML
    private void handleNovaCompra() {
        handleComprarNovo();
    }
}