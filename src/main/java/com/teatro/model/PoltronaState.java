package com.teatro.model;

public interface PoltronaState {
    void ocupar(Poltrona poltrona);
    void liberar(Poltrona poltrona);
    void selecionar(Poltrona poltrona);
    String getNomeEstado();
} 