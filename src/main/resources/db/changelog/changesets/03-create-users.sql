--liquibase formatted sql

--changeset hotel_admin:3

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role varchar(30) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (username, password)
VALUES ('admin', '$2a$10$oNigFSrOvR8uM0t1FPRknuwklJ2gZMrTgGyoo8gVF9upTJ3kMpv3C');

INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_USER');

INSERT INTO users (username, password)
VALUES ('user', '$2a$10$EsBJgLuZ7aFxTjsYNenWN.3AVoz/R1/DHL./XEXQb3VGGtrLnFGji');

INSERT INTO user_roles (user_id, role) VALUES (2, 'ROLE_USER')