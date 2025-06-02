package com.teatro.model;

import java.util.ArrayList;
import java.util.List;

public class Evento {
    private String nome;
    private List<Sessao> sessoes;

    public Evento(String nome) {
        this.nome = nome;
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
} 