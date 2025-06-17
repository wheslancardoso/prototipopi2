# Melhorias de Segurança - Sistema de Senhas

## Problema Identificado

O sistema estava armazenando senhas em **texto plano** no banco de dados, o que representa um risco crítico de segurança. Em caso de vazamento de dados, todas as senhas dos usuários ficariam expostas.

## Solução Implementada

### 1. Classe PasswordHasher

Foi criada uma classe utilitária `PasswordHasher` que implementa:

-   **Hashing com Salt**: Usa SHA-256 com salt aleatório de 16 bytes
-   **Formato de Armazenamento**: `salt:hash` (separados por `:`)
-   **Compatibilidade**: Mantém suporte para senhas antigas em texto plano durante a migração

#### Métodos Principais:

```java
// Gera hash de uma senha com salt aleatório
public static String hashPassword(String password)

// Verifica se uma senha corresponde ao hash armazenado
public static boolean verifyPassword(String password, String storedHash)

// Verifica se uma senha já está hasheada
public static boolean isHashed(String storedHash)
```

### 2. Modificações no DAO

O `UsuarioDAOImpl` foi modificado para:

-   **Salvar senhas hasheadas** ao criar novos usuários
-   **Verificar senhas usando hash** durante autenticação
-   **Manter compatibilidade** com senhas antigas em texto plano

#### Principais mudanças:

```java
// Antes: Comparação direta em SQL
String sql = "SELECT * FROM usuarios WHERE (cpf = ? OR email = ?) AND senha = ?";

// Depois: Busca usuário e verifica hash
String sql = "SELECT * FROM usuarios WHERE cpf = ? OR email = ?";
// ... depois verifica com PasswordHasher.verifyPassword()
```

### 3. Sistema de Migração Automática

Foi implementado um sistema que:

-   **Detecta automaticamente** senhas em texto plano
-   **Migra senhas antigas** para formato hasheado na primeira execução
-   **Não interrompe** o funcionamento do sistema se a migração falhar

#### Execução automática no `Main.java`:

```java
// Executa migração de senhas se necessário
boolean migracaoExecutada = PasswordMigration.executarMigracaoSeNecessario();
if (migracaoExecutada) {
    System.out.println("Migração de senhas executada com sucesso.");
}
```

### 4. Testes Unitários

Foram criados testes abrangentes para garantir:

-   ✅ Geração correta de hashes
-   ✅ Verificação de senhas hasheadas
-   ✅ Compatibilidade com senhas antigas
-   ✅ Unicidade de salts
-   ✅ Segurança contra ataques de rainbow table

## Benefícios de Segurança

### 1. Proteção contra Vazamentos

-   Senhas não podem ser recuperadas mesmo com acesso ao banco
-   Cada senha tem um salt único, impedindo ataques de rainbow table

### 2. Compatibilidade

-   Sistema continua funcionando com usuários existentes
-   Migração automática e transparente

### 3. Conformidade

-   Segue as melhores práticas de segurança
-   Implementa padrões recomendados pela OWASP

## Como Funciona

### Para Novos Usuários:

1. Usuário digita senha em texto plano
2. Sistema gera salt aleatório
3. Combina salt + senha e aplica SHA-256
4. Armazena no formato `salt:hash`

### Para Autenticação:

1. Sistema busca usuário pelo CPF/email
2. Recupera o hash armazenado
3. Aplica o mesmo processo na senha fornecida
4. Compara os hashes

### Para Senhas Antigas:

1. Sistema detecta formato antigo (sem `:`)
2. Compara diretamente para compatibilidade
3. Na próxima atualização, converte para hash

## Arquivos Modificados

-   ✅ `src/main/java/com/teatro/util/PasswordHasher.java` (novo)
-   ✅ `src/main/java/com/teatro/util/PasswordMigration.java` (novo)
-   ✅ `src/main/java/com/teatro/dao/UsuarioDAOImpl.java` (modificado)
-   ✅ `src/main/java/com/teatro/Main.java` (modificado)
-   ✅ `src/test/java/com/teatro/util/PasswordHasherTest.java` (novo)

## Próximos Passos Recomendados

1. **Monitoramento**: Implementar logs de tentativas de login
2. **Política de Senhas**: Adicionar validação de força de senha
3. **Rate Limiting**: Limitar tentativas de login
4. **Auditoria**: Registrar mudanças de senha
5. **Backup**: Garantir backup seguro das senhas hasheadas

## Teste da Implementação

Para testar se tudo está funcionando:

```bash
# Executar testes unitários
mvn test -Dtest=PasswordHasherTest

# Compilar o projeto
mvn clean compile

# Executar o sistema (a migração acontecerá automaticamente)
mvn javafx:run
```

A migração será executada automaticamente na primeira execução após a implementação, convertendo todas as senhas existentes para o formato seguro.
