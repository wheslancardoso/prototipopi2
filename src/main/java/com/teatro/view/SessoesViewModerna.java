package com.teatro.view;

import com.teatro.controller.SessaoController;
import com.teatro.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Versão modernizada da tela de seleção de sessões.
 */
public class SessoesViewModerna {
    private Teatro teatro;
    private Usuario usuario;
    private Stage stage;
    private SessaoController sessaoController;
    
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Cores do tema
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#2ecc71";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String CARD_BACKGROUND = "white";

    public SessoesViewModerna(Teatro teatro, Usuario usuario, Stage stage) {
        this.teatro = teatro;
        this.usuario = usuario;
        this.stage = stage;
        this.sessaoController = new SessaoController();
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Sessões Disponíveis");

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
        Label pageTitle = new Label("Sessões Disponíveis");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        pageTitle.setTextFill(Color.web(TEXT_COLOR));
        
        // Adiciona os cards de eventos
        VBox eventsContainer = new VBox(25);
        for (Evento evento : teatro.getEventos()) {
            eventsContainer.getChildren().add(criarCardEvento(evento));
        }
        
        contentContainer.getChildren().addAll(pageTitle, eventsContainer);
        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
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
        
        // Botão voltar para o dashboard
        Button homeButton = new Button("Dashboard");
        homeButton.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold; -fx-cursor: hand;");
        
        homeButton.setOnAction(e -> {
            new DashboardView(teatro, usuario, stage).show();
        });
        
        // Espaçador para empurrar o userInfo para a direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topBar.getChildren().addAll(systemTitle, homeButton, spacer, userInfo);
        
        return topBar;
    }

    private VBox criarCardEvento(Evento evento) {
        VBox card = new VBox();
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 20px;" +
            "-fx-spacing: 15px;");

        // Título do evento
        Label titulo = new Label(evento.getNome());
        titulo.setFont(Font.font("System", FontWeight.BOLD, 22));
        titulo.setTextFill(Color.web(TEXT_COLOR));
        
        // Separador
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");
        
        // Container para os controles de seleção
        GridPane selectionGrid = new GridPane();
        selectionGrid.setHgap(15);
        selectionGrid.setVgap(15);
        selectionGrid.setAlignment(Pos.CENTER_LEFT);
        
        // Data da sessão
        Label dataLabel = new Label("Data:");
        dataLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(
            "-fx-pref-width: 200;" +
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;");
        
        // Converter para formatar a data no padrão brasileiro
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return date.format(DATE_FORMATTER);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, DATE_FORMATTER);
                }
                return null;
            }
        };
        
        datePicker.setConverter(converter);
        datePicker.setPromptText("Selecione uma data");
        
        // Tipo de sessão (Manhã, Tarde, Noite)
        Label sessaoLabel = new Label("Sessão:");
        sessaoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        ComboBox<String> sessaoComboBox = new ComboBox<>();
        sessaoComboBox.getItems().addAll("Manhã", "Tarde", "Noite");
        sessaoComboBox.setPromptText("Selecione uma sessão");
        sessaoComboBox.setStyle("-fx-pref-width: 200; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 4;");
        
        // Horário específico
        Label horarioLabel = new Label("Horário:");
        horarioLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        ComboBox<HorarioDisponivel> horarioComboBox = new ComboBox<>();
        horarioComboBox.setPromptText("Selecione um horário");
        horarioComboBox.setDisable(true); // Inicialmente desabilitado
        horarioComboBox.setStyle(
            "-fx-pref-width: 200;" +
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;");
        
        // Atualiza os horários disponíveis quando o tipo de sessão é selecionado
        sessaoComboBox.setOnAction(e -> {
            String tipoSessao = sessaoComboBox.getValue();
            if (tipoSessao != null) {
                LocalDate dataSelecionada = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
                
                // Busca os horários disponíveis para o tipo de sessão
                List<HorarioDisponivel> horarios = sessaoController.buscarHorariosPorTipoSessao(tipoSessao);
                
                // Filtra os horários disponíveis de acordo com a data selecionada
                horarios = sessaoController.filtrarHorariosDisponiveis(horarios, dataSelecionada);
                
                horarioComboBox.getItems().clear();
                if (!horarios.isEmpty()) {
                    horarioComboBox.getItems().addAll(horarios);
                    horarioComboBox.setDisable(false);
                } else {
                    horarioComboBox.setDisable(true);
                    horarioComboBox.setPromptText("Nenhum horário disponível");
                }
            } else {
                horarioComboBox.getItems().clear();
                horarioComboBox.setDisable(true);
                horarioComboBox.setPromptText("Selecione um horário");
            }
        });
        
        // Atualiza os horários quando a data é alterada
        datePicker.setOnAction(e -> {
            String tipoSessao = sessaoComboBox.getValue();
            if (tipoSessao != null) {
                LocalDate dataSelecionada = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
                
                // Busca os horários disponíveis para o tipo de sessão
                List<HorarioDisponivel> horarios = sessaoController.buscarHorariosPorTipoSessao(tipoSessao);
                
                // Filtra os horários disponíveis de acordo com a data selecionada
                horarios = sessaoController.filtrarHorariosDisponiveis(horarios, dataSelecionada);
                
                horarioComboBox.getItems().clear();
                if (!horarios.isEmpty()) {
                    horarioComboBox.getItems().addAll(horarios);
                    horarioComboBox.setDisable(false);
                } else {
                    horarioComboBox.setDisable(true);
                    horarioComboBox.setPromptText("Nenhum horário disponível");
                }
            }
        });
        
        // Botão para continuar
        Button continuarButton = new Button("Continuar");
        continuarButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 4; -fx-cursor: hand;");
        continuarButton.setDisable(true);
        
        // Habilita o botão quando todos os campos estão preenchidos
        horarioComboBox.setOnAction(e -> {
            continuarButton.setDisable(horarioComboBox.getValue() == null);
        });
        
        continuarButton.setOnAction(e -> {
            LocalDate dataSelecionada = datePicker.getValue();
            String tipoSessao = sessaoComboBox.getValue();
            HorarioDisponivel horarioSelecionado = horarioComboBox.getValue();
            
            if (dataSelecionada != null && tipoSessao != null && horarioSelecionado != null) {
                // Busca a sessão correspondente ao evento e tipo de sessão
                Sessao sessaoSelecionada = null;
                for (Sessao sessao : evento.getSessoes()) {
                    if (sessao.getHorario().equals(tipoSessao)) {
                        sessaoSelecionada = sessao;
                        break;
                    }
                }
                
                if (sessaoSelecionada != null) {
                    // Atualiza a data e o horário específico da sessão
                    sessaoSelecionada.setDataSessao(dataSelecionada);
                    sessaoSelecionada.setHorarioEspecifico(horarioSelecionado);
                    
                    // Abre a tela de compra de ingressos
                    new CompraIngressoViewModerna(teatro, usuario, stage, sessaoSelecionada).show();
                }
            }
        });
        
        // Adiciona os componentes ao grid
        selectionGrid.add(dataLabel, 0, 0);
        selectionGrid.add(datePicker, 1, 0);
        selectionGrid.add(sessaoLabel, 0, 1);
        selectionGrid.add(sessaoComboBox, 1, 1);
        selectionGrid.add(horarioLabel, 0, 2);
        selectionGrid.add(horarioComboBox, 1, 2);
        selectionGrid.add(continuarButton, 1, 3);
        
        card.getChildren().addAll(titulo, separator, selectionGrid);
        return card;
    }
}
