package com.teatro.model.factory;

import com.teatro.model.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioFactoryTest {
    @Test
    void testCriarUsuarioComum() {
        UsuarioFactory factory = new UsuarioComumFactory();
        Usuario usuario = factory.criarUsuario("João", "12345678900", "joao@email.com", "senha123", "Rua A", "99999-9999");
        assertEquals("COMUM", usuario.getTipoUsuario());
        assertEquals("João", usuario.getNome());
    }

    @Test
    void testCriarUsuarioAdmin() {
        UsuarioFactory factory = new UsuarioAdminFactory();
        Usuario usuario = factory.criarUsuario("Maria", "98765432100", "maria@email.com", "senha456", "Rua B", "88888-8888");
        assertEquals("ADMIN", usuario.getTipoUsuario());
        assertEquals("Maria", usuario.getNome());
    }
} 