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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller para a tela de impressão de ingressos.
 */
public class ImpressaoIngressoController implements Initializable {
    
    @FXML
    private Label impressao_lblLogo;
    
    @FXML
    private Label impressao_lblUsuario;
    
    @FXML
    private Label impressao_lblTitulo;
    
    @FXML
    private Label impressao_lblSubtitulo;
    
    @FXML
    private Button impressao_btnDashboard;
    
    @FXML
    private Button impressao_btnSair;
    
    @FXML
    private VBox impressao_containerIngressos;
    
    @FXML
    private VBox impressao_containerVazio;
    
    @FXML
    private HBox impressao_containerBotoes;
    
    @FXML
    private Button impressao_btnComprarNovo;
    
    @FXML
    private Button impressao_btnImprimirTodos;
    
    @FXML
    private Button impressao_btnNovaCompra;
    
    private Teatro teatro;
    private Usuario usuario;
    private IngressoService ingressoService;
    private SceneManager sceneManager;
    private List<IngressoModerno> ingressos;
    private boolean isCompraNova = false; // Flag para saber se vem de uma compra nova
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        teatro = sceneManager.getTeatro();
        usuario = sceneManager.getUsuarioLogado();
        ingressoService = IngressoService.getInstance();
        
        // Configurar interface
        if (usuario != null) {
            impressao_lblUsuario.setText(usuario.getNome());
            
            // Ocultar botão dashboard se não for admin
            if (!"ADMIN".equals(usuario.getTipoUsuario())) {
                impressao_btnDashboard.setVisible(false);
                impressao_btnDashboard.setManaged(false);
            }
        }
        
