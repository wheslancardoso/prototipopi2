package com.teatro.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.List;

/**
 * Testes unitários para a classe EstatisticasService.
 */
public class EstatisticasServiceTest {
    
    private EstatisticasService estatisticasService;
    
    @BeforeEach
    void setUp() {
        estatisticasService = EstatisticasService.getInstance();
    }
    
    @Test
    void testBuscarEstatisticas() {
        // Testa se o método não lança exceção
        Map<String, Object> estatisticas = estatisticasService.buscarEstatisticas();
        
        assertNotNull(estatisticas);
        assertTrue(estatisticas.containsKey("pecaMaisVendida"));
        assertTrue(estatisticas.containsKey("pecaMenosVendida"));
        assertTrue(estatisticas.containsKey("sessaoMaiorOcupacao"));
        assertTrue(estatisticas.containsKey("sessaoMenorOcupacao"));
        assertTrue(estatisticas.containsKey("pecaMaisLucrativa"));
        assertTrue(estatisticas.containsKey("pecaMenosLucrativa"));
        assertTrue(estatisticas.containsKey("lucroMedioPorPeca"));
    }
    
    @Test
    void testEstruturaEstatisticas() {
        Map<String, Object> estatisticas = estatisticasService.buscarEstatisticas();
        
        // Verifica estrutura da peça mais vendida
        Map<String, Object> pecaMaisVendida = (Map<String, Object>) estatisticas.get("pecaMaisVendida");
        assertNotNull(pecaMaisVendida);
        assertTrue(pecaMaisVendida.containsKey("nome"));
        assertTrue(pecaMaisVendida.containsKey("totalVendas"));
        
        // Verifica estrutura da sessão com maior ocupação
        Map<String, Object> sessaoMaiorOcupacao = (Map<String, Object>) estatisticas.get("sessaoMaiorOcupacao");
        assertNotNull(sessaoMaiorOcupacao);
        assertTrue(sessaoMaiorOcupacao.containsKey("nome"));
        assertTrue(sessaoMaiorOcupacao.containsKey("data"));
        assertTrue(sessaoMaiorOcupacao.containsKey("horario"));
        assertTrue(sessaoMaiorOcupacao.containsKey("ocupacao"));
        
        // Verifica estrutura da peça mais lucrativa
        Map<String, Object> pecaMaisLucrativa = (Map<String, Object>) estatisticas.get("pecaMaisLucrativa");
        assertNotNull(pecaMaisLucrativa);
        assertTrue(pecaMaisLucrativa.containsKey("nome"));
        assertTrue(pecaMaisLucrativa.containsKey("faturamento"));
        
        // Verifica estrutura do lucro médio por peça
        List<Map<String, Object>> lucroMedioPorPeca = (List<Map<String, Object>>) estatisticas.get("lucroMedioPorPeca");
        assertNotNull(lucroMedioPorPeca);
    }
    
    @Test
    void testValoresEstatisticas() {
        Map<String, Object> estatisticas = estatisticasService.buscarEstatisticas();
        
        // Verifica se os valores são do tipo correto
        Map<String, Object> pecaMaisVendida = (Map<String, Object>) estatisticas.get("pecaMaisVendida");
        assertTrue(pecaMaisVendida.get("nome") instanceof String);
        assertTrue(pecaMaisVendida.get("totalVendas") instanceof Integer);
        
        Map<String, Object> pecaMaisLucrativa = (Map<String, Object>) estatisticas.get("pecaMaisLucrativa");
        assertTrue(pecaMaisLucrativa.get("nome") instanceof String);
        assertTrue(pecaMaisLucrativa.get("faturamento") instanceof String);
        
        // Verifica se o faturamento está formatado corretamente
        String faturamento = (String) pecaMaisLucrativa.get("faturamento");
        if (!"N/A".equals(faturamento)) {
            assertTrue(faturamento.startsWith("R$ "));
        }
    }
    
    @Test
    void testSingleton() {
        EstatisticasService instance1 = EstatisticasService.getInstance();
        EstatisticasService instance2 = EstatisticasService.getInstance();
        
        assertSame(instance1, instance2);
    }
} 