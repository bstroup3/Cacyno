start transaction;

create user if not exists 'admin'@'localhost' identified by 'password';
grant all privileges on *.* to 'admin'@'localhost';
drop database if exists cacyno;
create database cacyno;
use cacyno;
drop table if exists user;
create table user (
	id varchar(50) unique not null,
    username varchar(50) not null,
    money int,
    displayname bool,
    mutechat bool,
    primary key(id)
);

commit;