package com.teatro.model.state;

import com.teatro.model.Poltrona;

public class SelecionadaState implements PoltronaState {
    @Override
    public void ocupar(Poltrona poltrona) {
        poltrona.setState(new OcupadaState());
    }
    @Override
    public void liberar(Poltrona poltrona) {
        poltrona.setState(new DisponivelState());
    }
    @Override
    public void selecionar(Poltrona poltrona) {
        // Já está selecionada
    }
    @Override
    public String getNomeEstado() {
        return "Selecionada";
    }
} 