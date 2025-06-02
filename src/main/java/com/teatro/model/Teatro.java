package com.teatro.model;

import com.teatro.dao.UsuarioDAO;
import com.teatro.dao.IngressoDAO;
import java.util.*;

public class Teatro {
    private UsuarioDAO usuarioDAO;
    private IngressoDAO ingressoDAO;
    private List<Evento> eventos;
    private Map<String, Area> areas;

    public Teatro() {
        this.usuarioDAO = new UsuarioDAO();
        this.ingressoDAO = new IngressoDAO();
        this.eventos = new ArrayList<>();
        this.areas = new HashMap<>();
        
        inicializarAreas();
        inicializarEventos();
    }

    private void inicializarAreas() {
        // Plateia A (25 poltronas)
        areas.put("Plateia A", new Area("PA", "Plateia A", 40.00, 25));
        
        // Plateia B (100 poltronas)
        areas.put("Plateia B", new Area("PB", "Plateia B", 60.00, 100));
        
        // Camarotes (5 camarotes com 10 poltronas cada)
        for (int i = 1; i <= 5; i++) {
            String id = String.format("CM%02d", i);
            areas.put("Camarote " + i, 
                     new Area(id, "Camarote " + i, 80.00, 10));
        }
        
        // Frisas (6 frisas com 5 poltronas cada)
        for (int i = 1; i <= 6; i++) {
            String id = String.format("FR%02d", i);
            areas.put("Frisa " + String.format("%02d", i), 
                     new Area(id, "Frisa " + String.format("%02d", i), 120.00, 5));
        }
        
        // Balcão Nobre (50 poltronas)
        areas.put("Balcão Nobre", new Area("BN", "Balcão Nobre", 250.00, 50));
    }

    private void inicializarEventos() {
        String[] nomesEventos = {"The Batman", "Kill Bill: Volume 1", "Django Livre"};
        String[] horariosSessoes = {"Manhã", "Tarde", "Noite"};
        long sessaoId = 1; // Contador para IDs das sessões

        for (String nomeEvento : nomesEventos) {
            Evento evento = new Evento(nomeEvento);
            
            for (String horario : horariosSessoes) {
                Sessao sessao = new Sessao(horario);
                sessao.setNome(nomeEvento);
                sessao.setId(sessaoId); // Define um ID único para cada sessão
                
                // Adiciona todas as áreas à sessão
                for (Area area : areas.values()) {
                    Area areaClone = new Area(area.getId(), area.getNome(), area.getPreco(), area.getCapacidadeTotal(), sessaoId);
                    
                    // Carrega as poltronas ocupadas do banco de dados
                    long areaIdLong;
                    String areaId = area.getId();
                    
                    if (areaId.startsWith("PA")) {
                        areaIdLong = 1;
                    } else if (areaId.startsWith("PB")) {
                        areaIdLong = 2;
                    } else if (areaId.startsWith("CM")) {
                        int num = Integer.parseInt(areaId.substring(2));
                        areaIdLong = 2 + num;
                    } else if (areaId.startsWith("FR")) {
                        int num = Integer.parseInt(areaId.substring(2));
                        areaIdLong = 7 + num;
                    } else {
                        areaIdLong = 13; // Balcão Nobre
                    }
                    
                    List<Integer> poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(sessaoId, areaIdLong);
                    areaClone.carregarPoltronasOcupadas(poltronasOcupadas);
                    
                    sessao.addArea(areaClone);
                }
                
                evento.addSessao(sessao);
                sessaoId++; // Incrementa o ID para a próxima sessão
            }
            
            eventos.add(evento);
        }
    }

    public boolean cadastrarUsuario(Usuario usuario) {
        return usuarioDAO.cadastrar(usuario);
    }

