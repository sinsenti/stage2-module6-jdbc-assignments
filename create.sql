-- create.sql
CREATE DATABASE myfirstdb;

\c myfirstdb

CREATE TABLE myusers (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(50),
    lastname VARCHAR(50),
    age INTEGER
);
