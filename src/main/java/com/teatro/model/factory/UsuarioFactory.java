package com.teatro.model.factory;

import com.teatro.model.Usuario;

public interface UsuarioFactory {
    Usuario criarUsuario(String nome, String cpf, String email, String senha, String endereco, String telefone);
} 