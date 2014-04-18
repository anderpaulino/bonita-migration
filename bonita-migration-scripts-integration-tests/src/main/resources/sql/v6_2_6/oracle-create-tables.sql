CREATE TABLE arch_process_instance (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  name VARCHAR2(75) NOT NULL,
  processDefinitionId NUMBER(19, 0) NOT NULL,
  description VARCHAR2(255),
  startDate NUMBER(19, 0) NOT NULL,
  startedBy NUMBER(19, 0) NOT NULL,
  startedByDelegate NUMBER(19, 0) NULL,
  endDate NUMBER(19, 0) NOT NULL,
  archiveDate NUMBER(19, 0) NOT NULL,
  stateId INT NOT NULL,
  lastUpdate NUMBER(19, 0) NOT NULL,
  rootProcessInstanceId NUMBER(19, 0),
  callerId NUMBER(19, 0),
  migration_plan NUMBER(19, 0),
  sourceObjectId NUMBER(19, 0) NOT NULL,
  stringIndex1 VARCHAR2(50),
  stringIndex2 VARCHAR2(50),
  stringIndex3 VARCHAR2(50),
  stringIndex4 VARCHAR2(50),
  stringIndex5 VARCHAR2(50),
  PRIMARY KEY (tenantid, id)
)

/

CREATE TABLE arch_flownode_instance (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  flownodeDefinitionId NUMBER(19, 0) NOT NULL,
  kind VARCHAR2(25) NOT NULL,
  sourceObjectId NUMBER(19, 0),
  archiveDate NUMBER(19, 0) NOT NULL,
  rootContainerId NUMBER(19, 0) NOT NULL,
  parentContainerId NUMBER(19, 0) NOT NULL,
  name VARCHAR2(50) NOT NULL,
  displayName VARCHAR2(75),
  displayDescription VARCHAR2(255),
  stateId INT NOT NULL,
  stateName VARCHAR2(50),
  terminal NUMBER(1) NOT NULL,
  stable NUMBER(1) ,
  actorId NUMBER(19, 0) NULL,
  assigneeId NUMBER(19, 0) DEFAULT 0 NOT NULL,
  reachedStateDate NUMBER(19, 0),
  lastUpdateDate NUMBER(19, 0),
  expectedEndDate NUMBER(19, 0),
  claimedDate NUMBER(19, 0),
  priority SMALLINT,
  gatewayType VARCHAR2(50),
  hitBys VARCHAR2(255),
  logicalGroup1 NUMBER(19, 0) NOT NULL,
  logicalGroup2 NUMBER(19, 0) NOT NULL,
  logicalGroup3 NUMBER(19, 0),
  logicalGroup4 NUMBER(19, 0) NOT NULL,
  loop_counter INT,
  loop_max INT,
  loopCardinality INT,
  loopDataInputRef VARCHAR2(255),
  loopDataOutputRef VARCHAR2(255),
  description VARCHAR2(255),
  sequential NUMBER(1),
  dataInputItemRef VARCHAR2(255),
  dataOutputItemRef VARCHAR2(255),
  nbActiveInst INT,
  nbCompletedInst INT,
  nbTerminatedInst INT,
  executedBy NUMBER(19, 0),
  executedByDelegate NUMBER(19, 0),
  activityInstanceId NUMBER(19, 0),
  aborting NUMBER(1) NOT NULL,
  triggeredByEvent NUMBER(1),
  interrupting NUMBER(1),
  PRIMARY KEY (tenantid, id)
)

/

CREATE TABLE arch_transition_instance (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  rootContainerId NUMBER(19, 0) NOT NULL,
  parentContainerId NUMBER(19, 0) NOT NULL,
  name VARCHAR2(255) NOT NULL,
  source NUMBER(19, 0),
  target NUMBER(19, 0),
  state VARCHAR2(50),
  terminal NUMBER(1) NOT NULL,
  stable NUMBER(1) ,
  stateCategory VARCHAR2(50) NOT NULL,
  logicalGroup1 NUMBER(19, 0) NOT NULL,
  logicalGroup2 NUMBER(19, 0) NOT NULL,
  logicalGroup3 NUMBER(19, 0),
  logicalGroup4 NUMBER(19, 0) NOT NULL,
  description VARCHAR2(255),
  sourceObjectId NUMBER(19, 0),
  archiveDate NUMBER(19, 0) NOT NULL,
  PRIMARY KEY (tenantid, id)
)

/

CREATE TABLE arch_connector_instance (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  containerId NUMBER(19, 0) NOT NULL,
  containerType VARCHAR2(10) NOT NULL,
  connectorId VARCHAR2(255) NOT NULL,
  version VARCHAR2(10) NOT NULL,
  name VARCHAR2(255) NOT NULL,
  activationEvent VARCHAR2(30),
  state VARCHAR2(50),
  sourceObjectId NUMBER(19, 0),
  archiveDate NUMBER(19, 0) NOT NULL,
  PRIMARY KEY (tenantid, id)
)

/

CREATE TABLE arch_data_instance (
    tenantId NUMBER(19, 0) NOT NULL,
	id NUMBER(19, 0) NOT NULL,
	name VARCHAR2(50),
	description VARCHAR2(50),
	transientData NUMBER(1),
	className VARCHAR2(100),
	containerId NUMBER(19, 0),
	containerType VARCHAR2(60),
	namespace VARCHAR2(100),
	element VARCHAR2(60),
	intValue INT,
	longValue NUMBER(19, 0),
	shortTextValue VARCHAR2(255),
	booleanValue NUMBER(1),
	doubleValue NUMERIC(19,5),
	floatValue REAL,
	blobValue BLOB,
	clobValue CLOB,
	discriminant VARCHAR2(50) NOT NULL,
	archiveDate NUMBER(19, 0) NOT NULL,
	sourceObjectId NUMBER(19, 0) NOT NULL,
	PRIMARY KEY (tenantid, id)
)

/

CREATE TABLE arch_data_mapping (
    tenantid NUMBER(19, 0) NOT NULL,
	id NUMBER(19, 0) NOT NULL,
	containerId NUMBER(19, 0),
	containerType VARCHAR2(60),
	dataName VARCHAR2(50),
	dataInstanceId NUMBER(19, 0) NOT NULL,
	archiveDate NUMBER(19, 0) NOT NULL,
	sourceObjectId NUMBER(19, 0) NOT NULL,
	PRIMARY KEY (tenantid, id)
)

/