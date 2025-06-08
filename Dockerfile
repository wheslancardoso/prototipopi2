# Dockerfile para o Sistema de Teatro JavaFX
FROM openjdk:21-jdk-slim

# Instalar dependências necessárias
RUN apt-get update && apt-get install -y \
    maven \
    wget \
    unzip \
    xvfb \
    libgtk-3-0 \
    libx11-xcb1 \
    libxcomposite1 \
    libxcursor1 \
    libxdamage1 \
    libxi6 \
    libxtst6 \
    libnss3 \
    libcups2 \
    libxss1 \
    libxrandr2 \
    libasound2 \
    libpangocairo-1.0-0 \
    libatk1.0-0 \
    libcairo-gobject2 \
    libgtk-3-0 \
    libgdk-pixbuf2.0-0 \
    libxfixes3 \
    libxrender1 \
    libxrandr2 \
    libxcomposite1 \
    libxcursor1 \
    libxdamage1 \
    libxi6 \
    libxtst6 \
    libnss3 \
    libcups2 \
    libxss1 \
    libasound2 \
    && rm -rf /var/lib/apt/lists/*

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos do projeto
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn ./.mvn

# Dar permissão de execução ao mvnw
RUN chmod +x mvnw

# Baixar dependências do Maven
RUN ./mvnw dependency:go-offline -B

# Compilar o projeto
RUN ./mvnw clean compile

# Criar JAR executável
RUN ./mvnw package -DskipTests

# Expor porta (se necessário para comunicação)
EXPOSE 8080

# Comando para executar a aplicação
CMD ["./mvnw", "javafx:run"] 