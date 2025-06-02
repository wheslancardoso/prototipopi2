package com.teatro.view;

import com.teatro.model.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Tela de pagamento simulado para o sistema de teatro.
 * Esta classe implementa uma simulação de fluxo de pagamento sem processamento real.
 */
public class PagamentoSimuladoView extends VBox {
    
    // Constantes de estilo
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#2ecc71";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String CARD_BACKGROUND = "white";
    private static final String ERROR_COLOR = "#e74c3c";
    private static final String SUCCESS_COLOR = "#27ae60";
    
    // Elementos da UI
    private TextField campoNome;
    private TextField campoNumeroCartao;
    private TextField campoValidade;
    private TextField campoCVV;
    private Button btnPagar;
    private Label lblStatus;
    private ProgressIndicator progresso;
    private VBox containerFormulario;
    
    // Referências
    private final Stage stage;
    private final Usuario usuario;
    private final List<IngressoModerno> ingressos;
    private final double valorTotal;
    private final Runnable onPagamentoConcluido;
    
    /**
     * Constrói a tela de pagamento simulado.
     * 
     * @param stage Stage principal da aplicação
     * @param usuario Usuário realizando o pagamento
     * @param ingressos Lista de ingressos modernos a serem pagos
     * @param onPagamentoConcluido Callback executado quando o pagamento é concluído
     */
    public PagamentoSimuladoView(Stage stage, Usuario usuario, List<IngressoModerno> ingressos, Runnable onPagamentoConcluido) {
        this.stage = stage;
        this.usuario = usuario;
        this.ingressos = ingressos;
        this.onPagamentoConcluido = onPagamentoConcluido;
        
        // Calcular valor total
        this.valorTotal = ingressos.stream()
                .mapToDouble(IngressoModerno::getValor)
                .sum();
        
        // Configuração do layout
        setSpacing(20);
        setPadding(new Insets(30, 40, 40, 40));
        setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Inicializar componentes
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        // Título da página
        Label titulo = new Label("Finalizar Pagamento");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web(TEXT_COLOR));
        
        // Card de resumo do pedido
        VBox cardResumo = criarCardResumo();
        
        // Card de dados do pagamento
        VBox cardPagamento = criarCardPagamento();
        
        // Botão de voltar
        Button btnVoltar = new Button("Voltar");
        btnVoltar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoltar.setOnAction(e -> {
            // Volta para a tela de seleção de poltronas
            Sessao sessao = ingressos.get(0).getSessao();
            Area area = ingressos.get(0).getArea();
            new SelecionarPoltronaViewModerna(new Teatro(), usuario, stage, sessao, area).show();
        });
        
