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
    tipo_usuario VARCHAR(10) NOT NULL DEFAULT 'COMUM',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS eventos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Nova tabela para armazenar os horários disponíveis
CREATE TABLE IF NOT EXISTS horarios_disponiveis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_sessao VARCHAR(10) NOT NULL,
    horario TIME NOT NULL,
    ordem INT NOT NULL
);

CREATE TABLE IF NOT EXISTS sessoes (
    id BIGINT NOT NULL PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    horario VARCHAR(10) NOT NULL,
    data_sessao DATE NOT NULL,
    horario_especifico_id BIGINT,
    faturamento DECIMAL(10,2) DEFAULT 0.0,
    FOREIGN KEY (evento_id) REFERENCES eventos(id),
    FOREIGN KEY (horario_especifico_id) REFERENCES horarios_disponiveis(id)
);

CREATE TABLE IF NOT EXISTS areas (
    id BIGINT NOT NULL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    capacidade_total INT NOT NULL
);

CREATE TABLE IF NOT EXISTS sessoes_areas (
    sessao_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    poltronas_disponiveis INT NOT NULL,
    faturamento DECIMAL(10,2) DEFAULT 0.0,
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
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (sessao_id) REFERENCES sessoes(id),
    FOREIGN KEY (area_id) REFERENCES areas(id)
);

-- Inserir usuário administrador
INSERT INTO usuarios (nome, cpf, senha, email, tipo_usuario) VALUES 
('Administrador', '000.000.000-00', 'admin123', 'admin@teatro.com', 'ADMIN');

-- Inserir eventos
INSERT INTO eventos (id, nome) VALUES 
(1, 'The Batman'),
(2, 'Kill Bill: Volume 1'),
(3, 'Django Livre');

-- Inserir áreas
INSERT INTO areas (id, nome, preco, capacidade_total) VALUES
(1, 'Plateia A', 40.00, 25),
(2, 'Plateia B', 60.00, 100),
(3, 'Camarote 1', 80.00, 10),
(4, 'Camarote 2', 80.00, 10),
(5, 'Camarote 3', 80.00, 10),
(6, 'Camarote 4', 80.00, 10),
(7, 'Camarote 5', 80.00, 10),
(8, 'Frisa 01', 120.00, 5),
(9, 'Frisa 02', 120.00, 5),
(10, 'Frisa 03', 120.00, 5),
(11, 'Frisa 04', 120.00, 5),
(12, 'Frisa 05', 120.00, 5),
(13, 'Frisa 06', 120.00, 5);

-- Inserir horários disponíveis para cada tipo de sessão
INSERT INTO horarios_disponiveis (tipo_sessao, horario, ordem) VALUES
-- Manhã
('Manhã', '09:30:00', 1),
('Manhã', '10:30:00', 2),
('Manhã', '11:30:00', 3),
-- Tarde
('Tarde', '14:00:00', 1),
('Tarde', '15:30:00', 2),
('Tarde', '17:00:00', 3),
-- Noite
('Noite', '19:00:00', 1),
('Noite', '20:30:00', 2),
('Noite', '22:00:00', 3);

-- Inserir sessões
INSERT INTO sessoes (id, evento_id, horario, data_sessao) VALUES
-- The Batman
(1, 1, 'Manhã', CURDATE()),
(2, 1, 'Tarde', CURDATE()),
(3, 1, 'Noite', CURDATE()),
-- Kill Bill
(4, 2, 'Manhã', CURDATE()),
(5, 2, 'Tarde', CURDATE()),
(6, 2, 'Noite', CURDATE()),
-- Django Livre
(7, 3, 'Manhã', CURDATE()),
(8, 3, 'Tarde', CURDATE()),
(9, 3, 'Noite', CURDATE());

-- Inserir relações entre sessões e áreas
INSERT INTO sessoes_areas (sessao_id, area_id, poltronas_disponiveis)
SELECT s.id, a.id, a.capacidade_total
FROM sessoes s
CROSS JOIN areas a;
