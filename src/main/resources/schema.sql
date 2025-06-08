-- Criação da tabela de sessões
CREATE TABLE IF NOT EXISTS sessoes (
    id BIGSERIAL PRIMARY KEY,
    evento_nome VARCHAR(100) NOT NULL,
    tipo_sessao VARCHAR(10) NOT NULL CHECK (tipo_sessao IN ('MANHA', 'TARDE', 'NOITE')),
    data_sessao DATE NOT NULL,
    UNIQUE(evento_nome, tipo_sessao, data_sessao)
);

-- Criação da tabela de áreas
CREATE TABLE IF NOT EXISTS areas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    capacidade_total INTEGER NOT NULL
);

-- Criação da tabela de ingressos
CREATE TABLE IF NOT EXISTS ingressos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    sessao_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    numero_poltrona INTEGER NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_compra TIMESTAMP NOT NULL,
    codigo VARCHAR(8) NOT NULL UNIQUE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (sessao_id) REFERENCES sessoes(id),
    FOREIGN KEY (area_id) REFERENCES areas(id),
    UNIQUE(sessao_id, area_id, numero_poltrona)
); 