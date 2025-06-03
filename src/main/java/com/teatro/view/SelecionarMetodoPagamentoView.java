package com.teatro.view;

import java.util.List;

import com.teatro.model.Usuario;
import com.teatro.model.IngressoModerno;
import com.teatro.service.PagamentoPixService;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SelecionarMetodoPagamentoView extends VBox {
    
    private static final String SECONDARY_COLOR = "#2ecc71";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";

    
    public SelecionarMetodoPagamentoView(Stage stage, Usuario usuario, List<IngressoModerno> ingressos, Runnable onPagamentoConcluido) {
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Título
        Label titulo = new Label("Selecione a forma de pagamento");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setStyle("-fx-text-fill: #00008B;");
        
        // Container dos métodos de pagamento
        HBox metodosContainer = new HBox(20);
        metodosContainer.setAlignment(Pos.CENTER);
        
        // Cartão de Crédito/Débito
        VBox cartaoCard = criarMetodoPagamentoCard(
            "Cartão de Crédito/Débito", 
            "Pague com cartão de crédito ou débito",
            ""
        );
        cartaoCard.setOnMouseClicked(e -> {
            mostrarFormularioCartao(stage, usuario, ingressos, onPagamentoConcluido);
        });
        
        // PIX
        VBox pixCard = criarMetodoPagamentoCard(
            "PIX", 
            "Pague via PIX com QR Code",
            ""
        );
        pixCard.setOnMouseClicked(e -> {
            try {
                double valorTotal = ingressos.stream()
                    .mapToDouble(IngressoModerno::getValor)
                    .sum();
                mostrarQRCodePix(stage, valorTotal, onPagamentoConcluido);
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Erro ao processar pagamento PIX");
                alert.setContentText("Ocorreu um erro ao tentar processar o pagamento. Por favor, tente novamente.");
                alert.showAndWait();
            }
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
        String cardStyle = ""
            + "-fx-background-color: white;"
            + "-fx-border-color: #ddd;"
            + "-fx-border-radius: 8;"
            + "-fx-background-radius: 8;"
            + "-fx-padding: 20;"
            + "-fx-cursor: hand;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);";
        card.setStyle(cardStyle);
        
        // Efeito de hover
        String hoverStyle = ""
            + "-fx-background-color: #f8f8f8;"
            + "-fx-border-color: #0078d7;"
            + "-fx-border-radius: 8;"
            + "-fx-background-radius: 8;"
            + "-fx-padding: 20;"
            + "-fx-cursor: hand;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4);";
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(cardStyle));
        
        Label lblIcone = new Label(icone);
        lblIcone.setFont(Font.font(36));
        
        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblTitulo.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        
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
    
    private void mostrarQRCodePix(Stage stage, double valorTotal, Runnable onPagamentoConcluido) {
        try {
            // Cria um novo estágio para mostrar o QR Code
            Stage qrStage = new Stage();
            qrStage.initModality(Modality.APPLICATION_MODAL);
            qrStage.setTitle("Pagamento via PIX");
            
            // Layout principal
            VBox layout = new VBox(20);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));
            
            // Título
            Label tituloLabel = new Label("PAGAMENTO VIA PIX");
            tituloLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            
            // Cria o serviço PIX
            PagamentoPixService pixService = new PagamentoPixService();
            
            // Gera o QR Code
            ImageView qrCodeView = pixService.gerarQRCodePix(valorTotal, "Ingressos");
            qrCodeView.setFitWidth(250);
            qrCodeView.setFitHeight(250);
            
            // Obtém o ID do pagamento para verificação
            String pagamentoId = pixService.getPagamentosAtivos().keySet().iterator().next();
            
            // Informações do pagamento
            Label valorLabel = new Label(String.format("Valor: R$ %.2f", valorTotal));
            valorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            Label instrucoesLabel = new Label("Escaneie o QR Code com o app do seu banco");
            instrucoesLabel.setStyle("-fx-text-alignment: center;");
            
            // Status do pagamento
            Label statusLabel = new Label("Aguardando confirmação do pagamento...");
            statusLabel.setStyle("-fx-font-weight: bold;");
            
            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setVisible(true);
            
            // Botão para fechar
            Button fecharBtn = new Button("Fechar");
            fecharBtn.setDisable(true); // Desabilita até o pagamento ser confirmado
            fecharBtn.setOnAction(e -> {
                pixService.shutdown();
                qrStage.close();
            });
            
            // Adiciona os controles ao layout
            VBox codigoBox = new VBox(15, 
                tituloLabel,
                valorLabel,
                instrucoesLabel,
                qrCodeView,
                statusLabel,
                progressIndicator,
                fecharBtn
            );
            
            codigoBox.setAlignment(Pos.CENTER);
            codigoBox.setMaxWidth(300);
            
            layout.getChildren().add(codigoBox);
            
            // Configura a verificação periódica do pagamento
            pixService.verificarPagamentoPeriodicamente(
                pagamentoId,
                new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            // Pagamento confirmado
                            statusLabel.setText("Pagamento confirmado com sucesso!");
                            statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                            progressIndicator.setVisible(false);
                            fecharBtn.setDisable(false);
                            
                            // Fecha a janela do QR code e chama o callback após 3 segundos
                            new Thread(() -> {
                                try {
                                    Thread.sleep(3000);
                                    Platform.runLater(() -> {
                                        qrStage.close();
                                        // Executa o callback para mostrar a tela de confirmação
                                        onPagamentoConcluido.run();
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        });
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            // Falha no pagamento
                            statusLabel.setText("Falha no pagamento ou tempo esgotado!");
                            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            progressIndicator.setVisible(false);
                            fecharBtn.setDisable(false);
                        });
                    }
                }
            );
            
            // Fecha o serviço quando a janela for fechada
            qrStage.setOnHidden(e -> pixService.shutdown());
            
            // Configura a cena e mostra o estágio
            Scene scene = new Scene(layout, 400, 600);
            qrStage.setScene(scene);
            qrStage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao processar pagamento PIX");
            alert.setContentText("Ocorreu um erro ao tentar processar o pagamento. Por favor, tente novamente.");
            alert.showAndWait();
        }
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
                Platform.runLater(() -> {
                    statusLabel.setText("Pagamento aprovado via " + metodo + "!");
                    progress.setVisible(false);
                    
                    // Fecha o diálogo após 1 segundo
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            Platform.runLater(() -> {
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
