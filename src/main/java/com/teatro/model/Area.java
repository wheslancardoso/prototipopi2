package com.teatro.model;

import java.util.ArrayList;
import java.util.List;

public class Area implements Comparable<Area> {
    private Long id;
    private String nome;
    private double preco;
    private int capacidadeTotal;
    private List<Boolean> poltronas;
    private double faturamento;
    private Long sessaoId;

    public Area() {
        // Construtor padrão
    }

    public Area(Long id, String nome, double preco, int capacidadeTotal) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.capacidadeTotal = capacidadeTotal;
        this.poltronas = new ArrayList<>();
        this.faturamento = 0.0;
        
        // Inicializa todas as poltronas como disponíveis
        for (int i = 0; i < capacidadeTotal; i++) {
            poltronas.add(false);
        }
    }

    public Area(Long id, String nome, double preco, int capacidadeTotal, Long sessaoId) {
        this(id, nome, preco, capacidadeTotal);
        this.sessaoId = sessaoId;
    }

    public void carregarPoltronasOcupadas(List<Integer> poltronasOcupadas) {
        // Reseta todas as poltronas para disponíveis
        for (int i = 0; i < poltronas.size(); i++) {
            poltronas.set(i, false);
        }
        
        // Marca as poltronas ocupadas
        for (Integer numeroPoltrona : poltronasOcupadas) {
            if (numeroPoltrona > 0 && numeroPoltrona <= poltronas.size()) {
                poltronas.set(numeroPoltrona - 1, true);
            }
        }
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

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getCapacidadeTotal() {
        return capacidadeTotal;
    }

    public void setCapacidadeTotal(int capacidadeTotal) {
        this.capacidadeTotal = capacidadeTotal;
    }

    public double getFaturamento() {
        return faturamento;
    }

    public Long getSessaoId() {
        return sessaoId;
    }

    public int getPoltronasDisponiveis() {
        int disponiveis = 0;
        for (Boolean ocupada : poltronas) {
            if (!ocupada) disponiveis++;
        }
        return disponiveis;
    }

    public List<Integer> getPoltronasDisponiveisList() {
        List<Integer> disponiveisList = new ArrayList<>();
        for (int i = 0; i < poltronas.size(); i++) {
            if (!poltronas.get(i)) {
                disponiveisList.add(i + 1);
            }
        }
        return disponiveisList;
    }

    public int getPrimeiraPoltrona() {
        for (int i = 0; i < poltronas.size(); i++) {
            if (!poltronas.get(i)) {
                return i + 1; // Retorna o número da poltrona (1-indexed)
            }
        }
        return -1; // Não há poltronas disponíveis
    }

    public boolean ocuparPoltrona(int numeroPoltrona) {
        int index = numeroPoltrona - 1; // Converte para 0-indexed
        if (index >= 0 && index < poltronas.size() && !poltronas.get(index)) {
            poltronas.set(index, true);
            faturamento += preco;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return nome + " - R$ " + String.format("%.2f", preco);
    }

    @Override
    public int compareTo(Area outra) {
        // Define a ordem de prioridade: Plateia A, Plateia B, Camarotes, Frisas
        if (this.nome.startsWith("Plateia") && outra.nome.startsWith("Plateia")) {
            return this.nome.compareTo(outra.nome);
        }
        if (this.nome.startsWith("Plateia")) return -1;
        if (outra.nome.startsWith("Plateia")) return 1;
        
        if (this.nome.startsWith("Camarote") && outra.nome.startsWith("Camarote")) {
            // Extrai o número do camarote para comparação numérica
            int thisNum = Integer.parseInt(this.nome.replaceAll("\\D+", ""));
            int outraNum = Integer.parseInt(outra.nome.replaceAll("\\D+", ""));
            return Integer.compare(thisNum, outraNum);
        }
        if (this.nome.startsWith("Camarote")) return -1;
        if (outra.nome.startsWith("Camarote")) return 1;
        
        if (this.nome.startsWith("Frisa") && outra.nome.startsWith("Frisa")) {
            // Extrai o número da frisa para comparação numérica
            int thisNum = Integer.parseInt(this.nome.replaceAll("\\D+", ""));
            int outraNum = Integer.parseInt(outra.nome.replaceAll("\\D+", ""));
            return Integer.compare(thisNum, outraNum);
        }
        
        return this.nome.compareTo(outra.nome);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Area area = (Area) o;
        return id != null && id.equals(area.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 