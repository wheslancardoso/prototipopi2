package com.teatro;

import com.teatro.database.DatabaseConnection;
import com.teatro.model.Teatro;
import com.teatro.view.LoginView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Classe principal da aplicação que inicia o sistema de teatro.
 */
public class Main extends Application {
    private Teatro teatro;

    public static void main(String[] args) {
        try {
            // Verifica se o JavaFX está disponível
            Class.forName("javafx.application.Application");
            launch(args);
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Os componentes de runtime do JavaFX estão faltando.\n" +
                    "Execute o programa com: mvn javafx:run");
        }
    }

    @Override
    public void init() {
        // Inicializa o sistema
        teatro = Teatro.getInstance();
        
        // Verifica a conexão com o banco de dados
        try {
            DatabaseConnection.getInstance().getConnection();
            System.out.println("Conexão com o banco de dados estabelecida com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            Platform.exit();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Configura o palco principal
        primaryStage.setTitle("Sistema de Teatro");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        
        // Inicia com a tela de login modernizada
        new LoginView(teatro, primaryStage).show();
    }

}