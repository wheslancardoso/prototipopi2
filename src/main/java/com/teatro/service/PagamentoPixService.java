package com.teatro.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
// Removido import não utilizado

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Serviço para gerenciamento de pagamentos via PIX.
 * Gera QR codes e simula a confirmação de pagamento.
 */
public class PagamentoPixService {
    
    private static final int WIDTH = 250;
    private static final int HEIGHT = 250;
    private static final String PIX_PREFIX = "00020126330014BR.GOV.BCB.PIX0111";
    private static final String PIX_SUFFIX = "5204000053039865802BR5913Teatro Exemplo6008BRASILIA62070503***6304";
    
    private final Map<String, PagamentoPix> pagamentosAtivos = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @SuppressWarnings("unused") // Campos são usados na lógica de negócio
    private static class PagamentoPix {
        @SuppressWarnings("unused") // Usado na geração do QR Code
        String id;
        @SuppressWarnings("unused") // Pode ser usado para validação futura
        double valor;
        @SuppressWarnings("unused") // Pode ser usado para exibição ao usuário
        String descricao;
        boolean pago;
        long timestamp;
        
        PagamentoPix(String id, double valor, String descricao) {
            this.id = id;
            this.valor = valor;
            this.descricao = descricao;
            this.pago = false;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * Gera um novo QR Code PIX para pagamento.
     * @param valor Valor do pagamento
     * @param descricao Descrição do pagamento
     * @return ImageView contendo o QR Code gerado
     */
    public ImageView gerarQRCodePix(double valor, String descricao) {
        try {
            // Gera um ID único para o pagamento
            String pagamentoId = UUID.randomUUID().toString().substring(0, 8);
            
            // Cria a string PIX Copia e Cola
            String pixPayload = String.format("%s%s%04d%s", 
                PIX_PREFIX, 
                pagamentoId, 
                (int)(valor * 100), 
                PIX_SUFFIX);
            
            // Gera o QR Code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(pixPayload, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
            
            // Converte para imagem JavaFX
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            Image qrImage = new Image(inputStream);
            
            // Cria e armazena o pagamento
            PagamentoPix pagamento = new PagamentoPix(pagamentoId, valor, descricao);
            pagamentosAtivos.put(pagamentoId, pagamento);
            
            // Agenda a limpeza do pagamento após 30 minutos
            scheduler.schedule(() -> {
                pagamentosAtivos.remove(pagamentoId);
            }, 30, TimeUnit.MINUTES);
            
            return new ImageView(qrImage);
            
        } catch (WriterException e) {
            e.printStackTrace();
            // Retorna um QR Code de erro
            return criarQRCodeDeErro();
        } catch (Exception e) {
            e.printStackTrace();
            return criarQRCodeDeErro();
        }
    }
    
    /**
     * Verifica se um pagamento foi confirmado.
     * @param pagamentoId ID do pagamento
     * @return true se o pagamento foi confirmado, false caso contrário
     */
    public boolean verificarPagamento(String pagamentoId) {
        PagamentoPix pagamento = pagamentosAtivos.get(pagamentoId);
        if (pagamento == null) {
            return false; // Pagamento não encontrado ou expirado
        }
        
        // Simula confirmação de pagamento após 10-20 segundos
        if (!pagamento.pago && (System.currentTimeMillis() - pagamento.timestamp > 10000)) {
            // 10% de chance de falha para simular pagamento não aprovado
            if (new Random().nextInt(10) < 1) { // 10% de chance de falha
                pagamentosAtivos.remove(pagamentoId);
                return false;
            }
            
            pagamento.pago = true;
            // Agenda a remoção do pagamento após 5 minutos da confirmação
            scheduler.schedule(() -> {
                pagamentosAtivos.remove(pagamentoId);
            }, 5, TimeUnit.MINUTES);
        }
        
        return pagamento.pago;
    }
    
    /**
     * Inicia a verificação periódica de um pagamento.
     * @param pagamentoId ID do pagamento
     * @param onPagamentoConfirmado Callback chamado quando o pagamento é confirmado
     * @param onFalha Callback chamado se o pagamento falhar ou expirar
     */
    public void verificarPagamentoPeriodicamente(String pagamentoId, Runnable onPagamentoConfirmado, Runnable onFalha) {
        final long startTime = System.currentTimeMillis();
        final long timeout = 300; // 5 minutos em segundos
        
        scheduler.scheduleAtFixedRate(() -> {
            if (verificarPagamento(pagamentoId)) {
                onPagamentoConfirmado.run();
                throw new RuntimeException("Pagamento confirmado"); // Para o agendamento
            } else if (System.currentTimeMillis() - startTime > timeout * 1000) {
                onFalha.run();
                throw new RuntimeException("Tempo esgotado"); // Para o agendamento
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    
    /**
     * Cria um QR Code de erro para ser exibido em caso de falha.
     */
    private ImageView criarQRCodeDeErro() {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode("ERRO: Não foi possível gerar o QR Code", 
                BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            
            return new ImageView(new Image(inputStream));
        } catch (Exception e) {
            // Se não conseguir gerar nem o QR Code de erro, retorna um ImageView vazio
            return new ImageView();
        }
    }
    
    /**
     * Retorna o mapa de pagamentos ativos.
     * @return Mapa de pagamentos ativos
     */
    public Map<String, PagamentoPix> getPagamentosAtivos() {
        return new HashMap<>(pagamentosAtivos);
    }
    
    /**
     * Para o serviço e libera recursos.
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}
