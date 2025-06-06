-- Script para reiniciar o banco de dados com a nova estrutura
-- ATENÇÃO: Este script irá APAGAR TODOS OS DADOS existentes

-- 1. Remover o banco de dados existente
DROP DATABASE IF EXISTS teatro_db;

-- 2. Criar o banco de dados novamente
CREATE DATABASE teatro_db;
USE teatro_db;

-- 3. Criar tabelas básicas (mantendo compatibilidade)
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

-- Tabela de horários disponíveis (nova funcionalidade)
CREATE TABLE IF NOT EXISTS horarios_disponiveis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_sessao VARCHAR(50) NOT NULL,
    horario TIME NOT NULL,
    ordem INT NOT NULL,
    descricao VARCHAR(100),
    UNIQUE KEY uk_horario_tipo (horario, tipo_sessao)
);

-- Tabela de sessões_areas (mantendo compatibilidade)
CREATE TABLE IF NOT EXISTS sessoes_areas (
    sessao_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    faturamento DECIMAL(10,2) DEFAULT 0.0,
    PRIMARY KEY (sessao_id, area_id),
    FOREIGN KEY (sessao_id) REFERENCES sessoes(id),
    FOREIGN KEY (area_id) REFERENCES areas(id)
);

-- Tabela de ingressos (compatível com o código existente)
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
    FOREIGN KEY (horario_especifico_id) REFERENCES horarios_disponiveis(id) ON DELETE SET NULL,
    CONSTRAINT uk_poltrona_sessao_area UNIQUE (sessao_id, area_id, horario_especifico_id, numero_poltrona)
);

-- 4. Criar VIEW de compatibilidade (opcional, se o código usar)
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
    COALESCE(SUM(i.valor), 0.0) AS faturamento_calculado_ingressos
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

-- 5. Inserir dados iniciais (igual ao banco antigo, mas com horários adicionais)

-- Usuário administrador
INSERT INTO usuarios (nome, cpf, senha, email, tipo_usuario) VALUES 
('Administrador', '000.000.000-00', 'admin123', 'admin@teatro.com', 'ADMIN');

-- Eventos
INSERT INTO eventos (id, nome) VALUES 
(1, 'The Batman'),
(2, 'Kill Bill: Volume 1'),
(3, 'Django Livre');

-- Áreas
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

-- Horários disponíveis (atualizados conforme solicitado)
INSERT INTO horarios_disponiveis (tipo_sessao, horario, ordem, descricao) VALUES
-- Manhã
('Manhã', '09:30:00', 1, 'Sessão Matutina - 09:30'),
('Manhã', '10:30:00', 2, 'Sessão Matutina - 10:30'),
('Manhã', '11:30:00', 3, 'Sessão Matutina - 11:30'),
-- Tarde
('Tarde', '14:00:00', 1, 'Sessão Vespertina - 14:00'),
('Tarde', '15:30:00', 2, 'Sessão Vespertina - 15:30'),
('Tarde', '17:00:00', 3, 'Sessão Vespertina - 17:00'),
-- Noite
('Noite', '19:00:00', 1, 'Sessão Noturna - 19:00'),
('Noite', '20:30:00', 2, 'Sessão Noturna - 20:30'),
('Noite', '22:00:00', 3, 'Sessão Noturna - 22:00');

-- Sessões (compatíveis com o sistema antigo)
INSERT INTO sessoes (id, evento_id, horario, data_sessao) VALUES
(1, 1, 'Manhã', CURDATE()),
(2, 1, 'Tarde', CURDATE()),
(3, 1, 'Noite', CURDATE()),
(4, 2, 'Manhã', CURDATE()),
(5, 2, 'Tarde', CURDATE()),
(6, 2, 'Noite', CURDATE()),
(7, 3, 'Manhã', CURDATE()),
(8, 3, 'Tarde', CURDATE()),
(9, 3, 'Noite', CURDATE());

-- Relacionamentos entre sessões e áreas (compatível com o sistema antigo)
INSERT INTO sessoes_areas (sessao_id, area_id, faturamento)
SELECT s.id, a.id, 0.0
FROM sessoes s
CROSS JOIN areas a;

-- 6. Criar índices para melhorar o desempenho
CREATE INDEX idx_ingressos_horario ON ingressos (sessao_id, area_id, horario_especifico_id);

-- 7. Mensagem de conclusão
SELECT 'Banco de dados reiniciado com sucesso!' AS mensagem;
