package com.teatro.view;

import com.teatro.model.Area;
import com.teatro.model.Evento;
import com.teatro.model.IngressoModerno;
import com.teatro.model.Poltrona;
import com.teatro.model.Sessao;
import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
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
import java.util.Optional;

/**
 * Versão modernizada da tela de seleção de poltronas.
 */
public class SelecionarPoltronaView {
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private Sessao sessao;
    private Area area;
    private List<Poltrona> poltronasSelecionadas;
    private List<Integer> poltronasDisponiveis;
    
    // Componentes da interface que precisam ser acessados em múltiplos métodos
    private Label qtdValor;
    private Label valorTotalValor;
    private Button confirmarButton;
    
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

    public SelecionarPoltronaView(Teatro teatro, Usuario usuario, Stage stage, Sessao sessao, Area area) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
        this.sessao = sessao;
        this.area = area;
        this.poltronasSelecionadas = new ArrayList<>();
        this.poltronasDisponiveis = new ArrayList<>();
        
        try {
            // Busca as poltronas disponíveis para a área na sessão
            this.poltronasDisponiveis = teatro.getPoltronasDisponiveis(sessao, area);
            if (this.poltronasDisponiveis.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Área lotada");
                alert.setContentText("Esta área não possui mais poltronas disponíveis.");
                alert.showAndWait();
                new CompraIngressoView(teatro, usuario, stage, sessao).show();
                return;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao buscar poltronas disponíveis");
            alert.setContentText("Ocorreu um erro ao buscar as poltronas disponíveis. Por favor, tente novamente.");
            alert.showAndWait();
            new CompraIngressoView(teatro, usuario, stage, sessao).show();
            return;
        }
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
        Label sessaoLabel = new Label("Horário:");
        sessaoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label sessaoHorario = new Label(sessao.getHorario());
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
        int numColunas = 10;
        int capacidade = area.getCapacidadeTotal();
        int numLinhas = (int) Math.ceil((double) capacidade / numColunas);

        // Cria as poltronas
        for (int i = 0; i < numLinhas; i++) {
            for (int j = 0; j < numColunas; j++) {
                int numero = i * numColunas + j + 1;
                if (numero > capacidade) break;

                // Cria a poltrona
                Button poltrona = new Button(String.valueOf(numero));
                poltrona.setPrefSize(50, 50);
                poltrona.setFont(Font.font("System", FontWeight.BOLD, 14));

                // Verifica se a poltrona está ocupada
                boolean ocupada = !poltronasDisponiveis.contains(numero);

                if (ocupada) {
                    poltrona.setStyle("-fx-background-color: " + POLTRONA_OCUPADA + "; -fx-text-fill: white; -fx-background-radius: 5;");
                    poltrona.setDisable(true);
                } else {
                    poltrona.setStyle("-fx-background-color: " + POLTRONA_DISPONIVEL + "; -fx-text-fill: white; -fx-background-radius: 5;");
                    // Adiciona evento de clique
                    poltrona.setOnAction(e -> {
                        try {
                            // Verifica se a poltrona ainda está disponível
                            if (!teatro.verificarPoltronaDisponivel(sessao, area, numero)) {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Atenção");
                                alert.setHeaderText("Poltrona ocupada");
                                alert.setContentText("Esta poltrona acabou de ser ocupada. Por favor, selecione outra.");
                                alert.showAndWait();
                                
                                // Atualiza o estado da poltrona
                                poltrona.setStyle("-fx-background-color: " + POLTRONA_OCUPADA + "; -fx-text-fill: white; -fx-background-radius: 5;");
                                poltrona.setDisable(true);
                                poltronasDisponiveis.remove(Integer.valueOf(numero));
                                poltronasSelecionadas.removeIf(p -> p.getNumero() == numero);
                                atualizarResumo();
                                return;
                            }
                            
                            if (poltrona.getStyle().contains(POLTRONA_DISPONIVEL)) {
                                // Seleciona a poltrona
                                poltrona.setStyle("-fx-background-color: " + POLTRONA_SELECIONADA + "; -fx-text-fill: white; -fx-background-radius: 5;");
                                poltronasSelecionadas.add(new Poltrona(numero, area));
                            } else {
                                // Desseleciona a poltrona
                                poltrona.setStyle("-fx-background-color: " + POLTRONA_DISPONIVEL + "; -fx-text-fill: white; -fx-background-radius: 5;");
                                poltronasSelecionadas.removeIf(p -> p.getNumero() == numero);
                            }
                            atualizarResumo();
                        } catch (Exception ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erro");
                            alert.setHeaderText("Erro ao selecionar poltrona");
                            alert.setContentText("Ocorreu um erro ao verificar a disponibilidade da poltrona. Por favor, tente novamente.");
                            alert.showAndWait();
                        }
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
        this.qtdValor = new Label("0");
        this.qtdValor.setFont(Font.font("System", 14));
        qtdBox.getChildren().addAll(qtdLabel, this.qtdValor);
        
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
        this.valorTotalValor = new Label("R$ 0,00");
        this.valorTotalValor.setFont(Font.font("System", FontWeight.BOLD, 16));
        this.valorTotalValor.setTextFill(Color.web(SECONDARY_COLOR));
        valorTotalBox.getChildren().addAll(valorTotalLabel, this.valorTotalValor);
        
        // Botões
        HBox botoesBox = new HBox(15);
        botoesBox.setAlignment(Pos.CENTER_RIGHT);
        botoesBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button voltarButton = new Button("Voltar");
        voltarButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14; -fx-cursor: hand; -fx-background-radius: 4;");
        
        voltarButton.setOnAction(e -> {
            new CompraIngressoView(teatro, usuario, stage, sessao).show();
        });
        
        this.confirmarButton = new Button("Confirmar Compra");
        this.confirmarButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14; -fx-cursor: hand; -fx-background-radius: 4; -fx-font-weight: bold;");
        this.confirmarButton.setDisable(true);
        
        this.confirmarButton.setOnAction(e -> {
            try {
                // Verifica se todas as poltronas selecionadas ainda estão disponíveis
                for (Poltrona poltrona : new ArrayList<>(poltronasSelecionadas)) {
                    if (!teatro.verificarPoltronaDisponivel(sessao, area, poltrona.getNumero())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Atenção");
                        alert.setHeaderText("Poltrona ocupada");
                        alert.setContentText("A poltrona " + poltrona.getNumero() + " acabou de ser ocupada. Por favor, selecione outra.");
                        alert.showAndWait();
                        
                        // Remove a poltrona da seleção
                        poltronasSelecionadas.remove(poltrona);
                        atualizarResumo();
                        return;
                    }
                }
                
                // Cria os ingressos
                List<IngressoModerno> ingressos = new ArrayList<>();
                boolean todosSalvos = true;
                
                for (Poltrona poltrona : poltronasSelecionadas) {
                    try {
                        // Cria o ingresso moderno
                        IngressoModerno ingresso = new IngressoModerno(sessao, area, poltrona, usuario);
                        ingresso.setCodigo(gerarCodigoIngresso());
                        
                        // Busca o evento da sessão
                        Evento evento = teatro.getEventos().stream()
                            .filter(evt -> evt.getSessoes().contains(sessao))
                            .findFirst()
                            .orElse(null);
                        
                        if (evento == null) {
                            throw new Exception("Não foi possível encontrar o evento da sessão");
                        }
                        
                        // Tenta salvar o ingresso no banco de dados
                        Optional<IngressoModerno> ingressoSalvo = teatro.comprarIngresso(usuario.getCpf(), evento, sessao, area, poltrona.getNumero());
                        if (ingressoSalvo.isPresent()) {
                            // Adiciona o ingresso à lista
                            ingressos.add(ingressoSalvo.get());
                            // Remove a poltrona da lista de disponíveis
                            poltronasDisponiveis.remove(Integer.valueOf(poltrona.getNumero()));
                        } else {
                            throw new Exception("Não foi possível comprar o ingresso para a poltrona " + poltrona.getNumero());
                        }
                    } catch (Exception ex) {
                        todosSalvos = false;
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Erro ao comprar ingresso");
                        alert.setContentText(ex.getMessage() + ". Tente novamente.");
                        alert.showAndWait();
                        break;
                    }
                }
                
                if (todosSalvos && !ingressos.isEmpty()) {
                    // Adiciona os ingressos ao usuário
                    usuario.adicionarIngressos(ingressos);
                    
                    // Mostra a tela de impressão
                    new ImpressaoIngressoView(teatro, usuario, stage, ingressos).show();
                } else if (ingressos.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Nenhum ingresso foi comprado");
                    alert.setContentText("Ocorreu um erro ao processar sua compra. Por favor, tente novamente.");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Erro ao processar compra");
                alert.setContentText("Ocorreu um erro ao processar sua compra. Por favor, tente novamente.");
                alert.showAndWait();
            }
        });
        
        botoesBox.getChildren().addAll(voltarButton, this.confirmarButton);
        
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
            new LoginView(stage).show();
        });
        
        userInfo.getChildren().addAll(userName, logoutButton);
        
        // Botão voltar
        Button backButton = new Button("Voltar para Seleção de Área");
        backButton.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-cursor: hand;");
        
        backButton.setOnAction(e -> {
            new CompraIngressoView(teatro, usuario, stage, sessao).show();
        });
        
        // Espaçador para empurrar o userInfo para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(systemTitle, backButton, spacer, userInfo);
        
        return topBar;
    }
    
    private void atualizarResumo() {
        // Atualiza a quantidade de ingressos selecionados
        qtdValor.setText(String.valueOf(poltronasSelecionadas.size()));
        
        // Calcula o valor total
        double valorTotal = poltronasSelecionadas.size() * area.getPreco();
        valorTotalValor.setText("R$ " + String.format("%.2f", valorTotal));
        
        // Habilita/desabilita o botão de confirmar com base na seleção de poltronas
        confirmarButton.setDisable(poltronasSelecionadas.isEmpty());
    }

    private String gerarCodigoIngresso() {
        // Gera um código único para o ingresso usando timestamp e número aleatório
        return String.format("ING-%d-%d", System.currentTimeMillis(), (int)(Math.random() * 1000));
    }
}
