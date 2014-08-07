CREATE TABLE authorized_program (
  tenantId BIGINT NOT NULL,
  id BIGINT NOT NULL,
  programName VARCHAR(255) NOT NULL,
  programSecurityToken VARCHAR(512) NOT NULL,
  UNIQUE (tenantId, programName),
  PRIMARY KEY (tenantId, id)
) ENGINE = INNODB;

INSERT INTO sequence (tenantid, id, nextid)
SELECT ID, 10019, 1 FROM tenant
ORDER BY id ASC;