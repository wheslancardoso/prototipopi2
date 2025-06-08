package com.teatro.model;

import com.teatro.service.IngressoService;
import com.teatro.service.UsuarioService;
import com.teatro.util.TeatroLogger;
import com.teatro.util.Validator;
import com.teatro.exception.TeatroException;
import com.teatro.exception.UsuarioNaoEncontradoException;
import com.teatro.exception.SessaoNaoEncontradaException;
import com.teatro.exception.PoltronaOcupadaException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import com.teatro.dao.EventoDAO;
import com.teatro.database.DatabaseConnection;
import java.sql.Connection;
import com.teatro.dao.SessaoDAO;
import com.teatro.dao.AreaDAO;

/**
 * Classe principal do sistema, implementando o padrão Façade.
 */
public class Teatro {
    private static Teatro instance;
    private final TeatroLogger logger = TeatroLogger.getInstance();
    private final UsuarioService usuarioService;
    private final IngressoService ingressoService;
    private List<Area> areas;
    private List<Evento> eventos;
    
    private Teatro() {
        this.usuarioService = UsuarioService.getInstance();
        this.ingressoService = IngressoService.getInstance();
        this.areas = new ArrayList<>();
        this.eventos = new ArrayList<>();
        carregarAreasDoBanco();
        carregarEventosDoBanco();
    }
    
    public static synchronized Teatro getInstance() {
        if (instance == null) {
            instance = new Teatro();
        }
        return instance;
    }
    
    public UsuarioService getUsuarioService() {
        return usuarioService;
    }
    
    public IngressoService getIngressoService() {
        return ingressoService;
    }
    
    public List<Ingresso> buscarIngressosPorCpf(String cpf) {
        try {
            Validator.validarCpf(cpf);
            Optional<Usuario> usuario = usuarioService.buscarPorCpf(cpf);
            if (usuario.isPresent()) {
                return ingressoService.buscarIngressosPorUsuario(usuario.get().getId());
            }
            return new ArrayList<>();
        } catch (TeatroException e) {
            logger.error("Erro ao buscar ingressos por CPF: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar ingressos por CPF: " + e.getMessage());
            throw new TeatroException("Erro ao buscar ingressos por CPF", e);
        }
    }
    
    public List<Ingresso> buscarIngressosPorUsuario(Long usuarioId) {
        try {
            Validator.validarNaoNulo(usuarioId, "ID do Usuário");
            return ingressoService.buscarIngressosPorUsuario(usuarioId);
        } catch (TeatroException e) {
            logger.error("Erro ao buscar ingressos do usuário: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar ingressos do usuário: " + e.getMessage());
            throw new TeatroException("Erro ao buscar ingressos do usuário", e);
        }
    }
    
    public List<Ingresso> buscarIngressosPorSessao(Long sessaoId) {
        try {
            Validator.validarNaoNulo(sessaoId, "ID da Sessão");
            return ingressoService.buscarIngressosPorSessao(sessaoId);
        } catch (TeatroException e) {
            logger.error("Erro ao buscar ingressos da sessão: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar ingressos da sessão: " + e.getMessage());
            throw new TeatroException("Erro ao buscar ingressos da sessão", e);
        }
    }
    
    public void cancelarIngresso(Long ingressoId) {
        try {
            Validator.validarNaoNulo(ingressoId, "ID do Ingresso");
            ingressoService.cancelarIngresso(ingressoId);
        } catch (TeatroException e) {
            logger.error("Erro ao cancelar ingresso: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao cancelar ingresso: " + e.getMessage());
            throw new TeatroException("Erro ao cancelar ingresso", e);
        }
    }

    private void carregarAreasDoBanco() {
        try {
            java.sql.Connection conn = com.teatro.database.DatabaseConnection.getInstance().getConnection();
            AreaDAO areaDAO = new AreaDAO(conn);
            this.areas = areaDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Erro ao carregar áreas do banco: " + e.getMessage());
        }
    }

    private void carregarEventosDoBanco() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            EventoDAO eventoDAO = new EventoDAO(conn);
            SessaoDAO sessaoDAO = new SessaoDAO(conn);
            this.eventos = eventoDAO.listarTodos();
            for (Evento evento : this.eventos) {
                List<Sessao> sessoes = sessaoDAO.buscarPorEvento(evento.getId());
                for (Sessao sessao : sessoes) {
                    evento.addSessao(sessao);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao carregar eventos e sessões do banco: " + e.getMessage());
        }
    }

