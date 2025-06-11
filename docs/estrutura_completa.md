# Estrutura Completa do Projeto Teatro - FXML

## 📁 Estrutura de Diretórios

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── teatro/
│   │           ├── Main.java (atualizar)
│   │           ├── controller/
│   │           │   └── SessaoController.java
│   │           ├── dao/
│   │           │   ├── DAO.java
│   │           │   ├── UsuarioDAO.java
│   │           │   ├── UsuarioDAOImpl.java
│   │           │   ├── IngressoDAO.java
│   │           │   ├── AreaDAO.java
│   │           │   ├── SessaoDAO.java
│   │           │   └── EventoDAO.java
│   │           ├── database/
│   │           │   ├── DatabaseConnection.java
│   │           │   ├── DatabaseConfig.java
│   │           │   └── ViewsEstatisticas.java
│   │           ├── exception/
│   │           │   ├── TeatroException.java
│   │           │   ├── IngressoException.java
│   │           │   ├── AreaException.java
│   │           │   ├── PoltronaOcupadaException.java
│   │           │   ├── UsuarioNaoEncontradoException.java
│   │           │   └── SessaoNaoEncontradaException.java
│   │           ├── model/
│   │           │   ├── Teatro.java
│   │           │   ├── Usuario.java
│   │           │   ├── Evento.java
│   │           │   ├── Sessao.java
│   │           │   ├── Area.java
│   │           │   ├── Poltrona.java
│   │           │   ├── TipoSessao.java
│   │           │   ├── Ingresso.java
│   │           │   ├── IngressoModerno.java
│   │           │   ├── builder/
│   │           │   │   ├── AbstractBuilder.java
│   │           │   │   ├── UsuarioBuilder.java
│   │           │   │   └── IngressoBuilder.java
│   │           │   ├── factory/
│   │           │   │   ├── UsuarioFactory.java
│   │           │   │   ├── UsuarioComumFactory.java
│   │           │   │   └── UsuarioAdminFactory.java
│   │           │   └── state/
│   │           │       ├── PoltronaState.java
│   │           │       ├── DisponivelState.java
│   │           │       ├── OcupadaState.java
│   │           │       └── SelecionadaState.java
│   │           ├── observer/
│   │           │   ├── Observer.java
│   │           │   ├── Subject.java
│   │           │   ├── AbstractSubject.java
│   │           │   ├── NotificacaoSubject.java
│   │           │   ├── NotificacaoVenda.java
│   │           │   ├── VendaObserver.java (vazio)
│   │           │   └── VendaLoggerObserver.java
│   │           ├── service/
│   │           │   ├── Service.java
│   │           │   ├── AbstractService.java
│   │           │   ├── UsuarioService.java
│   │           │   └── IngressoService.java
│   │           ├── util/
│   │           │   ├── TeatroLogger.java
│   │           │   └── Validator.java
│   │           └── view/
│   │               ├── controllers/ (NOVO)
│   │               │   ├── LoginController.java ✅
│   │               │   ├── CadastroController.java ✅
│   │               │   ├── RecuperacaoSenhaController.java (criar)
│   │               │   ├── DashboardController.java (criar)
│   │               │   ├── SessoesController.java ✅
│   │               │   ├── CompraIngressoController.java (criar)
│   │               │   ├── SelecionarPoltronaController.java (criar)
│   │               │   ├── ImpressaoIngressoController.java (criar)
│   │               │   └── EventoItemController.java (refatorar existente)
│   │               ├── util/ (NOVO)
│   │               │   ├── SceneManager.java ✅
│   │               │   └── ValidationUtils.java ✅
│   │               ├── LoginView.java (DEPRECAR)
│   │               ├── SessoesView.java (DEPRECAR)
│   │               ├── DashboardView.java (DEPRECAR)
│   │               ├── CompraIngressoView.java (DEPRECAR)
│   │               ├── SelecionarPoltronaView.java (DEPRECAR)
│   │               └── ImpressaoIngressoView.java (DEPRECAR)
│   └── resources/
│       ├── com/
│       │   └── teatro/
│       │       └── view/
│       │           ├── fxml/ (NOVO)
│       │           │   ├── login.fxml ✅
│       │           │   ├── cadastro.fxml ✅
│       │           │   ├── recuperacao-senha.fxml ✅
│       │           │   ├── dashboard.fxml ✅
│       │           │   ├── sessoes.fxml ✅
│       │           │   ├── compra-ingresso.fxml (criar)
│       │           │   ├── selecionar-poltrona.fxml (criar)
│       │           │   ├── impressao-ingresso.fxml (criar)
│       │           │   └── EventoItem.fxml (já existe)
│       │           └── css/ (NOVO)
│       │               ├── login.css ✅
│       │               ├── cadastro.css ✅
│       │               ├── recuperacao-senha.css (criar)
│       │               ├── dashboard.css ✅
│       │               ├── sessoes.css ✅
│       │               ├── compra-ingresso.css (criar)
│       │               ├── selecionar-poltrona.css (criar)
│       │               └── impressao-ingresso.css (criar)
│       ├── database.properties
│       ├── database.sql
│       ├── db/
│       │   └── views_estatisticas.sql
│       └── posters/
│           ├── hamletposter.jpg
│           ├── compadecidaposter.jpg
│           ├── ofantasmadaoperaposter.jpg
│           └── default.jpg
└── module-info.java
```

## 🎯 Arquivos Criados e Status

### ✅ Concluídos

1. **LoginController.java** - Controller para autenticação
2. **CadastroController.java** - Controller para registro de usuários
3. **SessoesController.java** - Controller para listagem de sessões
4. **SceneManager.java** - Gerenciador de navegação entre telas
5. **ValidationUtils.java** - Utilitários para validação e máscaras
6. **login.fxml** - Layout da tela de login
7. **cadastro.fxml** - Layout da tela de cadastro
8. **recuperacao-senha.fxml** - Layout da recuperação de senha
9. **dashboard.fxml** - Layout do dashboard
10. **sessoes.fxml** - Layout da listagem de sessões
11. **login.css** - Estilos da tela de login
12. **cadastro.css** - Estilos da tela de cadastro
13. **dashboard.css** - Estilos do dashboard
14. **sessoes.css** - Estilos da listagem de sessões

### ⭕ A Criar

1. **RecuperacaoSenhaController.java** - (código fornecido nas instruções)
2. **DashboardController.java** - (código fornecido nas instruções)
3. **CompraIngressoController.java**
4. **SelecionarPoltronaController.java**
5. **ImpressaoIngressoController.java**
6. **compra-ingresso.fxml**
7. **selecionar-poltrona.fxml**
8. **impressao-ingresso.fxml**
9. **recuperacao-senha.css**
10. **compra-ingresso.css**
11. **selecionar-poltrona.css**
12. **impressao-ingresso.css**

### 🔄 A Refatorar

1. **EventoItemController.java** - Adaptar para usar FXML
2. **Main.java** - Atualizar para usar SceneManager

## 🚀 Benefícios da Nova Arquitetura

### 🎨 Separação de Responsabilidades

-   **FXML**: Estrutura e layout das interfaces
-   **CSS**: Estilização e temas
-   **Controllers**: Lógica de apresentação e eventos
-   **Utils**: Funcionalidades compartilhadas

### 🔧 Funcionalidades Implementadas

1. **Navegação Centralizada** (SceneManager)

    - Gerenciamento de stage principal
    - Abertura de modais
    - Injeção automática de dependências
    - Navegação baseada em tipo de usuário

2. **Validações Padronizadas** (ValidationUtils)

    - Máscaras para CPF e telefone
    - Validação de email em tempo real
    - Validações visuais (CSS classes)
    - Utilitários de validação comum

3. **Design Responsivo**
    - Media queries no CSS
    - Layouts flexíveis
    - Componentes escaláveis
    - Temas consistentes

### 🎯 Padrões de Design Implementados

1. **MVC** - Model-View-Controller com FXML
2. **Singleton** - SceneManager para navegação
3. **Observer** - Já existente para notificações
4. **Factory** - Criação de usuários
5. **Builder** - Construção de objetos complexos
6. **State** - Estados das poltronas
7. **Facade** - Teatro como fachada do sistema

## 📋 Checklist de Implementação

### Etapa 1: Configuração Base

-   [ ] Criar estrutura de diretórios
-   [ ] Mover arquivos FXML e CSS para resources
-   [ ] Criar controllers base
-   [ ] Atualizar Main.java
-   [ ] Testar login e navegação básica

### Etapa 2: Funcionalidades Core

-   [ ] Implementar cadastro completo
-   [ ] Implementar recuperação de senha
-   [ ] Implementar dashboard (usuário e admin)
-   [ ] Testar fluxo completo de autenticação

### Etapa 3: Funcionalidades de Negócio

-   [ ] Implementar listagem de sessões
-   [ ] Implementar compra de ingressos
-   [ ] Implementar seleção de poltronas
-   [ ] Implementar impressão de ingressos

### Etapa 4: Polimento e Testes

-   [ ] Aplicar estilos CSS finais
-   [ ] Implementar validações completas
-   [ ] Testar responsividade
-   [ ] Testar todos os fluxos de usuário
-   [ ] Documentar componentes

## 🔧 Comandos para Execução

```bash
# Executar com Maven
mvn clean javafx:run

# Ou compilar e executar
mvn clean compile
mvn javafx:run
```

## 📝 Notas Importantes

1. **Compatibilidade**: Mantém compatibilidade com código existente
2. **Migração Gradual**: Permite migração gradual das views antigas
3. **Extensibilidade**: Fácil adição de novas telas
4. **Manutenibilidade**: Código mais organizado e reutilizável
5. **Performance**: Carregamento otimizado de recursos

## 🎨 Customização de Temas

Os arquivos CSS permitem fácil customização:

```css
.root {
    -fx-primary-color: #3498db; /* Azul principal */
    -fx-secondary-color: #2ecc71; /* Verde secundário */
    -fx-background-color: #f8f9fa; /* Fundo da aplicação */
    -fx-text-color: #2c3e50; /* Cor do texto */
    -fx-card-background: white; /* Fundo dos cards */
}
```

Esta arquitetura FXML/CSS/Controller proporciona uma base sólida, escalável e moderna para o sistema de teatro! 🎭
