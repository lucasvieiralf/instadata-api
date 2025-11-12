-- -----------------------------------------------------
-- Table `plans`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `plans` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `plan_name` VARCHAR(100) NOT NULL,
  `monthly_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `plan_name_idx` (`plan_name` ASC) VISIBLE)
COMMENT = 'Planos de assinatura oferecidos às empresas (Ex: Básico, Premium)';


-- -----------------------------------------------------
-- Table `features`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `features` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `feature_key` VARCHAR(50) NOT NULL COMMENT 'Chave interna para o backend (Ex: USER_LIMIT)',
  `display_name` VARCHAR(255) NOT NULL COMMENT 'Nome amigável para UI (Ex: Limite de Usuários)',
  `data_type` ENUM('NUMERIC', 'BOOLEAN', 'TEXT') NOT NULL DEFAULT 'NUMERIC',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `feature_key_idx` (`feature_key` ASC) VISIBLE)
COMMENT = 'Lista mestra de funcionalidades que podem ser limitadas por um plano';


-- -----------------------------------------------------
-- Table `plan_limits`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `plan_limits` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `plan_id` INT NOT NULL,
  `feature_id` INT NOT NULL,
  `limit_value` VARCHAR(255) NOT NULL COMMENT 'O valor do limite (Ex: \"5\", \"1000\", \"true\")',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_plan_feature` (`plan_id` ASC, `feature_id` ASC) VISIBLE,
  INDEX `plan_id_idx` (`plan_id` ASC) VISIBLE,
  INDEX `feature_id_idx` (`feature_id` ASC) VISIBLE,
  CONSTRAINT `fk_plan_limits_plan`
    FOREIGN KEY (`plan_id`)
    REFERENCES `plans` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_plan_limits_feature`
    FOREIGN KEY (`feature_id`)
    REFERENCES `features` (`id`)
    ON DELETE RESTRICT)
COMMENT = 'Define os limites/valores específicos para cada funcionalidade em um plano';


-- -----------------------------------------------------
-- Table `companies`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `companies` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `trading_name` VARCHAR(255) NOT NULL COMMENT 'Nome fantasia da empresa',
  `legal_name` VARCHAR(255) NULL DEFAULT NULL COMMENT 'Razão social',
  `tax_id` VARCHAR(20) NULL DEFAULT NULL COMMENT 'CNPJ ou outro identificador fiscal',
  `status` ENUM('ACTIVE', 'INACTIVE', 'TRIAL', 'CANCELED') NOT NULL DEFAULT 'ACTIVE',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `tax_id_idx` (`tax_id` ASC) VISIBLE)
COMMENT = 'Os tenants (clientes) que assinam o serviço';


-- -----------------------------------------------------
-- Table `subscriptions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `subscriptions` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `company_id` INT NOT NULL,
  `plan_id` INT NOT NULL,
  `starts_at` DATE NOT NULL COMMENT 'Data de início da assinatura',
  `next_billing_at` DATE NOT NULL COMMENT 'Data da próxima cobrança',
  `status` ENUM('ACTIVE', 'CANCELED', 'PAST_DUE') NOT NULL DEFAULT 'ACTIVE' COMMENT 'Status do pagamento/assinatura',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `status_idx` (`status` ASC) VISIBLE,
  INDEX `next_billing_at_idx` (`next_billing_at` ASC) VISIBLE,
  INDEX `company_id_idx` (`company_id` ASC) VISIBLE,
  INDEX `plan_id_idx` (`plan_id` ASC) VISIBLE,
  CONSTRAINT `fk_subscriptions_company`
    FOREIGN KEY (`company_id`)
    REFERENCES `companies` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_subscriptions_plan`
    FOREIGN KEY (`plan_id`)
    REFERENCES `plans` (`id`)
    ON DELETE RESTRICT)
COMMENT = 'Vincula uma empresa a um plano, controlando o status da cobrança';


-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `status` ENUM('ACTIVE', 'INACTIVE', 'PENDING_VERIFICATION') NOT NULL DEFAULT 'PENDING_VERIFICATION',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_idx` (`email` ASC) VISIBLE)
COMMENT = 'Armazena todos os usuários de todas as empresas (agnóstico)';


