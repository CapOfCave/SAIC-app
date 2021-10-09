drop table T_Book if exists;

create table T_Book (
    ID integer identity primary key,
    TITEL varchar(25) not null,
    AUTOR varchar(25) not null,
    VERLAG varchar(25) not null,
    ISBN13 varchar(25) not null);