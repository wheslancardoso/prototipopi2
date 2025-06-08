package com.teatro.dao;

import com.teatro.model.Usuario;
import com.teatro.util.TeatroLogger;
import com.teatro.database.DatabaseConnection;
import com.teatro.exception.TeatroException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do DAO para a entidade Usuario.
 */
public class UsuarioDAOImpl implements UsuarioDAO {
    private final TeatroLogger logger = TeatroLogger.getInstance();
    private final DatabaseConnection dbConnection;

    public UsuarioDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // Métodos específicos do UsuarioDAO
    @Override
    public Optional<Usuario> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM usuarios WHERE cpf = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(montarUsuario(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Erro ao buscar usuário por CPF: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar usuário por CPF", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(montarUsuario(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Erro ao buscar usuário por email: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar usuário por email", e);
        }
    }

    @Override
    public Optional<Usuario> autenticar(String identificador, String senha) {
        String sql = "SELECT * FROM usuarios WHERE (cpf = ? OR email = ?) AND senha = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, identificador);
            stmt.setString(2, identificador);
            stmt.setString(3, senha);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(montarUsuario(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Erro ao autenticar usuário: " + e.getMessage());
            throw new RuntimeException("Erro ao autenticar usuário", e);
        }
    }

    @Override
    public Optional<Usuario> autenticarPorEmail(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(montarUsuario(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Erro ao autenticar usuário por email: " + e.getMessage());
            throw new RuntimeException("Erro ao autenticar usuário por email", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorCpfEEmail(String cpf, String email) {
        String sql = "SELECT * FROM usuarios WHERE cpf = ? AND email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(montarUsuario(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Erro ao buscar usuário por CPF e email: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar usuário por CPF e email", e);
        }
    }

    @Override
    public void atualizarSenha(Long id, String novaSenha) {
        String sql = "UPDATE usuarios SET senha = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, novaSenha);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao atualizar senha do usuário: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar senha do usuário", e);
        }
    }

    // Implementação dos métodos da interface DAO
    @Override
    public void salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, cpf, endereco, telefone, email, senha, tipo_usuario) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEndereco());
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getSenha());
            stmt.setString(7, usuario.getTipoUsuario());
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            logger.error("Erro ao salvar usuário: " + e.getMessage());
            throw new TeatroException("Erro ao salvar usuário", e);
        }
    }

    @Override
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, cpf = ?, endereco = ?, telefone = ?, " +
                    "email = ?, senha = ?, tipo_usuario = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEndereco());
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getSenha());
            stmt.setString(7, usuario.getTipoUsuario());
            stmt.setLong(8, usuario.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao atualizar usuário: " + e.getMessage());
            throw new TeatroException("Erro ao atualizar usuário", e);
        }
    }

    @Override
    public void remover(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao remover usuário: " + e.getMessage());
            throw new TeatroException("Erro ao remover usuário", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(montarUsuario(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Erro ao buscar usuário por ID: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar usuário por ID", e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(montarUsuario(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            logger.error("Erro ao listar usuários: " + e.getMessage());
            throw new RuntimeException("Erro ao listar usuários", e);
        }
    }

    @Override
    public boolean existe(Long id) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Erro ao verificar existência do usuário: " + e.getMessage());
            throw new TeatroException("Erro ao verificar existência do usuário", e);
        }
    }

    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setCpf(rs.getString("cpf"));
        usuario.setEndereco(rs.getString("endereco"));
        usuario.setTelefone(rs.getString("telefone"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setTipoUsuario(rs.getString("tipo_usuario"));
        return usuario;
    }
} 