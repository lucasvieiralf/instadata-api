-- liquibase formatted sql
-- changeset shin:v1.0.4-fix-subscription-status-length

-- Altera a coluna 'status' na tabela 'subscriptions' para um tamanho seguro (VARCHAR(50))
-- Isso previne o erro "Data truncated" ao salvar enums como "TRIALING"
ALTER TABLE `subscriptions`
MODIFY COLUMN `status` VARCHAR(50) NOT NULL;