        // Adiciona componentes ao layout
        getChildren().addAll(titulo, cardResumo, cardPagamento, btnVoltar);
    }
    
    private VBox criarCardResumo() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label titulo = new Label("Resumo do Pedido");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setTextFill(Color.web(TEXT_COLOR));
        
        // Exibir informações da sessão
        IngressoModerno primeiroIngresso = ingressos.get(0);
        Sessao sessao = primeiroIngresso.getSessao();
        
        Label lblEvento = new Label("Evento: " + sessao.getNome());
        Label lblData = new Label("Data: " + sessao.getDataFormatada());
        Label lblLocal = new Label("Horário: " + sessao.getHorarioCompleto());
        Label lblIngressos = new Label("Quantidade de ingressos: " + ingressos.size());
        
        // Formatar valor total
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String valorFormatado = formatoMoeda.format(valorTotal);
        Label lblValorTotal = new Label("Valor Total: " + valorFormatado);
        lblValorTotal.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        card.getChildren().addAll(titulo, lblEvento, lblData, lblLocal, lblIngressos, lblValorTotal);
        
        return card;
    }
    
    private VBox criarCardPagamento() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label titulo = new Label("Dados do Cartão");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setTextFill(Color.web(TEXT_COLOR));
        
        // Formulário de pagamento
        GridPane formulario = new GridPane();
        formulario.setVgap(15);
        formulario.setHgap(10);
        
        // Nome do titular
        Label lblNome = new Label("Nome no Cartão:");
        campoNome = new TextField();
        campoNome.setPromptText("Como está no cartão");
        campoNome.setPrefWidth(300);
        
        // Número do cartão
        Label lblNumeroCartao = new Label("Número do Cartão:");
        campoNumeroCartao = new TextField();
        campoNumeroCartao.setPromptText("Ex: 4242 4242 4242 4242");
        
        // Validade e CVV na mesma linha
        HBox linhaValidadeCVV = new HBox(10);
        
        VBox containerValidade = new VBox(5);
        Label lblValidade = new Label("Validade:");
        campoValidade = new TextField();
        campoValidade.setPromptText("MM/AA");
        campoValidade.setPrefWidth(100);
        containerValidade.getChildren().addAll(lblValidade, campoValidade);
        
        VBox containerCVV = new VBox(5);
        Label lblCVV = new Label("CVV:");
        campoCVV = new TextField();
        campoCVV.setPromptText("123");
        campoCVV.setPrefWidth(80);
        containerCVV.getChildren().addAll(lblCVV, campoCVV);
        
        linhaValidadeCVV.getChildren().addAll(containerValidade, containerCVV);
        
        // Adiciona campos ao grid
        formulario.add(lblNome, 0, 0);
        formulario.add(campoNome, 0, 1, 2, 1);
        
        formulario.add(lblNumeroCartao, 0, 2);
        formulario.add(campoNumeroCartao, 0, 3, 2, 1);
        
        formulario.add(linhaValidadeCVV, 0, 4, 2, 1);
        
        // Botão de pagamento
        btnPagar = new Button("Confirmar Pagamento");
        btnPagar.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;"
        );
        btnPagar.setOnAction(e -> processarPagamento());
        
        // Indicador de progresso (inicialmente invisível)
        progresso = new ProgressIndicator();
        progresso.setVisible(false);
        progresso.setPrefSize(30, 30);
        
        // Container para o botão e o indicador de progresso
        HBox containerBotao = new HBox(10, btnPagar, progresso);
        containerBotao.setAlignment(Pos.CENTER_LEFT);
        
        // Label de status
        lblStatus = new Label();
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(400);
        
        // Adiciona tudo ao card
        card.getChildren().addAll(titulo, formulario, containerBotao, lblStatus);
        
        // Salva referência para o container do formulário (será escondido após o pagamento)
        containerFormulario = card;
        
        return card;
    }
    
    /**
     * Processa o pagamento de forma simulada.
     */
    private void processarPagamento() {
        // Validação básica dos campos
        if (campoNome.getText().trim().isEmpty() || 
            campoNumeroCartao.getText().trim().isEmpty() || 
            campoValidade.getText().trim().isEmpty() || 
            campoCVV.getText().trim().isEmpty()) {
            
            mostrarMensagem("Por favor, preencha todos os campos do cartão.", false);
            return;
        }
        
        // Desabilita o botão e mostra o indicador de progresso
        btnPagar.setDisable(true);
        progresso.setVisible(true);
        
        // Simula um processamento assíncrono
        PauseTransition pausa = new PauseTransition(Duration.seconds(2));
        pausa.setOnFinished(e -> {
            // Verifica se o cartão é "válido" (apenas para demonstração)
            boolean pagamentoAprovado = validarCartao(campoNumeroCartao.getText().trim());
            
            if (pagamentoAprovado) {
                // Pagamento aprovado
                exibirResultadoPagamento(true);
            } else {
                // Pagamento recusado
                exibirResultadoPagamento(false);
            }
        });
        
        pausa.play();
    }
    
    /**
     * Valida o número do cartão (simulação).
     * Para fins de demonstração, cartões que começam com "4242" são aprovados.
     */
    private boolean validarCartao(String numeroCartao) {
        // Remove espaços em branco
        String numeroLimpo = numeroCartao.replaceAll("\\s+", "");
        
        // Verifica se o cartão começa com 4242 (apenas para demonstração)
        return numeroLimpo.startsWith("4242") && numeroLimpo.length() >= 16;
    }
    
    /**
     * Exibe o resultado do pagamento.
     * @param sucesso true se o pagamento foi aprovado, false caso contrário
     */
    private void exibirResultadoPagamento(boolean sucesso) {
        // Esconde o formulário
        containerFormulario.setVisible(false);
        
        // Cria um novo card para o resultado
        VBox cardResultado = new VBox(20);
        cardResultado.setPadding(new Insets(30));
        cardResultado.setAlignment(Pos.CENTER);
        cardResultado.setStyle(
            "-fx-background-color: " + (sucesso ? "#e8f5e9" : "#ffebee") + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Ícone e mensagem de status
        Label icone = new Label(sucesso ? "✓" : "✗");
        icone.setStyle(
            "-fx-font-size: 48px;" +
            "-fx-text-fill: " + (sucesso ? SUCCESS_COLOR : ERROR_COLOR) + ";"
        );
        
        Label mensagem = new Label(sucesso ? 
            "Pagamento Aprovado!" : 
            "Pagamento Recusado");
        mensagem.setFont(Font.font("System", FontWeight.BOLD, 20));
        mensagem.setTextFill(Color.web(sucesso ? SUCCESS_COLOR : ERROR_COLOR));
        
        // Detalhes adicionais
        Label detalhes = new Label(sucesso ?
            "Seu pagamento foi processado com sucesso. Os ingressos foram reservados em seu nome." :
            "Não foi possível processar seu pagamento. Verifique os dados do cartão e tente novamente.");
        detalhes.setWrapText(true);
        detalhes.setMaxWidth(400);
        detalhes.setAlignment(Pos.CENTER);
        
        // Botão de ação
        Button btnAcao = new Button(sucesso ? "Visualizar Ingressos" : "Tentar Novamente");
        btnAcao.setStyle(
            "-fx-background-color: " + (sucesso ? SECONDARY_COLOR : PRIMARY_COLOR) + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 25;"
        );
        
        if (sucesso) {
            // Se o pagamento foi aprovado, chama o callback de conclusão
            btnAcao.setOnAction(e -> onPagamentoConcluido.run());
        } else {
            // Se foi recusado, permite tentar novamente
            btnAcao.setOnAction(e -> {
                getChildren().remove(cardResultado);
                containerFormulario.setVisible(true);
                btnPagar.setDisable(false);
                progresso.setVisible(false);
            });
        }
        
        // Adiciona tudo ao card de resultado
        cardResultado.getChildren().addAll(icone, mensagem, detalhes, btnAcao);
        
        // Adiciona o card de resultado à view
        getChildren().add(cardResultado);
    }
    
    /**
     * Exibe uma mensagem de status.
     * @param mensagem A mensagem a ser exibida
     * @param sucesso true para mensagem de sucesso, false para erro
     */
    private void mostrarMensagem(String mensagem, boolean sucesso) {
        lblStatus.setText(mensagem);
        lblStatus.setTextFill(sucesso ? Color.web(SUCCESS_COLOR) : Color.web(ERROR_COLOR));
    }
}
