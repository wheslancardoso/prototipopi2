package com.teatro.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.teatro.model.state.*;

public class PoltronaStateTest {
    @Test
    void testTransicoesDeEstado() {
        Poltrona poltrona = new Poltrona(1);
        // Estado inicial
        assertEquals("Disponível", poltrona.getNomeEstado());
        // Selecionar
        poltrona.selecionar();
        assertEquals("Selecionada", poltrona.getNomeEstado());
        // Ocupar
        poltrona.ocupar();
        assertEquals("Ocupada", poltrona.getNomeEstado());
        // Liberar
        poltrona.liberar();
        assertEquals("Disponível", poltrona.getNomeEstado());
    }
    @Test
    void testNaoPermiteSelecionarOcupada() {
        Poltrona poltrona = new Poltrona(2);
        poltrona.ocupar();
        assertEquals("Ocupada", poltrona.getNomeEstado());
        poltrona.selecionar();
        // Continua ocupada
        assertEquals("Ocupada", poltrona.getNomeEstado());
    }
} 