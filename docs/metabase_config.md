# Configuração do Metabase para Estatísticas do Teatro

Este documento descreve como configurar o Metabase para visualizar as estatísticas do sistema de teatro.

## Conexão com o Banco de Dados

1. Acesse o Metabase (http://localhost:3000 por padrão)
2. Vá em "Admin" > "Databases"
3. Clique em "Add database"
4. Selecione "MySQL"
5. Configure a conexão:
    - Name: Teatro DB
    - Host: localhost
    - Port: 3306
    - Database name: teatro_db
    - Username: root (ou seu usuário configurado)
    - Password: root (ou sua senha configurada)
    - SSL: Desativado (para ambiente de desenvolvimento)

## Dashboards Recomendados

### 1. Dashboard de Vendas por Peça

**Visualizações:**

1. Gráfico de Barras: Total de Ingressos Vendidos por Peça

    - Query: `SELECT * FROM estatisticas_vendas_peca`
    - X-axis: nome_peca
    - Y-axis: total_ingressos_vendidos
    - Tipo de Gráfico: Bar Chart
    - Ordenação: Descendente por total_ingressos_vendidos

2. Gráfico de Pizza: Distribuição de Faturamento por Peça
    - Query: `SELECT * FROM estatisticas_vendas_peca`
    - Categorias: nome_peca
    - Valores: faturamento_total
    - Tipo de Gráfico: Pie Chart
    - Formatação: Valores em Reais (R$)

### 2. Dashboard de Ocupação

**Visualizações:**

1. Gráfico de Barras: Ocupação por Sessão

    - Query: `SELECT * FROM estatisticas_ocupacao_sessao`
    - X-axis: nome_peca
    - Y-axis: percentual_ocupacao
    - Agrupar por: data_sessao, horario
    - Tipo de Gráfico: Bar Chart
    - Formatação: Percentual com 2 casas decimais

2. Tabela Detalhada: Ocupação por Sessão
    - Query: `SELECT * FROM estatisticas_ocupacao_sessao`
    - Colunas:
        - nome_peca (Texto)
        - data_sessao (Data)
        - horario (Texto)
        - ingressos_vendidos (Número)
        - total_poltronas (Número)
        - percentual_ocupacao (Percentual)

### 3. Dashboard de Faturamento

**Visualizações:**

1. Gráfico de Barras Empilhadas: Faturamento por Área e Peça

    - Query: `SELECT * FROM estatisticas_faturamento_area_peca`
    - X-axis: nome_peca
    - Y-axis: faturamento_total
    - Agrupar por: nome_area
    - Tipo de Gráfico: Stacked Bar Chart
    - Formatação: Valores em Reais (R$)

2. Gráfico de Linha: Evolução do Faturamento por Período
    - Query: `SELECT * FROM estatisticas_vendas_periodo`
    - X-axis: mes
    - Y-axis: faturamento_total
    - Agrupar por: nome_peca
    - Tipo de Gráfico: Line Chart
    - Formatação:
        - Eixo X: Mês/Ano
        - Eixo Y: Valores em Reais (R$)

### 4. Dashboard de Análise Comparativa

**Visualizações:**

1. Gráfico de Dispersão: Relação entre Ocupação e Faturamento

    - Query:
        ```sql
        SELECT
            e.nome_peca,
            AVG(e.percentual_ocupacao) as media_ocupacao,
            SUM(f.faturamento_total) as faturamento_total
        FROM
            estatisticas_ocupacao_sessao e
            JOIN estatisticas_faturamento_area_peca f ON f.nome_peca = e.nome_peca
        GROUP BY
            e.nome_peca
        ```
    - X-axis: media_ocupacao
    - Y-axis: faturamento_total
    - Tipo de Gráfico: Scatter Plot
    - Formatação:
        - Eixo X: Percentual
        - Eixo Y: Valores em Reais (R$)

2. Tabela de Métricas Principais
    - Query:
        ```sql
        SELECT
            nome_peca,
            MAX(total_ingressos_vendidos) as max_ingressos,
            MIN(total_ingressos_vendidos) as min_ingressos,
            MAX(faturamento_total) as max_faturamento,
            MIN(faturamento_total) as min_faturamento,
            AVG(valor_medio_ingresso) as ticket_medio
        FROM
            estatisticas_vendas_peca
        GROUP BY
            nome_peca
        ```
    - Colunas:
        - nome_peca (Texto)
        - max_ingressos (Número)
        - min_ingressos (Número)
        - max_faturamento (Moeda)
        - min_faturamento (Moeda)
        - ticket_medio (Moeda)

## Filtros Recomendados

Para cada dashboard, adicione os seguintes filtros:

1. Período (Data)

    - Aplicável em: data_sessao, mes
    - Tipo: Date Range
    - Formato: DD/MM/YYYY

2. Peça

    - Aplicável em: nome_peca
    - Tipo: Dropdown
    - Fonte: Query `SELECT DISTINCT nome_peca FROM estatisticas_vendas_peca`

3. Área

    - Aplicável em: nome_area
    - Tipo: Dropdown
    - Fonte: Query `SELECT DISTINCT nome_area FROM estatisticas_faturamento_area_peca`

4. Tipo de Sessão
    - Aplicável em: horario
    - Tipo: Dropdown
    - Fonte: Query `SELECT DISTINCT horario FROM estatisticas_ocupacao_sessao`

## Dicas de Visualização

1. Use cores consistentes para cada peça em todos os gráficos
2. Adicione tooltips informativos em todos os gráficos
3. Configure alertas para:
    - Ocupação abaixo de 30%
    - Faturamento abaixo da média mensal
    - Vendas abaixo do esperado

## Atualização dos Dados

As views são atualizadas automaticamente quando novos dados são inseridos no banco. Para garantir performance:

1. Configure o Metabase para atualizar os dados a cada 5 minutos
2. Mantenha os índices do banco de dados otimizados
3. Monitore o tempo de execução das queries

## Dicas Específicas para MySQL

1. Certifique-se de que o usuário do MySQL tem permissões adequadas:

    ```sql
    GRANT SELECT ON teatro_db.* TO 'seu_usuario'@'localhost';
    FLUSH PRIVILEGES;
    ```

2. Para melhor performance, adicione índices nas colunas mais consultadas:

    ```sql
    ALTER TABLE ingressos ADD INDEX idx_data_compra (data_compra);
    ALTER TABLE ingressos ADD INDEX idx_sessao_id (sessao_id);
    ALTER TABLE ingressos ADD INDEX idx_evento_id (evento_id);
    ```

3. Configure o Metabase para usar o timezone correto:
    - Vá em Admin > Settings > General
    - Defina o timezone para "America/Sao_Paulo"
