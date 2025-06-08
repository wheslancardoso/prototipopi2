#!/bin/bash

# Script para parar o ambiente Docker do Sistema de Teatro
echo "🛑 Parando Sistema de Teatro..."

# Parar e remover containers
docker-compose down

# Remover volumes (opcional - descomente se quiser limpar os dados)
# echo "🗑️ Removendo volumes..."
# docker-compose down -v

# Remover imagens (opcional - descomente se quiser limpar as imagens)
# echo "🗑️ Removendo imagens..."
# docker-compose down --rmi all

echo "✅ Sistema parado com sucesso!" 