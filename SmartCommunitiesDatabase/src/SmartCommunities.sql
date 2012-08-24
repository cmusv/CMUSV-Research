
    alter table AbsoluteLocation 
        drop 
        foreign key FK7E61EE6C9A34AAE2;

    alter table Locality 
        drop 
        foreign key FK752661839077F4CE;

    alter table Measurement 
        drop 
        foreign key FKF75C839CE81C3052;

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

    drop table if exists Locality;

    drop table if exists Measurement;

    drop table if exists MeasurementType;

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

    create table Locality (
        localityId bigint not null auto_increment,
        version integer not null,
        name varchar(255) not null,
        parentLocalityId bigint,
        watts double precision,
        primary key (localityId)
    );

    create table Measurement (
        measurementId bigint not null auto_increment,
        version integer not null,
        measurementDateTime datetime not null,
        sensorId bigint not null,
        value double precision not null,
        primary key (measurementId)
    );

    create table MeasurementType (
        measurementTypeId bigint not null auto_increment,
        version integer not null,
        cumulative char not null,
        name varchar(255) not null,
        primary key (measurementTypeId)
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

    alter table Locality 
        add index FK752661839077F4CE (parentLocalityId), 
        add constraint FK752661839077F4CE 
        foreign key (parentLocalityId) 
        references Locality (localityId);

    create index measurementDateTimeIndex on Measurement (measurementDateTime);

    alter table Measurement 
        add index FKF75C839CE81C3052 (sensorId), 
        add constraint FKF75C839CE81C3052 
        foreign key (sensorId) 
        references Sensor (sensorId);

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
        add index FKB52FD9422B99ED98 (sensorPlatformId), 
        add constraint FKB52FD9422B99ED98 
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
