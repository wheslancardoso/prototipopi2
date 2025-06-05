-- Add data_sessao column to ingressos table
ALTER TABLE ingressos ADD COLUMN data_sessao TIMESTAMP;

-- Update existing records to use the session date from sessoes table
UPDATE ingressos i
JOIN sessoes s ON i.sessao_id = s.id
SET i.data_sessao = s.data_sessao
WHERE i.data_sessao IS NULL;

-- Make the column NOT NULL after populating all existing records
ALTER TABLE ingressos MODIFY COLUMN data_sessao TIMESTAMP NOT NULL;
