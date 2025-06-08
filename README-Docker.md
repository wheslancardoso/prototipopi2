# 🎭 Sistema de Teatro - Docker

Este documento explica como executar o Sistema de Venda de Ingressos de Teatro usando Docker.

## 📋 Pré-requisitos

-   **Docker** (versão 20.10 ou superior)
-   **Docker Compose** (versão 2.0 ou superior)
-   **Git** (para clonar o repositório)

### Instalação do Docker

#### Ubuntu/Debian:

```bash
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
```

#### Windows/macOS:

Baixe e instale o [Docker Desktop](https://www.docker.com/products/docker-desktop)

## 🚀 Início Rápido

### 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd prototipopi2
```

### 2. Executar com script automatizado

```bash
chmod +x docker-start.sh
./docker-start.sh
```

### 3. Ou executar manualmente

```bash
# Construir e iniciar todos os serviços
docker-compose up --build -d

# Verificar status
docker-compose ps
```

## 🏗️ Arquitetura Docker

O projeto utiliza uma arquitetura multi-container:

```
┌─────────────────┐    ┌─────────────────┐
│   teatro-app    │    │     mysql       │
│   (JavaFX)      │◄──►│   (Database)    │
│   Port: 8080    │    │   Port: 3306    │
└─────────────────┘    └─────────────────┘
```

### Serviços

1. **mysql**: Banco de dados MySQL 8.0

    - Porta: 3306
    - Usuário: root
    - Senha: root
    - Database: teatro_db

2. **teatro-app**: Aplicação JavaFX

    - Interface gráfica
    - Conecta ao MySQL
    - Logs em `./logs/`

3. **teatro-jar** (opcional): Versão otimizada
    - Executa apenas o JAR compilado
    - Mais rápido para produção

## 📖 Comandos Úteis

### Iniciar o sistema

```bash
# Método completo (recomendado para desenvolvimento)
docker-compose up --build -d

# Método otimizado (apenas JAR)
docker-compose --profile jar up --build -d
```

### Ver logs

```bash
# Logs da aplicação
docker-compose logs -f teatro-app

# Logs do banco de dados
docker-compose logs -f mysql

# Todos os logs
docker-compose logs -f
```

### Parar o sistema

```bash
# Parar containers
docker-compose down

# Parar e remover volumes (limpa dados)
docker-compose down -v

# Parar e remover imagens
docker-compose down --rmi all
```

### Acessar o banco de dados

```bash
# Conectar via linha de comando
docker exec -it teatro_mysql mysql -u root -p

# Ou usar um cliente MySQL externo
# Host: localhost
# Port: 3306
# User: root
# Password: root
# Database: teatro_db
```

### Reconstruir containers

```bash
# Reconstruir sem cache
docker-compose build --no-cache

# Reconstruir e reiniciar
docker-compose up --build --force-recreate -d
```

## 🔧 Configuração

### Variáveis de Ambiente

O sistema usa as seguintes variáveis de ambiente:

```yaml
# Banco de dados
DB_HOST=mysql
DB_PORT=3306
DB_NAME=teatro_db
DB_USER=root
DB_PASSWORD=root

# Interface gráfica (para desenvolvimento)
DISPLAY=${DISPLAY}
```

### Volumes

-   `mysql_data`: Dados persistentes do MySQL
-   `./logs`: Logs da aplicação
-   `/tmp/.X11-unix`: Socket X11 para interface gráfica

### Portas

-   **3306**: MySQL (localhost:3306)
-   **8080**: Aplicação (se necessário)

## 🐛 Solução de Problemas

### Problema: Interface gráfica não aparece

**Solução 1**: Executar com X11 forwarding

```bash
# No Linux
xhost +local:docker
docker-compose up --build

# No Windows/macOS
# Use um servidor X11 como VcXsrv ou XQuartz
```

**Solução 2**: Executar sem interface gráfica

```bash
# Modificar o docker-compose.yml para executar em modo headless
# Ou usar apenas o JAR
docker-compose --profile jar up --build -d
```

### Problema: Banco de dados não conecta

```bash
# Verificar se o MySQL está rodando
docker-compose ps

# Verificar logs do MySQL
docker-compose logs mysql

# Aguardar mais tempo para inicialização
docker-compose up --build -d
sleep 60
```

### Problema: Porta 3306 já em uso

```bash
# Parar serviços locais do MySQL
sudo systemctl stop mysql

# Ou alterar a porta no docker-compose.yml
ports:
  - "3307:3306"  # Usar porta 3307 externamente
```

### Problema: Permissões de arquivo

```bash
# Dar permissão aos scripts
chmod +x docker-start.sh docker-stop.sh

# Corrigir permissões do Maven wrapper
chmod +x mvnw
```

## 📊 Monitoramento

### Verificar recursos

```bash
# Uso de CPU e memória
docker stats

# Espaço em disco
docker system df
```

### Limpeza

```bash
# Limpar containers parados
docker container prune

# Limpar imagens não utilizadas
docker image prune

# Limpeza completa
docker system prune -a
```

## 🔄 Desenvolvimento

### Modo de desenvolvimento

```bash
# Executar com volumes para hot-reload
docker-compose -f docker-compose.dev.yml up --build
```

### Testes

```bash
# Executar testes
docker-compose run --rm teatro-app ./mvnw test

# Executar testes com cobertura
docker-compose run --rm teatro-app ./mvnw jacoco:report
```

## 📝 Notas Importantes

1. **Primeira execução**: O banco de dados pode demorar até 2 minutos para inicializar completamente
2. **Interface gráfica**: Funciona melhor no Linux. No Windows/macOS pode precisar de configuração adicional
3. **Dados**: Os dados do MySQL são persistidos no volume `mysql_data`
4. **Logs**: Os logs da aplicação são salvos em `./logs/`
5. **Performance**: Use o perfil `jar` para melhor performance em produção

## 🆘 Suporte

Se encontrar problemas:

1. Verifique os logs: `docker-compose logs`
2. Verifique o status: `docker-compose ps`
3. Reconstrua os containers: `docker-compose up --build --force-recreate -d`
4. Consulte a documentação do projeto principal

## 📄 Licença

Este projeto segue a mesma licença do projeto principal.