    public List<Area> getAreas() {
        return areas;
    }

    public Optional<Usuario> autenticarUsuario(String identificador, String senha) {
        try {
            Validator.validarStringNaoVazia(identificador, "Identificador (CPF ou Email)");
            Validator.validarStringNaoVazia(senha, "Senha");
            
            // Se o identificador parece ser um email, valida como email
            if (identificador.contains("@")) {
                Validator.validarEmail(identificador);
            } else {
                // Caso contrário, valida como CPF
                Validator.validarCpf(identificador);
            }
            
            return usuarioService.autenticar(identificador, senha);
        } catch (TeatroException e) {
            logger.error("Erro ao autenticar usuário: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao autenticar usuário: " + e.getMessage());
            throw new TeatroException("Erro ao autenticar usuário", e);
        }
    }

    public boolean cadastrarUsuario(Usuario usuario) {
        try {
            Validator.validarNaoNulo(usuario, "Usuário");
            usuarioService.salvar(usuario);
            return true;
        } catch (TeatroException e) {
            logger.error("Erro ao cadastrar usuário: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao cadastrar usuário: " + e.getMessage());
            throw new TeatroException("Erro ao cadastrar usuário", e);
        }
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public List<Area> getAreasDisponiveis(Sessao sessao) {
        try {
            Validator.validarNaoNulo(sessao, "Sessão");
            java.sql.Connection conn = DatabaseConnection.getInstance().getConnection();
            AreaDAO areaDAO = new AreaDAO(conn);
            List<Area> areasDaSessao = areaDAO.buscarPorSessao(sessao.getId());
            List<Area> areasDisponiveis = new ArrayList<>();
            for (Area area : areasDaSessao) {
                // Busca as poltronas ocupadas para esta área na sessão
                List<Integer> poltronasOcupadas = ingressoService.getPoltronasOcupadas(sessao.getId(), area.getId());
                // Cria uma cópia da área com as poltronas ocupadas
                Area areaAtualizada = new Area(area.getId(), area.getNome(), area.getPreco(), area.getCapacidadeTotal());
                areaAtualizada.carregarPoltronasOcupadas(poltronasOcupadas);
                // Adiciona apenas se houver poltronas disponíveis
                if (areaAtualizada.getPoltronasDisponiveis() > 0) {
                    areasDisponiveis.add(areaAtualizada);
                }
            }
            return areasDisponiveis;
        } catch (TeatroException e) {
            logger.error("Erro ao buscar áreas disponíveis: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar áreas disponíveis: " + e.getMessage());
            throw new TeatroException("Erro ao buscar áreas disponíveis", e);
        }
    }
    
    public Area getAreaAtualizada(Long sessaoId, Long areaId) {
        try {
            Validator.validarNaoNulo(areaId, "ID da Área");
            Validator.validarNaoNulo(sessaoId, "ID da Sessão");
            // Busca a área original
            Optional<Area> areaOriginal = areas.stream()
                .filter(a -> a.getId().equals(areaId))
                .findFirst();
            if (areaOriginal.isEmpty()) {
                throw new TeatroException("Área não encontrada");
            }
            // Busca as poltronas ocupadas para esta área NA SESSÃO CORRETA
            List<Integer> poltronasOcupadas = ingressoService.getPoltronasOcupadas(sessaoId, areaId);
            // Cria uma cópia da área com as poltronas ocupadas
            Area areaAtualizada = new Area(
                areaOriginal.get().getId(),
                areaOriginal.get().getNome(),
                areaOriginal.get().getPreco(),
                areaOriginal.get().getCapacidadeTotal()
            );
            areaAtualizada.carregarPoltronasOcupadas(poltronasOcupadas);
            return areaAtualizada;
        } catch (TeatroException e) {
            logger.error("Erro ao buscar área atualizada: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar área atualizada: " + e.getMessage());
            throw new TeatroException("Erro ao buscar área atualizada", e);
        }
    }
    
    public List<Integer> getPoltronasDisponiveis(Sessao sessao, Area area) {
        try {
            Validator.validarNaoNulo(sessao, "Sessão");
            Validator.validarNaoNulo(area, "Área");
            
            // Busca as poltronas ocupadas para esta área na sessão
            List<Integer> poltronasOcupadas = ingressoService.getPoltronasOcupadas(sessao.getId(), area.getId());
            
            // Cria uma lista com todas as poltronas disponíveis
            List<Integer> poltronasDisponiveis = new ArrayList<>();
            for (int i = 1; i <= area.getCapacidadeTotal(); i++) {
                if (!poltronasOcupadas.contains(i)) {
                    poltronasDisponiveis.add(i);
                }
            }
            
            return poltronasDisponiveis;
        } catch (TeatroException e) {
            logger.error("Erro ao buscar poltronas disponíveis: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar poltronas disponíveis: " + e.getMessage());
            throw new TeatroException("Erro ao buscar poltronas disponíveis", e);
        }
    }
    
    public boolean verificarPoltronaDisponivel(Sessao sessao, Area area, int numeroPoltrona) {
        try {
            Validator.validarNaoNulo(sessao, "Sessão");
            Validator.validarNaoNulo(area, "Área");
            Validator.validarNumeroPositivo(numeroPoltrona, "Número da Poltrona");
            
            if (numeroPoltrona > area.getCapacidadeTotal()) {
                throw new TeatroException("Número da poltrona inválido para esta área");
            }
            
            // Verifica se a poltrona está ocupada
            return !ingressoService.poltronaOcupada(sessao.getId(), area.getId(), numeroPoltrona);
        } catch (TeatroException e) {
            logger.error("Erro ao verificar disponibilidade da poltrona: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao verificar disponibilidade da poltrona: " + e.getMessage());
            throw new TeatroException("Erro ao verificar disponibilidade da poltrona", e);
        }
    }

    public Optional<IngressoModerno> comprarIngresso(String cpf, Evento evento, Sessao sessao, Area area, int numeroPoltrona) {
        try {
            Validator.validarCpf(cpf);
            Validator.validarNaoNulo(evento, "Evento");
            Validator.validarNaoNulo(sessao, "Sessão");
            Validator.validarNaoNulo(area, "Área");
            Validator.validarNumeroPositivo(numeroPoltrona, "Número da Poltrona");

            Optional<Usuario> usuario = usuarioService.buscarPorCpf(cpf);
            if (usuario.isEmpty()) {
                throw new UsuarioNaoEncontradoException(cpf);
            }

            if (!eventos.contains(evento)) {
                throw new TeatroException("Evento não encontrado");
            }

            if (!evento.getSessoes().contains(sessao)) {
                throw new SessaoNaoEncontradaException(evento.getNome(), sessao.getHorario().toString());
            }

            if (!areas.contains(area)) {
                throw new TeatroException("Área não encontrada");
            }

            if (numeroPoltrona > area.getCapacidadeTotal()) {
                throw new TeatroException("Número da poltrona inválido para esta área");
            }

            if (ingressoService.poltronaOcupada(sessao.getId(), area.getId(), numeroPoltrona)) {
                throw new PoltronaOcupadaException(numeroPoltrona, area.getNome());
            }

            Ingresso ingresso = ingressoService.comprarIngresso(cpf, sessao.getId(), area.getId(), numeroPoltrona);
            IngressoModerno ingressoModerno = new IngressoModerno(ingresso, evento, sessao, area);
            return Optional.ofNullable(ingressoModerno);
        } catch (TeatroException e) {
            logger.error("Erro ao comprar ingresso: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao comprar ingresso: " + e.getMessage());
            throw new TeatroException("Erro ao comprar ingresso", e);
        }
    }

    private String gerarCodigoIngresso() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }

    public Optional<Usuario> verificarUsuarioParaRecuperacao(String cpf, String email) {
        try {
            Validator.validarCpf(cpf);
            Validator.validarEmail(email);
            return usuarioService.buscarPorCpfEEmail(cpf, email);
        } catch (TeatroException e) {
            logger.error("Erro ao verificar usuário para recuperação: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao verificar usuário para recuperação: " + e.getMessage());
            throw new TeatroException("Erro ao verificar usuário para recuperação", e);
        }
    }

    public void recuperarSenha(Long usuarioId, String novaSenha) {
        try {
            Validator.validarNaoNulo(usuarioId, "ID do Usuário");
            Validator.validarStringNaoVazia(novaSenha, "Nova senha");
            usuarioService.atualizarSenha(usuarioId, novaSenha);
        } catch (TeatroException e) {
            logger.error("Erro ao recuperar senha: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao recuperar senha: " + e.getMessage());
            throw new TeatroException("Erro ao recuperar senha", e);
        }
    }
} 