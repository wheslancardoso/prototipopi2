# Documentação da Arquitetura - Sistema de Venda de Ingressos Teatro ABC

## 3. Arquitetura e Projeto de Software

### 3.1 Visão Geral da Arquitetura

O Sistema de Venda de Ingressos do Teatro ABC é uma aplicação desktop desenvolvida em Java, utilizando JavaFX para a interface gráfica e MySQL como banco de dados. O sistema foi projetado para gerenciar a venda de ingressos para diferentes eventos teatrais, oferecendo funcionalidades de cadastro de usuários, gerenciamento de eventos, sessões, áreas e poltronas, além da compra e emissão de ingressos.

#### Principais Responsabilidades:

-   Gerenciamento de usuários (clientes e administradores)
-   Cadastro e gestão de eventos teatrais
-   Controle de sessões e horários
-   Gerenciamento de áreas e poltronas
-   Processo de compra e emissão de ingressos
-   Relatórios e estatísticas de vendas

#### Requisitos Não-Funcionais:

-   **Desempenho**: Resposta rápida nas operações de consulta e venda de ingressos
-   **Segurança**: Autenticação de usuários e proteção contra injeção SQL
-   **Escalabilidade**: Suporte a múltiplos eventos e sessões simultâneas
-   **Manutenibilidade**: Código modular e bem documentado
-   **Usabilidade**: Interface gráfica intuitiva e responsiva

### 3.2 Estilo Arquitetural Escolhido

O sistema adota o padrão arquitetural **MVC (Model-View-Controller)** em conjunto com o padrão **DAO (Data Access Object)**, complementado por uma camada de serviço.

#### Justificativa da Escolha:

-   **MVC**: Separação clara entre interface do usuário (View), lógica de negócios (Model) e controle de fluxo (Controller)
-   **DAO**: Abstração da camada de persistência, facilitando a manutenção e possíveis mudanças no banco de dados
-   **Camada de Serviço**: Encapsula a lógica de negócios e coordena as operações entre diferentes componentes

### 3.3 Estrutura de Pacotes no Projeto Java

```
com.teatro/
├── model/         # Classes de domínio (entidades)
├── view/          # Interfaces gráficas (JavaFX)
├── controller/    # Controladores das views
├── service/       # Lógica de negócios
├── dao/           # Acesso a dados
├── database/      # Configuração do banco de dados
├── util/          # Classes utilitárias
├── exception/     # Exceções personalizadas
└── observer/      # Implementação do padrão Observer
```

#### Justificativa da Separação de Responsabilidades:

-   **model**: Contém as classes que representam as entidades do negócio
-   **view**: Responsável pela interface gráfica e interação com o usuário
-   **controller**: Medeia a comunicação entre view e model
-   **service**: Implementa a lógica de negócios
-   **dao**: Gerencia a persistência dos dados
-   **database**: Configuração e gerenciamento da conexão com o banco
-   **util**: Classes auxiliares reutilizáveis
-   **exception**: Tratamento de erros específicos do sistema
-   **observer**: Implementa o padrão Observer para notificações

### 3.4 Tecnologias e Bibliotecas

#### Tecnologias Principais:

-   **Java 21**: Linguagem de programação principal
-   **JavaFX**: Framework para interface gráfica
-   **MySQL**: Sistema gerenciador de banco de dados
-   **JDBC**: API para conexão com o banco de dados
-   **Maven**: Gerenciamento de dependências e build

#### Justificativa das Escolhas:

-   **Java**: Linguagem robusta e madura, adequada para aplicações desktop
-   **JavaFX**: Framework moderno para GUI, com suporte a FXML e CSS
-   **MySQL**: SGBD relacional gratuito e amplamente utilizado
-   **JDBC**: API padrão para acesso a banco de dados em Java
-   **Maven**: Facilita o gerenciamento de dependências e o processo de build

### 3.5 Fluxo de Dados e Controle

#### Fluxo de Dados Principal:

1. **Entrada de Dados**

    - Interface gráfica (JavaFX) captura interações do usuário
    - Validação inicial dos dados no controller

2. **Processamento**

    - Controller recebe os dados e os encaminha para a camada de serviço
    - Service implementa a lógica de negócios
    - Validações de regras de negócio são aplicadas

3. **Persistência**

    - Service utiliza DAO para operações no banco de dados
    - DAO executa queries SQL através do JDBC
    - Resultados são mapeados para objetos do modelo

4. **Retorno**
    - Dados processados retornam pela mesma cadeia
    - View é atualizada com os resultados
    - Feedback é apresentado ao usuário

#### Exemplo de Fluxo (Compra de Ingresso):

1. Usuário seleciona evento e sessão na interface
2. Controller valida a seleção
3. Service verifica disponibilidade de poltronas
4. DAO consulta o banco de dados
5. Service processa a compra
6. DAO persiste a transação
7. Controller atualiza a interface
8. Usuário recebe confirmação e ingresso
