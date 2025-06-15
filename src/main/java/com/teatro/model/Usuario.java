package com.teatro.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private Long id;
    private String nome;
    private String cpf;
    private String endereco;
    private String telefone;
    private String email;
    private String senha;
    private String tipoUsuario;
    private List<IngressoModerno> ingressos = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nome, String cpf, String endereco, String telefone, String email, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = "COMUM";
    }

    public Usuario(String nome, String cpf, String senha, String email, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
        this.tipoUsuario = "COMUM";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public List<IngressoModerno> getIngressos() {
        return ingressos;
    }

    public void setIngressos(List<IngressoModerno> ingressos) {
        this.ingressos = ingressos;
    }

    /**
     * Adiciona uma lista de ingressos ao usuário
     * @param ingressos Lista de ingressos a serem adicionados
     */
    public void adicionarIngressos(List<IngressoModerno> ingressos) {
        if (this.ingressos == null) {
            this.ingressos = new ArrayList<>();
        }
        this.ingressos.addAll(ingressos);
    }
} 