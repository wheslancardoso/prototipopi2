package com.teatro.model;

import java.util.ArrayList;
import java.util.List;

public class Area implements Comparable<Area> {
    private String id;
    private String nome;
    private double preco;
    private int capacidadeTotal;
    private List<Boolean> poltronas;
    private double faturamento;
    private Long sessaoId;

    public Area(String id, String nome, double preco, int capacidadeTotal) {
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

    public Area(String id, String nome, double preco, int capacidadeTotal, Long sessaoId) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.capacidadeTotal = capacidadeTotal;
        this.poltronas = new ArrayList<>(capacidadeTotal);
        this.faturamento = 0.0;
        this.sessaoId = sessaoId;
        
        // Inicializa todas as poltronas como disponíveis
        for (int i = 0; i < capacidadeTotal; i++) {
            poltronas.add(false);
        }
    }
    
    /**
     * Construtor de cópia para criar uma nova instância de Area com base em outra
     * @param outra Área a ser copiada
     * @param novoSessaoId Novo ID de sessão para a cópia
     */
    public Area(Area outra, Long novoSessaoId) {
        this.id = outra.id;
        this.nome = outra.nome;
        this.preco = outra.preco;
        this.capacidadeTotal = outra.capacidadeTotal;
        this.faturamento = 0.0; // Inicia com faturamento zerado
        this.sessaoId = novoSessaoId;
        
        // Cria uma nova lista de poltronas
        this.poltronas = new ArrayList<>(outra.capacidadeTotal);
        
        // Inicializa todas as poltronas como disponíveis
        for (int i = 0; i < capacidadeTotal; i++) {
            this.poltronas.add(false);
        }
    }

    /**
     * Carrega as poltronas ocupadas para esta área específica
     * @param poltronasOcupadas Lista de números de poltronas ocupadas
     */
    public void carregarPoltronasOcupadas(List<Integer> poltronasOcupadas) {
        System.out.println("Carregando poltronas ocupadas para área " + nome + 
                         " (Capacidade: " + capacidadeTotal + ")");
        System.out.println("Poltronas ocupadas recebidas: " + poltronasOcupadas);
        
        // Garante que a lista de poltronas está inicializada
        if (poltronas == null) {
            poltronas = new ArrayList<>(capacidadeTotal);
        }
        
        // Limpa a lista de poltronas e garante que tem o tamanho correto
        poltronas.clear();
        for (int i = 0; i < capacidadeTotal; i++) {
            poltronas.add(false);
        }
        
        // Marca as poltronas ocupadas
        if (poltronasOcupadas != null && !poltronasOcupadas.isEmpty()) {
            int poltronasInvalidas = 0;
            
            for (Integer numeroPoltrona : poltronasOcupadas) {
                if (numeroPoltrona != null && numeroPoltrona > 0 && numeroPoltrona <= capacidadeTotal) {
                    poltronas.set(numeroPoltrona - 1, true);
                } else {
                    poltronasInvalidas++;
                }
            }
            
            if (poltronasInvalidas > 0) {
                System.out.println("Aviso: " + poltronasInvalidas + " números de poltronas inválidos foram ignorados.");
            }
            
            // Atualiza o contador de poltronas ocupadas para o faturamento
            int ocupadas = 0;
            for (Boolean ocupada : poltronas) {
                if (ocupada) ocupadas++;
            }
            
            this.faturamento = ocupadas * preco;
            System.out.println("Total de poltronas ocupadas: " + ocupadas + "/" + capacidadeTotal);
            System.out.println("Faturamento calculado: R$" + faturamento);
        } else {
            this.faturamento = 0.0;
            System.out.println("Nenhuma poltrona ocupada encontrada para esta área.");
        }
        
        System.out.println("Estado final das poltronas (V=disponível, X=ocupada): " + 
                           getEstadoPoltronasString());
    }
    
    /**
     * Carrega as poltronas ocupadas para esta área específica, considerando o horário específico
     * @param poltronasOcupadas Lista de números de poltronas ocupadas
     * @param horarioEspecificoId ID do horário específico
     */
    public void carregarPoltronasOcupadas(List<Integer> poltronasOcupadas, Long horarioEspecificoId) {
        System.out.println("Carregando poltronas ocupadas para área " + nome + 
                         " (Capacidade: " + capacidadeTotal + ", Horário ID: " + horarioEspecificoId + ")");
        System.out.println("Poltronas ocupadas recebidas: " + poltronasOcupadas);
        
        // Garante que a lista de poltronas está inicializada
        if (poltronas == null) {
            poltronas = new ArrayList<>(capacidadeTotal);
        }
        
        // Limpa a lista de poltronas e garante que tem o tamanho correto
        poltronas.clear();
        for (int i = 0; i < capacidadeTotal; i++) {
            poltronas.add(false);
        }
        
        // Marca as poltronas ocupadas
        if (poltronasOcupadas != null && !poltronasOcupadas.isEmpty()) {
            int poltronasInvalidas = 0;
            
            for (Integer numeroPoltrona : poltronasOcupadas) {
                if (numeroPoltrona != null && numeroPoltrona > 0 && numeroPoltrona <= capacidadeTotal) {
                    poltronas.set(numeroPoltrona - 1, true);
                } else {
                    poltronasInvalidas++;
                }
            }
            
            if (poltronasInvalidas > 0) {
                System.out.println("Aviso: " + poltronasInvalidas + " números de poltronas inválidos foram ignorados.");
            }
            
            // Atualiza o contador de poltronas ocupadas para o faturamento
            int ocupadas = 0;
            for (Boolean ocupada : poltronas) {
                if (ocupada) ocupadas++;
            }
            
            this.faturamento = ocupadas * preco;
            System.out.println("Total de poltronas ocupadas: " + ocupadas + "/" + capacidadeTotal);
            System.out.println("Faturamento calculado: R$" + faturamento);
        } else {
            this.faturamento = 0.0;
            System.out.println("Nenhuma poltrona ocupada encontrada para esta área e horário.");
        }
        
        System.out.println("Estado final das poltronas (V=disponível, X=ocupada): " + 
                           getEstadoPoltronasString());
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getCapacidade() {
        return capacidadeTotal;
    }
    
    public int getCapacidadeTotal() {
        return capacidadeTotal;
    }

    public double getFaturamento() {
        return faturamento;
    }

    public Long getSessaoId() {
        return sessaoId;
    }

    /**
     * Retorna uma representação em string do estado das poltronas (para debug)
     */
    private String getEstadoPoltronasString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(poltronas.size(), 50); i++) { // Limita a 50 para não ficar muito grande
            sb.append(poltronas.get(i) ? "X" : "V");
            if ((i + 1) % 10 == 0) sb.append(" ");
        }
        if (poltronas.size() > 50) {
            sb.append("... (mais ").append(poltronas.size() - 50).append(" poltronas)");
        }
        return sb.toString();
    }
    
    public int getPoltronasDisponiveis() {
        int disponiveis = 0;
        for (Boolean ocupada : poltronas) {
            if (!ocupada) disponiveis++;
        }
        System.out.println("Poltronas disponíveis em " + nome + ": " + disponiveis + "/" + capacidadeTotal);
        return disponiveis;
    }

    public List<Boolean> getPoltronas() {
        return new ArrayList<>(poltronas); // Retorna uma cópia para evitar modificações externas
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
        return String.format("%s - R$%.2f", nome, preco);
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
} 