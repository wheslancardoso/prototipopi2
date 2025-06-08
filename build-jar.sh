#!/bin/bash

# Script para compilar o JAR do projeto
echo "🔨 Compilando JAR do Sistema de Teatro..."

# Verificar se o Maven está disponível
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado. Usando Maven wrapper..."
    if [ ! -f "./mvnw" ]; then
        echo "❌ Maven wrapper não encontrado!"
        exit 1
    fi
    MVN_CMD="./mvnw"
else
    MVN_CMD="mvn"
fi

# Limpar e compilar
echo "🧹 Limpando build anterior..."
$MVN_CMD clean

echo "📦 Baixando dependências..."
$MVN_CMD dependency:resolve

echo "🔨 Compilando projeto..."
$MVN_CMD compile

echo "📦 Criando JAR executável..."
$MVN_CMD package -DskipTests

# Verificar se o JAR foi criado
JAR_FILE=$(find target -name "*-jar-with-dependencies.jar" | head -n 1)
if [ -f "$JAR_FILE" ]; then
    echo "✅ JAR criado com sucesso: $JAR_FILE"
    echo "📊 Tamanho do arquivo: $(du -h "$JAR_FILE" | cut -f1)"
else
    echo "❌ Erro: JAR não foi criado!"
    exit 1
fi

echo ""
echo "🎯 Agora você pode executar:"
echo "   docker-compose --profile jar up --build -d"
echo "   ou"
echo "   docker-compose -f docker-compose.prod.yml up --build -d" 