-- Script de Migração Segura para o Novo Esquema
-- Este script deve ser executado em um ambiente de teste primeiro

-- 1. Criar backup das tabelas atuais (opcional, apenas se não tiver backup)
-- CREATE TABLE IF NOT EXISTS backup_ingressos AS SELECT * FROM ingressos;
-- CREATE TABLE IF NOT EXISTS backup_sessoes_areas AS SELECT * FROM sessoes_areas;

-- 2. Criar a nova tabela de horários disponíveis
CREATE TABLE IF NOT EXISTS horarios_disponiveis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_sessao VARCHAR(50) NOT NULL,
    horario TIME NOT NULL,
    ordem INT NOT NULL,
    descricao VARCHAR(100),
    UNIQUE KEY uk_horario_tipo (horario, tipo_sessao)
);

-- 3. Inserir horários padrão compatíveis com o sistema atual
INSERT IGNORE INTO horarios_disponiveis (tipo_sessao, horario, ordem, descricao) VALUES
('Manhã', '10:00:00', 1, 'Sessão Matutina - 10:00'),
('Tarde', '14:00:00', 2, 'Sessão Vespertina - 14:00'),
('Noite', '19:00:00', 3, 'Sessão Noturna - 19:00');

-- 4. Adicionar novas colunas à tabela de ingressos
ALTER TABLE ingressos 
ADD COLUMN horario_especifico_id BIGINT DEFAULT NULL,
ADD COLUMN codigo_ingresso VARCHAR(100) NULL,
ADD CONSTRAINT fk_ingresso_horario 
    FOREIGN KEY (horario_especifico_id) 
    REFERENCES horarios_disponiveis(id) 
    ON DELETE SET NULL;

-- 5. Atualizar os ingressos existentes com os horários correspondentes
-- Primeiro, obter o ID do horário padrão para cada tipo de sessão
SET @manha_id = (SELECT id FROM horarios_disponiveis WHERE tipo_sessao = 'Manhã' LIMIT 1);
SET @tarde_id = (SELECT id FROM horarios_disponiveis WHERE tipo_sessao = 'Tarde' LIMIT 1);
SET @noite_id = (SELECT id FROM horarios_disponiveis WHERE tipo_sessao = 'Noite' LIMIT 1);

-- Atualizar ingressos existentes baseado no horário da sessão
UPDATE ingressos i
JOIN sessoes s ON i.sessao_id = s.id
SET i.horario_especifico_id = 
    CASE 
        WHEN s.horario = 'Manhã' THEN @manha_id
        WHEN s.horario = 'Tarde' THEN @tarde_id
        WHEN s.horario = 'Noite' THEN @noite_id
        ELSE NULL
    END
WHERE i.horario_especifico_id IS NULL;

-- 6. Criar a VIEW de disponibilidade
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

-- 7. Atualizar a tabela sessoes_areas para remover a coluna poltronas_disponiveis
-- Primeiro, criar uma nova tabela temporária sem a coluna
CREATE TABLE sessoes_areas_nova (
    sessao_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    faturamento DECIMAL(10,2) DEFAULT 0.0,
    PRIMARY KEY (sessao_id, area_id),
    FOREIGN KEY (sessao_id) REFERENCES sessoes(id),
    FOREIGN KEY (area_id) REFERENCES areas(id)
);

-- Copiar dados para a nova tabela
INSERT INTO sessoes_areas_nova (sessao_id, area_id, faturamento)
SELECT sessao_id, area_id, faturamento FROM sessoes_areas;

-- Remover a tabela antiga e renomear a nova
DROP TABLE sessoes_areas;
RENAME TABLE sessoes_areas_nova TO sessoes_areas;

-- 8. Adicionar índices para melhorar o desempenho
CREATE INDEX idx_ingressos_horario ON ingressos (sessao_id, area_id, horario_especifico_id);

-- 9. Atualizar o faturamento nas tabelas sessoes e sessoes_areas
UPDATE sessoes s
SET faturamento = (
    SELECT COALESCE(SUM(i.valor), 0.0)
    FROM ingressos i
    WHERE i.sessao_id = s.id
);

UPDATE sessoes_areas sa
SET faturamento = (
    SELECT COALESCE(SUM(i.valor), 0.0)
    FROM ingressos i
    WHERE i.sessao_id = sa.sessao_id AND i.area_id = sa.area_id
);

-- 10. Atualizar códigos de ingresso para os ingressos existentes
UPDATE ingressos 
SET codigo_ingresso = CONCAT('ING', LPAD(id, 6, '0'), '-', DATE_FORMAT(NOW(), '%Y%m%d'))
WHERE codigo_ingresso IS NULL;
