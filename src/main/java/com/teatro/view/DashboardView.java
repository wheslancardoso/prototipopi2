package com.teatro.view;

import com.teatro.model.*;
import com.teatro.dao.IngressoDAO;
import com.teatro.view.ImpressaoIngressoViewModerna;
import com.teatro.view.LoginViewModerna;
import com.teatro.view.SessoesViewModerna;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Map;
import java.util.List;

public class DashboardView {
    private Teatro teatro;
    private Usuario usuarioLogado;
    private Stage stage;
    private IngressoDAO ingressoDAO;

    public DashboardView(Teatro teatro, Usuario usuarioLogado, Stage stage) {
        this.teatro = teatro;
        this.usuarioLogado = usuarioLogado;
        this.stage = stage;
        this.ingressoDAO = new IngressoDAO();
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Dashboard");

        // Layout principal
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Menu lateral
        VBox menuLateral = criarMenuLateral();
        root.setLeft(menuLateral);

        // Área principal
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        // Verifica se é admin para mostrar estatísticas ou tela padrão
        if ("ADMIN".equals(usuarioLogado.getTipoUsuario())) {
            scrollPane.setContent(criarAreaPrincipalAdmin());
        } else {
            scrollPane.setContent(criarAreaPrincipalUsuario());
        }
        
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.show();
    }

    private VBox criarMenuLateral() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #2c3e50;");
        menu.setPrefWidth(200);

        Button btnDashboard = new Button("Dashboard");
        Button btnComprar = new Button("Comprar Ingresso");
        Button btnImprimir = new Button("Imprimir Ingresso");
        Button btnSair = new Button("Sair");

        // Estilização dos botões
        String buttonStyle = """
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-min-width: 180;
            -fx-min-height: 40;
            -fx-cursor: hand;
            """;

        btnDashboard.setStyle(buttonStyle);
        btnComprar.setStyle(buttonStyle);
        btnImprimir.setStyle(buttonStyle);
        btnSair.setStyle(buttonStyle.replace("#3498db", "#e74c3c"));

        menu.getChildren().addAll(btnDashboard, btnComprar, btnImprimir, btnSair);

        // Ações dos botões
        btnDashboard.setOnAction(e -> mostrarDashboard());
        btnComprar.setOnAction(e -> mostrarTelaCompra());
        btnImprimir.setOnAction(e -> mostrarTelaImpressao());
        btnSair.setOnAction(e -> {
            new LoginViewModerna(stage).show();
        });

        return menu;
    }

    private VBox criarAreaPrincipalUsuario() {
        VBox area = new VBox(20);
        area.setPadding(new Insets(10));
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
        area.getChildren().addAll(lblBoasVindas, cardInstrucoes);

        return area;
    }

    private HBox criarItemInstrucao(String titulo, String descricao) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);

        Label lblBullet = new Label("•");
        lblBullet.setStyle("-fx-font-size: 20; -fx-text-fill: #3498db;");

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
        area.setPadding(new Insets(10));

        // Buscar estatísticas
        Map<String, Object> estatisticas = ingressoDAO.buscarEstatisticasVendas();

        // Título do Dashboard Admin
        Label lblTituloDashboard = new Label("Dashboard Administrativo");
        lblTituloDashboard.setStyle("""
            -fx-font-size: 24;
            -fx-font-weight: bold;
            -fx-text-fill: #2c3e50;
            -fx-padding: 0 0 20 0;
            """);

        // Seção de Vendas
        VBox secaoVendas = new VBox(10);
        secaoVendas.getChildren().addAll(
            criarTituloSecao("Estatísticas de Vendas"),
            criarGridEstatisticas(
                new String[][] {
                    {"Peça Mais Vendida", formatarEstatistica(estatisticas, "pecaMaisVendida")},
                    {"Peça Menos Vendida", formatarEstatistica(estatisticas, "pecaMenosVendida")}
                }
            )
        );

        // Seção de Ocupação
        VBox secaoOcupacao = new VBox(10);
        secaoOcupacao.getChildren().addAll(
            criarTituloSecao("Estatísticas de Ocupação"),
            criarGridEstatisticas(
                new String[][] {
                    {"Sessão com Maior Ocupação", formatarEstatistica(estatisticas, "sessaoMaiorOcupacao")},
                    {"Sessão com Menor Ocupação", formatarEstatistica(estatisticas, "sessaoMenorOcupacao")}
                }
            )
        );

        // Seção de Faturamento
        VBox secaoFaturamento = new VBox(10);
        secaoFaturamento.getChildren().addAll(
            criarTituloSecao("Estatísticas de Faturamento"),
            criarGridEstatisticas(
                new String[][] {
                    {"Peça Mais Lucrativa", formatarEstatistica(estatisticas, "pecaMaisLucrativa")},
                    {"Peça Menos Lucrativa", formatarEstatistica(estatisticas, "pecaMenosLucrativa")}
                }
            )
        );

        // Seção de Lucro Médio
        VBox secaoLucroMedio = new VBox(10);
        secaoLucroMedio.getChildren().addAll(
            criarTituloSecao("Lucro Médio por Peça"),
            criarTabelaLucroMedio((List<Map<String, Object>>) estatisticas.get("lucroMedioPorPeca"))
        );

        area.getChildren().addAll(
            lblTituloDashboard,
            secaoVendas,
            secaoOcupacao,
            secaoFaturamento,
            secaoLucroMedio
        );
        return area;
    }

    private Label criarTituloSecao(String titulo) {
        Label label = new Label(titulo);
        label.setStyle("""
            -fx-font-size: 20;
            -fx-font-weight: bold;
            -fx-text-fill: #2c3e50;
            -fx-padding: 10 0 5 0;
            """);
        return label;
    }

    private GridPane criarGridEstatisticas(String[][] dados) {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.setStyle("""
            -fx-background-color: white;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            -fx-background-radius: 5;
            """);

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

    private void mostrarDashboard() {
        show();
    }

    private void mostrarTelaCompra() {
        new SessoesViewModerna(teatro, usuarioLogado, stage).show();
    }

    private void mostrarTelaImpressao() {
        new ImpressaoIngressoViewModerna(teatro, usuarioLogado, stage, List.of()).show();
    }
} 