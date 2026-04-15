CREATE DATABASE student_management;
CREATE TABLE student (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT,
    grade DECIMAL(4,2) -- Permet de stocker des notes comme 15.50
);
CREATE TABLE users (
    id SERIAL PRIMARY KEY, -- SERIAL correspond au int4 auto-incrémenté
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    salt VARCHAR(255),
    avatar_url VARCHAR(255)
);