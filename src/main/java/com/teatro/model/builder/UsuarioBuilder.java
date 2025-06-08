package com.teatro.model.builder;

import com.teatro.model.Usuario;
import com.teatro.util.Validator;
import java.util.ArrayList;

/**
 * Builder para a classe Usuario.
 */
public class UsuarioBuilder extends AbstractBuilder<Usuario, UsuarioBuilder> {
    private final Usuario usuario;
    
    public UsuarioBuilder() {
        this.usuario = new Usuario();
        usuario.setIngressos(new ArrayList<>());
        usuario.setTipoUsuario("COMUM");
    }
    
    public UsuarioBuilder comId(Long id) {
        usuario.setId(id);
        return self();
    }
    
    public UsuarioBuilder comNome(String nome) {
        usuario.setNome(nome);
        return self();
    }
    
    public UsuarioBuilder comCpf(String cpf) {
        usuario.setCpf(cpf);
        return self();
    }
    
    public UsuarioBuilder comEndereco(String endereco) {
        usuario.setEndereco(endereco);
        return self();
    }
    
    public UsuarioBuilder comTelefone(String telefone) {
        usuario.setTelefone(telefone);
        return self();
    }
    
    public UsuarioBuilder comEmail(String email) {
        usuario.setEmail(email);
        return self();
    }
    
    public UsuarioBuilder comSenha(String senha) {
        usuario.setSenha(senha);
        return self();
    }
    
    public UsuarioBuilder comTipoUsuario(String tipoUsuario) {
        usuario.setTipoUsuario(tipoUsuario);
        return self();
    }
    
    @Override
    protected Usuario getObjeto() {
        return usuario;
    }
    
    @Override
    protected UsuarioBuilder self() {
        return this;
    }
    
    @Override
    protected void validar() {
        Validator.validarStringNaoVazia(usuario.getNome(), "Nome");
        Validator.validarCpf(usuario.getCpf());
        Validator.validarEmail(usuario.getEmail());
        Validator.validarTelefone(usuario.getTelefone());
        Validator.validarStringNaoVazia(usuario.getSenha(), "Senha");
        Validator.validarStringNaoVazia(usuario.getTipoUsuario(), "Tipo de Usuário");
        
        if (!usuario.getTipoUsuario().equals("COMUM") && !usuario.getTipoUsuario().equals("ADMIN")) {
            throw new IllegalStateException("Tipo de usuário inválido: " + usuario.getTipoUsuario());
        }
    }
} 