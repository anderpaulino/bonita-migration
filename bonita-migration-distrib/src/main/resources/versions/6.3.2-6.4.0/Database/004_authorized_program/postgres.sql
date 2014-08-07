CREATE TABLE authorized_program (
  tenantId INT8 NOT NULL,
  id INT8 NOT NULL,
  programName VARCHAR(255) NOT NULL,
  programSecurityToken VARCHAR(512) NOT NULL,
  UNIQUE (tenantId, programName),
  PRIMARY KEY (tenantId, id)
);
INSERT INTO sequence (tenantid, id, nextid)
SELECT ID, 514, 1 FROM tenant
ORDER BY id ASC;
