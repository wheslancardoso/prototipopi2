package com.teatro.model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Versão modernizada da classe Ingresso que mantém referências diretas aos objetos
 * ao invés de apenas IDs.
 */
public class IngressoModerno {
    private Long id;
    private String eventoNome;
    private String horario;
    private Timestamp dataSessao;
    private String areaNome;
    private int numeroPoltrona;
    private double valor;
    private Timestamp dataCompra;
    private String codigo;

    public IngressoModerno(Long id, String eventoNome, String horario, Timestamp dataSessao, 
                          String areaNome, int numeroPoltrona, double valor, 
                          Timestamp dataCompra, String codigo) {
        this.id = id;
        this.eventoNome = eventoNome;
        this.horario = horario;
        this.dataSessao = dataSessao;
        this.areaNome = areaNome;
        this.numeroPoltrona = numeroPoltrona;
        this.valor = valor;
        this.dataCompra = dataCompra;
        this.codigo = codigo;
    }

    public IngressoModerno(Sessao sessao, Area area, Poltrona poltrona, Usuario usuario) {
        this.eventoNome = sessao.getNome();
        this.horario = sessao.getHorario();
        this.dataSessao = sessao.getData();
        this.areaNome = area.getNome();
        this.numeroPoltrona = poltrona.getNumero();
        this.valor = area.getPreco();
        this.dataCompra = new Timestamp(System.currentTimeMillis());
        this.codigo = gerarCodigoIngresso();
    }

    public IngressoModerno(Ingresso ingresso, Evento evento, Sessao sessao, Area area) {
        this.id = ingresso.getId();
        this.eventoNome = evento.getNome();
        this.horario = sessao.getTipoSessao().getDescricao();
        this.dataSessao = sessao.getData();
        this.areaNome = area.getNome();
        this.numeroPoltrona = ingresso.getNumeroPoltrona();
        this.valor = area.getPreco();
        this.dataCompra = ingresso.getDataCompra();
        this.codigo = ingresso.getCodigo();
    }

    private String gerarCodigoIngresso() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Timestamp getDataSessao() {
        return dataSessao;
    }

    public void setDataSessao(Timestamp dataSessao) {
        this.dataSessao = dataSessao;
    }

    public String getAreaNome() {
        return areaNome;
    }

    public void setAreaNome(String areaNome) {
        this.areaNome = areaNome;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Converte para o formato de Ingresso tradicional para persistência no banco de dados.
     */
    public Ingresso toIngresso() {
        Ingresso ingresso = new Ingresso();
        ingresso.setEventoNome(eventoNome);
        ingresso.setHorario(horario);
        ingresso.setDataSessao(dataSessao);
        ingresso.setAreaNome(areaNome);
        ingresso.setNumeroPoltrona(numeroPoltrona);
        ingresso.setValor(valor);
        ingresso.setDataCompra(dataCompra);
        ingresso.setCodigo(codigo);
        return ingresso;
    }
}
