package com.teatro.model;

public class Poltrona {
    private int numero;
    private boolean ocupada;
    private String cpfOcupante;
    private Area area; // Referência à área da poltrona

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
} 