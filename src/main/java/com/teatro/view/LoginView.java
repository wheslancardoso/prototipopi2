package com.teatro.view;

import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import javafx.geometry.Insets;
import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.teatro.model.factory.UsuarioFactory;
import com.teatro.model.factory.UsuarioComumFactory;

/**
 * Versão modernizada da tela de login.
 */
public class LoginView {
    private Teatro teatro;
    private Stage stage;
    
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;
    
    // Cores do tema
    private static final String PRIMARY_COLOR = "#3498db";

    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String CARD_BACKGROUND = "white";

    public LoginView(Teatro teatro, Stage stage) {
        this.teatro = teatro;
        this.stage = stage;
    }
    
    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("Sistema de Teatro - Login");

        // Container principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Container central
        VBox centerContainer = new VBox();
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.setPadding(new Insets(50));
        
        // Card de login
        VBox loginCard = createLoginCard();
        
        centerContainer.getChildren().add(loginCard);
        root.setCenter(centerContainer);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show(); // Adicionando para exibir a janela
    }
    
    private VBox createLoginCard() {
        VBox card = new VBox(25);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(450);
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" +
            "-fx-background-radius: 8;"
        );
        
        // Título do sistema
        Label titulo = new Label("Ingresso Fácil");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.web(PRIMARY_COLOR));
        
        // Subtítulo
        Label subtitle = new Label("Faça login para continuar");
        subtitle.setFont(Font.font("System", 16));
        subtitle.setTextFill(Color.web(TEXT_COLOR));
        
        // Separador
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        // Formulário de login
        VBox loginForm = new VBox(15);
        loginForm.setAlignment(Pos.CENTER);
        
        // Campo de CPF/Email
        VBox identificadorBox = new VBox(5);
        Label identificadorLabel = new Label("CPF ou Email");
        identificadorLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TextField identificadorField = new TextField();
        identificadorField.setPromptText("Digite seu CPF ou email");
        identificadorField.setPrefHeight(40);
        identificadorField.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 4;
            -fx-padding: 8;
            """);
        
        identificadorBox.getChildren().addAll(identificadorLabel, identificadorField);
        
        // Campo de senha
        VBox senhaBox = new VBox(5);
        Label senhaLabel = new Label("Senha");
        senhaLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Digite sua senha");
        senhaField.setPrefHeight(40);
        senhaField.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 4;
            -fx-padding: 8;
            """);
        
        senhaBox.getChildren().addAll(senhaLabel, senhaField);
        
        // Botão de login
        Button loginButton = new Button("Entrar");
        loginButton.setPrefHeight(40);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 4;" +
            "-fx-cursor: hand;"
        );
        
        // Botão de esqueceu a senha
        Button esqueceuSenhaButton = new Button("Esqueceu a senha?");
        esqueceuSenhaButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";" +
            "-fx-cursor: hand;"
        );
        
        // Botão de cadastro
        Button cadastrarButton = new Button("Criar nova conta");
        cadastrarButton.setPrefHeight(40);
        cadastrarButton.setPrefWidth(200);
        cadastrarButton.setFont(Font.font("System", 13));
        cadastrarButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + PRIMARY_COLOR + ";" +
            "-fx-border-color: " + PRIMARY_COLOR + ";" +
            "-fx-border-radius: 4;" +
            "-fx-border-width: 1px;" +
            "-fx-cursor: hand;"
        );
        
        // Mensagem de erro
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#e74c3c"));
        errorLabel.setVisible(false);
        
        // Ação do botão de cadastro
        cadastrarButton.setOnAction(e -> mostrarTelaCadastro());
        
        // Ação do botão de esqueceu a senha
        esqueceuSenhaButton.setOnAction(e -> mostrarTelaRecuperacaoSenha());
        
        loginButton.setOnAction(e -> {
            String identificador = identificadorField.getText();
            String senha = senhaField.getText();
            
            if (identificador.isEmpty() || senha.isEmpty()) {
                errorLabel.setText("Por favor, preencha todos os campos.");
                errorLabel.setVisible(true);
                return;
            }
            
            // Inicializa o teatro se necessário
            if (teatro == null) {
                teatro = Teatro.getInstance();
            }
            
            // Tenta autenticar o usuário
            Optional<Usuario> usuarioOpt = teatro.autenticarUsuario(identificador, senha);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                errorLabel.setVisible(false);
                
                // Redireciona para a tela apropriada com base no tipo de usuário
                if ("ADMIN".equals(usuario.getTipoUsuario())) {
                    new DashboardView(teatro, usuario, stage).show();
                } else {
                    new SessoesView(teatro, usuario, stage).show();
                }
            } else {
                errorLabel.setText("CPF/Email ou senha incorretos.");
                errorLabel.setVisible(true);
            }
        });
        
        loginForm.getChildren().addAll(identificadorBox, senhaBox, loginButton, esqueceuSenhaButton, cadastrarButton, errorLabel);
        
        card.getChildren().addAll(titulo, subtitle, separator, loginForm);
        return card;
    }
    
    private void mostrarTelaCadastro() {
        Stage cadastroStage = new Stage();
        cadastroStage.setTitle("Cadastro de Usuário");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(500);
        formContainer.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" +
            "-fx-background-radius: 8;"
        );

        Label titulo = new Label("Cadastro de Usuário");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web(PRIMARY_COLOR));
        
        Label subtitulo = new Label("Preencha os dados para criar sua conta");
        subtitulo.setFont(Font.font("System", 14));
        subtitulo.setTextFill(Color.web(TEXT_COLOR));
        
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 20, 0));

        // Campos do formulário
        VBox formFields = new VBox(15);
        formFields.setAlignment(Pos.CENTER);
        
        // Criar campos manualmente (sem função aninhada)
        // Campo Nome
        VBox nomeBox = new VBox(5);
        Label nomeLabel = new Label("Nome Completo");
        nomeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField nomeField = new TextField();
        nomeField.setPromptText("Digite seu nome completo");
        nomeField.setPrefHeight(40);
        nomeField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;" +
            "-fx-pref-width: 300;"
        );
        nomeBox.getChildren().addAll(nomeLabel, nomeField);
        formFields.getChildren().add(nomeBox);
        
        // Campo CPF
        VBox cpfBox = new VBox(5);
        Label cpfLabel = new Label("CPF");
        cpfLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField cpfField = new TextField();
        cpfField.setPromptText("Digite seu CPF (apenas números)");
        cpfField.setPrefHeight(40);
        cpfField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;" +
            "-fx-pref-width: 300;"
        );
        cpfBox.getChildren().addAll(cpfLabel, cpfField);
        formFields.getChildren().add(cpfBox);
        
        // Campo Endereço
        VBox enderecoBox = new VBox(5);
        Label enderecoLabel = new Label("Endereço");
        enderecoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField enderecoField = new TextField();
        enderecoField.setPromptText("Digite seu endereço");
        enderecoField.setPrefHeight(40);
        enderecoField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;" +
            "-fx-pref-width: 300;"
        );
        enderecoBox.getChildren().addAll(enderecoLabel, enderecoField);
        formFields.getChildren().add(enderecoBox);
        
        // Campo Telefone
        VBox telefoneBox = new VBox(5);
        Label telefoneLabel = new Label("Telefone");
        telefoneLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField telefoneField = new TextField();
        telefoneField.setPromptText("Digite seu telefone");
        telefoneField.setPrefHeight(40);
        telefoneField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;" +
            "-fx-pref-width: 300;"
        );
        telefoneBox.getChildren().addAll(telefoneLabel, telefoneField);
        formFields.getChildren().add(telefoneBox);
        
        // Campo E-mail
        VBox emailBox = new VBox(5);
        Label emailLabel = new Label("E-mail");
        emailLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField emailField = new TextField();
        emailField.setPromptText("Digite seu e-mail");
        emailField.setPrefHeight(40);
        emailField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;" +
            "-fx-pref-width: 300;"
        );
        emailBox.getChildren().addAll(emailLabel, emailField);
        formFields.getChildren().add(emailBox);
        
        // Campo Senha
        VBox senhaBox = new VBox(5);
        Label senhaLabel = new Label("Senha");
        senhaLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Crie uma senha segura");
        senhaField.setPrefHeight(40);
        senhaField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;" +
            "-fx-pref-width: 300;"
        );
        senhaBox.getChildren().addAll(senhaLabel, senhaField);
        formFields.getChildren().add(senhaBox);
        
        // Botão de cadastrar
        Button cadastrarButton = new Button("Criar Conta");
        cadastrarButton.setPrefHeight(45);
        cadastrarButton.setPrefWidth(200);
        cadastrarButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        cadastrarButton.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 4;" +
            "-fx-cursor: hand;"
        );
        
        // Mensagem de erro
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#e74c3c"));
        errorLabel.setVisible(false);
        
        // Layout
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(titulo, subtitulo, separator, formFields, cadastrarButton, errorLabel);
        
        formContainer.getChildren().add(content);
        root.getChildren().add(formContainer);
        
        // Ação do botão de cadastrar
        cadastrarButton.setOnAction(e -> {
            // Validação dos campos
            if (nomeField.getText().isEmpty() || cpfField.getText().isEmpty() ||
                enderecoField.getText().isEmpty() || telefoneField.getText().isEmpty() ||
                emailField.getText().isEmpty() || senhaField.getText().isEmpty()) {
                errorLabel.setText("Por favor, preencha todos os campos");
                errorLabel.setVisible(true);
                return;
            }

            // Substituir criação direta por Factory Method
            UsuarioFactory factory = new UsuarioComumFactory();
            Usuario novoUsuario = factory.criarUsuario(
                nomeField.getText(),
                cpfField.getText(),
                emailField.getText(),
                senhaField.getText(),
                enderecoField.getText(),
                telefoneField.getText()
            );

            if (!novoUsuario.validarCPF()) {
                errorLabel.setText("CPF inválido");
                errorLabel.setVisible(true);
                return;
            }

            if (teatro.cadastrarUsuario(novoUsuario)) {
                cadastroStage.close();
                errorLabel.setText("");
                errorLabel.setVisible(false);
                
                // Mostrar mensagem de sucesso
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Cadastro realizado");
                alert.setHeaderText(null);
                alert.setContentText("Cadastro realizado com sucesso! Agora você pode fazer login.");
                alert.showAndWait();
            } else {
                errorLabel.setText("CPF já cadastrado");
                errorLabel.setVisible(true);
            }
        });

        Scene scene = new Scene(root, 800, 800);
        cadastroStage.setScene(scene);
        cadastroStage.show();
    }

    private void mostrarTelaRecuperacaoSenha() {
        Stage recuperacaoStage = new Stage();
        recuperacaoStage.setTitle("Recuperação de Senha");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Container do formulário
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        formContainer.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + ";" +
            "-fx-padding: 30;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        // Título
        Label titulo = new Label("Recuperação de Senha");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web(PRIMARY_COLOR));

        // Subtítulo
        Label subtitulo = new Label("Digite seu CPF e email para recuperar sua senha");
        subtitulo.setFont(Font.font("System", 14));
        subtitulo.setTextFill(Color.web(TEXT_COLOR));

        // Separador
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        // Campos do formulário
        VBox formFields = new VBox(15);
        formFields.setAlignment(Pos.CENTER);

        // Campo CPF
        VBox cpfBox = new VBox(5);
        Label cpfLabel = new Label("CPF");
        cpfLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField cpfField = new TextField();
        cpfField.setPromptText("Digite seu CPF");
        cpfField.setPrefHeight(40);
        cpfField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;"
        );
        cpfBox.getChildren().addAll(cpfLabel, cpfField);

        // Campo Email
        VBox emailBox = new VBox(5);
        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField emailField = new TextField();
        emailField.setPromptText("Digite seu email");
        emailField.setPrefHeight(40);
        emailField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;"
        );
        emailBox.getChildren().addAll(emailLabel, emailField);

        // Campo Nova Senha
        VBox novaSenhaBox = new VBox(5);
        Label novaSenhaLabel = new Label("Nova Senha");
        novaSenhaLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        PasswordField novaSenhaField = new PasswordField();
        novaSenhaField.setPromptText("Digite sua nova senha");
        novaSenhaField.setPrefHeight(40);
        novaSenhaField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 4;" +
            "-fx-padding: 8;"
        );
        novaSenhaBox.getChildren().addAll(novaSenhaLabel, novaSenhaField);

        formFields.getChildren().addAll(cpfBox, emailBox, novaSenhaBox);

        // Botão de recuperar
        Button recuperarButton = new Button("Recuperar Senha");
        recuperarButton.setPrefHeight(40);
        recuperarButton.setMaxWidth(Double.MAX_VALUE);
        recuperarButton.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 4;" +
            "-fx-cursor: hand;"
        );

        // Mensagem de erro
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#e74c3c"));
        errorLabel.setVisible(false);

        // Layout
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(titulo, subtitulo, separator, formFields, recuperarButton, errorLabel);

        formContainer.getChildren().add(content);
        root.getChildren().add(formContainer);

        // Ação do botão de recuperar
        recuperarButton.setOnAction(e -> {
            String cpf = cpfField.getText();
            String email = emailField.getText();
            String novaSenha = novaSenhaField.getText();

            if (cpf.isEmpty() || email.isEmpty() || novaSenha.isEmpty()) {
                errorLabel.setText("Por favor, preencha todos os campos");
                errorLabel.setVisible(true);
                return;
            }

            try {
                // Inicializa o teatro se necessário
                if (teatro == null) {
                    teatro = Teatro.getInstance();
                }

                // Verifica se o usuário existe
                Optional<Usuario> usuarioOpt = teatro.verificarUsuarioParaRecuperacao(cpf, email);
                
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    teatro.recuperarSenha(usuario.getId(), novaSenha);
                    
                    // Mostra mensagem de sucesso
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Senha Recuperada");
                    alert.setHeaderText(null);
                    alert.setContentText("Sua senha foi atualizada com sucesso!");
                    alert.showAndWait();
                    
                    recuperacaoStage.close();
                } else {
                    errorLabel.setText("CPF ou email não encontrados");
                    errorLabel.setVisible(true);
                }
            } catch (Exception ex) {
                errorLabel.setText("Erro ao recuperar senha: " + ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        Scene scene = new Scene(root, 500, 600);
        recuperacaoStage.setScene(scene);
        recuperacaoStage.show();
    }
}
