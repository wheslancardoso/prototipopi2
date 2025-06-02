package com.teatro.view;

import com.teatro.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Versão modernizada da tela de seleção de poltronas.
 */
public class SelecionarPoltronaViewModerna {
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private Sessao sessao;
    private Area area;
    private List<Poltrona> poltronasSelecionadas;
    
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;
    
    // Cores do tema
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#2ecc71";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String CARD_BACKGROUND = "white";
    
    // Cores das poltronas
    private static final String POLTRONA_DISPONIVEL = "#2ecc71";
    private static final String POLTRONA_OCUPADA = "#e74c3c";
    private static final String POLTRONA_SELECIONADA = "#3498db";

    public SelecionarPoltronaViewModerna(Teatro teatro, Usuario usuario, Stage stage, Sessao sessao, Area area) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
        this.sessao = sessao;
        this.area = area;
        this.poltronasSelecionadas = new ArrayList<>();
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Seleção de Poltronas");

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
        Label pageTitle = new Label("Seleção de Poltronas");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        pageTitle.setTextFill(Color.web(TEXT_COLOR));
        
        // Informações da sessão e área
        HBox infoBox = new HBox(30);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        // Informações do evento
        VBox eventoInfo = new VBox(5);
        Label eventoLabel = new Label("Evento:");
        eventoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label eventoNome = new Label(sessao.getNome());
        eventoNome.setFont(Font.font("System", 14));
        eventoInfo.getChildren().addAll(eventoLabel, eventoNome);
        
        // Informações da sessão
        VBox sessaoInfo = new VBox(5);
        Label sessaoLabel = new Label("Sessão:");
        sessaoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label sessaoHorario = new Label(sessao.getDataFormatada() + " - " + sessao.getHorarioCompleto());
        sessaoHorario.setFont(Font.font("System", 14));
        sessaoInfo.getChildren().addAll(sessaoLabel, sessaoHorario);
        
        // Informações da área
        VBox areaInfo = new VBox(5);
        Label areaLabel = new Label("Área:");
        areaLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label areaNome = new Label(area.getNome() + " - R$ " + String.format("%.2f", area.getPreco()));
        areaNome.setFont(Font.font("System", 14));
        areaInfo.getChildren().addAll(areaLabel, areaNome);
        
        infoBox.getChildren().addAll(eventoInfo, sessaoInfo, areaInfo);
        
        // Legenda das poltronas
        HBox legendaBox = new HBox(20);
        legendaBox.setAlignment(Pos.CENTER);
        legendaBox.setPadding(new Insets(20, 0, 20, 0));
        
        // Disponível
        HBox disponivelLegenda = new HBox(10);
        disponivelLegenda.setAlignment(Pos.CENTER_LEFT);
        Rectangle disponivelRect = new Rectangle(20, 20);
        disponivelRect.setFill(Color.web(POLTRONA_DISPONIVEL));
        disponivelRect.setArcWidth(5);
        disponivelRect.setArcHeight(5);
        Label disponivelLabel = new Label("Disponível");
        disponivelLabel.setFont(Font.font("System", 14));
        disponivelLegenda.getChildren().addAll(disponivelRect, disponivelLabel);
        
        // Ocupada
        HBox ocupadaLegenda = new HBox(10);
        ocupadaLegenda.setAlignment(Pos.CENTER_LEFT);
        Rectangle ocupadaRect = new Rectangle(20, 20);
        ocupadaRect.setFill(Color.web(POLTRONA_OCUPADA));
        ocupadaRect.setArcWidth(5);
        ocupadaRect.setArcHeight(5);
        Label ocupadaLabel = new Label("Ocupada");
        ocupadaLabel.setFont(Font.font("System", 14));
        ocupadaLegenda.getChildren().addAll(ocupadaRect, ocupadaLabel);
        
        // Selecionada
        HBox selecionadaLegenda = new HBox(10);
        selecionadaLegenda.setAlignment(Pos.CENTER_LEFT);
        Rectangle selecionadaRect = new Rectangle(20, 20);
        selecionadaRect.setFill(Color.web(POLTRONA_SELECIONADA));
        selecionadaRect.setArcWidth(5);
        selecionadaRect.setArcHeight(5);
        Label selecionadaLabel = new Label("Selecionada");
        selecionadaLabel.setFont(Font.font("System", 14));
        selecionadaLegenda.getChildren().addAll(selecionadaRect, selecionadaLabel);
        
