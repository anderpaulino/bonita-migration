CREATE TABLE tenant (
  id NUMBER(19, 0) NOT NULL,
  PRIMARY KEY (id)
)@@


CREATE TABLE sequence (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  nextid NUMBER(19, 0) NOT NULL,
  PRIMARY KEY (tenantid, id)
)@@

CREATE TABLE profile (
  tenantId NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  isDefault NUMBER(1) NOT NULL,
  name VARCHAR2(50) NOT NULL,
  description VARCHAR2(1024),
  creationDate NUMBER(19, 0) NOT NULL,
  createdBy NUMBER(19, 0) NOT NULL,
  lastUpdateDate NUMBER(19, 0) NOT NULL,
  lastUpdatedBy NUMBER(19, 0) NOT NULL,
  UNIQUE (tenantId, name),
  PRIMARY KEY (tenantId, id)
)@@

CREATE TABLE page (
  tenantId NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  name VARCHAR2(50) NOT NULL,
  displayName VARCHAR2(255) NOT NULL,
  description VARCHAR2(1024),
  installationDate NUMBER(19, 0) NOT NULL,
  installedBy NUMBER(19, 0) NOT NULL,
  provided NUMBER(1),
  lastModificationDate NUMBER(19, 0) NOT NULL,
  lastUpdatedBy NUMBER(19, 0) NOT NULL,
  contentName VARCHAR2(50) NOT NULL,
  content BLOB,
  UNIQUE (tenantId, name),
  PRIMARY KEY (tenantId, id)
)
