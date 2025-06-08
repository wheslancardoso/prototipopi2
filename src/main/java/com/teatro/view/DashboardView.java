package com.teatro.view;

import com.teatro.model.*;
import com.teatro.service.IngressoService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Versão modernizada da tela de dashboard do sistema.
 */
public class DashboardView {
    private Teatro teatro;
    private Usuario usuarioLogado;
    private Stage stage;
    private IngressoService ingressoService;
    
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;
    
    // Cores do tema
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#2ecc71";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String CARD_BACKGROUND = "white";

    public DashboardView(Teatro teatro, Usuario usuarioLogado, Stage stage) {
        this.teatro = teatro;
        this.usuarioLogado = usuarioLogado;
        this.stage = stage;
        this.ingressoService = IngressoService.getInstance();
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Dashboard");

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
        Label pageTitle = new Label("Dashboard");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        pageTitle.setTextFill(Color.web(TEXT_COLOR));
        
        // Conteúdo principal baseado no tipo de usuário
        if ("ADMIN".equals(usuarioLogado.getTipoUsuario())) {
            contentContainer.getChildren().addAll(pageTitle, criarAreaPrincipalAdmin());
        } else {
            contentContainer.getChildren().addAll(pageTitle, criarAreaPrincipalUsuario());
        }
        
        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
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
        
        Label userName = new Label(usuarioLogado.getNome());
        userName.setTextFill(Color.WHITE);
        
        Button logoutButton = new Button("Sair");
        logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 3; -fx-cursor: hand;");
        
        logoutButton.setOnAction(e -> {
            new LoginView(teatro, stage).show();
        });
        
        userInfo.getChildren().addAll(userName, logoutButton);
        
        // Espaçador para empurrar o userInfo para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(systemTitle, spacer, userInfo);
        
        return topBar;
    }

    private VBox criarAreaPrincipalUsuario() {
        VBox area = new VBox(20);
        area.setPadding(new Insets(20));
        area.setAlignment(Pos.CENTER);

        // Mensagem de boas-vindas
        Label lblBoasVindas = new Label("Bem-vindo(a), " + usuarioLogado.getNome() + "!");
        lblBoasVindas.setStyle("""
            -fx-font-size: 24;
            -fx-font-weight: bold;
            -fx-text-fill: #2c3e50;
            """);

        // Card com instruções
        VBox cardInstrucoes = new VBox(10);
        cardInstrucoes.setPadding(new Insets(20));
        cardInstrucoes.setStyle("""
            -fx-background-color: white;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            -fx-background-radius: 5;
            -fx-max-width: 600;
            """);

        Label lblInstrucoes = new Label("O que você pode fazer:");
        lblInstrucoes.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        VBox listaInstrucoes = new VBox(10);
        listaInstrucoes.getChildren().addAll(
            criarItemInstrucao("Comprar Ingresso", "Escolha seu evento favorito e garanta seu lugar"),
            criarItemInstrucao("Imprimir Ingresso", "Visualize e imprima seus ingressos comprados")
        );

        cardInstrucoes.getChildren().addAll(lblInstrucoes, listaInstrucoes);
        
        // Botões de ação
        HBox botoesAcao = new HBox(20);
        botoesAcao.setAlignment(Pos.CENTER);
        
        Button btnComprar = new Button("Comprar Ingresso");
        Button btnImprimir = new Button("Imprimir Ingresso");
        
        String buttonStyle = """
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-padding: 10 20;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """;
        
        btnComprar.setStyle(String.format(buttonStyle, PRIMARY_COLOR));
        btnImprimir.setStyle(String.format(buttonStyle, SECONDARY_COLOR));
        
        btnComprar.setOnAction(e -> new SessoesView(teatro, usuarioLogado, stage).show());
        btnImprimir.setOnAction(e -> {
            List<Ingresso> ingressos = ingressoService.buscarPorUsuario(usuarioLogado.getCpf());
            List<IngressoModerno> ingressosModernos = new ArrayList<>();
            
            for (Ingresso ingresso : ingressos) {
                ingressosModernos.add(new IngressoModerno(
                    ingresso.getId(),
                    ingresso.getEventoNome(),
                    ingresso.getTipoSessao().getDescricao(),
                    ingresso.getDataSessao(),
                    ingresso.getAreaNome(),
                    ingresso.getNumeroPoltrona(),
                    ingresso.getValor(),
                    ingresso.getDataCompra(),
                    ingresso.getCodigo()
                ));
            }
            
            new ImpressaoIngressoView(teatro, usuarioLogado, stage, ingressosModernos).show();
        });
        
        botoesAcao.getChildren().addAll(btnComprar, btnImprimir);
        
        area.getChildren().addAll(lblBoasVindas, cardInstrucoes, botoesAcao);
        return area;
    }

