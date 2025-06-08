package com.teatro.service;

import com.teatro.dao.IngressoDAO;
import com.teatro.exception.IngressoException;
import com.teatro.exception.TeatroException;
import com.teatro.model.Ingresso;
import com.teatro.model.Usuario;
import com.teatro.observer.Observer;
import com.teatro.observer.NotificacaoVenda;
import com.teatro.util.TeatroLogger;
import com.teatro.util.Validator;
import com.teatro.database.DatabaseConnection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.SQLException;
import java.util.ArrayList;
import com.teatro.exception.PoltronaOcupadaException;
import com.teatro.exception.UsuarioNaoEncontradoException;
import com.teatro.dao.AreaDAO;
import com.teatro.model.Area;
import com.teatro.observer.VendaLoggerObserver;
import com.teatro.dao.SessaoDAO;
import com.teatro.model.Sessao;
import com.teatro.dao.EventoDAO;
import com.teatro.model.Evento;

/**
 * Serviço para gerenciamento de ingressos.
 */
public class IngressoService extends AbstractService<Ingresso, Long, IngressoDAO> {
    
    private static IngressoService instance;
    private final TeatroLogger logger = TeatroLogger.getInstance();
    private final UsuarioService usuarioService;
    private final List<Observer<NotificacaoVenda>> observers;
    private final IngressoDAO dao;
    private final AreaDAO areaDAO;
    
    private IngressoService() throws SQLException {
        super(new IngressoDAO(DatabaseConnection.getInstance().getConnection()));
        this.dao = (IngressoDAO) super.dao;
        this.usuarioService = UsuarioService.getInstance();
        this.observers = new ArrayList<>();
        this.areaDAO = new AreaDAO(DatabaseConnection.getInstance().getConnection());
        // Registrar observer de log de venda
        this.registerObserver(new VendaLoggerObserver());
    }
    
    /**
     * Obtém a instância única do serviço (Singleton).
     * @return A instância do IngressoService
     */
    public static synchronized IngressoService getInstance() {
        if (instance == null) {
            try {
                instance = new IngressoService();
            } catch (SQLException e) {
                TeatroLogger.getInstance().error("Erro ao inicializar IngressoService: " + e.getMessage());
                throw new TeatroException("Erro ao inicializar serviço de ingressos", e);
            }
        }
        return instance;
    }
    
    /**
     * Registra um observador para notificações de venda.
     * @param observer O observador a ser registrado
     */
    public void registerObserver(Observer<NotificacaoVenda> observer) {
        observers.add(observer);
    }
    
    /**
     * Remove um observador de notificações de venda.
     * @param observer O observador a ser removido
     */
    public void removeObserver(Observer<NotificacaoVenda> observer) {
        observers.remove(observer);
    }
    
    /**
     * Notifica todos os observadores sobre uma venda.
     * @param notificacao A notificação a ser enviada
     */
    private void notifyObservers(NotificacaoVenda notificacao) {
        for (Observer<NotificacaoVenda> observer : observers) {
            observer.update(notificacao);
        }
    }
    
