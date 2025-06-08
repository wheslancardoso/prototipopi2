package com.teatro.model;

import com.teatro.model.state.PoltronaState;
import com.teatro.model.state.DisponivelState;

public class Poltrona {
    private int numero;
    private boolean ocupada;
    private String cpfOcupante;
    private Area area; // Referência à área da poltrona
    private PoltronaState state = new DisponivelState();

    public Poltrona(int numero) {
        this.numero = numero;
        this.ocupada = false;
        this.cpfOcupante = null;
    }
    
    public Poltrona(int numero, Area area) {
        this.numero = numero;
        this.area = area;
        this.ocupada = false;
        this.cpfOcupante = null;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public String getCpfOcupante() {
        return cpfOcupante;
    }

    public void setCpfOcupante(String cpfOcupante) {
        this.cpfOcupante = cpfOcupante;
    }
    
    public Area getArea() {
        return area;
    }
    
    public void setArea(Area area) {
        this.area = area;
    }

    public void ocupar() {
        state.ocupar(this);
    }
    public void liberar() {
        state.liberar(this);
    }
    public void selecionar() {
        state.selecionar(this);
    }
    public String getNomeEstado() {
        return state.getNomeEstado();
    }
    public void setState(PoltronaState state) {
        this.state = state;
    }
} 