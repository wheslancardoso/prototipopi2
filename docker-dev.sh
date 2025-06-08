#!/bin/bash

# Script para desenvolvimento com hot-reload
echo "🔧 Iniciando modo de desenvolvimento..."

# Função para compilar e executar
compile_and_run() {
    echo "🔄 Compilando projeto..."
    ./mvnw clean compile
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilação bem-sucedida!"
        echo "🚀 Iniciando aplicação..."
        ./mvnw javafx:run
    else
        echo "❌ Erro na compilação!"
    fi
}

# Função para monitorar mudanças
watch_changes() {
    echo "👀 Monitorando mudanças nos arquivos..."
    inotifywait -m -r -e modify,create,delete src/ pom.xml | while read path action file; do
        echo "📝 Arquivo alterado: $file"
        echo "🔄 Reiniciando aplicação..."
        pkill -f "javafx:run" || true
        sleep 2
        compile_and_run &
    done
}

# Compilar e executar pela primeira vez
compile_and_run &

# Iniciar monitoramento de mudanças
watch_changes 