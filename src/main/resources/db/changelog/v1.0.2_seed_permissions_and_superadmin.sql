-- Liquibase changeset para v1.0.2 (Corrigido para MySQL)
-- Autor: shin (seed)
-- Objetivo: Criar permissões, SUPER_ADMIN e associar tudo.

-- 1. Cria a tabela 'permissions' (se ainda não existir)
CREATE TABLE IF NOT EXISTS permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 2. Cria a tabela de junção 'roles_permissions' (se ainda não existir)
CREATE TABLE IF NOT EXISTS roles_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- 3. Inserir as PERMISSÕES
-- (Usa INSERT IGNORE para sintaxe MySQL - ignora se a chave única já existir)
INSERT IGNORE INTO permissions (permission_name, description) VALUES
('MANAGE_USERS', 'Permite convidar, editar e remover usuários da empresa'),
('READ_REPORTS', 'Permite visualizar os relatórios financeiros'),
('MANAGE_BILLING', 'Permite gerenciar assinaturas e pagamentos'),
('VIEW_DASHBOARD', 'Permite visualizar o dashboard principal');

-- 4. Inserir os PAPÉIS (Roles)
-- (Usa INSERT IGNORE para sintaxe MySQL)
INSERT IGNORE INTO roles (role_name) VALUES
('SUPER_ADMIN'),
('COMPANY_ADMIN'),
('COMPANY_MEMBER'),
('COMPANY_VIEWER');

-- 5. Ligar os Papéis às Permissões
-- (Usa INSERT IGNORE para sintaxe MySQL)
-- (Usando sub-selects para não depender de IDs fixos)
INSERT IGNORE INTO roles_permissions (role_id, permission_id) VALUES
((SELECT id FROM roles WHERE role_name = 'COMPANY_ADMIN'), (SELECT id FROM permissions WHERE permission_name = 'MANAGE_USERS')),
((SELECT id FROM roles WHERE role_name = 'COMPANY_ADMIN'), (SELECT id FROM permissions WHERE permission_name = 'READ_REPORTS')),
((SELECT id FROM roles WHERE role_name = 'COMPANY_ADMIN'), (SELECT id FROM permissions WHERE permission_name = 'MANAGE_BILLING')),
((SELECT id FROM roles WHERE role_name = 'COMPANY_ADMIN'), (SELECT id FROM permissions WHERE permission_name = 'VIEW_DASHBOARD')),

((SELECT id FROM roles WHERE role_name = 'COMPANY_MEMBER'), (SELECT id FROM permissions WHERE permission_name = 'VIEW_DASHBOARD')),

((SELECT id FROM roles WHERE role_name = 'COMPANY_VIEWER'), (SELECT id FROM permissions WHERE permission_name = 'READ_REPORTS')),
((SELECT id FROM roles WHERE role_name = 'COMPANY_VIEWER'), (SELECT id FROM permissions WHERE permission_name = 'VIEW_DASHBOARD'));


-- 6. DADOS DE TESTE PARA O SUPER_ADMIN (CRÍTICO)
-- ---------------------------------------------------------------------------------
INSERT IGNORE INTO users (name, email, password_hash, status, created_at, updated_at) VALUES
('Super Admin', 'superadmin@grape.com', '$2a$10$tOKLTN8g3MUeBgq/0N0LK.ArqEclNQmBVjj0p53n3nqQxOZ0zeOk2', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Criar uma "empresa" fictícia chamada "Grape HQ" para o admin
INSERT IGNORE INTO companies (trading_name, legal_name, tax_id, status, created_at, updated_at) VALUES
('Grape HQ', 'Grape Access Manager HQ', '00000000000000', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Vincular o superadmin à Grape HQ com o papel SUPER_ADMIN
INSERT IGNORE INTO company_members (user_id, company_id, role_id, status, created_at, updated_at) VALUES
(
  (SELECT id from users WHERE email = 'superadmin@grape.com'),
  (SELECT id from companies WHERE tax_id = '00000000000000'),
  (SELECT id from roles WHERE role_name = 'SUPER_ADMIN'),
  'ACTIVE',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);