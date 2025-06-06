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
    data_sessao DATE,  -- Adicionado para compatibilidade
    evento_nome VARCHAR(100),  -- Adicionado para compatibilidade
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (sessao_id) REFERENCES sessoes(id),
    FOREIGN KEY (area_id) REFERENCES areas(id),
    FOREIGN KEY (horario_especifico_id) REFERENCES horarios_disponiveis(id) ON DELETE SET NULL,
    CONSTRAINT uk_poltrona_sessao_area UNIQUE (sessao_id, area_id, horario_especifico_id, numero_poltrona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

-- Sessões para cada evento e data
-- Criando 7 dias de sessões para cada evento (3 sessões por dia)
-- Formato: (id, evento_id, horario, data_sessao)
INSERT INTO sessoes (id, evento_id, horario, data_sessao) VALUES
-- Dia 1 (hoje)
(1, 1, 'Manhã', CURDATE()),
(2, 1, 'Tarde', CURDATE()),
(3, 1, 'Noite', CURDATE()),
(4, 2, 'Manhã', CURDATE()),
(5, 2, 'Tarde', CURDATE()),
(6, 2, 'Noite', CURDATE()),
(7, 3, 'Manhã', CURDATE()),
(8, 3, 'Tarde', CURDATE()),
(9, 3, 'Noite', CURDATE()),

-- Dia 2 (amanhã)
(10, 1, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(11, 1, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(12, 1, 'Noite', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(13, 2, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(14, 2, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(15, 2, 'Noite', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(16, 3, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(17, 3, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(18, 3, 'Noite', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),

-- Dia 3
(19, 1, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(20, 1, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(21, 1, 'Noite', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(22, 2, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(23, 2, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(24, 2, 'Noite', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(25, 3, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(26, 3, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(27, 3, 'Noite', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),

-- Dia 4
(28, 1, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
(29, 1, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
(30, 1, 'Noite', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
(31, 2, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
(32, 2, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
(33, 2, 'Noite', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
(34, 3, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
(35, 3, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
(36, 3, 'Noite', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),

-- Dia 5
(37, 1, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 4 DAY)),
(38, 1, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 4 DAY)),
(39, 1, 'Noite', DATE_ADD(CURDATE(), INTERVAL 4 DAY)),
(40, 2, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 4 DAY)),
(41, 2, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 4 DAY)),
(42, 2, 'Noite', DATE_ADD(CURDATE(), INTERVAL 4 DAY)),
(43, 3, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 4 DAY)),
(44, 3, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 4 DAY)),
(45, 3, 'Noite', DATE_ADD(CURDATE(), INTERVAL 4 DAY)),

-- Dia 6
(46, 1, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
(47, 1, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
(48, 1, 'Noite', DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
(49, 2, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
(50, 2, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
(51, 2, 'Noite', DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
(52, 3, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
(53, 3, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 5 DAY)),
(54, 3, 'Noite', DATE_ADD(CURDATE(), INTERVAL 5 DAY)),

-- Dia 7
(55, 1, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 6 DAY)),
(56, 1, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 6 DAY)),
(57, 1, 'Noite', DATE_ADD(CURDATE(), INTERVAL 6 DAY)),
(58, 2, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 6 DAY)),
(59, 2, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 6 DAY)),
(60, 2, 'Noite', DATE_ADD(CURDATE(), INTERVAL 6 DAY)),
(61, 3, 'Manhã', DATE_ADD(CURDATE(), INTERVAL 6 DAY)),
(62, 3, 'Tarde', DATE_ADD(CURDATE(), INTERVAL 6 DAY)),
(63, 3, 'Noite', DATE_ADD(CURDATE(), INTERVAL 6 DAY));

-- Relacionamentos entre sessões e áreas (compatível com o sistema antigo)
INSERT INTO sessoes_areas (sessao_id, area_id, faturamento)
SELECT s.id, a.id, 0.0
FROM sessoes s
CROSS JOIN areas a;

-- 6. Criar índices para melhorar o desempenho
CREATE INDEX idx_ingressos_horario ON ingressos (sessao_id, area_id, horario_especifico_id);

-- 11. Criar trigger para preencher automaticamente data_sessao e evento_nome
DELIMITER //
CREATE TRIGGER before_insert_ingresso
BEFORE INSERT ON ingressos
FOR EACH ROW
BEGIN
    -- Preenche a data da sessão com base na tabela sessoes
    SELECT s.data_sessao, e.nome 
    INTO NEW.data_sessao, NEW.evento_nome
    FROM sessoes s
    JOIN eventos e ON s.evento_id = e.id
    WHERE s.id = NEW.sessao_id
    LIMIT 1;
    
    -- Gera um código de ingresso se não existir
    IF NEW.codigo_ingresso IS NULL THEN
        SET NEW.codigo_ingresso = CONCAT('ING', LPAD(NEW.id, 6, '0'), '-', DATE_FORMAT(NOW(), '%Y%m%d'));
    END IF;
END//
DELIMITER ;

-- 12. Atualizar ingressos existentes com dados de sessão
UPDATE ingressos i
JOIN sessoes s ON i.sessao_id = s.id
JOIN eventos e ON s.evento_id = e.id
SET i.data_sessao = s.data_sessao,
    i.evento_nome = e.nome
WHERE i.data_sessao IS NULL OR i.evento_nome IS NULL;

-- 13. Mensagem de conclusão
SELECT 'Banco de dados reiniciado com sucesso!' AS mensagem;
