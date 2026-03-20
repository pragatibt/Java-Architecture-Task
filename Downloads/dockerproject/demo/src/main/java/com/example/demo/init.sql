CREATE DATABASE IF NOT EXISTS mydb;

USE mydb;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);

INSERT INTO users (name, email) VALUES
('Rahul', 'rahul@test.com'),
('Amit', 'amit@test.com');