package com.teatro.util;

import com.google.zxing.BarcodeFormat;
// Removido import não utilizado
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Utilitário para geração de códigos QR.
 */
public class QRCodeUtil {
    
    private QRCodeUtil() {
        // Construtor privado para evitar instanciação
    }
    
    /**
     * Gera um ImageView contendo um QR Code a partir de um texto.
     *
     * @param text O texto a ser codificado no QR Code
     * @param width Largura da imagem do QR Code
     * @param height Altura da imagem do QR Code
     * @return ImageView contendo o QR Code gerado
     */
    public static ImageView generateQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            
            ImageView imageView = new ImageView(new Image(inputStream));
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(true);
            
            return imageView;
            
        } catch (Exception e) {
            e.printStackTrace();
            return generateErrorQRCode(width, height);
        }
    }
    
    /**
     * Gera um QR Code de erro.
     */
    private static ImageView generateErrorQRCode(int width, int height) {
        try {
            return generateQRCode("ERRO: Não foi possível gerar o QR Code", width, height);
        } catch (Exception e) {
            // Se não conseguir gerar nem o QR Code de erro, retorna um ImageView vazio
            ImageView errorView = new ImageView();
            errorView.setFitWidth(width);
            errorView.setFitHeight(height);
            return errorView;
        }
    }
}
