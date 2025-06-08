package com.teatro.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

public class Sessao {
    private Long id;
    private String nome;
    private TipoSessao tipoSessao;
    private Timestamp data;
    private List<Area> areas;
    private Evento evento;
    private Long eventoId;
    
    public Sessao() {
        this.areas = new ArrayList<>();
    }
    
    public Sessao(String eventoNome, TipoSessao tipoSessao, Timestamp data) {
        this.nome = eventoNome;
        this.tipoSessao = tipoSessao;
        this.data = data;
        this.areas = new ArrayList<>();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public TipoSessao getTipoSessao() {
        return tipoSessao;
    }
    
    public void setTipoSessao(TipoSessao tipoSessao) {
        this.tipoSessao = tipoSessao;
    }
    
    public Timestamp getData() {
        return data;
    }
    
    public void setData(Timestamp data) {
        this.data = data;
    }
    
    public String getHorario() {
        return tipoSessao.getDescricao();
    }
    
    public List<Area> getAreas() {
        return areas;
    }
    
    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }
    
    public Evento getEvento() {
        return evento;
    }
    
    public void setEvento(Evento evento) {
        this.evento = evento;
    }
    
    public Long getEventoId() {
        return eventoId;
    }
    
    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s - %s", 
            nome, 
            data.toString(), 
            tipoSessao.getDescricao());
    }
}