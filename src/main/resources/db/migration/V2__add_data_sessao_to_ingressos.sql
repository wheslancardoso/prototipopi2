-- Add data_sessao column to ingressos table if it doesn't exist
ALTER TABLE ingressos 
ADD COLUMN IF NOT EXISTS data_sessao TIMESTAMP;

-- Update existing records to use the session date from sessoes table
UPDATE ingressos i
JOIN sessoes s ON i.sessao_id = s.id
SET i.data_sessao = s.data
WHERE i.data_sessao IS NULL;

-- Make the column NOT NULL after populating all existing records
-- Note: This might fail if there are NULL values that couldn't be populated
-- Comment this out if you get an error and need to handle NULL values first
-- ALTER TABLE ingressos MODIFY COLUMN data_sessao TIMESTAMP NOT NULL;
