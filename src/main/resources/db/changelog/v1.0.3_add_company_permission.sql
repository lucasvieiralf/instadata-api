-- liquibase formatted sql
-- changeset shin:v1.0.3-add-company-settings-permission

-- 1. Adiciona a nova permissão
INSERT IGNORE INTO permissions (permission_name, description)
VALUES ('MANAGE_COMPANY_SETTINGS', 'Permite editar as informações da própria empresa (ex: nome, CNPJ)');

-- 2. Vincula a nova permissão ao papel de COMPANY_ADMIN (vamos supor que o ID dele é 1)
-- (O INSERT IGNORE previne erros se a relação já existir)
INSERT IGNORE INTO roles_permissions (role_id, permission_id)
SELECT
    (SELECT id FROM roles WHERE role_name = 'COMPANY_ADMIN'),
    (SELECT id FROM permissions WHERE permission_name = 'MANAGE_COMPANY_SETTINGS');