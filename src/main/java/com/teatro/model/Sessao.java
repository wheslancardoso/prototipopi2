package com.teatro.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Sessao {
    private Long id;
    private String horario; // Manhã, Tarde, Noite
    private List<Area> areas;
    private double faturamento;
    private String nome;
    private String dataFormatada;
    private double valorIngresso;
    private LocalDate dataSessao;
    private HorarioDisponivel horarioEspecifico;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Sessao(String horario) {
        this.horario = horario;
        this.areas = new ArrayList<>();
        this.faturamento = 0.0;
        this.dataSessao = LocalDate.now();
        this.dataFormatada = dataSessao.format(DATE_FORMATTER);
        this.valorIngresso = 0.0;
    }
    
    public Sessao(String horario, LocalDate dataSessao) {
        this.horario = horario;
        this.areas = new ArrayList<>();
        this.faturamento = 0.0;
        this.dataSessao = dataSessao;
        this.dataFormatada = dataSessao.format(DATE_FORMATTER);
        this.valorIngresso = 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void addArea(Area area) {
        this.areas.add(area);
    }

    public double getFaturamento() {
        return faturamento;
    }

    public void setFaturamento(double faturamento) {
        this.faturamento = faturamento;
    }

    public void atualizarFaturamento() {
        this.faturamento = areas.stream()
                .mapToDouble(Area::getFaturamento)
                .sum();
    }
    
    /**
     * Adiciona um valor ao faturamento da sessão
     * @param valor Valor a ser adicionado ao faturamento
     */
    public void adicionarFaturamento(double valor) {
        this.faturamento += valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataFormatada() {
        return dataFormatada;
    }

    public void setDataFormatada(String dataFormatada) {
        this.dataFormatada = dataFormatada;
    }

    public double getValorIngresso() {
        return valorIngresso;
    }

    public void setValorIngresso(double valorIngresso) {
        this.valorIngresso = valorIngresso;
    }
    
    public LocalDate getDataSessao() {
        return dataSessao;
    }
    
    public void setDataSessao(LocalDate dataSessao) {
        this.dataSessao = dataSessao;
        this.dataFormatada = dataSessao.format(DATE_FORMATTER);
    }
    
    public HorarioDisponivel getHorarioEspecifico() {
        return horarioEspecifico;
    }
    
    public void setHorarioEspecifico(HorarioDisponivel horarioEspecifico) {
        this.horarioEspecifico = horarioEspecifico;
    }
    
    /**
     * Retorna o horário formatado completo (período + horário específico)
     * @return String com o horário formatado
     */
    public String getHorarioCompleto() {
        if (horarioEspecifico != null) {
            return horario + " - " + horarioEspecifico.getHorarioFormatado();
        }
        return horario;
    }
    
    /**
     * Verifica se o horário específico está disponível para a data da sessão
     * @param horaAtual Hora atual do sistema
     * @param dataAtual Data atual do sistema
     * @return true se o horário estiver disponível, false caso contrário
     */
    public boolean isHorarioDisponivel(LocalTime horaAtual, LocalDate dataAtual) {
        if (horarioEspecifico == null) {
            return true;
        }
        
        boolean isDataAtual = dataSessao.equals(dataAtual);
        return horarioEspecifico.isDisponivel(horaAtual, isDataAtual);
    }
}