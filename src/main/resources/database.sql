CREATE DATABASE IF NOT EXISTS teatro_db;
USE teatro_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    endereco VARCHAR(200),
    telefone VARCHAR(20),
    email VARCHAR(100),
    senha VARCHAR(100) NOT NULL,
    tipo_usuario ENUM('COMUM', 'ADMIN') NOT NULL DEFAULT 'COMUM',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS eventos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    poster VARCHAR(255),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(nome)
);

CREATE TABLE IF NOT EXISTS sessoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    horario ENUM('Manhã', 'Tarde', 'Noite') NOT NULL,
    data_sessao DATE NOT NULL,
    FOREIGN KEY (evento_id) REFERENCES eventos(id)
);

CREATE TABLE IF NOT EXISTS areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    capacidade_total INT NOT NULL
);

CREATE TABLE IF NOT EXISTS sessoes_areas (
    sessao_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    poltronas_disponiveis INT NOT NULL,
    PRIMARY KEY (sessao_id, area_id),
    FOREIGN KEY (sessao_id) REFERENCES sessoes(id),
    FOREIGN KEY (area_id) REFERENCES areas(id)
);

CREATE TABLE IF NOT EXISTS ingressos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    sessao_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    numero_poltrona INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (sessao_id) REFERENCES sessoes(id),
    FOREIGN KEY (area_id) REFERENCES areas(id)
);

-- Inserir usuário administrador
INSERT INTO usuarios (nome, cpf, senha, email, tipo_usuario) VALUES 
('Administrador', '000.000.000-00', 'admin123', 'admin@teatro.com', 'ADMIN');

-- Inserir eventos com posters
INSERT INTO eventos (nome, poster) VALUES 
('Hamlet', 'hamletposter.jpg'),
('O Fantasma da Opera', 'ofantasmadaoperaposter.jpg'),
('O Auto da Compadecida', 'compadecidaposter.jpg');

-- Inserir áreas com as configurações especificadas
INSERT INTO areas (nome, preco, capacidade_total) VALUES
('Plateia A', 40.00, 25),
('Plateia B', 60.00, 100),
('Camarote 1', 80.00, 10),
('Camarote 2', 80.00, 10),
('Camarote 3', 80.00, 10),
('Camarote 4', 80.00, 10),
('Camarote 5', 80.00, 10),
('Frisa 1', 120.00, 5),
('Frisa 2', 120.00, 5),
('Frisa 3', 120.00, 5),
('Frisa 4', 120.00, 5),
('Frisa 5', 120.00, 5),
('Frisa 6', 120.00, 5),
('Balcão Nobre', 250.00, 50);

-- Inserir sessões para cada evento
INSERT INTO sessoes (evento_id, horario, data_sessao) VALUES
-- Hamlet
(1, 'Manhã', CURDATE()),
(1, 'Tarde', CURDATE()),
(1, 'Noite', CURDATE()),
-- O Fantasma da Opera
(2, 'Manhã', CURDATE()),
(2, 'Tarde', CURDATE()),
(2, 'Noite', CURDATE()),
-- O Auto da Compadecida
(3, 'Manhã', CURDATE()),
(3, 'Tarde', CURDATE()),
(3, 'Noite', CURDATE());

-- Inserir relações entre sessões e áreas
INSERT INTO sessoes_areas (sessao_id, area_id, poltronas_disponiveis)
SELECT s.id, a.id, a.capacidade_total
FROM sessoes s
CROSS JOIN areas a; 