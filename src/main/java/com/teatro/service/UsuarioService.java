package com.teatro.service;

import com.teatro.dao.UsuarioDAO;
import com.teatro.dao.UsuarioDAOImpl;
import com.teatro.exception.TeatroException;
import com.teatro.model.Usuario;
import com.teatro.util.TeatroLogger;
import com.teatro.util.Validator;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciamento de usuários.
 */
public class UsuarioService extends AbstractService<Usuario, Long, UsuarioDAO> {
    private static UsuarioService instance;
    private final UsuarioDAO usuarioDAO;

    private UsuarioService() {
        super(new UsuarioDAOImpl());
        this.usuarioDAO = (UsuarioDAO) dao;
    }

    public static synchronized UsuarioService getInstance() {
        if (instance == null) {
            instance = new UsuarioService();
        }
        return instance;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        validarAntesSalvar(usuario);
        usuarioDAO.salvar(usuario);
        return usuario;
    }

    @Override
    public Usuario atualizar(Usuario usuario) {
        validarAntesAtualizar(usuario);
        usuarioDAO.atualizar(usuario);
        return usuario;
    }

    public void remover(Long id) {
        usuarioDAO.remover(id);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDAO.buscarPorId(id);
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        return usuarioDAO.buscarPorCpf(cpf);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.buscarPorEmail(email);
    }

    public Optional<Usuario> autenticar(String identificador, String senha) {
        return usuarioDAO.autenticar(identificador, senha);
    }

    public Optional<Usuario> autenticarPorEmail(String email, String senha) {
        return usuarioDAO.autenticarPorEmail(email, senha);
    }

    public Optional<Usuario> buscarPorCpfEEmail(String cpf, String email) {
        return usuarioDAO.buscarPorCpfEEmail(cpf, email);
    }

    public void atualizarSenha(Long id, String novaSenha) {
        Validator.validarStringNaoVazia(novaSenha, "Nova senha");
        usuarioDAO.atualizarSenha(id, novaSenha);
    }

    @Override
    protected void validarAntesSalvar(Usuario usuario) {
        Validator.validarStringNaoVazia(usuario.getNome(), "Nome");
        Validator.validarCpf(usuario.getCpf());
        Validator.validarEmail(usuario.getEmail());
        Validator.validarStringNaoVazia(usuario.getSenha(), "Senha");
        Validator.validarStringNaoVazia(usuario.getTipoUsuario(), "Tipo de Usuário");
        
        // Verifica se já existe usuário com o mesmo CPF
        if (buscarPorCpf(usuario.getCpf()).isPresent()) {
            throw new TeatroException("Já existe um usuário cadastrado com este CPF");
        }
        
        // Verifica se já existe usuário com o mesmo email
        if (buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new TeatroException("Já existe um usuário cadastrado com este email");
        }
    }
    
    @Override
    protected void validarAntesAtualizar(Usuario usuario) {
        Validator.validarNaoNulo(usuario.getId(), "ID");
        validarAntesSalvar(usuario);
        
        // Verifica se o usuário existe
        if (!existe(usuario.getId())) {
            throw new TeatroException("Usuário não encontrado");
        }
        
        // Verifica se o CPF já está em uso por outro usuário
        Optional<Usuario> usuarioComCpf = buscarPorCpf(usuario.getCpf());
        if (usuarioComCpf.isPresent() && !usuarioComCpf.get().getId().equals(usuario.getId())) {
            throw new TeatroException("Já existe outro usuário cadastrado com este CPF");
        }
        
        // Verifica se o email já está em uso por outro usuário
        Optional<Usuario> usuarioComEmail = buscarPorEmail(usuario.getEmail());
        if (usuarioComEmail.isPresent() && !usuarioComEmail.get().getId().equals(usuario.getId())) {
            throw new TeatroException("Já existe outro usuário cadastrado com este email");
        }
    }
    
    @Override
    protected void validarAntesRemover(Long id) {
        Validator.validarNaoNulo(id, "ID");
        if (!existe(id)) {
            throw new TeatroException("Usuário não encontrado");
        }
    }
} 