    /**
     * Compra um ingresso para um usuário.
     * @param cpf O CPF do usuário
     * @param sessaoId O ID da sessão
     * @param areaId O ID da área
     * @param numeroPoltrona O número da poltrona
     * @return O ingresso comprado
     */
    public Ingresso comprarIngresso(String cpf, Long sessaoId, Long areaId, int numeroPoltrona) {
        try {
            Validator.validarCpf(cpf);
            Validator.validarNaoNulo(sessaoId, "ID da Sessão");
            Validator.validarNaoNulo(areaId, "ID da Área");
            Validator.validarNumeroPositivo(numeroPoltrona, "Número da Poltrona");
            
            Optional<Usuario> usuario = usuarioService.buscarPorCpf(cpf);
            if (usuario.isEmpty()) {
                throw new UsuarioNaoEncontradoException(cpf);
            }
            
            Ingresso ingresso = new Ingresso();
            ingresso.setUsuarioId(usuario.get().getId());
            ingresso.setSessaoId(sessaoId);
            ingresso.setAreaId(areaId);
            ingresso.setNumeroPoltrona(numeroPoltrona);
            ingresso.setDataCompra(new Timestamp(System.currentTimeMillis()));
            ingresso.setCodigo(gerarCodigoIngresso());
            // Buscar o valor da área e setar no ingresso
            Optional<Area> areaOpt = areaDAO.buscarPorId(areaId);
            if (areaOpt.isPresent()) {
                ingresso.setValor(areaOpt.get().getPreco());
            } else {
                ingresso.setValor(0.0); // fallback, mas ideal lançar exceção
            }
            
            if (dao.poltronaOcupada(sessaoId, areaId, numeroPoltrona)) {
                Optional<Area> area = areaDAO.buscarPorId(areaId);
                String nomeArea = area.map(Area::getNome).orElse("Área desconhecida");
                throw new PoltronaOcupadaException(numeroPoltrona, nomeArea);
            }
            
            dao.salvar(ingresso);
            // Preencher campos de exibição para notificação
            try {
                SessaoDAO sessaoDAO = new SessaoDAO(DatabaseConnection.getInstance().getConnection());
                Optional<Sessao> sessaoOpt = sessaoDAO.buscarPorId(ingresso.getSessaoId());
                if (sessaoOpt.isPresent()) {
                    Sessao sessao = sessaoOpt.get();
                    ingresso.setHorario(sessao.getTipoSessao().getDescricao());
                    ingresso.setDataSessao(sessao.getData());
                    Long eventoId = sessao.getEventoId();
                    if (eventoId != null) {
                        EventoDAO eventoDAO = new EventoDAO(DatabaseConnection.getInstance().getConnection());
                        Evento evento = eventoDAO.buscarPorId(eventoId);
                        if (evento != null) {
                            ingresso.setEventoNome(evento.getNome());
                        }
                    }
                }
                AreaDAO areaDAO = new AreaDAO(DatabaseConnection.getInstance().getConnection());
                Optional<Area> areaOptNotificacao = areaDAO.buscarPorId(ingresso.getAreaId());
                areaOptNotificacao.ifPresent(area -> ingresso.setAreaNome(area.getNome()));
            } catch (Exception e) {
                logger.error("Erro ao preencher dados de exibição do ingresso para notificação: " + e.getMessage());
            }
            notifyObservers(new NotificacaoVenda(ingresso));
            return ingresso;
        } catch (TeatroException e) {
            logger.error("Erro ao comprar ingresso: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao comprar ingresso: " + e.getMessage());
            throw new TeatroException("Erro ao comprar ingresso", e);
        }
    }
    
    /**
     * Busca ingressos por usuário.
     * @param cpf O CPF do usuário
     * @return Lista de ingressos do usuário
     */
    public List<Ingresso> buscarPorUsuario(String cpf) {
        try {
            Validator.validarCpf(cpf);
            Optional<Usuario> usuario = usuarioService.buscarPorCpf(cpf);
            if (usuario.isEmpty()) {
                throw new TeatroException("Usuário não encontrado");
            }
            return dao.buscarPorUsuario(usuario.get().getId());
        } catch (Exception e) {
            logger.error("Erro ao buscar ingressos do usuário: {}", e.getMessage());
            throw new TeatroException("Erro ao buscar ingressos do usuário", e);
        }
    }
    
    /**
     * Busca ingressos por sessão.
     * @param sessaoId O ID da sessão
     * @return Lista de ingressos da sessão
     */
    public List<Ingresso> buscarPorSessao(Long sessaoId) {
        try {
            Validator.validarNaoNulo(sessaoId, "ID da Sessão");
            return dao.buscarPorSessao(sessaoId);
        } catch (Exception e) {
            logger.error("Erro ao buscar ingressos da sessão: {}", e.getMessage());
            throw new TeatroException("Erro ao buscar ingressos da sessão", e);
        }
    }
    
