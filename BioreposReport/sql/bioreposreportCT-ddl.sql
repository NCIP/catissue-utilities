/*L
   Copyright Washington University in St. Louis
   Copyright SemanticBits
   Copyright Persistent Systems
   Copyright Krishagni

   Distributed under the OSI-approved BSD 3-Clause License.
   See http://ncip.github.com/catissue-utilities/LICENSE.txt for details.
L*/

create database reposReportCT;
use reposReportCT;


GRANT ALL PRIVILEGES ON reposReportCT.* TO 'reposReportAdmin'@'localhost' IDENTIFIED BY 'reposReportAdmin' WITH GRANT OPTION;

GRANT ALL PRIVILEGES ON reposReportCT.* TO 'reposReportAdmin'@'%' IDENTIFIED BY 'reposReportAdmin' WITH GRANT OPTION;

    create table reposReportCT.collection_protocol (
        id bigint not null,
        name varchar(255),
        identifier varchar(255),
        patients_enrolled bigint,
        patients_planned bigint,
        primary key (id)
    );

    create table reposReportCT.number_shipped (
        id bigint not null   AUTO_INCREMENT,
        number_shipped bigint,
        specimen_type_id bigint not null,
        collection_protocol_id bigint not null,
        report_period_id bigint not null,
        primary key (id)
    );

    create table reposReportCT.reporting_period (
        id bigint not null   AUTO_INCREMENT,
        start date,
        end date,
        primary key (id)
    );

    create table reposReportCT.specimen_inventory (
        id bigint not null  AUTO_INCREMENT,
        specimen_count bigint,
        specimen_type_id bigint not null,
        collection_protocol_id bigint not null,
        primary key (id)
    );

    create table reposReportCT.specimen_type (
        id bigint not null,
        name varchar(255),
        primary key (id)
    );

    alter table reposReportCT.number_shipped 
        add index FKC442065D44C9BB33 (report_period_id), 
        add constraint FKC442065D44C9BB33 
        foreign key (report_period_id) 
        references reposReportCT.reporting_period (id);

    alter table reposReportCT.number_shipped 
        add index FKC442065D4F4DCD75 (specimen_type_id), 
        add constraint FKC442065D4F4DCD75 
        foreign key (specimen_type_id) 
        references reposReportCT.specimen_type (id);

    alter table reposReportCT.number_shipped 
        add index FKC442065DE43A88E1 (collection_protocol_id), 
        add constraint FKC442065DE43A88E1 
        foreign key (collection_protocol_id) 
        references reposReportCT.collection_protocol (id);

    alter table reposReportCT.specimen_inventory 
        add index FKBA2DECE54F4DCD75 (specimen_type_id), 
        add constraint FKBA2DECE54F4DCD75 
        foreign key (specimen_type_id) 
        references reposReportCT.specimen_type (id);

    alter table reposReportCT.specimen_inventory 
        add index FKBA2DECE5E43A88E1 (collection_protocol_id), 
        add constraint FKBA2DECE5E43A88E1 
        foreign key (collection_protocol_id) 
        references reposReportCT.collection_protocol (id);
