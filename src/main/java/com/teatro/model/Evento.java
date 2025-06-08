package com.teatro.model;

import java.util.ArrayList;
import java.util.List;

public class Evento {
    private Long id;
    private String nome;
    private List<Sessao> sessoes;

    public Evento(Long id, String nome) {
        this.id = id;
        this.nome = nome;
        this.sessoes = new ArrayList<>();
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

    public List<Sessao> getSessoes() {
        return sessoes;
    }

    public void addSessao(Sessao sessao) {
        this.sessoes.add(sessao);
    }
} 