    /**
     * Verifica se uma poltrona está ocupada.
     * @param sessaoId O ID da sessão
     * @param areaId O ID da área
     * @param numeroPoltrona O número da poltrona
     * @return true se a poltrona estiver ocupada, false caso contrário
     */
    public boolean poltronaOcupada(Long sessaoId, Long areaId, int numeroPoltrona) {
        try {
            Validator.validarNaoNulo(areaId, "ID da Área");
            Validator.validarNumeroPositivo(numeroPoltrona, "Número da Poltrona");
            if (sessaoId != null) {
                Validator.validarNaoNulo(sessaoId, "ID da Sessão");
            }
            return dao.poltronaOcupada(sessaoId, areaId, numeroPoltrona);
        } catch (Exception e) {
            logger.error("Erro ao verificar ocupação da poltrona: {}", e.getMessage());
            throw new TeatroException("Erro ao verificar ocupação da poltrona", e);
        }
    }
    
    /**
     * Busca as poltronas ocupadas para uma área em uma sessão.
     * @param sessaoId O ID da sessão (pode ser null para buscar todas as sessões)
     * @param areaId O ID da área
     * @return Lista com os números das poltronas ocupadas
     */
    public List<Integer> getPoltronasOcupadas(Long sessaoId, Long areaId) {
        try {
            Validator.validarNaoNulo(areaId, "ID da Área");
            if (sessaoId != null) {
                Validator.validarNaoNulo(sessaoId, "ID da Sessão");
            }
            
            List<Ingresso> ingressos = sessaoId != null ? 
                dao.buscarPorSessao(sessaoId) : 
                dao.listarTodos();
                
            return ingressos.stream()
                .filter(i -> i.getAreaId().equals(areaId))
                .map(Ingresso::getNumeroPoltrona)
                .toList();
        } catch (Exception e) {
            logger.error("Erro ao buscar poltronas ocupadas: {}", e.getMessage());
            throw new TeatroException("Erro ao buscar poltronas ocupadas", e);
        }
    }
    
    @Override
    protected void validarAntesSalvar(Ingresso ingresso) {
        Validator.validarNaoNulo(ingresso.getUsuarioId(), "ID do Usuário");
        Validator.validarNaoNulo(ingresso.getSessaoId(), "ID da Sessão");
        Validator.validarNaoNulo(ingresso.getAreaId(), "ID da Área");
        Validator.validarNumeroPositivo(ingresso.getNumeroPoltrona(), "Número da Poltrona");
        Validator.validarNaoNulo(ingresso.getDataCompra(), "Data de Compra");
        Validator.validarStringNaoVazia(ingresso.getCodigo(), "Código");
        
        // Verifica se a poltrona está ocupada
        if (poltronaOcupada(ingresso.getSessaoId(), ingresso.getAreaId(), ingresso.getNumeroPoltrona())) {
            throw new IngressoException("Poltrona já ocupada");
        }
    }
    
    @Override
    protected void validarAntesAtualizar(Ingresso ingresso) {
        Validator.validarNaoNulo(ingresso.getId(), "ID");
        validarAntesSalvar(ingresso);
        
        // Verifica se o ingresso existe
        if (!existe(ingresso.getId())) {
            throw new TeatroException("Ingresso não encontrado");
        }
    }
    
    @Override
    protected void validarAntesRemover(Long id) {
        Validator.validarNaoNulo(id, "ID");
        if (!existe(id)) {
            throw new TeatroException("Ingresso não encontrado");
        }
    }
    
    private String gerarCodigoIngresso() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public List<Ingresso> buscarIngressosPorUsuario(Long usuarioId) {
        try {
            return dao.buscarPorUsuario(usuarioId);
        } catch (Exception e) {
            logger.error("Erro ao buscar ingressos do usuário: {}", e.getMessage());
            throw new TeatroException("Erro ao buscar ingressos do usuário", e);
        }
    }

    public List<Ingresso> buscarIngressosPorSessao(Long sessaoId) {
        try {
            return dao.buscarPorSessao(sessaoId);
        } catch (Exception e) {
            logger.error("Erro ao buscar ingressos da sessão: {}", e.getMessage());
            throw new TeatroException("Erro ao buscar ingressos da sessão", e);
        }
    }

    public void cancelarIngresso(Long ingressoId) {
        try {
            dao.remover(ingressoId);
        } catch (Exception e) {
            logger.error("Erro ao cancelar ingresso: {}", e.getMessage());
            throw new TeatroException("Erro ao cancelar ingresso", e);
        }
    }
} 