package com.teatro.model.state;

import com.teatro.model.Poltrona;

public class DisponivelState implements PoltronaState {
    @Override
    public void ocupar(Poltrona poltrona) {
        poltrona.setState(new OcupadaState());
    }
    @Override
    public void liberar(Poltrona poltrona) {
        // Já está disponível
    }
    @Override
    public void selecionar(Poltrona poltrona) {
        poltrona.setState(new SelecionadaState());
    }
    @Override
    public String getNomeEstado() {
        return "Disponível";
    }
} 