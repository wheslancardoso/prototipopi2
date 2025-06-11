# Instruções para Implementação das Views FXML

## 1. Estrutura de Diretórios a Criar

```
src/main/
├── java/com/teatro/view/
│   ├── controllers/
│   │   ├── LoginController.java ✓
│   │   ├── CadastroController.java ✓
│   │   ├── RecuperacaoSenhaController.java (criar)
│   │   ├── DashboardController.java (criar)
│   │   ├── SessoesController.java ✓
│   │   ├── CompraIngressoController.java (criar)
│   │   ├── SelecionarPoltronaController.java (criar)
│   │   └── ImpressaoIngressoController.java (criar)
│   └── util/
│       ├── SceneManager.java ✓
│       └── ValidationUtils.java ✓
└── resources/com/teatro/view/
    ├── fxml/
    │   ├── login.fxml ✓
    │   ├── cadastro.fxml ✓
    │   ├── recuperacao-senha.fxml ✓
    │   ├── dashboard.fxml ✓
    │   ├── sessoes.fxml ✓
    │   ├── compra-ingresso.fxml (criar)
    │   ├── selecionar-poltrona.fxml (criar)
    │   ├── impressao-ingresso.fxml (criar)
    │   └── EventoItem.fxml (já existe)
    └── css/
        ├── login.css ✓
        ├── cadastro.css ✓
        ├── recuperacao-senha.css (criar)
        ├── dashboard.css ✓
        ├── sessoes.css ✓
        ├── compra-ingresso.css (criar)
        ├── selecionar-poltrona.css (criar)
        └── impressao-ingresso.css (criar)
```

## 2. Atualizar Main.java

Substitua o conteúdo da classe Main.java por:

```java
package com.teatro;

import com.teatro.view.util.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            Class.forName("javafx.application.Application");
            launch(args);
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Os componentes de runtime do JavaFX estão faltando.");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Configurar stage principal
            primaryStage.setTitle("Sistema de Teatro");
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(768);

            // Inicializar SceneManager
            SceneManager sceneManager = SceneManager.getInstance();
            sceneManager.setStage(primaryStage);

            // Carregar tela de login
            sceneManager.loadScene("/com/teatro/view/fxml/login.fxml",
                                 "Sistema de Teatro - Login");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar aplicação: " + e.getMessage());
            Platform.exit();
        }
    }
}
```

## 3. Controllers Faltantes a Criar

### 3.1. RecuperacaoSenhaController.java

```java
package com.teatro.view.controllers;

import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import com.teatro.view.util.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecuperacaoSenhaController implements Initializable {

    @FXML private TextField recuperacao_txtCpf;
    @FXML private TextField recuperacao_txtEmail;
    @FXML private PasswordField recuperacao_txtNovaSenha;
    @FXML private PasswordField recuperacao_txtConfirmarSenha;
    @FXML private Button recuperacao_btnRecuperar;
    @FXML private Button recuperacao_btnCancelar;
    @FXML private Label recuperacao_lblErro;
    @FXML private Label recuperacao_lblSucesso;

    private Teatro teatro;
    private Stage currentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teatro = Teatro.getInstance();

        // Aplicar máscaras
        ValidationUtils.addCpfMask(recuperacao_txtCpf);
        ValidationUtils.addEmailValidation(recuperacao_txtEmail);

        // Ocultar mensagens
        recuperacao_lblErro.setVisible(false);
        recuperacao_lblSucesso.setVisible(false);
    }

    public void setStage(Stage stage) {
        this.currentStage = stage;
    }

    @FXML
    private void handleRecuperar() {
        if (!validarCampos()) return;

        try {
            Optional<Usuario> usuarioOpt = teatro.verificarUsuarioParaRecuperacao(
                recuperacao_txtCpf.getText().trim(),
                recuperacao_txtEmail.getText().trim()
            );

            if (usuarioOpt.isPresent()) {
                teatro.recuperarSenha(usuarioOpt.get().getId(),
                                    recuperacao_txtNovaSenha.getText());

                recuperacao_lblSucesso.setText("Senha atualizada com sucesso!");
                recuperacao_lblSucesso.setVisible(true);
                recuperacao_lblErro.setVisible(false);

                // Fechar após 2 segundos
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            if (currentStage != null) currentStage.close();
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } else {
                showError("CPF ou email não encontrados.");
            }
        } catch (Exception e) {
            showError("Erro: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        if (currentStage != null) currentStage.close();
    }

    private boolean validarCampos() {
        if (recuperacao_txtCpf.getText().trim().isEmpty() ||
            recuperacao_txtEmail.getText().trim().isEmpty() ||
            recuperacao_txtNovaSenha.getText().isEmpty() ||
            recuperacao_txtConfirmarSenha.getText().isEmpty()) {
            showError("Todos os campos são obrigatórios.");
            return false;
        }

        if (!ValidationUtils.isValidCpf(recuperacao_txtCpf.getText())) {
            showError("CPF inválido.");
            return false;
        }

        if (!ValidationUtils.isValidEmail(recuperacao_txtEmail.getText())) {
            showError("Email inválido.");
            return false;
        }

        if (!recuperacao_txtNovaSenha.getText().equals(recuperacao_txtConfirmarSenha.getText())) {
            showError("Senhas não coincidem.");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        recuperacao_lblErro.setText(message);
        recuperacao_lblErro.setVisible(true);
        recuperacao_lblSucesso.setVisible(false);
    }
}
```

