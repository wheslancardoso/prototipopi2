package com.teatro.model;

public enum TipoSessao {
    MANHA("Manhã"),
    TARDE("Tarde"),
    NOITE("Noite");
    
    private final String descricao;
    
    TipoSessao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
} 