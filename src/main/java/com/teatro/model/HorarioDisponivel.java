package com.teatro.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe que representa um horário disponível para uma sessão.
 */
public class HorarioDisponivel implements Comparable<HorarioDisponivel> {
    private Long id;
    private String tipoSessao;
    private LocalTime horario;
    private int ordem;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public HorarioDisponivel(Long id, String tipoSessao, LocalTime horario, int ordem) {
        this.id = id;
        this.tipoSessao = tipoSessao;
        this.horario = horario;
        this.ordem = ordem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoSessao() {
        return tipoSessao;
    }

    public void setTipoSessao(String tipoSessao) {
        this.tipoSessao = tipoSessao;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }
    
    public String getHorarioFormatado() {
        return horario.format(FORMATTER);
    }
    
    @Override
    public String toString() {
        return getHorarioFormatado();
    }
    
    @Override
    public int compareTo(HorarioDisponivel outro) {
        return Integer.compare(this.ordem, outro.ordem);
    }
    
    /**
     * Verifica se o horário está disponível para a data atual.
     * @param dataAtual Data atual
     * @return true se o horário estiver disponível, false caso contrário
     */
    public boolean isDisponivel(LocalTime horaAtual, boolean isDataAtual) {
        if (!isDataAtual) {
            return true;
        }
        
        // Se a data for a atual, verifica se o horário já passou
        return horaAtual.isBefore(horario);
    }
}
