CREATE TABLE tenant (
  id BIGINT NOT NULL,
  PRIMARY KEY (id)
) ENGINE = INNODB;

CREATE TABLE sequence (
  tenantid BIGINT NOT NULL,
  id BIGINT NOT NULL,
  nextid BIGINT NOT NULL,
  PRIMARY KEY (tenantid, id)
) ENGINE = INNODB;

CREATE TABLE command (
  tenantid BIGINT NOT NULL,
  id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  description TEXT,
  IMPLEMENTATION VARCHAR(100) NOT NULL,
  system BOOLEAN,
  UNIQUE (tenantid, name),
  PRIMARY KEY (tenantid, id)
) ENGINE = INNODB;

