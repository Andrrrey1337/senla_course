--liquibase formatted sql

--changeset hotel_admin:3

CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL
);

INSERT INTO users (username, password, role)
VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGz2rxn3F3V5QzFwQn5O', 'ROLE_ADMIN');

INSERT INTO users (username, password, role)
VALUES ('user', '$2a$10$4.a5dM1mX1T9vWw1/32XUeA.s.nUeQxGqH7D4N1m4Q1j4o/U5M5mG', 'ROLE_USER');