
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


