drop database if exists shorturi;
create database shorturi;
use shorturi;
set time_zone = "+01:00";

create table users(
	id int primary key,
    email varchar(200),
    password varchar(200),
    active boolean,
    roles varchar(200)
);

create table associated_uri (
	id int primary key,
    user_id int,
	short_id varchar(200) ,
    long_uri varchar(200),
    number_visits int,
    created_at varchar(50),
    updated_at varchar(50),
    foreign key (user_id) references users(id)
);

create table hibernate_sequence(
	next_val int
);

insert into hibernate_sequence values(1);