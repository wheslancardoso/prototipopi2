package com.teatro.controller;

import com.teatro.dao.HorarioDisponivelDAO;
import com.teatro.model.Area;
import com.teatro.model.HorarioDisponivel;
import com.teatro.model.Sessao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SessaoController {
    
    private HorarioDisponivelDAO horarioDisponivelDAO;
    
    public SessaoController() {
        this.horarioDisponivelDAO = new HorarioDisponivelDAO();
    }
    
    public boolean verificarDisponibilidade(Sessao sessao, Area area, int numeroPoltrona) {
        return !area.ocuparPoltrona(numeroPoltrona);
    }
    
    public double calcularValorTotal(Area area, int quantidadePoltronas) {
        return area.getPreco() * quantidadePoltronas;
    }
    
    public void atualizarFaturamento(Sessao sessao) {
        sessao.atualizarFaturamento();
    }
    
    /**
     * Busca todos os horários disponíveis para um determinado tipo de sessão
     * 
     * @param tipoSessao Tipo de sessão (Manhã, Tarde, Noite)
     * @return Lista de horários disponíveis
     */
    public List<HorarioDisponivel> buscarHorariosPorTipoSessao(String tipoSessao) {
        return horarioDisponivelDAO.buscarPorTipoSessao(tipoSessao);
    }
    
    /**
     * Filtra os horários disponíveis de acordo com a data selecionada
     * Se a data for a atual, remove os horários que já passaram
     * 
     * @param horarios Lista de horários disponíveis
     * @param dataSelecionada Data selecionada
     * @return Lista de horários filtrados
     */
    public List<HorarioDisponivel> filtrarHorariosDisponiveis(List<HorarioDisponivel> horarios, LocalDate dataSelecionada) {
        if (horarios == null || horarios.isEmpty()) {
            return new ArrayList<>();
        }
        
        LocalDate dataAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();
        
        // Cria uma nova lista para não modificar a original
        List<HorarioDisponivel> horariosFiltrados = new ArrayList<>(horarios);
        
        // Se a data selecionada for a atual, filtra os horários que já passaram
        if (dataSelecionada.isEqual(dataAtual)) {
            horariosFiltrados.removeIf(horario -> !horario.isDisponivel(horaAtual, true));
        }
        
        return horariosFiltrados;
    }
    
    /**
     * Verifica se um horário específico está disponível para a data selecionada
     * 
     * @param horario Horário a ser verificado
     * @param dataSelecionada Data selecionada
     * @return true se o horário estiver disponível, false caso contrário
     */
    public boolean isHorarioDisponivel(HorarioDisponivel horario, LocalDate dataSelecionada) {
        LocalDate dataAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();
        
        boolean isDataAtual = dataSelecionada.equals(dataAtual);
        return horario.isDisponivel(horaAtual, isDataAtual);
    }
}