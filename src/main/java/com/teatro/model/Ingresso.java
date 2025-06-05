package com.teatro.model;

import java.sql.Timestamp;

public class Ingresso {
    private Long id;
    private Long usuarioId;
    private Long sessaoId;
    private Long areaId;
    private int numeroPoltrona;
    private double valor;
    private Timestamp dataCompra;
    private Long horarioEspecificoId; // ID do horário específico (14:00, 15:30, etc.)
    private Timestamp dataSessao; // Data da sessão para a qual o ingresso foi comprado
    
    // Campos para exibição
    private String eventoNome;
    private String horario;
    private String areaNome;

    public Ingresso() {
    }

    public Ingresso(Long usuarioId, Long sessaoId, Long areaId, int numeroPoltrona, double valor) {
        this.usuarioId = usuarioId;
        this.sessaoId = sessaoId;
        this.areaId = areaId;
        this.numeroPoltrona = numeroPoltrona;
        this.valor = valor;
        this.horarioEspecificoId = null;
    }
    
    public Ingresso(Long usuarioId, Long sessaoId, Long areaId, int numeroPoltrona, double valor, Long horarioEspecificoId) {
        this.usuarioId = usuarioId;
        this.sessaoId = sessaoId;
        this.areaId = areaId;
        this.numeroPoltrona = numeroPoltrona;
        this.valor = valor;
        this.horarioEspecificoId = horarioEspecificoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getSessaoId() {
        return sessaoId;
    }

    public void setSessaoId(Long sessaoId) {
        this.sessaoId = sessaoId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public int getNumeroPoltrona() {
        return numeroPoltrona;
    }

    public void setNumeroPoltrona(int numeroPoltrona) {
        this.numeroPoltrona = numeroPoltrona;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Timestamp getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(Timestamp dataCompra) {
        this.dataCompra = dataCompra;
    }

    public String getEventoNome() {
        return eventoNome;
    }

    public void setEventoNome(String eventoNome) {
        this.eventoNome = eventoNome;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getAreaNome() {
        return areaNome;
    }

    public void setAreaNome(String areaNome) {
        this.areaNome = areaNome;
    }

    public Long getHorarioEspecificoId() {
        return horarioEspecificoId;
    }

    public void setHorarioEspecificoId(Long horarioEspecificoId) {
        this.horarioEspecificoId = horarioEspecificoId;
    }
    
    public Timestamp getDataSessao() {
        return dataSessao;
    }
    
    public void setDataSessao(Timestamp dataSessao) {
        this.dataSessao = dataSessao;
    }

    @Override
    public String toString() {
        // Extrai o nome da sessão (parte antes do hífen) e o horário (parte depois do hífen)
        String[] partesHorario = horario != null ? horario.split(" - ", 2) : new String[]{"", ""};
        String nomeSessao = partesHorario.length > 0 ? partesHorario[0].trim() : "";
        String horarioSessao = partesHorario.length > 1 ? partesHorario[1].trim() : horario;
        
        return String.format("""
            ==========================================
            INGRESSO - TEATRO
            ==========================================
            Evento: %s
            Sessão: %s
            Horário: %s
            Área: %s
            Poltrona: %d
            Valor: R$ %.2f
            Data da Compra: %s
            ==========================================
            """,
            eventoNome,
            nomeSessao,
            horarioSessao,
            areaNome,
            numeroPoltrona,
            valor,
            dataCompra);
    }
} 