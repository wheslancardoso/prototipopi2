package com.teatro.model;

import java.sql.Timestamp;

/**
 * Versão modernizada da classe Ingresso que mantém referências diretas aos objetos
 * ao invés de apenas IDs.
 */
public class IngressoModerno {
    private Long id;
    private Usuario usuario;
    private Sessao sessao;
    private Area area;
    private Poltrona poltrona;
    private double valor;
    private Timestamp dataCompra;
    private String codigo;

    public IngressoModerno() {
    }

    public IngressoModerno(Sessao sessao, Area area, Poltrona poltrona, Usuario usuario) {
        this.sessao = sessao;
        this.area = area;
        this.poltrona = poltrona;
        this.usuario = usuario;
        this.valor = area.getPreco();
        this.dataCompra = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Poltrona getPoltrona() {
        return poltrona;
    }

    public void setPoltrona(Poltrona poltrona) {
        this.poltrona = poltrona;
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
        ingresso.setUsuarioId(usuario.getId());
        ingresso.setSessaoId(sessao.getId());
        // O ID da área é String, então precisamos converter para Long para o Ingresso
        ingresso.setAreaId(Long.valueOf(area.getId()));
        ingresso.setNumeroPoltrona(poltrona.getNumero());
        ingresso.setValor(valor);
        ingresso.setDataCompra(dataCompra);
        ingresso.setEventoNome(sessao.getNome());
        ingresso.setHorario(sessao.getHorario());
        ingresso.setAreaNome(area.getNome());
        return ingresso;
    }
}