        legendaBox.getChildren().addAll(disponivelLegenda, ocupadaLegenda, selecionadaLegenda);
        
        // Mapa de poltronas
        VBox mapaContainer = new VBox(20);
        mapaContainer.setAlignment(Pos.CENTER);
        mapaContainer.setPadding(new Insets(20));
        mapaContainer.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 8;");
        
        // Tela
        Rectangle tela = new Rectangle(600, 20);
        tela.setFill(Color.web("#95a5a6"));
        tela.setArcWidth(10);
        tela.setArcHeight(10);
        
        Label telaLabel = new Label("TELA");
        telaLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        telaLabel.setTextFill(Color.WHITE);
        
        StackPane telaStack = new StackPane();
        telaStack.getChildren().addAll(tela, telaLabel);
        telaStack.setPadding(new Insets(0, 0, 30, 0));
        
        // Grid de poltronas
        GridPane poltronasGrid = new GridPane();
        poltronasGrid.setAlignment(Pos.CENTER);
        poltronasGrid.setHgap(10);
        poltronasGrid.setVgap(10);
        
        // Número de linhas e colunas para o grid
        int numLinhas = 5;
        int numColunas = 10;
        
        // Cria as poltronas
        for (int i = 0; i < numLinhas; i++) {
            for (int j = 0; j < numColunas; j++) {
                int numero = i * numColunas + j + 1;
                
                // Cria a poltrona
                Button poltrona = new Button(String.valueOf(numero));
                poltrona.setPrefSize(50, 50);
                poltrona.setFont(Font.font("System", FontWeight.BOLD, 14));
                
                // Verifica se a poltrona está ocupada no banco de dados
                boolean ocupada = teatro.isPoltronaOcupada(sessao.getId(), area.getId(), numero);
                
                if (ocupada) {
                    poltrona.setStyle("-fx-background-color: " + POLTRONA_OCUPADA + "; -fx-text-fill: white; -fx-background-radius: 5;");
                    poltrona.setDisable(true);
                } else {
                    poltrona.setStyle("-fx-background-color: " + POLTRONA_DISPONIVEL + "; -fx-text-fill: white; -fx-background-radius: 5;");
                    
                    // Adiciona evento de clique
                    poltrona.setOnAction(e -> {
                        if (poltrona.getStyle().contains(POLTRONA_DISPONIVEL)) {
                            // Seleciona a poltrona
                            poltrona.setStyle("-fx-background-color: " + POLTRONA_SELECIONADA + "; -fx-text-fill: white; -fx-background-radius: 5;");
                            poltronasSelecionadas.add(new Poltrona(numero, area));
                        } else {
                            // Desseleciona a poltrona
                            poltrona.setStyle("-fx-background-color: " + POLTRONA_DISPONIVEL + "; -fx-text-fill: white; -fx-background-radius: 5;");
                            poltronasSelecionadas.removeIf(p -> p.getNumero() == numero);
                        }
                        
                        // Atualiza o resumo
                        atualizarResumo();
                    });
                }
                
                poltronasGrid.add(poltrona, j, i);
            }
        }
        
        mapaContainer.getChildren().addAll(telaStack, poltronasGrid);
        
        // Resumo da compra
        VBox resumoContainer = new VBox(15);
        resumoContainer.setPadding(new Insets(20));
        resumoContainer.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 8;");
        
        Label resumoTitulo = new Label("Resumo da Compra");
        resumoTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        resumoTitulo.setTextFill(Color.web(TEXT_COLOR));
        
        // Quantidade de ingressos
        HBox qtdBox = new HBox(10);
        qtdBox.setAlignment(Pos.CENTER_LEFT);
        Label qtdLabel = new Label("Quantidade de ingressos:");
        qtdLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        qtdValor = new Label("0");
        qtdValor.setFont(Font.font("System", 14));
        qtdBox.getChildren().addAll(qtdLabel, qtdValor);
        
