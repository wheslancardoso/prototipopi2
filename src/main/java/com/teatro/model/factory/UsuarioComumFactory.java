package com.teatro.model.factory;

import com.teatro.model.Usuario;

public class UsuarioComumFactory implements UsuarioFactory {
    @Override
    public Usuario criarUsuario(String nome, String cpf, String email, String senha, String endereco, String telefone) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setEndereco(endereco);
        usuario.setTelefone(telefone);
        usuario.setTipoUsuario("COMUM");
        return usuario;
    }
} 