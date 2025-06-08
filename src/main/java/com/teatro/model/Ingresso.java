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
    
    // Campos para exibição
    private String eventoNome;
    private String horario;
    private String areaNome;
    private String codigo;
    private TipoSessao tipoSessao;
    private Timestamp dataSessao;

    public Ingresso() {
    }

    public Ingresso(Long usuarioId, Long sessaoId, Long areaId, int numeroPoltrona, double valor) {
        this.usuarioId = usuarioId;
        this.sessaoId = sessaoId;
        this.areaId = areaId;
        this.numeroPoltrona = numeroPoltrona;
        this.valor = valor;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoSessao getTipoSessao() {
        return tipoSessao;
    }

    public void setTipoSessao(TipoSessao tipoSessao) {
        this.tipoSessao = tipoSessao;
    }

    public Timestamp getDataSessao() {
        return dataSessao;
    }

    public void setDataSessao(Timestamp dataSessao) {
        this.dataSessao = dataSessao;
    }

    @Override
    public String toString() {
        return String.format("""
            ==========================================
            INGRESSO - TEATRO
            ==========================================
            Evento: %s
            Horário: %s
            Área: %s
            Poltrona: %d
            Valor: R$ %.2f
            Data da Compra: %s
            ==========================================
            """,
            eventoNome,
            horario,
            areaNome,
            numeroPoltrona,
            valor,
            dataCompra);
    }
} 