-- -----------------------------------------------------
-- Table `roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL COMMENT 'Ex: COMPANY_ADMIN, NEXOS_USER',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `role_name_idx` (`role_name` ASC) VISIBLE)
COMMENT = 'Papéis de permissão dentro de uma empresa';


-- -----------------------------------------------------
-- Table `company_members`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `company_members` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `company_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  `status` ENUM('PENDING_INVITE', 'ACTIVE', 'DISABLED') NOT NULL DEFAULT 'PENDING_INVITE',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_company` (`user_id` ASC, `company_id` ASC) VISIBLE,
  INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
  INDEX `company_id_idx` (`company_id` ASC) VISIBLE,
  INDEX `role_id_idx` (`role_id` ASC) VISIBLE,
  CONSTRAINT `fk_company_members_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_company_members_company`
    FOREIGN KEY (`company_id`)
    REFERENCES `companies` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_company_members_role`
    FOREIGN KEY (`role_id`)
    REFERENCES `roles` (`id`)
    ON DELETE RESTRICT)
COMMENT = 'Vincula usuários a empresas, definindo seu papel';


-- -----------------------------------------------------
-- Table `platforms`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `platforms` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `platform_name` VARCHAR(150) NOT NULL,
  `platform_key` VARCHAR(50) NOT NULL COMMENT 'Chave interna para o backend (Ex: NEXOS)',
  `status` ENUM('ONLINE', 'OFFLINE', 'MAINTENANCE') NOT NULL DEFAULT 'ONLINE',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `platform_key_idx` (`platform_key` ASC) VISIBLE)
COMMENT = 'Lista todos os sistemas externos (como Nexos) com os quais podemos integrar';


-- -----------------------------------------------------
-- Table `member_platform_access`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `member_platform_access` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `company_member_id` INT NOT NULL COMMENT 'Refere-se ao ID da tabela company_members',
  `platform_id` INT NOT NULL,
  `access_token` VARCHAR(255) NOT NULL COMMENT 'O token único para este usuário nesta plataforma',
  `status` ENUM('ACTIVE', 'REVOKED') NOT NULL DEFAULT 'ACTIVE',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `access_token_idx` (`access_token` ASC) VISIBLE,
  UNIQUE INDEX `uk_member_platform` (`company_member_id` ASC, `platform_id` ASC) VISIBLE,
  INDEX `company_member_id_idx` (`company_member_id` ASC) VISIBLE,
  INDEX `platform_id_idx` (`platform_id` ASC) VISIBLE,
  CONSTRAINT `fk_member_platform_access_member`
    FOREIGN KEY (`company_member_id`)
    REFERENCES `company_members` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_member_platform_access_platform`
    FOREIGN KEY (`platform_id`)
    REFERENCES `platforms` (`id`)
    ON DELETE CASCADE)
COMMENT = 'Armazena os tokens de acesso de cada usuário para cada plataforma';


-- -----------------------------------------------------
-- Table `activity_logs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `activity_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'BIGINT pois esta tabela crescerá muito',
  `user_id` INT NULL DEFAULT NULL COMMENT 'Quem fez a ação (NULL se for ação do sistema)',
  `company_id` INT NULL DEFAULT NULL COMMENT 'Em qual empresa (NULL se for ação de plataforma)',
  `action_key` VARCHAR(100) NOT NULL COMMENT 'Chave da ação (Ex: USER.INVITE, PLAN.CHANGE)',
  `description` TEXT NOT NULL COMMENT 'Descrição humana da ação',
  `ip_address` VARCHAR(45) NULL DEFAULT NULL COMMENT 'IP de origem da requisição',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
  INDEX `company_id_idx` (`company_id` ASC) VISIBLE,
  INDEX `action_key_idx` (`action_key` ASC) VISIBLE,
  INDEX `created_at_idx` (`created_at` ASC) VISIBLE,
  CONSTRAINT `fk_activity_logs_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_activity_logs_company`
    FOREIGN KEY (`company_id`)
    REFERENCES `companies` (`id`)
    ON DELETE SET NULL)
COMMENT = 'Log de auditoria para todas as ações importantes na plataforma';