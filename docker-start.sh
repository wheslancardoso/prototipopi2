#!/bin/bash

# Script para iniciar o ambiente Docker do Sistema de Teatro
echo "🎭 Iniciando Sistema de Teatro com Docker..."

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Erro: Docker não está instalado. Por favor, instale o Docker primeiro."
    exit 1
fi

# Verificar se o Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Erro: Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
    exit 1
fi

# Parar containers existentes (se houver)
echo "🛑 Parando containers existentes..."
docker-compose down

# Construir e iniciar os containers
echo "🔨 Construindo e iniciando containers..."
docker-compose up --build -d

# Aguardar o banco de dados estar pronto
echo "⏳ Aguardando o banco de dados estar pronto..."
sleep 30

# Verificar status dos containers
echo "📊 Status dos containers:"
docker-compose ps

echo ""
echo "✅ Sistema iniciado com sucesso!"
echo ""
echo "📋 Informações importantes:"
echo "   • Banco de dados MySQL: localhost:3306"
echo "   • Usuário: root"
echo "   • Senha: root"
echo "   • Database: teatro_db"
echo ""
echo "🔍 Para ver os logs da aplicação:"
echo "   docker-compose logs -f teatro-app"
echo ""
echo "🛑 Para parar o sistema:"
echo "   docker-compose down"
echo ""
echo "🎯 Para executar apenas o JAR (mais rápido):"
echo "   docker-compose --profile jar up --build -d" 