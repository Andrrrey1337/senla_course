--liquibase formatted sql

--changeset hotel_admin:3

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL
);

INSERT INTO users (username, password, role)
VALUES ('admin', '$2a$10$oNigFSrOvR8uM0t1FPRknuwklJ2gZMrTgGyoo8gVF9upTJ3kMpv3C', 'ROLE_ADMIN');

INSERT INTO users (username, password, role)
VALUES ('user', '$2a$10$EsBJgLuZ7aFxTjsYNenWN.3AVoz/R1/DHL./XEXQb3VGGtrLnFGji', 'ROLE_USER');