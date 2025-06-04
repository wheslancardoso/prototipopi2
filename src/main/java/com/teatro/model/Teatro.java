package com.teatro.model;

import com.teatro.dao.UsuarioDAO;
import com.teatro.dao.IngressoDAO;
import java.time.LocalDate;
import java.time.LocalTime;
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
        // Limpa a lista de eventos existentes
        eventos.clear();
        
        String[] nomesEventos = {"The Batman", "Kill Bill: Volume 1", "Django Livre"};
        String[] horariosSessoes = {"Manhã", "Tarde", "Noite"};
        long sessaoId = 1; // Contador para IDs das sessões
        
        // Inicializa o DAO se necessário
        if (ingressoDAO == null) {
            ingressoDAO = new IngressoDAO();
        }
        
        // Cria eventos e sessões
        for (String nomeEvento : nomesEventos) {
            Evento evento = new Evento(nomeEvento);
            
            for (String horario : horariosSessoes) {
                // Para cada horario generico, criar uma sessao
                Sessao sessao = new Sessao(horario);
                sessao.setId(sessaoId);
                sessao.setNome(nomeEvento);
                
                // Define uma data para a sessão (data atual como padrão)
                sessao.setDataSessao(LocalDate.now());
                
                // Obtém o horário específico para a sessão (manhã, tarde, noite)
                HorarioDisponivel horarioEspecifico = obterHorarioEspecifico(horario);
                sessao.setHorarioEspecifico(horarioEspecifico);
                
                // Adiciona cada uma das áreas do teatro à sessão (com instâncias independentes)
                for (Map.Entry<String, Area> entry : areas.entrySet()) {
                    Area areaOriginal = entry.getValue();
                    Area areaCopia = new Area(
                        areaOriginal.getId(),
                        areaOriginal.getNome(),
                        areaOriginal.getPreco(),
                        areaOriginal.getCapacidadeTotal()
                    );
                    
                    try {
                        // Converte a data da sessão para o formato esperado pelo DAO
                        String dataSessaoFormatada = sessao.getDataSessao() != null ? 
                                                   sessao.getDataSessao().toString() : null; // Formato YYYY-MM-DD
                        
                        // Inicializa a lista de poltronas ocupadas
                        List<Integer> poltronasOcupadas = new ArrayList<>();
                        
                        // Obtém o ID do horário específico
                        Long horarioEspecificoId = horarioEspecifico != null ? horarioEspecifico.getId() : null;
                        
                        // Converte o ID da área para o formato numérico usado no banco
                        long areaIdLong = getAreaIdAsLong(areaCopia.getId());
                        
                        // Busca poltronas ocupadas com o filtro de data
                        if (dataSessaoFormatada != null) {
                            try {
                                poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(
                                        sessao.getId(), areaIdLong, horarioEspecificoId, dataSessaoFormatada);
                            } catch (Exception e) {
                                System.err.println("Erro ao buscar poltronas com data para área " + 
                                                   areaCopia.getNome() + ": " + e.getMessage());
                                // Tentativa de fallback para busca sem data
                                poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(
                                        sessao.getId(), areaIdLong, horarioEspecificoId);
                            }
                        } else {
                            // Busca sem data
                            poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(
                                    sessao.getId(), areaIdLong, horarioEspecificoId);
                        }
                        
                        // Garante que a lista de poltronas ocupadas não seja nula
                        if (poltronasOcupadas == null) poltronasOcupadas = new ArrayList<>();
                        
                        // Marca as poltronas como ocupadas no modelo usando o método correto
                        areaCopia.carregarPoltronasOcupadas(poltronasOcupadas);
                        
                        // Registra informações de debugging
                        System.out.println("Inicializando sessão: " + sessao.getNome() + 
                                         " Horário: " + sessao.getHorario() + 
                                         " Data: " + (dataSessaoFormatada != null ? dataSessaoFormatada : "N/A") + 
                                         " Área " + areaCopia.getNome() + ": " + poltronasOcupadas.size() + 
                                         " poltronas ocupadas, " + areaCopia.getPoltronasDisponiveis() + 
                                         " disponíveis");
                        
                    } catch (Exception e) {
                        // Em caso de erro, exibe mensagem de erro
                        System.err.println("Erro ao inicializar área " + areaCopia.getNome() + 
                                         " para sessão " + sessao.getNome() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    // Adiciona a área à sessão
                    sessao.addArea(areaCopia);
                }
                
                // Adiciona a sessão ao evento
                evento.addSessao(sessao);
                
                sessaoId++;
            }
            
            // Adiciona o evento à lista de eventos
            eventos.add(evento);
            
            System.out.println("Evento '" + evento.getNome() + "' inicializado com " + 
                evento.getSessoes().size() + " sessões");
        }
        
        System.out.println("\n--- Inicialização concluída ---");
        System.out.println("Total de eventos: " + eventos.size());
        System.out.println("Total de sessões: " + (sessaoId - 1));
    }

    public boolean cadastrarUsuario(Usuario usuario) {
        return usuarioDAO.cadastrar(usuario);
    }

    public Optional<Usuario> autenticarUsuario(String login, String senha) {
        System.out.println("Tentando autenticar usuário com login: " + login);
        
        // Primeiro tenta buscar por CPF
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorCpf(login);
        
        // Se não encontrou por CPF, tenta buscar por e-mail
        if (usuarioOpt.isEmpty()) {
            System.out.println("Usuário não encontrado por CPF, tentando por e-mail...");
            usuarioOpt = usuarioDAO.buscarPorEmail(login);
        }
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            System.out.println("Senha fornecida: " + senha);
            System.out.println("Senha armazenada: " + (usuario.getSenha() != null ? "[PROTEGIDA]" : "nula"));
            
            if (usuario.getSenha() != null && usuario.getSenha().equals(senha)) {
                System.out.println("Autenticação bem-sucedida!");
                return Optional.of(usuario);
            } else {
                System.out.println("Senha incorreta!");
            }
        } else {
            System.out.println("Usuário não encontrado com o login fornecido!");
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
        // Chama a versão com horário específico como null
        return isPoltronaOcupada(sessaoId, areaId, numeroPoltrona, null);
    }
    
    /**
     * Verifica se uma poltrona específica está ocupada em uma determinada sessão, área e horário específico.
     * @param sessaoId ID da sessão
     * @param areaId ID da área
     * @param numeroPoltrona Número da poltrona
     * @param horarioEspecificoId ID do horário específico
     * @return true se a poltrona estiver ocupada, false caso contrário
     */
    public boolean isPoltronaOcupada(Long sessaoId, String areaId, int numeroPoltrona, Long horarioEspecificoId) {
        // Primeiro verifica no modelo local
        for (Evento evento : eventos) {
            for (Sessao sessao : evento.getSessoes()) {
                if (sessao.getId().equals(sessaoId)) {
                    for (Area area : sessao.getAreas()) {
                        if (area.getId().equals(areaId)) {
                            // Verifica se a poltrona está ocupada no modelo local
                            if (area.getPoltronas() != null && numeroPoltrona > 0 && numeroPoltrona <= area.getPoltronas().size()) {
                                if (area.getPoltronas().get(numeroPoltrona - 1)) {
                                    return true;
                                }
                            }
                            
                            // Se não estiver ocupada no modelo local, verifica no banco de dados
                            String dataSessao = sessao.getDataSessao() != null ? sessao.getDataSessao().toString() : null;
                            List<Integer> poltronasOcupadas = dataSessao != null ?
                                ingressoDAO.buscarPoltronasOcupadas(sessaoId, getAreaIdAsLong(areaId), horarioEspecificoId, dataSessao) :
                                ingressoDAO.buscarPoltronasOcupadas(sessaoId, getAreaIdAsLong(areaId), horarioEspecificoId);
                            return poltronasOcupadas.contains(numeroPoltrona);
                        }
                    }
                }
            }
        }
        // Se não encontrou a área ou sessão, verifica no banco de dados como fallback
        List<Integer> poltronasOcupadas = ingressoDAO.buscarPoltronasOcupadas(sessaoId, 
            getAreaIdAsLong(areaId), horarioEspecificoId);
        return poltronasOcupadas.contains(numeroPoltrona);
    }
    
    /**
     * Converte o ID da área para o formato numérico usado no banco de dados.
     * @param areaId ID da área no formato String
     * @return ID da área no formato numérico
     */
    public long getAreaIdAsLong(String areaId) {
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
    
    /**
     * Retorna um horário específico baseado no tipo de sessão
     * @param tipoSessao Tipo de sessão (Manhã, Tarde ou Noite)
     * @return HorarioDisponivel correspondente ao tipo de sessão
     */
    private HorarioDisponivel obterHorarioEspecifico(String tipoSessao) {
        switch (tipoSessao) {
            case "Manhã":
                return new HorarioDisponivel(1L, tipoSessao, LocalTime.of(10, 0), 1);
            case "Tarde":
                return new HorarioDisponivel(2L, tipoSessao, LocalTime.of(15, 0), 2);
            case "Noite":
                return new HorarioDisponivel(3L, tipoSessao, LocalTime.of(19, 0), 3);
            default:
                return new HorarioDisponivel(0L, "Indefinido", LocalTime.of(0, 0), 0);
        }
    }
    
    /**
     * Retorna o DAO de ingressos para operações específicas
     * @return IngressoDAO utilizado pelo teatro
     */
    public IngressoDAO getIngressoDAO() {
        return this.ingressoDAO;
    }

    public Optional<IngressoModerno> comprarIngresso(String cpf, Evento evento, Sessao sessao, Area area, int numeroPoltrona) {
        // Verifica se o usuário existe
        Optional<Usuario> usuario = usuarioDAO.buscarPorCpf(cpf);
        if (usuario.isEmpty()) {
            return Optional.empty();
        }

        // Encontra a área correspondente na sessão
        Optional<Area> areaEvento = sessao.getAreas().stream()
                .filter(a -> a.getId().equals(area.getId()))
                .findFirst();

        // Verifica se a área existe e se a poltrona está disponível
        if (areaEvento.isPresent() && areaEvento.get().ocuparPoltrona(numeroPoltrona)) {
            // Obtém o ID numérico da área para o banco de dados
            long areaIdLong = getAreaIdAsLong(area.getId());
            
            // Obtém o ID do horário específico da sessão
            Long horarioEspecificoId = null;
            if (sessao.getHorarioEspecifico() != null) {
                horarioEspecificoId = sessao.getHorarioEspecifico().getId();
            }
            
            // Cria o ingresso moderno
            Poltrona poltrona = new Poltrona(numeroPoltrona);
            IngressoModerno ingressoModerno = new IngressoModerno(sessao, area, poltrona, usuario.get());
            
            // Cria e salva o ingresso no banco de dados
            Ingresso ingressoParaSalvar = new Ingresso(
                usuario.get().getId(),
                sessao.getId(),
                areaIdLong,
                numeroPoltrona,
                area.getPreco(),
                horarioEspecificoId
            );
            
            // Define as informações adicionais do ingresso para exibição
            ingressoParaSalvar.setEventoNome(sessao.getNome());
            ingressoParaSalvar.setHorario(sessao.getHorarioCompleto());
            ingressoParaSalvar.setAreaNome(area.getNome());
            
            // Salva o ingresso no banco de dados e obtém o ID gerado
            Long idSalvo = ingressoDAO.salvarERetornarId(ingressoParaSalvar);
            
            if (idSalvo != null) {
                // Atualiza o ID do ingresso moderno com o ID gerado pelo banco de dados
                ingressoModerno.setId(idSalvo);
                
                // Atualiza o status da poltrona e o faturamento
                ingressoDAO.atualizarStatusPoltrona(sessao.getId(), areaIdLong, numeroPoltrona, true);
                ingressoDAO.atualizarFaturamento(sessao.getId(), areaIdLong, area.getPreco());
                
                System.out.println("Ingresso salvo com sucesso. ID: " + idSalvo + 
                                 ", Sessão: " + sessao.getNome() + 
                                 ", Área: " + area.getNome() + 
                                 ", Poltrona: " + numeroPoltrona);
                
                return Optional.of(ingressoModerno);
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