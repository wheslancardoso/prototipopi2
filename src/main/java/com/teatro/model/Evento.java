package com.teatro.model;

import java.util.ArrayList;
import java.util.List;

public class Evento {
    private Long id;
    private String nome;
    private String poster;
    private List<Sessao> sessoes;

    public Evento(Long id, String nome, String poster) {
        this.id = id;
        this.nome = nome;
        this.poster = poster;
        this.sessoes = new ArrayList<>();
    }

    public Evento(Long id, String nome) {
        this(id, nome, null);
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

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<Sessao> getSessoes() {
        return sessoes;
    }

    public void addSessao(Sessao sessao) {
        this.sessoes.add(sessao);
    }
} 