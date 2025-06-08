package com.teatro.model.state;

import com.teatro.model.Poltrona;

public class OcupadaState implements PoltronaState {
    @Override
    public void ocupar(Poltrona poltrona) {
        // Já está ocupada
    }
    @Override
    public void liberar(Poltrona poltrona) {
        poltrona.setState(new DisponivelState());
    }
    @Override
    public void selecionar(Poltrona poltrona) {
        // Não pode selecionar uma poltrona ocupada
    }
    @Override
    public String getNomeEstado() {
        return "Ocupada";
    }
} 