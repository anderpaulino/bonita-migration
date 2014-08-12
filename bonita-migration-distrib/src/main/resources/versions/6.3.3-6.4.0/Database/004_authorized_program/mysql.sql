CREATE TABLE authorized_program (
  tenantId BIGINT NOT NULL,
  id BIGINT NOT NULL,
  programName VARCHAR(255) NOT NULL,
  programSecurityToken VARCHAR(512) NOT NULL,
  creationDate TIMESTAMP NOT NULL,
  UNIQUE (tenantId, programName),
  PRIMARY KEY (tenantId, id)
) ENGINE = INNODB;

INSERT INTO sequence (tenantid, id, nextid)
SELECT ID, 514, 1 FROM tenant
ORDER BY id ASC;