package com.teatro.model;

import java.util.ArrayList;
import java.util.List;

public class Evento {
    private String nome;
    private List<Sessao> sessoes;
    private Long id;
    private String descricao;

    public Evento(String nome) {
        this.nome = nome;
        this.sessoes = new ArrayList<>();
    }
    
    public Evento(Long id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.sessoes = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Sessao> getSessoes() {
        return sessoes;
    }

    public void addSessao(Sessao sessao) {
        this.sessoes.add(sessao);
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
} 