    private HBox criarItemInstrucao(String titulo, String descricao) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);

        Label lblBullet = new Label("•");
        lblBullet.setStyle("-fx-font-size: 20; -fx-text-fill: " + PRIMARY_COLOR + ";");

        VBox textos = new VBox(5);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        Label lblDescricao = new Label(descricao);
        lblDescricao.setStyle("-fx-font-size: 14; -fx-text-fill: #7f8c8d;");

        textos.getChildren().addAll(lblTitulo, lblDescricao);
        item.getChildren().addAll(lblBullet, textos);

        return item;
    }

    private VBox criarAreaPrincipalAdmin() {
        VBox area = new VBox(20);
        area.setPadding(new Insets(20));

        // Estatísticas padrão
        Map<String, Object> estatisticas = Map.of(
            "pecaMaisVendida", Map.of("nome", "N/A", "totalVendas", 0),
            "pecaMenosVendida", Map.of("nome", "N/A", "totalVendas", 0),
            "sessaoMaiorOcupacao", Map.of("nome", "N/A", "data", "N/A", "horario", "N/A", "ocupacao", "0%"),
            "sessaoMenorOcupacao", Map.of("nome", "N/A", "data", "N/A", "horario", "N/A", "ocupacao", "0%"),
            "pecaMaisLucrativa", Map.of("nome", "N/A", "faturamento", "R$ 0,00"),
            "pecaMenosLucrativa", Map.of("nome", "N/A", "faturamento", "R$ 0,00"),
            "lucroMedioPorPeca", List.of()
        );

        // Seção de Vendas
        VBox secaoVendas = criarCardSecao(
            "Estatísticas de Vendas",
            criarGridEstatisticas(
                new String[][] {
                    {"Peça Mais Vendida", formatarEstatistica(estatisticas, "pecaMaisVendida")},
                    {"Peça Menos Vendida", formatarEstatistica(estatisticas, "pecaMenosVendida")}
                }
            )
        );

        // Seção de Ocupação
        VBox secaoOcupacao = criarCardSecao(
            "Estatísticas de Ocupação",
            criarGridEstatisticas(
                new String[][] {
                    {"Sessão com Maior Ocupação", formatarEstatistica(estatisticas, "sessaoMaiorOcupacao")},
                    {"Sessão com Menor Ocupação", formatarEstatistica(estatisticas, "sessaoMenorOcupacao")}
                }
            )
        );

        // Seção de Faturamento
        VBox secaoFaturamento = criarCardSecao(
            "Estatísticas de Faturamento",
            criarGridEstatisticas(
                new String[][] {
                    {"Peça Mais Lucrativa", formatarEstatistica(estatisticas, "pecaMaisLucrativa")},
                    {"Peça Menos Lucrativa", formatarEstatistica(estatisticas, "pecaMenosLucrativa")}
                }
            )
        );

        // Seção de Lucro Médio
        VBox secaoLucroMedio = criarCardSecao(
            "Lucro Médio por Peça",
            criarTabelaLucroMedio((List<Map<String, Object>>) estatisticas.get("lucroMedioPorPeca"))
        );

        // Botões de ação
        HBox botoesAcao = new HBox(20);
        botoesAcao.setAlignment(Pos.CENTER);
        
        Button btnComprar = new Button("Comprar Ingresso");
        Button btnImprimir = new Button("Imprimir Ingresso");
        
        String buttonStyle = """
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-padding: 10 20;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """;
        
        btnComprar.setStyle(String.format(buttonStyle, PRIMARY_COLOR));
        btnImprimir.setStyle(String.format(buttonStyle, SECONDARY_COLOR));
        
        btnComprar.setOnAction(e -> new SessoesView(teatro, usuarioLogado, stage).show());
        btnImprimir.setOnAction(e -> new ImpressaoIngressoView(teatro, usuarioLogado, stage, List.of()).show());
        
        botoesAcao.getChildren().addAll(btnComprar, btnImprimir);

        area.getChildren().addAll(
            secaoVendas,
            secaoOcupacao,
            secaoFaturamento,
            secaoLucroMedio,
            botoesAcao
        );
        return area;
    }

    private VBox criarCardSecao(String titulo, Node conteudo) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("""
            -fx-background-color: white;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            -fx-background-radius: 5;
            """);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("""
            -fx-font-size: 20;
            -fx-font-weight: bold;
            -fx-text-fill: #2c3e50;
            """);

        card.getChildren().addAll(lblTitulo, conteudo);
        return card;
    }

    private GridPane criarGridEstatisticas(String[][] dados) {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        for (int i = 0; i < dados.length; i++) {
            Label titulo = new Label(dados[i][0]);
            titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            
            Label valor = new Label(dados[i][1]);
            valor.setStyle("-fx-font-size: 14;");
            
            grid.add(titulo, 0, i);
            grid.add(valor, 1, i);
        }

        return grid;
    }

    private TableView<Map<String, String>> criarTabelaLucroMedio(List<Map<String, Object>> dados) {
        TableView<Map<String, String>> tabela = new TableView<>();
        tabela.setStyle("""
            -fx-background-color: white;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            """);

        TableColumn<Map<String, String>, String> colNome = new TableColumn<>("Nome da Peça");
        colNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().get("nome")
        ));

        TableColumn<Map<String, String>, String> colMedia = new TableColumn<>("Média de Faturamento");
        colMedia.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().get("mediaFaturamento")
        ));

        tabela.getColumns().addAll(colNome, colMedia);

        // Converter dados para o formato esperado pela tabela
        if (dados != null) {
            dados.forEach(item -> {
                tabela.getItems().add(Map.of(
                    "nome", (String) item.get("nome"),
                    "mediaFaturamento", (String) item.get("mediaFaturamento")
                ));
            });
        }

        return tabela;
    }

    private String formatarEstatistica(Map<String, Object> estatisticas, String chave) {
        if (estatisticas == null || !estatisticas.containsKey(chave)) {
            return "Dados não disponíveis";
        }

        Map<String, Object> dados = (Map<String, Object>) estatisticas.get(chave);
        
        switch (chave) {
            case "pecaMaisVendida":
            case "pecaMenosVendida":
                return String.format("%s (%d ingressos)", 
                    dados.get("nome"), 
                    dados.get("totalVendas"));
                
            case "sessaoMaiorOcupacao":
            case "sessaoMenorOcupacao":
                return String.format("%s - %s %s (%s)", 
                    dados.get("nome"),
                    dados.get("data"),
                    dados.get("horario"),
                    dados.get("ocupacao"));
                
            case "pecaMaisLucrativa":
            case "pecaMenosLucrativa":
                return String.format("%s (%s)", 
                    dados.get("nome"),
                    dados.get("faturamento"));
                
            default:
                return "Formato não suportado";
        }
    }
} 