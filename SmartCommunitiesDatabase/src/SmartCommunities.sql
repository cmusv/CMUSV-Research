
    alter table Locality 
        drop 
        foreign key FK752661839077F4CE;

    alter table Measurement 
        drop 
        foreign key FKF75C839C601838A4;

    alter table Measurement 
        drop 
        foreign key FKF75C839C697DCEC4;

    alter table MeasurementIntent 
        drop 
        foreign key FK91D08158601838A4;

    alter table MeasurementIntent 
        drop 
        foreign key FK91D08158697DCEC4;

    drop table if exists Locality;

    drop table if exists Measurement;

    drop table if exists MeasurementIntent;

    drop table if exists MeasurementType;

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
        localityId bigint not null,
        measurementDateTime datetime not null,
        measurementTypeId bigint not null,
        value double precision not null,
        primary key (measurementId)
    );

    create table MeasurementIntent (
        measurementIntentId bigint not null auto_increment,
        version integer not null,
        localityId bigint not null,
        measurementTypeId bigint not null,
        primary key (measurementIntentId)
    );

    create table MeasurementType (
        measurementTypeId bigint not null auto_increment,
        version integer not null,
        cumulative char not null,
        name varchar(255) not null,
        primary key (measurementTypeId)
    );

    alter table Locality 
        add index FK752661839077F4CE (parentLocalityId), 
        add constraint FK752661839077F4CE 
        foreign key (parentLocalityId) 
        references Locality (localityId);

    create index measurementDateTimeIndex on Measurement (measurementDateTime);

    alter table Measurement 
        add index FKF75C839C601838A4 (localityId), 
        add constraint FKF75C839C601838A4 
        foreign key (localityId) 
        references Locality (localityId);

    alter table Measurement 
        add index FKF75C839C697DCEC4 (measurementTypeId), 
        add constraint FKF75C839C697DCEC4 
        foreign key (measurementTypeId) 
        references MeasurementType (measurementTypeId);

    alter table MeasurementIntent 
        add index FK91D08158601838A4 (localityId), 
        add constraint FK91D08158601838A4 
        foreign key (localityId) 
        references Locality (localityId);

    alter table MeasurementIntent 
        add index FK91D08158697DCEC4 (measurementTypeId), 
        add constraint FK91D08158697DCEC4 
        foreign key (measurementTypeId) 
        references MeasurementType (measurementTypeId);
