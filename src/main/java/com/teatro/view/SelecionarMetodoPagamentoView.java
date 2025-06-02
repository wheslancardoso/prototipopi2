package com.teatro.view;

import com.teatro.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class SelecionarMetodoPagamentoView extends VBox {
    
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#2ecc71";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String CARD_BACKGROUND = "white";
    
    public SelecionarMetodoPagamentoView(Stage stage, Usuario usuario, List<IngressoModerno> ingressos, Runnable onPagamentoConcluido) {
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Título
        Label titulo = new Label("Selecione a forma de pagamento");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web(TEXT_COLOR));
        
        // Container dos métodos de pagamento
        HBox metodosContainer = new HBox(20);
        metodosContainer.setAlignment(Pos.CENTER);
        
        // Cartão de Crédito/Débito
        VBox cartaoCard = criarMetodoPagamentoCard(
            "Cartão de Crédito/Débito", 
            "Pague com cartão de crédito ou débito",
            "🎫"
        );
        cartaoCard.setOnMouseClicked(e -> {
            mostrarFormularioCartao(stage, usuario, ingressos, onPagamentoConcluido);
        });
        
        // PIX
        VBox pixCard = criarMetodoPagamentoCard(
            "PIX", 
            "Pague via PIX com QR Code",
            "🏦"
        );
        pixCard.setOnMouseClicked(e -> {
            mostrarQRCodePix(stage, usuario, ingressos, onPagamentoConcluido);
        });
        
        metodosContainer.getChildren().addAll(cartaoCard, pixCard);
        
        // Botão de voltar
        Button btnVoltar = new Button("Voltar");
        btnVoltar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnVoltar.setOnAction(e -> stage.close());
        
        getChildren().addAll(titulo, metodosContainer, btnVoltar);
    }
    
    private VBox criarMetodoPagamentoCard(String titulo, String descricao, String icone) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 200px;" +
            "-fx-min-height: 150px;"
        );
        
        // Efeito de hover
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0);", "")));
        
        Label lblIcone = new Label(icone);
        lblIcone.setFont(Font.font(36));
        
        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.web(TEXT_COLOR));
        
        Label lblDescricao = new Label(descricao);
        lblDescricao.setWrapText(true);
        lblDescricao.setMaxWidth(200);
        lblDescricao.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(lblIcone, lblTitulo, lblDescricao);
        return card;
    }
    
    private void mostrarFormularioCartao(Stage parentStage, Usuario usuario, List<IngressoModerno> ingressos, Runnable onPagamentoConcluido) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Pagamento com Cartão");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        // Seletor de tipo de cartão
        ToggleGroup tipoCartaoGroup = new ToggleGroup();
        RadioButton credito = new RadioButton("Crédito");
        credito.setToggleGroup(tipoCartaoGroup);
        credito.setSelected(true);
        RadioButton debito = new RadioButton("Débito");
        debito.setToggleGroup(tipoCartaoGroup);
        
        HBox tipoCartaoBox = new HBox(20, credito, debito);
        tipoCartaoBox.setAlignment(Pos.CENTER);
        
        // Campos do cartão
        TextField txtNumero = new TextField();
        txtNumero.setPromptText("Número do cartão");
        txtNumero.setMaxWidth(250);
        
        HBox validadeCVVBox = new HBox(10);
        TextField txtValidade = new TextField();
        txtValidade.setPromptText("MM/AA");
        txtValidade.setMaxWidth(80);
        
        TextField txtCVV = new TextField();
        txtCVV.setPromptText("CVV");
        txtCVV.setMaxWidth(60);
        
        validadeCVVBox.getChildren().addAll(new Label("Validade:"), txtValidade, new Label("CVV:"), txtCVV);
        
        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome impresso no cartão");
        txtNome.setMaxWidth(250);
        
        // Botões
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER);
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnCancelar.setOnAction(e -> dialog.close());
        
        Button btnPagar = new Button("Pagar");
        btnPagar.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPagar.setOnAction(e -> {
            // Simula processamento do pagamento
            simularProcessamentoPagamento(dialog, onPagamentoConcluido, "Cartão de " + (credito.isSelected() ? "Crédito" : "Débito"));
        });
        
        botoesBox.getChildren().addAll(btnCancelar, btnPagar);
        
        root.getChildren().addAll(
            new Label("Tipo de Cartão:"),
            tipoCartaoBox,
            new Label("Número do Cartão:"),
            txtNumero,
            validadeCVVBox,
            new Label("Nome no Cartão:"),
            txtNome,
            botoesBox
        );
        
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void mostrarQRCodePix(Stage parentStage, Usuario usuario, List<IngressoModerno> ingressos, Runnable onPagamentoConcluido) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Pagamento via PIX");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        
        // Cálculo do valor total
        double valorTotal = ingressos.stream()
            .mapToDouble(IngressoModerno::getValor)
            .sum();
        
        Label lblInstrucao = new Label("Escaneie o QR Code com seu app bancário");
        lblInstrucao.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // QR Code simulado (apenas um quadrado com texto)
        StackPane qrCode = new StackPane();
        qrCode.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-width: 2px;" +
            "-fx-min-width: 200px;" +
            "-fx-min-height: 200px;"
        );
        
        Label lblQRCode = new Label("PIX\nQR Code\nSimulado");
        lblQRCode.setStyle("-fx-text-alignment: center;");
        lblQRCode.setAlignment(Pos.CENTER);
        qrCode.getChildren().add(lblQRCode);
        
        Label lblValor = new Label(String.format("Valor: R$ %.2f", valorTotal));
        lblValor.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        Label lblAguardando = new Label("Aguardando confirmação do pagamento...");
        
        ProgressIndicator progress = new ProgressIndicator();
        progress.setVisible(false);
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnCancelar.setOnAction(e -> dialog.close());
        
        // Simula o pagamento após 3 segundos
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    progress.setVisible(true);
                    lblAguardando.setText("Pagamento aprovado!");
                    
                    // Fecha o diálogo após 1 segundo
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            javafx.application.Platform.runLater(() -> {
                                dialog.close();
                                onPagamentoConcluido.run();
                            });
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        root.getChildren().addAll(
            lblInstrucao,
            qrCode,
            lblValor,
            lblAguardando,
            progress,
            btnCancelar
        );
        
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void simularProcessamentoPagamento(Stage dialog, Runnable onPagamentoConcluido, String metodo) {
        // Simula processamento do pagamento
        ProgressIndicator progress = new ProgressIndicator();
        Label statusLabel = new Label("Processando pagamento...");
        
        VBox content = new VBox(20, progress, statusLabel);
        content.setAlignment(Pos.CENTER);
        
        Scene currentScene = dialog.getScene();
        currentScene.setRoot(content);
        
        // Simula tempo de processamento
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2 segundos de processamento
                
                // Atualiza a UI na thread do JavaFX
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Pagamento aprovado via " + metodo + "!");
                    progress.setVisible(false);
                    
                    // Fecha o diálogo após 1 segundo
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            javafx.application.Platform.runLater(() -> {
                                dialog.close();
                                onPagamentoConcluido.run();
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
