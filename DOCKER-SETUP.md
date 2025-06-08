# 🐳 Configuração Docker - Sistema de Teatro

## 📁 Arquivos Criados

### Arquivos Principais

-   `Dockerfile` - Container principal com Maven e JavaFX
-   `Dockerfile.jar` - Container otimizado para JAR compilado
-   `Dockerfile.dev` - Container para desenvolvimento com hot-reload
-   `docker-compose.yml` - Orquestração principal
-   `docker-compose.dev.yml` - Configuração para desenvolvimento
-   `docker-compose.prod.yml` - Configuração para produção

### Scripts de Automação

-   `docker-start.sh` - Iniciar ambiente completo
-   `docker-stop.sh` - Parar ambiente
-   `build-jar.sh` - Compilar JAR para produção
-   `docker-dev.sh` - Script de desenvolvimento com hot-reload

### Configurações

-   `env.example` - Exemplo de variáveis de ambiente
-   `src/main/resources/database-docker.properties` - Configuração do banco para Docker
-   `.dockerignore` - Arquivos ignorados no build

### Documentação

-   `README-Docker.md` - Documentação completa
-   `DOCKER-SETUP.md` - Este arquivo

## 🚀 Como Usar

### Início Rápido

```bash
# Dar permissão aos scripts
chmod +x *.sh

# Iniciar ambiente completo
./docker-start.sh
```

### Desenvolvimento

```bash
# Modo desenvolvimento com hot-reload
docker-compose -f docker-compose.dev.yml up --build

# Ou usar o script
./docker-dev.sh
```

### Produção

```bash
# Compilar JAR
./build-jar.sh

# Executar em produção
docker-compose -f docker-compose.prod.yml up --build -d
```

### Comandos Úteis

```bash
# Ver logs
docker-compose logs -f

# Parar tudo
docker-compose down

# Reconstruir
docker-compose up --build --force-recreate -d
```

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌─────────────────┐
│   teatro-app    │    │     mysql       │
│   (JavaFX)      │◄──►│   (Database)    │
│   Port: 8080    │    │   Port: 3306    │
└─────────────────┘    └─────────────────┘
```

## 📋 Pré-requisitos

-   Docker 20.10+
-   Docker Compose 2.0+
-   2GB RAM disponível
-   5GB espaço em disco

## 🎯 Próximos Passos

1. Teste a configuração: `./docker-start.sh`
2. Verifique os logs: `docker-compose logs -f`
3. Acesse o banco: `docker exec -it teatro_mysql mysql -u root -p`
4. Compartilhe o projeto com outros desenvolvedores

## 🆘 Suporte

-   Verifique `README-Docker.md` para documentação completa
-   Use `docker-compose logs` para diagnosticar problemas
-   Consulte a documentação do Docker se necessário
