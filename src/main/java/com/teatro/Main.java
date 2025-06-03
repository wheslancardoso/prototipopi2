package com.teatro;

import com.teatro.database.DatabaseConnection;
import com.teatro.model.Teatro;
import com.teatro.view.LoginViewModerna;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Classe principal da aplicação que inicia o sistema de teatro.
 */
public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String APP_TITLE = "Sistema de Teatro";
    private static final int MIN_WINDOW_WIDTH = 1024;
    private static final int MIN_WINDOW_HEIGHT = 768;
    
    private Teatro teatro;

    public static void main(String[] args) {
        try {
            // Verifica se o JavaFX está disponível
            Class.forName("javafx.application.Application");
            launch(args);
        } catch (ClassNotFoundException e) {
            logger.error("Erro: Os componentes de runtime do JavaFX estão faltando.\n" +
                       "Execute o programa com: mvn javafx:run", e);
            System.exit(1);
        } catch (Exception e) {
            logger.error("Erro inesperado ao iniciar a aplicação", e);
            System.exit(1);
        }
    }

    @Override
    public void init() {
        try {
            // Inicializa o objeto Teatro, que é o controlador principal
            teatro = new Teatro();
            
            // Verifica a conexão com o banco de dados
            DatabaseConnection.getConnection();
            logger.info("Conexão com o banco de dados estabelecida com sucesso.");
            
            // Executa o script de atualização do banco de dados, se necessário
            try {
                boolean atualizacaoBemSucedida = com.teatro.database.DatabaseUpdateRunner.updateDatabase();
                if (atualizacaoBemSucedida) {
                    logger.info("Atualizações do banco de dados verificadas com sucesso.");
                } else {
                    logger.warn("Falha ao atualizar o banco de dados. Verifique os logs para mais detalhes.");
                }
            } catch (Exception e) {
                logger.error("Erro inesperado ao atualizar o banco de dados", e);
                // Continua a execução mesmo se a atualização falhar
            }
            
        } catch (RuntimeException e) {
            logger.error("Erro ao conectar ao banco de dados: {}", e.getMessage(), e);
            Platform.exit();
        } catch (Exception e) {
            logger.error("Erro inesperado ao inicializar a aplicação", e);
            Platform.exit();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        if (teatro == null) {
            logger.error("Falha ao inicializar o controlador principal");
            Platform.exit();
            return;
        }
        
        try {
            // Configura o palco principal
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
            primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
            
            // Inicia com a tela de login modernizada
            new LoginViewModerna(teatro, primaryStage).show();
            logger.info("Aplicação iniciada com sucesso");
            
        } catch (Exception e) {
            logger.error("Erro ao iniciar a interface gráfica", e);
            showErrorAndExit("Erro ao iniciar a aplicação", e.getMessage());
        }
    }
    
    /**
     * Exibe uma mensagem de erro e encerra a aplicação.
     * 
     * @param titulo Título da mensagem de erro
     * @param mensagem Mensagem de erro detalhada
     */
    private void showErrorAndExit(String titulo, String mensagem) {
        logger.error("{}: {}", titulo, mensagem);
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Erro Fatal");
            alert.setHeaderText(titulo);
            alert.setContentText(mensagem);
            alert.showAndWait();
            Platform.exit();
        });
    }
}