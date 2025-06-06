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
    tipo_usuario VARCHAR(10) NOT NULL DEFAULT 'COMUM', -- Pode ser 'COMUM' ou 'ADMIN'
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS eventos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sessoes (
    id BIGINT NOT NULL PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    horario VARCHAR(10) NOT NULL, 
    data_sessao DATE NOT NULL,
    faturamento DECIMAL(10,2) DEFAULT 0.0,
    FOREIGN KEY (evento_id) REFERENCES eventos(id)
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
    -- A coluna poltronas_disponiveis foi removida. Será calculada pela VIEW.
    faturamento DECIMAL(10,2) DEFAULT 0.0,
    PRIMARY KEY (sessao_id, area_id),
    FOREIGN KEY (sessao_id) REFERENCES sessoes(id),
    FOREIGN KEY (area_id) REFERENCES areas(id)
);

CREATE TABLE IF NOT EXISTS horarios_disponiveis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_sessao VARCHAR(50) NOT NULL, -- Ex: Manhã, Tarde, Noite
    horario TIME NOT NULL,
    ordem INT NOT NULL, -- Para ordenação lógica dos horários
    descricao VARCHAR(100), -- Descrição opcional mais detalhada se necessário
    UNIQUE KEY uk_horario_tipo (horario, tipo_sessao) -- Um horário específico (e.g., 10:00) pode existir para diferentes tipos de sessão
);

CREATE TABLE IF NOT EXISTS ingressos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    sessao_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    horario_especifico_id BIGINT DEFAULT NULL,
    numero_poltrona INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    codigo_ingresso VARCHAR(100) NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (sessao_id) REFERENCES sessoes(id),
    FOREIGN KEY (area_id) REFERENCES areas(id),
    FOREIGN KEY (horario_especifico_id) REFERENCES horarios_disponiveis(id),
    CONSTRAINT uk_poltrona_sessao_area UNIQUE (sessao_id, area_id, horario_especifico_id, numero_poltrona),
    INDEX idx_ingressos_horario (sessao_id, area_id, horario_especifico_id)
);

-- VIEW para calcular poltronas disponíveis
CREATE OR REPLACE VIEW v_sessoes_areas_disponibilidade AS
SELECT
    sa.sessao_id,
    s.evento_id,
    e.nome AS nome_evento,
    s.data_sessao,
    s.horario AS horario_sessao,
    sa.area_id,
    a.nome AS nome_area,
    a.preco AS preco_area,
    a.capacidade_total,
    (a.capacidade_total - COUNT(i.id)) AS poltronas_disponiveis,
    COALESCE(SUM(i.valor), 0.0) AS faturamento_calculado_ingressos -- Faturamento baseado nos ingressos vendidos
FROM
    sessoes_areas sa
JOIN
    sessoes s ON sa.sessao_id = s.id
JOIN
    eventos e ON s.evento_id = e.id
JOIN
    areas a ON sa.area_id = a.id
LEFT JOIN
    ingressos i ON sa.sessao_id = i.sessao_id AND sa.area_id = i.area_id
GROUP BY
    sa.sessao_id,
    s.evento_id,
    e.nome,
    s.data_sessao,
    s.horario,
    sa.area_id,
    a.nome,
    a.preco,
    a.capacidade_total;

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

-- Inserir sessões
INSERT INTO sessoes (id, evento_id, horario, data_sessao) VALUES
(1, 1, 'Manhã', CURDATE()), (2, 1, 'Tarde', CURDATE()), (3, 1, 'Noite', CURDATE()),
(4, 2, 'Manhã', CURDATE()), (5, 2, 'Tarde', CURDATE()), (6, 2, 'Noite', CURDATE()),
(7, 3, 'Manhã', CURDATE()), (8, 3, 'Tarde', CURDATE()), (9, 3, 'Noite', CURDATE());

-- Inserir relações entre sessões e áreas (sem poltronas_disponiveis)
INSERT INTO sessoes_areas (sessao_id, area_id, faturamento)
SELECT s.id, a.id, 0.0 -- Faturamento inicial é 0.0, pode ser atualizado por triggers ou pela aplicação.
FROM sessoes s
CROSS JOIN areas a;

-- Inserir horários disponíveis
INSERT INTO horarios_disponiveis (tipo_sessao, horario, ordem, descricao) VALUES
-- Manhã
('Manhã', '09:00:00', 1, 'Sessão Matutina - Início às 09:00'),
('Manhã', '10:00:00', 2, 'Sessão Matutina - Início às 10:00'),
('Manhã', '11:00:00', 3, 'Sessão Matutina - Início às 11:00'),
-- Tarde
('Tarde', '14:00:00', 10, 'Sessão Vespertina - Início às 14:00'),
('Tarde', '15:00:00', 11, 'Sessão Vespertina - Início às 15:00'),
('Tarde', '16:00:00', 12, 'Sessão Vespertina - Início às 16:00'),
('Tarde', '17:00:00', 13, 'Sessão Vespertina - Início às 17:00'),
-- Noite
('Noite', '19:00:00', 20, 'Sessão Noturna - Início às 19:00'),
('Noite', '20:00:00', 21, 'Sessão Noturna - Início às 20:00'),
('Noite', '21:00:00', 22, 'Sessão Noturna - Início às 21:00'),
('Noite', '22:00:00', 23, 'Sessão Noturna - Início às 22:00');

 