package com.teatro.view;

import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {
    private Teatro teatro;
    private Stage stage;

    public LoginView(Teatro teatro, Stage stage) {
        this.teatro = teatro;
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Login");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #ecf0f1;");

        // Container do formulário
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(400);
        formContainer.setStyle("""
            -fx-background-color: white;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            -fx-background-radius: 5;
            """);

        // Título
        Label titulo = new Label("Login");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Campos de login
        TextField cpfField = new TextField();
        cpfField.setPromptText("CPF");
        cpfField.setMaxWidth(300);
        cpfField.setStyle("""
            -fx-padding: 10;
            -fx-background-radius: 5;
            """);

        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Senha");
        senhaField.setMaxWidth(300);
        senhaField.setStyle("""
            -fx-padding: 10;
            -fx-background-radius: 5;
            """);

        // Botões
        Button loginButton = new Button("Entrar");
        loginButton.setMaxWidth(300);
        loginButton.setStyle("""
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-background-radius: 5;
            """);

        Button cadastrarButton = new Button("Criar nova conta");
        cadastrarButton.setMaxWidth(300);
        cadastrarButton.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #3498db;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-border-color: #3498db;
            -fx-border-radius: 5;
            """);

        // Mensagem de erro
        Label mensagemErro = new Label();
        mensagemErro.setStyle("-fx-text-fill: #e74c3c;");
        mensagemErro.setVisible(false);

        formContainer.getChildren().addAll(
            titulo,
            cpfField,
            senhaField,
            mensagemErro,
            loginButton,
            cadastrarButton
        );

        root.getChildren().add(formContainer);

        // Ações dos botões
        loginButton.setOnAction(e -> {
            String cpf = cpfField.getText();
            String senha = senhaField.getText();

            if (cpf.isEmpty() || senha.isEmpty()) {
                mensagemErro.setText("Por favor, preencha todos os campos");
                mensagemErro.setVisible(true);
                return;
            }

            teatro.autenticarUsuario(cpf, senha).ifPresentOrElse(
                usuario -> {
                    mensagemErro.setVisible(false);
                    new DashboardView(teatro, usuario, stage).show();
                },
                () -> {
                    mensagemErro.setText("CPF ou senha inválidos");
                    mensagemErro.setVisible(true);
                }
            );
        });

        cadastrarButton.setOnAction(e -> mostrarTelaCadastro());

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void mostrarTelaCadastro() {
        Stage cadastroStage = new Stage();
        cadastroStage.setTitle("Cadastro de Usuário");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #ecf0f1;");

        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(400);
        formContainer.setStyle("""
            -fx-background-color: white;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            -fx-background-radius: 5;
            """);

        Label titulo = new Label("Cadastro");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome");
        TextField cpfField = new TextField();
        cpfField.setPromptText("CPF");
        TextField enderecoField = new TextField();
        enderecoField.setPromptText("Endereço");
        TextField telefoneField = new TextField();
        telefoneField.setPromptText("Telefone");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Senha");

        String fieldStyle = """
            -fx-padding: 10;
            -fx-background-radius: 5;
            -fx-max-width: 300;
            """;

        nomeField.setStyle(fieldStyle);
        cpfField.setStyle(fieldStyle);
        enderecoField.setStyle(fieldStyle);
        telefoneField.setStyle(fieldStyle);
        emailField.setStyle(fieldStyle);
        senhaField.setStyle(fieldStyle);

        Button salvarButton = new Button("Salvar");
        salvarButton.setMaxWidth(300);
        salvarButton.setStyle("""
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-padding: 10 20;
            -fx-font-size: 14;
            -fx-cursor: hand;
            -fx-background-radius: 5;
            """);

        Label mensagemErro = new Label();
        mensagemErro.setStyle("-fx-text-fill: #e74c3c;");
        mensagemErro.setVisible(false);

        formContainer.getChildren().addAll(
            titulo,
            nomeField,
            cpfField,
            enderecoField,
            telefoneField,
            emailField,
            senhaField,
            mensagemErro,
            salvarButton
        );

        root.getChildren().add(formContainer);

        salvarButton.setOnAction(e -> {
            // Validação dos campos
            if (nomeField.getText().isEmpty() || cpfField.getText().isEmpty() ||
                enderecoField.getText().isEmpty() || telefoneField.getText().isEmpty() ||
                emailField.getText().isEmpty() || senhaField.getText().isEmpty()) {
                mensagemErro.setText("Por favor, preencha todos os campos");
                mensagemErro.setVisible(true);
                return;
            }

            Usuario novoUsuario = new Usuario(
                nomeField.getText(),
                cpfField.getText(),
                enderecoField.getText(),
                telefoneField.getText(),
                emailField.getText(),
                senhaField.getText()
            );

            if (!novoUsuario.validarCPF()) {
                mensagemErro.setText("CPF inválido");
                mensagemErro.setVisible(true);
                return;
            }

            if (teatro.cadastrarUsuario(novoUsuario)) {
                cadastroStage.close();
            } else {
                mensagemErro.setText("CPF já cadastrado");
                mensagemErro.setVisible(true);
            }
        });

        Scene scene = new Scene(root, 800, 800);
        cadastroStage.setScene(scene);
        cadastroStage.show();
    }
} 