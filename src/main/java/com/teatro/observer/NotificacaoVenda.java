package com.teatro.observer;

import com.teatro.model.Ingresso;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma notificação de venda de ingresso.
 */
public class NotificacaoVenda {
    
    private final Ingresso ingresso;
    private final String mensagem;
    private final String dataHora;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Cria uma nova notificação de venda.
     * @param ingresso O ingresso vendido
     */
    public NotificacaoVenda(Ingresso ingresso) {
        this.ingresso = ingresso;
        this.dataHora = ingresso.getDataCompra().toLocalDateTime().format(FORMATTER);
        this.mensagem = gerarMensagem();
    }
    
    /**
     * Gera a mensagem da notificação.
     * @return A mensagem formatada
     */
    private String gerarMensagem() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nova venda de ingresso:\n");
        sb.append("Evento: ").append(ingresso.getEventoNome()).append("\n");
        sb.append("Horário: ").append(ingresso.getHorario()).append("\n");
        sb.append("Área: ").append(ingresso.getAreaNome()).append("\n");
        sb.append("Poltrona: ").append(ingresso.getNumeroPoltrona()).append("\n");
        sb.append("Valor: R$ ").append(String.format("%.2f", ingresso.getValor())).append("\n");
        sb.append("Data/Hora: ").append(dataHora).append("\n");
        sb.append("Código: ").append(ingresso.getCodigo());
        return sb.toString();
    }
    
    /**
     * Obtém o ingresso da notificação.
     * @return O ingresso vendido
     */
    public Ingresso getIngresso() {
        return ingresso;
    }
    
    /**
     * Obtém a mensagem da notificação.
     * @return A mensagem formatada
     */
    public String getMensagem() {
        return mensagem;
    }
    
    /**
     * Obtém a data e hora da notificação.
     * @return A data e hora formatada
     */
    public String getDataHora() {
        return dataHora;
    }
    
    @Override
    public String toString() {
        return mensagem;
    }
} 