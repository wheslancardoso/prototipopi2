-- Script para adicionar a coluna horario_especifico_id à tabela ingressos
ALTER TABLE ingressos ADD COLUMN horario_especifico_id BIGINT DEFAULT 0;
ALTER TABLE ingressos ADD FOREIGN KEY (horario_especifico_id) REFERENCES horarios_disponiveis(id);

-- Criar índice para melhorar a performance das consultas
CREATE INDEX idx_ingressos_horario ON ingressos(sessao_id, area_id, horario_especifico_id);
