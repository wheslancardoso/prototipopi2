package com.teatro.model;

import com.teatro.dao.UsuarioDAO;
import com.teatro.dao.IngressoDAO;
import com.teatro.dao.AreaDAO;
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
        AreaDAO areaDAO = new AreaDAO();
        List<Area> listaAreas = areaDAO.listarTodas();
        areas.clear();
        for (Area area : listaAreas) {
            System.out.println("[DEBUG] Area carregada: id=" + area.getId() + ", nome=" + area.getNome());
            areas.put(area.getNome(), area);
        }
    }

    private void inicializarEventos() {
        String[] nomesEventos = {"Hamlet", "O Fantasma da Opera", "O Auto da Compadecida"};
        String[] horariosSessoes = {"Manhã", "Tarde", "Noite"};
        long sessaoId = 1; // Contador para IDs das sessões

        for (String nomeEvento : nomesEventos) {
            Evento evento = new Evento(nomeEvento);
            
            for (String horario : horariosSessoes) {
                Sessao sessao = new Sessao(horario);
                sessao.setNome(nomeEvento);
                sessao.setId(sessaoId); // Define um ID único para cada sessão
                System.out.println("[DEBUG] Criando sessão: id=" + sessaoId + ", evento=" + nomeEvento + ", horario=" + horario);
                
                // Adiciona todas as áreas à sessão
                for (Area area : areas.values()) {
                    Area areaClone = new Area(area.getId(), area.getNome(), area.getPreco(), area.getCapacidadeTotal(), sessaoId);
                    System.out.println("[DEBUG]  - Associando área à sessão: areaId=" + area.getId() + ", nome=" + area.getNome());
                    // Carrega as poltronas ocupadas do banco de dados usando o ID real da área
                    List<Integer> poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(sessaoId, area.getId());
                    System.out.println("[DEBUG]    - Poltronas ocupadas retornadas: " + poltronasOcupadas);
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

    public Optional<IngressoModerno> comprarIngresso(String cpf, Evento evento, Sessao sessao, Area area, int numeroPoltrona) {
        // Validação inicial
        if (cpf == null || evento == null || sessao == null || area == null || numeroPoltrona <= 0) {
            System.err.println("Parâmetros inválidos na compra de ingresso");
            return Optional.empty();
        }

        // Verifica se a sessão pertence ao evento
        if (!evento.getSessoes().contains(sessao)) {
            System.err.println("Sessão não pertence ao evento especificado");
            return Optional.empty();
        }

        // Verifica se a área pertence à sessão
        if (!sessao.getAreas().stream().anyMatch(a -> a.getNome().equals(area.getNome()))) {
            System.err.println("Área não pertence à sessão especificada");
            return Optional.empty();
        }

        Optional<Usuario> usuario = usuarioDAO.buscarPorCpf(cpf);
        if (usuario.isEmpty()) {
            System.err.println("Usuário não encontrado: " + cpf);
            return Optional.empty();
        }

        // Verifica se a poltrona está disponível
        Optional<Area> areaEvento = sessao.getAreas().stream()
                .filter(a -> a.getNome().equals(area.getNome()))
                .findFirst();

        if (areaEvento.isEmpty()) {
            System.err.println("Área não encontrada na sessão: " + area.getNome());
            return Optional.empty();
        }

        // Verifica se a poltrona está dentro do limite da área
        if (numeroPoltrona > areaEvento.get().getCapacidadeTotal()) {
            System.err.println("Número da poltrona inválido: " + numeroPoltrona);
            return Optional.empty();
        }

        // Verifica se a poltrona já está ocupada
        if (ingressoDAO.isPoltronaOcupada(sessao.getId(), area.getId(), numeroPoltrona)) {
            System.err.println("Poltrona já ocupada: " + numeroPoltrona);
            return Optional.empty();
        }

        if (areaEvento.get().ocuparPoltrona(numeroPoltrona)) {
            // Cria o ingresso moderno
            IngressoModerno ingresso = new IngressoModerno(sessao, area, new Poltrona(numeroPoltrona), usuario.get());
            ingresso.setCodigo(gerarCodigoIngresso(cpf));
            
            // Converte para o formato antigo para salvar no banco
            Ingresso ingressoAntigo = ingresso.toIngresso();
            
            if (ingressoDAO.salvar(ingressoAntigo)) {
                if (ingressoDAO.atualizarStatusPoltrona(sessao.getId(), area.getId(), numeroPoltrona, true)) {
                    return Optional.of(ingresso);
                } else {
                    // Se falhar ao atualizar o status, tenta reverter a ocupação
                    areaEvento.get().ocuparPoltrona(numeroPoltrona);
                    System.err.println("Falha ao atualizar status da poltrona");
                }
            }
        }

        return Optional.empty();
    }

    private String gerarCodigoIngresso(String cpf) {
        // Gera um código único para o ingresso usando timestamp, CPF e número aleatório
        return String.format("ING-%d-%s-%d", 
            System.currentTimeMillis(),
            cpf.substring(0, 3), // Primeiros 3 dígitos do CPF
            (int)(Math.random() * 1000));
    }

    public List<IngressoModerno> buscarIngressosPorCpf(String cpf) {
        Optional<Usuario> usuario = usuarioDAO.buscarPorCpf(cpf);
        if (usuario.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Ingresso> ingressosAntigos = ingressoDAO.buscarPorUsuarioId(usuario.get().getId());
        List<IngressoModerno> ingressosModernos = new ArrayList<>();
        
        for (Ingresso ingressoAntigo : ingressosAntigos) {
            // Busca a sessão
            Optional<Sessao> sessao = eventos.stream()
                .flatMap(e -> e.getSessoes().stream())
                .filter(s -> s.getId().equals(ingressoAntigo.getSessaoId()))
                .findFirst();
                
            if (sessao.isPresent()) {
                // Busca a área (comparando id como Long)
                Optional<Area> area = sessao.get().getAreas().stream()
                    .filter(a -> a.getId().equals(ingressoAntigo.getAreaId()))
                    .findFirst();
                    
                if (area.isPresent()) {
                    // Cria o ingresso moderno
                    IngressoModerno ingressoModerno = new IngressoModerno(
                        sessao.get(),
                        area.get(),
                        new Poltrona(ingressoAntigo.getNumeroPoltrona()),
                        usuario.get()
                    );
                    ingressoModerno.setId(ingressoAntigo.getId());
                    ingressoModerno.setDataCompra(ingressoAntigo.getDataCompra());
                    ingressoModerno.setCodigo(ingressoAntigo.getCodigo());
                    ingressosModernos.add(ingressoModerno);
                }
            }
        }
        
        return ingressosModernos;
    }

    public Map<String, Integer> getEstatisticasVendas() {
        Map<String, Integer> estatisticas = new HashMap<>();
        
        // Evento com mais ingressos vendidos
        Map<String, Long> vendasPorEvento = new HashMap<>();
        List<IngressoModerno> todosIngressos = new ArrayList<>();
        
        // Busca todos os ingressos de todos os usuários
        for (Evento evento : eventos) {
            for (Sessao sessao : evento.getSessoes()) {
                for (Area area : sessao.getAreas()) {
                    List<Integer> poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(sessao.getId(), area.getId());
                    for (Integer numeroPoltrona : poltronasOcupadas) {
                        IngressoModerno ingresso = new IngressoModerno(
                            sessao,
                            area,
                            new Poltrona(numeroPoltrona),
                            new Usuario() // Usuário temporário apenas para estatísticas
                        );
                        todosIngressos.add(ingresso);
                    }
                }
            }
        }
        
        for (IngressoModerno ingresso : todosIngressos) {
            String eventoNome = eventos.stream()
                .filter(e -> e.getSessoes().stream()
                    .anyMatch(s -> s.getId().equals(ingresso.getSessao().getId())))
                .findFirst()
                .map(Evento::getNome)
                .orElse("Desconhecido");
            vendasPorEvento.merge(eventoNome, 1L, Long::sum);
        }
        
        if (!vendasPorEvento.isEmpty()) {
            String eventoMaisVendido = Collections.max(vendasPorEvento.entrySet(), Map.Entry.comparingByValue()).getKey();
            String eventoMenosVendido = Collections.min(vendasPorEvento.entrySet(), Map.Entry.comparingByValue()).getKey();
            
            estatisticas.put("eventoMaisVendido", vendasPorEvento.get(eventoMaisVendido).intValue());
            estatisticas.put("eventoMenosVendido", vendasPorEvento.get(eventoMenosVendido).intValue());
        }
        
        // Sessão com maior ocupação
        Map<String, Long> ocupacaoPorSessao = new HashMap<>();
        for (IngressoModerno ingresso : todosIngressos) {
            ocupacaoPorSessao.merge(ingresso.getSessao().getHorario(), 1L, Long::sum);
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
        
        // Busca todos os ingressos de todos os usuários
        List<IngressoModerno> todosIngressos = new ArrayList<>();
        for (Evento evento : eventos) {
            for (Sessao sessao : evento.getSessoes()) {
                for (Area area : sessao.getAreas()) {
                    List<Integer> poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(sessao.getId(), area.getId());
                    for (Integer numeroPoltrona : poltronasOcupadas) {
                        IngressoModerno ingresso = new IngressoModerno(
                            sessao,
                            area,
                            new Poltrona(numeroPoltrona),
                            new Usuario() // Usuário temporário apenas para estatísticas
                        );
                        todosIngressos.add(ingresso);
                    }
                }
            }
        }
        
        for (IngressoModerno ingresso : todosIngressos) {
            String eventoNome = eventos.stream()
                .filter(e -> e.getSessoes().stream()
                    .anyMatch(s -> s.getId().equals(ingresso.getSessao().getId())))
                .findFirst()
                .map(Evento::getNome)
                .orElse("Desconhecido");
                
            lucroPorEvento.merge(eventoNome, ingresso.getValor(), Double::sum);
            lucroPorSessao.merge(ingresso.getSessao().getHorario(), ingresso.getValor(), Double::sum);
        }
        
        if (!lucroPorEvento.isEmpty()) {
            String eventoMaisLucrativo = Collections.max(lucroPorEvento.entrySet(), Map.Entry.comparingByValue()).getKey();
            String eventoMenosLucrativo = Collections.min(lucroPorEvento.entrySet(), Map.Entry.comparingByValue()).getKey();
            
            estatisticas.put("eventoMaisLucrativo", lucroPorEvento.get(eventoMaisLucrativo));
            estatisticas.put("eventoMenosLucrativo", lucroPorEvento.get(eventoMenosLucrativo));
        }
        
        if (!lucroPorSessao.isEmpty()) {
            String sessaoMaisLucrativa = Collections.max(lucroPorSessao.entrySet(), Map.Entry.comparingByValue()).getKey();
            String sessaoMenosLucrativa = Collections.min(lucroPorSessao.entrySet(), Map.Entry.comparingByValue()).getKey();
            
            estatisticas.put("sessaoMaisLucrativa", lucroPorSessao.get(sessaoMaisLucrativa));
            estatisticas.put("sessaoMenosLucrativa", lucroPorSessao.get(sessaoMenosLucrativa));
        }
        
        return estatisticas;
    }

    public boolean isPoltronaOcupada(Long sessaoId, Long areaId, int numeroPoltrona) {
        List<Integer> poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(sessaoId, areaId);
        return poltronasOcupadas.contains(numeroPoltrona);
    }
} 