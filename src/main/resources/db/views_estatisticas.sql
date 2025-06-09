-- View para estatísticas de vendas por peça
CREATE OR REPLACE VIEW estatisticas_vendas_peca AS
SELECT 
    e.nome AS nome_peca,
    COUNT(i.id) AS total_ingressos_vendidos,
    SUM(i.valor) AS faturamento_total,
    AVG(i.valor) AS valor_medio_ingresso
FROM 
    eventos e
    LEFT JOIN sessoes s ON s.evento_id = e.id
    LEFT JOIN ingressos i ON i.sessao_id = s.id
GROUP BY 
    e.id, e.nome
ORDER BY 
    total_ingressos_vendidos DESC;

-- View para estatísticas de ocupação por sessão
CREATE OR REPLACE VIEW estatisticas_ocupacao_sessao AS
WITH total_poltronas AS (
    SELECT 
        s.id AS sessao_id,
        SUM(a.capacidade_total) AS total_poltronas
    FROM 
        sessoes s
        JOIN sessoes_areas sa ON sa.sessao_id = s.id
        JOIN areas a ON a.id = sa.area_id
    GROUP BY 
        s.id
)
SELECT 
    e.nome AS nome_peca,
    s.data_sessao AS data_sessao,
    s.horario AS horario,
    COUNT(i.id) AS ingressos_vendidos,
    tp.total_poltronas,
    ROUND((COUNT(i.id) / tp.total_poltronas) * 100, 2) AS percentual_ocupacao
FROM 
    eventos e
    JOIN sessoes s ON s.evento_id = e.id
    JOIN total_poltronas tp ON tp.sessao_id = s.id
    LEFT JOIN ingressos i ON i.sessao_id = s.id
GROUP BY 
    e.nome, s.id, s.data_sessao, s.horario, tp.total_poltronas
ORDER BY 
    percentual_ocupacao DESC;

-- View para estatísticas de faturamento por área e peça
CREATE OR REPLACE VIEW estatisticas_faturamento_area_peca AS
SELECT 
    e.nome AS nome_peca,
    a.nome AS nome_area,
    COUNT(i.id) AS total_ingressos_vendidos,
    SUM(i.valor) AS faturamento_total,
    AVG(i.valor) AS valor_medio_ingresso
FROM 
    eventos e
    JOIN sessoes s ON s.evento_id = e.id
    JOIN ingressos i ON i.sessao_id = s.id
    JOIN areas a ON a.id = i.area_id
GROUP BY 
    e.id, e.nome, a.id, a.nome
ORDER BY 
    e.nome, faturamento_total DESC;

-- View para estatísticas de vendas por período
CREATE OR REPLACE VIEW estatisticas_vendas_periodo AS
SELECT 
    DATE_FORMAT(i.data_compra, '%Y-%m') AS mes,
    e.nome AS nome_peca,
    COUNT(i.id) AS total_ingressos_vendidos,
    SUM(i.valor) AS faturamento_total,
    AVG(i.valor) AS valor_medio_ingresso
FROM 
    eventos e
    JOIN sessoes s ON s.evento_id = e.id
    JOIN ingressos i ON i.sessao_id = s.id
GROUP BY 
    DATE_FORMAT(i.data_compra, '%Y-%m'),
    e.id, e.nome
ORDER BY 
    mes DESC, faturamento_total DESC; 