        // Valor unitário
        HBox valorUnitBox = new HBox(10);
        valorUnitBox.setAlignment(Pos.CENTER_LEFT);
        Label valorUnitLabel = new Label("Valor unitário:");
        valorUnitLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label valorUnitValor = new Label("R$ " + String.format("%.2f", area.getPreco()));
        valorUnitValor.setFont(Font.font("System", 14));
        valorUnitBox.getChildren().addAll(valorUnitLabel, valorUnitValor);
        
        // Valor total
        HBox valorTotalBox = new HBox(10);
        valorTotalBox.setAlignment(Pos.CENTER_LEFT);
        Label valorTotalLabel = new Label("Valor total:");
        valorTotalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        valorTotalValor = new Label("R$ 0,00");
        valorTotalValor.setFont(Font.font("System", FontWeight.BOLD, 16));
        valorTotalValor.setTextFill(Color.web(SECONDARY_COLOR));
        valorTotalBox.getChildren().addAll(valorTotalLabel, valorTotalValor);
        
        // Botões
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        botoesBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button voltarButton = new Button("Voltar");
        voltarButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14; -fx-cursor: hand; -fx-background-radius: 4;");
        
        voltarButton.setOnAction(e -> {
            new CompraIngressoViewModerna(teatro, usuario, stage, sessao).show();
        });
        
        confirmarButton = new Button("Confirmar Compra");
        confirmarButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14; -fx-cursor: hand; -fx-background-radius: 4; -fx-font-weight: bold;");
        confirmarButton.setDisable(true);
        
        confirmarButton.setOnAction(e -> {
            // Cria os ingressos modernos
            List<IngressoModerno> ingressosModernos = new ArrayList<>();
            for (Poltrona poltrona : poltronasSelecionadas) {
                IngressoModerno ingresso = new IngressoModerno(sessao, area, poltrona, usuario);
                ingressosModernos.add(ingresso);
            }
            
            // Cria o callback para quando o pagamento for concluído
            Runnable onPagamentoConcluido = () -> {
                // Adiciona os ingressos ao usuário
                usuario.adicionarIngressos(ingressosModernos);
                
                // Atualiza o faturamento da sessão
                double valorTotal = poltronasSelecionadas.size() * area.getPreco();
                sessao.adicionarFaturamento(valorTotal);
                
                // Mostra a tela de impressão de ingressos
                new ImpressaoIngressoViewModerna(teatro, usuario, stage, ingressosModernos).show();
            };
            
            // Abre a tela de seleção de método de pagamento
            SelecionarMetodoPagamentoView metodoPagamentoView = new SelecionarMetodoPagamentoView(
                stage,
                usuario,
                ingressosModernos,
                onPagamentoConcluido
            );
            
            // Cria uma nova cena com a tela de seleção de método de pagamento
            Scene cenaPagamento = new Scene(metodoPagamentoView, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(cenaPagamento);
        });
        
        botoesBox.getChildren().addAll(voltarButton, confirmarButton);
        
        resumoContainer.getChildren().addAll(resumoTitulo, qtdBox, valorUnitBox, valorTotalBox, botoesBox);
        
        contentContainer.getChildren().addAll(pageTitle, infoBox, legendaBox, mapaContainer, resumoContainer);
        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        
        // Atualiza o resumo inicial
        atualizarResumo();
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
        
        // Botão voltar
        Button backButton = new Button("Voltar para Seleção de Área");
        backButton.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-cursor: hand;");
        
        backButton.setOnAction(e -> {
            new CompraIngressoViewModerna(teatro, usuario, stage, sessao).show();
        });
        
        // Espaçador para empurrar o userInfo para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(systemTitle, backButton, spacer, userInfo);
        
        return topBar;
    }
    
    // Referências para os componentes da UI que precisam ser atualizados
    private Label qtdValor;
    private Label valorTotalValor;
    private Button confirmarButton;
    
    private void atualizarResumo() {
        // Atualiza a quantidade de ingressos
        qtdValor.setText(String.valueOf(poltronasSelecionadas.size()));
        
        // Calcula e atualiza o valor total
        double valorTotal = poltronasSelecionadas.size() * area.getPreco();
        valorTotalValor.setText(String.format("R$ %.2f", valorTotal));
        
        // Habilita/desabilita o botão de confirmar com base na seleção
        confirmarButton.setDisable(poltronasSelecionadas.isEmpty());
    }
}
