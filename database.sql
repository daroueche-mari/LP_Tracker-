CREATE DATABASE student_management;
CREATE TABLE student (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT,
    grade DECIMAL(4,2) -- Permet de stocker des notes comme 15.50
);