    public Optional<Usuario> autenticarUsuario(String cpf, String senha) {
        System.out.println("Tentando autenticar usuário com CPF: " + cpf);
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorCpf(cpf);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            System.out.println("Senha fornecida: " + senha);
            System.out.println("Senha armazenada: " + usuario.getSenha());
            if (usuario.getSenha().equals(senha)) {
                System.out.println("Autenticação bem-sucedida!");
                return Optional.of(usuario);
            } else {
                System.out.println("Senha incorreta!");
            }
        } else {
            System.out.println("Usuário não encontrado!");
        }
        return Optional.empty();
    }

    public List<Evento> getEventos() {
        return eventos;
    }
    
    /**
     * Verifica se uma poltrona específica está ocupada em uma determinada sessão e área.
     * @param sessaoId ID da sessão
     * @param areaId ID da área
     * @param numeroPoltrona Número da poltrona
     * @return true se a poltrona estiver ocupada, false caso contrário
     */
    public boolean isPoltronaOcupada(Long sessaoId, String areaId, int numeroPoltrona) {
        // Encontra a sessão pelo ID
        for (Evento evento : eventos) {
            for (Sessao sessao : evento.getSessoes()) {
                if (sessao.getId().equals(sessaoId)) {
                    // Encontra a área na sessão
                    for (Area area : sessao.getAreas()) {
                        if (area.getId().equals(areaId)) {
                            // Verifica se a poltrona está ocupada
                            List<Integer> poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(sessaoId, 
                                getAreaIdAsLong(areaId));
                            return poltronasOcupadas.contains(numeroPoltrona);
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Converte o ID da área para o formato numérico usado no banco de dados.
     * @param areaId ID da área no formato String
     * @return ID da área no formato numérico
     */
    private long getAreaIdAsLong(String areaId) {
        if (areaId.startsWith("PA")) {
            return 1; // Plateia A
        } else if (areaId.startsWith("PB")) {
            return 2; // Plateia B
        } else if (areaId.startsWith("CM")) {
            int num = Integer.parseInt(areaId.substring(2));
            return 2 + num;
        } else if (areaId.startsWith("FR")) {
            int num = Integer.parseInt(areaId.substring(2));
            return 7 + num;
        } else {
            return 13; // Balcão Nobre
        }
    }

    public Optional<Ingresso> comprarIngresso(String cpf, Evento evento, Sessao sessao, Area area, int numeroPoltrona) {
        Optional<Usuario> usuario = usuarioDAO.buscarPorCpf(cpf);
        if (usuario.isEmpty()) {
            return Optional.empty();
        }

        // Verifica se a poltrona está disponível
        Optional<Area> areaEvento = sessao.getAreas().stream()
                .filter(a -> a.getNome().equals(area.getNome()))
                .findFirst();

        if (areaEvento.isPresent() && areaEvento.get().ocuparPoltrona(numeroPoltrona)) {
            // Mapeia o ID da área para um número sequencial
            long areaIdLong;
            String areaId = area.getId();
            
            if (areaId.startsWith("PA")) {
                areaIdLong = 1;
            } else if (areaId.startsWith("PB")) {
                areaIdLong = 2;
            } else if (areaId.startsWith("CM")) {
                int num = Integer.parseInt(areaId.substring(2));
                areaIdLong = 2 + num;
            } else if (areaId.startsWith("FR")) {
                int num = Integer.parseInt(areaId.substring(2));
                areaIdLong = 7 + num;
            } else {
                areaIdLong = 13; // Balcão Nobre
            }
            
            Ingresso ingresso = new Ingresso(
                usuario.get().getId(),
                sessao.getId(),
                areaIdLong,
                numeroPoltrona,
                area.getPreco()
            );
            
            if (ingressoDAO.salvar(ingresso)) {
                ingressoDAO.atualizarStatusPoltrona(sessao.getId(), areaIdLong, numeroPoltrona, true);
                ingressoDAO.atualizarFaturamento(sessao.getId(), areaIdLong, area.getPreco());
                return Optional.of(ingresso);
            }
        }

        return Optional.empty();
    }

    public List<Ingresso> buscarIngressosPorCpf(String cpf) {
        Optional<Usuario> usuario = usuarioDAO.buscarPorCpf(cpf);
        if (usuario.isEmpty()) {
            return new ArrayList<>();
        }
        return ingressoDAO.buscarPorUsuarioId(usuario.get().getId());
    }

    public Map<String, Integer> getEstatisticasVendas() {
        Map<String, Integer> estatisticas = new HashMap<>();
        
        // Evento com mais ingressos vendidos
        Map<String, Long> vendasPorEvento = new HashMap<>();
        for (Ingresso ingresso : ingressoDAO.buscarPorUsuarioId(null)) {
            vendasPorEvento.merge(ingresso.getEventoNome(), 1L, Long::sum);
        }
        
        if (!vendasPorEvento.isEmpty()) {
            String eventoMaisVendido = Collections.max(vendasPorEvento.entrySet(), Map.Entry.comparingByValue()).getKey();
            String eventoMenosVendido = Collections.min(vendasPorEvento.entrySet(), Map.Entry.comparingByValue()).getKey();
            
            estatisticas.put("eventoMaisVendido", vendasPorEvento.get(eventoMaisVendido).intValue());
            estatisticas.put("eventoMenosVendido", vendasPorEvento.get(eventoMenosVendido).intValue());
        }
        
        // Sessão com maior ocupação
        Map<String, Long> ocupacaoPorSessao = new HashMap<>();
        for (Ingresso ingresso : ingressoDAO.buscarPorUsuarioId(null)) {
            ocupacaoPorSessao.merge(ingresso.getHorario(), 1L, Long::sum);
        }
        
        if (!ocupacaoPorSessao.isEmpty()) {
            String sessaoMaisOcupada = Collections.max(ocupacaoPorSessao.entrySet(), Map.Entry.comparingByValue()).getKey();
            String sessaoMenosOcupada = Collections.min(ocupacaoPorSessao.entrySet(), Map.Entry.comparingByValue()).getKey();
            
            estatisticas.put("sessaoMaisOcupada", ocupacaoPorSessao.get(sessaoMaisOcupada).intValue());
            estatisticas.put("sessaoMenosOcupada", ocupacaoPorSessao.get(sessaoMenosOcupada).intValue());
        }
        
        return estatisticas;
    }

    public Map<String, Double> getEstatisticasLucratividade() {
        Map<String, Double> estatisticas = new HashMap<>();
        
        // Lucratividade por evento/sessão
        Map<String, Double> lucroPorEvento = new HashMap<>();
        Map<String, Double> lucroPorSessao = new HashMap<>();
        
        for (Ingresso ingresso : ingressoDAO.buscarPorUsuarioId(null)) {
            String evento = ingresso.getEventoNome();
            String sessao = ingresso.getHorario();
            
            lucroPorEvento.merge(evento, ingresso.getValor(), Double::sum);
            lucroPorSessao.merge(sessao, ingresso.getValor(), Double::sum);
        }
        
        if (!lucroPorEvento.isEmpty()) {
            Map.Entry<String, Double> eventoMaisLucrativo = Collections.max(lucroPorEvento.entrySet(), Map.Entry.comparingByValue());
            Map.Entry<String, Double> eventoMenosLucrativo = Collections.min(lucroPorEvento.entrySet(), Map.Entry.comparingByValue());
            
            estatisticas.put("eventoMaisLucrativo", eventoMaisLucrativo.getValue());
            estatisticas.put("eventoMenosLucrativo", eventoMenosLucrativo.getValue());
        }
        
        if (!lucroPorSessao.isEmpty()) {
            Map.Entry<String, Double> sessaoMaisLucrativa = Collections.max(lucroPorSessao.entrySet(), Map.Entry.comparingByValue());
            Map.Entry<String, Double> sessaoMenosLucrativa = Collections.min(lucroPorSessao.entrySet(), Map.Entry.comparingByValue());
            
            estatisticas.put("sessaoMaisLucrativa", sessaoMaisLucrativa.getValue());
            estatisticas.put("sessaoMenosLucrativa", sessaoMenosLucrativa.getValue());
        }
        
        // Lucro médio por área
        double lucroTotal = ingressoDAO.buscarPorUsuarioId(null).stream()
                .mapToDouble(Ingresso::getValor)
                .sum();
        double lucroMedio = lucroTotal / areas.size();
        estatisticas.put("lucroMedioPorArea", lucroMedio);
        
        return estatisticas;
    }
} 