### 3.2. DashboardController.java

```java
package com.teatro.view.controllers;

import com.teatro.model.Teatro;
import com.teatro.model.Usuario;
import com.teatro.view.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label dashboard_lblUsuario;
    @FXML private VBox dashboard_containerConteudo;
    @FXML private VBox dashboard_containerUsuario;
    @FXML private VBox dashboard_containerAdmin;
    @FXML private Label dashboard_lblBoasVindas;
    @FXML private GridPane dashboard_gridVendas;
    @FXML private GridPane dashboard_gridOcupacao;
    @FXML private GridPane dashboard_gridFaturamento;
    @FXML private TableView dashboard_tblLucroMedio;

    private Teatro teatro;
    private Usuario usuario;
    private SceneManager sceneManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sceneManager = SceneManager.getInstance();
        teatro = sceneManager.getTeatro();
        usuario = sceneManager.getUsuarioLogado();

        if (usuario != null) {
            dashboard_lblUsuario.setText(usuario.getNome());
            configurarInterface();
        }
    }

    private void configurarInterface() {
        if ("ADMIN".equals(usuario.getTipoUsuario())) {
            mostrarInterfaceAdmin();
        } else {
            mostrarInterfaceUsuario();
        }
    }

    private void mostrarInterfaceUsuario() {
        dashboard_lblBoasVindas.setText("Bem-vindo(a), " + usuario.getNome() + "!");
        dashboard_containerUsuario.setVisible(true);
        dashboard_containerUsuario.setManaged(true);
        dashboard_containerAdmin.setVisible(false);
        dashboard_containerAdmin.setManaged(false);
    }

    private void mostrarInterfaceAdmin() {
        dashboard_containerAdmin.setVisible(true);
        dashboard_containerAdmin.setManaged(true);
        dashboard_containerUsuario.setVisible(false);
        dashboard_containerUsuario.setManaged(false);

        // Carregar estatísticas
        carregarEstatisticas();
    }

    private void carregarEstatisticas() {
        // Implementar carregamento de estatísticas
        // Por enquanto, dados fictícios para demonstração

        // Grid de vendas
        adicionarEstatistica(dashboard_gridVendas, 0, "Peça Mais Vendida:", "N/A (0 ingressos)");
        adicionarEstatistica(dashboard_gridVendas, 1, "Peça Menos Vendida:", "N/A (0 ingressos)");

        // Grid de ocupação
        adicionarEstatistica(dashboard_gridOcupacao, 0, "Maior Ocupação:", "N/A (0%)");
        adicionarEstatistica(dashboard_gridOcupacao, 1, "Menor Ocupação:", "N/A (0%)");

        // Grid de faturamento
        adicionarEstatistica(dashboard_gridFaturamento, 0, "Mais Lucrativa:", "N/A (R$ 0,00)");
        adicionarEstatistica(dashboard_gridFaturamento, 1, "Menos Lucrativa:", "N/A (R$ 0,00)");
    }

    private void adicionarEstatistica(GridPane grid, int row, String titulo, String valor) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("stat-title");

        Label lblValor = new Label(valor);
        lblValor.getStyleClass().add("stat-value");

        grid.add(lblTitulo, 0, row);
        grid.add(lblValor, 1, row);
    }

    @FXML
    private void handleSair() {
        sceneManager.goToLogin();
    }

    @FXML
    private void handleComprarIngresso() {
        sceneManager.goToSessoes();
    }

    @FXML
    private void handleImprimirIngresso() {
        try {
            sceneManager.loadScene("/com/teatro/view/fxml/impressao-ingresso.fxml",
                                 "Sistema de Teatro - Impressão de Ingressos");
        } catch (Exception e) {
            mostrarErro("Erro ao abrir tela de impressão: " + e.getMessage());
        }
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
```

