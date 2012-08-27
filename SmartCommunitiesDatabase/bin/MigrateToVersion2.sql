--
--  This SQL script migrates the SmartCommunities database to the new database structure.
--
--  The changes are:
--
--  +------------------------+-----------+----------------------------------------------------------+
--  | Table                  | Status    | Comments                                                 |
--  +------------------------+-----------+----------------------------------------------------------+
--  | AbsoluteLocation       | New       | No data                                                  |
--  | Locality               | No change |                                                          |
--  | Measurement            | Changed   | Replacing localityId and measurementTypeId with sensorId |
--  | MeasurementType        | No change |                                                          |
--  | Sensor                 | New       | Need to generate data                                    |
--  | SensorPlatform         | New       | Need to generate data                                    |
--  | SensorPlatformLocation | New       | Need to generate data                                    |
--  | SymbolicLocation       | New       | Need to generate data                                    |
--  +------------------------+-----------+----------------------------------------------------------+

--  Gather some temporary data

DROP TABLE IF EXISTS TemporarySensor;
CREATE TEMPORARY TABLE TemporarySensor
   (
   localityId               BIGINT NOT NULL,
   measurementTypeId        BIGINT NOT NULL,
   sensorId                 BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
   sensorPlatformId         BIGINT
   );

INSERT
  INTO TemporarySensor (localityId, measurementTypeId)
SELECT DISTINCT localityId,
                measurementTypeId
  FROM Measurement;

DROP TABLE IF EXISTS TemporarySensorPlatform;
CREATE TEMPORARY TABLE TemporarySensorPlatform
   (
   localityId               BIGINT NOT NULL,
   sensorPlatformId         BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT
   );

INSERT
  INTO TemporarySensorPlatform (localityId)
SELECT DISTINCT localityId
  FROM TemporarySensor;

UPDATE TemporarySensor
   SET sensorPlatformId = (SELECT sensorPlatformId
                             FROM TemporarySensorPlatform
                            WHERE TemporarySensorPlatform.localityId = TemporarySensor.localityId);

DROP TABLE IF EXISTS TemporarySensorPlatformLocation;
CREATE TEMPORARY TABLE TemporarySensorPlatformLocation
   (
   localityId               BIGINT NOT NULL,
   sensorPlatformId         BIGINT NOT NULL,
   sensorPlatformLocationId BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT
   );

INSERT
  INTO TemporarySensorPlatformLocation (localityId, sensorPlatformId)
SELECT localityId,
       sensorPlatformId
  FROM TemporarySensorPlatform;

DROP TABLE IF EXISTS TemporarySymbolicLocation;
CREATE TEMPORARY TABLE TemporarySymbolicLocation
   (
   localityId               BIGINT NOT NULL,
   sensorPlatformLocationId BIGINT NOT NULL
   );

INSERT
  INTO TemporarySymbolicLocation
SELECT localityId,
       sensorPlatformLocationId
  FROM TemporarySensorPlatformLocation;

--  Create the new tables

alter table AbsoluteLocation
    drop
    foreign key FK7E61EE6C9A34AAE2;

alter table Sensor
    drop
    foreign key FK93653FDA2B99ED98;

alter table Sensor
    drop
    foreign key FK93653FDA697DCEC4;

alter table SensorPlatformLocation
    drop
    foreign key FKB52FD9422B99ED98;

alter table SymbolicLocation
    drop
    foreign key FK38D9F2A7601838A4;

alter table SymbolicLocation
    drop
    foreign key FK38D9F2A79A34AAE2;

drop table if exists AbsoluteLocation;

drop table if exists Sensor;

drop table if exists SensorPlatform;

drop table if exists SensorPlatformLocation;

drop table if exists SymbolicLocation;

create table AbsoluteLocation (
    sensorPlatformLocationId bigint not null,
    altitude double precision not null,
    latitude double precision not null,
    longitude double precision not null,
    primary key (sensorPlatformLocationId)
);
 
create table Sensor (
    sensorId bigint not null auto_increment,
    version integer not null,
    measurementTypeId bigint,
    sensorPlatformId bigint,
    primary key (sensorId)
);
 
