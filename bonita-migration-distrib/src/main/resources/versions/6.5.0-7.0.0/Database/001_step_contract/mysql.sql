CREATE TABLE contract_data (
  tenantid BIGINT NOT NULL,
  id BIGINT NOT NULL,
  scopeId BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  val LONGBLOB,
) ENGINE = INNODB
@@
ALTER TABLE contract_data ADD CONSTRAINT pk_contract_data PRIMARY KEY (tenantid, id)
@@
ALTER TABLE contract_data ADD CONSTRAINT uc_cd_scope_name UNIQUE (scopeId, name, tenantid)
@@
CREATE INDEX idx_cd_scope_name ON contract_data (scopeId, name, tenantid)
@@

CREATE TABLE arch_contract_data (
  tenantid BIGINT NOT NULL,
  id BIGINT NOT NULL,
  scopeId BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  val LONGBLOB,
  archiveDate BIGINT NOT NULL,
  sourceObjectId BIGINT NOT NULL,
) ENGINE = INNODB
@@
ALTER TABLE arch_contract_data ADD CONSTRAINT pk_arch_contract_data PRIMARY KEY (tenantid, id)
@@
ALTER TABLE arch_contract_data ADD CONSTRAINT uc_acd_scope_name UNIQUE (scopeId, name, tenantid)
@@
CREATE INDEX idx_acd_scope_name ON arch_contract_data (scopeId, name, tenantid)
@@
