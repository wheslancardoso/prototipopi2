package com.teatro.service;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Serviço simplificado para simulação de pagamento PIX.
 * Gera um QR code estático para demonstração.
 */
public class PixService {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    
    private ImageView criarQRCodePlaceholder() {
        // Cria um StackPane simples com um retângulo e um texto
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(250, 250);
        stackPane.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: lightgray;" +
            "-fx-border-width: 2px;"
        );
        
        Label label = new Label("QR Code\nIndisponível");
        label.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
        stackPane.getChildren().add(label);
        
        // Cria um ImageView vazio (o StackPane será adicionado diretamente à cena)
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(250);
        return imageView;
    }
    
    /**
     * Gera um QR code de exemplo para pagamento PIX.
     * @param valor Valor do pagamento
     * @param descricao Descrição do pagamento
     * @return ImageView com o QR code
     */
    public ImageView gerarQRCodePix(double valor, String descricao) {
        // Retorna um placeholder simples
        // Em uma aplicação real, aqui seria gerado um QR code real
        return criarQRCodePlaceholder();
    }
    
    /**
     * Verifica o status do pagamento de forma assíncrona.
     * @param onPagamentoConfirmado Callback chamado quando o pagamento é confirmado
     * @param onFalha Callback chamado se o pagamento falhar
     */
    public void verificarPagamento(Runnable onPagamentoConfirmado, Runnable onFalha) {
        // Simula uma verificação de pagamento após 5-10 segundos
        int delay = 5 + random.nextInt(6); // 5-10 segundos
        
        scheduler.schedule(() -> {
            // 80% de chance de sucesso, 20% de falha
            if (random.nextDouble() < 0.8) {
                Platform.runLater(onPagamentoConfirmado);
            } else {
                Platform.runLater(onFalha);
            }
        }, delay, TimeUnit.SECONDS);
    }
    
    /**
     * Para o serviço e libera recursos.
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}
