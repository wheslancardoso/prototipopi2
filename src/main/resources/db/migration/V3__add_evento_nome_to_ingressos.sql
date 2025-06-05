-- Add evento_nome column to ingressos table if it doesn't exist
ALTER TABLE ingressos 
ADD COLUMN IF NOT EXISTS evento_nome VARCHAR(255);

-- Update existing records to use the event name from eventos table through sessoes
UPDATE ingressos i
JOIN sessoes s ON i.sessao_id = s.id
JOIN eventos e ON s.evento_id = e.id
SET i.evento_nome = e.nome
WHERE i.evento_nome IS NULL;
