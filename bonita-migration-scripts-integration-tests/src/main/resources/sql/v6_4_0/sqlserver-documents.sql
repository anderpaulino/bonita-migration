CREATE TABLE tenant (
  id NUMERIC(19, 0) NOT NULL,
  PRIMARY KEY (id)
)@@
CREATE TABLE sequence (
  tenantid NUMERIC(19, 0) NOT NULL,
  id NUMERIC(19, 0) NOT NULL,
  nextid NUMERIC(19, 0) NOT NULL,
  PRIMARY KEY (tenantid, id)
)@@
CREATE TABLE document_mapping (
  tenantid NUMERIC(19, 0) NOT NULL,
  id NUMERIC(19, 0) NOT NULL,
  processinstanceid NUMERIC(19, 0),
  documentName NVARCHAR(50) NOT NULL,
  documentAuthor NUMERIC(19, 0),
  documentCreationDate NUMERIC(19, 0) NOT NULL,
  documentHasContent BIT NOT NULL,
  documentContentFileName NVARCHAR(255),
  documentContentMimeType NVARCHAR(255),
  contentStorageId NVARCHAR(50),
  documentURL NVARCHAR(255),
  PRIMARY KEY (tenantid, ID)
)@@
CREATE TABLE arch_document_mapping (
    tenantid NUMERIC(19, 0) NOT NULL,
    id NUMERIC(19, 0) NOT NULL,
    processinstanceid NUMERIC(19, 0),
    sourceObjectId NUMERIC(19, 0),
    documentName NVARCHAR(50) NOT NULL,
    documentAuthor NUMERIC(19, 0),
    documentCreationDate NUMERIC(19, 0) NOT NULL,
    documentHasContent BIT NOT NULL,
    documentContentFileName NVARCHAR(255),
    documentContentMimeType NVARCHAR(255),
    contentStorageId NVARCHAR(50),
    documentURL NVARCHAR(255),
    archiveDate NUMERIC(19, 0) NOT NULL,
    PRIMARY KEY (tenantid, ID)
)@@
CREATE TABLE document_content (
  tenantid NUMERIC(19, 0) NOT NULL,
  id NUMERIC(19, 0) NOT NULL,
  documentId NVARCHAR(50) NOT NULL,
  content VARBINARY(MAX) NULL,
  PRIMARY KEY (tenantid, id)
)@@
ALTER TABLE document_content ADD CONSTRAINT fk_document_content_tenantId FOREIGN KEY (tenantid) REFERENCES tenant(id)@@