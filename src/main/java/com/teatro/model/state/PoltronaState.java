package com.teatro.model.state;

import com.teatro.model.Poltrona;

public interface PoltronaState {
    void ocupar(Poltrona poltrona);
    void liberar(Poltrona poltrona);
    void selecionar(Poltrona poltrona);
    String getNomeEstado();
} 