
create database relations_1;
use relations_1;

create table R1(
	K int, A int, B int, C int,
	primary key (K)
	);

create table R2(
	K int, D int, E int,
	primary key (K)
	);

create table R3(
	A int, A1 int, A2 int, A3 int,
	primary key (A)
	);

create table R4(
	B int, B1 int, B2 int,
	primary key (B)
	);

create table R5(
	C int, C1 int, C2 int, C3 int, C4 int, C5 int,
	primary key (C)
	);

insert into R1 values (4,2,0,6);
insert into R1 values (5,2,0,5);
insert into R1 values (1,1,3,8);
insert into R1 values (2,1,3,7);
insert into R1 values (3,2,3,3);

insert into R2 values (4,1,6);
insert into R2 values (5,1,5);
insert into R2 values (1,1,8);
insert into R2 values (2,1,7);
insert into R2 values (3,1,3);

insert into R3 values (2,4,6,8);
insert into R3 values (1,2,3,4);

insert into R4 values (0,0,0);
insert into R4 values (3,9,27);

insert into R5 values (4,2,0,6,1,6);
insert into R5 values (5,2,0,5,1,5);
insert into R5 values (1,1,3,8,1,8);
insert into R5 values (2,1,3,7,1,7);
insert into R5 values (3,2,3,3,1,3);
