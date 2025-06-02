#!/bin/bash

# Verifica se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "Erro: Maven não encontrado. Por favor, instale o Maven e tente novamente."
    exit 1
fi

# Verifica se o Java 21 está instalado
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" != "21" ] && [ "$JAVA_VERSION" != "17" ]; then
    echo "Aviso: Java 21 ou 17 é recomendado. Versão encontrada: $JAVA_VERSION"
    read -p "Deseja continuar mesmo assim? (s/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Ss]$ ]]; then
        exit 1
    fi
fi

# Navega para o diretório do projeto
cd "$(dirname "$0")"

echo "Compilando o projeto..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Erro ao compilar o projeto. Verifique os erros acima."
    exit 1
fi

echo "Iniciando a aplicação..."
mvn javafx:run

if [ $? -ne 0 ]; then
    echo "Erro ao iniciar a aplicação. Verifique os erros acima."
    echo "Tentando executar o JAR gerado..."
    
    # Tenta executar o JAR gerado
    JAR_FILE=$(find target -name "*-jar-with-dependencies.jar" | head -n 1)
    if [ -f "$JAR_FILE" ]; then
        echo "Executando $JAR_FILE..."
        java -jar "$JAR_FILE"
    else
        echo "Erro: JAR não encontrado. Verifique se o build foi concluído com sucesso."
        exit 1
    fi
fi
