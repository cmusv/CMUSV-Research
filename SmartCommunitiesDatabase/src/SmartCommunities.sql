
    alter table Locality 
        drop 
        foreign key FK752661839077F4CE;

    alter table Measurement 
        drop 
        foreign key FKF75C839C601838A4;

    drop table if exists Locality;

    drop table if exists Measurement;

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
        carbonDioxide double precision,
        humidity double precision,
        light double precision,
        localityId bigint not null,
        measurementDateTime datetime not null,
        occupancy integer,
        temperature double precision,
        watts double precision,
        primary key (measurementId)
    );

    alter table Locality 
        add index FK752661839077F4CE (parentLocalityId), 
        add constraint FK752661839077F4CE 
        foreign key (parentLocalityId) 
        references Locality (localityId);

    alter table Measurement 
        add index FKF75C839C601838A4 (localityId), 
        add constraint FKF75C839C601838A4 
        foreign key (localityId) 
        references Locality (localityId);