        // Carregar ingressos do usuário
        carregarIngressos();
    }
    
    /**
     * Define se esta tela foi aberta após uma compra nova.
     */
    public void setCompraNova(boolean compraNova) {
        this.isCompraNova = compraNova;
        
        if (compraNova) {
            impressao_lblTitulo.setText("Compra Realizada com Sucesso!");
            impressao_lblSubtitulo.setText("Seus ingressos estão prontos para impressão.");
        } else {
            impressao_lblTitulo.setText("Seus Ingressos");
            impressao_lblSubtitulo.setText("Visualize e imprima seus ingressos comprados.");
        }
    }
    
    /**
     * Define a lista de ingressos específicos (usado quando vem de uma compra).
     */
    public void setIngressos(List<IngressoModerno> ingressos) {
        this.ingressos = new ArrayList<>(ingressos);
        
        // Recarregar interface
        if (this.ingressos.isEmpty()) {
            mostrarContainerVazio();
        } else {
            mostrarIngressos();
        }
    }
    
    /**
     * Carrega os ingressos do usuário.
     */
    private void carregarIngressos() {
        try {
            if (ingressos == null) {
                // Buscar todos os ingressos do usuário
                List<Ingresso> ingressosLegacy = ingressoService.buscarPorUsuario(usuario.getCpf());
                ingressos = new ArrayList<>();
                
                // Converter para IngressoModerno
                for (Ingresso ingresso : ingressosLegacy) {
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
            
            if (ingressos.isEmpty()) {
                mostrarContainerVazio();
            } else {
                mostrarIngressos();
            }
            
        } catch (Exception e) {
            mostrarErro("Erro ao carregar ingressos: " + e.getMessage());
            mostrarContainerVazio();
        }
    }
    
    /**
     * Mostra a lista de ingressos.
     */
    private void mostrarIngressos() {
        impressao_containerIngressos.getChildren().clear();
        
        for (IngressoModerno ingresso : ingressos) {
            VBox cardIngresso = criarCardIngresso(ingresso);
            impressao_containerIngressos.getChildren().add(cardIngresso);
        }
        
        // Mostrar containers apropriados
        impressao_containerIngressos.setVisible(true);
        impressao_containerIngressos.setManaged(true);
        impressao_containerBotoes.setVisible(true);
        impressao_containerBotoes.setManaged(true);
        impressao_containerVazio.setVisible(false);
        impressao_containerVazio.setManaged(false);
    }
    
    /**
     * Mostra o container vazio.
     */
    private void mostrarContainerVazio() {
        impressao_containerIngressos.setVisible(false);
        impressao_containerIngressos.setManaged(false);
        impressao_containerBotoes.setVisible(false);
        impressao_containerBotoes.setManaged(false);
        impressao_containerVazio.setVisible(true);
        impressao_containerVazio.setManaged(true);
    }
    
    /**
     * Cria um card para um ingresso.
     */
    private VBox criarCardIngresso(IngressoModerno ingresso) {
        VBox card = new VBox(15);
        card.getStyleClass().add("ingresso-card");
        card.setPadding(new Insets(20));
        
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
        adicionarInfoGrid(infoGrid, 0, 0, "Horário:", ingresso.getHorario());
        
        // Área
        adicionarInfoGrid(infoGrid, 0, 1, "Área:", ingresso.getAreaNome());
        
        // Poltrona
        adicionarInfoGrid(infoGrid, 0, 2, "Poltrona:", String.valueOf(ingresso.getNumeroPoltrona()));
        
        // Valor
        adicionarInfoGrid(infoGrid, 1, 0, "Valor:", String.format("R$ %.2f", ingresso.getValor()));
        
        // Código
        adicionarInfoGrid(infoGrid, 1, 1, "Código:", ingresso.getCodigo());
        
        // Data da compra
        String dataCompra = "N/A";
        try {
            if (ingresso.getDataCompra() != null) {
                dataCompra = ingresso.getDataCompra().toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }
        } catch (Exception e) {
            // Manter "N/A" se houver erro
        }
        adicionarInfoGrid(infoGrid, 1, 2, "Data da Compra:", dataCompra);
        
        // Botão para imprimir
        Button imprimirButton = new Button("Imprimir Ingresso");
        imprimirButton.getStyleClass().add("imprimir-button");
        imprimirButton.setOnAction(e -> imprimirIngresso(ingresso));
        
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(imprimirButton);
        
        card.getChildren().addAll(eventoLabel, separator, infoGrid, buttonBox);
        return card;
    }
    
    /**
     * Adiciona uma informação ao grid.
     */
    private void adicionarInfoGrid(GridPane grid, int col, int row, String titulo, String valor) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("info-title");
        
        Label lblValor = new Label(valor);
        lblValor.getStyleClass().add("info-value");
        
        VBox container = new VBox(2);
        container.getChildren().addAll(lblTitulo, lblValor);
        
        grid.add(container, col, row);
    }
    
    /**
     * Imprime um ingresso individual.
     */
    private void imprimirIngresso(IngressoModerno ingresso) {
        try {
            // Criar janela modal para impressão
            Stage impressaoStage = new Stage();
            impressaoStage.initModality(Modality.APPLICATION_MODAL);
            impressaoStage.setTitle("Impressão de Ingresso");
            impressaoStage.setResizable(false);
            
            VBox root = new VBox(20);
            root.setPadding(new Insets(30));
            root.setAlignment(Pos.CENTER);
            root.getStyleClass().add("impressao-modal");
            
            // Texto do ingresso formatado
            TextArea ingressoTexto = new TextArea();
            ingressoTexto.setEditable(false);
            ingressoTexto.setPrefWidth(400);
            ingressoTexto.setPrefHeight(300);
            ingressoTexto.getStyleClass().add("ingresso-texto");
            
            // Formatar o texto do ingresso
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
                if (ingresso.getDataCompra() != null) {
                    sb.append("Data da Compra: ").append(
                        ingresso.getDataCompra().toLocalDateTime().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        )
                    ).append("\n");
                }
            } catch (Exception e) {
                sb.append("Data da Compra: Não disponível\n");
            }
            
            sb.append("\nObrigado pela sua compra!\n");
            sb.append("========================================\n");
            
            ingressoTexto.setText(sb.toString());
            
            // Botão de fechar
            Button fecharButton = new Button("Fechar");
            fecharButton.getStyleClass().add("fechar-button");
            fecharButton.setOnAction(e -> impressaoStage.close());
            
            root.getChildren().addAll(ingressoTexto, fecharButton);
            
            Scene scene = new Scene(root, 460, 500);
            scene.getStylesheets().add(getClass().getResource("/com/teatro/view/css/impressao-ingresso.css").toExternalForm());
            
            impressaoStage.setScene(scene);
            impressaoStage.showAndWait();
            
        } catch (Exception e) {
            mostrarErro("Erro ao imprimir ingresso: " + e.getMessage());
        }
    }
    
    /**
     * Manipula o botão dashboard.
     */
    @FXML
    private void handleDashboard() {
        try {
            sceneManager.goToDashboard();
        } catch (Exception e) {
            mostrarErro("Erro ao navegar para o dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Manipula o botão sair.
     */
    @FXML
    private void handleSair() {
        try {
            sceneManager.goToLogin();
        } catch (Exception e) {
            mostrarErro("Erro ao fazer logout: " + e.getMessage());
        }
    }
    
    /**
     * Manipula o botão comprar novo (quando não há ingressos).
     */
    @FXML
    private void handleComprarNovo() {
        try {
            sceneManager.goToSessoes();
        } catch (Exception e) {
            mostrarErro("Erro ao navegar para sessões: " + e.getMessage());
        }
    }
    
    /**
     * Manipula o botão imprimir todos.
     */
    @FXML
    private void handleImprimirTodos() {
        if (ingressos == null || ingressos.isEmpty()) {
            mostrarErro("Nenhum ingresso para imprimir.");
            return;
        }
        
        try {
            // Criar janela modal para impressão de todos
            Stage impressaoStage = new Stage();
            impressaoStage.initModality(Modality.APPLICATION_MODAL);
            impressaoStage.setTitle("Impressão de Todos os Ingressos");
            impressaoStage.setResizable(true);
            
            VBox root = new VBox(20);
            root.setPadding(new Insets(30));
            root.setAlignment(Pos.CENTER);
            
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefWidth(600);
            scrollPane.setPrefHeight(500);
            
            VBox contentBox = new VBox(30);
            contentBox.setPadding(new Insets(20));
            
            // Adicionar cada ingresso
            for (IngressoModerno ingresso : ingressos) {
                TextArea ingressoTexto = new TextArea();
                ingressoTexto.setEditable(false);
                ingressoTexto.setPrefHeight(200);
                ingressoTexto.getStyleClass().add("ingresso-texto");
                
                // Mesmo formato do ingresso individual
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
                sb.append("\nObrigado pela sua compra!\n");
                sb.append("========================================\n");
                
                ingressoTexto.setText(sb.toString());
                contentBox.getChildren().add(ingressoTexto);
            }
            
            scrollPane.setContent(contentBox);
            
            Button fecharButton = new Button("Fechar");
            fecharButton.getStyleClass().add("fechar-button");
            fecharButton.setOnAction(e -> impressaoStage.close());
            
            root.getChildren().addAll(
                new Label("Todos os Seus Ingressos"),
                scrollPane,
                fecharButton
            );
            
            Scene scene = new Scene(root, 650, 600);
            scene.getStylesheets().add(getClass().getResource("/com/teatro/view/css/impressao-ingresso.css").toExternalForm());
            
            impressaoStage.setScene(scene);
            impressaoStage.showAndWait();
            
        } catch (Exception e) {
            mostrarErro("Erro ao imprimir todos os ingressos: " + e.getMessage());
        }
    }
    
    /**
     * Manipula o botão nova compra.
     */
    @FXML
    private void handleNovaCompra() {
        handleComprarNovo();
    }
    
    /**
     * Exibe uma mensagem de erro.
     */
    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}