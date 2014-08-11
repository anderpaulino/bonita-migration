CREATE TABLE business_app (
  tenantId NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  name VARCHAR2(50) NOT NULL,
  version VARCHAR2(50) NOT NULL,
  path VARCHAR2(255) NOT NULL,
  description VARCHAR2(1024),
  iconPath VARCHAR2(255),
  creationDate NUMBER(19, 0) NOT NULL,
  createdBy NUMBER(19, 0) NOT NULL,
  lastUpdateDate NUMBER(19, 0) NOT NULL,
  updatedBy NUMBER(19, 0) NOT NULL,
  state VARCHAR2(30) NOT NULL,
  homePageId NUMBER(19, 0),
  displayName VARCHAR2(255),
  UNIQUE (tenantId, name, version),
  PRIMARY KEY (tenantId, id)
)
@@

ALTER TABLE business_app ADD CONSTRAINT fk_app_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id)
@@
CREATE INDEX idx_app_name ON business_app (name, tenantid)
@@

CREATE TABLE business_app_page (
  tenantId NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  applicationId NUMBER(19, 0) NOT NULL,
  pageId NUMBER(19, 0) NOT NULL,
  name VARCHAR2(255),
  UNIQUE (tenantId, applicationId, name),
  PRIMARY KEY (tenantId, id)
)
@@

CREATE INDEX idx_app_page_name ON business_app_page (applicationId, name, tenantid)
@@
ALTER TABLE business_app_page ADD CONSTRAINT fk_app_page_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id)
@@
ALTER TABLE business_app_page ADD CONSTRAINT fk_bus_app_id FOREIGN KEY (tenantid, applicationId) REFERENCES business_app (tenantid, id) ON DELETE CASCADE
@@
ALTER TABLE business_app_page ADD CONSTRAINT fk_page_id FOREIGN KEY (tenantid, pageId) REFERENCES page (tenantid, id)
@@

INSERT INTO "SEQUENCE" ("TENANTID", "ID", "NEXTID")
	SELECT "ID", 10200, 1 FROM "TENANT"
	ORDER BY "ID" ASC 
@@
	
INSERT INTO "SEQUENCE" ("TENANTID", "ID", "NEXTID")
	SELECT "ID", 10201, 1 FROM "TENANT"
	ORDER BY "ID" ASC 
@@