create table SensorPlatform (
    sensorPlatformId bigint not null auto_increment,
    version integer not null,
    macAddress varchar(12),
    primary key (sensorPlatformId)
);
 
create table SensorPlatformLocation (
    sensorPlatformLocationId bigint not null auto_increment,
    version integer not null,
    beginDateTime datetime not null,
    endDateTime datetime,
    sensorPlatformId bigint not null,
    primary key (sensorPlatformLocationId)
);
 
create table SymbolicLocation (
    sensorPlatformLocationId bigint not null,
    localityId bigint not null,
    primary key (sensorPlatformLocationId)
);
 
alter table AbsoluteLocation 
    add index FK7E61EE6C9A34AAE2 (sensorPlatformLocationId), 
    add constraint FK7E61EE6C9A34AAE2 
    foreign key (sensorPlatformLocationId) 
    references SensorPlatformLocation (sensorPlatformLocationId);
 
alter table Sensor 
    add index FK93653FDA2B99ED98 (sensorPlatformId), 
    add constraint FK93653FDA2B99ED98 
    foreign key (sensorPlatformId) 
    references SensorPlatform (sensorPlatformId);
 
alter table Sensor 
    add index FK93653FDA697DCEC4 (measurementTypeId), 
    add constraint FK93653FDA697DCEC4 
    foreign key (measurementTypeId) 
    references MeasurementType (measurementTypeId);
 
alter table SensorPlatformLocation
    add index FKB52C36F02B99ED98 (sensorPlatformId), 
    add constraint FKB52C36F02B99ED98 
    foreign key (sensorPlatformId) 
    references SensorPlatform (sensorPlatformId);
 
alter table SymbolicLocation 
    add index FK38D9F2A7601838A4 (localityId), 
    add constraint FK38D9F2A7601838A4 
    foreign key (localityId) 
    references Locality (localityId);
 
alter table SymbolicLocation 
    add index FK38D9F2A79A34AAE2 (sensorPlatformLocationId), 
    add constraint FK38D9F2A79A34AAE2 
    foreign key (sensorPlatformLocationId) 
    references SensorPlatformLocation (sensorPlatformLocationId);

--  Populate SensorPlatform

ALTER TABLE SensorPlatform AUTO_INCREMENT = 0;
INSERT
  INTO SensorPlatform
SELECT sensorPlatformId,
       0,
       NULL
  FROM TemporarySensorPlatform;

--  Populate Sensor

ALTER TABLE Sensor AUTO_INCREMENT = 0;
INSERT
  INTO Sensor
SELECT sensorId,
       0,
       measurementTypeId,
       sensorPlatformId
  FROM TemporarySensor;

--  Populate SensorPlatformLocation

ALTER TABLE SensorPlatformLocation AUTO_INCREMENT = 0;
INSERT
  INTO SensorPlatformLocation
SELECT sensorPlatformLocationId,
       0,
       "2012-01-01 00:00:00",
       NULL,
       sensorPlatformId
  FROM TemporarySensorPlatformLocation;

--  Populate SymbolicLocation

INSERT
  INTO SymbolicLocation
SELECT sensorPlatformLocationId,
       localityId
  FROM TemporarySymbolicLocation;

--  Modify Measurements

ALTER TABLE Measurement
  ADD sensorId BIGINT AFTER measurementDateTime;

UPDATE Measurement
   SET sensorId = (SELECT sensorId
                     FROM TemporarySensor
                    WHERE TemporarySensor.localityId        = Measurement.localityId AND
                          TemporarySensor.measurementTypeId = Measurement.measurementTypeId);

alter table Measurement
    drop
    foreign key FKF75C839C601838A4;

ALTER TABLE Measurement
  DROP localityId;

alter table Measurement
    drop
    foreign key FKF75C839C697DCEC4;

ALTER TABLE Measurement
  DROP measurementTypeId,

MODIFY sensorId BIGINT NOT NULL;
 
alter table Measurement 
    add index FKF75C839CE81C3052 (sensorId), 
    add constraint FKF75C839CE81C3052 
    foreign key (sensorId) 
    references Sensor (sensorId);