## 4. CSS Faltante a Criar

### 4.1. recuperacao-senha.css

```css
/* Usar como base o cadastro.css, adaptando as cores */
@import url("cadastro.css");

/* Personalizar conforme necessário */
.recuperacao-card {
    -fx-background-color: -fx-card-background;
    -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);
    -fx-background-radius: 8;
}
```

## 5. Atualizações no module-info.java

Adicione as seguintes linhas se necessário:

```java
exports com.teatro.view.controllers;
exports com.teatro.view.util;

opens com.teatro.view.controllers to javafx.fxml;
```

## 6. Passos para Implementação

1. **Criar estrutura de diretórios** conforme mostrado acima
2. **Mover arquivos FXML** para `/src/main/resources/com/teatro/view/fxml/`
3. **Mover arquivos CSS** para `/src/main/resources/com/teatro/view/css/`
4. **Criar controllers** em `/src/main/java/com/teatro/view/controllers/`
5. **Criar utilitários** em `/src/main/java/com/teatro/view/util/`
6. **Atualizar Main.java** conforme mostrado
7. **Remover ou depreciar** as antigas classes View do pacote view
8. **Testar** cada tela individualmente

## 7. Ordem de Implementação Recomendada

1. ✅ LoginController + login.fxml + login.css
2. ✅ CadastroController + cadastro.fxml + cadastro.css
3. ⭕ RecuperacaoSenhaController + recuperacao-senha.fxml + recuperacao-senha.css
4. ⭕ DashboardController + dashboard.fxml + dashboard.css
5. ✅ SessoesController + sessoes.fxml + sessoes.css
6. ⭕ CompraIngressoController + compra-ingresso.fxml + compra-ingresso.css
7. ⭕ SelecionarPoltronaController + selecionar-poltrona.fxml + selecionar-poltrona.css
8. ⭕ ImpressaoIngressoController + impressao-ingresso.fxml + impressao-ingresso.css

## 8. Vantagens da Nova Arquitetura

-   **Separação clara** entre apresentação (FXML), estilo (CSS) e lógica (Controller)
-   **Reutilização** de componentes e estilos
-   **Manutenibilidade** melhorada
-   **Design responsivo** com CSS
-   **Navegação centralizada** com SceneManager
-   **Validações padronizadas** com ValidationUtils
-   **Injeção de dependências** automática

## 9. Debugging e Troubleshooting

-   Verificar caminhos dos recursos FXML e CSS
-   Confirmar que fx:controller aponta para a classe correta
-   Validar que todos os fx:id têm @FXML correspondente
-   Testar navegação entre telas
-   Verificar se as máscaras e validações funcionam
-   Confirmar que os estilos CSS são aplicados corretamente

Esta arquitetura FXML proporciona uma base sólida e escalável para o sistema de teatro!
